package com.pls.restful;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.exception.ApplicationException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.export.ExcelDataBuilder;
import com.pls.export.ExportData;
import com.pls.export.service.FileInputStreamResponseService;

/**
 * Service for saving data for export.
 *
 * @author Mykola Teslenko
 */
@Controller
@RequestMapping("/export")
public class ExportResource {

    @Autowired
    private FileInputStreamResponseService fileInputStreamResponseService;

    /**
     * Method which takes objects list for export.
     *
     * @param request httpServletRequest
     * @param exportData data for export
     * @throws java.io.IOException when IO happens during reading request body
     * @return generated UUID for current export
     */
    @RequestMapping(value = "/exportData", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String setObjectsForExport(HttpServletRequest request, @RequestBody ExportData exportData) throws IOException {
        final String generatedUuid = UUID.randomUUID().toString();

        HttpSession session = request.getSession();
        session.setAttribute(generatedUuid, exportData);

        return generatedUuid;
    }

    /**
     * Exports Excel report.
     *
     * @param uuid uuid for defining which export should be performed
     * @param httpSession httpSession
     * @return response object that will contains exported file as attachment
     * @throws IOException when IO errors during export happens
     * @throws com.pls.core.exception.ApplicationException if list of invoices is empty or is null in session
     */
    @RequestMapping(value = "/report", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity getReport(@RequestParam(value = "uuid", required = true) String uuid, HttpSession httpSession)
            throws ApplicationException {
        ExportData exportData = getExportData(httpSession, uuid);
        ExcelDataBuilder excelDataBuilder = new ExcelDataBuilder(exportData);
        return fileInputStreamResponseService.getExcelDataAsStreamResource(excelDataBuilder);
    }

    private ExportData getExportData(HttpSession httpSession, String uuid) throws ApplicationException {
        ExportData exportData = (ExportData) httpSession.getAttribute(uuid);
        if (exportData == null || exportData.getData().isEmpty()) {
            throw new ApplicationException("Preparing document failed.");
        }
        httpSession.removeAttribute(uuid);
        return exportData;
    }

}
