package com.pls.restful.shipment;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.dtobuilder.util.ReportParamsBOBuilder;
import com.pls.shipment.service.ShipmentReportService;

/**
 * Reports REST resource.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@Controller
@Transactional
@RequestMapping("/reports")
public class ReportResource {

    @Autowired
    private ShipmentReportService service;

    /**
     * Returns record of loads as XLSX document with data.
     * 
     * @param request
     *            request coming from the UI containing parameters
     * @return text data file or empty response.
     * @throws IOException
     *             when can't write file content to input stream
     * @throws ParseException
     *             - parse exception
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity getReport(HttpServletRequest request) throws IOException, ParseException {
        return service.exportReport(new ReportParamsBOBuilder(request).build());
    }
}
