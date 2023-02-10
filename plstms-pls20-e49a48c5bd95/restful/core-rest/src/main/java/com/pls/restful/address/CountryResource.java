package com.pls.restful.address;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.service.address.CountryService;
import com.pls.dto.address.CountryDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.address.CountryDTOBuilder;

/**
 * Country REST resource.
 * 
 * @author Artem Arapov
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/country")
public class CountryResource {

    private static final AbstractDTOBuilder<CountryEntity, CountryDTO> DTO_BUILDER = new CountryDTOBuilder();

    @Autowired
    private CountryService countryService;

    /**
     * Retrieves countries by search criteria and if omitted will be return all countries.
     * 
     * @param searchCriteria
     *            is the string to be searched if omitted will be return all countries
     * @param count
     *            count of records in result list
     * @return list of countries
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<CountryDTO> searchCountries(@RequestParam("searchCriteria") String searchCriteria,
            @RequestParam(value = "count", defaultValue = "10") Integer count) {
        List<CountryEntity> searchResult;
        if (StringUtils.isNotBlank(searchCriteria)) {
            searchResult = countryService.findCountries(searchCriteria, count);
        } else {
            searchResult = countryService.getAll();
        }
        return DTO_BUILDER.buildList(searchResult);
    }

}
