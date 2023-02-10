package com.pls.extint.service;


import java.math.BigDecimal;

import com.pls.core.exception.ApplicationException;

/**
 * Interface for connecting to the financial integration service to retrieve
 * a customer's current credit balance.
 *
 * @author Thomas Clancy
 */
public interface AxIntegrationService {

    /**
     * Get AmountCurr from AX CustOpenBalance for specified account.
     *
     * @param accountNum
     *            The customer account number.
     * @return current credit balance currency.
     *
     * @throws ApplicationException
     *             If there was a problem connecting to the remote integration service or if the there was a
     *             problem locating the credit amount in the returned xml.
     */
    BigDecimal getCustomerCurrentCreditBalance(String accountNum) throws ApplicationException;
}
