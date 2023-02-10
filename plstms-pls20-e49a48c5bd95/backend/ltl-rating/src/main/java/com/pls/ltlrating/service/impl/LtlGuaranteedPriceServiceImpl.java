package com.pls.ltlrating.service.impl;

import java.math.BigDecimal;
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

import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlGuaranteedPriceDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlGuaranBlockDestDetailsEntity;
import com.pls.ltlrating.domain.LtlGuaranteedBlockDestEntity;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.GuaranteedPriceListItemVO;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.service.LtlGuaranteedPriceService;
import com.pls.ltlrating.service.validation.LtlGuaranteedPriceValidator;

/**
 * Implementation of {@link LtlGuaranteedPriceService}.
 *
 * @author Artem Arapov
 *
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class LtlGuaranteedPriceServiceImpl implements LtlGuaranteedPriceService {

    @Autowired
    private LtlGuaranteedPriceDao dao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Autowired
    private OrganizationPricingDao orgPricingDao;

    @Resource(type = LtlGuaranteedPriceValidator.class)
    private Validator<LtlGuaranteedPriceEntity> validator;

    private static final String BLANKET = "BLANKET";

    @Override
    public LtlGuaranteedPriceEntity saveGuaranteedPrice(LtlGuaranteedPriceEntity entity) throws ValidationException {
        validator.validate(entity);

        processGuaranteed(entity);
        return (entity.getId() == null) ? addGuaranteed(entity) : updateGuaranteed(entity);
    }

    @Override
    public LtlGuaranteedPriceEntity getGuaranteedPriceById(Long id) {
        LtlGuaranteedPriceEntity entity = dao.find(id);

        for (LtlGuaranteedBlockDestEntity item : entity.getGuaranteedBlockDestinations()) {
            item.setOrigin(StringUtils.join(item.getLtlGuaranOriginDetails(), ','));
            item.setDestination(StringUtils.join(item.getLtlGuaranDestinationDetails(), ','));
        }
        return entity;
    }

    @Override
    public List<LtlGuaranteedPriceEntity> getAllGuaranteedByProfileDetailId(Long profileDetailId) {
        return dao.findByProfileDetailId(profileDetailId);
    }

    @Override
    public List<GuaranteedPriceListItemVO> getActiveGuaranteedByProfileDetailId(Long profileDetailId) {
        return dao.findActiveAndEffectiveByProfileDetailId(profileDetailId);
    }

    @Override
    public List<GuaranteedPriceListItemVO> getInactiveGuaranteedByProfileDetailId(Long profileDetailId) {
        return dao.findByStatusAndProfileDetailId(Status.INACTIVE, profileDetailId);
    }

    @Override
    public List<GuaranteedPriceListItemVO> getExpiredGuaranteedByProfileDetailId(Long profileDetailId) {
        return dao.findExpiredByProfileDetailId(profileDetailId);
    }

    @Override
    public List<GuaranteedPriceListItemVO> inactivateGuaranteedPricings(List<Long> guaranteedIds, Long profileDetailId,
            boolean isActiveList) {
        dao.updateStatusOfGuaranteedPriceList(guaranteedIds, Status.INACTIVE, SecurityUtils.getCurrentPersonId());
        updateStatusInChildsListCSPs(guaranteedIds, Status.INACTIVE, profileDetailId);

        return isActiveList ? getActiveGuaranteedByProfileDetailId(profileDetailId)
                : getExpiredGuaranteedByProfileDetailId(profileDetailId);
    }

    @Override
    public List<GuaranteedPriceListItemVO> reactivateGuaranteedPricings(List<Long> guaranteedIds, Long profileDetailId) {
        dao.updateStatusOfGuaranteedPriceList(guaranteedIds, Status.ACTIVE, SecurityUtils.getCurrentPersonId());
        updateStatusInChildsListCSPs(guaranteedIds, Status.ACTIVE, profileDetailId);

        return getInactiveGuaranteedByProfileDetailId(profileDetailId);
    }

    @Override
    public List<GuaranteedPriceListItemVO> expireGuaranteedPricings(List<Long> guaranteedIds, Long profileDetailId) {
        Long personId = SecurityUtils.getCurrentPersonId();
        dao.expireByListOfIds(guaranteedIds, personId);
        if (profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(BLANKET)) {
            dao.expirateCSPByCopiedFrom(guaranteedIds, personId);
        }

        return getActiveGuaranteedByProfileDetailId(profileDetailId);
    }

    @Override
    public void copyFrom(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyToCSP) {
        dao.updateStatusToInactiveByProfileId(copyToProfileDetailId, SecurityUtils.getCurrentPersonId());

        List<LtlGuaranteedPriceEntity> sourceList = dao.findActiveAndEffectiveEntitiesForProfile(copyFromProfileDetailId);
        List<LtlGuaranteedPriceEntity> clonedList = cloneList(sourceList, copyToProfileDetailId);

        dao.saveOrUpdateBatch(clonedList);

        if (shouldCopyToCSP && profileDao.findPricingTypeByProfileDetailId(copyToProfileDetailId).equalsIgnoreCase(BLANKET)) {
            copyToCSPProfiles(clonedList, copyToProfileDetailId);
        }
    }

    private List<LtlGuaranteedPriceEntity> cloneList(List<LtlGuaranteedPriceEntity> source, Long profileDetailId) {
        List<LtlGuaranteedPriceEntity> clonedList = new ArrayList<LtlGuaranteedPriceEntity>(source.size());

        for (LtlGuaranteedPriceEntity item : source) {
            LtlGuaranteedPriceEntity clone = new LtlGuaranteedPriceEntity(item);
            clone.setLtlPricProfDetailId(profileDetailId);
            clonedList.add(clone);
        }

        return clonedList;
    }

    private void copyToCSPProfiles(List<LtlGuaranteedPriceEntity> sourceList, Long parentProfileDetailId) {
        dao.inactivateCSPByProfileDetailId(parentProfileDetailId, SecurityUtils.getCurrentPersonId());
        List<BigInteger> childDetailsIds = profileDao.findChildCSPDetailByParentDetailId(parentProfileDetailId);
        for (BigInteger detailId : childDetailsIds) {
            List<LtlGuaranteedPriceEntity> clonedList = cloneList(sourceList, detailId.longValue());
            dao.saveOrUpdateBatch(clonedList);
        }
    }

    private LtlGuaranteedPriceEntity addGuaranteed(LtlGuaranteedPriceEntity entity) {
        LtlGuaranteedPriceEntity savedEntity = dao.saveOrUpdate(entity);
        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricProfDetailId()).equalsIgnoreCase(BLANKET)) {
            addEntityToChildCSPs(savedEntity);
        }

        return savedEntity;
    }

    private void addEntityToChildCSPs(LtlGuaranteedPriceEntity entity) {
        List<LtlPricingProfileEntity> childProfilesList = profileDao.findChildCSPByProfileDetailId(entity.getLtlPricProfDetailId());

        for (LtlPricingProfileEntity profile : childProfilesList) {
            addEntityToChildCSP(entity, profile);
        }
    }

    private void addEntityToChildCSP(LtlGuaranteedPriceEntity source, LtlPricingProfileEntity childProfile) {
        for (LtlPricingProfileDetailsEntity detail : childProfile.getProfileDetails()) {
            addEntityToChildCSPDetail(source, childProfile.getId(), detail.getId());
        }
    }

    private void addEntityToChildCSPDetail(LtlGuaranteedPriceEntity source, Long profileId, Long profileDetailId) {
        LtlGuaranteedPriceEntity entity = createChildCopyOfGuaranteedEntity(source, new LtlGuaranteedPriceEntity());
        entity.setLtlPricProfDetailId(profileDetailId);
        BigDecimal defaultMargin = getDefaultPricingOfProfileCustomer(profileId);
        entity.setUnitMargin(defaultMargin);

        dao.saveOrUpdate(entity);
    }

    private BigDecimal getDefaultPricingOfProfileCustomer(Long profileId) {
        LtlPricingApplicableCustomersEntity applicableCustomer = profileDao.findActivePricingApplicableCustomer(profileId);
        OrganizationPricingEntity orgPricing = orgPricingDao.getActivePricing(applicableCustomer.getCustomer().getId());

        return orgPricing.getDefaultMargin();
    }

    private LtlGuaranteedPriceEntity updateGuaranteed(LtlGuaranteedPriceEntity entity) {
        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricProfDetailId()).equalsIgnoreCase(BLANKET)) {
            updateEntityInChildCSPs(entity);
        }

        return dao.merge(entity);
    }

    private void updateEntityInChildCSPs(LtlGuaranteedPriceEntity entity) {
        List<LtlGuaranteedPriceEntity> childs = dao.findAllCspChildsCopyedFrom(entity.getId());

        for (LtlGuaranteedPriceEntity child : childs) {
            LtlGuaranteedPriceEntity updatedChild = createChildCopyOfGuaranteedEntity(entity, child);

            dao.saveOrUpdate(updatedChild);
        }
    }

    private LtlGuaranteedPriceEntity createChildCopyOfGuaranteedEntity(LtlGuaranteedPriceEntity source,
            LtlGuaranteedPriceEntity child) {
        child.setApplyBeforeFuel(source.getApplyBeforeFuel());
        child.setBollCarrierName(source.getBollCarrierName());
        child.setChargeRuleType(source.getChargeRuleType());
        child.setEffDate(source.getEffDate());
        child.setExpDate(source.getExpDate());
        child.setMinCost(source.getMinCost());
        child.setStatus(source.getStatus());
        child.setTime(source.getTime());
        child.setUnitCost(source.getUnitCost());
        child.setCostApplMinWt(source.getCostApplMinWt());
        child.setCostApplMaxWt(source.getCostApplMaxWt());
        child.setCostApplWtUom(source.getCostApplWtUom());
        child.setCostApplMinDist(source.getCostApplMinDist());
        child.setCostApplMaxDist(source.getCostApplMaxDist());
        child.setCostApplDistUom(source.getCostApplDistUom());
        child.setMinCost(source.getMinCost());
        child.setMaxCost(source.getMaxCost());
        child.setMovementType(source.getMovementType());
        child.setServiceType(source.getServiceType());
        child.setCopiedFrom(source.getId());
        child.setExtNotes(source.getExtNotes());
        child.setIntNotes(source.getIntNotes());

        child.getGuaranteedBlockDestinations().clear();
        for (LtlGuaranteedBlockDestEntity item : source.getGuaranteedBlockDestinations()) {
            child.getGuaranteedBlockDestinations().add(new LtlGuaranteedBlockDestEntity(item));
        }

        return child;
    }

    private void updateStatusInChildsListCSPs(List<Long> ownersListId, Status status, Long profileDetailId) {
        if (profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(BLANKET)) {
            dao.updateStatusInCSPByCopiedFrom(ownersListId, status, SecurityUtils.getCurrentPersonId());
        }
    }

    private void processGuaranteed(LtlGuaranteedPriceEntity entity) {
        if (entity.getGuaranteedBlockDestinations() != null) {
            for (LtlGuaranteedBlockDestEntity geoServices : entity.getGuaranteedBlockDestinations()) {
                geoServices.setLtlGuaranOriginDetails(getGeoServiceDetails(geoServices, geoServices.getOrigin(), GeoType.ORIGIN));
                geoServices.setLtlGuaranDestinationDetails(getGeoServiceDetails(geoServices, geoServices.getDestination(),
                        GeoType.DESTINATION));
            }
        }
    }

    private List<LtlGuaranBlockDestDetailsEntity> getGeoServiceDetails(LtlGuaranteedBlockDestEntity geoService, String geoValue, GeoType geoType) {
        List<LtlGuaranBlockDestDetailsEntity> geoServiceDetails = new ArrayList<LtlGuaranBlockDestDetailsEntity>();

        if (StringUtils.isNotEmpty(geoValue)) {
            String[] geoCodes = StringUtils.split(geoValue, ',');
            for (String geoCode : geoCodes) {
                Pair<Integer, String> geoPair = GeoHelper.getGeoServType(geoCode);
                geoServiceDetails.add(new LtlGuaranBlockDestDetailsEntity(geoService, geoCode.trim(), geoType, geoPair
                        .getLeft().intValue(), geoPair.getRight()));
            }
        }

        return geoServiceDetails;
    }
}
