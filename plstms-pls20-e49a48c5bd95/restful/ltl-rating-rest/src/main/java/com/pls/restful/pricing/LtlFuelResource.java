package com.pls.restful.pricing;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.exception.ApplicationException;
import com.pls.dto.query.DateRangeQueryDTO;
import com.pls.dtobuilder.DateRangeQueryDTOBuilder;
import com.pls.ltlrating.domain.DotRegionEntity;
import com.pls.ltlrating.domain.DotRegionFuelEntity;
import com.pls.ltlrating.domain.LtlFuelEntity;
import com.pls.ltlrating.domain.bo.FuelListItemVO;
import com.pls.ltlrating.domain.bo.GetRegionsFuelCO;
import com.pls.ltlrating.service.LtlFuelService;

/**
 * RESRful for working Ltl Fuel Trigger.
 * 
 * @author Stas Norochevskiy
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/ltlfuel")
public class LtlFuelResource {

    @Autowired
    private LtlFuelService service;

    private final DateRangeQueryDTOBuilder dateRangeBuilder = new DateRangeQueryDTOBuilder();

    private static final Logger LOG = LoggerFactory.getLogger(LtlFuelResource.class);

    /**
     * Performs "copy from" functionality. - inactivates active LtlFuelEntities in copyToProfileId profile -
     * copies active LtlFuelEntities from copyFromProfileId profile to copyToProfileId
     * 
     * @param paramsMap
     *            map with copyFromProfileId and copyToProfileId
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copyFrom", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyFrom(@RequestBody Map<String, Long> paramsMap) {

        Long copyFromProfileId = paramsMap.get("copyFromProfileId");
        Long copyToProfileId = paramsMap.get("copyToProfileId");
        service.copyFrom(copyFromProfileId, copyToProfileId, true);
    }

    /**
     * Retrieve all active and effective (LOCALTIMESTAMP <= expdate) Fuel Triggers for selected profile detail and
     * display them on the grid in the screen - in the active tab.
     * 
     * @param profileDetailId
     *            ID of profile details
     * @return list of active LtlFuelEntity object with specified profile details ID
     */
    @RequestMapping(value = "/active/{profileDetailId}", method = RequestMethod.GET)
    @ResponseBody
    public List<FuelListItemVO> getActiveByProfileDetailId(@PathVariable("profileDetailId") Long profileDetailId) {
        return service.getActiveFuelTriggersByProfileDetailId(profileDetailId);
    }

    /**
     * Retrieve all active and expired (LOCALTIMESTAMP > expdate) Fuel Triggers for selected profile detail and
     * display them on the grid in the screen - in the expired tab.
     * 
     * @param profileDetailId
     *            ID of profile details
     * @return list of expired LtlFuelEntity object with specified profile details ID
     */
    @RequestMapping(value = "/expired/{profileDetailId}", method = RequestMethod.GET)
    @ResponseBody
    public List<FuelListItemVO> getExpiredByProfileDetailId(@PathVariable("profileDetailId") Long profileDetailId) {
        return service.getExpiredFuelTriggersByProfileDetailId(profileDetailId);
    }

    /**
     * Retrieve all inactive Guaranteed Price for selected profile detail and display them on the grid in the
     * screen - in the archive tab.
     * 
     * @param profileDetailId
     *            ID of profile details
     * @return list of inactive LtlFuelEntity object with specified profile details ID
     */
    @RequestMapping(value = "/inactive/{profileDetailId}", method = RequestMethod.GET)
    @ResponseBody
    public List<FuelListItemVO> getInactiveByProfileDetailId(@PathVariable("profileDetailId") Long profileDetailId) {
        return service.getInactiveFuelTriggersByProfileDetailId(profileDetailId);
    }

    /**
     * Retrieve All fuel triggers for a specific profile.
     * 
     * @param profileDetailId
     *            ID of profile details
     * @return list of LtlFuelEntity object with specified profile details ID
     */
    @RequestMapping(value = "/getbyprofiledetails/{profileDetailId}", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlFuelEntity> getAllFuelTriggersByProfileDetailId(
            @PathVariable("profileDetailId") Long profileDetailId) {
        return service.getAllFuelTriggersByProfileDetailId(profileDetailId);
    }

    /**
     * Module 4, UI 4.1.2.5; Module 6, UI 6.2.1: Retrive active and effective ((exp_date is null or LOCALTIMESTAMP &lt;
     * exp_date)and eff_date &lt; LOCALTIMESTAMP) - To populate the Region dropdown and also to display the current
     * value with fuel rate for the selected region from the database. This data should be cached as it
     * changes only once a week. Also this data has to be displayed in the Fuel tab in module 6 to allow user
     * to read the data and edit the same if needed
     * 
     * @return active and effective DotRegionFuelEntity objects
     */
    @RequestMapping(value = "/getdotregions", method = RequestMethod.GET)
    @ResponseBody
    public List<DotRegionEntity> getRegions() {
        return service.getDotRegions();
    }

    /**
     * Retrieve Fuel Trigger by Primary Key.
     * 
     * @param id
     *            primary key
     * @return selected LtlFuelEntity object
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public LtlFuelEntity getFuelTriggerById(@PathVariable("id") Long id) {
        return service.getFuelTriggerById(id);
    }

    /**
     * Get list of {@link DotRegionFuelEntity} that match condition specified by {@link GetRegionsFuelCO}
     * instance.
     * 
     * @param dateRange
     *            specifies time interval. {@see DateRangeQueryDTO}
     * @return retrieved list of GetRegionsFuelCO
     */
    @RequestMapping(value = "/getRegionFuelBySelectedCriteria", method = RequestMethod.GET)
    @ResponseBody
    public List<DotRegionFuelEntity> getRegionFuelBySelectedCriteria(@RequestParam("dateRange") DateRangeQueryDTO dateRange) {

        DateRangeQueryBO dateRangeBO = parseDateRange(dateRange);

        //TODO: Currently second parameter is unused due to requirements.
        return service.getRegionFuelBySelectedCriteria(dateRangeBO, null);
    }

    /**
     * Get list of Region Fuel Rates.
     * 
     * @return list of {@link DotRegionFuelEntity}.
     */
    @RequestMapping(value = "/regionsrates", method = RequestMethod.GET)
    @ResponseBody
    public List<DotRegionFuelEntity> getRegionsRates() {
        return service.getRegionsRates();
    }

    /**
     * Module 4, UI 4.1.2.5 and Module 6, 6.1.1.2.5: To archive multiple Ltl Fuel triggers. Return list of
     * active or expired fuel triggers based on the boolean flag "isActiveList". If flag is yes, the fuel
     * triggers are picked from "Active" grid and so need to return updated "Active" list using method
     * "getActiveFuelTriggersByProfileDetailId(Long profileDetailId);" else return updated "Expired" list
     * using method "getExpiredFuelTriggersByProfileDetailId(Long profileDetailId);" method. In UI, "Edit" and
     * "Archive" fields should be disabled when user selects "Archived" tab.
     * 
     * @param ids
     *            List of LTL Fuel Ids (primary keys) that need to be inactivated
     * @param profileDetailId
     *            LTL_PRIC_PROF_DETAIL_ID which is a foreign key to LTL_PRICING_PROFILE_DETAILS table
     * @param isActiveList
     *            to differentiate if we are picking the pricing details to inactivate from the "Active"
     *            pricing details list or from "Expired" pricng details list
     * @return list of deactivated objects
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<FuelListItemVO> inactivateFuelTriggers(@RequestBody List<Long> ids,
            @RequestParam("profileDetailId") Long profileDetailId,
            @RequestParam("isActiveList") Boolean isActiveList) {
        return service.inactivateFuelTriggers(ids, profileDetailId, isActiveList);
    }

    /**
     * Module 4, UI 4.1.2.5 and Module 6, 6.1.1.2.5: To reactivate multiple Ltl Fuel triggers. Return updated
     * list of inactive using method "getInactiveFuelTriggersByProfileDetailId(Long profileDetailId);". In UI,
     * "Edit" field should be disabled when user selects "Archived" tab and change "Archive" button to
     * "Rectivate" button. This is missing in UI and should be added to allow the user to have the flexibility
     * of correcting their mistakes.
     * 
     * @param ids
     *            List of LTL Fuel Ids (primary keys) that need to be reactivated
     * @param profileDetailId
     *            LTL_PRIC_PROF_DETAIL_ID which is a foreign key to LTL_PRICING_PROFILE_DETAILS table
     * @return list of activated objects
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/reactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<FuelListItemVO> reactivateFuelTriggers(@RequestBody List<Long> ids,
            @RequestParam("profileDetailId") Long profileDetailId) {
        return service.reactivateFuelTriggers(ids, profileDetailId);
    }

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
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/expire", method = RequestMethod.POST)
    @ResponseBody
    public List<FuelListItemVO> expireFuelTriggers(@RequestBody List<Long> ids,
            @RequestParam("profileDetailId") Long profileDetailId) {
        return service.expireFuelTriggers(ids, profileDetailId);
    }

    /**
     * Persists entity.
     * 
     * @param ltlFuelEntity
     *            entity to save or update
     * @return persisted entity
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public LtlFuelEntity saveFuelTrigger(@RequestBody LtlFuelEntity ltlFuelEntity) {
        return service.saveFuelTrigger(ltlFuelEntity);
    }

    /**
     * Save multiple fuels at the same time.
     * 
     * @param regionFuelEntities
     *            multiple fuels
     * @return persisted fuel entities
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/saveRegionFuel", method = RequestMethod.POST)
    @ResponseBody
    public List<DotRegionFuelEntity> saveRegionFuel(@RequestBody List<DotRegionFuelEntity> regionFuelEntities) {
        return service.saveRegionFuel(regionFuelEntities);
    }

    private DateRangeQueryBO parseDateRange(DateRangeQueryDTO datesDTO) {
        DateRangeQueryBO dateRange = null;
        try {
            dateRange = dateRangeBuilder.buildEntity(datesDTO);
        } catch (Exception e) {
            LOG.error("exception on getDateRange:", e);
        }
        return dateRange;
    }

    /**
     * Update Region Fuel Rates and get it.
     * 
     * @return list of {@link DotRegionFuelEntity}.
     * @throws ApplicationException
     *             when can't recieve Fuel Rates From EIA.GOV.
     */
    @RequestMapping(value = "/updateRegionsRates", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public List<DotRegionFuelEntity> updateRegionsRates() throws ApplicationException {
        LOG.info("Recieving Fuel Rates From EIA.GOV");
        try {
            service.receiveRegionsFuelRates();
        } catch (Exception e) {
            LOG.error("Can't recieve Fuel Rates From EIA.GOV. " + e.getMessage(), e);
            throw new ApplicationException("Can't recieve Fuel Rates From EIA.GOV");
        }
        LOG.info("Finished recieving Fuel Rates From EIA.GOV");
        return service.getRegionsRates();
    }
}