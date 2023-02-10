package com.pls.restful.organization;

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

import com.pls.core.domain.bo.SimpleCarrierInfoBO;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.service.CarrierService;
import com.pls.dto.organization.CarrierSettingsDTO;
import com.pls.dto.organization.CarrierInfoDTO;
import com.pls.dtobuilder.organization.CarrierSettingsDTOBuilder;
import com.pls.dtobuilder.organization.CarrierSettingsDTOBuilder.DataProvider;
import com.pls.dtobuilder.organization.CarrierInfoDTOBuilder;

/**
 * Carrier REST resource.
 * 
 * @author Alexander Nalapko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/carrier")
public class CarrierResource {

    @Autowired
    private CarrierService service;

    private static final CarrierInfoDTOBuilder DTO_BUILDER = new CarrierInfoDTOBuilder();

    private final CarrierSettingsDTOBuilder carrierDtoBuilder = CarrierSettingsDTOBuilder.builder().addDataProvider(new DataProvider() {

        @Override
        public CarrierEntity getCarrierEntity(Long id) {
            return service.findCarrierById(id);
        }
    });

    /**
     * Find Carriers by name.
     * 
     * @param name
     *            carrier name
     * @param limit
     *            page size
     * @param offset
     *            pages
     * @return list of carriers
     */
    @RequestMapping(value = "/list/byName/{name}", method = RequestMethod.GET)
    @ResponseBody
    public List<CarrierInfoDTO> findCarrierByName(@PathVariable("name") String name,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset) {

        return DTO_BUILDER.buildList(service.findCarrierByName(name, limit, offset));
    }

    /**
     *  Fetches carrier infos by specified criteria.
     * 
     * @param carrier
     *          carrier name
     * @param scac
     *          carrier scac code
     * @param status
     *          carrier status
     * @return list of carriers matching search criteria
     */
    @RequestMapping(value = "/carrierInfos", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleCarrierInfoBO> getCarrierInfos(@RequestParam(value = "carrier", defaultValue = "", required = false) String carrier,
            @RequestParam(value = "scacCode", defaultValue = "", required = false) String scac,
            @RequestParam(value = "status", defaultValue = "", required = false) String status) {
        return service.getCarrierInfos(carrier, scac, status);
    }

    /**
     *  Retrieves default carrier.
     * 
     * @return - carrier entity
     */
    @RequestMapping(value = "/default", method = RequestMethod.GET)
    @ResponseBody
    public CarrierInfoDTO getDefaultCarrier() {
        return DTO_BUILDER.buildDTO(service.getDefaultCarrier());
    }

    /**
     * Returns carrier by specified id.
     * 
     * @param id - Not <code>null</code> {@link CarrierEntity#getId()}.
     * @return {@link CarrierSettingsDTO}.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CarrierSettingsDTO getCarrierById(@PathVariable("id") Long id) {
        return carrierDtoBuilder.buildDTO(service.findCarrierById(id));
    }

    /**
     * Save corresponded {@link CarrierSettingsDTO}.
     * 
     * @param carrier - {@link CarrierSettingsDTO}.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void saveCarrier(@RequestBody CarrierSettingsDTO carrier) {
        CarrierEntity entity = carrierDtoBuilder.buildEntity(carrier);
        service.save(entity);
    }
}
