package com.pls.shipment.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.CustomsEntityDao;
import com.pls.shipment.domain.customs.CustomsEntity;

/**
 * Class to get the Customs Entities for a Load (Shipper, Customs Broker, ...)
 */
@Transactional
@Repository
public class CustomsEntityDaoImpl extends AbstractDaoImpl<CustomsEntity, Long> implements CustomsEntityDao {
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CustomsEntity> getCustomsEntitiesForLoad(Long loadId) {
		return getCurrentSession().createCriteria(CustomsEntity.class)
              .add(Restrictions.eq("loadId", loadId)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomsEntity> getGoShipCustomsEntity() {
		return getCurrentSession().createCriteria(CustomsEntity.class)
              .add(Restrictions.eq("isGsCustomsBroker", true)).list();
	}
}
