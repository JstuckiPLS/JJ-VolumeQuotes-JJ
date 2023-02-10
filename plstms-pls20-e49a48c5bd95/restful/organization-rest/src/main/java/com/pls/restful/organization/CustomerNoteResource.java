package com.pls.restful.organization;

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

import com.pls.core.domain.organization.CustomerNoteEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.CustomerNoteService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.validation.ValidationException;
import com.pls.dto.organization.CustomerNoteDTO;
import com.pls.dtobuilder.organization.CustomerNoteDTOBuilder;

/**
 * Customer REST resource.
 * 
 * @author Aleksandr Leshchenko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/note")
public class CustomerNoteResource {
    private final CustomerNoteDTOBuilder builder = new CustomerNoteDTOBuilder();

    @Autowired
    private CustomerNoteService customerNoteService;

    @Autowired
    private UserPermissionsService permissionsService;

    /**
     * Get notes for customer.
     * 
     * @param customerId
     *            id of customer
     * @return list of customer notes
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<CustomerNoteDTO> getCustomerNotes(@PathVariable("customerId") Long customerId) {
        permissionsService.checkCapability(Capabilities.VIEW_ACTIVE_CUSTOMER_PROFILE.name(), Capabilities.CUSTOMER_PROFILE_VIEW.name());
        permissionsService.checkOrganization(customerId);
        return builder.buildList(customerNoteService.getAllCustomerNotes(customerId));
    }

    /**
     * Save note for customer.
     * 
     * @param customerNote
     *            note for customer
     * @throws ValidationException
     *             when note validation fails.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveCustomerNote(@RequestBody CustomerNoteDTO customerNote) throws ValidationException {
        CustomerNoteEntity customerNoteEntity = builder.buildEntity(customerNote);
        customerNoteService.saveCustomerNote(customerNoteEntity);
    }
}
