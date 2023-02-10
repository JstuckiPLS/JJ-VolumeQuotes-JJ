package com.pls.shipment.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.customs.CustomsLoadDetailsEntity;

/**
 * Class to get The UOM values used by a Load.
 * 
 */
public interface CustomsLoadDetailsDao extends AbstractDao<CustomsLoadDetailsEntity, Long> {

    /**
     * Find the UOMS (Units of Measure) used for a Load. */
	List<CustomsLoadDetailsEntity> getCustomsLoadDetails(Long loadId);
}