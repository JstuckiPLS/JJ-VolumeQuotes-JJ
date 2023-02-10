package com.pls.core.dao;

import com.pls.core.domain.user.PromoCodeEntity;

/**
 * DAO that works with PromoCodeEntity in PLS 2.0 system.
 * 
 * @author Brichak Aleksandr
 */
public interface PromoCodesDao extends AbstractDao<PromoCodeEntity, Long> {

    /**
     * Check uniqueness of code.
     *
     * @param code
     *            promo code.
     * @param personId
     *            id of Person.
     * 
     * @return <code>true</code> if promo code unique. <code>false</code> otherwise
     */
    boolean isPromoCodeUnique(String code, Long personId);

    /**
     * Find Promo code by User.
     * 
     * @return {@link PromoCodeEntity}
     * 
     */
    PromoCodeEntity getPromoCodeByUser();
}
