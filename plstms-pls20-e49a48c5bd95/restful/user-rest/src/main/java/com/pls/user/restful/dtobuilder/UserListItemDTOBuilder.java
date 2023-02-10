package com.pls.user.restful.dtobuilder;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.user.domain.bo.UserListItemBO;
import com.pls.user.restful.dto.UserListItemDTO;

/**
 * Builder for {@link UserListItemDTO} objects.
 * 
 * @author Maxim Medvedev
 */
public class UserListItemDTOBuilder extends AbstractDTOBuilder<UserListItemBO, UserListItemDTO> {

    @Override
    public UserListItemDTO buildDTO(UserListItemBO entity) {
        UserListItemDTO result = new UserListItemDTO();
        result.setEmail(entity.getEmail());
        result.setFullName(UserNameBuilder.buildFullName(entity.getFirstName(), entity.getLastName()));
        result.setPersonId(entity.getPersonId());
        result.setUserId(entity.getUserId());
        result.setParentOrgId(entity.getParentOrgId());
        result.setParentOrgName(entity.getParentOrgName());
        return result;
    }

    @Override
    public UserListItemBO buildEntity(UserListItemDTO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }

}
