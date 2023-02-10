package com.pls.ltlrating.dao;

import java.util.Date;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.DotRegionFuelEntity;
import com.pls.ltlrating.domain.LtlFuelEntity;
import com.pls.ltlrating.domain.bo.FuelListItemVO;

/**
 * Dao fro {@link LtlFuelEntity}.
 *
 * @author Stas Norochevskiy
 *
 */
public interface LtlFuelDao extends AbstractDao<LtlFuelEntity, Long> {

    /**
     * Retrieve All fuel triggers for a specific profile.
     *
     * @param profileDetailId ID of profile details
     * @return list of LtlFuelEntity object with specified profile details ID
     */
    List<LtlFuelEntity> getAllFuelTriggersByProfileDetailId(Long profileDetailId);

    /**
     * Retrieve All fuel triggers that are active and currently effective.
     *
     * @param profileDetailId ID of profile details
     * @return list of active and effective LtlFuelEntity object with specified profile details ID
     */
    List<FuelListItemVO> getActiveAndEffectiveByProfileDetailId(Long profileDetailId);

    /**
     * Retrieve All fuel triggers that are active and currently effective.
     *
     * @param profileDetailId ID of profile details
     * @return list of active and effective LtlFuelEntity object with specified profile details ID
     */
    List<LtlFuelEntity> getActiveAndEffectiveForProfile(Long profileDetailId);

    /**
     * Retrieve All fuel triggers with specified status for a specific profile.
     * @param profileDetailId ID of profile details
     * @param status ltl fuel status
     * @return list of LtlFuelEntity object with specified profile details ID and status
     */
    List<FuelListItemVO> getAllFuelTriggersByProfileDetailIdAndStatus(Long profileDetailId, Status status);

    /**
     * Retrieve All fuel triggers with expiration date less than specified.
     * @param profileDetailId profileDetailId ID of profile details
     * @param date date that should be greater than expiration date
     * @return list of expired LtlFuelEntity
     */
    List<FuelListItemVO> getFuelTriggersByProfileDetailWithExpireDateLessThan(Long profileDetailId, Date date);

    /**
     * Update status for Fuel entity with specified ID.
     * @param fuelTriggerIds primary keys of LtlFuelEntity
     * @param newStatus status to be set
     */
    void updateFuelStatus(List<Long> fuelTriggerIds, Status newStatus);

    /**
     * Retrieve all DotRegionFuelEntity objects.
     * @return all DotRegionFuelEntity objects
     */
    List<DotRegionFuelEntity> getDOTRegionsAndFuelRates();

    /**
     * Save object.
     * @param dotRegionFuelEntity entity
     * @return persisted object
     */
    DotRegionFuelEntity saveDotRegionFuelEntity(DotRegionFuelEntity dotRegionFuelEntity);

    /**
     * Inactivate all active and effective {@link LtlFuelEntity} entities by specified profileId.
     * @param profileId profile to inactive fuels
     * @param modifiedBy user who made inactivateion
     */
    void updateStatusToInactiveByProfileId(Long profileId, Long modifiedBy);

    /**
     * Makes specified records Expired.
     *
     * @param ids
     *            Not <code>null</code> {@link List} of {@link Long} with id's of {@link LtlFuelEntity} which
     *            should be expired.
     * @param modifiedBy
     *            The user who modified or inactivated/reactivated the price detail.
     */
    void expireByListOfIds(List<Long> ids, Long modifiedBy);

    /**
     * Find all {@link LtlFuelEntity} which pricingType is 'BLANKET_CSP' and was copied from specified
     * <tt>copiedFrom</tt> value.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlFuelEntity}.
     */
    List<LtlFuelEntity> findAllCspChildsCopyedFrom(Long copiedFrom);

    /**
     * Makes all child Customer Specific Prices expire.
     *
     * @param copiedFromIds
     *              Ids of parent entities from which child CSPs were copied. (It's a link to parent entity).
     *              Not <code>null</code> value of {@link Long}.
     * @param modifiedBy
     *              Not <code>null</code> value of {@link Long}.
     */
    void expirateCSPByCopiedFrom(List<Long> copiedFromIds, Long modifiedBy);

    /**
     * Updates status in child Customer Specific Price entities.
     *
     * @param copiedFromIds
     *              Ids of profile detail from which child CSPs were copied. (It's a link to parent entity).
     *              Not <code>null</code> value of {@link Long}.
     * @param status
     *              Value of {@link Status} enumeration.
     *              Not <code>null</code>.
     * @param modifiedBy
     *              Not <code>null</code> value of {@link Long}.
     */
    void updateStatusInCSPByCopiedFrom(List<Long> copiedFromIds, Status status, Long modifiedBy);

    /**
     * Updates status in child Customer Specific Price entities.
     *
     * @param profileDetailId
     *            Id of profile detail which child CSPs should be inactivated.
     * @param modifiedBy
     *            Not <code>null</code> value of {@link Long}.
     */
    void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy);
}
