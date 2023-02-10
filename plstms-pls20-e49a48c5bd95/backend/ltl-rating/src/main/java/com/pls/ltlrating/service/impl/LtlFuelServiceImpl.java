package com.pls.ltlrating.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.DotRegionDao;
import com.pls.ltlrating.dao.DotRegionFuelDao;
import com.pls.ltlrating.dao.LtlFuelDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.DotRegionEntity;
import com.pls.ltlrating.domain.DotRegionFuelEntity;
import com.pls.ltlrating.domain.LtlFuelEntity;
import com.pls.ltlrating.domain.LtlFuelGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlFuelGeoServicesEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.FuelListItemVO;
import com.pls.ltlrating.domain.bo.fuel.FuelAPIResponse;
import com.pls.ltlrating.domain.bo.fuel.FuelRegionSeries;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.service.LtlFuelService;
import com.sun.syndication.io.FeedException;
/**
 * Implementation for {@link LtlFuelService}.
 *
 * @author Stas Norochevskiy
 *
 */
@Service
@Transactional(readOnly = true)
public class LtlFuelServiceImpl implements LtlFuelService {

    private static final String BLANKET = "BLANKET";

    @Value("${dotAPIUrl}")
    private String dotAPIUrl;

    @Autowired
    private LtlFuelDao dao;

    @Autowired
    private DotRegionFuelDao regionFuelDao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Autowired
    private DotRegionDao regionsDao;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlFuelEntity saveFuelTrigger(LtlFuelEntity entity) {
        processFuelEntity(entity);
        return (entity.getId() == null) ? addFuelEntity(entity) : updateFuelEntity(entity);
    }

    @Override
    public LtlFuelEntity getFuelTriggerById(Long id) {
        LtlFuelEntity entity = dao.find(id);

        for (LtlFuelGeoServicesEntity item : entity.getLtlFuelGeoServices()) {
            item.setOrigin(StringUtils.join(item.getLtlFuelGeoServiceDetails(), ','));
        }
        return entity;
    }

    @Override
    public List<LtlFuelEntity> getAllFuelTriggersByProfileDetailId(Long profileDetailId) {
        return dao.getAllFuelTriggersByProfileDetailId(profileDetailId);
    }

    @Override
    public List<FuelListItemVO> getActiveFuelTriggersByProfileDetailId(Long profileDetailId) {
        return dao.getActiveAndEffectiveByProfileDetailId(profileDetailId);
    }

    @Override
    public List<FuelListItemVO> getInactiveFuelTriggersByProfileDetailId(Long profileDetailId) {
        return dao.getAllFuelTriggersByProfileDetailIdAndStatus(profileDetailId, Status.INACTIVE);
    }

    @Override
    public List<FuelListItemVO> getExpiredFuelTriggersByProfileDetailId(Long profileDetailId) {
        return dao.getFuelTriggersByProfileDetailWithExpireDateLessThan(profileDetailId, new Date());
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<FuelListItemVO> inactivateFuelTriggers(List<Long> ids, Long profileDetailId, Boolean isActiveList) {
        updateStatus(ids, Status.INACTIVE);
        updateStatusInChildsListCSPs(ids, Status.INACTIVE, profileDetailId);

        return isActiveList
                ? this.getActiveFuelTriggersByProfileDetailId(profileDetailId)
                : this.getExpiredFuelTriggersByProfileDetailId(profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<FuelListItemVO> reactivateFuelTriggers(List<Long> ids, Long profileDetailId) {
        updateStatus(ids, Status.ACTIVE);
        updateStatusInChildsListCSPs(ids, Status.ACTIVE, profileDetailId);

        return this.getInactiveFuelTriggersByProfileDetailId(profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<FuelListItemVO> expireFuelTriggers(List<Long> ids, Long profileDetailId) {
        dao.expireByListOfIds(ids, SecurityUtils.getCurrentPersonId());
        if (profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(BLANKET)) {
            dao.expirateCSPByCopiedFrom(ids, SecurityUtils.getCurrentPersonId());
        }

        return getActiveFuelTriggersByProfileDetailId(profileDetailId);
    }

    @Override
    public List<DotRegionEntity> getDotRegions() {
        return regionsDao.getAll();
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateStatus(List<Long> fuelTriggerIds, Status newStatus) {
        if (fuelTriggerIds != null && !fuelTriggerIds.isEmpty()) {
            dao.updateFuelStatus(fuelTriggerIds, newStatus);
        }
    }

    @Override
    public void receiveRegionsFuelRates() throws IllegalArgumentException, FeedException, IOException {
        Map<String, String> freshDOT = retrieveRegionsFuelRatesFromDOT();
        regionFuelDao.expirateRates();
        List<DotRegionEntity> regions = regionsDao.getAll();
        for (DotRegionEntity region : regions) {
            DotRegionFuelEntity fuel = new DotRegionFuelEntity();
            fuel.setDotRegion(region);
            fuel.setFuelCharge(new BigDecimal(freshDOT.get(region.getDotRegionName())).setScale(3, RoundingMode.HALF_UP));
            fuel.setEffectiveDate(getFirstDayOfTheWeek(Calendar.getInstance()));
            fuel.setStatus(Status.ACTIVE);

            dao.saveDotRegionFuelEntity(fuel);
        }
    }

    @Override
    public Map<String, String> retrieveRegionsFuelRatesFromDOT() {
        RestTemplate restTemplate = new RestTemplate();
        FuelAPIResponse response = restTemplate.getForObject(dotAPIUrl, FuelAPIResponse.class);

        Map<String, String> regionToSurcharge = new HashMap<String, String>();

        for (FuelRegionSeries series : response.getSeries()) {
            regionToSurcharge.put(series.getSeriesId(), series.getData().get(0).get(1));
        }
        return regionToSurcharge;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<DotRegionFuelEntity> saveRegionFuel(List<DotRegionFuelEntity> regionFuelEntities) {
        return regionFuelDao.saveAll(regionFuelEntities);
    }

    @Override
    public List<DotRegionFuelEntity> getRegionFuelBySelectedCriteria(DateRangeQueryBO dateRange, List<Long> regionIds) {
        return regionFuelDao.getByDateRangeAndIds(dateRange.getFromDate(), dateRange.getToDate(), regionIds);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void copyFrom(Long copyFromProfileId, Long copyToProfileId, boolean shouldCopyToCSP) {
        dao.updateStatusToInactiveByProfileId(copyToProfileId, SecurityUtils.getCurrentPersonId());

        List<LtlFuelEntity> sourceList = dao.getActiveAndEffectiveForProfile(copyFromProfileId);
        List<LtlFuelEntity> clonedList = cloneList(sourceList, copyToProfileId);

        dao.saveOrUpdateBatch(clonedList);

        if (shouldCopyToCSP && profileDao.findPricingTypeByProfileDetailId(copyToProfileId).equalsIgnoreCase(BLANKET)) {
            copyToCSPProfiles(clonedList, copyToProfileId);
        }
    }

    @Override
    public List<DotRegionFuelEntity> getRegionsRates() {
        return regionFuelDao.getActiveRegionRates();
    }

    private List<LtlFuelEntity> cloneList(List<LtlFuelEntity> source, Long profileDetailId) {
        List<LtlFuelEntity> clonedList = new ArrayList<LtlFuelEntity>(source.size());

        for (LtlFuelEntity item : source) {
            LtlFuelEntity clone = new LtlFuelEntity(item);
            clone.setLtlPricingProfileId(profileDetailId);
            clonedList.add(clone);
        }

        return clonedList;
    }

    private LtlFuelEntity addFuelEntity(LtlFuelEntity entity) {
        LtlFuelEntity savedEntity = dao.saveOrUpdate(entity);

        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricingProfileId()).equalsIgnoreCase(BLANKET)) {
            addEntityToChildCSPs(savedEntity);
        }

        return savedEntity;
    }

    private void addEntityToChildCSPs(LtlFuelEntity entity) {
        List<LtlPricingProfileEntity> childProfilesList = profileDao.findChildCSPByProfileDetailId(entity.getLtlPricingProfileId());

        for (LtlPricingProfileEntity profile : childProfilesList) {
            addEntityToChildCSP(entity, profile);
        }
    }

    private void addEntityToChildCSP(LtlFuelEntity source, LtlPricingProfileEntity childProfile) {
        for (LtlPricingProfileDetailsEntity detail : childProfile.getProfileDetails()) {
            addEntityToChildCSPDetail(source, detail.getId());
        }
    }

    private void addEntityToChildCSPDetail(LtlFuelEntity source, Long profileDetailId) {
        LtlFuelEntity entity = createChildCopyOfFuelEntity(source, new LtlFuelEntity());
        entity.setLtlPricingProfileId(profileDetailId);

        dao.saveOrUpdate(entity);
    }

    private LtlFuelEntity createChildCopyOfFuelEntity(LtlFuelEntity source, LtlFuelEntity child) {
        child.setDotRegion(source.getDotRegion());
        child.setEffectiveDate(source.getEffectiveDate());
        child.setEffectiveDay(source.getEffectiveDay());
        child.setExpirationDate(source.getExpirationDate());
        child.setStatus(source.getStatus());
        child.setUpchargeFlat(source.getUpchargeFlat());
        child.setUpchargePercent(source.getUpchargePercent());
        child.setUpchargeType(source.getUpchargeType());
        child.setCopiedFrom(source.getId());

        child.getLtlFuelGeoServices().clear();
        for (LtlFuelGeoServicesEntity item : source.getLtlFuelGeoServices()) {
            LtlFuelGeoServicesEntity geo = new LtlFuelGeoServicesEntity();
            for (LtlFuelGeoServiceDetailsEntity geoDetail : item.getLtlFuelGeoServiceDetails()) {
                geo.getLtlFuelGeoServiceDetails().add(new LtlFuelGeoServiceDetailsEntity(geoDetail));
            }

            child.getLtlFuelGeoServices().add(geo);
        }

        return child;
    }

    private LtlFuelEntity updateFuelEntity(LtlFuelEntity entity) {
        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricingProfileId()).equalsIgnoreCase(BLANKET)) {
            updateEntityInChildCSPs(entity);
        }

        return dao.merge(entity);
    }

    private void updateEntityInChildCSPs(LtlFuelEntity entity) {
        List<LtlFuelEntity> childs = dao.findAllCspChildsCopyedFrom(entity.getId());

        for (LtlFuelEntity child : childs) {
            LtlFuelEntity updatedChild = createChildCopyOfFuelEntity(entity, child);

            dao.saveOrUpdate(updatedChild);
        }
    }

    private void updateStatusInChildsListCSPs(List<Long> ownersListId, Status status, Long profileDetailId) {
        if (profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(BLANKET)) {
            dao.updateStatusInCSPByCopiedFrom(ownersListId, status, SecurityUtils.getCurrentPersonId());
        }
    }

    private void copyToCSPProfiles(List<LtlFuelEntity> sourceList, Long parentProfileDetailId) {
        dao.inactivateCSPByProfileDetailId(parentProfileDetailId, SecurityUtils.getCurrentPersonId());
        List<BigInteger> childDetailsIds = profileDao.findChildCSPDetailByParentDetailId(parentProfileDetailId);
        for (BigInteger detailId : childDetailsIds) {
            List<LtlFuelEntity> clonedList = cloneList(sourceList, detailId.longValue());
            dao.saveOrUpdateBatch(clonedList);
        }
    }

    private void processFuelEntity(LtlFuelEntity fuel) {
        if (fuel.getLtlFuelGeoServices() != null) {
            for (LtlFuelGeoServicesEntity geoServices : fuel.getLtlFuelGeoServices()) {
                geoServices.setLtlFuelGeoServiceDetails(getGeoServiceDetails(geoServices.getId(), geoServices.getOrigin()));
            }
        }
    }

    private List<LtlFuelGeoServiceDetailsEntity> getGeoServiceDetails(Long ltlFuelGeoServiceId, String origin) {
        List<LtlFuelGeoServiceDetailsEntity> geoServiceDetails = new ArrayList<LtlFuelGeoServiceDetailsEntity>();

        String[] geoCodes = StringUtils.split(origin, ',');
        for (String geoCode : geoCodes) {
            Pair<Integer, String> geoPair = GeoHelper.getGeoServType(geoCode);
            geoServiceDetails.add(new LtlFuelGeoServiceDetailsEntity(ltlFuelGeoServiceId, geoCode.trim(),
                    GeoType.ORIGIN, geoPair.getLeft().intValue(), geoPair.getRight()));
        }

        return geoServiceDetails;
    }

    private Date getFirstDayOfTheWeek(Calendar date) {
        date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return date.getTime();
    }
}
