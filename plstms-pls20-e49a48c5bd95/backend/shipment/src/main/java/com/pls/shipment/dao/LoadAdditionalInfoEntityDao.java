package com.pls.shipment.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LoadAdditionalInfoEntity;

/**
 * DAO Class for LoadAdditionalInfoEntity.
 * 
 * @author Sergii Belodon
 */
public interface LoadAdditionalInfoEntityDao extends AbstractDao<LoadAdditionalInfoEntity, Long> {

    /**
     * Gets the additional info by load id.
     *
     * @param loadId the load id
     * @return {@link LoadAdditionalInfoEntity}
     */
    LoadAdditionalInfoEntity getAdditionalInfoByLoadId(Long loadId);
}
