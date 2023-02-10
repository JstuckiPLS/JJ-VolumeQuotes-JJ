package com.pls.dtobuilder.organization;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.OrganizationFaxPhoneEntity;
import com.pls.core.domain.organization.OrganizationVoicePhoneEntity;
import com.pls.dto.organization.OrganizationDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.address.AddressBookEntryDTOBuilder;

/**
 * Builder for conversion organization entity to organization profile DTO.
 * 
 * @author Alexander Nalapko
 */
public class OrganizationDTOBuilder extends AbstractDTOBuilder<OrganizationEntity, OrganizationDTO> {
    @Override
    public OrganizationEntity buildEntity(OrganizationDTO dto) {
        OrganizationEntity org = new OrganizationEntity();
        org.setName(dto.getName());

        OrganizationStatus status;
        if (dto.isActive()) {
            status = OrganizationStatus.ACTIVE;
        } else {
            status = OrganizationStatus.INACTIVE;
        }
        org.setStatus(status);

        org.setContract(dto.isContract());
        org.setFederalTaxId(dto.getFederalTaxId());
        org.setContactFirstName(dto.getContactFirstName());
        org.setContactLastName(dto.getContactLastName());
        org.setContactEmail(dto.getContactEmail());

        if (dto.getAddress() != null) {
            AddressEntity address = new AddressBookEntryDTOBuilder().buildEntity(dto.getAddress());
            org.setAddress(address);
        }

        if (dto.getContactPhone() != null && !dto.getContactPhone().isEmpty()) {
            OrganizationVoicePhoneEntity phone = new OrganizationVoicePhoneEntity();
            phone.setPhoneNumber(dto.getContactPhone());
            phone.setOrganization(org);
            org.setPhone(phone);
        }

        if (dto.getContactFax() != null && !dto.getContactFax().isEmpty()) {
            OrganizationFaxPhoneEntity fax = new OrganizationFaxPhoneEntity();
            fax.setPhoneNumber(dto.getContactFax());
            fax.setOrganization(org);
            org.setFax(fax);
        }

        return org;
    }

    @Override
    public OrganizationDTO buildDTO(OrganizationEntity org) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setName(org.getName());

        dto.setContract(org.isContract());
        dto.setFederalTaxId(org.getFederalTaxId());

        dto.setContactFirstName(org.getContactFirstName());
        dto.setContactLastName(org.getContactLastName());
        dto.setContactEmail(org.getContactEmail());
        return dto;
    }
}
