package com.pls.shipment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.PromoCodesDao;
import com.pls.shipment.service.PromoCodesService;

/**
 * {@link PromoCodesService} implementation.
 *
 * @author Brichak Aleksandr
 */

@Service
@Transactional
public class PromoCodesServiceImpl implements PromoCodesService {

    @Autowired
    private PromoCodesDao promoCodesDao;

    @Override
    public boolean isPromoCodeUnique(String code, Long personId) {
        return promoCodesDao.isPromoCodeUnique(code, personId);
    }
}
