package com.pls.user.restful.dtobuilder;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.domain.bo.user.UserEmailBO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.user.restful.dto.UserEmailItemDTO;
import com.pls.user.restful.dto.UserListItemDTO;

/**
 * Builder for {@link UserListItemDTO} objects.
 * 
 * @author Brichak Aleksandr
 */
public class UserEmailItemDTOBuilder extends AbstractDTOBuilder<UserEmailBO, UserEmailItemDTO> {

    @Override
    public UserEmailItemDTO buildDTO(UserEmailBO entity) {
        UserEmailItemDTO result = new UserEmailItemDTO();
        result.setEmail(entity.getEmail());
        result.setFullName(UserNameBuilder.buildFullName(entity.getFirstName(), entity.getLastName()));

        return result;
    }

    @Override
    public UserEmailBO buildEntity(UserEmailItemDTO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }

}
