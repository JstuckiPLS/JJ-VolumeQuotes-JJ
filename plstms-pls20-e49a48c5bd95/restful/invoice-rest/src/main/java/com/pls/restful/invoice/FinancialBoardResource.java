package com.pls.restful.invoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.bo.AuditRecordsBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.ResponseVO;
import com.pls.dtobuilder.util.RegularSearchQueryBOBuilder;
import com.pls.invoice.domain.bo.AuditBO;
import com.pls.invoice.domain.bo.CBIHistoryBO;
import com.pls.invoice.domain.bo.ConsolidatedInvoiceBO;
import com.pls.invoice.domain.bo.InvoiceBO;
import com.pls.invoice.domain.bo.InvoiceHistoryBO;
import com.pls.invoice.domain.bo.InvoiceResultBO;
import com.pls.invoice.domain.bo.MultipleAuditRecordsBO;
import com.pls.invoice.domain.bo.ProcessedLoadsReportBO;
import com.pls.invoice.domain.bo.ReprocessHistoryBO;
import com.pls.invoice.domain.bo.SendToFinanceBO;
import com.pls.invoice.service.FinancialBoardService;
import com.pls.invoice.service.InvoiceAuditService;
import com.pls.invoice.service.InvoiceHistoryService;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.restful.TransactionalReadOnly;
import com.pls.restful.TransactionalReadWrite;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.impl.BillingAuditService;

/**
 * Shipment financial board resource.
 * 
 * @author Aleksandr Leshchenko
 */
@Controller
@TransactionalReadOnly
@RequestMapping("/invoice/financial/board")
public class FinancialBoardResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(FinancialBoardResource.class);

    @Autowired
    private FinancialBoardService service;

    @Autowired
    private InvoiceAuditService auditService;

    @Autowired
    private InvoiceHistoryService historyService;

    @Autowired
    private BillingAuditService billingAuditService;

    @Autowired
    private FinancialInvoiceProcessingService invoiceProcessingService;

    /**
     * Get data for invoice audit that includes shipments and adjustments.
     * 
     * @return data for invoice audit {@link AuditBO}
     */
    @RequestMapping(value = "/lowBenefit", method = RequestMethod.GET)
    @ResponseBody
    public List<AuditBO> getLowBenefitShipments() {
        return auditService.getInvoiceAuditData(SecurityUtils.getCurrentPersonId());
    }

    /**
     * Get data for price audit that includes shipments and adjustments.
     * 
     * @return data for price audit {@link AuditBO}
     */
    @RequestMapping(value = "/priceAudit", method = RequestMethod.GET)
    @ResponseBody
    public List<AuditBO> getPriceAuditShipments() {
        return auditService.getPriceAuditData(SecurityUtils.getCurrentPersonId());
    }

    /**
     * Get shipments which are ready to be invoiced to customer.
     * 
     * @return shipments which are ready to be invoiced to customer.
     */
    @RequestMapping(value = "/transactional", method = RequestMethod.GET)
    @ResponseBody
    public List<InvoiceBO> getTransactionalInvoices() {
        return service.getTransactionalInvoices(SecurityUtils.getCurrentPersonId());
    }

    /**
     * Approve load or adjustment for automatic invoicing to finance system.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @param approve
     *            <code>true</code> if load needs to be approved. <code>false</code> otherwise
     * @param adjustmentId
     *            {@link com.pls.shipment.domain.FinancialAccessorialsEntity#getId()}
     * @throws ApplicationException
     *             if load is already sent to finance system or is not ready to be sent yet, etc.
     */
    @TransactionalReadWrite
    @RequestMapping(value = "/approve", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void approveSendingInvoice(@RequestParam("loadId") Long loadId, @RequestParam("approve") Boolean approve,
            @RequestParam(value = "adjustmentId", required = false) Long adjustmentId) throws ApplicationException {
        if (adjustmentId == null) {
            auditService.updateInvoiceApproved(Collections.singletonList(loadId), approve);
        } else {
            auditService.updateAdjustmentInvoiceApproved(Collections.singletonList(adjustmentId), approve);
        }
    }

    /**
     * Approve loads or adjustments. If adjustment ID is present then adjustment will be approved otherwise it
     * will be load.
     * 
     * @param auditRecords
     *            contains parameters for Invoice Audit History.
     * @return empty string if everything was approved. error message otherwise.
     */
    @TransactionalReadWrite
    @RequestMapping(value = "/audit/approve", method = RequestMethod.PUT)
    @ResponseBody
    public String approveAudit(@RequestBody MultipleAuditRecordsBO auditRecords) {
        try {
            List<AuditReasonBO> records = auditRecords.getAuditRecords().stream()
                    .map(AuditReasonBO::new).collect(Collectors.toList());
            auditService.approveAudit(records);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return e.getMessage();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Send load or adjustment to audit. if adjustment ID is present then adjustment will be send to audit
     * otherwise it will be load.
     * 
     * @param auditRecords
     *            contains parameters for Invoice Audit History,
     * @param code
     *            contains the code for Audit Records
     * @param note
     *            contains the note for Audit Records
     * @throws ApplicationException
     *             if cost items check failed
     */
    @TransactionalReadWrite
    @RequestMapping(value = "/invoiceAudit/send", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void sendToInvoiceAudit(@RequestBody MultipleAuditRecordsBO auditRecords, @RequestParam("code") String code,
            @RequestParam(value = "note", required = false) String note) throws ApplicationException {
        if (auditRecords != null && !auditRecords.getAuditRecords().isEmpty()) {
            auditService.sendToAudit(auditRecords.getAuditRecords(), code, note, true);
        }
    }

    /**
     * Send load or adjustment to audit. if adjustment ID is present then adjustment will be send to audit
     * otherwise it will be load.
     * 
     * @param auditRecords
     *            contains parameters for Invoice Audit History,
     * @param code
     *            contains the code for Audit Records
     * @param note
     *            contains the note for Audit Records
     * @throws ApplicationException
     *             if cost items check failed
     */
    @TransactionalReadWrite
    @RequestMapping(value = "/priceAudit/send", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void sendToPriceAudit(@RequestBody MultipleAuditRecordsBO auditRecords, @RequestParam("code") String code,
            @RequestParam(value = "note", required = false) String note) throws ApplicationException {
        if (auditRecords != null && !auditRecords.getAuditRecords().isEmpty()) {
            auditService.sendToAudit(auditRecords.getAuditRecords(), code, note, false);
        }
    }

    /**
     * Process invoices and send documents in email to finance and via EDI if needed.
     * 
     * @param bo
     *            information required to send loads to finance
     * @return processed invoices with error description in case of failure
     * @throws ApplicationException
     *             if specified invoice id is already used for existing invoices
     */
    @TransactionalReadWrite
    @RequestMapping(value = "/processInvoices", method = RequestMethod.PUT)
    @ResponseBody
    public List<InvoiceResultBO> processInvoices(@RequestBody SendToFinanceBO bo) throws ApplicationException {
        if (bo.getInvoiceDate() == null) {
            bo.setInvoiceDate(new Date());
        }
        return service.processInvoices(bo, SecurityUtils.getCurrentPersonId());
    }

    /**
     * Returns record of invoices as XLSX document with data.
     * 
     * @param bo
     *            information required to send loads to finance
     * 
     * @return text data file or empty response.
     * @throws IOException
     *             when can't write file content to input stream
     */
    @RequestMapping(value = "/transactional/processExport", method = RequestMethod.POST,
            produces = { "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    @ResponseBody
    public FileInputStreamResponseEntity processTransactionalInvoicesExport(@RequestBody ProcessedLoadsReportBO bo)
            throws IOException {
        return service.getTransactionalProcessedReport(bo);
    }

    /**
     * Returns record of invoices as XLSX document with data.
     * 
     * @param bo
     *            information required to send loads to finance
     * 
     * @return text data file or empty response.
     * @throws IOException
     *             when can't write file content to input stream
     */
    @RequestMapping(value = "/CBI/processExport", method = RequestMethod.POST,
            produces = { "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    @ResponseBody
    public FileInputStreamResponseEntity processCBIInvoicesExport(@RequestBody ProcessedLoadsReportBO bo)
            throws IOException {
        return service.getCBIProcessedReport(bo);
    }

    /**
     * Get history of all successful invoices.
     *
     * @param request
     *            standard http servlet request
     * @return history of all successful invoices as list of {@link InvoiceHistoryBO}.
     * @throws ApplicationException
     *             if the wrong Bol Number of inappropriate wildcard pattern was entered.
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @ResponseBody
    public List<InvoiceHistoryBO> getHistoryInvoices(HttpServletRequest request) throws ApplicationException {

        return historyService.getInvoiceHistory(new RegularSearchQueryBOBuilder(request).build(), SecurityUtils.getCurrentPersonId());
    }

    /**
     * Get CBI history details.
     *
     * @param invoiceId
     *            invoice ID
     * @param groupInvoiceNumber
     *            group number of the invoice
     * @return all CBI items as list of {@link InvoiceHistoryBO}
     */
    @RequestMapping(value = "/history/cbi", method = RequestMethod.GET)
    @ResponseBody
    public List<CBIHistoryBO> getInvoiceHistoryCBIDetails(@RequestParam("invoiceId") Long invoiceId,
            @RequestParam(value = "groupInvoiceNumber", required = false) String groupInvoiceNumber) {
        return historyService.getInvoiceHistoryCBIDetails(invoiceId, groupInvoiceNumber);
    }

    /**
     * Re-process invoice for specified shipment.
     * 
     * @param reprocessBO
     *            information required to re-process invoice
     * @throws ApplicationException
     *             if invoice generation or sending of email failed or can be subclass
     *             {@link EntityNotFoundException} if entity not found
     */
    @TransactionalReadWrite
    @RequestMapping(value = "/history/reprocess", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void reProcessHistoryInvoice(@RequestBody ReprocessHistoryBO reprocessBO) throws ApplicationException {
        historyService.reprocessHistory(reprocessBO, SecurityUtils.getCurrentPersonId());
    }

    /**
     * Get consolidated shipments data which are ready to be invoiced to customer.
     *
     * @return shipments data which are ready to be invoiced to customer.
     */
    @RequestMapping(value = "/cbi", method = RequestMethod.GET)
    @ResponseBody
    public List<ConsolidatedInvoiceBO> getCBIInvoices() {
        return service.getConsolidatedInvoiceData(SecurityUtils.getCurrentPersonId());
    }

    /**
     * Get consolidated shipments which are ready to be invoiced to customer.
     *
     * @param billToId
     *            id of bill to.
     * @return shipments which are ready to be invoiced to customer.
     */
    @RequestMapping(value = "/cbi/{billToID}", method = RequestMethod.GET)
    @ResponseBody
    public List<InvoiceBO> getCBILoads(@PathVariable("billToID") List<Long> billToId) {
        return service.getConsolidatedLoads(SecurityUtils.getCurrentPersonId(), billToId);
    }

    /**
     * Get another part of specified rebill adjustments.
     * 
     * @param ids
     *            list of adjustments ids
     * @return list of rebill adjustments
     */
    @RequestMapping(value = "/rebill/{ids}", method = RequestMethod.GET)
    @ResponseBody
    public List<InvoiceBO> getRebillAdjustments(@PathVariable("ids") List<Long> ids) {
        return service.getRebillAdjustments(SecurityUtils.getCurrentPersonId(), ids);
    }

    /**
     * Set invoice approved for loads and their financial adjustments (if any) based on bill to.
     *
     * @param billToId
     *            bill to id for filtering
     * @param approved
     *            new approve status
     */
    @RequestMapping(value = "/cbi/{billToID}/approve", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @TransactionalReadWrite
    public void setConsolidatedInvoiceApproved(@PathVariable("billToID") Long billToId,
            @RequestParam("approved") boolean approved) {
        List<InvoiceBO> loads = service.getConsolidatedLoads(SecurityUtils.getCurrentPersonId(), billToId);
        List<Long> loadIds = new ArrayList<Long>();
        List<Long> adjustmentIds = new ArrayList<Long>();
        for (InvoiceBO bo : loads) {
            if (bo.getAdjustmentId() == null) {
                loadIds.add(bo.getLoadId());
            } else {
                adjustmentIds.add(bo.getAdjustmentId());
            }
        }
        if (!loadIds.isEmpty()) {
            auditService.updateInvoiceApproved(loadIds, approved);
        }
        if (!adjustmentIds.isEmpty()) {
            auditService.updateAdjustmentInvoiceApproved(adjustmentIds, approved);
        }
    }

    /**
     * Get CBI invoice number.
     *
     * @return CBI invoice number.
     */
    @RequestMapping(value = "/cbi/invoiceNumber", method = RequestMethod.GET)
    @ResponseBody
    @TransactionalReadWrite
    public ResponseVO getNextInvoiceIDForCBI() {
        return new ResponseVO(invoiceProcessingService.getCBIInvoiceNumber());
    }

    /**
     * Return Reason for Load.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @throws EntityNotFoundException
     *             if can't find entity
     * @return billing audit reason
     */
    @ResponseBody
    @RequestMapping(value = "/reason", method = RequestMethod.GET)
    public ResponseVO getReasonForLoad(@RequestParam("loadId") Long loadId) throws EntityNotFoundException {
        return new ResponseVO(billingAuditService.getBillingAuditReasonForLoad(loadId));
    }

    /**
     * Add new manual reason {@link com.pls.core.shared.Reasons#READY_FOR_CONSOLIDATED}. It is used to
     * indicate when load is ready to move to the Consolidated Invoice Screen.
     * 
     * @param auditRecords
     *            contains list of load which ready to move to the Consolidated Invoice Screen.
     */
    @TransactionalReadWrite
    @RequestMapping(value = "/invoiceAudit/readyForConsolidated", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void readyForConsolidated(@RequestBody List<AuditRecordsBO> auditRecords) {
        auditService.setReadyForConsolidatedReason(auditRecords);
    }
}
