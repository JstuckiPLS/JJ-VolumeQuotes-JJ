package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.bo.AccessorialListItemVO;

/**
 * Data Access Object for {@link LtlAccessorialsEntity} data.
 *
 * @author Hima Bindu Challa
 */
public interface LtlAccessorialsDao extends AbstractDao<LtlAccessorialsEntity, Long> {

    /**
     * To get All Accessorials irrespective of status and expiration date for the given profile Id.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<LtlAccessorialsEntity> - List of all LtlAccessorialsEntities for selected profile
     */
    List<LtlAccessorialsEntity> findAllByProfileDetailId(Long profileDetailId);

    /**
     * To get All Accessorials irrespective expiration date for the given status and profile Id. Will return
     * all active profiles or all inactive profiles.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @param status
     *            - Status of the accessorial - Active/Inactive = A/I
     * @return List<AccessorialListItemVO> - List of all LtlAccessorialsEntities for selected profile
     */
    List<AccessorialListItemVO> findAllByStatusAndProfileDetailId(Long profileDetailId, Status status);

    /**
     * To get All active and effective Accessorials for the given profile Id. This returns the actual entity as the operation invoking this method
     * needs all the data of the accessorial.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<LtlAccessorialsEntity> - List of all LtlAccessorialsEntities for selected profile
     */
    List<LtlAccessorialsEntity> findActiveAndEffectiveForProfile(Long profileDetailId);

    /**
     * To get All active and effective Accessorials for the given profile Id.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<AccessorialListItemVO> - List of all LtlAccessorialsEntities for selected profile
     */
    List<AccessorialListItemVO> findActiveAndEffectiveByProfileDetailId(Long profileDetailId);

    /**
     * To get All active and expired Accessorials for the given profile Id.
     *
     * @param profileDetailId
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<AccessorialListItemVO> - List of all LtlAccessorialsEntities for selected profile
     */
    List<AccessorialListItemVO> findExpiredByProfileDetailId(Long profileDetailId);

    /**
     * To archive/inactivate multiple active accessorials by Profile Detail ID.
     *
     * This method inactivates all active accessorials and is used when cloning the accessorials from
     * another Profile.
     *
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the accessorials should be inactivated
     */
    void inactivateActiveAndEffAccByProfDetailId(Long profileDetailId);

    /**
     * Makes specified records Expired.
     *
     * @param ids
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlAccessorialsEntity} which should be expired.
     * @param modifiedBy
     *            The user who modified or inactivated/reactivated the price detail.
     */
    void expireByListOfIds(List<Long> ids, Long modifiedBy);

    /**
     * Find all {@link LtlAccessorialsEntity} which pricingType is 'BLANKET_CSP' and was copied from specified
     * <tt>copiedFrom</tt> value.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlAccessorialsEntity}.
     */
    List<LtlAccessorialsEntity> findAllCspChildsCopyedFrom(Long copiedFrom);

    /**
     * Makes all child Customer Specific Prices expire.
     *
     * @param copiedFromIds
     *              Ids of profile detail from which child CSPs were copied. (It's a link to parent entity).
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
     * Updates status in {@link LtlAccessorialsEntity}.
     *
     * @param accessorialIds
     *              Ids of accessorials.
     * @param status
     *              Value of {@link Status} enumeration.
     *              Not <code>null</code>.
     * @param modifiedBy
     *              Not <code>null</code> value of {@link Long}.
     */
    void updateStatuses(List<Long> accessorialIds, Status status, Long modifiedBy);

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
