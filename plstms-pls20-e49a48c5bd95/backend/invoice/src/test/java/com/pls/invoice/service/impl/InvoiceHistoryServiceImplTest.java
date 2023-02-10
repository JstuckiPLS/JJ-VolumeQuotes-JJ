package com.pls.invoice.service.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceSortType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.dao.HistoryInvoiceDao;
import com.pls.invoice.domain.bo.CBIHistoryBO;
import com.pls.invoice.domain.bo.InvoiceHistoryBO;
import com.pls.invoice.domain.bo.ReprocessHistoryBO;
import com.pls.invoice.service.processing.EDIInvoiceProcessingService;
import com.pls.invoice.service.processing.EmailInvoiceProcessingService;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceDocumentGeneratorService;
import com.pls.invoice.service.processing.InvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceService;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Test cases for {@link InvoiceHistoryServiceImpl} class.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class InvoiceHistoryServiceImplTest {

    @Mock
    private HistoryInvoiceDao historyDao;

    @Mock
    private FinancialInvoiceDao invoiceDao;

    @Mock
    private FinancialInvoiceProcessingService finacialInvoiceService;

    @Mock
    private InvoiceProcessingService invoiceProcessingService;

    @Mock
    private EmailInvoiceProcessingService emailInvoiceService;

    @Mock
    private EDIInvoiceProcessingService ediInvoiceService;

    @Mock
    private InvoiceDocumentGeneratorService documentGeneratorService;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceHistoryServiceImpl service;

    private static final Long PERSON_ID = 1L;

    private static final Long INVOICE_ID = 10L;

    @Test
    public void shouldGetInvoiceHistory() {
        List<InvoiceHistoryBO> invoiceHistory = new ArrayList<InvoiceHistoryBO>();

        RegularSearchQueryBO search = new RegularSearchQueryBO();
        when(historyDao.getInvoiceHistory(search, PERSON_ID)).thenReturn(invoiceHistory);

        List<InvoiceHistoryBO> result = service.getInvoiceHistory(search, PERSON_ID);
        assertSame(invoiceHistory, result);
    }

    @Test
    public void shouldGetInvoiceHistoryCBIDetails() {
        List<CBIHistoryBO> invoiceHistoryCBI = new ArrayList<CBIHistoryBO>();
        String invoiceNumber = "invoiceNumber" + Math.random();
        when(historyDao.getInvoiceHistoryCBIDetails(1L, invoiceNumber)).thenReturn(invoiceHistoryCBI);

        List<CBIHistoryBO> result = service.getInvoiceHistoryCBIDetails(1L, invoiceNumber);
        assertSame(invoiceHistoryCBI, result);
    }

    @Test
    public void shouldReprocessInvoiceHistory() throws ApplicationException {
        ReprocessHistoryBO reprocessBO = new ReprocessHistoryBO();
        reprocessBO.setInvoiceId((long) (Math.random() * 100));
        List<Long> loadIds = new ArrayList<Long>();
        loadIds.add((long) (Math.random() * 100));
        reprocessBO.setLoadIds(loadIds);
        reprocessBO.setFinancial(true);
        reprocessBO.setInvoiceId((long) (Math.random() * 100));
        reprocessBO.setCustomerEmail(true);
        reprocessBO.setEmails("testmail@mail.com");
        reprocessBO.setSubject("subject" + Math.random());
        reprocessBO.setComments("comments" + Math.random());
        reprocessBO.setCustomerEDI(true);

        BillToEntity billTo = new BillToEntity();
        billTo.setInvoiceSettings(new InvoiceSettingsEntity());
        billTo.getInvoiceSettings().setSortType(InvoiceSortType.LOAD_ID);

        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new HashMap<>();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>(1);

        when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);
        when(invoiceProcessingService.prepareLoadsAndAdjustmentsForReProcessing(reprocessBO.getLoadIds(),
                                reprocessBO.getAdjustmentIds(), PERSON_ID))
                .thenReturn(INVOICE_ID);
        when(invoiceDao.getBillToByInvoiceId(INVOICE_ID)).thenReturn(billTo);

        service.reprocessHistory(reprocessBO, PERSON_ID);

        verify(invoiceProcessingService).prepareLoadsAndAdjustmentsForReProcessing(reprocessBO.getLoadIds(),
                        reprocessBO.getAdjustmentIds(), PERSON_ID);

        verify(invoiceDao).getBillToByInvoiceId(Matchers.eq(INVOICE_ID));

        verify(finacialInvoiceService).sendInvoicesToAX(INVOICE_ID, PERSON_ID);
        verify(emailInvoiceService).sendInvoicesViaEmail(INVOICE_ID, reprocessBO.getEmails(), reprocessBO.getSubject(),
                reprocessBO.getComments(), billTo, PERSON_ID, invoiceDocuments);
        verify(ediInvoiceService).sendInvoiceViaEDI(INVOICE_ID, billTo);
    }
}
