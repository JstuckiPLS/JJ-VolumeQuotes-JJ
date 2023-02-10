package com.pls.invoice.service.impl.processing;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToDao;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.domain.bo.CustomerInvoiceProcessingBO;
import com.pls.invoice.domain.bo.InvoiceProcessingBO;
import com.pls.invoice.domain.bo.SendToFinanceBO;
import com.pls.invoice.service.CustomerInvoiceDocumentService;
import com.pls.invoice.service.InvoiceErrorService;
import com.pls.invoice.service.impl.jms.InvoiceJMSMessage;
import com.pls.invoice.service.impl.jms.InvoiceJmsMessageProducer;
import com.pls.invoice.service.processing.EDIInvoiceProcessingService;
import com.pls.invoice.service.processing.EmailInvoiceProcessingService;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceDocumentGeneratorService;
import com.pls.invoice.service.processing.InvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceService;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Implementation of {@link InvoiceProcessingService}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class InvoiceProcessingServiceImpl implements InvoiceProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceProcessingServiceImpl.class);

    static final String CBI_CODE_PREFIX = "C-";
    static final String GENERATED_CBI_CODE_FORMAT = "%s%07d";

    @Autowired
    private FinancialInvoiceProcessingService finacialInvoiceService;

    @Autowired
    private EmailInvoiceProcessingService emailInvoiceService;

    @Autowired
    private InvoiceErrorService invoiceErrorService;

    @Autowired
    private FinancialInvoiceDao invoiceDao;

    @Autowired
    private BillToDao billToDao;

    @Autowired
    private EDIInvoiceProcessingService ediInvoiceService;

    @Autowired
    private CustomerInvoiceDocumentService invoiceDocumentService;

    @Autowired
    private InvoiceDocumentGeneratorService documentGeneratorService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceJmsMessageProducer invoiceMessageProducer;

    @Override
    public void processInvoices(SendToFinanceBO bo, Long invoiceId, Long userId) {
        LOGGER.info("Processing invoices for invoice ID: {}", invoiceId);
        boolean sentToFinance = false;
        boolean sentByEmail = false;
        boolean sentByEdi = false;
        boolean sentDocuments = false;
        try {
            finacialInvoiceService.sendInvoicesToAX(invoiceId, userId);
            sentToFinance = true;

            Map<Long, CustomerInvoiceProcessingBO> customerInvoiceProcessingMap = getCustomerInvoiceProcessingMap(bo, invoiceId);

            for (CustomerInvoiceProcessingBO invoiceProcessingBO : customerInvoiceProcessingMap.values()) {
                emailInvoiceService.sendInvoicesViaEmail(invoiceId, userId, invoiceProcessingBO);
            }
            sentByEmail = true;

            for (CustomerInvoiceProcessingBO invoiceProcessingBO : customerInvoiceProcessingMap.values()) {
                ediInvoiceService.sendInvoiceViaEDI(invoiceId, invoiceProcessingBO.getBillTo());
            }
            sentByEdi = true;

            InvoiceJMSMessage message = new InvoiceJMSMessage();
            message.setFinanceBO(bo);
            message.setInvoiceId(invoiceId);
            message.setUserId(userId);
            invoiceMessageProducer.sendDocumentsMessage(message);
            sentDocuments = true;

        } catch (Exception e) {
            String msg = getProcessErrorMessage(sentToFinance, sentByEmail, sentByEdi, sentDocuments) + e.getMessage();
            invoiceErrorService.saveInvoiceError(invoiceId, msg, e, sentToFinance, sentByEmail, sentByEdi, sentDocuments, userId);
            LOGGER.error(msg, e);
            return;
        }
        LOGGER.info("Finished processing invoices for invoice ID: {}", invoiceId);
    }

    @Override
    public Long prepareLoadsAndAdjustmentsForReProcessing(Collection<Long> loads, Collection<Long> adjustments, Long userId) {
        boolean reprocessLoads = CollectionUtils.isNotEmpty(loads);
        boolean reprocessAdjustments = CollectionUtils.isNotEmpty(adjustments);
        if (reprocessLoads || reprocessAdjustments) {
            Long invoiceId = invoiceDao.getNextInvoiceId();
            if (reprocessLoads) {
                invoiceDao.insertLoadsForReProcess(invoiceId, loads, userId);
            }
            if (reprocessAdjustments) {
                invoiceDao.insertAdjustmentsForReProcess(invoiceId, adjustments, userId);
            }
            return invoiceId;
        }
        return null;
    }

    @Override
    public void processInvoiceDocuments(SendToFinanceBO bo, Long invoiceId) throws ApplicationException, IOException {
        Map<Long, CustomerInvoiceProcessingBO> customerInvoiceProcessingMap = getCustomerInvoiceProcessingMap(bo, invoiceId);
        for (CustomerInvoiceProcessingBO invoiceProcessingBO : customerInvoiceProcessingMap.values()) {
            invoiceDocumentService.convertAndSendDocument(invoiceId, invoiceProcessingBO.getBillTo(), invoiceProcessingBO.getInvoices(),
                    invoiceProcessingBO.getInvoiceDocuments());
        }
    }

    private Map<Long, CustomerInvoiceProcessingBO> getCustomerInvoiceProcessingMap(SendToFinanceBO bo, Long invoiceId) throws ApplicationException {
        List<Long> billToIds = bo.getInvoiceProcessingDetails().stream().map(InvoiceProcessingBO::getBillToId).collect(Collectors.toList());
        List<BillToEntity> billToList = billToDao.getAll(billToIds);
        Map<Long, BillToEntity> billToMap = billToList.stream().collect(Collectors.toMap(BillToEntity::getId, Function.identity()));
        Map<Long, CustomerInvoiceProcessingBO> customerInvoiceProcessingMap = new HashMap<Long, CustomerInvoiceProcessingBO>();

        List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(invoiceId);

        for (InvoiceProcessingBO invoiceBO : bo.getInvoiceProcessingDetails()) {
            BillToEntity billTo =  billToMap.get(invoiceBO.getBillToId());
            List<LoadAdjustmentBO> invoicesForBillTo =
                    invoices.stream().filter(invoice -> isTheSameBillTo(billTo.getId(), invoice)).collect(Collectors.toList());
            if (!invoicesForBillTo.isEmpty()) { // it can be empty when reprocessing invoices
                Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = documentGeneratorService.generateInvoiceDocuments(billTo,
                        invoicesForBillTo);
                customerInvoiceProcessingMap.put(billTo.getId(),
                        createCustomerInvoiceProcessingBO(invoiceBO, billTo, invoicesForBillTo, invoiceDocuments));
            } else {
                LOGGER.info("Skip sending information to customer for Bill To {}", invoiceBO.getBillToId());
            }
        }
        return customerInvoiceProcessingMap;
    }

    private boolean isTheSameBillTo(Long billToId, LoadAdjustmentBO bo) {
        return bo.getAdjustment() != null && billToId.equals(bo.getAdjustment().getCostDetailItems().iterator().next().getBillTo().getId())
                || (bo.getAdjustment() == null && billToId.equals(bo.getLoad().getBillToId()));
    }

    private CustomerInvoiceProcessingBO createCustomerInvoiceProcessingBO(InvoiceProcessingBO invoice,
            BillToEntity billTo, List<LoadAdjustmentBO> invoices, Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments) {
        CustomerInvoiceProcessingBO customerinvoiceProcessingBO = new CustomerInvoiceProcessingBO();
        customerinvoiceProcessingBO.setBillToId(billTo.getId());
        customerinvoiceProcessingBO.setEmail(invoice.getEmail());
        customerinvoiceProcessingBO.setComments(invoice.getComments());
        customerinvoiceProcessingBO.setSubject(invoice.getSubject());
        customerinvoiceProcessingBO.setBillTo(billTo);
        customerinvoiceProcessingBO.setInvoices(invoices);
        customerinvoiceProcessingBO.setInvoiceDocuments(invoiceDocuments);
        return customerinvoiceProcessingBO;
    }

    private String getProcessErrorMessage(boolean sentToFinance, boolean sentByEmail, boolean sentByEdi, boolean sentDocuments) {
        if (!sentToFinance) {
            return "Error during sending invoices to AX. ";
        }
        if (!sentByEmail) {
            return "Error during sending invoices to customer via Email. ";
        }
        if (!sentByEdi) {
            return "Error during sending invoices to customer via EDI. ";
        }
        if (!sentDocuments) {
            return "Error during sending documents to finance. ";
        }
        return StringUtils.EMPTY;
    }
}
