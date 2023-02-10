package com.pls.restful.pricing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.service.validation.ValidationException;
import com.pls.dto.LtlBlockLaneDTO;
import com.pls.dtobuilder.pricing.LtlBlockLaneDTOBuilder;
import com.pls.ltlrating.domain.LtlBlockLaneEntity;
import com.pls.ltlrating.domain.bo.BlanketCarrListItemVO;
import com.pls.ltlrating.domain.bo.BlockLaneListItemVO;
import com.pls.ltlrating.service.LtlBlockLaneService;

/**
 * RESTful resource for blocking lanes of specific/all blanket carriers for particular customer.
 * 
 * @author Ashwini Neelgund
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/blockLane")
public class LtlBlockLaneResource {

    private final LtlBlockLaneDTOBuilder builder = new LtlBlockLaneDTOBuilder();

    @Autowired
    private LtlBlockLaneService service;

    /**
     * Get list of {@link BlockLaneListItemVO} with active status and effective date >= current date.
     * 
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/profile/{profileId}/active", method = RequestMethod.GET)
    @ResponseBody
    public List<BlockLaneListItemVO> getActiveBlockedLanesByProfileId(@PathVariable("profileId") Long profileId) {
        return service.getActiveBlockedLanesByProfileId(profileId);
    }

    /**
     * Get list of {@link BlockLaneListItemVO} with expired effective dates.
     * 
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/profile/{profileId}/expired", method = RequestMethod.GET)
    @ResponseBody
    public List<BlockLaneListItemVO> getExpiredGuaranteedByProfileDetailId(@PathVariable("profileId") Long profileId) {
        return service.getExpiredBlockLaneByProfileId(profileId);
    }

    /**
     * Get list of {@link BlockLaneListItemVO} with inactive status.
     * 
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/profile/{profileId}/inactive", method = RequestMethod.GET)
    @ResponseBody
    public List<BlockLaneListItemVO> getInactiveGuaranteedByProfileDetailId(@PathVariable("profileId") Long profileId) {
        return service.getInactiveBlockLaneByProfileId(profileId);
    }

    /**
     * Returns List of unblocked blanket carrier profiles for the customer.
     * 
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return list of {@link BlanketCarrListItemVO} items
     */
    @RequestMapping(value = "/blanketCarrList/{profileId}", method = RequestMethod.GET)
    @ResponseBody
    public List<BlanketCarrListItemVO> getApplicableBlanketCarrListForCust(@PathVariable("profileId") Long profileId) {
        return service.getApplicableBlanketCarrListForCust(profileId);
    }

    /**
     * Save or update Blocked Lane set on all/single carrier for specific customer.
     * 
     * @param dto
     *            Not <code>null</code> instance of {@link LtlBlockLaneDTO} which should be saved.
     * @return persisted object of {@link LtlBlockLaneDTO} class.
     * @throws ValidationException
     *             validation exceptions
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public LtlBlockLaneDTO saveBlockedLane(@RequestBody LtlBlockLaneDTO dto) throws ValidationException {
        return builder.buildDTO(service.saveBlockedLane(builder.buildEntity(dto)));
    }

    /**
     * Get {@link LtlBlockLaneEntity} object by primary key id.
     * 
     * @param blockId
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlBlockLaneEntity} if it was found, otherwise return <code>null</code>
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public BlockLaneListItemVO getBlockedLaneById(@PathVariable("id") Long blockId) {
        return service.getBlockedLaneById(blockId);
    }

    /**
     * Makes specified records Expired.
     * 
     * @param blockedLaneIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of {@link LtlBlockLaneEntity}
     *            which should be expired.
     * @param profileId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/expire", method = RequestMethod.POST)
    @ResponseBody
    public List<BlockLaneListItemVO> expireBlockedLanes(@RequestBody List<Long> blockedLaneIds,
            @RequestParam("profileId") Long profileId) {
        return service.expireBlockedLanes(blockedLaneIds, profileId);
    }

    /**
     * To archive multiple blocked lanes. Return list of active or expired blocked lanes based on the boolean
     * flag "isActiveList". If flag is yes, the blocked lanes are picked from "Active" grid and so need to
     * return updated "Active" list else return updated "Expired" list.
     * 
     * @param blockedLaneIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of {@link LtlBlockLaneEntity}
     *            which should be saved.
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @param isActive
     *            Not <code>null</code> instance of {@link Boolean}.
     * @return {@link List} of {@link BlockLaneListItemVO}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<BlockLaneListItemVO> inactivateBlockedLanes(@RequestBody List<Long> blockedLaneIds,
            @RequestParam("profileId") Long profileId, @RequestParam("isActive") boolean isActive) {
        return service.inactivateBlockedLanes(blockedLaneIds, profileId, isActive);
    }

    /**
     * To reactivate multiple blocked lanes. Return updated list of inactive blocked lanes.
     * 
     * @param blockedLaneIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of {@link LtlBlockLaneEntity}
     *            which should be saved.
     * @param profileId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/reactivate", method = RequestMethod.PUT)
    @ResponseBody
    public List<BlockLaneListItemVO> reactivateBlockedLanes(@RequestBody List<Long> blockedLaneIds,
            @RequestParam("profileId") Long profileId) {
        return service.reactivateBlockedLanes(blockedLaneIds, profileId);
    }

}
