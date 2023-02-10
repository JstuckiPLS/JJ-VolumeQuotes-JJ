package com.pls.core.dao.customs;

import java.util.List;

import com.pls.core.domain.customs.CustomsEntityTypeEntity;

public interface CustomsEntityTypeDao {

	public List<CustomsEntityTypeEntity> getCustomsEntityTypebyName (String customsEntityTypeName);
}
