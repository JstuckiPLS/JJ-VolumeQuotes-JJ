package com.pls.user.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.user.domain.UserSettingsEntity;

public interface UserSettingsDao extends AbstractDao<UserSettingsEntity, Long> {

    List<UserSettingsEntity> getByPersonId(Long personId);

    UserSettingsEntity getUserSettingsByPersonIdAndKey(Long personId, String key);
   
}
