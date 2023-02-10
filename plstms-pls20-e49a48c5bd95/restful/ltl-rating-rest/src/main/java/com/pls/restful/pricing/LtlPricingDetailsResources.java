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
import com.pls.dto.PricingDetailDTO;
import com.pls.dto.pricing.PricingDetailsDictionaryDTO;
import com.pls.dtobuilder.pricing.PricingDetailDTOBuilder;
import com.pls.dtobuilder.pricing.PricingDetailDTOBuilder.DataProvider;
import com.pls.dtobuilder.pricing.PricingDetailsDictionaryDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.bo.PricingDetailListItemVO;
import com.pls.ltlrating.service.LtlPricingDetailsService;
import com.pls.smc3.service.SMC3Service;

/**
 * RESTful resource for Ltl Pricing Details.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/profile/{profileId}/pricing")
public class LtlPricingDetailsResources {

    private final PricingDetailDTOBuilder dtoBuilder = new PricingDetailDTOBuilder(new DataProvider() {
        @Override
        public LtlPricingDetailsEntity getById(Long id) {
            return service.getPricingDetailById(id);
        }
    });

    @Autowired
    private LtlPricingDetailsService service;

    @Autowired
    private SMC3Service smc3Service;

    /**
     * Copying Pricing Details from one profile to another.
     * 
     * @param profileToCopy
     *            Copy from profile id. Not <code>null</code> value of {@link Long}
     * @param profileId
     *            Copy to profile id. Not <code>null</code> value of {@link Long}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copyfrom/{profileToCopy}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyFrom(@PathVariable("profileToCopy") Long profileToCopy, @PathVariable("profileId") Long profileId) {
        service.copyFrom(profileToCopy, profileId);
    }

    /**
     * Returns default dictionary data for Pricing Details screen.
     * 
     * @return {@link PricingDetailsDictionaryDTO}.
     * @throws Exception - exception thrown when getting smc3 tariffs.
     */
    @RequestMapping(value = "/dictionary", method = RequestMethod.GET)
    @ResponseBody
    public PricingDetailsDictionaryDTO getDictionary() throws Exception {
        PricingDetailsDictionaryDTO dto = new PricingDetailsDictionaryDTOBuilder().buildDTO(null);
        dto.setSmc3Tariffs(smc3Service.getAvailableTariffs());

        return dto;
    }

    /**
     * Get {@link PricingDetailDTO} object by pricing detail id.
     * 
     * @param id
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link PricingDetailDTO} if it was found
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public PricingDetailDTO getPricingDetailById(@PathVariable("id") Long id) {
        return dtoBuilder.buildDTO(service.getPricingDetailById(id));
    }

    /**
     * To retrieve Pricing Details for editing/viewing in Module 4 and Viewing in Module 6 - retrieve all
     * active and effective (LOCALTIMESTAMP <= expdate) pricing details for selected profile and display them on the
     * grid in the screen - in the active tab.
     * 
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link PricingDetailListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    @ResponseBody
    public List<PricingDetailListItemVO> getShortActivePricingDetailsByProfileDetailId(@PathVariable("profileId") Long profileDetailId) {
        return service.getActivePricingDetailsByProfileDetailId(profileDetailId);
    }

    /**
     * To retrieve Pricing Details for editing/viewing in Module 4 and Viewing in Module 6 - retrieve all
     * active and expired (LOCALTIMESTAMP > expdate) pricing details for selected profile and display them on the
     * grid in the screen - in the expired tab.
     * 
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link PricingDetailListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/expired", method = RequestMethod.GET)
    @ResponseBody
    public List<PricingDetailListItemVO> getShortExpiredPricingDetailsByProfileDetailId(@PathVariable("profileId") Long profileDetailId) {
        return service.getExpiredPricingDetailsByProfileDetailId(profileDetailId);
    }

    /**
     * To retrieve Pricing Details for viewing in Module 4 and Module 6 - retrieve all inactive pricing
     * details for selected profile and display them on the grid in the screen - in the archive tab.
     * 
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link PricingDetailListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(value = "/inactive", method = RequestMethod.GET)
    @ResponseBody
    public List<PricingDetailListItemVO> getShortInactivePricingDetailsByProfileDetailId(@PathVariable("profileId") Long profileDetailId) {
        return service.getInactivePricingDetailsByProfileDetailId(profileDetailId);
    }

    /**
     * To archive multiple pricing details. Return list of active or expired pricing details based on the
     * boolean flag "isActiveList". If flag is yes, the pricing details are picked from "Active" grid and so
     * need to return updated "Active" list using method "getActivePricingDetailsByProfileId(Long profileId);"
     * else return updated "Expired" list using method "getExpiredPricingDetailsByProfileId(Long profileId);"
     * method. In UI, "Edit", "Copy From" and "Archive" fields should be disabled when user selects "Archived"
     * tab.
     * 
     * @param ids
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlPricingDetailsEntity} which should be saved.
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @param isActiveList
     *            Not <code>null</code> instance of {@link Boolean}.
     * @return {@link List} of {@link PricingDetailListItemVO}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.POST)
    @ResponseBody
    public List<PricingDetailListItemVO> inactivatePricingDetails(@PathVariable("profileId") Long profileDetailId, @RequestBody List<Long> ids,
            @RequestParam("isActiveList") Boolean isActiveList) {
        return service.inactivatePricingDetails(ids, profileDetailId, isActiveList);
    }

    /**
     * To reactivate multiple pricing details when the user is in Archived tab. Return updated list of
     * archived pricing details using method "getInactivePricingDetailsByProfileId(Long profileId);". In UI,
     * "Edit", "Copy From" fields should be disabled when user selects "Archived" tab and "Archive" button
     * should be changed to "Re-activate". This is missing in UI and should be added to allow the user to have
     * the flexibility of correcting their mistakes
     * 
     * @param ids
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlPricingDetailsEntity} which should be saved.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link PricingDetailListItemVO}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/reactivate", method = RequestMethod.POST)
    @ResponseBody
    public List<PricingDetailListItemVO> reactivatePricingDetails(@PathVariable("profileId") Long profileDetailId, @RequestBody List<Long> ids) {
        return service.reactivatePricingDetails(ids, profileDetailId);
    }

    /**
     * Makes specified records Expired.
     * 
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @param ids
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlPricingDetailsEntity} which should be expired.
     * @return {@link List} of {@link PricingDetailListItemVO}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/expirate", method = RequestMethod.POST)
    @ResponseBody
    public List<PricingDetailListItemVO> expiratePricingDetails(@PathVariable("profileId") Long profileDetailId, @RequestBody List<Long> ids) {
        return service.expiratePricingDetails(ids, profileDetailId);
    }

    /**
     * Save Pricing detail.
     * 
     * @param dto
     *            Not <code>null</code> instance of {@link PricingDetailDTO} which should be saved.
     * @throws ValidationException
     *             validation exceptions
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void savePricingDetail(@RequestBody PricingDetailDTO dto) throws ValidationException {
        service.savePricingDetail(dtoBuilder.buildEntity(dto));
    }
}
