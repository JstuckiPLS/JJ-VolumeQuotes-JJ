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
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.LtlZonesDao;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.LtlZoneGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlZoneGeoServicesEntity;
import com.pls.ltlrating.domain.LtlZonesEntity;
import com.pls.ltlrating.domain.bo.ZonesListItemVO;
import com.pls.ltlrating.service.LtlZonesSerivce;
import com.pls.ltlrating.service.validation.LtlZonesValidator;

/**
 * Implementation of {@link LtlZonesSerivce}.
 *
 * @author Artem Arapov
 *
 */
@Service
@Transactional(readOnly = true)
public class LtlZonesServiceImpl implements LtlZonesSerivce {

    @Autowired
    private LtlZonesDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Resource(type = LtlZonesValidator.class)
    private Validator<LtlZonesEntity> validator;

    private static final String BLANKET = "BLANKET";

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public LtlZonesEntity saveLTLZone(LtlZonesEntity entity) throws ValidationException {
        validator.validate(entity);

        processZoneEntity(entity);
        return (entity.getId() == null) ? addZone(entity) : updateZone(entity);
    }

    @Override
    public LtlZonesEntity getLTLZoneById(Long id) {
        LtlZonesEntity entity = dao.find(id);
        for (LtlZoneGeoServicesEntity item : entity.getLtlZoneGeoServicesEntities()) {
            item.setLocation(StringUtils.join(item.getLtlZoneGeoServiceDetails(), ','));
        }
        return entity;
    }

    @Override
    public List<LtlZonesEntity> getAllLTLZonesByProfileDetailId(Long profileDetailId) {
        return dao.findByProfileDetailId(profileDetailId);
    }

    @Override
    public List<ZonesListItemVO> getInactiveLTLZonesByProfileDetailId(Long profileDetailId) {
        return dao.findByStatusAndProfileId(Status.INACTIVE, profileDetailId);
    }

    @Override
    public List<ZonesListItemVO> getActiveLTLZonesByProfileDetailId(Long profileDetailId) {
        return dao.findByStatusAndProfileId(Status.ACTIVE, profileDetailId);
    }

    @Override
    public List<LtlZonesEntity> getActiveZoneEntitiesForProfile(Long profileDetailId) {
        return dao.findActiveForProfile(profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<ZonesListItemVO> inactivateLTLZones(List<Long> ids, Long profileDetailId) {
        dao.updateStatus(ids, Status.INACTIVE, SecurityUtils.getCurrentPersonId());
        updateStatusInChildsListCSPs(ids, Status.INACTIVE, profileDetailId);

        return dao.findByStatusAndProfileId(Status.ACTIVE, profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<ZonesListItemVO> reactivateLTLZones(List<Long> ids, Long profileDetailId) {
        dao.updateStatus(ids, Status.ACTIVE, SecurityUtils.getCurrentPersonId());
        updateStatusInChildsListCSPs(ids, Status.ACTIVE, profileDetailId);

        return dao.findByStatusAndProfileId(Status.INACTIVE, profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void cloneLTLZones(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyChilds) {
        dao.inactivateByProfileDetailId(copyToProfileDetailId);

        boolean cloneChilds = shouldCopyChilds && profileDao.findPricingTypeByProfileDetailId(copyToProfileDetailId).equalsIgnoreCase(BLANKET);

        if (cloneChilds) {
            dao.inactivateCSPByProfileDetailId(copyToProfileDetailId, SecurityUtils.getCurrentPersonId());
        }

        cloneZones(copyFromProfileDetailId, copyToProfileDetailId, cloneChilds);
    }

    private LtlZonesEntity addZone(LtlZonesEntity entity) {
        dao.saveOrUpdate(entity);
        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricProfDetailId()).equalsIgnoreCase(BLANKET)) {
            addEntityToChildCSPs(entity);
        }

        return entity;
    }

    private void addEntityToChildCSPs(LtlZonesEntity entity) {
        List<LtlPricingProfileEntity> childProfilesList = profileDao.findChildCSPByProfileDetailId(entity
                .getLtlPricProfDetailId());

        for (LtlPricingProfileEntity profile : childProfilesList) {
            addEntityToChildCSP(entity, profile);
        }
    }

    private void addEntityToChildCSP(LtlZonesEntity source, LtlPricingProfileEntity childProfile) {
        for (LtlPricingProfileDetailsEntity detail : childProfile.getProfileDetails()) {
            addEntityToChildCSPDetail(source, detail.getId());
        }
    }

    private void addEntityToChildCSPDetail(LtlZonesEntity source, Long profileDetailId) {
        LtlZonesEntity entity = new LtlZonesEntity(source);
        entity.setLtlPricProfDetailId(profileDetailId);

        dao.saveOrUpdate(entity);
    }

    private LtlZonesEntity updateZone(LtlZonesEntity entity) {
        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricProfDetailId()).equalsIgnoreCase(BLANKET)) {
            updateEntityInChildCSPs(entity);
        }

        return dao.merge(entity);
    }

    private void updateEntityInChildCSPs(LtlZonesEntity entity) {
        List<LtlZonesEntity> childs = dao.findAllCspChildsCopyedFrom(entity.getId());

        for (LtlZonesEntity child : childs) {
            LtlZonesEntity updatedChild = modifyChildCopyOfEntity(entity, child);

            dao.saveOrUpdate(updatedChild);
        }
    }

    private LtlZonesEntity modifyChildCopyOfEntity(LtlZonesEntity source, LtlZonesEntity child) {
        child.setName(source.getName());
        child.setStatus(source.getStatus());
        child.setCopiedFrom(source.getId());

        child.getLtlZoneGeoServicesEntities().clear();
        for (LtlZoneGeoServicesEntity item : source.getLtlZoneGeoServicesEntities()) {
            child.getLtlZoneGeoServicesEntities().add(new LtlZoneGeoServicesEntity(item));
        }

        return child;
    }

    private void updateStatusInChildsListCSPs(List<Long> ownersListId, Status status, Long profileDetailId) {
        if (profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(BLANKET)) {
            dao.updateStatusInCSPByCopiedFrom(ownersListId, status, SecurityUtils.getCurrentPersonId());
        }
    }

    private void cloneZones(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyToChildCSP) {
        List<LtlZonesEntity> sourceList = dao.findActiveForProfile(copyFromProfileDetailId);

        List<LtlZonesEntity> clonedList = createCloneList(sourceList, copyToProfileDetailId);
        dao.saveOrUpdateBatch(clonedList);

        if (shouldCopyToChildCSP) {
            cloneToChildCSP(copyToProfileDetailId, clonedList);
        }
    }

    private List<LtlZonesEntity> createCloneList(List<LtlZonesEntity> listToClone, Long profileDetailIdToCopy) {
        List<LtlZonesEntity> result = new ArrayList<LtlZonesEntity>();
        for (LtlZonesEntity item : listToClone) {
            LtlZonesEntity clone = new LtlZonesEntity(item);
            clone.setLtlPricProfDetailId(profileDetailIdToCopy);
            result.add(clone);
        }

        return result;
    }

    private void cloneToChildCSP(Long parentDetailId, List<LtlZonesEntity> listToClone) {
        List<BigInteger> detailIdList = profileDao.findChildCSPDetailByParentDetailId(parentDetailId);

        for (BigInteger detailId : detailIdList) {
            List<LtlZonesEntity> clonedList = createCloneList(listToClone, detailId.longValue());
            dao.saveOrUpdateBatch(clonedList);
        }
    }

    private void processZoneEntity(LtlZonesEntity zone) {
        if (zone.getLtlZoneGeoServicesEntities() != null) {
            for (LtlZoneGeoServicesEntity geoServices : zone.getLtlZoneGeoServicesEntities()) {
                geoServices.setLtlZoneGeoServiceDetails(getGeoServiceDetails(geoServices.getId(), geoServices.getLocation()));
            }
        }
    }

    private List<LtlZoneGeoServiceDetailsEntity> getGeoServiceDetails(Long ltlZoneGeoServiceId, String origin) {
        List<LtlZoneGeoServiceDetailsEntity> geoServiceDetails = new ArrayList<LtlZoneGeoServiceDetailsEntity>();

        String[] geoCodes = StringUtils.split(origin, ',');
        for (String geoCode : geoCodes) {
            Pair<Integer, String> ltlZoneGeoPair = GeoHelper.getGeoServType(geoCode);
            geoServiceDetails.add(new LtlZoneGeoServiceDetailsEntity(ltlZoneGeoServiceId, geoCode.trim(), ltlZoneGeoPair
                    .getLeft().intValue(), ltlZoneGeoPair.getRight()));
        }

        return geoServiceDetails;
    }
}
