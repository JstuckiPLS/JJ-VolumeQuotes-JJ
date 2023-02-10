package com.pls.invoice.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.domain.bo.InvoiceProcessingBO;
import com.pls.invoice.domain.bo.InvoiceResultBO;
import com.pls.invoice.domain.bo.SendToFinanceBO;
import com.pls.invoice.service.InvoiceErrorService;
import com.pls.invoice.service.impl.jms.InvoiceJMSMessage;
import com.pls.invoice.service.jms.InvoiceMessageProducer;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceProcessingService;

/**
 * Test for {@link FinancialBoardServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class FinancialBoardServiceImplTest {
    @Mock
    private FinancialInvoiceDao invoiceDao;

    @Mock
    private InvoiceProcessingService invoiceProcessingService;

    @Mock
    private InvoiceErrorService invoiceErrorService;

    @Mock
    private FinancialInvoiceProcessingService finacialInvoiceService;

    @Mock
    private InvoiceMessageProducer invoiceMessageProducer;

    @InjectMocks
    private FinancialBoardServiceImpl finBoard;

    @Test
    public void shouldProcessTransactionalLoadsAndAdjustments() throws Exception {
        Long billToId = (long) (Math.random() * 100);
        List<Long> loads = Arrays.asList((long) Math.random() * 100);
        List<Long> adjustments = Arrays.asList((long) Math.random() * 100);
        String emails = "emails" + Math.random();
        Long userId = (long) (Math.random() * 100);
        Date invoiceDate = DateUtils.addDays(new Date(), -3);
        Long invoiceId = (long) (Math.random() * 100);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setLoadIds(loads);
        invoiceBillToDetails.setAdjustmentIds(adjustments);
        invoiceBillToDetails.setEmail(emails);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));
        bo.setInvoiceDate(invoiceDate);

        InvoiceJMSMessage billToMessage = new InvoiceJMSMessage();
        billToMessage.setUserId(userId);
        billToMessage.setInvoiceId(invoiceId);
        billToMessage.setFinanceBO(bo);

        Mockito.when(finacialInvoiceService.processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId))).thenReturn(invoiceId);

        List<InvoiceResultBO> processedInvoices = Arrays.asList(getInvoiceResultBO(), getInvoiceResultBO());
        Mockito.when(invoiceDao.getInvoiceResults(invoiceId)).thenReturn(processedInvoices);

        List<InvoiceResultBO> processInvoicesResult = finBoard.processInvoices(bo, userId);

        Assert.assertSame(processedInvoices, processInvoicesResult);
        Mockito.verify(finacialInvoiceService).processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId));
        Mockito.verify(invoiceMessageProducer, Mockito.times(1)).sendMessage(Mockito.any(InvoiceJMSMessage.class));
        Mockito.verify(invoiceDao).getInvoiceResults(invoiceId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldProcessCBI() throws Exception {
        Long billToId = (long) (Math.random() * 100);
        List<Long> loads = Collections.<Long>emptyList();
        List<Long> adjustments = Collections.<Long>emptyList();
        String emails = "emails" + Math.random();
        Long userId = (long) (Math.random() * 100);
        Date invoiceDate = DateUtils.addDays(new Date(), -3);
        Long invoiceId = (long) (Math.random() * 100);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setLoadIds(loads);
        invoiceBillToDetails.setAdjustmentIds(adjustments);
        invoiceBillToDetails.setEmail(emails);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));
        bo.setInvoiceDate(invoiceDate);

        InvoiceJMSMessage billToMessage = new InvoiceJMSMessage();
        billToMessage.setUserId(userId);
        billToMessage.setInvoiceId(invoiceId);
        billToMessage.setFinanceBO(bo);

        Mockito.when(finacialInvoiceService.processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId))).thenReturn(invoiceId);
        List<InvoiceResultBO> processedInvoices = Arrays.asList(getInvoiceResultBO(), getInvoiceResultBO());
        Mockito.when(invoiceDao.getInvoiceResults(invoiceId)).thenReturn(processedInvoices);

        List<InvoiceResultBO> processInvoicesResult = finBoard.processInvoices(bo, userId);

        Assert.assertSame(processedInvoices, processInvoicesResult);
        Mockito.verify(finacialInvoiceService).processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId));
        Mockito.verify(invoiceMessageProducer, Mockito.times(1)).sendMessage(Mockito.any(InvoiceJMSMessage.class));
        Mockito.verify(invoiceDao).getInvoiceResults(invoiceId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldNotSendToCustomerFailedCBIProcessing() throws Exception {
        Long billToId = (long) (Math.random() * 100);
        List<Long> loads = Collections.<Long>emptyList();
        List<Long> adjustments = Collections.<Long>emptyList();
        String emails = "emails" + Math.random();
        Long userId = (long) (Math.random() * 100);
        Date invoiceDate = DateUtils.addDays(new Date(), -3);
        Long invoiceId = (long) (Math.random() * 100);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setLoadIds(loads);
        invoiceBillToDetails.setAdjustmentIds(adjustments);
        invoiceBillToDetails.setEmail(emails);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));
        bo.setInvoiceDate(invoiceDate);

        Mockito.when(finacialInvoiceService.processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId))).thenReturn(invoiceId);
        InvoiceResultBO failedInvoice = getInvoiceResultBO();
        failedInvoice.setErrorMessage("errorMessage" + Math.random());
        List<InvoiceResultBO> processedInvoices = Arrays.asList(getInvoiceResultBO(), failedInvoice, getInvoiceResultBO());
        Mockito.when(invoiceDao.getInvoiceResults(invoiceId)).thenReturn(processedInvoices);

        List<InvoiceResultBO> processInvoicesResult = finBoard.processInvoices(bo, userId);

        Assert.assertSame(processedInvoices, processInvoicesResult);
        Mockito.verify(finacialInvoiceService).processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId));
        Mockito.verify(invoiceDao).getInvoiceResults(invoiceId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldNotSendToCustomerFailedCBIProcessingWhenIncorrectFiniancialStatus() throws Exception {
        Long billToId = (long) (Math.random() * 100);
        List<Long> loads = Collections.<Long>emptyList();
        List<Long> adjustments = Collections.<Long>emptyList();
        String emails = "emails" + Math.random();
        Long userId = (long) (Math.random() * 100);
        Date invoiceDate = DateUtils.addDays(new Date(), -3);
        Long invoiceId = (long) (Math.random() * 100);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setLoadIds(loads);
        invoiceBillToDetails.setAdjustmentIds(adjustments);
        invoiceBillToDetails.setEmail(emails);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));
        bo.setInvoiceDate(invoiceDate);

        Mockito.when(finacialInvoiceService.processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId))).thenReturn(invoiceId);
        InvoiceResultBO failedInvoice = getInvoiceResultBO();
        failedInvoice.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING);
        List<InvoiceResultBO> processedInvoices = Arrays.asList(getInvoiceResultBO(), failedInvoice, getInvoiceResultBO());
        Mockito.when(invoiceDao.getInvoiceResults(invoiceId)).thenReturn(processedInvoices);

        List<InvoiceResultBO> processInvoicesResult = finBoard.processInvoices(bo, userId);

        Assert.assertSame(processedInvoices, processInvoicesResult);
        Mockito.verify(finacialInvoiceService).processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId));
        Mockito.verify(invoiceDao).getInvoiceResults(invoiceId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldNotSendToCustomerFailedTransactionalInvoiceLoadsProcessing() throws Exception {
        Long billToId = (long) (Math.random() * 100);
        List<Long> loads = Arrays.asList((long) Math.random() * 100);
        List<Long> adjustments = Collections.<Long>emptyList();
        String emails = "emails" + Math.random();
        Long userId = (long) (Math.random() * 100);
        Date invoiceDate = DateUtils.addDays(new Date(), -3);
        Long invoiceId = (long) (Math.random() * 100);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setLoadIds(loads);
        invoiceBillToDetails.setAdjustmentIds(adjustments);
        invoiceBillToDetails.setEmail(emails);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));
        bo.setInvoiceDate(invoiceDate);

        InvoiceJMSMessage billToMessage = new InvoiceJMSMessage();
        billToMessage.setUserId(userId);
        billToMessage.setInvoiceId(invoiceId);
        billToMessage.setFinanceBO(bo);

        Mockito.when(finacialInvoiceService.processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId))).thenReturn(invoiceId);
        InvoiceResultBO failedInvoice = getInvoiceResultBO();
        failedInvoice.setErrorMessage("errorMessage" + Math.random());
        List<InvoiceResultBO> processedInvoices = Arrays.asList(getInvoiceResultBO(), failedInvoice, getInvoiceResultBO());
        Mockito.when(invoiceDao.getInvoiceResults(invoiceId)).thenReturn(processedInvoices);

        List<InvoiceResultBO> processInvoicesResult = finBoard.processInvoices(bo, userId);

        Assert.assertSame(processedInvoices, processInvoicesResult);
        Mockito.verify(finacialInvoiceService).processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId));
        Mockito.verify(invoiceDao).getInvoiceResults(invoiceId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldNotSendToCustomerFailedTransactionalInvoiceAdjustmentsProcessing() throws Exception {
        Long billToId = (long) (Math.random() * 100);
        List<Long> loads = Collections.<Long>emptyList();
        List<Long> adjustments = Arrays.asList((long) Math.random() * 100);
        String emails = "emails" + Math.random();
        Long userId = (long) (Math.random() * 100);
        Date invoiceDate = DateUtils.addDays(new Date(), -3);
        Long invoiceId = (long) (Math.random() * 100);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setLoadIds(loads);
        invoiceBillToDetails.setAdjustmentIds(adjustments);
        invoiceBillToDetails.setEmail(emails);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));
        bo.setInvoiceDate(invoiceDate);

        InvoiceJMSMessage billToMessage = new InvoiceJMSMessage();
        billToMessage.setUserId(userId);
        billToMessage.setInvoiceId(invoiceId);
        billToMessage.setFinanceBO(bo);

        Mockito.when(finacialInvoiceService.processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId))).thenReturn(invoiceId);
        InvoiceResultBO failedInvoice = getInvoiceResultBO();
        failedInvoice.setErrorMessage("errorMessage" + Math.random());
        List<InvoiceResultBO> processedInvoices = Arrays.asList(getInvoiceResultBO(), failedInvoice, getInvoiceResultBO());
        Mockito.when(invoiceDao.getInvoiceResults(invoiceId)).thenReturn(processedInvoices);

        List<InvoiceResultBO> processInvoicesResult = finBoard.processInvoices(bo, userId);

        Assert.assertSame(processedInvoices, processInvoicesResult);
        Mockito.verify(finacialInvoiceService).processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId));
        Mockito.verify(invoiceDao).getInvoiceResults(invoiceId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    @Test
    public void shouldProcessInvoicesToFinanceAndSaveJMSError() throws Exception {
        Long billToId = (long) (Math.random() * 100);
        List<Long> loads = Collections.<Long>emptyList();
        List<Long> adjustments = Collections.<Long>emptyList();
        String emails = "emails" + Math.random();
        String subject = "subject" + Math.random();
        String comments = "comments" + Math.random();
        Long userId = (long) (Math.random() * 100);
        Date invoiceDate = DateUtils.addDays(new Date(), -3);
        Long invoiceId = (long) (Math.random() * 100);

        SendToFinanceBO bo = new SendToFinanceBO();
        InvoiceProcessingBO invoiceBillToDetails = new InvoiceProcessingBO();
        invoiceBillToDetails.setBillToId(billToId);
        invoiceBillToDetails.setLoadIds(loads);
        invoiceBillToDetails.setAdjustmentIds(adjustments);
        invoiceBillToDetails.setEmail(emails);
        invoiceBillToDetails.setSubject(subject);
        invoiceBillToDetails.setComments(comments);
        bo.setInvoiceProcessingDetails(Arrays.asList(invoiceBillToDetails));
        bo.setInvoiceDate(invoiceDate);

        InvoiceJMSMessage billToMessage = new InvoiceJMSMessage();
        billToMessage.setUserId(userId);
        billToMessage.setInvoiceId(invoiceId);
        billToMessage.setFinanceBO(bo);

        Mockito.when(finacialInvoiceService.processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId))).thenReturn(invoiceId);
        List<InvoiceResultBO> processedInvoices = Arrays.asList(getInvoiceResultBO(), getInvoiceResultBO());
        Mockito.when(invoiceDao.getInvoiceResults(invoiceId)).thenReturn(processedInvoices);

        ArgumentMatcher<InvoiceJMSMessage> matcher = new ArgumentMatcher<InvoiceJMSMessage>() {
            @Override
            public boolean matches(Object argument) {
                InvoiceJMSMessage result = (InvoiceJMSMessage) argument;
                return result.getInvoiceId().longValue() == billToMessage.getInvoiceId().longValue()
                        && result.getUserId().longValue() == billToMessage.getUserId().longValue()
                        && result.getFinanceBO().getInvoiceProcessingDetails().get(0).getBillToId().longValue()
                        == billToMessage.getFinanceBO().getInvoiceProcessingDetails().get(0).getBillToId().longValue();
            }
        };

        JMSException jmsException = new JMSException("jms" + Math.random());
        Mockito.doThrow(jmsException).when(invoiceMessageProducer).sendMessage(Mockito.argThat(matcher));

        List<InvoiceResultBO> processInvoicesResult = finBoard.processInvoices(bo, userId);

        Assert.assertSame(processedInvoices, processInvoicesResult);
        Mockito.verify(finacialInvoiceService).processInvoices(Mockito.eq(bo), Mockito.eq(true), Mockito.eq(userId));
        Mockito.verify(invoiceDao).getInvoiceResults(invoiceId);
        Mockito.verify(invoiceMessageProducer, Mockito.times(1)).sendMessage(Mockito.any(InvoiceJMSMessage.class));
        Mockito.verify(invoiceErrorService).saveInvoiceError(invoiceId, "Error during sending invoices to AX. " + jmsException.getMessage(),
                jmsException, false, false, false, false, userId);

        Mockito.verifyNoMoreInteractions(finacialInvoiceService);
        Mockito.verifyNoMoreInteractions(invoiceMessageProducer);
        Mockito.verifyNoMoreInteractions(invoiceErrorService);
        Mockito.verifyNoMoreInteractions(invoiceDao);
    }

    private InvoiceResultBO getInvoiceResultBO() {
        InvoiceResultBO result = new InvoiceResultBO();
        result.setInvoiceNumber("invoiceNumber" + Math.random());
        result.setAdjustmentId((long) (Math.random() * 100));
        result.setLoadId((long) (Math.random() * 100));
        result.setDoNotInvoice(Math.random() > 0.5);
        result.setBol("bol" + Math.random());
        result.setCost(new BigDecimal(Math.random()));
        result.setRevenue(new BigDecimal(Math.random()));
        result.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        return result;
    }
}
