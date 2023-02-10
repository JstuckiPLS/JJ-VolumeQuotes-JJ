package com.pls.user.restful.dtobuilder;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.domain.user.UserPhoneEntity;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.user.restful.dto.UserEmailPhoneItemDTO;

/**
 * Builder for {@link UserEmailPhoneItemDTO}.
 * 
 * @author Aleksandr Leshchenko
 */
public class UserEmailPhoneItemDTOBuilder extends AbstractDTOBuilder<UserEntity, UserEmailPhoneItemDTO> {

    @Override
    public UserEmailPhoneItemDTO buildDTO(UserEntity entity) {
        UserEmailPhoneItemDTO result = new UserEmailPhoneItemDTO();
        result.setEmail(entity.getEmail());
        result.setFullName(UserNameBuilder.buildFullName(entity.getFirstName(), entity.getLastName()));
        result.setPhone(buildPhoneDTO(entity.getActiveUserPhoneByType(PhoneType.VOICE)));
        return result;
    }

    private PhoneBO buildPhoneDTO(UserPhoneEntity phone) {
        PhoneBO result = null;
        if (phone != null) {
            result = new PhoneBO();
            result.setCountryCode(phone.getCountryCode());
            result.setAreaCode(phone.getAreaCode());
            result.setNumber(phone.getNumber());
        }
        return result;
    }

    @Override
    public UserEntity buildEntity(UserEmailPhoneItemDTO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }

}
