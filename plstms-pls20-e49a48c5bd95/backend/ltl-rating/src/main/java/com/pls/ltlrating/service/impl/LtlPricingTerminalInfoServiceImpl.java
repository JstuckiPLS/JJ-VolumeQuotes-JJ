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
import com.pls.ltlrating.dao.LtlPricingTerminalInfoDao;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;
import com.pls.ltlrating.service.LtlPricingTerminalInfoService;
import com.pls.ltlrating.service.validation.LtlPricingTerminalInfoValidator;

/**
 * Implementation of {@link LtlPricingTerminalInfoService}.
 *
 * @author Artem Arapov
 *
 */

@Service
@Transactional
public class LtlPricingTerminalInfoServiceImpl implements LtlPricingTerminalInfoService {

    @Autowired
    private LtlPricingTerminalInfoDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Resource(type = LtlPricingTerminalInfoValidator.class)
    private Validator<LtlPricingTerminalInfoEntity> validator;

    private static final String BLANKET = "BLANKET";

    @Override
    public LtlPricingTerminalInfoEntity saveCarrierTerminalInfo(LtlPricingTerminalInfoEntity entity)
            throws ValidationException {
        validator.validate(entity);

        boolean isNewEntity = entity.getId() == null;
        LtlPricingTerminalInfoEntity savedEntity = dao.saveOrUpdate(entity);

        if (profileDao.findPricingTypeByProfileDetailId(entity.getPriceProfileId()).equalsIgnoreCase(BLANKET)) {
            modifyChildCSPEntity(entity, isNewEntity);
        }

        return savedEntity;
    }

    @Override
    public LtlPricingTerminalInfoEntity getCarrierTerminalInfoById(Long id) {
        return dao.find(id);
    }

    @Override
    public LtlPricingTerminalInfoEntity getActiveCarrierTerminalInfoByProfileDetailId(Long profileDetailId) {
        return dao.findActiveByProfileDetailId(profileDetailId);
    }

    @Override
    public void copyFrom(Long copyFromProfileId, Long copyToProfileId, boolean shouldCopyChilds) {
        dao.updateStatus(copyToProfileId, Status.INACTIVE, SecurityUtils.getCurrentPersonId());

        boolean copyChilds = shouldCopyChilds && profileDao.findPricingTypeByProfileDetailId(copyToProfileId).equalsIgnoreCase(BLANKET);

        if (copyChilds) {
            dao.inactivateCSPByProfileDetailId(copyToProfileId, SecurityUtils.getCurrentPersonId());
        }

        cloneBetweenProfileDetails(copyFromProfileId, copyToProfileId, copyChilds);
    }

    private void modifyChildCSPEntity(LtlPricingTerminalInfoEntity entity, boolean isNewEntity) {
        if (isNewEntity) {
            addEntityToChildCSPs(entity);
        } else {
            updateEntityInChildCSPs(entity);
        }
    }

    private void addEntityToChildCSPs(LtlPricingTerminalInfoEntity entity) {
        List<LtlPricingProfileEntity> childProfilesList = profileDao.findChildCSPByProfileDetailId(entity.getPriceProfileId());

        for (LtlPricingProfileEntity profile : childProfilesList) {
            addEntityToChildCSP(entity, profile);
        }
    }

    private void addEntityToChildCSP(LtlPricingTerminalInfoEntity source, LtlPricingProfileEntity childProfile) {
        for (LtlPricingProfileDetailsEntity detail : childProfile.getProfileDetails()) {
            addEntityToChildCSPDetail(source, detail.getId());
        }
    }

    private void addEntityToChildCSPDetail(LtlPricingTerminalInfoEntity source, Long profileDetailId) {
        LtlPricingTerminalInfoEntity entity = new LtlPricingTerminalInfoEntity(source);
        entity.setPriceProfileId(profileDetailId);

        dao.saveOrUpdate(entity);
    }

    private void updateEntityInChildCSPs(LtlPricingTerminalInfoEntity entity) {
        List<LtlPricingTerminalInfoEntity> childs = dao.findAllCspChildsCopyedFrom(entity.getId());

        for (LtlPricingTerminalInfoEntity child : childs) {
            LtlPricingTerminalInfoEntity updatedChild = modifyChildCopyOfEntity(entity, child);

            dao.saveOrUpdate(updatedChild);
        }
    }

    private LtlPricingTerminalInfoEntity modifyChildCopyOfEntity(LtlPricingTerminalInfoEntity source, LtlPricingTerminalInfoEntity child) {
        child.setContactName(source.getContactName());
        child.setAccountNum(source.getAccountNum());
        child.setAddress(source.getAddress());
        child.setEmail(source.getEmail());
        child.setFax(source.getFax());
        child.setPhone(source.getPhone());
        child.setStatus(source.getStatus());
        child.setTransiteTime(source.getTransiteTime());
        child.setVisible(source.getVisible());
        child.setTerminal(source.getTerminal());
        child.setCopiedFrom(source.getId());

        return child;
    }

    private void cloneBetweenProfileDetails(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyToChildCSP) {
        LtlPricingTerminalInfoEntity sourceEntity = dao.findActiveByProfileDetailId(copyFromProfileDetailId);

        if (sourceEntity == null) {
            return;
        }

        LtlPricingTerminalInfoEntity clonedEntity = cloneEntity(sourceEntity, copyToProfileDetailId);
        dao.saveOrUpdate(clonedEntity);

        if (shouldCopyToChildCSP) {
            cloneToChildCSP(copyToProfileDetailId, clonedEntity);
        }
    }

    private void cloneToChildCSP(Long parentDetailId, LtlPricingTerminalInfoEntity sourceEntity) {
        List<BigInteger> childsDetailsIdList = profileDao.findChildCSPDetailByParentDetailId(parentDetailId);

        for (BigInteger profileDetailId : childsDetailsIdList) {
            LtlPricingTerminalInfoEntity clone = cloneEntity(sourceEntity, profileDetailId.longValue());
            dao.saveOrUpdate(clone);
        }
    }

    private LtlPricingTerminalInfoEntity cloneEntity(LtlPricingTerminalInfoEntity sourceEntity, Long profileDetailId) {
        LtlPricingTerminalInfoEntity clonedEntity = new LtlPricingTerminalInfoEntity(sourceEntity);
        clonedEntity.setPriceProfileId(profileDetailId);

        return clonedEntity;
    }
}
