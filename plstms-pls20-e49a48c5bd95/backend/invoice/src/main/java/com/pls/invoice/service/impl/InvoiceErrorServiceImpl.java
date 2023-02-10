package com.pls.invoice.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.invoice.dao.CustomerInvoiceErrorsDao;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.domain.CustomerInvoiceErrorEntity;
import com.pls.invoice.domain.bo.InvoiceErrorDetailsBO;
import com.pls.invoice.service.CustomerInvoiceDocumentService;
import com.pls.invoice.service.InvoiceErrorService;
import com.pls.invoice.service.processing.EDIInvoiceProcessingService;
import com.pls.invoice.service.processing.EmailInvoiceProcessingService;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceDocumentGeneratorService;
import com.pls.invoice.service.processing.InvoiceService;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Implementation of {@link InvoiceErrorService}.
 *
 * @author Alexander Kirichenko
 */
@Service
@Transactional
public class InvoiceErrorServiceImpl implements InvoiceErrorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceErrorServiceImpl.class);

    @Autowired
    private FinancialInvoiceDao invoiceDao;

    @Autowired
    protected CustomerInvoiceErrorsDao errorsDao;

    @Autowired
    private FinancialInvoiceProcessingService finacialInvoiceService;

    @Autowired
    private EmailInvoiceProcessingService emailInvoiceService;

    @Autowired
    private EDIInvoiceProcessingService ediInvoiceService;

    @Autowired
    private CustomerInvoiceDocumentService invoiceDocumentService;

    @Autowired
    private InvoiceDocumentGeneratorService documentGeneratorService;

    @Autowired
    private InvoiceService invoiceService;

    @Override
    public List<CustomerInvoiceErrorEntity> getInvoiceErrors() {
        return errorsDao.getActiveErrors();
    }

    @Override
    public Long getCountOfInvoiceErrors() {
        return errorsDao.getActiveErrorsCount();
    }

    @Override
    public void cancelError(long errorId) throws EntityNotFoundException {
        CustomerInvoiceErrorEntity entity = errorsDao.get(errorId);
        entity.setStatus(Status.INACTIVE);
        errorsDao.update(entity);
    }

    @Override
    public String reprocessError(long errorId, String subject, String comments, Long personId) {
        LOGGER.info("Reprocessing Invoice Error {}", errorId);
        CustomerInvoiceErrorEntity error = errorsDao.find(errorId);
        String errorMsg = null;

        try {
            // FIXME errors are reprocessed only for one bill to.
            reprocessFinance(error, personId);
            reprocessCustomerEmail(error, subject, comments, personId);
            reprocessCustomerEDI(error);
            reprocessDocuments(error);

        } catch (ApplicationException | IOException e) {
            errorMsg = "Failed reprocessing invoice error. " + e.getMessage();
            LOGGER.error(errorMsg, e);
        }
        if (BooleanUtils.isTrue(error.getSentToFinance()) && BooleanUtils.isTrue(error.getSentEmail())
                && BooleanUtils.isTrue(error.getSentEdi()) && BooleanUtils.isTrue(error.getSentDocuments())) {
            error.setStatus(Status.INACTIVE);
        }
        errorsDao.update(error);
        return errorMsg;
    }

    private void reprocessDocuments(CustomerInvoiceErrorEntity error) throws ApplicationException, IOException {
        if (BooleanUtils.isNotTrue(error.getSentDocuments())) {
            BillToEntity billTo = invoiceDao.getBillToByInvoiceId(error.getInvoiceId());
            List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(error.getInvoiceId());
            Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = documentGeneratorService
                    .generateInvoiceDocuments(billTo, invoices);
            invoiceDocumentService.convertAndSendDocument(error.getInvoiceId(), billTo, invoices, invoiceDocuments);
            error.setSentDocuments(true);
        }
    }

    private void reprocessCustomerEDI(CustomerInvoiceErrorEntity error)
            throws ApplicationException {
        if (BooleanUtils.isNotTrue(error.getSentEdi())) {
            BillToEntity billTo = invoiceDao.getBillToByInvoiceId(error.getInvoiceId());
            ediInvoiceService.sendInvoiceViaEDI(error.getInvoiceId(), billTo);
            error.setSentEdi(true);
        }
    }

    private void reprocessCustomerEmail(CustomerInvoiceErrorEntity error, String subject, String comments, Long personId)
            throws ApplicationException {
        if (BooleanUtils.isNotTrue(error.getSentEmail())) {
            BillToEntity billTo = invoiceDao.getBillToByInvoiceId(error.getInvoiceId());
            List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(error.getInvoiceId());
            Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments =
                    documentGeneratorService.generateInvoiceDocuments(billTo, invoices);
            emailInvoiceService.sendInvoicesViaEmail(error.getInvoiceId(), null, subject, comments, billTo, personId, invoiceDocuments);
            error.setSentEmail(true);
        }
    }

    private void reprocessFinance(CustomerInvoiceErrorEntity error, Long personId)
            throws ApplicationException {
        if (BooleanUtils.isNotTrue(error.getSentToFinance())) {
            finacialInvoiceService.sendInvoicesToAX(error.getInvoiceId(), personId);
            error.setSentToFinance(true);
        }
    }

    @Override
    public void saveInvoiceError(Long invoiceId, String message, Throwable e, boolean wasSentToFinance,
            boolean wasSentByEmail, boolean wasSentByEdi, boolean sentDocuments, Long personId) {
        CustomerInvoiceErrorEntity error = new CustomerInvoiceErrorEntity();
        error.setInvoiceId(invoiceId);
        error.setMessage(StringUtils.left(message, 200));
        if (e != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            printWriter.flush();
            printWriter.close();
            error.setStackTrace(stringWriter.getBuffer().toString());
        }
        error.setSentToFinance(wasSentToFinance);
        error.setSentEmail(wasSentByEmail);
        error.setSentEdi(wasSentByEdi);
        error.setSentDocuments(sentDocuments);
        error.getModification().setCreatedBy(personId);
        errorsDao.saveOrUpdate(error);
    }

    @Override
    public String getEmailSubjectForReprocessError(long errorId) {
        List<InvoiceErrorDetailsBO> invoiceDetails = errorsDao.getInvoiceErrorDetails(errorId);
        if (invoiceDetails.isEmpty()) {
            return null;
        }

        return emailInvoiceService.getEmailSubject(invoiceDetails.get(0).getInvoiceType(),
                invoiceDetails.stream().map(detail -> detail.getInvoiceNumber()).collect(Collectors.toList()));
    }
}
