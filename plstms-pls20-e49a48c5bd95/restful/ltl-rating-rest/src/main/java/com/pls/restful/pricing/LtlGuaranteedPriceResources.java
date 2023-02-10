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
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;
import com.pls.ltlrating.domain.bo.GuaranteedPriceListItemVO;
import com.pls.ltlrating.service.LtlGuaranteedPriceService;

/**
 * RESTful resource for Ltl Guaranteed Price.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/ltlGuaranteedPrice")
public class LtlGuaranteedPriceResources {

    @Autowired
    private LtlGuaranteedPriceService service;

    /**
     * Copying Guaranteed from one profile to another.
     * 
     * @param profileToCopy
     *            Copy from profile id. Not <code>null</code> value of {@link Long}
     * @param profileId
     *            Copy to profile id. Not <code>null</code> value of {@link Long}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copyfrom", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyFrom(@RequestParam("profileToCopy") Long profileToCopy,
            @RequestParam("profileId") Long profileId) {
        service.copyFrom(profileToCopy, profileId, true);
    }

    /**
     * Get list of {@link GuaranteedPriceListItemVO} with active status and effective date >= current date.
     * 
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link GuaranteedPriceListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/profile/{profileDetailId}/active", method = RequestMethod.GET)
    @ResponseBody
    public List<GuaranteedPriceListItemVO> getActiveGuaranteedByProfileDetailId(
            @PathVariable("profileDetailId") Long profileDetailId) {
        return service.getActiveGuaranteedByProfileDetailId(profileDetailId);
    }

    /**
     * Get list of {@link LtlGuaranteedPriceEntity} by specified profile id.
     * 
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link LtlGuaranteedPriceEntity} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/profile/{profileDetailId}/list", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlGuaranteedPriceEntity> getAllGuaranteedByProfileDetailId(
            @PathVariable("profileDetailId") Long profileDetailId) {
        return service.getAllGuaranteedByProfileDetailId(profileDetailId);
    }

    /**
     * Get list of {@link GuaranteedPriceListItemVO} with expired effective dates.
     * 
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link GuaranteedPriceListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/profile/{profileDetailId}/expired", method = RequestMethod.GET)
    @ResponseBody
    public List<GuaranteedPriceListItemVO> getExpiredGuaranteedByProfileDetailId(
            @PathVariable("profileDetailId") Long profileDetailId) {
        return service.getExpiredGuaranteedByProfileDetailId(profileDetailId);
    }

    /**
     * Get {@link LtlGuaranteedPriceEntity} object by pricing detail id.
     * 
     * @param id
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlGuaranteedPriceEntity} if it was found, otherwise return
     *         <code>null</code>
     */
    @RequestMapping(value = "/{guaranteeId}", method = RequestMethod.GET)
    @ResponseBody
    public LtlGuaranteedPriceEntity getGuaranteedPriceById(@PathVariable("guaranteeId") Long id) {
        return service.getGuaranteedPriceById(id);
    }

    /**
     * Get list of {@link GuaranteedPriceListItemVO} with inactive status.
     * 
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link GuaranteedPriceListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/profile/{profileDetailId}/inactive", method = RequestMethod.GET)
    @ResponseBody
    public List<GuaranteedPriceListItemVO> getInactiveGuaranteedByProfileDetailId(
            @PathVariable("profileDetailId") Long profileDetailId) {
        return service.getInactiveGuaranteedByProfileDetailId(profileDetailId);
    }

    /**
     * To archive multiple guaranteed price. Return list of active or expired guaranteed price based on the
     * boolean flag "isActiveList". If flag is yes, the guaranteed price are picked from "Active" grid and so
     * need to return updated "Active" list using method
     * "getActiveGuaranteedByProfileDetailId(Long profileDetailId);" else return updated "Expired" list using
     * method "getExpiredGuaranteedByProfileDetailId(Long profileDetailId);" method. In UI, "Edit" and
     * "Archive" fields should be disabled when user selects "Archived" tab.
     * 
     * @param guaranteedIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlGuaranteedPriceEntity} which should be saved.
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @param isActiveList
     *            Not <code>null</code> instance of {@link Boolean}.
     * @return {@link Response}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<GuaranteedPriceListItemVO> inactivateGuaranteedPricings(@RequestBody List<Long> guaranteedIds,
            @RequestParam("profileDetailId") Long profileDetailId,
            @RequestParam("isActiveList") boolean isActiveList) {
        return service.inactivateGuaranteedPricings(guaranteedIds, profileDetailId, isActiveList);
    }

    /**
     * To reactivate multiple guaranteed price. Return updated list of inactive using method
     * {@link LtlGuaranteedPriceService#getInactiveGuaranteedByProfileDetailId(Long)}.
     * 
     * @param guaranteedIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlGuaranteedPriceEntity} which should be saved.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link Response}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/reactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<GuaranteedPriceListItemVO> reactivateGuaranteedPricings(@RequestBody List<Long> guaranteedIds,
            @RequestParam("profileDetailId") Long profileDetailId) {
        return service.reactivateGuaranteedPricings(guaranteedIds, profileDetailId);
    }

    /**
     * Save and update {@link LtlGuaranteedPriceEntity}.
     * 
     * @param entity
     *            Not <code>null</code> instance of {@link LtlGuaranteedPriceEntity} which should be saved.
     * @return persisted object of {@link LtlGuaranteedPriceEntity} class.
     * @throws ValidationException
     *             Validation Exception.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public LtlGuaranteedPriceEntity saveGuaranteedPrice(@RequestBody LtlGuaranteedPriceEntity entity)
            throws ValidationException {
        return service.saveGuaranteedPrice(entity);
    }

    /**
     * Makes specified records Expired.
     * 
     * @param guaranteedIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlGuaranteedPriceEntity} which should be expired.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link LtlGuaranteedPriceEntity}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/expire", method = RequestMethod.POST)
    @ResponseBody
    public List<GuaranteedPriceListItemVO> expireGuaranteedPricings(@RequestBody List<Long> guaranteedIds,
            @RequestParam("profileDetailId") Long profileDetailId) {
        return service.expireGuaranteedPricings(guaranteedIds, profileDetailId);
    }
}
