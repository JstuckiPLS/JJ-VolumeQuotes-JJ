package com.pls.ltlrating.service.impl;

import com.pls.core.dao.LtlLookupValueDao;
import com.pls.core.dao.OrganizationDao;
import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.OrganizationService;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;
import com.pls.extint.shared.AvailableCarrierRequestVO;
import com.pls.extint.shared.DataModuleVO;
import com.pls.ltlrating.dao.LtlPricingApplicableCustomersDao;
import com.pls.ltlrating.dao.LtlPricingBlockedCustomersDao;
import com.pls.ltlrating.dao.LtlPricingCarrierTypesDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.LtlPricingTypesDao;
import com.pls.ltlrating.dao.MileageTypesDao;
import com.pls.ltlrating.dao.SMC3TariffsDao;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingBlockedCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingCarrierTypesEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.LtlPricingTypesEntity;
import com.pls.ltlrating.domain.MileageTypesEntity;
import com.pls.ltlrating.domain.SMC3TariffsEntity;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.service.LtlAccessorialsService;
import com.pls.ltlrating.service.LtlBlockCarrGeoServicesService;
import com.pls.ltlrating.service.LtlCarrierLiabilitiesService;
import com.pls.ltlrating.service.LtlFuelService;
import com.pls.ltlrating.service.LtlFuelSurchargeService;
import com.pls.ltlrating.service.LtlGuaranteedPriceService;
import com.pls.ltlrating.service.LtlPalletPricingDetailsService;
import com.pls.ltlrating.service.LtlPricingDetailsService;
import com.pls.ltlrating.service.LtlPricingTerminalInfoService;
import com.pls.ltlrating.service.LtlPricingThirdPartyInfoService;
import com.pls.ltlrating.service.LtlProfileDetailsService;
import com.pls.ltlrating.service.LtlZonesSerivce;
import com.pls.ltlrating.shared.GetRatesCO;
import com.pls.ltlrating.shared.LtlCopyProfileVO;
import com.pls.ltlrating.shared.LtlCustomerPricingVO;
import com.pls.ltlrating.shared.LtlPricingProfileLookupValuesVO;
import com.pls.ltlrating.shared.LtlPricingProfileVO;
import com.pls.smc3.service.SMC3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pricing profile details service implementation {@link com.pls.ltlrating.service.LtlProfileDetailsService}.
 *
 * @author Aleksandr Leshchenko
 * @author Andrey Kachur
 * @author Hima Bindu Challa
 * @author Ashwini Neelgund
 */
@Service
@Transactional(readOnly = true)
public class LtlProfileDetailsServiceImpl implements LtlProfileDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(LtlProfileDetailsServiceImpl.class);

    private static final String SMC3 = "SMC3";

    @Value("${goShip.goShipNetwork}")
    private Long businessUnitId;

    @Autowired
    private LtlPricingTypesDao ltlPricingTypesDao;
    @Autowired
    private LtlPricingCarrierTypesDao ltlRatingCarrierTypesDao;
    @Autowired
    private MileageTypesDao mileageTypesDao;
    @Autowired
    private LtlLookupValueDao ltlLookupValueDao;
    @Autowired
    private SMC3TariffsDao smc3TariffsDao;
    @Autowired
    private LtlPricingProfileDao ltlPricingProfileDao;
    @Autowired
    private LtlPricingBlockedCustomersDao ltlPricingBlockedCustomersDao;
    @Autowired
    private LtlPricingApplicableCustomersDao ltlPricingApplicableCustomersDao;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SMC3Service smc3Service;
    @Autowired
    private OrganizationDao organizationDao;
    @Autowired
    private OrganizationPricingDao orgPricingDao;
    @Autowired
    private LtlPricingDetailsService pricingDetailsService;
    @Autowired
    private LtlAccessorialsService accessorialService;
    @Autowired
    private LtlGuaranteedPriceService guaranteedService;
    @Autowired
    private LtlPricingThirdPartyInfoService thirdPartyService;
    @Autowired
    private LtlPricingTerminalInfoService terminalService;
    @Autowired
    private LtlZonesSerivce zonesService;
    @Autowired
    private LtlBlockCarrGeoServicesService blockCarrService;
    @Autowired
    private LtlFuelService fuelService;
    @Autowired
    private LtlFuelSurchargeService fuelSurchargeService;
    @Autowired
    private LtlPalletPricingDetailsService palletService;
    @Autowired
    private LtlCarrierLiabilitiesService liabilitiesService;

    @Override
    public List<LtlPricingTypesEntity> getPricingTypes() {
        return ltlPricingTypesDao.getAll();
    }

    @Override
    public List<LtlPricingTypesEntity> getPricingTypesByGroup(String group) {
        return ltlPricingTypesDao.findAllByGroup(group);
    }

    @Override
    public List<LtlPricingCarrierTypesEntity> getCarrierTypes() {
        return ltlRatingCarrierTypesDao.getAll();
    }

    @Override
    public List<MileageTypesEntity> getMileageTypes() {
        return mileageTypesDao.getAll();
    }

    @Override
    public List<MileageTypesEntity> getMileageTypesByStatus(Status status) {
        return mileageTypesDao.findAllByStatus(status);
    }

    @Override
    public List<SMC3TariffsEntity> getSMC3Tariffs() {
        return smc3TariffsDao.getAll();
    }

    @Override
    public LtlPricingTypesEntity getPricingTypesByName(String name) {
        return ltlPricingTypesDao.findByName(name);
    }


    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlPricingProfileEntity saveProfile(LtlPricingProfileEntity profile) throws ValidationException {
        if (PricingType.MARGIN != profile.getLtlPricingType()) {
            if (profile.getStatus() == Status.ACTIVE) {
                validateProfile(profile);
            }
            if (PricingType.BLANKET == profile.getLtlPricingType() && profile.getApplicableCustomers() != null) {
                profile.getApplicableCustomers().clear();
            }
            setProfileCarrierInfo(profile);

            if (profile.getId() == null && PricingType.BENCHMARK != profile.getLtlPricingType()) {
                profile.setCarrierCode(ltlPricingProfileDao.getCarrierCodeSeqNum() + " " + profile.getCarrierCode());
            }
        }

        if (profile.getLtlPricingType() == PricingType.BLANKET && profile.getId() != null) {
            saveCSPProhibitedCommodities(profile);
        }

        return saveEntity(profile);
    }

    private LtlPricingProfileEntity saveEntity(LtlPricingProfileEntity entity) {
        return entity.getId() != null ? ltlPricingProfileDao.merge(entity) : ltlPricingProfileDao.saveOrUpdate(entity);
    }

    private void saveCSPProhibitedCommodities(LtlPricingProfileEntity parent) {
        List<LtlPricingProfileEntity> childs = ltlPricingProfileDao.findChildCSPByParentProfile(parent);
        for (LtlPricingProfileEntity child : childs) {
            child.setProhibitedCommodities(parent.getProhibitedCommodities());
        }

        ltlPricingProfileDao.saveOrUpdateBatch(childs);
    }

    private void validateProfile(LtlPricingProfileEntity profile) throws ValidationException {
        Map<String, ValidationError> errors = new HashMap<String, ValidationError>();
        List<LtlPricingProfileEntity> profilesByRateName =
                ltlPricingProfileDao.getActiveProfileByRateName(profile.getRateName(), profile.getId());
        if (profilesByRateName != null && !profilesByRateName.isEmpty()) {
            errors.put("rateName", ValidationError.UNIQUE);
            throw new ValidationException(errors, "Another profile exists with the name " + profile.getRateName());
        }

        validateProfileUniqueness(profile, errors);
    }

    private void validateProfileUniqueness(LtlPricingProfileEntity profile,
                                           Map<String, ValidationError> errors) throws ValidationException {
        if (PricingType.BLANKET == profile.getLtlPricingType()) {
            List<LtlPricingProfileEntity> profilesByOrgId =
                    ltlPricingProfileDao.getActiveBlanketProfileByCarrier(profile);

            if (profilesByOrgId != null && !profilesByOrgId.isEmpty()) {
                errors.put("carrierOrgId, pricingType", ValidationError.UNIQUE);
                throw new ValidationException(errors, "A Blanket profile exists for this carrier. Please edit the other profile.");
            }
        } else if (PricingType.BENCHMARK == profile.getLtlPricingType()) {
            System.out.println("YET to Add Validation Logic ");
            //            List<LtlPricingProfileEntity> profilesByOrgId =
            //                    ltlPricingProfileDao.getActiveBlanketProfileByCarrier(profile);
            //
            //            if (profilesByOrgId != null && !profilesByOrgId.isEmpty()) {
            //                errors.put("carrierOrgId, pricingType", ValidationError.UNIQUE);
            //                throw new ValidationException(errors, "A Blanket profile exists for this carrier. Please edit the other profile.");
            //            }
        } else {
            boolean duplicateProfileExists = ltlPricingProfileDao.isDuplicateProfileExists(profile);

            if (duplicateProfileExists) {
                errors.put("applicableCustomers.customer.id", ValidationError.UNIQUE);
                throw new ValidationException(errors, "Another profile exists for this Customer. Please edit the other profile.");
            }
        }
    }

    private void setProfileCarrierInfo(LtlPricingProfileEntity profile) {
        if (profile != null && profile.getProfileDetails() != null && profile.getProfileDetails().size() > 0) {
            for (LtlPricingProfileDetailsEntity details : profile.getProfileDetails()) {
                if ("API".equalsIgnoreCase(details.getCarrierType())) {
                    details.setMileageType(null);
                    details.setMileageVersion(null);
                    details.setSmc3Carrier(null);
                    details.setSmc3Tariff(null);
                    details.setMScale(null);

                } else if (SMC3.equalsIgnoreCase(details.getCarrierType())) {
                    details.setCarrierAPIDetails(null);

                } else if ("PALLET".equalsIgnoreCase(details.getCarrierType())) {
                    details.setCarrierAPIDetails(null);
                    details.setSmc3Carrier(null);
                    details.setSmc3Tariff(null);
                }
            }
        }
    }

    @Override
    public LtlPricingProfileEntity getProfileById(Long profileId) {
        return ltlPricingProfileDao.find(profileId);
    }

    @Override
    public List<LtlPricingBlockedCustomersEntity> getExplicitlyBlockedCustomersByProfileId(Long profileId) {
        return ltlPricingBlockedCustomersDao.getExplicitlyBlockedCustomersByProfileId(profileId);
    }

    @Override
    public List<LtlPricingApplicableCustomersEntity> getApplicableCustomersByProfileId(Long profileId) {
        return ltlPricingApplicableCustomersDao.getApplicableCustomersByProfileId(profileId);
    }

    @Override
    public List<String> getApplicableCustomersBySMC3TariffName(String tariffName) {
        return ltlPricingApplicableCustomersDao.findApplicableCustomersBySMC3TariffName(tariffName);
    }

    @Override
    public LtlPricingProfileLookupValuesVO getLookupValues(Long profileId) {
        LtlPricingProfileEntity profile = getProfileById(profileId);
        if (profile.getCarrierOrganization() != null) {
            LtlPricingProfileLookupValuesVO profileLookupValuesVO = new LtlPricingProfileLookupValuesVO();
            profileLookupValuesVO.setSelectedCarrierAPIDetails(
                    organizationService.getCarrierAPIDetailsByOrgId(profile.getCarrierOrganization().getId()));
        }
        return null;
    }

    @Override
    public LtlPricingProfileLookupValuesVO getDefaultLookupValues()
            throws Exception {
        //TODO fill availableCarrierRequestVO
        AvailableCarrierRequestVO availableCarrierRequestVO = new AvailableCarrierRequestVO();
        availableCarrierRequestVO.setServiceMethod("LTL");
        LtlPricingProfileLookupValuesVO defaultValuesVO = new LtlPricingProfileLookupValuesVO();
        List<LtlPricingTypesEntity> pricingTypes = this.getPricingTypesByGroup("CARRIER");
        pricingTypes.add(getPricingTypesByName("BENCHMARK"));
        defaultValuesVO.setPricingTypes(pricingTypes);
        defaultValuesVO.setCarrierTypes(getCarrierTypes());
        defaultValuesVO.setMileageTypes(getMileageTypesByStatus(Status.ACTIVE));
        defaultValuesVO.setMscaleValues(ltlLookupValueDao.findLookupValuesbyGroup("MSCALE"));
        defaultValuesVO.setSmc3Tariffs(smc3Service.getAvailableTariffs());
        defaultValuesVO.setSmc3Carriers(smc3Service.getAvailableCarriers(availableCarrierRequestVO));
        return defaultValuesVO;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<LtlPricingProfileVO> inactivateProfiles(GetRatesCO criteria, List<Long> profileIds, Boolean isActiveList) {
        LOG.info("Inactivating Profiles .... ");

        for (Long profileId : profileIds) {
            inactivateProfile(profileId);
        }

        return getTariffsBySelectedCriteria(criteria);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlPricingProfileEntity inactivateProfile(Long profileId) {
        ltlPricingProfileDao.updateStatuses(Arrays.asList(profileId), Status.INACTIVE);

        //If profile is blanket profile, inactivate all blanket/csp profiles copied from this blanket profile
        //ltlPricingProfileDao.inactivateCopiedProfiles(profileId);

        return getProfileById(profileId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<LtlPricingProfileVO> reactivateProfiles(GetRatesCO criteria, List<Long> profileIds) throws ValidationException {
        LOG.info("Re-activating Profiles .... ");

        for (Long profileId : profileIds) {
            LtlPricingProfileEntity profile = getProfileById(profileId);
            profile.setStatus(Status.ACTIVE);
            validateProfile(profile);
            ltlPricingProfileDao.updateStatuses(Arrays.asList(profileId), Status.ACTIVE);
        }

        return getTariffsBySelectedCriteria(criteria);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlPricingProfileEntity reactivateProfile(Long profileId) {
        ltlPricingProfileDao.updateStatuses(Arrays.asList(profileId), Status.ACTIVE);
        return getProfileById(profileId);
    }

    @Override
    public List<LtlPricingProfileVO> getTariffsBySelectedCriteria(GetRatesCO criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("Get Rates criteria object cannot be null");
        }
        List<LtlPricingProfileVO> tariffs = ltlPricingProfileDao.getTariffsBySelectedCriteria(criteria);

        if (isFetchSMC3(criteria)) {
            try {
                List<DataModuleVO> listOfSMC3TariffsDetails = smc3Service.getAvailableTariffs();
                tariffs.addAll(addSMC3Tariffs(listOfSMC3TariffsDetails));
            } catch (Exception e) {
                LOG.error("An error occured while trying to get available tariffs from SMC3 Servise", e);
            }
        }
        return tariffs;
    }

    @Override
    public Set<LtlCopyProfileVO> getProfilesToCopy(GetRatesCO criteria) throws EntityNotFoundException {
        List<LtlPricingProfileEntity> profiles = ltlPricingProfileDao.getProfilesToCopy(criteria);

        Set<LtlCopyProfileVO> copyProfiles = new HashSet<LtlCopyProfileVO>();

        for (LtlPricingProfileEntity profile : profiles) {
            LtlCopyProfileVO copyProfile = new LtlCopyProfileVO();
            if (criteria.getProhibitedNLiabilities() != null && criteria.getProhibitedNLiabilities()) {
                copyProfile.setId(profile.getId());
                copyProfile.setRateName(profile.getRateName());
                copyProfiles.add(copyProfile);
            } else {
                if (profile.getProfileDetails() != null && profile.getProfileDetails().size() > 0) {
                    Long id = profile.getProfileDetails().iterator().next().getId();
                    copyProfile.setId(id);
                    copyProfile.setRateName(profile.getRateName());
                    copyProfiles.add(copyProfile);
                }
            }
        }

        return copyProfiles;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlCustomerPricingVO saveProfilesForCustomer(LtlCustomerPricingVO customerPricing) {
        if (customerPricing.getOrgPricing() != null) {
            if (customerPricing.getOrgPricing().getId() == null) {
                customerPricing.getOrgPricing().setId(customerPricing.getOrgId());
            }
            if (customerPricing.getOrgPricing().getGainshare().equals(StatusYesNo.NO)) {
                customerPricing.getOrgPricing().setGsPlsPct(BigDecimal.ZERO);
                customerPricing.getOrgPricing().setGsCustPct(BigDecimal.ZERO);
            }
        }
        orgPricingDao.saveOrUpdate(customerPricing.getOrgPricing());
        ltlPricingProfileDao.saveProfilesForCustomer(customerPricing.getPricingProfiles());
        return getCustomerPricingProfiles(customerPricing.getOrgId());
    }

    @Override
    public LtlCustomerPricingVO getCustomerPricingProfiles(Long id) {
        LtlCustomerPricingVO custPricingVO = new LtlCustomerPricingVO();
        OrganizationEntity orgEntity = organizationDao.find(id);
        OrganizationPricingEntity orgPricingEntity = orgPricingDao.find(id);

        custPricingVO.setOrgId(orgEntity.getId());
        custPricingVO.setCustomerName(orgEntity.getName());
        custPricingVO.setIsGoShipBusinessUnit(orgEntity.getNetworkId().equals(businessUnitId));

        if (orgPricingEntity != null && orgPricingEntity.getId() != null) {
            custPricingVO.setOrgPricing(orgPricingEntity);
        }
        custPricingVO.setPricingProfiles(ltlPricingProfileDao.getPricingProfilesForCustomer(id));
        return custPricingVO;
    }

    /**
     * Get a copy of the profile to display on the screen so that user can make changes and save the same.
     *
     * @param copyFromProfileId - The profile id from which the profile should be copied
     * @return the copied Profile
     */
    @Override
    public LtlPricingProfileEntity getUnsavedProfileCopy(Long copyFromProfileId) {
        return ltlPricingProfileDao.getUnsavedProfileCopy(copyFromProfileId);
    }

    /**
     * Save the copied profile. While saving copied profile, should clone and save all other tabs information also.
     *
     * @param copiedProfile - The profile that is copied by calling getUnsavedProfileCopy method
     * @return the saved Profile
     * @throws ValidationException - validates data and throws exception if not meeting the required validations.
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LtlPricingProfileEntity saveCopiedProfile(LtlPricingProfileEntity copiedProfile) throws ValidationException {
        LtlPricingProfileEntity newProfile = this.saveProfile(copiedProfile);
        LtlPricingProfileEntity copiedFromProfile = ltlPricingProfileDao.find(copiedProfile.getCopiedFromProfileId());
        newProfile.setCopiedFrom(copiedFromProfile.getId());
        liabilitiesService.cloneLiabilities(copiedFromProfile.getId(), newProfile.getId());

        Iterator<LtlPricingProfileDetailsEntity> itr = copiedFromProfile.getProfileDetails().iterator();

        for (LtlPricingProfileDetailsEntity profileDetails : newProfile.getProfileDetails()) {
            Long copiedFromProfDetailsId = itr.next().getId();
            Long copyToProfDetailsId = profileDetails.getId();
            if (newProfile.getLtlPricingType() != PricingType.BLANKET_CSP) {
                pricingDetailsService.copyFrom(copiedFromProfDetailsId, copyToProfDetailsId);
            }
            accessorialService.cloneAccessorials(copiedFromProfDetailsId, copyToProfDetailsId, false);
            guaranteedService.copyFrom(copiedFromProfDetailsId, copyToProfDetailsId, false);
            thirdPartyService.copyFrom(copiedFromProfDetailsId, copyToProfDetailsId, false);
            terminalService.copyFrom(copiedFromProfDetailsId, copyToProfDetailsId, false);
            zonesService.cloneLTLZones(copiedFromProfDetailsId, copyToProfDetailsId, false);
            blockCarrService.cloneBlockedCarrierGeoServices(copiedFromProfDetailsId, copyToProfDetailsId, false);
            fuelService.copyFrom(copiedFromProfDetailsId, copyToProfDetailsId, false);
            fuelSurchargeService.copyFrom(copiedFromProfDetailsId, copyToProfDetailsId, false);
            palletService.copyFrom(copiedFromProfDetailsId, copyToProfDetailsId);
        }
        return newProfile;
    }

    /**
     * Returns customer margin pricing profile entity found by org id.
     *
     * @param orgId The customer org ID to retrieve margin profile.
     * @return entity or <null>
     */
    public LtlPricingProfileEntity getCustomerMarginProfileByOrgId(Long orgId) {
        return ltlPricingProfileDao.findMarginProfileByOrgId(orgId);
    }

    private static List<LtlPricingProfileVO> addSMC3Tariffs(List<DataModuleVO> listOfDetails) {
        List<LtlPricingProfileVO> smc3ProfilesList = new ArrayList<LtlPricingProfileVO>();

        listOfDetails.forEach(detail -> {
            LtlPricingProfileVO profileVO = new LtlPricingProfileVO();
            profileVO.setPricingType(SMC3);
            profileVO.setRateName(detail.getDescription());
            profileVO.setSmc3TariffName(detail.getTariffName());
            profileVO.setCarrierName(null);
            profileVO.setScac(null);
            smc3ProfilesList.add(profileVO);
        });
        return smc3ProfilesList;
    }

    private static boolean isFetchSMC3(GetRatesCO criteria) {
        return criteria.getPricingTypes().contains(PricingType.SMC3) && criteria.getPricingGroup().equals("CARRIER")
                && criteria.getFromDate() == null && criteria.getToDate() == null && criteria.getStatus() == Status.ACTIVE;
    }

}
