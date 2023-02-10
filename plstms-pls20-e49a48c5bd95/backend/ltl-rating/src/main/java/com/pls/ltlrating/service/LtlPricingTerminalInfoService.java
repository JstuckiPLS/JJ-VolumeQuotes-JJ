package com.pls.ltlrating.service;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;

/**
 * Service that handle business logic and for Ltl Pricing Terminal Info.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPricingTerminalInfoService {

    /**
     * This method is for both create and update operations. This operation return the updated data (succes or
     * roll back) along with other field values - primary key, date created, created by, date modified,
     * modified by, version.
     *
     * @param entity
     *            Entity which should be saved.
     *
     * @return {@link LtlPricingTerminalInfoEntity}
     * @throws ValidationException
     *             Validation Exception.
     */
    LtlPricingTerminalInfoEntity saveCarrierTerminalInfo(LtlPricingTerminalInfoEntity entity)
            throws ValidationException;

    /**
     * Get {@link LtlPricingTerminalInfoEntity} by primary key.
     *
     * @param id
     *            Not <code>null</code> {@link Long}.
     * @return {@link LtlPricingTerminalInfoEntity}
     */
    LtlPricingTerminalInfoEntity getCarrierTerminalInfoById(Long id);

    /**
     * Get {@link LtlPricingTerminalInfoEntity} by profile detail id.
     *
     * @param profileDetailId
     *            Not <code>null</code> {@link Long}
     * @return {@link LtlPricingTerminalInfoEntity}
     */
    LtlPricingTerminalInfoEntity getActiveCarrierTerminalInfoByProfileDetailId(Long profileDetailId);

    /**
     * Copying {@link LtlPricingTerminalInfoEntity} from profile specified by copyFromProfileId argument to
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
