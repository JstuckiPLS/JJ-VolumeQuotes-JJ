/**
 * 
 */
package com.pls.shipment.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.LoadEdiDataDao;
import com.pls.shipment.domain.LoadEdiDataEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * {@link LoadEdiDataDao} implementation.
 * 
 * @author Alexander Nalapko
 *
 */
@Repository
@Transactional
public class LoadEdiDataDaoImpl extends AbstractDaoImpl<LoadEdiDataEntity, Long> implements LoadEdiDataDao {

    @Override
    public LoadEntity getLoadByGS(Long gs) {
        return (LoadEntity) getCurrentSession().getNamedQuery(LoadEdiDataEntity.Q_GET_LOAD_BY_GS_SEGMENT)
                .setParameter("gs", gs).uniqueResult();
    }
}
