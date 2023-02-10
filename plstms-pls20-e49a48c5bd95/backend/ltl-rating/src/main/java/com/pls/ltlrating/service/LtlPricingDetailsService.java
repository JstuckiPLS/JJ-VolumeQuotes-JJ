package com.pls.ltlrating.service;

import java.util.List;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.bo.PricingDetailListItemVO;

/**
 * Service that handle business logic and for Ltl Pricing Details.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPricingDetailsService {

    /**
     * This method is for both create and update operations. The Save operation should return the updated data
     * (succes or roll back) along with other field values - primary key, date created, created by, date
     * modified, modified by, version and use the same to populate the screen and this should be done by
     * calling getPricingDetailById(Long id) method instead of duplicating logic. This is required especially
     * for pessimistic locking. Note that we will save one "Pricing Detail" at a time.
     *
     * @param entity
     *            Not <code>null</code> instance of {@link LtlPricingDetailsEntity} which should be saved.
     * @return persisted object of {@link LtlPricingDetailsEntity} class.
     * @throws ValidationException
     *             validation exceptions
     */
    LtlPricingDetailsEntity savePricingDetail(LtlPricingDetailsEntity entity) throws ValidationException;

    /**
     * Get {@link LtlPricingDetailsEntity} object by pricing detail id.
     *
     * @param id
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlPricingDetailsEntity} if it was found, otherwise return <code>null</code>
     */
    LtlPricingDetailsEntity getPricingDetailById(Long id);

    /**
     * To retrieve Pricing Details for editing/viewing in Module 4 and Viewing in Module 6 - retrieve all
     * active and effective (LOCALTIMESTAMP <= expdate) pricing details for selected profile and display them on the
     * grid in the screen - in the active tab.
     *
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link PricingDetailListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<PricingDetailListItemVO> getActivePricingDetailsByProfileDetailId(Long profileDetailId);

    /**
     * To retrieve Pricing Details for viewing in Module 4 and Module 6 - retrieve all inactive pricing
     * details for selected profile and display them on the grid in the screen - in the archive tab.
     *
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link PricingDetailListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<PricingDetailListItemVO> getInactivePricingDetailsByProfileDetailId(Long profileDetailId);

    /**
     * To retrieve Pricing Details for editing/viewing in Module 4 and Viewing in Module 6 - retrieve all
     * active and expired (LOCALTIMESTAMP > expdate) pricing details for selected profile and display them on the
     * grid in the screen - in the expired tab.
     *
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link PricingDetailListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<PricingDetailListItemVO> getExpiredPricingDetailsByProfileDetailId(Long profileDetailId);

    /**
     * To archive multiple pricing details. Return list of active or expired pricing details based on the
     * boolean flag "isActiveList". If flag is yes, the pricing details are picked from "Active" grid and so
     * need to return updated "Active" list using method "getActivePricingDetailsByProfileId(Long profileId);"
     * else return updated "Expired" list using method "getExpiredPricingDetailsByProfileId(Long profileId);"
     * method. In UI, "Edit", "Copy From" and "Archive" fields should be disabled when user selects "Archived"
     * tab.
     *
     * @param pricingDetailIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlPricingDetailsEntity} which should be saved.
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @param isActiveList
     *            Not <code>null</code> instance of {@link Boolean}.
     * @return {@link List} of {@link PricingDetailListItemVO}
     */
    List<PricingDetailListItemVO> inactivatePricingDetails(List<Long> pricingDetailIds, Long profileDetailId,
            Boolean isActiveList);

    /**
     * To reactivate multiple pricing details when the user is in Archived tab. Return updated list of
     * archived pricing details using method "getInactivePricingDetailsByProfileId(Long profileId);". In UI,
     * "Edit", "Copy From" fields should be disabled when user selects "Archived" tab and "Archive" button
     * should be changed to "Re-activate". This is missing in UI and should be added to allow the user to have
     * the flexibility of correcting their mistakes
     *
     * @param pricingDetailIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlPricingDetailsEntity} which should be saved.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link PricingDetailListItemVO}
     */
    List<PricingDetailListItemVO> reactivatePricingDetails(List<Long> pricingDetailIds, Long profileDetailId);

    /**
     * Makes specified records Expired.
     *
     * @param pricingDetailIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlPricingDetailsEntity} which should be expired.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link PricingDetailListItemVO}
     */
    List<PricingDetailListItemVO> expiratePricingDetails(List<Long> pricingDetailIds, Long profileDetailId);

    /**
     * Copying list of {@link LtlPricingDetailsEntity} from profile specified by copyFromProfileId argument to
     * profile specified by copyToProfileId.
     *
     * @param copyFromProfileId
     *            Not <code>null</code> value of {@link Long}
     * @param copyToProfileId
     *            Not <code>null</code> value of {@link Long}
     */
    void copyFrom(Long copyFromProfileId, Long copyToProfileId);
}
