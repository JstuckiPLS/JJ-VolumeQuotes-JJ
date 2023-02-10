package com.pls.user.restful.dtobuilder;

import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.user.domain.UserSettingsEntity;
import com.pls.user.restful.dto.UserSettingsDTO;

public class UserSettingsDTOBuilder extends AbstractDTOBuilder<UserSettingsEntity, UserSettingsDTO> {

    @Override
    public UserSettingsDTO buildDTO(UserSettingsEntity userEntity) {
        UserSettingsDTO result = new UserSettingsDTO();
        result.setKey(userEntity.getKey());
        result.setValue(userEntity.getValue());

        return result;
    }

    @Override
    public UserSettingsEntity buildEntity(UserSettingsDTO dto) {
        UserSettingsEntity result = new UserSettingsEntity();
        result.setKey(dto.getKey());
        result.setValue(dto.getValue());

        return result;
    }

}
