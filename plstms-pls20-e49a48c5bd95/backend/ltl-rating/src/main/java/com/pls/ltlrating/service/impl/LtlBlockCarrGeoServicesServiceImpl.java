package com.pls.ltlrating.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlBlockCarrGeoServicesDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;
import com.pls.ltlrating.domain.LtlBlockCarrierGeoServDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.BlockCarrierListItemVO;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.service.LtlBlockCarrGeoServicesService;
import com.pls.ltlrating.service.validation.LtlBlockCarrGeoServicesValidator;

/**
 * Implementation of {@link LtlBlockCarrGeoServicesService}.
 *
 * @author Artem Arapov
 *
 */
@Service
@Transactional(readOnly = true)
public class LtlBlockCarrGeoServicesServiceImpl implements LtlBlockCarrGeoServicesService {

    @Autowired
    private LtlBlockCarrGeoServicesDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Resource(type = LtlBlockCarrGeoServicesValidator.class)
    private Validator<LtlBlockCarrGeoServicesEntity> validator;

    private static final String BLANKET = "BLANKET";

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlBlockCarrGeoServicesEntity saveBlockedCarrierGeoService(LtlBlockCarrGeoServicesEntity entity)
            throws ValidationException {
        validator.validate(entity);

        boolean isNewEntity = entity.getId() == null;
        entity.setLtlBkCarrOriginGeoServiceDetails(getGeoServiceDetails(entity, entity.getOrigin(), GeoType.ORIGIN));
        entity.setLtlBkCarrDestGeoServiceDetails(getGeoServiceDetails(entity, entity.getDestination(),
                        GeoType.DESTINATION));

        LtlBlockCarrGeoServicesEntity mergedEntity = isNewEntity ? dao.saveOrUpdate(entity) : dao.merge(entity);

        if (profileDao.findPricingTypeByProfileDetailId(mergedEntity.getProfileId()).equalsIgnoreCase(BLANKET)) {
            modifyChildCSPEntity(mergedEntity, isNewEntity);
        }

        return mergedEntity;
    }

    @Override
    public LtlBlockCarrGeoServicesEntity getBlockedCarrierGeoServiceById(Long id) {
        LtlBlockCarrGeoServicesEntity entity = dao.find(id);
        entity.setOrigin(StringUtils.join(entity.getLtlBkCarrOriginGeoServiceDetails(), ','));
        entity.setDestination(StringUtils.join(entity.getLtlBkCarrDestGeoServiceDetails(), ','));

        return entity;
    }

    @Override
    public List<BlockCarrierListItemVO> getActiveBlockedCarrGeoServByProfileDetailId(Long profileDetailId) {
        return dao.findByStatusAndProfileId(Status.ACTIVE, profileDetailId);
    }

    @Override
    public List<BlockCarrierListItemVO> getInactiveBlockedCarrGeoServByProfileDetailId(Long profileDetailId) {
        return dao.findByStatusAndProfileId(Status.INACTIVE, profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<BlockCarrierListItemVO> inactivateBlockedCarrierGeoServices(List<Long> ids, Long profileDetailId) {
        dao.updateStatus(ids, Status.INACTIVE, SecurityUtils.getCurrentPersonId());
        updateStatusInChildCSPs(ids, Status.INACTIVE, profileDetailId);

        return getActiveBlockedCarrGeoServByProfileDetailId(profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<BlockCarrierListItemVO> reactivateBlockedCarrierGeoServices(List<Long> ids, Long profileDetailId) {
        dao.updateStatus(ids, Status.ACTIVE, SecurityUtils.getCurrentPersonId());
        updateStatusInChildCSPs(ids, Status.ACTIVE, profileDetailId);

        return getInactiveBlockedCarrGeoServByProfileDetailId(profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cloneBlockedCarrierGeoServices(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyToCSP) {
        dao.inactivateByProfileDetailId(copyToProfileDetailId);

        List<LtlBlockCarrGeoServicesEntity> sourceList = dao.findActiveByProfileId(Status.ACTIVE, copyFromProfileDetailId);
        List<LtlBlockCarrGeoServicesEntity> clonedList = cloneList(sourceList, copyToProfileDetailId);

        dao.saveOrUpdateBatch(clonedList);

        if (shouldCopyToCSP && profileDao.findPricingTypeByProfileDetailId(copyToProfileDetailId).equalsIgnoreCase(BLANKET)) {
            copyToCSPProfiles(clonedList, copyToProfileDetailId);
        }
    }

    private List<LtlBlockCarrGeoServicesEntity> cloneList(List<LtlBlockCarrGeoServicesEntity> sourceList, Long copyToProfileDetailId) {
        List<LtlBlockCarrGeoServicesEntity> clonedList = new ArrayList<LtlBlockCarrGeoServicesEntity>();
        for (LtlBlockCarrGeoServicesEntity source : sourceList) {
            LtlBlockCarrGeoServicesEntity clone = new LtlBlockCarrGeoServicesEntity(source);
            clone.setProfileId(copyToProfileDetailId);
            clonedList.add(clone);
        }

        return clonedList;
    }

    private void copyToCSPProfiles(List<LtlBlockCarrGeoServicesEntity> sourceList, Long parentProfileDetailId) {
        dao.inactivateCSPByProfileDetailId(parentProfileDetailId, SecurityUtils.getCurrentPersonId());
        List<BigInteger> childDetailsIds = profileDao.findChildCSPDetailByParentDetailId(parentProfileDetailId);
        for (BigInteger detailId : childDetailsIds) {
            List<LtlBlockCarrGeoServicesEntity> clonedList = cloneList(sourceList, detailId.longValue());
            dao.saveOrUpdateBatch(clonedList);
        }
    }

    private void modifyChildCSPEntity(LtlBlockCarrGeoServicesEntity entity, boolean isNewEntity) {
        if (isNewEntity) {
            addEntityToChildCSPs(entity);
        } else {
            updateEntityInChildCSPs(entity);
        }
    }

    private void addEntityToChildCSPs(LtlBlockCarrGeoServicesEntity entity) {
        List<LtlPricingProfileEntity> childProfilesList = profileDao.findChildCSPByProfileDetailId(entity.getProfileId());

        for (LtlPricingProfileEntity profile : childProfilesList) {
            addEntityToChildCSP(entity, profile);
        }
    }

    private void addEntityToChildCSP(LtlBlockCarrGeoServicesEntity source, LtlPricingProfileEntity childProfile) {
        for (LtlPricingProfileDetailsEntity detail : childProfile.getProfileDetails()) {
            addEntityToChildCSPDetail(source, detail.getId());
        }
    }

    private void addEntityToChildCSPDetail(LtlBlockCarrGeoServicesEntity source, Long profileDetailId) {
        LtlBlockCarrGeoServicesEntity entity = new LtlBlockCarrGeoServicesEntity(source);
        entity.setProfileId(profileDetailId);

        dao.saveOrUpdate(entity);
    }

    private void updateEntityInChildCSPs(LtlBlockCarrGeoServicesEntity entity) {
        List<LtlBlockCarrGeoServicesEntity> childs = dao.findAllCspChildsCopyedFrom(entity.getId());

        for (LtlBlockCarrGeoServicesEntity child : childs) {
            LtlBlockCarrGeoServicesEntity updatedChild = createChildCopyOfBlockCarrierEntity(entity, child);
            dao.saveOrUpdate(updatedChild);
        }
    }

    private LtlBlockCarrGeoServicesEntity createChildCopyOfBlockCarrierEntity(LtlBlockCarrGeoServicesEntity source,
            LtlBlockCarrGeoServicesEntity child) {
        child.setStatus(source.getStatus());
        child.setNotes(source.getNotes());
        child.getLtlBkCarrOriginGeoServiceDetails().clear();
        child.getLtlBkCarrDestGeoServiceDetails().clear();
        for (LtlBlockCarrierGeoServDetailsEntity item : source.getLtlBkCarrOriginGeoServiceDetails()) {
            child.getLtlBkCarrOriginGeoServiceDetails().add(new LtlBlockCarrierGeoServDetailsEntity(item, child));
        }

        for (LtlBlockCarrierGeoServDetailsEntity item : source.getLtlBkCarrDestGeoServiceDetails()) {
            child.getLtlBkCarrDestGeoServiceDetails().add(new LtlBlockCarrierGeoServDetailsEntity(item, child));
        }

        return child;
    }

    private void updateStatusInChildCSPs(List<Long> ownerIds, Status status, Long priceDetailId) {
        if (profileDao.findPricingTypeByProfileDetailId(priceDetailId).equalsIgnoreCase(BLANKET)) {
            dao.updateStatusInCSPByCopiedFrom(ownerIds, status, SecurityUtils.getCurrentPersonId());
        }
    }

    private List<LtlBlockCarrierGeoServDetailsEntity> getGeoServiceDetails(LtlBlockCarrGeoServicesEntity geoService, String geoValue,
            GeoType geoType) {
        List<LtlBlockCarrierGeoServDetailsEntity> geoServiceDetails = new ArrayList<LtlBlockCarrierGeoServDetailsEntity>();

        if (StringUtils.isNotEmpty(geoValue)) {
            String[] geoCodes = StringUtils.split(geoValue, ',');
            for (String geoCode : geoCodes) {
                Pair<Integer, String> geoPair = GeoHelper.getGeoServType(geoCode);
                geoServiceDetails.add(new LtlBlockCarrierGeoServDetailsEntity(geoService, geoCode.trim(), geoType,
                        geoPair.getLeft().intValue(), geoPair.getRight()));
            }
        }

        return geoServiceDetails;
    }
}
