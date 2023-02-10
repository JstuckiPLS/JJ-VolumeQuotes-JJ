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

import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.bo.AccessorialListItemVO;
import com.pls.ltlrating.service.LtlAccessorialsService;

/**
 * RESTFul resource for LTL Accessorials
 * 
 * TODO: Only method signatures are valid. Rest data need to be rewritten once the UI approach is finalized
 * which means whether we have to create a new DTO and fill data into it and the same when capturing data from
 * UI. Will complete this class as last task.
 * 
 * @author Hima Bindu Challa
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/profile/{id}/accessorials")
public class LtlAccessorialsResources {

    @Autowired
    private LtlAccessorialsService ltlAccessorialsService;

    /**
     * Clone the Accessorials from "copyFromProfileDetailId" to "copyToProfileDetailId" and save the same.
     * 
     * @param copyFromProfileDetailId
     *            - The profile detail id from which the accessorials should be copied
     * @param id
     *            - The profile detail id to which the accessorials should be copied
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/cloneActive/{copyFromProfileDetailId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyFrom(@PathVariable("copyFromProfileDetailId") Long copyFromProfileDetailId, @PathVariable("id") Long id) {
        ltlAccessorialsService.cloneAccessorials(copyFromProfileDetailId, id, true);
    }

    /**
     * Returns Accessorials by profile id.
     * 
     * @param id
     *            - actual profile id
     * @return instance of {@link PricingProfileDTO} item
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public LtlAccessorialsEntity getAccessorialById(@PathVariable("id") Long id) {
        return ltlAccessorialsService.getAccessorialById(id);
    }

    /**
     * To get All Active and Effective Accessorials (LOCALTIMESTAMP <= expdate) for selected profile detail
     * (Buy/Sell/None).
     * 
     * @param id
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the active accessorials should be retrieved
     * @return List<LtlAccessorialsEntity> - List of all Active and Effective LtlAccessorialsEntities for
     *         selected profile
     */
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    @ResponseBody
    public List<AccessorialListItemVO> getActiveAccessorialsByProfileDetailId(@PathVariable("id") Long id) {
        return ltlAccessorialsService.getActiveAccessorialsByProfileDetailId(id);
    }

    /**
     * To get All active and expired (LOCALTIMESTAMP > expdate) Accessorials for selected profile detail
     * (Buy/Sell/None).
     * 
     * @param id
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the expired accessorials should be
     *            retrieved
     * @return List<LtlAccessorialsEntity> - List of all active and expired LtlAccessorialsEntities for
     *         selected profile
     */
    @RequestMapping(value = "/expired", method = RequestMethod.GET)
    @ResponseBody
    public List<AccessorialListItemVO> getExpiredAccessorialsByProfileDetailId(@PathVariable("id") Long id) {
        return ltlAccessorialsService.getExpiredAccessorialsByProfileDetailId(id);
    }

    /**
     * To get All inactive Accessorials for selected profile detail (Buy/Sell/None).
     * 
     * @param id
     *            - LTL Profile Detail Id (Buy/Sell/None) to which the inactive accessorials should be
     *            retrieved
     * @return List<LtlAccessorialsEntity> - List of all inactive LtlAccessorialsEntities for selected profile
     */
    @RequestMapping(value = "/inactive", method = RequestMethod.GET)
    @ResponseBody
    public List<AccessorialListItemVO> getInactiveAccessorialsByProfileDetailId(@PathVariable("id") Long id) {
        return ltlAccessorialsService.getInactiveAccessorialsByProfileDetailId(id);
    }

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
     * @param ids
     *            - List of LtlAccessorialsEntity Ids - primary keys that need to be inactivated
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved after
     *            inactivating the selected accessorials
     * @param isActiveList
     *            Not <code>null</code> instance of {@link Boolean}.
     * @return List<LtlAccessorialsEntity> - List of all (active & effective) or (expired)
     *         LtlAccessorialsEntities for selected profile
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.POST)
    @ResponseBody
    public List<AccessorialListItemVO> inactivateAccessorials(@PathVariable("id") Long profileDetailId,
            @RequestParam("ids") List<Long> ids, @RequestParam("isActiveList") Boolean isActiveList) {
        return ltlAccessorialsService.inactivateAccessorials(ids, profileDetailId, isActiveList);
    }

    /**
     * To reactivate multiple inactive accessorials. This method returns list of inactive accessorials as the
     * list of accessorials that will be reactivated are inactive accessorials.
     * 
     * @param ids
     *            - List of LtlAccessorialsEntity Ids - primary keys that need to be reactivated
     * @param profileDetailId
     *            - The Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved after
     *            reactivating the selected accessorials
     * @return List<LtlAccessorialsEntity> - List of all inactive LtlAccessorialsEntities for selected profile
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/reactivate", method = RequestMethod.POST)
    @ResponseBody
    public List<AccessorialListItemVO> reactivateAccessorials(@PathVariable("id") Long profileDetailId,
            @RequestParam("ids") List<Long> ids) {
        return ltlAccessorialsService.reactivateAccessorials(ids, profileDetailId);
    }

    /**
     * This method is for both create and update operations. The Save operation returns the updated data
     * (success or roll back) along with other field values - primary key, date created, created by, date
     * modified, modified by, version and will use the same to populate the screen. This is required
     * especially for pessimistic locking.
     * 
     * @param accessorial
     *            - The LtlAccessorialsEntity that need to be saved
     * @return LtlAccessorialsEntity - Updated LTLAccessorialEntity (With date created, created by and
     *         version values)
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public LtlAccessorialsEntity saveAccessorial(@RequestBody LtlAccessorialsEntity accessorial) {
        return ltlAccessorialsService.saveAccessorial(accessorial);
    }

    /**
     * Makes specified records Expired.
     * 
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @param ids
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlAccessorialsEntity} which should be expired.
     * @return {@link List} of {@link LtlAccessorialsEntity}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/expire", method = RequestMethod.POST)
    @ResponseBody
    public List<AccessorialListItemVO> expireAccessorials(@RequestParam("ids") List<Long> ids,
            @PathVariable("id") Long profileDetailId) {
        return ltlAccessorialsService.expirateByListOfIds(ids, profileDetailId);
    }
}
