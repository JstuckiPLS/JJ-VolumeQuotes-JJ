package com.pls.ltlrating.service;

import java.util.List;
import java.util.Set;

import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingBlockedCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingCarrierTypesEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.LtlPricingTypesEntity;
import com.pls.ltlrating.domain.MileageTypesEntity;
import com.pls.ltlrating.domain.SMC3TariffsEntity;
import com.pls.ltlrating.shared.GetRatesCO;
import com.pls.ltlrating.shared.LtlCopyProfileVO;
import com.pls.ltlrating.shared.LtlCustomerPricingVO;
import com.pls.ltlrating.shared.LtlPricingProfileLookupValuesVO;
import com.pls.ltlrating.shared.LtlPricingProfileVO;

/**
 * pricing profile details service.
 *
 * @author Aleksandr Leshchenko
 */
public interface LtlProfileDetailsService {

    /**
     * Get list of {@link LtlPricingTypesEntity}.
     *
     * @return list of {@link LtlPricingTypesEntity}
     */
    List<LtlPricingTypesEntity> getPricingTypes();

    /**
     * Get list of {@link LtlPricingTypesEntity} by group.
     *
     * @param group - Customer/Carrier
     * @return list of {@link LtlPricingTypesEntity}
     */
    List<LtlPricingTypesEntity> getPricingTypesByGroup(String group);

    /**
     * Get {@link LtlPricingTypesEntity} by name.
     *
     * @param name of Pricing Type
     * @return {@link LtlPricingTypesEntity}
     */
    LtlPricingTypesEntity getPricingTypesByName(String name);

    /**
     * Get list of {@link LtlPricingCarrierTypesEntity}.
     *
     * @return list of {@link LtlPricingCarrierTypesEntity}
     */
    List<LtlPricingCarrierTypesEntity> getCarrierTypes();

    /**
     * Get list of {@link MileageTypesEntity}.
     *
     * @return list of {@link MileageTypesEntity}
     */
    List<MileageTypesEntity> getMileageTypes();

    /**
     * Get list of {@link MileageTypesEntity}.
     *
     * @param status - Active/Inactive
     * @return list of {@link MileageTypesEntity}
     */
    List<MileageTypesEntity> getMileageTypesByStatus(Status status);

    /**
     * Get list of {@link SMC3TariffsEntity}.
     *
     * @return list of {@link SMC3TariffsEntity}
     */
    List<SMC3TariffsEntity> getSMC3Tariffs();

    /**
     * Saves or updates pricing profile.
     *
     * @param profile {@link LtlPricingProfileEntity} to save/update
     * @return persisted {@link LtlPricingProfileEntity}
     * @throws ValidationException validation exceptions
     */
    LtlPricingProfileEntity saveProfile(LtlPricingProfileEntity profile) throws ValidationException;

    /**
     * Returns pricing profile entity found by id.
     *
     * @param profileId identifier
     * @return entity or <null>
     */
    LtlPricingProfileEntity getProfileById(Long profileId);

    /**
     * Returns names of customers which are using specified SMC3 tariff.
     *
     * @param tariffName
     *            smc3 tariff name
     * @return list of Customer names
     */
    List<String> getApplicableCustomersBySMC3TariffName(String tariffName);

    /**
     * Returns explicitly blocked customers for pricing profile.
     *
     * @param profileId pricing profile identifier
     * @return list of {@link LtlPricingBlockedCustomersEntity}
     */
    List<LtlPricingBlockedCustomersEntity> getExplicitlyBlockedCustomersByProfileId(Long profileId);

    /**
     * Returns applicable customers for pricing profile.
     * In Library module, we have to display the list of customers associated to the Profile
     * when user selects the profile in the respective grid.
     *
     * @param profileId pricing profile identifier
     * @return list of {@link LtlPricingApplicableCustomersEntity}
     */
    List<LtlPricingApplicableCustomersEntity> getApplicableCustomersByProfileId(Long profileId);

    /**
     * Returns all lookup values required for the selected Profile.
     *
     * @param profileId pricing profile identifier
     * @return LtlPricingProfileLookupValuesVO
     */
    LtlPricingProfileLookupValuesVO getLookupValues(Long profileId);

    /**
     * Returns all lookup values required when creating new profile.
     *
     * @return LtlPricingProfileLookupValuesVO
     * @throws Exception - exception thrown from the SMC3.
     */
    LtlPricingProfileLookupValuesVO getDefaultLookupValues()
            throws Exception;

    /**
     * Deactivates pricing profiles.
     *
     * @param criteria The criteria used to get the updated result
     * @param profileIds pricing profile identifiers
     * @param isActiveList To return active list or expired list
     * @return list of active or expired profiles based on the boolean flag
     */
    List<LtlPricingProfileVO> inactivateProfiles(GetRatesCO criteria, List<Long> profileIds, Boolean isActiveList);

    /**
     * Deactivates/archives pricing profile.
     *
     * @param profileId profile id
     * @return updated entity
     */
    LtlPricingProfileEntity inactivateProfile(Long profileId);

    /**
     * Activates inactive/archived pricing profiles.
     *
     * @param criteria The criteria used to get the updated result
     * @param profileIds profiles identifier
     * @return list of activated profiles
     * @throws ValidationException validation exceptions
     */
    List<LtlPricingProfileVO> reactivateProfiles(GetRatesCO criteria, List<Long> profileIds) throws ValidationException;

    /**
     * Activates pricing profile.
     *
     * @param profileId profile identifier
     * @return updated entity
     */
    LtlPricingProfileEntity reactivateProfile(Long profileId);

    /**
     * Finds all pricing profiles tariffs.
     *
     * @param criteria aggregates filters
     * @return list
     */
    List<LtlPricingProfileVO> getTariffsBySelectedCriteria(GetRatesCO criteria);

    /**
     * Get all profiles to display in the Copy From dropdown. This contains ProfileDetailId and RateName.
     *
     * @param getRates to get Rates
     * @return Set of Copy Profile VOs
     * @throws EntityNotFoundException when entity was not found.
     */
    Set<LtlCopyProfileVO> getProfilesToCopy(GetRatesCO getRates) throws EntityNotFoundException;

    /**
     * Get all profiles to display in the Copy From dropdown. This contains ProfileDetailId and RateName.
     *
     * @param customerPricing - CustomerPricing VO that contains information about org changes for pricing and
     *                          List of customer profiles that need to be updated
     * @return customerPricing - CustomerPricing VO that contains information about org changes for pricing and
     *                          List of customer profiles that need to be updated
     */
    LtlCustomerPricingVO saveProfilesForCustomer(LtlCustomerPricingVO customerPricing);

    /**
     * Returns list of carrier pricing profiles for the given customer.
     *
     * @param id - Org Id of the customer
     * @return customerPricing - CustomerPricing VO that contains information about org changes for pricing and
     *                          List of customer profiles that need to be updated
     */
    LtlCustomerPricingVO getCustomerPricingProfiles(Long id);

    /**
     * Get a copy of the profile to display on the screen so that user can make changes and save the same.
     *
     * @param copyFromProfileId - The profile id from which the profile should be copied
     * @return the copied Profile
     */
    LtlPricingProfileEntity getUnsavedProfileCopy(Long copyFromProfileId);

    /**
     * Returns customer margin pricing profile entity found by org id.
     *
     * @param orgId The customer org ID to retrieve margin profile.
     * @return entity or <null>
     */
    LtlPricingProfileEntity getCustomerMarginProfileByOrgId(Long orgId);

    /**
     * Save the copied profile. While saving copied profile, should clone and save all other tabs information also.
     *
     * @param copiedProfile - The profile that is copied by calling getUnsavedProfileCopy method
     * @return the saved Profile
     * @throws ValidationException - validates data and throws exception if not meeting the required validations.
     */
    LtlPricingProfileEntity saveCopiedProfile(LtlPricingProfileEntity copiedProfile) throws ValidationException;
}
