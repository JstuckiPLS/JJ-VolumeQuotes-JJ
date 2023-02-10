package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlZonesEntity;
import com.pls.ltlrating.domain.bo.ZonesListItemVO;

/**
 * Data Access Object for {@link LtlZonesEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface LtlZonesDao extends AbstractDao<LtlZonesEntity, Long> {

    /**
     * Get a list of {@link LtlZonesEntity} by specified profile id.
     *
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link LtlZonesEntity}.
     */
    List<LtlZonesEntity> findByProfileDetailId(Long profileId);

    /**
     * Check if zone name already exist for given profile.
     *
     * @param profileDetailId
     *            profile for which zones has to be looked for
     * @param name
     *            zone name whose the matching zone to be looked for
     * @return the zone with matching name in the profile
     */
    LtlZonesEntity findZoneByProfileDetailIdAndName(Long profileDetailId, String name);

    /**
     * Get a list of {@link ZonesListItemVO} by specified status and profile id.
     *
     * @param status
     *            value of {@link Status} enumeration.
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link ZonesListItemVO}.
     */
    List<ZonesListItemVO> findByStatusAndProfileId(Status status, Long profileId);

    /**
     * Get a list of active {@link LtlZonesEntity} by profile id.
     *
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link LtlZonesEntity}.
     */
    List<LtlZonesEntity> findActiveForProfile(Long profileId);


    /**
     * Update list of {@link LtlZonesEntity} by specified ids, status and profile id.
     *
     * @param ids
     *            Instance of {@link List} with primary key ids of {@link LtlZonesEntity} which should be modified
     * @param status
     *            value of {@link Status} enumeration.
     * @param modifiedBy
     *            Not <code>null</code> instance of {@link Long}
     */
    void updateStatus(List<Long> ids, Status status, Long modifiedBy);

    /**
     * To archive/inactivate all active Zones by Profile Detail ID.
     *
     * This method inactivates all active Zones and is used when cloning the Zones from another Profile.
     *
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the Zones should be inactivated
     */
    void inactivateByProfileDetailId(Long profileDetailId);

    /**
     * Find all {@link LtlZonesEntity} which pricingType is 'BLANKET_CSP' and was copied from specified <tt>copiedFrom</tt> value.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlZonesEntity}.
     */
    List<LtlZonesEntity> findAllCspChildsCopyedFrom(Long copiedFrom);

    /**
     * Updates status in child Customer Specific Price entities.
     *
     * @param copiedFromIds
     *            Ids of profile detail from which child CSPs were copied. (It's a link to parent entity). Not <code>null</code> value of {@link Long}
     *            .
     * @param status
     *            Value of {@link Status} enumeration. Not <code>null</code>.
     * @param modifiedBy
     *            Not <code>null</code> value of {@link Long}.
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

    /**
     * Returns the zone for the profile with name same as that of zone with zone id.
     *
     * @param profileDetailId
     *            profile for which zones has to be looked for
     * @param zoneId
     *            zone with whose name the matching zone to be looked for
     * @return the zone with matching name in the profile
     */
    LtlZonesEntity getMatchingZoneByName(Long profileDetailId, Long zoneId);
}
