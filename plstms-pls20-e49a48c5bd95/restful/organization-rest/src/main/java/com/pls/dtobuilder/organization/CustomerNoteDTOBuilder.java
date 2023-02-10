package com.pls.dtobuilder.organization;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.CustomerNoteEntity;
import com.pls.dto.organization.CustomerNoteDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * Builder for conversion customer note entity to customer note DTO.
 * 
 * @author Aleksandr Leshchenko
 */
public class CustomerNoteDTOBuilder extends AbstractDTOBuilder<CustomerNoteEntity, CustomerNoteDTO> {

    @Override
    public CustomerNoteDTO buildDTO(CustomerNoteEntity entity) {
        CustomerNoteDTO dto = new CustomerNoteDTO();
        dto.setId(entity.getId());
        dto.setCreatedDate(ZonedDateTime.ofInstant(entity.getModification().getCreatedDate().toInstant(), ZoneOffset.UTC));
        dto.setUsername(UserNameBuilder.buildFullName(entity.getModification().getCreatedUser()));
        dto.setCustomerId(entity.getModification().getCreatedBy());
        dto.setText(entity.getNote());
        return dto;
    }

    @Override
    public CustomerNoteEntity buildEntity(CustomerNoteDTO dto) {
        CustomerNoteEntity entity = new CustomerNoteEntity();
        entity.setNote(dto.getText());
        Long customerId = dto.getCustomerId();
        entity.setCustomer(getCustomer(customerId));
        return entity;
    }

    /**
     * Creates customer entity by ID. Probably needs to be rewritten to get it from BD.
     * 
     * @param customerId
     *            the ID of customer
     * @return customer entity
     */
    public CustomerEntity getCustomer(Long customerId) {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(customerId);
        return customer;
    }

}
