package com.pls.invoice.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.dao.HistoryInvoiceDao;
import com.pls.invoice.domain.bo.CBIHistoryBO;
import com.pls.invoice.domain.bo.InvoiceHistoryBO;
import com.pls.invoice.domain.bo.ReprocessHistoryBO;
import com.pls.invoice.service.InvoiceHistoryService;
import com.pls.invoice.service.processing.EDIInvoiceProcessingService;
import com.pls.invoice.service.processing.EmailInvoiceProcessingService;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceDocumentGeneratorService;
import com.pls.invoice.service.processing.InvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceService;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Implementation of {@link InvoiceHistoryService}.
 *
 * @author Alexander Kirichenko
 */
@Service
@Transactional
public class InvoiceHistoryServiceImpl implements InvoiceHistoryService {

    @Autowired
    private HistoryInvoiceDao historyDao;

    @Autowired
    private FinancialInvoiceDao invoiceDao;

    @Autowired
    private FinancialInvoiceProcessingService finacialInvoiceService;

    @Autowired
    private InvoiceProcessingService invoiceProcessingService;

    @Autowired
    private EmailInvoiceProcessingService emailInvoiceService;

    @Autowired
    private EDIInvoiceProcessingService ediInvoiceService;

    @Autowired
    private InvoiceDocumentGeneratorService documentGeneratorService;

    @Autowired
    private InvoiceService invoiceService;

    @Override
    public List<InvoiceHistoryBO> getInvoiceHistory(RegularSearchQueryBO search, Long userId) {
        return historyDao.getInvoiceHistory(search, userId);
    }

    @Override
    public List<CBIHistoryBO> getInvoiceHistoryCBIDetails(Long invoiceId, String groupInvoiceNumber) {
        return historyDao.getInvoiceHistoryCBIDetails(invoiceId, groupInvoiceNumber);
    }

    @Override
    public void reprocessHistory(ReprocessHistoryBO reprocessBO, Long personId) throws ApplicationException {
        Long invoiceId = prepareLoadsAndAdjustmentsForReprocessing(reprocessBO, personId);

        // from now there is only one bill to for specified invoiceId
        if (BooleanUtils.isTrue(reprocessBO.getFinancial())) {
            finacialInvoiceService.sendInvoicesToAX(invoiceId, personId);
        }

        BillToEntity billTo = invoiceDao.getBillToByInvoiceId(invoiceId);
        if (BooleanUtils.isTrue(reprocessBO.getCustomerEmail()) && !StringUtils.isEmpty(reprocessBO.getEmails())) {
            List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(invoiceId);
            Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = documentGeneratorService.generateInvoiceDocuments(billTo, invoices);
            emailInvoiceService.sendInvoicesViaEmail(invoiceId, reprocessBO.getEmails(), reprocessBO.getSubject(), reprocessBO.getComments(),
                    billTo, personId, invoiceDocuments);
        }

        if (BooleanUtils.isTrue(reprocessBO.getCustomerEDI())) {
            ediInvoiceService.sendInvoiceViaEDI(invoiceId, billTo);
        }
    }

    private Long prepareLoadsAndAdjustmentsForReprocessing(ReprocessHistoryBO reprocessBO, Long personId) {
        Long invoiceId = reprocessBO.getInvoiceId();
        List<Long> loads = reprocessBO.getLoadIds();
        List<Long> adjustments = reprocessBO.getAdjustmentIds();
        if (CollectionUtils.isEmpty(loads) && CollectionUtils.isEmpty(adjustments) && StringUtils.isNotBlank(reprocessBO.getInvoiceNumber())) {
            // get loads and adjustments for re-process by Invoice ID and invoice Number
            List<CBIHistoryBO> cbiDetails = historyDao.getInvoiceHistoryCBIDetails(invoiceId, reprocessBO.getInvoiceNumber());
            loads = cbiDetails.stream().filter(item -> item.getAdjustmentId() == null).map(CBIHistoryBO::getLoadId).collect(Collectors.toList());
            adjustments = cbiDetails.stream().filter(item -> item.getAdjustmentId() != null).map(CBIHistoryBO::getAdjustmentId)
                    .filter(Objects::nonNull).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(loads) || CollectionUtils.isNotEmpty(adjustments)) {
            return invoiceProcessingService.prepareLoadsAndAdjustmentsForReProcessing(loads, adjustments, personId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
