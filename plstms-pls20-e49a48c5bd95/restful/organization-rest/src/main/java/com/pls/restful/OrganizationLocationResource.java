package com.pls.restful;

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

import com.pls.core.domain.bo.AssociatedCustomerLocationBO;
import com.pls.core.domain.bo.CustomerLocationListItemBO;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.dto.organization.CustomerLocationDTO;
import com.pls.dtobuilder.organization.CustomerLocationDTOBuilder;
import com.pls.location.service.OrganizationLocationService;


/**
 * Customer REST resource.
 * 
 * @author Aleksandr Leshchenko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/location")
public class OrganizationLocationResource {

    private final CustomerLocationDTOBuilder locationDtoBuilder = new CustomerLocationDTOBuilder(new CustomerLocationDTOBuilder.DataProvider() {

        @Override
        public OrganizationLocationEntity getOrganizationLocation(Long id) {
            return service.getOrganizationLocation(id);
        }
    });

    @Autowired
    private OrganizationLocationService service;

    @Autowired
    private UserPermissionsService permissionsService;

    /**
     * Get list of locations for specified customer with information for specified user that current user has access to.
     * 
     * @param customerId
     *            id of customer.
     * @param userId
     *            id of user to get modification information
     * @return list of locations for specified customer that current user has access to.
     */
    @RequestMapping(value = "associated", method = RequestMethod.GET)
    @ResponseBody
    public List<AssociatedCustomerLocationBO> getAssociatedCustomerLocations(@PathVariable("customerId") Long customerId,
            @RequestParam(value = "userId", required = false) Long userId) {
        return service.getAssociatedCustomerLocations(customerId, SecurityUtils.getCurrentPersonId(), userId);
    }

    /**
     * Get list of customers' locations available for shipment creation for current user.
     * 
     * @param customerId
     *            id of customer.
     * @return list of shipment locations for customer.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentLocationBO> getLocations(@PathVariable("customerId") Long customerId) {
        return service.getShipmentLocations(customerId, SecurityUtils.getCurrentPersonId());
    }

    /**
     * Get list of locations for specified customer.
     * 
     * @param customerId
     *            id of customer.
     * @return list of locations.
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<CustomerLocationListItemBO> getCustomerLocations(@PathVariable("customerId") Long customerId) {
        permissionsService.checkCapability(Capabilities.VIEW_ACTIVE_CUSTOMER_PROFILE.name(), Capabilities.CUSTOMER_PROFILE_VIEW.name());
        permissionsService.checkOrganization(customerId);
        return service.getCustomerLocations(customerId);
    }

    /**
     * Returns {@link OrganizationLocationEntity} by specified Id.
     * 
     * @param id Location Id.
     * @return {@link OrganizationLocationEntity}.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CustomerLocationDTO getOrganizationLocation(@PathVariable("id") Long id) {
        return locationDtoBuilder.buildDTO(service.getOrganizationLocation(id));
    }

    /**
     * Returns {@link UserAdditionalContactInfoBO} by specified customer and location Id.
     * 
     * @param customerId id of customer.
     * @param locationId id of location.
     * @return {@link UserAdditionalContactInfoBO}.
     */
    @RequestMapping(value = "/{locationId}/accountexecutive", method = RequestMethod.GET)
    @ResponseBody
    public UserAdditionalContactInfoBO getOrganizationAccountExecutive(@PathVariable("customerId") Long customerId,
            @PathVariable("locationId") Long locationId) {
        permissionsService.checkOrganization(customerId);
        return service.getAccountExecutiveByLocationId(locationId);
    }

    /**
     * Save specified {@link OrganizationLocationEntity}.
     * 
     * @param customerId - id of customer organization.
     * @param dto - customer location to be saved.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveOrganizationLocation(@PathVariable("customerId") Long customerId, @RequestBody CustomerLocationDTO dto) {
        //TODO: Instead of OrganizationLocationEntity should to get AccountExecutiveEntity what was previously assigned to that location.
        OrganizationLocationEntity oldLocation = service.getOrganizationLocation(dto.getId());
        Long personId = null;
        if (oldLocation != null) {
            AccountExecutiveEntity oldAE = oldLocation.getActiveAccountExecutive();
            if (oldAE != null) {
                personId = oldAE.getPersonId();
            }
        }
        dto.setOrgId(customerId);
        OrganizationLocationEntity entity = locationDtoBuilder.buildEntity(dto);

        service.saveOrganizationLocation(entity, personId);
    }
}
