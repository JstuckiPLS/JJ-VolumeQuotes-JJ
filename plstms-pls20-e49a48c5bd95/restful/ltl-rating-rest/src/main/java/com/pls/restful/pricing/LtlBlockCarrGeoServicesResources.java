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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;
import com.pls.ltlrating.domain.bo.BlockCarrierListItemVO;
import com.pls.ltlrating.domain.bo.CopyFromCO;
import com.pls.ltlrating.service.LtlBlockCarrGeoServicesService;

/**
 * Resource for Ltl Block Carrier Geo Services.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/blockedCarriers")
public class LtlBlockCarrGeoServicesResources {

    @Autowired
    private LtlBlockCarrGeoServicesService service;

    /**
     * Clones the Block Carrier Geo Services from "copyFromProfileDetailId" to "copyToProfileDetailId" and
     * save the same.
     * 
     * @param copyCriteria
     *            Criteria object containing the profile details from which Block Carrier Geo Services should
     *            be copied and to which profile they should be copied.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copyfrom", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyFrom(@RequestBody CopyFromCO copyCriteria) {
        service.cloneBlockedCarrierGeoServices(copyCriteria.getCopyFromId(), copyCriteria.getCopyToId(), true);
    }

    /**
     * Get list of {@link BlockCarrierListItemVO} with active status by specified profile id.
     * 
     * @param profileId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link} of {@link BlockCarrierListItemVO}
     * */
    @RequestMapping(value = "/profile/{profileId}/active", method = RequestMethod.GET)
    @ResponseBody
    public List<BlockCarrierListItemVO> getActiveBlockedCarrGeoServByProfileDetailId(
            @PathVariable("profileId") Long profileId) {
        return service.getActiveBlockedCarrGeoServByProfileDetailId(profileId);
    }

    /**
     * Get {@link LtlBlockCarrGeoServicesEntity} object by primary key id.
     * 
     * @param blockId
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlBlockCarrGeoServicesEntity} if it was found, otherwise return
     *         <code>null</code>
     */
    @RequestMapping(value = "/{blockId}", method = RequestMethod.GET)
    @ResponseBody
    public LtlBlockCarrGeoServicesEntity getBlockedCarrierGeoServiceById(@PathVariable("blockId") Long blockId) {
        return service.getBlockedCarrierGeoServiceById(blockId);
    }

    /**
     * Get list of {@link BlockCarrierListItemVO} with inactive status by specified profile id.
     * 
     * @param profileId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link} of {@link BlockCarrierListItemVO}
     * */
    @RequestMapping(value = "/profile/{profileId}/inactive", method = RequestMethod.GET)
    @ResponseBody
    public List<BlockCarrierListItemVO> getInactiveBlockedCarrGeoServByProfileDetailId(
            @PathVariable("profileId") Long profileId) {
        return service.getInactiveBlockedCarrGeoServByProfileDetailId(profileId);
    }

    /**
     * Change status on inactive of {@link LtlBlockCarrGeoServicesEntity} with specified list of id's and
     * profile id.
     * 
     * @param ids
     *            Not<code>null</code> instance of {@link List} with id of
     *            {@link LtlBlockCarrGeoServicesEntity}.
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link Response}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<BlockCarrierListItemVO> inactivateBlockedCarrierGeoServices(
            @RequestBody List<Long> ids, @RequestParam("profileId") Long profileId) {
        return service.inactivateBlockedCarrierGeoServices(ids, profileId);
    }

    /**
     * Change status on active of {@link LtlBlockCarrGeoServicesEntity} with specified list of id's and
     * profile id.
     * 
     * @param ids
     *            Not<code>null</code> instance of {@link List} with id of
     *            {@link LtlBlockCarrGeoServicesEntity}.
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link Response}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/reactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<BlockCarrierListItemVO> reactivateBlockedCarrierGeoServices(
            @RequestBody List<Long> ids, @RequestParam("profileId") Long profileId) {
        return service.reactivateBlockedCarrierGeoServices(ids, profileId);
    }

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
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public LtlBlockCarrGeoServicesEntity saveBlockedCarrierGeoService(
            @RequestBody LtlBlockCarrGeoServicesEntity entity) throws ValidationException {
        return service.saveBlockedCarrierGeoService(entity);
    }
}
