package com.pls.ltlrating.service.impl;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.LtlPricingThirdPartyInfoDao;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;
import com.pls.ltlrating.service.LtlPricingThirdPartyInfoService;
import com.pls.ltlrating.service.validation.LtlPricingThirdPartyInfoValidator;

/**
 * Implementation of {@link LtlPricingThirdPartyInfoService}.
 *
 * @author Artem Arapov
 *
 */
@Service
@Transactional
public class LtlPricingThirdPartyInfoServiceImpl implements LtlPricingThirdPartyInfoService {

    @Autowired
    private LtlPricingThirdPartyInfoDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Resource(type = LtlPricingThirdPartyInfoValidator.class)
    private Validator<LtlPricingThirdPartyInfoEntity> validator;

    private static final String BLANKET = "BLANKET";

    @Override
    public LtlPricingThirdPartyInfoEntity saveThirdPartyInfo(LtlPricingThirdPartyInfoEntity entity)
            throws ValidationException {
        validator.validate(entity);

        boolean isNewEntity = entity.getId() == null;
        LtlPricingThirdPartyInfoEntity savedEntity = dao.saveOrUpdate(entity);

        if (profileDao.findPricingTypeByProfileDetailId(entity.getPricProfDetailId()).equalsIgnoreCase(BLANKET)) {
            modifyChildCSPEntity(entity, isNewEntity);
        }

        return savedEntity;
    }

    @Override
    public LtlPricingThirdPartyInfoEntity getThirdPartyInfoById(Long id) {
        return dao.find(id);
    }

    @Override
    public LtlPricingThirdPartyInfoEntity getActiveThirdPartyInfoByProfileDetailId(Long profileDetailId) {
        return dao.findActiveByProfileDetailId(profileDetailId);
    }

    @Override
    public LtlPricingThirdPartyInfoEntity getActiveByProfileId(Long profileId) {
        return dao.findByProfileId(profileId);
    }

    @Override
    public void copyFrom(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyChilds) {
        dao.updateStatus(copyToProfileDetailId, Status.INACTIVE, SecurityUtils.getCurrentPersonId());

        boolean copyChilds = shouldCopyChilds && profileDao.findPricingTypeByProfileDetailId(copyToProfileDetailId).equalsIgnoreCase(BLANKET);

        if (copyChilds) {
            dao.inactivateCSPByProfileDetailId(copyToProfileDetailId, SecurityUtils.getCurrentPersonId());
        }

        clone(copyFromProfileDetailId, copyToProfileDetailId, copyChilds);
    }

    private void modifyChildCSPEntity(LtlPricingThirdPartyInfoEntity entity, boolean isNewEntity) {
        if (isNewEntity) {
            addEntityToChildCSPs(entity);
        } else {
            updateEntityInChildCSPs(entity);
        }
    }

    private void addEntityToChildCSPs(LtlPricingThirdPartyInfoEntity entity) {
        List<LtlPricingProfileEntity> childProfilesList = profileDao.findChildCSPByProfileDetailId(entity.getPricProfDetailId());

        for (LtlPricingProfileEntity profile : childProfilesList) {
            addEntityToChildCSP(entity, profile);
        }
    }

    private void addEntityToChildCSP(LtlPricingThirdPartyInfoEntity source, LtlPricingProfileEntity childProfile) {
        for (LtlPricingProfileDetailsEntity detail : childProfile.getProfileDetails()) {
            addEntityToChildCSPDetail(source, detail.getId());
        }
    }

    private void addEntityToChildCSPDetail(LtlPricingThirdPartyInfoEntity source, Long profileDetailId) {
        LtlPricingThirdPartyInfoEntity entity = new LtlPricingThirdPartyInfoEntity(source);
        entity.setPricProfDetailId(profileDetailId);

        dao.saveOrUpdate(entity);
    }

    private void updateEntityInChildCSPs(LtlPricingThirdPartyInfoEntity entity) {
        List<LtlPricingThirdPartyInfoEntity> childs = dao.findAllCspChildsCopyedFrom(entity.getId());

        for (LtlPricingThirdPartyInfoEntity child : childs) {
            LtlPricingThirdPartyInfoEntity updatedChild = modifyChildCopyOfEntity(entity, child);

            dao.saveOrUpdate(updatedChild);
        }
    }

    private LtlPricingThirdPartyInfoEntity modifyChildCopyOfEntity(LtlPricingThirdPartyInfoEntity source, LtlPricingThirdPartyInfoEntity child) {
        child.setAccountNum(source.getAccountNum());
        child.setAddress(source.getAddress());
        child.setCompany(source.getCompany());
        child.setContactName(source.getContactName());
        child.setEmail(source.getEmail());
        child.setFax(source.getFax());
        child.setPhone(source.getPhone());
        child.setStatus(source.getStatus());

        return child;
    }

    private void clone(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyToChildCSP) {
        LtlPricingThirdPartyInfoEntity sourceEntity = dao.findActiveByProfileDetailId(copyFromProfileDetailId);

        if (sourceEntity == null) {
            return;
        }

        LtlPricingThirdPartyInfoEntity clonedEntity = cloneEntity(sourceEntity, copyToProfileDetailId);
        dao.saveOrUpdate(clonedEntity);

        if (shouldCopyToChildCSP) {
            cloneToChildCSP(copyToProfileDetailId, clonedEntity);
        }
    }

    private void cloneToChildCSP(Long parentDetailId, LtlPricingThirdPartyInfoEntity sourceEntity) {
        List<BigInteger> childsDetailsIdList = profileDao.findChildCSPDetailByParentDetailId(parentDetailId);

        for (BigInteger profileDetailId : childsDetailsIdList) {
            LtlPricingThirdPartyInfoEntity clone = cloneEntity(sourceEntity, profileDetailId.longValue());
            dao.saveOrUpdate(clone);
        }
    }

    private LtlPricingThirdPartyInfoEntity cloneEntity(LtlPricingThirdPartyInfoEntity sourceEntity, Long profileDetailId) {
        LtlPricingThirdPartyInfoEntity clonedEntity = new LtlPricingThirdPartyInfoEntity(sourceEntity);
        clonedEntity.setPricProfDetailId(profileDetailId);

        return clonedEntity;
    }
}
