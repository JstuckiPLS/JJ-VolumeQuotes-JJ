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

import com.pls.dto.LtlAccessorialsMappingDTO;
import com.pls.dtobuilder.pricing.LtlAccessorialsMappingDTOBuilder;
import com.pls.dtobuilder.pricing.LtlAccessorialsMappingDTOBuilder.DataProvider;
import com.pls.ltlrating.domain.LtlAccessorialsMappingEntity;
import com.pls.ltlrating.service.LtlAccessorialsService;

/**
 * 
 * RESTFul resource for LTL Accessorials Mapping
 * For each carrier there would be an abitity to map PLS existing accessorials to Carrier's accessorials.
 * 
 * @author Dmitriy Davydenko.
 */
@Controller
@Transactional
@RequestMapping("/carrierAccessorials")
public class LtlAccessorialsMappingResource {

    @Autowired
    private LtlAccessorialsService ltlAccessorialsService;

    private final LtlAccessorialsMappingDTOBuilder builder = new LtlAccessorialsMappingDTOBuilder(new DataProvider() {
        @Override
        public LtlAccessorialsMappingEntity getById(Long id) {
            return ltlAccessorialsService.getAccMappingById(id);
        }
    });
    /**
     * Gets accessorials mapping for selected Carrier.
     * 
     * @param carrierId - id of the carrier.
     * @return List<AccessorialsMappingDTO> - List of AccessorialsMappingDTOs.
     */
    @RequestMapping(value = "/getMapping/{carrierId}", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlAccessorialsMappingDTO> getAccessorialsMapping(@PathVariable("carrierId") Long carrierId) {
        return builder.buildList(ltlAccessorialsService.getAccessorialsMapping(carrierId));
    }

    /**
     * Saves mapped accessorials for selected carrier.
     * 
     * @param dto - accessorials data to be saved.
     */
    @RequestMapping(value = "/saveMapping", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveAccessorialsMapping(@RequestBody List<LtlAccessorialsMappingDTO> dto) {
        ltlAccessorialsService.saveAccessorialsMapping(builder.buildEntityList(dto));
    }
}
