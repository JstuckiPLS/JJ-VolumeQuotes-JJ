package com.pls.ltlrating.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import com.pls.core.domain.bo.SurchargeFileImportResult;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;


/**
 *
 * Service for working with fuel surcharge.
 *
 * @author Stas Norochevskiy
 *
 */
public interface LtlFuelSurchargeService {

    /**
     *  This method is for both create and update operations.
     *  The Save operation should return the updated data (succes or roll back)
     *  along with other field values:
     *  primary key, date created, created by, date modified, modified by, version
     *  and use the same to populate the screen and this should be done by calling
     *  getActiveFuelSurchargeByProfileDetailId(Long profileDetailId) method instead of duplicating logic.
     *  This is required especially for pessimistic locking.
     *
     * @param ltlFuelSurchargeEntity entity to save
     * @return updated data
     */
    LtlFuelSurchargeEntity saveFuelSurcharge(LtlFuelSurchargeEntity ltlFuelSurchargeEntity);

    /**
     * Retrieve Active Fuel Surcharge and also to export fuel surcharge.
     *
     * @param profileDetailId profile detail
     * @return fuel surcharge for give profile datails
     */
    List<LtlFuelSurchargeEntity> getActiveFuelSurchargeByProfileDetailId(Long profileDetailId);

    /**
     * This method reads the excel sheet and calls saveFuelSurcharge() method to import fuel surcharge.
     *
     * @param ltlFuelSurchargeEntities entity to import
     * @return entities returned by saveFuelSurcharge
     */
    List<LtlFuelSurchargeEntity> importFuelSurchargeByProfileDetailId(
            List<LtlFuelSurchargeEntity> ltlFuelSurchargeEntities);

    /**
     * Imports {@link LtlFuelSurchargeEntity} from file and return them as list.
     *
     * @param stream
     *            document
     * @param extension
     *            extension
     * @param profileDetailId
     *            ID of Profile Detail that is related with importing entitiesinvalidRecords
     * @param surchargeFileImportResult
     *            - result of import
     *
     * @return list of retrieved from document entities
     * @throws ImportException
     *             import exception
     */
    List<LtlFuelSurchargeEntity> importFuelSurchargeByProfileDetailIdFromFile(
InputStream stream, String extension, Long profileDetailId,
            SurchargeFileImportResult surchargeFileImportResult) throws ImportException;


    /**
     * query LTL_FUEL_SURCHARGE table using this FUEL_CHARGE value and get the SURCHARGE like
     * “SELECT SURCHARGE FROM LTL_FUEL_SURCHARGE WHERE MIN_RATE  <=  <<FUEL_CHARGE>> AND MAX_RATE  >=  <<FUEL_CHARGE>>”.
     * @param charge taken from @see DotRegionFuelEntity#getFuelCharge()
     * @return proper surcharge value
     */
    BigDecimal getFuelSurchargeByFuelCharge(BigDecimal charge);

    /**
     * Copies all active and currently actual {@link LtlFuelSurchargeEntity} from profile copyFromProfileId to profile
     * copyToProfileId.
     * Before it all active and actual {@link LtlFuelSurchargeEntity} in copyToProfileId are made inactive.
     * @param copyFromProfileId source profile
     * @param copyToProfileId destination profile
     * @param shouldCopyToCSP Should copying to child CSP profiles
     */
    void copyFrom(Long copyFromProfileId, Long copyToProfileId, boolean shouldCopyToCSP);

    /**
     *  Saves all the fuel surcharges for a pricing profile.
     *
     * @param ltlFuelSurcharges entities to save
     * @return updated data
     */
    List<LtlFuelSurchargeEntity> saveAllFuelSurcharges(List<LtlFuelSurchargeEntity> ltlFuelSurcharges);

    /**
     * Update status of entity by specified ids.
     *
     * @param ids
     *          ids of entities which should be updated.
     *          Not <code>null</code>.
     * @param status
     *          new status {@link Status}.
     *          Not <code>null</code>.
     * @param profileDetailId
     *          profile detail id.
     *          Not <code>null</code>.
     */
    void updateStatus(List<Long> ids, Status status, Long profileDetailId);
}
