package com.pls.restful.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.OrganizationFreightBillPayToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.OrganizationFreightBillPayToService;
import com.pls.core.service.UserPermissionsService;
import com.pls.dto.FreightBillPayToDTO;
import com.pls.dto.organization.OrganizationFreightBillPayToDTO;
import com.pls.dtobuilder.FreightBillPayToDTOBuilder;

/**
 * Organization Freight Bill Pay To Resource.
 * 
 * @author Artem Arapov
 *
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/freightBillPayTo")
public class OrganizationFreightBillPayToResource {

    @Autowired
    private OrganizationFreightBillPayToService service;

    @Autowired
    private UserPermissionsService userPermissionsService;

    private static final FreightBillPayToDTOBuilder FREIGHT_BILL_PAY_TO_BUILDER = new FreightBillPayToDTOBuilder();

    /**
     * Returns active {@link OrganizationFreightBillPayToDTO} for specified Customer.
     * <p>
     * <b>Method validates does logged in user has necessary permissions</b>
     * 
     * @param customerId - Not <code>null</code> {@link CustomerEntity#getId()}.
     * @return Active {@link OrganizationFreightBillPayToDTO} if it exists, otherwise returns <code>null</code>.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public FreightBillPayToDTO getFreightBillPayToByOrgId(@PathVariable("customerId") Long customerId) {
        FreightBillPayToEntity entity = service.getActiveFreightBillPayToByOrgId(customerId);

        if (entity != null) {
            return FREIGHT_BILL_PAY_TO_BUILDER.buildDTO(entity);
        } else {
            return new FreightBillPayToDTO();
        }
    }

    /**
     * Save corresponded {@link OrganizationFreightBillPayToEntity}.
     * <p>
     * <b>Before saving method disable all existing entities related to specified Customer</b>
     * <b>Method validates if logged in user has necessary permissions</b>
     * 
     * @param customerId - Not <code>null</code> Customer identifier.
     * @param dto - Not <code>null</code> entity which should be saved.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void save(@PathVariable("customerId") Long customerId, @RequestBody FreightBillPayToDTO dto) {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.PRICING_PAGE_VIEW.name());

        FreightBillPayToEntity freightBillPayTo = FREIGHT_BILL_PAY_TO_BUILDER.buildEntity(dto);
        OrganizationFreightBillPayToEntity entity = new OrganizationFreightBillPayToEntity();
        entity.setFreightBillPayTo(freightBillPayTo);
        entity.setOrgId(customerId);

        service.save(entity);
    }
}
