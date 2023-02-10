package com.pls.dtobuilder.organization;

import com.pls.core.domain.bo.CustomerListItemBO;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.organization.CustomerListItemDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO builder for {@link CustomerListItemDTO}.
 * 
 * @author Aleksandr Leshchenko
 */
public class CustomerListItemDTOBuilder extends AbstractDTOBuilder<CustomerListItemBO, CustomerListItemDTO> {
    @Override
    public CustomerListItemDTO buildDTO(CustomerListItemBO bo) {
        CustomerListItemDTO dto = new CustomerListItemDTO();
        dto.setAccountExecutive(bo.getAccountExecutive());
        dto.setMultipleAccountExecitive(bo.isMultipleAccountExecitive());
        dto.setId(bo.getId());
        dto.setContactFirstName(bo.getContactFirstName());
        dto.setContactLastName(bo.getContactLastName());
        dto.setContract(bo.isContract());
        dto.setEdiNumber(bo.getEdiNumber());
        dto.setEmail(bo.getEmail());
        dto.setName(bo.getName());
        dto.setPhone(getPhone(bo));
        return dto;
    }

    private PhoneBO getPhone(CustomerListItemBO bo) {
        PhoneBO phone = new PhoneBO();
        phone.setAreaCode(bo.getAreaCode());
        phone.setCountryCode(bo.getDialingCode());
        phone.setNumber(bo.getPhoneNumber());
        phone.setExtension(bo.getExtension());
        return phone;
    }

    /**
     * Method is not supported.
     * 
     * @param dto
     *            dto
     * @return nothing
     * @throws UnsupportedOperationException
     */
    @Override
    public CustomerListItemBO buildEntity(CustomerListItemDTO dto) {
        throw new UnsupportedOperationException();
    }

}
