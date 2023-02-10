package com.pls.invoice.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.invoice.dao.CustomerInvoiceDao;
import com.pls.invoice.domain.bo.CustomerInvoiceBO;
import com.pls.invoice.domain.bo.CustomerInvoiceCO;
import com.pls.invoice.domain.bo.CustomerInvoiceSummaryBO;
import com.pls.invoice.domain.bo.enums.CustomerInvoiceType;

/**
 * Test cases for {@link CustomerInvoiceDaoImpl}.
 *
 * @author Alexander Kirichenko
 */
public class CustomerInvoiceDaoImplIT extends AbstractDaoTest {

    public static final long CUSTOMER_ID = 1L;
    @Autowired
    private CustomerInvoiceDao dao;

    @Before
    public void init() {
        getSession().createQuery("update FinancialAccountReceivablesEntity set invoiceDueDate = current_date + 2 where load.id in (60, 61)")
                .executeUpdate();
    }

    @Test
    public void testFindCustomerInvoices() {
        CustomerInvoiceCO queryParams = new CustomerInvoiceCO();

        List<CustomerInvoiceBO> customerInvoices = dao.findCustomerInvoices(CUSTOMER_ID, queryParams);
        assertNotNull(customerInvoices);
        assertFalse(customerInvoices.isEmpty());
        assertEquals(12, customerInvoices.size());

        queryParams.setInvoiceType(CustomerInvoiceType.OPEN);
        customerInvoices = dao.findCustomerInvoices(CUSTOMER_ID, queryParams);
        assertNotNull(customerInvoices);
        assertFalse(customerInvoices.isEmpty());
        assertEquals(2, customerInvoices.size());

        Calendar from = Calendar.getInstance();
        from.set(2013, Calendar.JUNE, 2);
        Calendar to = Calendar.getInstance();
        to.set(2013, Calendar.SEPTEMBER, 2);
        queryParams.setInvoiceType(CustomerInvoiceType.PAID);
        queryParams.setPaidFrom(from.getTime());
        queryParams.setPaidTo(to.getTime());
        customerInvoices = dao.findCustomerInvoices(CUSTOMER_ID, queryParams);
        assertNotNull(customerInvoices);
        assertFalse(customerInvoices.isEmpty());
        assertEquals(2, customerInvoices.size());
    }

    @Test
    public void testGetCustomerInvoiceSummary() {
        CustomerInvoiceSummaryBO customerInvoiceSummary = dao.getCustomerInvoiceSummary(CUSTOMER_ID);
        assertNotNull(customerInvoiceSummary);
        assertEquals(new BigDecimal("2923.17"), customerInvoiceSummary.getOpenInvoices());
        assertEquals(new BigDecimal("994.30"), customerInvoiceSummary.getPastDueTotalInvoices());

        customerInvoiceSummary = dao.getCustomerInvoiceSummary(-1L);
        assertNotNull(customerInvoiceSummary);
        assertNull(customerInvoiceSummary.getOpenInvoices());
        assertNull(customerInvoiceSummary.getPastDueTotalInvoices());
        assertNull(customerInvoiceSummary.getPastDue1to30Invoices());
        assertNull(customerInvoiceSummary.getPastDue31to60Invoices());
        assertNull(customerInvoiceSummary.getPastDue61to90Invoices());
        assertNull(customerInvoiceSummary.getPastDue91Invoices());
    }

    @Test
    public void testGetCustomerUnpaidAmount() {
        BigDecimal amount = dao.getCustomerUnpaidAmount(CUSTOMER_ID);
        assertNotNull(amount);
        assertTrue(amount.compareTo(BigDecimal.ZERO) > 0);
    }
}
