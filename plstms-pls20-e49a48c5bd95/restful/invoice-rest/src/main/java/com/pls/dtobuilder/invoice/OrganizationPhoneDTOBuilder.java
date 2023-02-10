package com.pls.dtobuilder.invoice;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.organization.OrganizationPhoneEntity;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO builder for {@link OrganizationPhoneEntity}.
 *
 * @author Mikhail Boldinov, 17/04/14
 */
public class OrganizationPhoneDTOBuilder extends AbstractDTOBuilder<OrganizationPhoneEntity, PhoneBO> {
    @Override
    public PhoneBO buildDTO(OrganizationPhoneEntity phone) {
        if (phone == null) {
            return null;
        }
        PhoneBO phoneDTO = new PhoneBO();
        phoneDTO.setAreaCode(phone.getAreaCode());
        phoneDTO.setCountryCode(phone.getDialingCode());
        phoneDTO.setNumber(phone.getPhoneNumber());
        return phoneDTO;
    }

    @Override
    public OrganizationPhoneEntity buildEntity(PhoneBO phoneDTO) {
        throw new UnsupportedOperationException();
    }
}
