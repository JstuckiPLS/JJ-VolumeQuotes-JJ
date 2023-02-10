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
import com.pls.dto.LtlTerminalInfoDTO;
import com.pls.dtobuilder.pricing.LtlTerminalInfoDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;
import com.pls.ltlrating.service.LtlPricingTerminalInfoService;

/**
 * RESTful resource for Ltl Pricing Terminal Info.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/profile/{profileId}/terminalInfo")
public class LtlPricingTerminalInfoResources {

    private final LtlTerminalInfoDTOBuilder builder = new LtlTerminalInfoDTOBuilder();

    @Autowired
    private LtlPricingTerminalInfoService service;

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
    public void copyFrom(@PathVariable("profileToCopy") Long profileToCopy,
            @PathVariable("profileId") Long profileId) {
        service.copyFrom(profileToCopy, profileId, true);
    }

    /**
     * Get {@link LtlPricingTerminalInfoEntity} by profile detail id.
     * 
     * @param profileDetailId
     *            Not <code>null</code> {@link Long}
     * @return {@link LtlPricingTerminalInfoEntity}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public LtlTerminalInfoDTO getActiveCarrierTerminalInfoByProfileDetailId(
            @PathVariable("profileId") Long profileDetailId) {
        return builder.buildDTO(service.getActiveCarrierTerminalInfoByProfileDetailId(profileDetailId));
    }

    /**
     * This method is for both create and update operations. This operation return the updated data (succes or
     * roll back) along with other field values - primary key, date created, created by, date modified,
     * modified by, version.
     * 
     * @param dto
     *            DTO which should be saved.
     * 
     * @return {@link LtlPricingTerminalInfoEntity}
     * @throws ValidationException
     *             Validation Exception
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public LtlPricingTerminalInfoEntity saveCarrierTerminalInfo(@RequestBody LtlTerminalInfoDTO dto)
            throws ValidationException {
        return service.saveCarrierTerminalInfo(builder.buildEntity(dto));
    }
}
