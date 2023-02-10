package com.pls.ltlrating.service;

import java.util.List;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlZonesEntity;
import com.pls.ltlrating.domain.bo.ZonesListItemVO;

/**
 * Service that handle business logic for Ltl Zones.
 *
 * @author Artem Arapov
 *
 */
public interface LtlZonesSerivce {

    /**
     * Save {@link LtlZonesEntity}.
     *
     * @param entity
     *            Not <code>null</code> instance of {@link LtlZonesEntity} which should be saved.
     * @return persisted object of {@link LtlZonesEntity} class.
     * @throws ValidationException
     *             validation exceptions
     */
    LtlZonesEntity saveLTLZone(LtlZonesEntity entity) throws ValidationException;

    /**
     * Get {@link LtlZonesEntity} object by primary key id.
     *
     * @param id
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlZonesEntity} if it was found, otherwise return
     *         <code>null</code>
     */
    LtlZonesEntity getLTLZoneById(Long id);

    /**
     * Get {@link LtlZonesEntity} object by primary key profile id.
     *
     * @param profileDetailId
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlZonesEntity} if it was found, otherwise return <code>null</code>
     */
    List<LtlZonesEntity> getAllLTLZonesByProfileDetailId(Long profileDetailId);

    /**
     * Get list of {@link ZonesListItemVO} with inactive status by specified profile id.
     *
     * @param profileDetailId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link} of {@link ZonesListItemVO}
     * */
    List<ZonesListItemVO> getInactiveLTLZonesByProfileDetailId(Long profileDetailId);

    /**
     * Get list of {@link ZonesListItemVO} with active status by specified profile id.
     *
     * @param profileDetailId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link} of {@link ZonesListItemVO}
     * */
    List<ZonesListItemVO> getActiveLTLZonesByProfileDetailId(Long profileDetailId);

    /**
     * Get list of {@link LtlZonesEntity} with active status by specified profile id.
     *
     * @param profileDetailId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link} of {@link LtlZonesEntity}
     * */
    List<LtlZonesEntity> getActiveZoneEntitiesForProfile(Long profileDetailId);


    /**
     * Change status on inactive of {@link ZonesListItemVO} with specified list of id's and profile id.
     *
     * @param ids
     *            Not<code>null</code> instance of {@link List} with id of {@link LtlZonesEntity}.
     * @param profileDetailId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link ZonesListItemVO} - remaining entities with active status.
     */
    List<ZonesListItemVO> inactivateLTLZones(List<Long> ids, Long profileDetailId);

    /**
     * Change status on active of {@link ZonesListItemVO} with specified list of id's and profile id.
     *
     * @param ids
     *            Not<code>null</code> instance of {@link List} with id of {@link LtlZonesEntity}.
     * @param profileDetailId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link ZonesListItemVO} - remaining entities with inactive status.
     */
    List<ZonesListItemVO> reactivateLTLZones(List<Long> ids, Long profileDetailId);

    /**
     * Clone the Zones from "copyFromProfileDetailId" to "copyToProfileDetailId" and save the same.
     *
     * @param copyFromProfileDetailId
     *            - The profile detail id from which the Zones should be copied
     * @param copyToProfileDetailId
     *            - The profile detail id to which the Zones should be copied
     * @param shouldCopyChilds
     *            - Should copying to child CSP profiles
     */
    void cloneLTLZones(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyChilds);
}
