package com.pls.invoice.dao.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.shared.Status;
import com.pls.invoice.dao.CustomerInvoiceErrorsDao;
import com.pls.invoice.domain.CustomerInvoiceErrorEntity;
import com.pls.invoice.domain.bo.InvoiceErrorDetailsBO;

/**
 * Tests for {@link CustomerInvoiceErrorsDao}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class CustomerInvoiceErrorsDaoImplIT extends AbstractDaoTest {
    @Autowired
    private CustomerInvoiceErrorsDao dao;

    @Test
    public void testGetActiveErrors() {
        List<CustomerInvoiceErrorEntity> actual = dao.getActiveErrors();
        Assert.assertNotNull(actual);
        Assert.assertFalse(actual.isEmpty());
        for (CustomerInvoiceErrorEntity error : actual) {
            Assert.assertEquals(Status.ACTIVE, error.getStatus());
        }
    }

    @Test
    public void testGetActiveErrorsCount() {
        Long expected = (Long) getSession().createQuery("select count(errors) from CustomerInvoiceErrorEntity errors where errors.status = 'A'").
                uniqueResult();

        Long actual = dao.getActiveErrorsCount();
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetInvoiceErrorDetails() {
        executeScript("prepareInvoiceErrors.sql");

        List<InvoiceErrorDetailsBO> invoiceErrorDetails = dao.getInvoiceErrorDetails(1);
        Assert.assertEquals(4, invoiceErrorDetails.size());
        List<InvoiceType> invoiceTypes = invoiceErrorDetails.stream().map(InvoiceErrorDetailsBO::getInvoiceType).sorted()
                .collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList(InvoiceType.TRANSACTIONAL, InvoiceType.TRANSACTIONAL, InvoiceType.CBI, InvoiceType.CBI), invoiceTypes);
        List<String> invoiceNumbers = invoiceErrorDetails.stream().map(InvoiceErrorDetailsBO::getInvoiceNumber).sorted()
                .collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("I-1-adj", "I-2-adj", "I-812-0000", "I-813-0000"), invoiceNumbers);
    }

    @Test
    public void shouldNotGetInvoiceErrorDetailsForErrorsWithProcessedEmails() {
        executeScript("prepareInvoiceErrors.sql");

        List<InvoiceErrorDetailsBO> invoiceErrorDetails = dao.getInvoiceErrorDetails(2);
        Assert.assertEquals(0, invoiceErrorDetails.size());
    }

    @Test
    public void shouldNotGetInvoiceErrorDetailsForInactiveErrors() {
        executeScript("prepareInvoiceErrors.sql");

        List<InvoiceErrorDetailsBO> invoiceErrorDetails = dao.getInvoiceErrorDetails(3);
        Assert.assertEquals(0, invoiceErrorDetails.size());
    }
}
