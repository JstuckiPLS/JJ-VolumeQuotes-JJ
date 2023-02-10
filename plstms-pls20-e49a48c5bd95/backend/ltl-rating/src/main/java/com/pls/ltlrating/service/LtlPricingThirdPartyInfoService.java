package com.pls.ltlrating.service;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;

/**
 * Service that handle business logic and for Ltl Third Party Info.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPricingThirdPartyInfoService {

    /**
     * The Save operation should return the updated data (succes or roll back) along with other field values -
     * primary key, date created, created by, date modified, modified by, version.
     *
     * @param entity
     *            Not <code>null</code> entity to save {@link LtlPricingThirdPartyInfoEntity}.
     * @return {@link LtlPricingThirdPartyInfoEntity} saved entity.
     * @throws ValidationException
     *             Validation Exception.
     */
    LtlPricingThirdPartyInfoEntity saveThirdPartyInfo(LtlPricingThirdPartyInfoEntity entity) throws ValidationException;

    /**
     * Get {@link LtlPricingThirdPartyInfoEntity} by primary key.
     *
     * @param id
     *            Not <code>null</code> {@link Long}.
     * @return {@link LtlPricingThirdPartyInfoEntity}
     */
    LtlPricingThirdPartyInfoEntity getThirdPartyInfoById(Long id);

    /**
     * Get {@link LtlPricingThirdPartyInfoEntity} by profile detail id.
     *
     * @param profileDetailId
     *            Not <code>null</code> {@link Long}
     * @return {@link LtlPricingThirdPartyInfoEntity}
     */
    LtlPricingThirdPartyInfoEntity getActiveThirdPartyInfoByProfileDetailId(Long profileDetailId);

    /**
     * Get {@link LtlPricingThirdPartyInfoEntity} by specified profile id.
     *
     * @param profileId -
     *          Not <code>null</code> {@link Long}
     * @return {@link LtlPricingThirdPartyInfoEntity}
     */
    LtlPricingThirdPartyInfoEntity getActiveByProfileId(Long profileId);

    /**
     * Copying {@link LtlPricingThirdPartyInfoEntity} from profile specified by copyFromProfileId argument to
     * profile specified by copyToProfileId.
     *
     * @param copyFromProfileId
     *            Not <code>null</code> value of {@link Long}
     * @param copyToProfileId
     *            Not <code>null</code> value of {@link Long}
     * @param shouldCopyChilds
     *            Should copying to child CSP profiles
     */
    void copyFrom(Long copyFromProfileId, Long copyToProfileId, boolean shouldCopyChilds);
}
