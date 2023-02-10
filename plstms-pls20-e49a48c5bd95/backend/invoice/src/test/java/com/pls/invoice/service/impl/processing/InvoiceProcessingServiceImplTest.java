package com.pls.invoice.service.impl.processing;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.BillToDao;
import com.pls.core.domain.enums.CbiInvoiceType;
import com.pls.core.domain.enums.InvoiceDocument;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
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
import com.pls.invoice.service.processing.InvoiceService;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Test for {@link InvoiceProcessingServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class InvoiceProcessingServiceImplTest {

    @Mock
    private FinancialInvoiceProcessingService finacialInvoiceService;

    @Mock
    private EmailInvoiceProcessingService emailInvoiceService;

    @Mock
    private InvoiceErrorService invoiceErrorService;

    @Mock
    private FinancialInvoiceDao invoiceDao;

    @Mock
    private BillToDao billToDao;

    @Mock
    private EDIInvoiceProcessingService ediInvoiceService;

    @Mock
    private CustomerInvoiceDocumentService invoiceDocumentService;

    @Mock
    private InvoiceDocumentGeneratorService documentGeneratorService;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private InvoiceJmsMessageProducer invoiceMessageProducer;

    @InjectMocks
    private InvoiceProcessingServiceImpl sut;

    @Test
    public void shouldProcessInvoices() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        Long billToId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String subject = "subject" + Math.random();
        String comments = "comments" + Math.random();

        BillToEntity billTo = getBillTo(billToId, InvoiceType.TRANSACTIONAL, null);
        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new HashMap<>();
        Mockito.when(billToDao.getAll(Arrays.asList(billToId))).thenReturn(Arrays.asList(billTo));
        List<LoadAdjustmentBO> invoices = Arrays.asList(new LoadAdjustmentBO(getLoad(billTo)));
        when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(invoices);
        when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setEmail(emails);
        invoiceBillToDetails.setSubject(subject);
        invoiceBillToDetails.setComments(comments);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));

        sut.processInvoices(bo, invoiceId, personId);

        Mockito.verify(finacialInvoiceService).sendInvoicesToAX(invoiceId, personId);
        Mockito.verify(emailInvoiceService, Mockito.times(1)).sendInvoicesViaEmail(Mockito.eq(invoiceId), Mockito.eq(personId),
                Mockito.any(CustomerInvoiceProcessingBO.class));
        Mockito.verify(ediInvoiceService).sendInvoiceViaEDI(invoiceId, billTo);
        ArgumentCaptor<InvoiceJMSMessage> argumentCaptor = ArgumentCaptor.forClass(InvoiceJMSMessage.class);
        Mockito.verify(invoiceMessageProducer).sendDocumentsMessage(argumentCaptor.capture());
        InvoiceJMSMessage invoiceMessage = argumentCaptor.getValue();
        Assert.assertSame(invoiceId, invoiceMessage.getInvoiceId());
        Assert.assertSame(bo, invoiceMessage.getFinanceBO());
        Assert.assertSame(personId, invoiceMessage.getUserId());

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(emailInvoiceService);
        Mockito.verifyNoMoreInteractions(ediInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceDocumentService);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
    }

    @Test
    public void shouldSaveErrorWhenProcessingToAXFailed() throws ApplicationException {
        Long invoiceId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        Long billToId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String subject = "subject" + Math.random();
        String comments = "comments" + Math.random();

        BillToEntity billTo = getBillTo(billToId, InvoiceType.TRANSACTIONAL, null);
        Mockito.when(billToDao.getAll(Arrays.asList(billToId))).thenReturn(Arrays.asList(billTo));

        String message = "message" + Math.random();
        ApplicationException exception = new ApplicationException(message);
        Mockito.doThrow(exception).when(finacialInvoiceService).sendInvoicesToAX(invoiceId, personId);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setEmail(emails);
        invoiceBillToDetails.setSubject(subject);
        invoiceBillToDetails.setComments(comments);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));

        sut.processInvoices(bo, invoiceId, personId);

        Mockito.verify(finacialInvoiceService).sendInvoicesToAX(invoiceId, personId);
        Mockito.verify(invoiceErrorService).saveInvoiceError(invoiceId, "Error during sending invoices to AX. " + message, exception, false,
                false, false, false, personId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(emailInvoiceService);
        Mockito.verifyNoMoreInteractions(ediInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceDocumentService);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
    }

    @Test
    public void shouldSaveErrorWhenEmailSendingFailed() throws ApplicationException {
        Long invoiceId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        Long billToId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String subject = "subject" + Math.random();
        String comments = "comments" + Math.random();

        BillToEntity billTo = getBillTo(billToId, InvoiceType.TRANSACTIONAL, null);
        Mockito.when(billToDao.getAll(Arrays.asList(billToId))).thenReturn(Arrays.asList(billTo));
        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new HashMap<>();
        List<LoadAdjustmentBO> invoices = Arrays.asList(new LoadAdjustmentBO(getLoad(billTo)));
        when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(invoices);
        when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        String message = "message" + Math.random();
        ApplicationException exception = new ApplicationException(message);
        Mockito.doThrow(exception).when(emailInvoiceService).sendInvoicesViaEmail(Mockito.eq(invoiceId),
                Mockito.eq(personId), Mockito.any(CustomerInvoiceProcessingBO.class));

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setEmail(emails);
        invoiceBillToDetails.setSubject(subject);
        invoiceBillToDetails.setComments(comments);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));

        sut.processInvoices(bo, invoiceId, personId);

        Mockito.verify(finacialInvoiceService).sendInvoicesToAX(invoiceId, personId);
        Mockito.verify(emailInvoiceService, Mockito.times(1)).sendInvoicesViaEmail(Mockito.eq(invoiceId), Mockito.eq(personId),
                Mockito.any(CustomerInvoiceProcessingBO.class));
        Mockito.verify(invoiceErrorService).saveInvoiceError(invoiceId, "Error during sending invoices to customer via Email. " + message,
                exception, true, false, false, false, personId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(emailInvoiceService);
        Mockito.verifyNoMoreInteractions(ediInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceDocumentService);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
    }

    @Test
    public void shouldSaveErrorWhenEDISendingFailed() throws ApplicationException {
        Long invoiceId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        Long billToId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String subject = "subject" + Math.random();
        String comments = "comments" + Math.random();

        BillToEntity billTo = getBillTo(billToId, InvoiceType.TRANSACTIONAL, null);
        Mockito.when(billToDao.getAll(Arrays.asList(billToId))).thenReturn(Arrays.asList(billTo));
        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new HashMap<>();
        List<LoadAdjustmentBO> invoices = Arrays.asList(new LoadAdjustmentBO(getLoad(billTo)));
        when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(invoices);
        when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        String message = "message" + Math.random();
        ApplicationException exception = new ApplicationException(message);
        Mockito.doThrow(exception).when(ediInvoiceService).sendInvoiceViaEDI(invoiceId, billTo);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setEmail(emails);
        invoiceBillToDetails.setSubject(subject);
        invoiceBillToDetails.setComments(comments);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));

        sut.processInvoices(bo, invoiceId, personId);

        Mockito.verify(finacialInvoiceService).sendInvoicesToAX(invoiceId, personId);
        Mockito.verify(emailInvoiceService, Mockito.times(1)).sendInvoicesViaEmail(Mockito.eq(invoiceId), Mockito.eq(personId),
                Mockito.any(CustomerInvoiceProcessingBO.class));
        Mockito.verify(ediInvoiceService).sendInvoiceViaEDI(invoiceId, billTo);
        Mockito.verify(invoiceErrorService).saveInvoiceError(invoiceId, "Error during sending invoices to customer via EDI. " + message,
                exception, true, true, false, false, personId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(emailInvoiceService);
        Mockito.verifyNoMoreInteractions(ediInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceDocumentService);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
    }

    @Test
    public void shouldSaveErrorWhenConvertingAndSavingDocumentFailed() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        Long billToId = (long) (Math.random() * 100);
        Long personId = (long) (Math.random() * 100);
        String subject = "subject" + Math.random();
        String comments = "comments" + Math.random();

        BillToEntity billTo = getBillTo(billToId, InvoiceType.TRANSACTIONAL, null);
        Mockito.when(billToDao.getAll(Arrays.asList(billToId))).thenReturn(Arrays.asList(billTo));
        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new HashMap<>();
        List<LoadAdjustmentBO> invoices = Arrays.asList(new LoadAdjustmentBO(getLoad(billTo)));
        when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(invoices);
        when(documentGeneratorService.generateInvoiceDocuments(billTo, invoices)).thenReturn(invoiceDocuments);

        String message = "message" + Math.random();
        JMSException exception = new JMSException(message);
        Mockito.doThrow(exception).when(invoiceMessageProducer).sendDocumentsMessage(Mockito.any());

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setEmail(emails);
        invoiceBillToDetails.setSubject(subject);
        invoiceBillToDetails.setComments(comments);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));

        sut.processInvoices(bo, invoiceId, personId);

        Mockito.verify(finacialInvoiceService).sendInvoicesToAX(invoiceId, personId);
        Mockito.verify(emailInvoiceService, Mockito.times(1)).sendInvoicesViaEmail(Mockito.eq(invoiceId), Mockito.eq(personId),
                Mockito.any(CustomerInvoiceProcessingBO.class));
        Mockito.verify(ediInvoiceService).sendInvoiceViaEDI(invoiceId, billTo);
        ArgumentCaptor<InvoiceJMSMessage> argumentCaptor = ArgumentCaptor.forClass(InvoiceJMSMessage.class);
        Mockito.verify(invoiceMessageProducer).sendDocumentsMessage(argumentCaptor.capture());
        InvoiceJMSMessage invoiceMessage = argumentCaptor.getValue();
        Assert.assertSame(invoiceId, invoiceMessage.getInvoiceId());
        Assert.assertSame(bo, invoiceMessage.getFinanceBO());
        Assert.assertSame(personId, invoiceMessage.getUserId());
        Mockito.verify(invoiceErrorService).saveInvoiceError(invoiceId, "Error during sending documents to finance. " + message, exception,
                true, true, true, false, personId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(emailInvoiceService);
        Mockito.verifyNoMoreInteractions(ediInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceDocumentService);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
    }

    @Test
    public void shouldPrepareLoadsAndAdjustmentsForReProcessing() {
        Collection<Long> loads = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101);
        Collection<Long> adjustments = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101);
        Long userId = (long) (Math.random() * 100);
        Long invoiceId = (long) (Math.random() * 100);

        Mockito.when(invoiceDao.getNextInvoiceId()).thenReturn(invoiceId);

        Long result = sut.prepareLoadsAndAdjustmentsForReProcessing(loads, adjustments, userId);

        Assert.assertEquals(invoiceId, result);
        Mockito.verify(invoiceDao).getNextInvoiceId();
        Mockito.verify(invoiceDao).insertLoadsForReProcess(invoiceId, loads, userId);
        Mockito.verify(invoiceDao).insertAdjustmentsForReProcess(invoiceId, adjustments, userId);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldPrepareLoadsorReProcessing() {
        Collection<Long> loads = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101);
        Collection<Long> adjustments = null;
        Long userId = (long) (Math.random() * 100);
        Long invoiceId = (long) (Math.random() * 100);

        Mockito.when(invoiceDao.getNextInvoiceId()).thenReturn(invoiceId);

        Long result = sut.prepareLoadsAndAdjustmentsForReProcessing(loads, adjustments, userId);

        Assert.assertEquals(invoiceId, result);
        Mockito.verify(invoiceDao).getNextInvoiceId();
        Mockito.verify(invoiceDao).insertLoadsForReProcess(invoiceId, loads, userId);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldPrepareAdjustmentsForReProcessing() {
        Collection<Long> loads = null;
        Collection<Long> adjustments = Arrays.asList((long) (Math.random() * 100), (long) (Math.random() * 100) + 101);
        Long userId = (long) (Math.random() * 100);
        Long invoiceId = (long) (Math.random() * 100);

        Mockito.when(invoiceDao.getNextInvoiceId()).thenReturn(invoiceId);

        Long result = sut.prepareLoadsAndAdjustmentsForReProcessing(loads, adjustments, userId);

        Assert.assertEquals(invoiceId, result);
        Mockito.verify(invoiceDao).getNextInvoiceId();
        Mockito.verify(invoiceDao).insertAdjustmentsForReProcess(invoiceId, adjustments, userId);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldPrepareNothingForReProcessing() {
        Collection<Long> loads = null;
        Collection<Long> adjustments = null;
        Long userId = (long) (Math.random() * 100);
        Long invoiceId = (long) (Math.random() * 100);

        Mockito.when(invoiceDao.getNextInvoiceId()).thenReturn(invoiceId);

        Long result = sut.prepareLoadsAndAdjustmentsForReProcessing(loads, adjustments, userId);

        Assert.assertNull(result);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldProcessInvoiceDocuments() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        String emails = "emails" + Math.random();
        Long billToId = (long) (Math.random() * 100);
        String subject = "subject" + Math.random();
        String comments = "comments" + Math.random();

        BillToEntity billTo = getBillTo(billToId, InvoiceType.TRANSACTIONAL, null);
        Map<InvoiceDocument, LoadDocumentEntity> invoiceDocuments = new HashMap<>();
        Mockito.when(billToDao.getAll(Arrays.asList(billToId))).thenReturn(Arrays.asList(billTo));
        List<LoadAdjustmentBO> invoices = Arrays.asList(new LoadAdjustmentBO(getLoad(billTo)));
        when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(invoices);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setEmail(emails);
        invoiceBillToDetails.setSubject(subject);
        invoiceBillToDetails.setComments(comments);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));

        sut.processInvoiceDocuments(bo, invoiceId);

        Mockito.verify(invoiceDocumentService).convertAndSendDocument(invoiceId, billTo, invoices, invoiceDocuments);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(emailInvoiceService);
        Mockito.verifyNoMoreInteractions(ediInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceDocumentService);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
    }

    private LoadEntity getLoad(BillToEntity billTo) {
        LoadEntity load = new LoadEntity();
        load.setBillTo(billTo);
        return load;
    }

    private BillToEntity getBillTo(Long billToId, InvoiceType invoiceType, CbiInvoiceType cbiInvoiceType) {
        BillToEntity billTo = new BillToEntity();
        billTo.setId(billToId);
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(invoiceType);
        invoiceSettings.setCbiInvoiceType(cbiInvoiceType);
        billTo.setInvoiceSettings(invoiceSettings);
        return billTo;
    }
}
