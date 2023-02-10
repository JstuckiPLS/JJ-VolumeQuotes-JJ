package com.pls.shipment.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.CustomsLoadDetailsDao;
import com.pls.shipment.domain.customs.CustomsLoadDetailsEntity;

/**
 * Class to get the UOMS for a shipment
 */
@Transactional
@Repository
public class CustomsLoadDetailsDaoImpl extends AbstractDaoImpl<CustomsLoadDetailsEntity, Long> implements CustomsLoadDetailsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomsLoadDetailsEntity> getCustomsLoadDetails(Long loadId) {
		return getCurrentSession().createCriteria(CustomsLoadDetailsEntity.class)
              .add(Restrictions.eq("loadId", loadId)).list();
	}

}
