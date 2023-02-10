package com.pls.invoice.service.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.enums.InvoiceType;
import com.pls.invoice.dao.CustomerInvoiceErrorsDao;
import com.pls.invoice.domain.bo.InvoiceErrorDetailsBO;
import com.pls.invoice.service.processing.EmailInvoiceProcessingService;

/**
 * Test cases for {@link InvoiceErrorServiceImpl} class.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class InvoiceErrorServiceImplTest {

    @Mock
    private EmailInvoiceProcessingService emailInvoiceService;

    @Mock
    private CustomerInvoiceErrorsDao errorsDao;

    @InjectMocks
    private InvoiceErrorServiceImpl service;

    @Test
    public void shouldGetEmailSubjectForReprocessTransactionalError() {
        long errorId = (long) (Math.random() * 100);
        String subject = "subject" + Math.random();

        List<InvoiceErrorDetailsBO> errorDetails = Arrays.asList(createInvoiceErrorDetails(InvoiceType.TRANSACTIONAL, "T-00123-0000"),
                createInvoiceErrorDetails(InvoiceType.TRANSACTIONAL, "T-01000-AD01"),
                createInvoiceErrorDetails(InvoiceType.TRANSACTIONAL, "T-00125-0000"));
        Mockito.when(errorsDao.getInvoiceErrorDetails(errorId)).thenReturn(errorDetails);
        Mockito.when(
                emailInvoiceService.getEmailSubject(InvoiceType.TRANSACTIONAL, Arrays.asList("T-00123-0000", "T-01000-AD01", "T-00125-0000")))
                .thenReturn(subject);

        String actualSubject = service.getEmailSubjectForReprocessError(errorId);

        Mockito.verify(emailInvoiceService).getEmailSubject(InvoiceType.TRANSACTIONAL,
                Arrays.asList("T-00123-0000", "T-01000-AD01", "T-00125-0000"));
        Assert.assertSame(subject, actualSubject);
    }

    @Test
    public void shouldGetEmailSubjectForReprocessCBIError() {
        long errorId = (long) (Math.random() * 100);
        String subject = "subject" + Math.random();

        List<InvoiceErrorDetailsBO> errorDetails = Arrays.asList(createInvoiceErrorDetails(InvoiceType.CBI, "C-120358"));
        Mockito.when(errorsDao.getInvoiceErrorDetails(errorId)).thenReturn(errorDetails);
        Mockito.when(emailInvoiceService.getEmailSubject(InvoiceType.CBI, Arrays.asList("C-120358"))).thenReturn(subject);

        String actualSubject = service.getEmailSubjectForReprocessError(errorId);

        Mockito.verify(emailInvoiceService).getEmailSubject(InvoiceType.CBI, Arrays.asList("C-120358"));
        Assert.assertSame(subject, actualSubject);
    }

    @Test
    public void shouldGetEmptyEmailSubject() {
        long errorId = (long) (Math.random() * 100);
        String subject = "subject" + Math.random();

        Mockito.when(emailInvoiceService.getEmailSubject(Mockito.any(), Mockito.any())).thenReturn(subject);

        String actualSubject = service.getEmailSubjectForReprocessError(errorId);

        Assert.assertNull(actualSubject);
    }

    private InvoiceErrorDetailsBO createInvoiceErrorDetails(InvoiceType invoiceType, String invoiceNumber) {
        InvoiceErrorDetailsBO bo = new InvoiceErrorDetailsBO();
        bo.setInvoiceType(invoiceType);
        bo.setInvoiceNumber(invoiceNumber);
        return bo;
    }
}
