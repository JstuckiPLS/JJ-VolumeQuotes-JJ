package com.pls.invoice.dao;

/**
 * DAO for billing status reasons.
 *
 * @author Aleksandr Leshchenko
 */
public interface BillingStatusReasonDao {

    /**
     * Get description of status reason by specified code.
     * 
     * @param reasonCode
     *            reason code
     * @return return string description of status reason.
     */
    String getDescriptionByCode(String reasonCode);
}
