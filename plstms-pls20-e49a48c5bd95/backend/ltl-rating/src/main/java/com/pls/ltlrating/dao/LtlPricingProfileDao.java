package com.pls.ltlrating.dao;

import java.math.BigInteger;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.shared.GetRatesCO;
import com.pls.ltlrating.shared.LtlCopyProfileVO;
import com.pls.ltlrating.shared.LtlCustomerPricingProfileVO;
import com.pls.ltlrating.shared.LtlPricingProfileVO;

/**
 * DAO for {@link LtlPricingProfileEntity}.
 *
 * @author Mikhail Boldinov, 22/02/13
 */
public interface LtlPricingProfileDao extends AbstractDao<LtlPricingProfileEntity, Long> {

    /**
     * Get the next sequence number for auto-generation of Carrier Code.
     * @return seq number.
     */
    Long getCarrierCodeSeqNum();

    /**
     * Gets list of tariffs by {@link GetRatesCO}.
     *
     * @param getRates the {@link GetRatesCO} search criteria
     * @return list of found entities
     */
    List<LtlPricingProfileVO> getTariffsBySelectedCriteria(GetRatesCO getRates);

    /**
     * Gets list of {@link LtlCopyProfileVO} by {@link GetRatesCO}.
     *
     * @param getRates the {@link GetRatesCO} search criteria to copy the rates
     * @return list of found entities
     * @throws EntityNotFoundException when entity was not found.
     */
    List<LtlPricingProfileEntity> getProfilesToCopy(GetRatesCO getRates) throws EntityNotFoundException;

    /**
     * Returns list of carrier pricing profiles for the given customer.
     *
     * @param shipperOrgId - Org Id of the customer
     * @return list of {@link LtlCustomerPricingProfileVO} items
     */
    List<LtlCustomerPricingProfileVO> getPricingProfilesForCustomer(Long shipperOrgId);

    /**
     * Save profiles.
     *
     * @param profiles
     *            - List of customer profiles that need to be updated
     */
    void saveProfilesForCustomer(List<LtlCustomerPricingProfileVO> profiles);

    /**
     * Get a copy of the profile to display on the screen so that user can make changes and save the same.
     *
     * @param copyFromProfileId - The profile id from which the profile should be copied
     * @return copied profile
     */
    LtlPricingProfileEntity getUnsavedProfileCopy(Long copyFromProfileId);

    /**
     * Method to check if duplicate profiles are available for the given carrier and customer combination.
     *
     * @param profile
     *            - The profile that needs to be validated.
     * @return <code>true</code> if duplicate profile available. <code>false</code> otherwise
     */
    boolean isDuplicateProfileExists(LtlPricingProfileEntity profile);

    /**
     * Method to get active profiles by Rate Name.
     * @param rateName - the rate name for which profiles need to be retrieved.
     * @param profileId - the profile for which the rate name should not be checked.
     * @return - list of active profiles.
     */
    List<LtlPricingProfileEntity> getActiveProfileByRateName(String rateName, Long profileId);

    /**
     * Method to get active profiles by Carrier Org id. This looks for only Blanket profiles.
     * @param profile - The profile that needs to be validated.
     * @return - list of active profiles.
     */
    List<LtlPricingProfileEntity> getActiveBlanketProfileByCarrier(LtlPricingProfileEntity profile);

    /**
     * Returns customer margin pricing profile entity found by org Id.
     *
     * @param orgId The customer org ID to retrieve margin profile.
     * @return entity or <null>
     */
    LtlPricingProfileEntity findMarginProfileByOrgId(Long orgId);

    /**
     * Returns ID of Pricing Details for customer margin pricing profile entity found by org Id.
     *
     * @param orgId
     *            The customer org ID.
     * @return ID or <null>
     */
    Long findMarginProfileDetailIdByOrgId(Long orgId);

    /**
     * Returns {@link LtlPricingProfileEntity#getLtlPricingType()} for specified
     * {@link LtlPricingProfileDetailsEntity#getId()}.
     *
     * @param profileDetailId
     *            - id of {@link LtlPricingProfileDetailsEntity}.
     * @return - pricing profile value.
     */
    String findPricingTypeByProfileDetailId(Long profileDetailId);

    /**
     * Returns {@link LtlPricingProfileEntity#getLtlPricingType()} for specified
     * {@link LtlPricingProfileEntity#getId()}.
     *
     * @param profileId
     *            - id of {@link LtlPricingProfileEntity}.
     * @return - pricing profile value.
     */
    String findPricingTypeByProfileId(Long profileId);

    /**
     * Returns List of child profiles by specified Profile Detail Id.
     *
     * @param parentProfileDetailId
     *            - id of parent {@link LtlPricingProfileDetailsEntity}.
     * @return list of {@link LtlPricingProfileEntity}.
     */
    List<LtlPricingProfileEntity> findChildCSPByProfileDetailId(Long parentProfileDetailId);

    /**
     * Returns List of child profiles by specified Profile Id.
     *
     * @param profileId
     *            - id of {@link LtlPricingProfileEntity}.
     * @return list of {@link Long} ids.
     */
    List<Long> findChildCSPByProfileId(Long profileId);

    /**
     * Returns List of {@link LtlPricingProfileEntity} by specified parent entity.
     *
     * @param parent
     *            - parent profile.
     * @return List of {@link LtlPricingProfileEntity}.
     */
    List<LtlPricingProfileEntity> findChildCSPByParentProfile(LtlPricingProfileEntity parent);

    /**
     * Returns List of CSP profileDetailId which was created from specified.
     *
     * @param parentDetailId
     *            - parent profile detail id.
     * @return List of {@link BigInteger}
     */
    List<BigInteger> findChildCSPDetailByParentDetailId(Long parentDetailId);

    /**
     * Returns Active {@link LtlPricingApplicableCustomersEntity} by specified profile Id.
     *
     * @param profileId
     *            id of {@link LtlPricingProfileEntity}.
     * @return {@link LtlPricingApplicableCustomersEntity} entity.
     */
    LtlPricingApplicableCustomersEntity findActivePricingApplicableCustomer(Long profileId);

    /**
     * Updates status in {@link LtlPricingProfileEntity}.
     *
     * @param profileIds
     *              Ids of profiles.
     * @param status
     *              Value of {@link com.pls.core.shared.Status} enumeration.
     *              Not <code>null</code>.
     */
    void updateStatuses(List<Long> profileIds, Status status);

    /**
     * Get ID's of Blanket type profiles for specified Carriers.
     * 
     * @param carriersIDs
     *            carrier's IDs
     * @return {@link List} of Blanket profiles IDs
     */
    List<Long> getBlanketProfileIDsForCarriers(List<Long> carriersIDs);
}
