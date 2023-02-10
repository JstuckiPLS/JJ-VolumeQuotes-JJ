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
import com.pls.core.shared.ResponseVO;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.service.LtlPalletPricingDetailsService;

/**
 * RESTful resource for Ltl Pallet Pricing details.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/profile/{detailId}/pallet")
public class LtlPalletPricingDetailsResource {

    @Autowired
    private LtlPalletPricingDetailsService service;

    /**
     * Makes entity active.
     * 
     * @param id
     *            Not <code>null</code> instance of {@link Long}. Primary key of object which should become active.
     * @param profileDetailId
     *            Id of Profile Detail {@link LtlPricingProfileDetailsEntity#getId()}.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void activate(@RequestBody Long id, @PathVariable("detailId") Long profileDetailId) {
        service.activate(id, profileDetailId);
    }

    /**
     * Performs copying entities from one Profile Detail to another.
     * 
     * @param detailToCopy
     *            Not <code>null</code> instance of {@link Long}.
     * @param detailId
     *            Not <code>null</code> instance of {@link Long}.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copyfrom/{detailToCopy}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyFrom(@PathVariable("detailToCopy") Long detailToCopy, @PathVariable("detailId") Long detailId) {
        service.copyFrom(detailToCopy, detailId);
    }

    /**
     * Find entities with active status and effective dates by specified <code>detailId</code>.
     * 
     * @param detailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return List of {@link LtlPalletPricingDetailsEntity}
     */
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlPalletPricingDetailsEntity> getActiveList(@PathVariable("detailId") Long detailId) {
        return service.findActiveAndEffective(detailId);
    }

    /**
     * Find entities with inactive status by specified <code>detailId</code>.
     * 
     * @param detailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return List of {@link LtlPalletPricingDetailsEntity}.
     */
    @RequestMapping(value = "/inactive", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlPalletPricingDetailsEntity> getInactive(@PathVariable("detailId") Long detailId) {
        return service.findInactive(detailId);
    }

    /**
     * Makes entity inactive.
     * 
     * @param id
     *            Not <code>null</code> instance of {@link Long}. Primary key of object which should become inactive.
     * @param profileDetailId
     *            Id of Profile Detail {@link LtlPricingProfileDetailsEntity#getId()}.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void inactivate(@RequestBody Long id, @PathVariable("detailId") Long profileDetailId) {
        service.inactivate(id, profileDetailId);
    }

    /**
     * Save list of {@link LtlPalletPricingDetailsEntity}.
     * 
     * @param list
     *            Not <code>null</code> implementation of {@link List} which should be saved.
     * @param profileDetailId
     *            Id of Profile Detail {@link LtlPricingProfileDetailsEntity#getId()}.
     * @throws ValidationException
     *             validation exceptions
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void save(@RequestBody List<LtlPalletPricingDetailsEntity> list, @PathVariable("detailId") Long profileDetailId)
            throws ValidationException {
        service.saveList(list, profileDetailId);
    }

    /**
     * Checks if all zones are available in current profile to copy the pallet details from other profile.
     * 
     * @param detailToCopy
     *            Profile Id to copy from
     * @param detailId
     *            profile to copy to
     * @return true if any zones are missing else false.
     */
    @RequestMapping(value = "/areZonesMissing/{detailToCopy}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVO areZonesMissing(@PathVariable("detailToCopy") Long detailToCopy, @PathVariable("detailId") Long detailId) {
        return new ResponseVO(service.areZonesMissing(detailToCopy, detailId));
    }
}
