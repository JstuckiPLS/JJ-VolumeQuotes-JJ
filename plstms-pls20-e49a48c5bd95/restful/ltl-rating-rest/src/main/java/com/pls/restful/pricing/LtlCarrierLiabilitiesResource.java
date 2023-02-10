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
import com.pls.dto.LtlCarrierLiabilitiesDTO;
import com.pls.dtobuilder.pricing.LtlCarrierLiabilitiesDTOBuilder;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;
import com.pls.ltlrating.domain.bo.ProhibitedNLiabilitiesVO;
import com.pls.ltlrating.service.LtlCarrierLiabilitiesService;

/**
 * Resource for Carrier Liabilities.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/profile/{profileId}/liabilities")
public class LtlCarrierLiabilitiesResource {

    @Autowired
    private LtlCarrierLiabilitiesService service;

    /**
     * Get {@link LtlCarrierLiabilitiesEntity} by specified primary key <code>id</code> value.
     * 
     * @param liabilityId
     *            Not <code>null</code> value of {@link Long}
     * @return appropriate {@link LtlCarrierLiabilitiesEntity} if it was found, otherwise return null.
     */
    @RequestMapping(value = "/{liabilityId}", method = RequestMethod.GET)
    @ResponseBody
    public LtlCarrierLiabilitiesEntity getCarrierLiabilitiesById(@PathVariable("liabilityId") Long liabilityId) {
        return service.getCarrierLiabilitiesById(liabilityId);
    }

    /**
     * Get a list of {@link LtlCarrierLiabilitiesEntity} by specified profile id.
     * 
     * @param profileId
     *            Not <code>null</code> value of {@link Long}.
     * @param isPallet
     *            flag indicating if the liabilities are for a pallet profile
     * 
     * @return {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     */
    @RequestMapping(value = "/pallet/{isPallet}", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlCarrierLiabilitiesDTO> getCarrierLiabilitiesByProfileId(@PathVariable("profileId") Long profileId,
            @PathVariable("isPallet") boolean isPallet) {
        LtlCarrierLiabilitiesDTOBuilder.getInstance();
        return LtlCarrierLiabilitiesDTOBuilder.getAllLiabilities(service.getCarrierLiabilitiesByProfileId(profileId), isPallet);
    }

    /**
     * Save a list of {@link LtlCarrierLiabilitiesEntity} and return list of saved entities.
     * 
     * @param list
     *            Not <code>null</code> {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     * @param profileId
     *            profile for which the liabilities have to be saved.
     * @return Not <code>null</code> {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     * @throws ValidationException
     *             validation exceptions
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{profileId}", method = RequestMethod.POST)
    @ResponseBody
    public List<LtlCarrierLiabilitiesEntity> saveCarrierLiabilities(@RequestBody List<LtlCarrierLiabilitiesEntity> list,
            @PathVariable("profileId") Long profileId) throws ValidationException {
        return service.saveCarrierLiabilities(list, profileId);
    }

    /**
     * Save a list of {@link LtlCarrierLiabilitiesEntity} and return list of saved entities.
     * 
     * @param vo
     *            vo to be saved
     * @throws ValidationException
     *             validation exceptions
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveProhibitedNLiabilities(@RequestBody ProhibitedNLiabilitiesVO vo) throws ValidationException {
        service.saveProhibitedNLiabilities(vo);
    }

    /**
     * Clones the Liabilities from "copyFromProfileDetailId" to "copyToProfileDetailId" and save the same.
     * 
     * @param profileId
     *            Copy to profile id. Not <code>null</code> value of {@link Long}
     * @param profileToCopy
     *            Copy from profile id. Not <code>null</code> value of {@link Long}
     * @throws ValidationException
     *             validation exceptions
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copyLiabilities/{profileToCopy}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyLiabilities(@PathVariable("profileId") Long profileId, @PathVariable("profileToCopy") Long profileToCopy)
                            throws ValidationException {
        service.cloneLiabilities(profileToCopy, profileId);
    }

    /**
     * Clones the Prohibited Commodities from "copyFromProfileDetailId" to "copyToProfileDetailId" and save the same.
     * 
     * @param profileId
     *            Copy to profile id. Not <code>null</code> value of {@link Long}
     * @param profileToCopy
     *            Copy from profile id. Not <code>null</code> value of {@link Long}
     * @throws ValidationException
     *             validation exceptions
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copyCommodities/{profileToCopy}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyCommodities(@PathVariable("profileId") Long profileId, @PathVariable("profileToCopy") Long profileToCopy)
                            throws ValidationException {
        service.cloneProhibitedCommodities(profileToCopy, profileId);
    }
}
