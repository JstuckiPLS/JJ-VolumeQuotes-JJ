package com.pls.ltlrating.dao;

import java.util.List;
import java.util.Set;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;
import com.pls.ltlrating.domain.bo.BlockCarrierListItemVO;

/**
 * Data Access Object for {@link LtlBlockCarrGeoServicesEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface LtlBlockCarrGeoServicesDao extends AbstractDao<LtlBlockCarrGeoServicesEntity, Long> {

    /**
     * Returns List of {@link LtlBlockCarrGeoServicesEntity} related to specified profile detail.
     *
     * @param profileDetailId
     *              - Profile Detail Id.
     * @return List of {@link LtlBlockCarrGeoServicesEntity}.
     */
    List<LtlBlockCarrGeoServicesEntity> findByProfileDetailId(Long profileDetailId);

    /**
     * Get a list of active {@link LtlBlockCarrGeoServicesEntity} by profile id.
     *
     * @param status
     *            value of {@link Status} enumeration.
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link BlockCarrierListItemVO}.
     */
    List<LtlBlockCarrGeoServicesEntity> findActiveByProfileId(Status status, Long profileId);

    /**
     * Get a list of {@link BlockCarrierListItemVO} by specified status and profile id.
     *
     * @param status
     *            value of {@link Status} enumeration.
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link BlockCarrierListItemVO}.
     */
    List<BlockCarrierListItemVO> findByStatusAndProfileId(Status status, Long profileId);

    /**
     * Update list of {@link LtlBlockCarrGeoServicesEntity} by specified ids, status and profile id.
     *
     * @param ids
     *            Instance of {@link List} with primary key ids of {@link LtlBlockCarrGeoServicesEntity} which
     *            should be modified
     * @param status
     *            value of {@link Status} enumeration.
     * @param modifiedBy
     *            Not <code>null</code> instance of {@link Long}
     */
    void updateStatus(List<Long> ids, Status status, Long modifiedBy);

    /**
     * To archive/inactivate all active Block Carrier Geo Services by Profile Detail ID.
     *
     * This method inactivates all active Block Carrier Geo Services and is used when cloning the Block
     * Carrier Geo Services from another Profile.
     *
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the Block Carrier Geo Services should be
     *            inactivated
     */
    void inactivateByProfileDetailId(Long profileDetailId);

    /**
     * Find all {@link LtlBlockCarrGeoServicesEntity} which pricingType is 'BLANKET_CSP' and was copied from
     * specified <tt>copiedFrom</tt> value.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlBlockCarrGeoServicesEntity}.
     */
    List<LtlBlockCarrGeoServicesEntity> findAllCspChildsCopyedFrom(Long copiedFrom);

    /**
     * Updates status in child Customer Specific Price entities.
     *
     * @param copiedFromIds
     *          Ids of {@link LtlBlockCarrGeoServicesEntity} from which child CSPs were copied. (It's a link to parent entity).
     *          Not <code>null</code>.
     * @param status
     *          Value of {@link Status} enumeration. Not <code>null</code>.
     * @param modifiedBy
     *          Id of user who performs this operation. Not <code>null</code>
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
     * To delete the Geo Service details associated with the Block Carrier Geo Service.
     *
     * This method deletes all Geo Service Details for the Blocked Carrier Geo Service. This is used when updating the Geo Service.
     *
     * @param geoServiceId
     *            - id of the geo service
     */
    void deleteGeoServiceDetails(Set<Long> geoServiceId);
}
