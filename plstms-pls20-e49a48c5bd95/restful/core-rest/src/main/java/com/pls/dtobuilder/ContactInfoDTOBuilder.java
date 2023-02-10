package com.pls.dtobuilder;

import com.pls.core.domain.bo.ContactInfoBO;
import com.pls.dto.ContactInfoDTO;

/**
 * DTO for Contact Info.
 * 
 * @author Gleb Zgonikov
 */
public class ContactInfoDTOBuilder extends AbstractDTOBuilder<ContactInfoBO, ContactInfoDTO> {
    @Override
    public ContactInfoDTO buildDTO(ContactInfoBO bo) {
        ContactInfoDTO dto = new ContactInfoDTO();
        dto.setEmail(bo.getEmail());
        dto.setFax(bo.getFax());
        dto.setName(bo.getName());
        dto.setPhone(bo.getPhone());
        return dto;
    }

    @Override
    public ContactInfoBO buildEntity(ContactInfoDTO contactInfoDTO) {
        return null;
    }
}
