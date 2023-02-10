package com.pls.ltlrating.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlAccessorialsDao;
import com.pls.ltlrating.dao.LtlAccessorialsMappingDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlAccGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlAccGeoServicesEntity;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.LtlAccessorialsMappingEntity;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.AccessorialListItemVO;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.domain.enums.LtlMarginType;
import com.pls.ltlrating.service.LtlAccessorialsService;

/**
 * Service Implementation class that handle business logic and transactions for LTL Accessorials.
 *
 * @author Hima Bindu Challa
 */
@Service
@Transactional(readOnly = true)
public class LtlAccessorialsServiceImpl implements LtlAccessorialsService {

    private static final Logger LOG = LoggerFactory.getLogger(LtlAccessorialsServiceImpl.class);

    private static final String BLANKET = "BLANKET";

    @Autowired
    private LtlAccessorialsDao dao;

    @Autowired
    private LtlAccessorialsMappingDao mappingDao;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Autowired
    private OrganizationPricingDao orgPricingDao;

    /**
     * This method is for both create and update operations. The Save operation returns the updated data
     * (succes or roll back) along with other field values - primary key, date created, created by, date
     * modified, modified by, version and will use the same to populate the screen. This is required
     * especially for pessimistic locking.
     *
     * @param accessorial
     *            - The LtlAccessorialsEntity that need to be saved
     * @return LtlAccessorialsEntity - Updated LTLAccessorialEntity (With date created, created by and version
     *         values)
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlAccessorialsEntity saveAccessorial(LtlAccessorialsEntity accessorial) {
        processAccessorial(accessorial);
        return (accessorial.getId() == null) ? addAccessorial(accessorial) : updateAccessorial(accessorial);
    }

    /**
     * To get Accessorial by primary Key.
     *
     * @param id
     *            - Primary Key of the entity - LTLAccessorialsEntity
     * @return LtlAccessorialsEntity
     */
    @Override
    public LtlAccessorialsEntity getAccessorialById(Long id) {
        LtlAccessorialsEntity entity = dao.find(id);

        for (LtlAccGeoServicesEntity item : entity.getLtlAccGeoServicesEntities()) {
            item.setOrigin(StringUtils.join(item.getLtlAccOriginGeoServiceDetails(), ','));
            item.setDestination(StringUtils.join(item.getLtlAccDestGeoServiceDetails(), ','));
        }
        return entity;
    }

    /**
     * To get All Accessorials irrespective of status and expiration date for the given profile detail
     * (Buy/Sell/None).
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<LtlAccessorialsEntity> - List of all LtlAccessorialsEntities for selected profile
     */
    public List<LtlAccessorialsEntity> getAllAccessorialsByProfileDetailId(Long profileDetailId) {
        return dao.findAllByProfileDetailId(profileDetailId);
    }

    /**
     * To get All Active and Effective Accessorials (LOCALTIMESTAMP <= expdate) for selected profile detail
     * (Buy/Sell/None).
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the active accessorials should be retrieved
     * @return List<LtlAccessorialsEntity> - List of all Active and Effective LtlAccessorialsEntities for
     *         selected profile
     */
    public List<AccessorialListItemVO> getActiveAccessorialsByProfileDetailId(Long profileDetailId) {
        return dao.findActiveAndEffectiveByProfileDetailId(profileDetailId);
    }

    /**
     * To get All inactive Accessorials for selected profile detail (Buy/Sell/None).
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the inactive accessorials should be
     *            retrieved
     * @return List<LtlAccessorialsEntity> - List of all inactive LtlAccessorialsEntities for selected profile
     */
    public List<AccessorialListItemVO> getInactiveAccessorialsByProfileDetailId(Long profileDetailId) {
        return dao.findAllByStatusAndProfileDetailId(profileDetailId, Status.INACTIVE);
    }

    /**
     * To get All active and expired (LOCALTIMESTAMP > expdate) Accessorials for selected profile detail
     * (Buy/Sell/None).
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the expired accessorials should be
     *            retrieved
     * @return List<LtlAccessorialsEntity> - List of all active and expired LtlAccessorialsEntities for
     *         selected profile
     */
    public List<AccessorialListItemVO> getExpiredAccessorialsByProfileDetailId(Long profileDetailId) {
        return dao.findExpiredByProfileDetailId(profileDetailId);
    }

    /**
     * To archive/inactivate multiple active accessorials.
     *
     * This method returns list of active or expired accessorials based on the boolean flag "isActiveList". If
     * flag is yes/true, we are inactivating "active and effective" accessorials, so this method returns
     * updated "Active and Effective" accessorial list using method
     * "getActiveAccessorialsByProfileId(Long profileDetailId);".
     *
     * If flag is no/false, we are inactivating "active and expired" accessorials, so this method returns
     * updated "Active and Expired" accessorial list using method
     * "getInactiveAccessorialsByProfileId(Long profileDetailId);"
     *
     * @param accessorialIds
     *            - List of LtlAccessorialsEntity Ids - primary keys that need to be inactivated
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved after
     *            inactivating the selected accessorials
     * @param isActiveList
     *            Not <code>null</code> instance of {@link Boolean}.
     * @return List<LtlAccessorialsEntity> - List of all (active & effective) or (expired)
     *         LtlAccessorialsEntities for selected profile
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<AccessorialListItemVO>  inactivateAccessorials(
            List<Long> accessorialIds, Long profileDetailId, Boolean isActiveList) {

        LOG.info("Inactivating Accessorials .... ");

        dao.updateStatuses(accessorialIds, Status.INACTIVE, SecurityUtils.getCurrentPersonId());
        updateStatusInChildCSPs(accessorialIds, Status.INACTIVE, profileDetailId);

        if (isActiveList) {
            LOG.info("Retrieving Active and effective Accessorials .... ");

            return getActiveAccessorialsByProfileDetailId(profileDetailId);
        }

        LOG.info("Retrieving Active and expired Accessorials .... ");

        return getExpiredAccessorialsByProfileDetailId(profileDetailId);
    }

    /**
     * To reactivate multiple inactive accessorials. This method returns list of inactive accessorials as the
     * list of accessorials that will be reactivated are inactive accessorials.
     *
     * @param accessorialIds
     *            - List of LtlAccessorialsEntity Ids - primary keys that need to be reactivated
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved after
     *            reactivating the selected accessorials
     * @return List<LtlAccessorialsEntity> - List of all inactive LtlAccessorialsEntities for selected profile
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<AccessorialListItemVO>  reactivateAccessorials(
            List<Long> accessorialIds, Long profileDetailId) {

        LOG.info("Reactivating Accessorials .... ");

        dao.updateStatuses(accessorialIds, Status.ACTIVE, SecurityUtils.getCurrentPersonId());
        updateStatusInChildCSPs(accessorialIds, Status.ACTIVE, profileDetailId);

        return getInactiveAccessorialsByProfileDetailId(profileDetailId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cloneAccessorials(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyToCSP) {
        dao.inactivateActiveAndEffAccByProfDetailId(copyToProfileDetailId);

        List<LtlAccessorialsEntity> sourceList = dao.findActiveAndEffectiveForProfile(copyFromProfileDetailId);
        List<LtlAccessorialsEntity> clonedList = cloneList(sourceList, copyToProfileDetailId);

        dao.saveOrUpdateBatch(clonedList);

        if (shouldCopyToCSP && profileDao.findPricingTypeByProfileDetailId(copyToProfileDetailId).equalsIgnoreCase(BLANKET)) {
            copyToCSPProfiles(clonedList, copyToProfileDetailId);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<AccessorialListItemVO> expirateByListOfIds(List<Long> ids, Long profileDetailId) {
        dao.expireByListOfIds(ids, SecurityUtils.getCurrentPersonId());
        if (profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(BLANKET)) {
            dao.expirateCSPByCopiedFrom(ids, SecurityUtils.getCurrentPersonId());
        }

        return getActiveAccessorialsByProfileDetailId(profileDetailId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<LtlAccessorialsMappingEntity> getAccessorialsMapping(Long carrierId) {
        return mappingDao.getAccessorialsMapping(carrierId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAccessorialsMapping(List<LtlAccessorialsMappingEntity> accList) {
        mappingDao.saveOrUpdateBatch(accList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LtlAccessorialsMappingEntity getAccMappingById(Long id) {
        return mappingDao.find(id);
    }

    private List<LtlAccessorialsEntity> cloneList(List<LtlAccessorialsEntity> sourceList, Long profileDetailId) {
        List<LtlAccessorialsEntity> clonedList = new ArrayList<LtlAccessorialsEntity>();
        for (LtlAccessorialsEntity source : sourceList) {
            LtlAccessorialsEntity clone = new LtlAccessorialsEntity(source);
            clone.setLtlPricProfDetailId(profileDetailId);
            clonedList.add(clone);
        }

        return clonedList;
    }

    private void copyToCSPProfiles(List<LtlAccessorialsEntity> sourceList, Long parentProfileDetailId) {
        dao.inactivateCSPByProfileDetailId(parentProfileDetailId, SecurityUtils.getCurrentPersonId());
        List<BigInteger> childDetailsIds = profileDao.findChildCSPDetailByParentDetailId(parentProfileDetailId);
        for (BigInteger detailId : childDetailsIds) {
            List<LtlAccessorialsEntity> clonedList = cloneList(sourceList, detailId.longValue());
            dao.saveOrUpdateBatch(clonedList);
        }
    }

    private LtlAccessorialsEntity addAccessorial(LtlAccessorialsEntity entity) {
        LtlAccessorialsEntity accessorial = dao.saveOrUpdate(entity);

        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricProfDetailId()).equalsIgnoreCase(BLANKET)) {
            addEntityToChildCSPs(accessorial);
        }

        return accessorial;
    }

    private LtlAccessorialsEntity updateAccessorial(LtlAccessorialsEntity entity) {
        if (profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricProfDetailId()).equalsIgnoreCase(BLANKET)) {
            updateEntityInChildCSPs(entity);
        }

        return dao.merge(entity);
    }

    private void addEntityToChildCSPs(LtlAccessorialsEntity entity) {
        List<LtlPricingProfileEntity> childProfilesList = profileDao.findChildCSPByProfileDetailId(entity
                .getLtlPricProfDetailId());

        for (LtlPricingProfileEntity profile : childProfilesList) {
            addEntityToChildCSP(entity, profile);
        }
    }

    private void addEntityToChildCSP(LtlAccessorialsEntity source, LtlPricingProfileEntity childProfile) {
        BigDecimal defaultMargin = getDefaultPricingOfProfileCustomer(childProfile.getId());
        for (LtlPricingProfileDetailsEntity detail : childProfile.getProfileDetails()) {
            addEntityToChildCSPDetail(source, detail.getId(), defaultMargin);
        }
    }

    private void addEntityToChildCSPDetail(LtlAccessorialsEntity source, Long profileDetailId, BigDecimal defaultMargin) {
        LtlAccessorialsEntity entity = createChildCopyOfAccessorialsEntity(source, new LtlAccessorialsEntity());
        entity.setLtlPricProfDetailId(profileDetailId);
        entity.setMarginType(LtlMarginType.MC.name());
        entity.setUnitMargin(defaultMargin);

        dao.saveOrUpdate(entity);
    }

    private BigDecimal getDefaultPricingOfProfileCustomer(Long profileId) {
        LtlPricingApplicableCustomersEntity applicableCustomer = profileDao
                .findActivePricingApplicableCustomer(profileId);
        OrganizationPricingEntity orgPricing = orgPricingDao.getActivePricing(applicableCustomer.getCustomer().getId());

        return orgPricing != null ? orgPricing.getDefaultMargin() : BigDecimal.ZERO;
    }

    private void updateEntityInChildCSPs(LtlAccessorialsEntity entity) {
        List<LtlAccessorialsEntity> childs = dao.findAllCspChildsCopyedFrom(entity.getId());

        for (LtlAccessorialsEntity child : childs) {
            LtlAccessorialsEntity updatedChild = createChildCopyOfAccessorialsEntity(entity, child);

            dao.saveOrUpdate(updatedChild);
        }
    }

    private LtlAccessorialsEntity createChildCopyOfAccessorialsEntity(LtlAccessorialsEntity source,
            LtlAccessorialsEntity child) {
        child.setAccessorialType(source.getAccessorialType());
        child.setBlocked(source.getBlocked());
        child.setCostType(source.getCostType());
        child.setUnitCost(source.getUnitCost());
        child.setCostApplMinWt(source.getCostApplMinWt());
        child.setCostApplMaxWt(source.getCostApplMaxWt());
        child.setCostApplWtUom(source.getCostApplWtUom());
        child.setCostApplMinDist(source.getCostApplMinDist());
        child.setCostApplMaxDist(source.getCostApplMaxDist());
        child.setCostApplDistUom(source.getCostApplDistUom());
        child.setMinCost(source.getMinCost());
        child.setMaxCost(source.getMaxCost());
        child.setServiceType(source.getServiceType());
        child.setMovementType(source.getMovementType());
        child.setEffDate(source.getEffDate());
        child.setExpDate(source.getExpDate());
        child.setNotes(source.getNotes());
        child.setStatus(source.getStatus());
        child.setCopiedFrom(source.getId());
        child.setExtNotes(source.getExtNotes());
        child.setIntNotes(source.getIntNotes());

        child.getLtlAccGeoServicesEntities().clear();
        cloneGeoServicesList(source.getLtlAccGeoServicesEntities(), child.getLtlAccGeoServicesEntities());

        return child;
    }

    private void cloneGeoServicesList(List<LtlAccGeoServicesEntity> source, List<LtlAccGeoServicesEntity> destination) {
        for (LtlAccGeoServicesEntity item : source) {
            destination.add(new LtlAccGeoServicesEntity(item));
        }
    }

    private void updateStatusInChildCSPs(List<Long> ownersListId, Status status, Long profileDetailId) {
        if (profileDao.findPricingTypeByProfileDetailId(profileDetailId).equalsIgnoreCase(BLANKET)) {
            dao.updateStatusInCSPByCopiedFrom(ownersListId, status, SecurityUtils.getCurrentPersonId());
        }
    }

    private void processAccessorial(LtlAccessorialsEntity accessorial) {
        if (accessorial.getLtlAccGeoServicesEntities() != null) {
            for (LtlAccGeoServicesEntity geoServices : accessorial.getLtlAccGeoServicesEntities()) {
                geoServices.setLtlAccOriginGeoServiceDetails(getGeoServiceDetails(geoServices, geoServices.getOrigin(), GeoType.ORIGIN));
                geoServices.setLtlAccDestGeoServiceDetails(getGeoServiceDetails(geoServices, geoServices.getDestination(),
                        GeoType.DESTINATION));
            }
        }
        //If accessorial is ODM then it would not use the minimum and maximum weight values, hence
        //set them to null. While accessorials other than ODM would not use minimum and maximum
        // length values, hence set them to null.
        if (accessorial.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.OVER_DIMENSION.getCode())) {
            accessorial.setCostApplMinWt(null);
            accessorial.setCostApplMaxWt(null);
        } else {
            accessorial.setCostApplMinLength(null);
            accessorial.setCostApplMaxLength(null);
        }
    }

    private List<LtlAccGeoServiceDetailsEntity> getGeoServiceDetails(LtlAccGeoServicesEntity geoService,
            String geoValue, GeoType geoType) {
        List<LtlAccGeoServiceDetailsEntity> geoServiceDetails = new ArrayList<LtlAccGeoServiceDetailsEntity>();

        if (StringUtils.isNotEmpty(geoValue)) {
            String[] geoCodes = StringUtils.split(geoValue, ',');
            for (String geoCode : geoCodes) {
                Pair<Integer, String> geoPair = GeoHelper.getGeoServType(geoCode);
                geoServiceDetails.add(new LtlAccGeoServiceDetailsEntity(geoService, geoCode.trim(), geoType, geoPair
                        .getLeft().intValue(), geoPair.getRight()));
            }
        }

        return geoServiceDetails;
    }
}
