/**
 * 
 */
package com.pls.shipment.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LoadEdiDataEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * DAO for {@link LoadEdiDataEntity}.
 * 
 * @author Alexander Nalapko
 *
 */
public interface LoadEdiDataDao extends AbstractDao<LoadEdiDataEntity, Long> {
    /**
     * Get load by GS segment.
     * 
     * @param gs
     *            unique number
     * @return {@link LoadEntity}
     */
    LoadEntity getLoadByGS(Long gs);
}
