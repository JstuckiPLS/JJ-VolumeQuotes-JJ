package com.pls.user.service;

/**
 * Service to manage Terms and Conditions information.
 *
 * @author Brichak Aleksandr
 */
public interface TermsAndConditionsService {

    /**
     * Apply Terms and Conditions.
     * 
     */
    void applyTermsAndConditions();

    /**
     * Check that Terms and Conditions is applied for logged user.
     * 
     * @return <code>true</code> if Terms and Conditions already applied otherwise <code>false</code>
     */
    boolean isTermsAndConditionsAccepted();
}
