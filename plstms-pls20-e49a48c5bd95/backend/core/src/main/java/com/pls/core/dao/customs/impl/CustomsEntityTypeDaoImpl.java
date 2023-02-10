package com.pls.core.dao.customs.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.customs.CustomsEntityTypeDao;
import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.customs.CustomsEntityTypeEntity;


/**
 * Class to get the UOMS for a shipment
 */
@Transactional
@Repository
public class CustomsEntityTypeDaoImpl extends AbstractDaoImpl<CustomsEntityTypeEntity, Long> implements CustomsEntityTypeDao {


	@SuppressWarnings("unchecked")
	@Override
	public List<CustomsEntityTypeEntity> getCustomsEntityTypebyName (String customsEntityTypeName) {
		return getCurrentSession().createCriteria(CustomsEntityTypeEntity.class)
              .add(Restrictions.eq("name", customsEntityTypeName)).list();
	}

}
