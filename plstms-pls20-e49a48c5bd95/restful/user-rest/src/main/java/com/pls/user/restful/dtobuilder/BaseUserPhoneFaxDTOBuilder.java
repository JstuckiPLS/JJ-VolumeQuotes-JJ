package com.pls.user.restful.dtobuilder;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.user.UserPhoneEntity;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO builder for {@link UserPhoneEntity}.
 * 
 * @author Aleksandr Leshchenko
 * 
 * @param <P>
 *            Subclass of {@link UserPhoneEntity}.
 */
public abstract class BaseUserPhoneFaxDTOBuilder<P extends UserPhoneEntity> extends AbstractDTOBuilder<P, PhoneBO> {
    /**
     * Get instance of {@link UserPhoneEntity}.
     * 
     * @return instance of {@link UserPhoneEntity}
     */
    public abstract P getEntity();

    @Override
    public PhoneBO buildDTO(P bo) {
        PhoneBO dto = new PhoneBO();
        dto.setCountryCode(bo.getCountryCode());
        dto.setAreaCode(bo.getAreaCode());
        dto.setNumber(bo.getNumber());
        return dto;
    }

    @Override
    public P buildEntity(PhoneBO dto) {
        P instance = getEntity();
        instance.setCountryCode(dto.getCountryCode());
        instance.setAreaCode(dto.getAreaCode());
        instance.setNumber(dto.getNumber());
        return instance;
    }
}
