package com.pls.restful.proposal;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.common.MimeTypes;
import com.pls.core.shared.ResponseVO;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.dtobuilder.proposal.FAReportStatusDTOBuilder;
import com.pls.dtobuilder.proposal.FreightAnalysisTarifsDTOBuilder;
import com.pls.ltlrating.domain.enums.FinancialAnalysisStatus;
import com.pls.ltlrating.service.analysis.AnalysisMessageProducer;
import com.pls.ltlrating.service.analysis.FreightAnalysisImportExportService;
import com.pls.ltlrating.service.analysis.FreightAnalysisValidationService;
import com.pls.ltlrating.service.analysis.ShipmentFreightAnalysisService;
import com.pls.ltlrating.shared.FreightAnalysisDetailsBO;
import com.pls.ltlrating.shared.FreightAnalysisReportStatusBO;

/**
 * Shipment freight analysis resource.
 * 
 * @author Brichak Aleksandr
 */
@SuppressWarnings("unused")
@Controller
@Transactional
@RequestMapping("/shipment/analysis")
@EnableAsync
public class ShipmentFreightAnalysisResource {

    private static final String ANALYSIS_FILE_UPLOAD = "Analysis file upload ";

    private static final FreightAnalysisTarifsDTOBuilder TARIFFS_BUILDER = new FreightAnalysisTarifsDTOBuilder();

    private static final FAReportStatusDTOBuilder REPORT_STATUS_BUILDER = new FAReportStatusDTOBuilder();

    @Autowired
    private ShipmentFreightAnalysisService freightAnalysisService;

    @Autowired
    private AnalysisMessageProducer operator;

    @Autowired
    private FreightAnalysisImportExportService fileService;

    @Autowired
    private DocumentService docService;

    @Autowired
    private FreightAnalysisValidationService validationService;

    @Value("/templates/Rate_Analysis_Import_Template.xls")
    private ClassPathResource rateAnalysisImportTemplateResource;

    /**
     * Restart Analysis job.
     * 
     * @param id
     *            {@link com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity#id}
     * @throws Exception
     *             if restart of analysis process failed
     */
    @RequestMapping(value = "/start/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void restart(@PathVariable("id") Long id) throws Exception {
        freightAnalysisService.updateAnalysisStatus(id, FinancialAnalysisStatus.Processing);
        operator.startAnalysis();
    }

    /**
     * Add new freight analysis job for processing.
     * 
     * @param vo
     *            {@link FreightAnalysisDetailsBO}
     * @throws Exception
     *             if entity not found or document doesn't saved
     */
    @RequestMapping(value = "/addPricing", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addNewPricing(@RequestBody FreightAnalysisDetailsBO vo) throws Exception {
        if (vo.getAnalysisId() != null) {
            freightAnalysisService.copyAnalysisJob(vo, TARIFFS_BUILDER.buildEntityList(vo.getTariffs()));
        } else {
            freightAnalysisService.addNewPricing(vo, TARIFFS_BUILDER.buildEntityList(vo.getTariffs()));
        }
        operator.startAnalysis();
    }

    /**
     * Pause Analysis job.
     * 
     * @param id
     *            {@link com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity#id}
     */
    @RequestMapping(value = "/pause/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void pause(@PathVariable("id") Long id) {
        freightAnalysisService.updateAnalysisStatus(id, FinancialAnalysisStatus.Stopped);
    }

    /**
     * Remove Analysis job.
     * 
     * @param id
     *            {@link com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity#id}
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        freightAnalysisService.updateAnalysisStatus(id, FinancialAnalysisStatus.Deleted);
    }

    /**
     * Get analysis jobs except Delete status.
     * 
     * @return {@link List<FreightAnalysisReportStatusVO>}.
     */
    @RequestMapping(value = "/getAnalysisJobs", method = RequestMethod.GET)
    @ResponseBody
    public List<FreightAnalysisReportStatusBO> getAnalysisJobs() {
        return freightAnalysisService.getAnalysisJobs();
    }

    /**
     * This method swaps priority for Analysis Jobs.
     * 
     * @param fAId
     *            {@link com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity#id}
     * @param step
     *            flag indicating the direction of movement. Priority of Job is incremented by one if step is
     *            true priority of Job reduced by one if step is false.
     * @throws Exception
     */
    @RequestMapping(value = "/swap/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void swapAnalysisJobs(@PathVariable("id") Long fAId, @RequestParam("step") Boolean step) {
        freightAnalysisService.swapAnalysisJobs(fAId, step);
    }

    /**
     * Get Analysis Import Template.
     * 
     * @param response
     *            servlet response
     * @throws IOException
     *             when can't write file to output stream
     */
    @RequestMapping(value = "/rateAnalysisImportTemplate", method = { RequestMethod.GET, RequestMethod.HEAD })
    // RequestMethod.HEAD is required for IE when displaying document inline
    public void downloadAnalysisImportTemplate(HttpServletResponse response) throws IOException {
        response.setContentType(MimeTypes.XLS.getMimeString());
        response.setHeader("Content-Disposition", "attachment; filename=RateAnalysisImportTemplate.xls");
        IOUtils.copy(rateAnalysisImportTemplateResource.getInputStream(), response.getOutputStream());
    }

    /**
     * Validate document by id.
     * 
     * @param tempDocId
     *            an id of a validated document.
     * @param session
     *            current session.
     * @throws Exception
     *          exception;
     */
    @RequestMapping(value = "/validateExelFile/{tempDocId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Async
    public void validateExelFile(@PathVariable("tempDocId") Long tempDocId, HttpSession session) throws Exception {
        long result = validationService.validateFileForFreightAnalysis(tempDocId);
        session.setAttribute(ANALYSIS_FILE_UPLOAD + tempDocId, result);
    }

    /**
     * Checking if file has been validated.
     * 
     * @param session
     *            current session.
     * @param tempDocId
     *            id of temporary stored document.
     * @return ValueLabelDTO object.
     */
    @RequestMapping(value = "/checkValidationStatus/{tempDocId}", method = { RequestMethod.GET })
    @ResponseBody
    public ResponseVO checkValidationStatus(@PathVariable("tempDocId") Long tempDocId, HttpSession session) {
        String attributeName = ANALYSIS_FILE_UPLOAD + tempDocId;
        Long id = null;
        if (session.getAttribute(attributeName) != null) {
            id = (Long) session.getAttribute(attributeName);
            session.removeAttribute(attributeName);
        }
        return new ResponseVO(id);
    }
}
