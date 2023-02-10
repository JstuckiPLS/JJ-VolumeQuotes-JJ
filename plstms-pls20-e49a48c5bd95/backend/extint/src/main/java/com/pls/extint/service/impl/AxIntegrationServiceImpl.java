package com.pls.extint.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pls.ax.custopenbalance.client.AxCustOpenBalanceClient;
import com.pls.ax.custopenbalance.client.proxy.CustOpenBalance;
import com.pls.core.exception.ApplicationException;
import com.pls.extint.service.AxIntegrationService;

/**
 * Implementation of <code>AxIntegrationService</code> interface.
 *
 * @author Thomas Clancy
 */
@Service("axIntegrationService")
public class AxIntegrationServiceImpl implements AxIntegrationService {

    @Value("${finance.ax.custOpenBalanceService.endpoint}")
    private String endpoint;

    @Value("${finance.ax.username}")
    private String username;

    @Value("${finance.ax.password}")
    private String password;

    @Override
    public BigDecimal getCustomerCurrentCreditBalance(String accountNum) throws ApplicationException {
        try {
            AxCustOpenBalanceClient client = new AxCustOpenBalanceClient(endpoint, username, password);
            CustOpenBalance cob = client.getCustOpenBalanceByAccount(accountNum);
            return cob.getAmountCUR();
        } catch (Exception ex) {
            throw new ApplicationException("Remote exception caught trying to get credit balance for customer: " + accountNum, ex);
        }
    }
}
