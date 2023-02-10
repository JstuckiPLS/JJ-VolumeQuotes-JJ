package com.pls.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.PromoCodesDao;
import com.pls.core.domain.user.PromoCodeEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.user.dao.UserDao;
import com.pls.user.service.TermsAndConditionsService;

/**
 * {@TermsAndConditionsService} implementation.
 *
 * @author Brichak Aleksandr
 */
@Service
@Transactional
public class TermsAndConditionsServiceImpl implements TermsAndConditionsService {

    @Value("${pls.termsAndConditionsVersion}")
    private long termsAndConditionsVersion;

    @Autowired
    private PromoCodesDao promoCodeDao;

    @Autowired
    private UserDao userDao;

    @Override
    public void applyTermsAndConditions() {
        PromoCodeEntity promoCode = promoCodeDao.getPromoCodeByUser();
        if (promoCode != null) {
            promoCode.setTermsAndConditionsVersion(termsAndConditionsVersion);
            promoCodeDao.saveOrUpdate(promoCode);
        } else {
            UserEntity user = userDao.find(SecurityUtils.getCurrentPersonId());
            promoCode = new PromoCodeEntity();
            promoCode.setStatus(Status.ACTIVE);
            promoCode.setTermsAndConditionsVersion(termsAndConditionsVersion);
            promoCode.setAccountExecutive(user);
            user.getPromoCodes().add(promoCode);
            userDao.saveOrUpdate(user);
        }
    }

    @Override
    public boolean isTermsAndConditionsAccepted() {
        PromoCodeEntity promoCode = promoCodeDao.getPromoCodeByUser();
        return promoCode != null && promoCode.getTermsAndConditionsVersion() != null
                && promoCode.getTermsAndConditionsVersion() == termsAndConditionsVersion;
    }

}
