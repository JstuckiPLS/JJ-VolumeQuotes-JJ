package com.pls.ltlrating.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.dao.LtlCarrierLiabilitiesDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.ProhibitedNLiabilitiesVO;
import com.pls.ltlrating.service.LtlCarrierLiabilitiesService;
import com.pls.ltlrating.service.LtlProfileDetailsService;

/**
 * Implementation of {@link LtlCarrierLiabilitiesService}.
 *
 * @author Artem Arapov
 *
 */
@Service
@Transactional(readOnly = true)
public class LtlCarrierLiabilitiesServiceImpl implements LtlCarrierLiabilitiesService {

    private static final String BLANKET = "BLANKET";

    @Autowired
    private LtlCarrierLiabilitiesDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Autowired
    private LtlProfileDetailsService profileService;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<LtlCarrierLiabilitiesEntity> saveCarrierLiabilities(List<LtlCarrierLiabilitiesEntity> list, Long profileId) {
        boolean needChildCopy = BLANKET.equalsIgnoreCase(profileDao.findPricingTypeByProfileId(profileId));

        saveListOfEntities(list, profileId);
        if (needChildCopy) {
            copyingListToChildCSPs(list, profileId);
        }

        return list;
    }

    @Override
    public LtlCarrierLiabilitiesEntity getCarrierLiabilitiesById(Long id) {
        return dao.find(id);
    }

    @Override
    public List<LtlCarrierLiabilitiesEntity> getCarrierLiabilitiesByProfileId(Long profileId) {
        return dao.findCarrierLiabilitiesByProfileId(profileId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<LtlCarrierLiabilitiesEntity> saveProhibitedNLiabilities(ProhibitedNLiabilitiesVO vo) throws ValidationException {
        profileService.saveProfile(vo.getProfile());

        return saveCarrierLiabilities(vo.getLiabilities(), vo.getProfile().getId());
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public LtlPricingProfileEntity cloneProhibitedCommodities(Long copyFromProfileId, Long copyToProfileId) throws ValidationException {
        LtlPricingProfileEntity fromProfile = profileService.getProfileById(copyFromProfileId);
        LtlPricingProfileEntity toProfile = profileService.getProfileById(copyToProfileId);

        toProfile.setProhibitedCommodities(fromProfile.getProhibitedCommodities());

        profileService.saveProfile(toProfile);

        return toProfile;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<LtlCarrierLiabilitiesEntity> cloneLiabilities(Long copyFromProfileDetailId, Long copyToProfileDetailId) throws ValidationException {
        List<LtlCarrierLiabilitiesEntity> liabilities = getCarrierLiabilitiesByProfileId(copyFromProfileDetailId);
        return saveCarrierLiabilities(cloneList(liabilities), copyToProfileDetailId);
    }

    private List<LtlCarrierLiabilitiesEntity> cloneList(List<LtlCarrierLiabilitiesEntity> source) {
        List<LtlCarrierLiabilitiesEntity> clones = new ArrayList<LtlCarrierLiabilitiesEntity>(source.size());

        for (LtlCarrierLiabilitiesEntity item : source) {
            clones.add(cloneEntity(item));
        }

        return clones;
    }

    private LtlCarrierLiabilitiesEntity cloneEntity(LtlCarrierLiabilitiesEntity source) {
        LtlCarrierLiabilitiesEntity clone = new LtlCarrierLiabilitiesEntity();
        clone.setFreightClass(source.getFreightClass());
        clone.setMaxNewProdLiabAmt(source.getMaxNewProdLiabAmt());
        clone.setMaxUsedProdLiabAmt(source.getMaxUsedProdLiabAmt());
        clone.setNewProdLiabAmt(source.getNewProdLiabAmt());
        clone.setUsedProdLiabAmt(source.getUsedProdLiabAmt());
        return clone;
    }

    private List<LtlCarrierLiabilitiesEntity> saveListOfEntities(List<LtlCarrierLiabilitiesEntity> list, Long profileId) {
        dao.deleteLiabilities(profileId);

        for (LtlCarrierLiabilitiesEntity item : list) {
            item.setPricingProfileId(profileId);

            dao.saveOrUpdate(item);
        }

        return list;
    }

    private void copyingListToChildCSPs(List<LtlCarrierLiabilitiesEntity> list, Long profileId) {
        List<Long> childProfileIds = profileDao.findChildCSPByProfileId(profileId);

        for (Long profile : childProfileIds) {
            saveListOfEntities(cloneList(list), profile);
        }
    }
}
