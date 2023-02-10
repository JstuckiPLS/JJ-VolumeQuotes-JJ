package com.pls.ltlrating.service;

import java.util.List;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;
import com.pls.ltlrating.domain.bo.BlockCarrierListItemVO;

/**
 * Service that handle business logic and for Ltl Block Carrier Geo Services.
 *
 * @author Artem Arapov
 *
 */
public interface LtlBlockCarrGeoServicesService {

    /**
     * Save or update {@link LtlBlockCarrGeoServicesEntity}.
     *
     * @param entity
     *            Not <code>null</code> instance of {@link LtlBlockCarrGeoServicesEntity} which should be
     *            saved.
     * @return persisted object of {@link LtlBlockCarrGeoServicesEntity} class.
     * @throws ValidationException
     *             validation exceptions
     */
    LtlBlockCarrGeoServicesEntity saveBlockedCarrierGeoService(LtlBlockCarrGeoServicesEntity entity)
            throws ValidationException;

    /**
     * Get {@link LtlBlockCarrGeoServicesEntity} object by primary key id.
     *
     * @param id
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlBlockCarrGeoServicesEntity} if it was found, otherwise return
     *         <code>null</code>
     */
    LtlBlockCarrGeoServicesEntity getBlockedCarrierGeoServiceById(Long id);

    /**
     * Get list of {@link BlockCarrierListItemVO} with active status by specified profile id.
     *
     * @param profileDetailId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link} of {@link BlockCarrierListItemVO}
     * */
    List<BlockCarrierListItemVO> getActiveBlockedCarrGeoServByProfileDetailId(Long profileDetailId);

    /**
     * Get list of {@link BlockCarrierListItemVO} with inactive status by specified profile id.
     *
     * @param profileDetailId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link} of {@link BlockCarrierListItemVO}
     * */
    List<BlockCarrierListItemVO> getInactiveBlockedCarrGeoServByProfileDetailId(Long profileDetailId);

    /**
     * Change status on inactive of {@link LtlBlockCarrGeoServicesEntity} with specified list of id's and
     * profile id.
     *
     * @param ids
     *            Not<code>null</code> instance of {@link List} with id of
     *            {@link LtlBlockCarrGeoServicesEntity}.
     * @param profileDetailId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link BlockCarrierListItemVO} - remaining entities with active status.
     */
    List<BlockCarrierListItemVO> inactivateBlockedCarrierGeoServices(List<Long> ids, Long profileDetailId);

    /**
     * Change status on active of {@link LtlBlockCarrGeoServicesEntity} with specified list of id's and
     * profile id.
     *
     * @param ids
     *            Not<code>null</code> instance of {@link List} with id of
     *            {@link LtlBlockCarrGeoServicesEntity}.
     * @param profileDetailId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link BlockCarrierListItemVO} - remaining entities with inactive
     *         status.
     */
    List<BlockCarrierListItemVO> reactivateBlockedCarrierGeoServices(List<Long> ids, Long profileDetailId);

    /**
     * Clone the Block Carrier Geo Services from "copyFromProfileDetailId" to "copyToProfileDetailId" and save
     * the same.
     *
     * @param copyFromProfileDetailId
     *            - The profile detail id from which the Block Carrier Geo Services should be copied
     * @param copyToProfileDetailId
     *            - The profile detail id to which the Block Carrier Geo Services should be copied
     * @param shouldCopyToCSP
     *            - Should copying to child CSP profiles
     */
    void cloneBlockedCarrierGeoServices(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyToCSP);
}
