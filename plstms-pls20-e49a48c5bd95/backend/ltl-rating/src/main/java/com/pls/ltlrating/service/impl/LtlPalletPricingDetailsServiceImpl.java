package com.pls.ltlrating.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPalletPricingDetailsDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.service.LtlPalletPricingDetailsService;
import com.pls.ltlrating.service.validation.LtlPalletPricingDetailsValidator;

/**
 * Implementation of {@link LtlPalletPricingDetailsService}.
 *
 * @author Artem Arapov
 *
 */
@Service
@Transactional
public class LtlPalletPricingDetailsServiceImpl implements LtlPalletPricingDetailsService {

    private static final String BLANKET = "BLANKET";

    @Autowired
    private LtlPalletPricingDetailsDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Resource(type = LtlPalletPricingDetailsValidator.class)
    private Validator<LtlPalletPricingDetailsEntity> validator;

    @Override
    public void saveList(List<LtlPalletPricingDetailsEntity> list, Long profileDetailId) throws ValidationException {
        boolean needChildCopy = needToMakeChildCopyToCSP(profileDetailId);

        if (!needChildCopy) {
            saveEntities(list);
        } else {
            saveEntitiesAndCopyToCSP(list, profileDetailId);
        }
    }

    @Override
    public List<LtlPalletPricingDetailsEntity> findActiveAndEffective(Long detailId) {
        return dao.findActiveAndEffective(detailId);
    }

    @Override
    public List<LtlPalletPricingDetailsEntity> findInactive(Long detailId) {
        return dao.findByStatusAndDetailId(detailId, Status.INACTIVE);
    }

    @Override
    public void activate(Long id, Long profileDetailId) {
        Long personId = SecurityUtils.getCurrentPersonId();
        dao.updateStatus(id, Status.ACTIVE, personId);
        if (needToMakeChildCopyToCSP(profileDetailId)) {
            dao.updateStatusInCSPByCopiedFrom(id, Status.ACTIVE, personId);
        }
    }

    @Override
    public void inactivate(Long id, Long profileDetailId) {
        Long personId = SecurityUtils.getCurrentPersonId();
        dao.updateStatus(id, Status.INACTIVE, personId);
        if (needToMakeChildCopyToCSP(profileDetailId)) {
            dao.updateStatusInCSPByCopiedFrom(id, Status.INACTIVE, personId);
        }
    }

    @Override
    public void copyFrom(Long copyFromProfileId, Long copyToProfileId) {
        dao.inactivateByDetailId(copyToProfileId, SecurityUtils.getCurrentPersonId());
        dao.clone(copyFromProfileId, copyToProfileId);
    }

    @Override
    public boolean areZonesMissing(Long copyFromProfileId, Long copyToProfileId) {
        return dao.areZonesMissing(copyFromProfileId, copyToProfileId);
    }

    private boolean needToMakeChildCopyToCSP(Long profileDetailId) {
        return profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(BLANKET);
    }

    private List<LtlPricingProfileEntity> findChildProfilesList(Long profileDetailId) {
        return profileDao.findChildCSPByProfileDetailId(profileDetailId);
    }

    private void saveEntities(List<LtlPalletPricingDetailsEntity> list) throws ValidationException {
        for (LtlPalletPricingDetailsEntity item : list) {
            validator.validate(item);
            dao.saveOrUpdate(item);
        }
    }

    private void saveEntitiesAndCopyToCSP(List<LtlPalletPricingDetailsEntity> list, Long profileDetailId) throws ValidationException {
        List<LtlPricingProfileEntity> childCSPs = findChildProfilesList(profileDetailId);
        for (LtlPalletPricingDetailsEntity item : list) {
            validator.validate(item);

            saveEntityAndCopyToChildCSP(item, childCSPs);
        }
    }

    private void saveEntityAndCopyToChildCSP(LtlPalletPricingDetailsEntity entity, List<LtlPricingProfileEntity> childCSPs) {
        if (entity.getId() == null) {
            addNewPalletPricingEntity(entity, childCSPs);
        } else {
            updatePalletPricingEntity(entity);
        }
    }

    private void addNewPalletPricingEntity(LtlPalletPricingDetailsEntity entity, List<LtlPricingProfileEntity> childCSPs) {
        dao.saveOrUpdate(entity);
        addEntityToChildCSPs(entity, childCSPs);
    }

    private void addEntityToChildCSPs(LtlPalletPricingDetailsEntity entity, List<LtlPricingProfileEntity> childCSPs) {
        for (LtlPricingProfileEntity profile : childCSPs) {
            addEntityToChildCSP(entity, profile);
        }
    }

    private void addEntityToChildCSP(LtlPalletPricingDetailsEntity source, LtlPricingProfileEntity childProfile) {
        for (LtlPricingProfileDetailsEntity detail : childProfile.getProfileDetails()) {
            addEntityToChildCSPDetail(source, detail.getId());
        }
    }

    private void addEntityToChildCSPDetail(LtlPalletPricingDetailsEntity source, Long profileDetailId) {
        LtlPalletPricingDetailsEntity entity = createChildCopyOfPalletEntity(source, new LtlPalletPricingDetailsEntity());
        entity.setProfileDetailId(profileDetailId);

        dao.saveOrUpdate(entity);
    }

    private void updatePalletPricingEntity(LtlPalletPricingDetailsEntity entity) {
        updateEntityInChildCSPs(entity);

        dao.saveOrUpdate(entity);
    }

    private void updateEntityInChildCSPs(LtlPalletPricingDetailsEntity entity) {
        List<LtlPalletPricingDetailsEntity> childs = dao.findAllCspChildsCopyedFrom(entity.getId());

        for (LtlPalletPricingDetailsEntity child : childs) {
            LtlPalletPricingDetailsEntity updatedChild = createChildCopyOfPalletEntity(entity, child);

            dao.saveOrUpdate(updatedChild);
        }
    }

    private LtlPalletPricingDetailsEntity createChildCopyOfPalletEntity(LtlPalletPricingDetailsEntity source, LtlPalletPricingDetailsEntity child) {
        child.setCopiedFrom(source.getId());
        child.setCostApplMaxWt(source.getCostApplMaxWt());
        child.setCostApplMinWt(source.getCostApplMinWt());
        child.setCostApplWtUom(source.getCostApplWtUom());
        child.setCostType(source.getCostType());
        child.setEffDate(source.getEffDate());
        child.setExpDate(source.getExpDate());
        child.setMaxQuantity(source.getMaxQuantity());
        child.setMinQuantity(source.getMinQuantity());
        child.setServiceType(source.getServiceType());
        child.setStatus(source.getStatus());
        child.setTransitTime(source.getTransitTime());
        child.setUnitCost(source.getUnitCost());
        child.setZoneFrom(source.getZoneFrom());
        child.setZoneTo(source.getZoneTo());
        child.setMovementType(source.getMovementType());

        return child;
    }
}
