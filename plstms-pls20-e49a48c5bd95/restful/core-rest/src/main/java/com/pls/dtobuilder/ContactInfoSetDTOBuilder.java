package com.pls.dtobuilder;

import com.pls.core.domain.bo.ContactInfoSetBO;
import com.pls.dto.ContactInfoSetDTO;

/**
 * DTO Builder for Contact Info set.
 * 
 * @author Gleb Zgonikov
 */
public class ContactInfoSetDTOBuilder extends AbstractDTOBuilder<ContactInfoSetBO, ContactInfoSetDTO> {

    private static final ContactInfoDTOBuilder CONTACT_INFO_DTO_BUILDER = new ContactInfoDTOBuilder();

    @Override
    public ContactInfoSetDTO buildDTO(ContactInfoSetBO bo) {
        ContactInfoSetDTO dto = new ContactInfoSetDTO();
        dto.setCustomerRep(CONTACT_INFO_DTO_BUILDER.buildDTO(bo.getCustomerRep()));
        dto.setPlsCorporate(CONTACT_INFO_DTO_BUILDER.buildDTO(bo.getPlsCorporate()));
        dto.setSalesRep(CONTACT_INFO_DTO_BUILDER.buildDTO(bo.getSalesRep()));
        dto.setTerminal(CONTACT_INFO_DTO_BUILDER.buildDTO(bo.getTerminal()));
        return dto;
    }

    @Override
    public ContactInfoSetBO buildEntity(ContactInfoSetDTO contactInfoSetDTO) {
        return null;
    }
}
