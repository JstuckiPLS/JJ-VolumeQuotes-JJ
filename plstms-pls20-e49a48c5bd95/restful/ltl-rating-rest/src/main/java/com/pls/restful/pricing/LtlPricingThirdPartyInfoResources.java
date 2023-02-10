package com.pls.restful.pricing;

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
import com.pls.dto.LtlThirdPartyInfoDTO;
import com.pls.dtobuilder.pricing.LtlThirdPartyInfoDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;
import com.pls.ltlrating.service.LtlPricingThirdPartyInfoService;

/**
 * RESTful resource for Ltl Pricing Third Party Info.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/profile/{profileId}/thirdParty")
public class LtlPricingThirdPartyInfoResources {

    private final LtlThirdPartyInfoDTOBuilder builder = new LtlThirdPartyInfoDTOBuilder();

    @Autowired
    private LtlPricingThirdPartyInfoService service;

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
        service.copyFrom(profileToCopy, profileId, true);
    }

    /**
     * Get {@link LtlPricingThirdPartyInfoEntity} by profile detail id.
     * 
     * @param profileDetailId
     *            Not <code>null</code> {@link Long}
     * @return {@link LtlPricingThirdPartyInfoEntity}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public LtlThirdPartyInfoDTO getActiveThirdPartyInfoByProfileId(
            @PathVariable("profileId") Long profileDetailId) {
        return builder.buildDTO(service.getActiveThirdPartyInfoByProfileDetailId(profileDetailId));
    }

    /**
     * The Save operation should return the updated data (succes or roll back) along with other field values -
     * primary key, date created, created by, date modified, modified by, version.
     * 
     * @param dto
     *            Not <code>null</code> DTO to save {@link LtlThirdPartyInfoDTO}.
     * @return {@link LtlPricingThirdPartyInfoEntity} saved entity.
     * @throws ValidationException
     *             Validation Exception
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public LtlPricingThirdPartyInfoEntity saveThirdPartyInfo(@RequestBody LtlThirdPartyInfoDTO dto)
            throws ValidationException {
        return service.saveThirdPartyInfo(builder.buildEntity(dto));
    }
}
