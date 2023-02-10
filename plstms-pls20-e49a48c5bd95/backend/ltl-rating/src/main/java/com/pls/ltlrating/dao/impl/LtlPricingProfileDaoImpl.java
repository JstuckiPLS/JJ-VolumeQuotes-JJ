package com.pls.ltlrating.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlCustHidePricDetailsDao;
import com.pls.ltlrating.dao.LtlPricingBlockedCustomersDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlCustHidePricDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingBlockedCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.enums.GetRatesDateType;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.shared.GetRatesCO;
import com.pls.ltlrating.shared.LtlCustomerPricingProfileVO;
import com.pls.ltlrating.shared.LtlPricingProfileVO;

/**
 * {@link LtlPricingProfileDao} implementation.
 *
 * @author Mikhail Boldinov, 22/02/13
 */
@Transactional
@Repository
@SuppressWarnings("unchecked")
public class LtlPricingProfileDaoImpl extends AbstractDaoImpl<LtlPricingProfileEntity, Long> implements LtlPricingProfileDao {
    @Autowired
    private LtlPricingBlockedCustomersDao blockedCustomerDao;

    @Autowired
    private LtlCustHidePricDetailsDao hidePricDetailsDao;

    @Override
    public Long getCarrierCodeSeqNum() {
        return (Long) getCurrentSession().createSQLQuery("select NEXTVAL('LTL_RATING_CARR_CODE_SEQ') as id")
                .addScalar("id", StandardBasicTypes.LONG).uniqueResult();
    }

    @Override
    public List<LtlPricingProfileVO> getTariffsBySelectedCriteria(GetRatesCO getRates) {
        List<PricingType> pricingTypes = getRates.getPricingTypes();
        if (pricingTypes != null && !pricingTypes.isEmpty()) {
            Query query = getCurrentSession().getNamedQuery(LtlPricingProfileEntity.FIND_TARIFFS);
            query.setParameter("status", getRates.getStatus());
            query.setParameter("customerId", getRates.getCustomer(), LongType.INSTANCE);
            query.setParameter("pricingGroup", getRates.getPricingGroup(), StringType.INSTANCE);
            query.setParameterList("ltlPricingType", pricingTypes);
            setDateParameters(getRates, query);
            query.setResultTransformer(Transformers.aliasToBean(LtlPricingProfileVO.class));
            return query.list();
        } else {
            return new ArrayList<LtlPricingProfileVO>(0);
        }
    }

    private void setDateParameters(GetRatesCO getRates, Query query) {
        switch (getRates.getDateType() != null ? getRates.getDateType() : GetRatesDateType.NONE) {
        case EFFECTIVE:
            query.setParameter("fromEffDate", getRates.getFromDate(), DateType.INSTANCE);
            query.setParameter("toEffDate", getRates.getToDate(), DateType.INSTANCE);
            query.setParameter("fromExpDate", null, DateType.INSTANCE);
            query.setParameter("toExpDate", null, DateType.INSTANCE);
            break;
        case EXPIRATION:
            query.setParameter("fromEffDate", null, DateType.INSTANCE);
            query.setParameter("toEffDate", null, DateType.INSTANCE);
            query.setParameter("fromExpDate", getRates.getFromDate(), DateType.INSTANCE);
            query.setParameter("toExpDate", getRates.getToDate(), DateType.INSTANCE);
            break;
        default:
            query.setParameter("fromEffDate", null, DateType.INSTANCE);
            query.setParameter("toEffDate", null, DateType.INSTANCE);
            query.setParameter("fromExpDate", null, DateType.INSTANCE);
            query.setParameter("toExpDate", null, DateType.INSTANCE);
            break;
        }
    }

    @Override
    public List<LtlPricingProfileEntity> getProfilesToCopy(GetRatesCO getRates) throws EntityNotFoundException {
        if (getRates == null) {
            throw new IllegalArgumentException("Get Rates criteria object cannot be null");
        }

        Criteria criteria = getCriteria();
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        criteria.add(Restrictions.eq("status", Status.ACTIVE));

        criteria.createAlias("pricingType", "pricingType", JoinType.INNER_JOIN);
        if (getRates.getPricingGroup() != null) {
            criteria.add(Restrictions.eq("pricingType.groupType", getRates.getPricingGroup()));
            if ("CARRIER".equalsIgnoreCase(getRates.getPricingGroup())) {
                criteria.add(Restrictions.isNull("shipperOrganization"));
            } else {
                criteria.add(Restrictions.isNotNull("shipperOrganization"));
            }
        }

        criteria.createAlias("profileDetails", "profileDetails", JoinType.INNER_JOIN);

        addCarrierTypeCriteria(criteria, getRates);

        return criteria.list();
    }

    @Override
    public List<LtlCustomerPricingProfileVO> getPricingProfilesForCustomer(Long shipperOrgId) {
        return getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_CUSTOMER_PROFILES)
                .setLong("shipperOrgId", shipperOrgId)
                .setResultTransformer(Transformers.aliasToBean(LtlCustomerPricingProfileVO.class))
                .list();
    }

    @Override
    public void saveProfilesForCustomer(List<LtlCustomerPricingProfileVO> profiles) {
        for (LtlCustomerPricingProfileVO custPricingProfile : profiles) {
            updateBlockedCustomer(custPricingProfile);
            updateHideCustPricingDetails(custPricingProfile);
        }
    }

    @Override
    public LtlPricingProfileEntity getUnsavedProfileCopy(Long copyFromProfileId) {
        LtlPricingProfileEntity source =  this.find(copyFromProfileId);

        return getProfileCopy(source);
    }

    @Override
    public boolean isDuplicateProfileExists(LtlPricingProfileEntity profile) {
        List<Long> applicableCustomers;
        if (CollectionUtils.isEmpty(profile.getApplicableCustomers())) {
            applicableCustomers = Arrays.asList(-1L);
        } else {
            applicableCustomers = profile.getApplicableCustomers().stream().map(c -> c.getCustomer().getId()).collect(Collectors.toList());
        }
        return getCurrentSession().getNamedQuery(LtlPricingProfileEntity.GET_DUPLICATE_PROFILES)
                .setParameter("expDate", profile.getExpDate(), DateType.INSTANCE)
                .setDate("effDate", profile.getEffDate())
                .setInteger("isFilterByCustomers", CollectionUtils.isEmpty(profile.getApplicableCustomers()) ? 0 : 1)
                .setParameterList("applicableCustomers", applicableCustomers)
                .setParameter("carrierId", getCarrierId(profile), LongType.INSTANCE)
                .setParameter("profileId", profile.getId(), LongType.INSTANCE)
                .setMaxResults(1)
                .uniqueResult() != null;
    }

    private Long getCarrierId(LtlPricingProfileEntity profile) {
        return profile.getCarrierOrganization() != null ? profile.getCarrierOrganization().getId() : null;
    }

    @Override
    public List<LtlPricingProfileEntity> getActiveProfileByRateName(String rateName, Long profileId) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("status", Status.ACTIVE));
        criteria.add(Restrictions.eq("rateName", rateName));
        if (profileId != null) {
            criteria.add(Restrictions.ne("id", profileId));
        }

        return criteria.list();
    }

    @Override
    public List<LtlPricingProfileEntity> getActiveBlanketProfileByCarrier(LtlPricingProfileEntity profile) {
        return getCurrentSession().getNamedQuery(LtlPricingProfileEntity.Q_BLANKET_PROFILE_BY_CARRIER)
                .setParameter("carrierId", profile.getCarrierOrganization().getId(), LongType.INSTANCE)
                .setParameter("expDate", profile.getExpDate(), DateType.INSTANCE)
                .setParameter("effDate", profile.getEffDate(), DateType.INSTANCE)
                .setParameter("profileId", profile.getId(), LongType.INSTANCE)
                .list();
    }

    @Override
    public LtlPricingProfileEntity findMarginProfileByOrgId(Long orgId) {
        return (LtlPricingProfileEntity) getCurrentSession().getNamedQuery(LtlPricingProfileEntity.Q_MARGIN_PROFILE_BY_ORG_ID)
                .setLong("orgId", orgId).uniqueResult();
    }

    @Override
    public Long findMarginProfileDetailIdByOrgId(Long orgId) {
        return (Long) getCurrentSession().getNamedQuery(LtlPricingProfileEntity.Q_MARGIN_PROFILE_DETAIL_ID_BY_ORG_ID)
                .setLong("orgId", orgId).setMaxResults(1).uniqueResult();
    }

    @Override
    public String findPricingTypeByProfileDetailId(Long profileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingProfileEntity.FIND_PRICING_TYPE_BY_DETAIL_QUERY);
        query.setParameter("id", profileDetailId);

        return (String) query.uniqueResult();
    }

    @Override
    public String findPricingTypeByProfileId(Long profileId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingProfileEntity.FIND_PRICING_TYPE_BY_ID_QUERY);
        query.setParameter("id", profileId);

        return (String) query.uniqueResult();
    }

    @Override
    public List<LtlPricingProfileEntity> findChildCSPByProfileDetailId(Long parentProfileDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingProfileEntity.FIND_CHILD_PROFILE_BY_DETAILS_ID_QUERY);
        query.setParameter("parentId", parentProfileDetailId);

        return query.list();
    }

    @Override
    public LtlPricingApplicableCustomersEntity findActivePricingApplicableCustomer(Long profileId) {
        Criteria criteria = getCurrentSession().createCriteria(LtlPricingApplicableCustomersEntity.class);
        criteria.add(Restrictions.eq("ltlPricingProfileId", profileId));
        criteria.add(Restrictions.eq("status", Status.ACTIVE));
        List<LtlPricingApplicableCustomersEntity> result = criteria.list();

        return result.get(0);
    }

    @Override
    public List<Long> findChildCSPByProfileId(Long profileId) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("copiedFrom", profileId));
        criteria.setProjection(Projections.distinct(Projections.property("id")));

        return criteria.list();
    }

    @Override
    public List<LtlPricingProfileEntity> findChildCSPByParentProfile(LtlPricingProfileEntity parent) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingProfileEntity.Q_FIND_CSP_BY_PARENT_ID);
        query.setLong("parentId", parent.getId());

        return query.list();
    }

    @Override
    public void updateStatuses(List<Long> profileIds, Status status) {
        getCurrentSession().getNamedQuery(LtlPricingProfileEntity.UPDATE_STATUSES).setParameterList("ids", profileIds).
                setParameter("status", status).executeUpdate();
    }

    @Override
    public List<BigInteger> findChildCSPDetailByParentDetailId(Long parentDetailId) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingProfileEntity.Q_FIND_CSP_DETAILS_BY_PARENT_DETAIL_ID);
        query.setParameter("ownerId", parentDetailId);

        return query.list();
    }

    @Override
    public List<Long> getBlanketProfileIDsForCarriers(List<Long> carriersIDs) {
        return getCurrentSession().getNamedQuery(LtlPricingProfileEntity.Q_GET_BLANKET_IDS_BY_CARRIERS_IDS)
                .setParameterList("carriersIDs", carriersIDs).list();
    }

    private LtlPricingProfileEntity getProfileCopy(LtlPricingProfileEntity source) {
        LtlPricingProfileEntity target = new LtlPricingProfileEntity();

        target.setCarrierOrganization(source.getCarrierOrganization());
        target.setShipperOrganization(source.getShipperOrganization());
        target.setLtlPricingType(source.getLtlPricingType());
        target.setEffDate(source.getEffDate());
        target.setExpDate(source.getExpDate());
        target.setBlocked(source.getBlocked());
        target.setAliasName(source.getAliasName());
        target.setCarrierWebsite(source.getCarrierWebsite());
        target.setNote(source.getNote());
        target.setProhibitedCommodities(source.getProhibitedCommodities());
        target.setCopiedFromProfileId(source.getId());
        target.setExtNotes(source.getExtNotes());
        target.setIntNotes(source.getIntNotes());

        Set<LtlPricingProfileDetailsEntity> copiedProfileDetails = new HashSet<LtlPricingProfileDetailsEntity>();
        for (LtlPricingProfileDetailsEntity sourceProfDetail : source.getProfileDetails()) {
            copiedProfileDetails.add(getProfileDetailsCopy(sourceProfDetail));
        }

        target.setProfileDetails(copiedProfileDetails);

        return target;
    }

    private LtlPricingProfileDetailsEntity getProfileDetailsCopy(LtlPricingProfileDetailsEntity source) {
        LtlPricingProfileDetailsEntity target = new LtlPricingProfileDetailsEntity();

        target.setPricingDetailType(source.getPricingDetailType());
        target.setCarrierType(source.getCarrierType());
        target.setMileageType(source.getMileageType());
        target.setMileageVersion(source.getMileageVersion());
        target.setSmc3Carrier(source.getSmc3Carrier());
        target.setSmc3Tariff(source.getSmc3Tariff());
        target.setMScale(source.getMScale());
        target.setCarrierAPIDetails(source.getCarrierAPIDetails());

        return target;
    }

    private void updateBlockedCustomer(LtlCustomerPricingProfileVO custPricingProfile) {
        getCurrentSession().getNamedQuery(LtlPricingBlockedCustomersEntity.ARCHIVE_BLOCKED_CUST)
                .setParameter("profileId", custPricingProfile.getLtlPricingProfileId())
                .setParameter("shipperOrgId", custPricingProfile.getShipperOrgId())
                .setParameter("modifiedBy", SecurityUtils.getCurrentPersonId()).executeUpdate();

        if (custPricingProfile.getBlocked()) {
            LtlPricingBlockedCustomersEntity blockedEntity = new LtlPricingBlockedCustomersEntity();
            blockedEntity.setLtlPricingProfileId(custPricingProfile.getLtlPricingProfileId());
            blockedEntity.setShipperOrgId(custPricingProfile.getShipperOrgId());
            blockedCustomerDao.saveOrUpdate(blockedEntity);
        }
    }

    private void updateHideCustPricingDetails(LtlCustomerPricingProfileVO custPricingProfile) {
        getCurrentSession().getNamedQuery(LtlCustHidePricDetailsEntity.ARCHIVE_HIDE_CUST_PRIC)
                .setParameter("profileId", custPricingProfile.getLtlPricingProfileId())
                .setParameter("shipperOrgId", custPricingProfile.getShipperOrgId())
                .setParameter("modifiedBy", SecurityUtils.getCurrentPersonId()).executeUpdate();

        if (custPricingProfile.getTier1()) {
            LtlCustHidePricDetailsEntity hidePricDetailsEntity = new LtlCustHidePricDetailsEntity();
            hidePricDetailsEntity.setLtlPricingProfileId(custPricingProfile.getLtlPricingProfileId());
            hidePricDetailsEntity.setShipperOrgId(custPricingProfile.getShipperOrgId());
            hidePricDetailsDao.saveOrUpdate(hidePricDetailsEntity);
        }
    }

    private String addCarrierTypeCriteria(Criteria criteria, GetRatesCO getRates) throws EntityNotFoundException {
        LtlPricingProfileEntity currentProfile = this.get(getRates.getProfileId());
        String carrierType = "";
        String profileDetailTypeLiteral = "profileDetails.pricingDetailType";

        criteria.add(Restrictions.ne("id", currentProfile.getId()));

        if (getRates.getCopyFromPricingDetailType() != null) {
            criteria.add(Restrictions.or(
                    Restrictions.eq(profileDetailTypeLiteral, getRates.getCopyFromPricingDetailType()),
                    Restrictions.isNull(profileDetailTypeLiteral)
                ));
        }
        for (LtlPricingProfileDetailsEntity profileDetail : currentProfile.getProfileDetails()) {
            if (getRates.getCopyFromPricingDetailType() == null
                    || profileDetail.getPricingDetailType() == getRates.getCopyFromPricingDetailType()) {
                carrierType = profileDetail.getCarrierType();
            }
        }

        return carrierType;
    }
}
