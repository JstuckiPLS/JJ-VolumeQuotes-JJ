package com.pls.ltlrating.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.DotRegionEntity;
import com.pls.ltlrating.domain.DotRegionFuelEntity;
import com.pls.ltlrating.domain.LtlFuelEntity;
import com.pls.ltlrating.domain.bo.FuelListItemVO;
import com.sun.syndication.io.FeedException;

/**
 * Service for working with LTL Fuel.
 *
 * @author Stas Norochevskiy
 *
 */
public interface LtlFuelService {

    /**
     * Persists entity.
     *
     * @param ltlFuelEntity entity to save or update
     * @return persisted entity
     */
    LtlFuelEntity saveFuelTrigger(LtlFuelEntity ltlFuelEntity);

    /**
     * Retrieve Fuel Trigger by Primary Key.
     * @param id primary key
     * @return selected LtlFuelEntity object
     */
    LtlFuelEntity getFuelTriggerById(Long id);

    /**
     * Retrieve All fuel triggers for a specific profile.
     *
     * @param profileDetailId ID of profile details
     * @return list of LtlFuelEntity object with specified profile details ID
     */
    List<LtlFuelEntity> getAllFuelTriggersByProfileDetailId(Long profileDetailId);

    /**
     * Retrieve all active and effective (LOCALTIMESTAMP <= expdate) Fuel Triggers for selected profile detail and
     * display them on the grid in the screen - in the active tab.
     *
     * @param profileDetailId ID of profile details
     * @return list of active LtlFuelEntity object with specified profile details ID
     */
    List<FuelListItemVO> getActiveFuelTriggersByProfileDetailId(Long profileDetailId);

    /**
     * Retrieve all inactive Guaranteed Price for selected profile detail and
     * display them on the grid in the screen - in the archive tab.
     *
     * @param profileDetailId ID of profile details
     * @return list of inactive LtlFuelEntity object with specified profile details ID
     */
    List<FuelListItemVO> getInactiveFuelTriggersByProfileDetailId(Long profileDetailId);

    /**
     * Retrieve all active and expired (LOCALTIMESTAMP > expdate) Fuel Triggers for selected profile detail
     * and display them on the grid in the screen - in the expired tab.
     * @param profileDetailId ID of profile details
     * @return list of expired LtlFuelEntity object with specified profile details ID
     */
    List<FuelListItemVO> getExpiredFuelTriggersByProfileDetailId(Long profileDetailId);

    /**
     * Module 4, UI 4.1.2.5 and Module 6, 6.1.1.2.5: To archive multiple Ltl Fuel triggers.
     * Return list of active or expired fuel triggers based on the boolean flag "isActiveList".
     * If flag is yes, the fuel triggers are picked from "Active" grid and so need
     * to return updated "Active" list using method "getActiveFuelTriggersByProfileDetailId(Long profileDetailId);"
     * else return updated "Expired" list using method "getExpiredFuelTriggersByProfileDetailId(Long profileDetailId);"
     * method. In UI, "Edit" and "Archive" fields should be disabled when user selects "Archived" tab.
     *
     * @param ids List of LTL Fuel Ids (primary keys) that need to be inactivated
     * @param profileDetailId LTL_PRIC_PROF_DETAIL_ID which is a foreign key to LTL_PRICING_PROFILE_DETAILS table
     * @param isActiveList to differentiate if we are picking the pricing details to inactivate from the "Active"
     *        pricing details list or from "Expired" pricng details list
     * @return list of deactivated objects
     */
    List<FuelListItemVO> inactivateFuelTriggers(List<Long> ids, Long profileDetailId, Boolean isActiveList);

    /**
     * Module 4, UI 4.1.2.5 and Module 6, 6.1.1.2.5: To reactivate multiple Ltl Fuel triggers.
     * Return updated list of inactive using method "getActiveFuelTriggersByProfileDetailId(Long profileDetailId);".
     * In UI, "Edit" field should be disabled when user selects "Archived" tab and change "Archive" button
     * to "Rectivate" button. This is missing in UI and should be added to allow the user
     * to have the flexibility of correcting their mistakes.
     *
     * @param ids List of LTL Fuel Ids (primary keys) that need to be reactivated
     * @param profileDetailId LTL_PRIC_PROF_DETAIL_ID which is a foreign key to LTL_PRICING_PROFILE_DETAILS table
     * @return list of activated objects
     */
    List<FuelListItemVO> reactivateFuelTriggers(List<Long> ids, Long profileDetailId);

    /**
     * Makes specified records Expired.
     *
     * @param ids
     *            Not <code>null</code> {@link List} of {@link Long} with id's of {@link LtlFuelEntity} which
     *            should be expired.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link LtlFuelEntity}
     */
    List<FuelListItemVO> expireFuelTriggers(List<Long> ids, Long profileDetailId);

    /**
     * Update status for Fuel entity with specified ID.
     * @param fuelTriggerIds primary keys of LtlFuelEntity
     * @param newStatus status to be set
     */
    void updateStatus(List<Long> fuelTriggerIds, Status newStatus);

    /**
     * Module 4, UI 4.1.2.5; Module 6, UI 6.2.1: Retrive active and effective
     * ((exp_date is null or LOCALTIMESTAMP < exp_date)and eff_date < LOCALTIMESTAMP) - To populate the Region dropdown
     * and also to display the current value with fuel rate for the selected region from the database.
     * This data should be cached as it changes only once a week.
     * Also this data has to be displayed in the Fuel tab in module 6
     * to allow user to read the data and edit the same if needed
     *
     * @return active and effective DotRegionEntity objects
     */
    List<DotRegionEntity> getDotRegions();

    /**
     * Read an RSS feed once in a week and populate the same into our table.
     * Every time new data is retrieved and saved, don’t delete old data, instead expire old data.
     * Also empty out the cache using CacheEvict
     * @return
     * @throws IOException exception
     * @throws FeedException RSS feed exception
     * @throws IllegalArgumentException exception
     */
    void receiveRegionsFuelRates() throws IllegalArgumentException, FeedException, IOException;

    /**
     * Read an RSS and return map Region ID => Charge. Every time new data is retrieved and saved, don’t
     * delete old data, instead expire old data. Also empty out the cache using CacheEvict
     * 
     * @return map Region ID => Charge
     */
    Map<String, String> retrieveRegionsFuelRatesFromDOT();

    /**
     * Save multiple fuels at the same time.
     * @param regionFuelEntities multiple fuels
     * @return persisted fuel entities
     */
    List<DotRegionFuelEntity> saveRegionFuel(List<DotRegionFuelEntity> regionFuelEntities);

    /**
     * Get list of {@link DotRegionFuelEntity} that match condition specified by {@link DateRangeQueryBO} instance.
     * @param dateRange specifies which entities should be retrieved. {@see DateRangeQueryBO}
     * @param regionIds specifies list of regions.
     * @return retrieved list of GetRegionsFuelCO
     */
    List<DotRegionFuelEntity> getRegionFuelBySelectedCriteria(DateRangeQueryBO dateRange, List<Long> regionIds);

    /**
     * Copies all active and currently actual {@link LtlFuelEntity} from profile copyFromProfileId to profile
     * copyToProfileId.
     * Before it all active and actual {@link LtlFuelEntity} in copyToProfileId are made inactive.
     * @param copyFromProfileId source profile
     * @param copyToProfileId destination profile
     * @param shouldCopyToCSP Should copying to child CSP profiles
     */
    void copyFrom(Long copyFromProfileId, Long copyToProfileId, boolean shouldCopyToCSP);

    /**
     * Gets current active Rates for Regions.
     *
     * @return List of Fuel Rates for current day
     */
    List<DotRegionFuelEntity> getRegionsRates();
}
