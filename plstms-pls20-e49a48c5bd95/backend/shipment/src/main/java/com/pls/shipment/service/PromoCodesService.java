package com.pls.shipment.service;

/**
 * Service for promo codes.
 * 
 * @author Brichak Aleksandr
 * 
 */
public interface PromoCodesService {

    /**
     * Check uniqueness of code.
     *
     * @param code
     *            promo code.
     * @param personId
     *            id of person.
     * 
     * @return <code>true</code> if promo code unique. <code>false</code> otherwise
     */
    boolean isPromoCodeUnique(String code, Long personId);

}
