package com.pls.ltlrating.service;

import java.util.List;

import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.LtlAccessorialsMappingEntity;
import com.pls.ltlrating.domain.bo.AccessorialListItemVO;

/**
 * Service that handle business logic and transactions for LTL Accessorials.
 *
 * @author Hima Bindu Challa
 */
public interface LtlAccessorialsService {

    /**
     * This method is for both create and update operations.
     * The Save operation returns the updated data (succes or roll back) along with other field values -
     * primary key, date created, created by, date modified, modified by, version
     * and will use the same to populate the screen. This is required especially for pessimistic locking.
     *
     * @param accessorial
     *       - The LtlAccessorialsEntity that need to be saved
     * @return LtlAccessorialsEntity
     *       - Updated LTLAccessorialEntity (With date created, created by and version values)
     */
    LtlAccessorialsEntity saveAccessorial(LtlAccessorialsEntity accessorial);

    /**
     * To get Accessorial by primary Key.
     *
     * @param id
     *            - Primary Key of the entity - LTLAccessorialsEntity
     * @return LtlAccessorialsEntity
     */
    LtlAccessorialsEntity getAccessorialById(Long id);

    /**
     * To get All Accessorials irrespective of status and expiration date for the given profile detail (Buy/Sell/None).
     *
     * @param profileDetailId
     *       - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<LtlAccessorialsEntity>
     *       - List of all LtlAccessorialsEntities for selected profile
     */
    List<LtlAccessorialsEntity> getAllAccessorialsByProfileDetailId(Long profileDetailId);

    /**
     * To get All Active and Effective Accessorials (LOCALTIMESTAMP <= expdate) for selected profile detail (Buy/Sell/None).
     *
     * @param profileDetailId
     *       - LTL Profile Detail Id (Buy/Sell/None) to which the active accessorials should be retrieved
     * @return List<AccessorialListItemVO>
     *       - List of all Active and Effective LtlAccessorialsEntities for selected profile
     */
    List<AccessorialListItemVO> getActiveAccessorialsByProfileDetailId(Long profileDetailId);

    /**
     * To get All inactive Accessorials for selected profile detail (Buy/Sell/None).
     *
     * @param profileDetailId
     *       - LTL Profile Detail Id (Buy/Sell/None) to which the inactive accessorials should be retrieved
     * @return List<AccessorialListItemVO>
     *       - List of all inactive LtlAccessorialsEntities for selected profile
     */
    List<AccessorialListItemVO> getInactiveAccessorialsByProfileDetailId(Long profileDetailId);

    /**
     * To get All active and expired (LOCALTIMESTAMP > expdate) Accessorials for selected profile detail (Buy/Sell/None).
     *
     * @param profileDetailId
     *       - LTL Profile Detail Id (Buy/Sell/None) to which the expired accessorials should be retrieved
     * @return List<AccessorialListItemVO>
     *       - List of all active and expired LtlAccessorialsEntities for selected profile
     */
    List<AccessorialListItemVO> getExpiredAccessorialsByProfileDetailId(Long profileDetailId);

    /**
     * To archive/inactivate multiple active accessorials.
     *
     * This method returns list of active or expired accessorials based on the boolean flag "isActiveList". If
     * flag is yes/true, we are inactivating "active and effective" accessorials, so this method returns
     * updated "Active and Effective" accessorial list using method
     * "getActiveAccessorialsByProfileId(Long profileDetailId);".
     *
     * If flag is no/false, we are inactivating "active and expired" accessorials, so this method returns
     * updated "Active and Expired" accessorial list using method
     * "getInactiveAccessorialsByProfileId(Long profileDetailId);"
     *
     * @param accessorialIds
     *            - List of LtlAccessorialsEntity Ids - primary keys that need to be inactivated
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved after
     *            inactivating the selected accessorials
     * @param isActiveList
     *            Not <code>null</code> instance of {@link Boolean}.
     * @return List<AccessorialListItemVO> - List of all (active & effective) or (expired)
     *         LtlAccessorialsEntities for selected profile
     */
    List<AccessorialListItemVO> inactivateAccessorials(
            List<Long> accessorialIds, Long profileDetailId, Boolean isActiveList);

    /**
     * To reactivate multiple inactive accessorials. This method returns list of inactive accessorials as the
     * list of accessorials that will be reactivated are inactive accessorials.
     *
     * @param accessorialIds
     *            - List of LtlAccessorialsEntity Ids - primary keys that need to be reactivated
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved after
     *            reactivating the selected accessorials
     * @return List<AccessorialListItemVO> - List of all inactive LtlAccessorialsEntities for selected profile
     */
    List<AccessorialListItemVO> reactivateAccessorials(
            List<Long> accessorialIds, Long profileDetailId);

    /**
     * Clone the Accessorials from "copyFromProfileDetailId" to "copyToProfileDetailId" and save the same.
     *
     * @param copyFromProfileDetailId - The profile detail id from which the accessorials should be copied
     * @param copyToProfileDetailId - The profile detail id to which the accessorials should be copied
     * @param shouldCopyToCSP       - Should copying to child CSP profiles
     */
    void cloneAccessorials(Long copyFromProfileDetailId, Long copyToProfileDetailId, boolean shouldCopyToCSP);

    /**
     * Makes specified records Expired.
     *
     * @param ids
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlAccessorialsEntity} which should be expired.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link AccessorialListItemVO}
     */
    List<AccessorialListItemVO> expirateByListOfIds(List<Long> ids, Long profileDetailId);

    /**
     * Gets accessorials mapping for selected Carrier.
     * 
     * @param carrierId - id of the carrier.
     * @return List<AccessorialsMappingDTO> - List of {@link LtlAccessorialsMappingEntity}.
     */
    List<LtlAccessorialsMappingEntity> getAccessorialsMapping(Long carrierId);

    /**
     * Saves mapped accessorials for selected carrier.
     * 
     * @param accList - list of {@link LtlAccessorialsMappingEntity}.
     */
    void saveAccessorialsMapping(List<LtlAccessorialsMappingEntity> accList);

    /**
     * Gets accessorial mapping by primary key.
     * @param id - primary key.
     * @return {@link LtlAccessorialsMappingEntity}.
     */
    LtlAccessorialsMappingEntity getAccMappingById(Long id);
}
