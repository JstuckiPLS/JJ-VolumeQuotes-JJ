package com.pls.invoice.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.bo.CustomerCreditInfoBO;
import com.pls.invoice.dao.CustomerInvoiceDao;
import com.pls.invoice.domain.bo.CustomerInvoiceBO;
import com.pls.invoice.domain.bo.CustomerInvoiceCO;
import com.pls.invoice.domain.bo.CustomerInvoiceSummaryBO;
import com.pls.invoice.service.CustomerInvoiceService;

/**
 * Implementation of {@link CustomerInvoiceService}.
 *
 * @author Alexander Kirichenko
 */
@Service
@Transactional
public class CustomerInvoiceServiceImpl implements CustomerInvoiceService {
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerInvoiceDao customerInvoiceDao;

    @Override
    public List<CustomerInvoiceBO> findCustomerInvoices(Long customerId, CustomerInvoiceCO queryParams) {
        return customerInvoiceDao.findCustomerInvoices(customerId, queryParams == null ? new CustomerInvoiceCO() : queryParams);
    }

    @Override
    public CustomerInvoiceSummaryBO getCustomerInvoiceSummary(Long customerId) {
        return customerInvoiceDao.getCustomerInvoiceSummary(customerId);
    }

    @Override
    public CustomerCreditInfoBO getCustomerCreditInfo(Long customerId) {
        CustomerCreditInfoBO customerCreditInfoBO = customerDao.getCustomerCreditInfo(customerId);
        BigDecimal unPaid = customerInvoiceDao.getCustomerUnpaidAmount(customerId);
        customerCreditInfoBO.setUnpaid(unPaid);
        return customerCreditInfoBO;
    }
}
