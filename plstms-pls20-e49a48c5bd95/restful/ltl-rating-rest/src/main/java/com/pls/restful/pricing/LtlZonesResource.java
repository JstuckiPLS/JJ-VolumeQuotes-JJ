package com.pls.restful.pricing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.service.validation.ValidationException;
import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.pricing.ZoneItemDTOBuilder;
import com.pls.ltlrating.domain.LtlZonesEntity;
import com.pls.ltlrating.domain.bo.CopyFromCO;
import com.pls.ltlrating.domain.bo.ZonesListItemVO;
import com.pls.ltlrating.service.LtlZonesSerivce;

/**
 * Resource for Ltl Zones.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/profile/{profileId}/zones")
public class LtlZonesResource {

    private static final ZoneItemDTOBuilder ZONE_ITEMS_BUILDER = new ZoneItemDTOBuilder();

    @Autowired
    private LtlZonesSerivce service;

    /**
     * Get list of {@link LtlZonesEntity} with active status by specified profile id.
     * 
     * @param profileId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link} of {@link LtlZonesEntity}
     * */
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    @ResponseBody
    public List<ZonesListItemVO> getActiveLTLZonesByProfileDetailId(@PathVariable("profileId") Long profileId) {
        return service.getActiveLTLZonesByProfileDetailId(profileId);
    }

    /**
     * Get {@link LtlZonesEntity} object by primary key profile id.
     * 
     * @param profileId
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlZonesEntity} if it was found, otherwise return <code>null</code>
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<LtlZonesEntity> getAllLTLZonesByProfileDetailId(@PathVariable("profileId") Long profileId) {
        return service.getAllLTLZonesByProfileDetailId(profileId);
    }

    /**
     * Get Active Zones for specified profile id.
     * 
     * @param profileId
     *            Not <code>null</code> {@link Long} value.
     * @return {@link List} of {@link ValueLabelDTO}.
     */
    @RequestMapping(value = "/dictionary", method = RequestMethod.GET)
    @ResponseBody
    public List<ValueLabelDTO> getDictionary(@PathVariable("profileId") Long profileId) {
        List<LtlZonesEntity> zones = service.getActiveZoneEntitiesForProfile(profileId);
        return ZONE_ITEMS_BUILDER.buildList(zones);
    }

    /**
     * Get list of {@link LtlZonesEntity} with inactive status by specified profile id.
     * 
     * @param profileId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link} of {@link LtlZonesEntity}
     * */
    @RequestMapping(value = "/inactive", method = RequestMethod.GET)
    @ResponseBody
    public List<ZonesListItemVO> getInactiveLTLZonesByProfileDetailId(@PathVariable("profileId") Long profileId) {
        return service.getInactiveLTLZonesByProfileDetailId(profileId);
    }

    /**
     * Get {@link LtlZonesEntity} object by primary key id.
     * 
     * @param zoneId
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlZonesEntity} if it was found, otherwise return <code>null</code>
     */
    @RequestMapping(value = "/{zoneId}", method = RequestMethod.GET)
    @ResponseBody
    public LtlZonesEntity getLTLZoneById(@PathVariable("zoneId") Long zoneId) {
        return service.getLTLZoneById(zoneId);
    }

    /**
     * Change status on inactive of {@link LtlZonesEntity} with specified list of id's and profile id.
     * 
     * @param ids
     *            Not<code>null</code> instance of {@link List} with id of {@link LtlZonesEntity}.
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link Response}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<ZonesListItemVO> inactivateLTLZones(@PathVariable("profileId") Long profileId,
            @RequestBody List<Long> ids) {
        return service.inactivateLTLZones(ids, profileId);
    }

    /**
     * Change status on active of {@link LtlZonesEntity} with specified list of id's and profile id.
     * 
     * @param ids
     *            Not<code>null</code> instance of {@link List} with id of {@link LtlZonesEntity}.
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link LtlZonesEntity} - remaining entities with inactive status.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/reactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<ZonesListItemVO> reactivateLTLZones(@PathVariable("profileId") Long profileId,
            @RequestBody List<Long> ids) {
        return service.reactivateLTLZones(ids, profileId);
    }

    /**
     * Save {@link LtlZonesEntity}.
     * 
     * @param entity
     *            Not <code>null</code> instance of {@link LtlZonesEntity} which should be saved.
     * @return {@link Response}
     * @throws ValidationException
     *             validation exceptions
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public LtlZonesEntity saveLTLZone(@RequestBody LtlZonesEntity entity) throws ValidationException {
        return service.saveLTLZone(entity);
    }

    /**
     * Clones the Zones from "copyFromProfileDetailId" to "copyToProfileDetailId" and save the same.
     * 
     * @param copyCriteria
     *            Criteria object containing the profile details from which Zones should be copied and to
     *            which profile they should be copied.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyFrom(@RequestBody CopyFromCO copyCriteria) {
        service.cloneLTLZones(copyCriteria.getCopyFromId(), copyCriteria.getCopyToId(), true);
    }
}
