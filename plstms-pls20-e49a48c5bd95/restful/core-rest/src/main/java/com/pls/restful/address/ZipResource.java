package com.pls.restful.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.bo.AddressBO;
import com.pls.core.service.address.ZipService;
import com.pls.core.service.validation.ValidationException;
import com.pls.dto.address.ZipDTO;
import com.pls.dtobuilder.address.ZipDTOBuilder;

/**
 * Zip related Rest service.
 * 
 * @author Gleb Zgonikov
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/zip")
public class ZipResource {

    private static final ZipDTOBuilder ZIP_DTO_BUILDER = new ZipDTOBuilder();

    @Autowired
    private ZipService zipService;

    /**
     * Method findZips is used to define a list of most matched zips according to search criteria.
     * 
     * @param searchCriteria
     *            of type String
     * @param country
     *            is a country code, optional parameter
     * @param count
     *            is an amount of results to return
     * @return list of the {@link ZipDTO}
     * @throws ValidationException
     *             if criteria is invalid
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<ZipDTO> findZips(@RequestParam(value = "searchCriteria", defaultValue = "") final String searchCriteria,
            @RequestParam(value = "country", defaultValue = "") String country,
            @RequestParam(value = "count", defaultValue = "20") Integer count) throws ValidationException {
        return ZIP_DTO_BUILDER.buildList(zipService.findZips(country, searchCriteria, count));
    }

    /**
     * Validate address.
     *
     * @param address the address
     * @return the address dto
     */
    @RequestMapping(value = "/validate_address", method = RequestMethod.POST)
    @ResponseBody
    public AddressBO validateAddress(@RequestBody(required = false) AddressBO address) {
        return zipService.validateAddress(address);
    }
}
