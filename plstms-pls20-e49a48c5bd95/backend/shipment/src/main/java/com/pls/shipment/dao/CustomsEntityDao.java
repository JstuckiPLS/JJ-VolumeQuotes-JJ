package com.pls.shipment.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.customs.CustomsEntity;

/**
 * Class to get The UOM values used by a Load.
 * 
 */
public interface CustomsEntityDao extends AbstractDao<CustomsEntity, Long> {

    /**
     * Find the Customs Entities for a Load. */
	public List<CustomsEntity> getCustomsEntitiesForLoad(Long loadId);
	
	/** Get the Goship Customs Broker. */
	public List<CustomsEntity> getGoShipCustomsEntity();
}