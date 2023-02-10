package com.pls.shipment.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.shipment.dao.ManualBolDao;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.bo.ManualBolListItemBO;

/**
 * {@link ManualBolDao} implementation.
 * 
 * @author Alexander Nalapko
 *
 */
@Repository
@Transactional
public class ManualBolDaoImpl extends AbstractDaoImpl<ManualBolEntity, Long> implements ManualBolDao {
    @SuppressWarnings("unchecked")
    @Override
    public List<ManualBolListItemBO> findAll(RegularSearchQueryBO search, Long userId) {
        Query query = getCurrentSession().getNamedQuery(ManualBolEntity.Q_FIND_FOR_CRITERIA);
        query.setParameter("dateFrom", search.getFromDate(), DateType.INSTANCE);
        query.setParameter("originZip", search.getOriginZip(), StringType.INSTANCE);
        query.setParameter("destinationZip", search.getDestinationZip(), StringType.INSTANCE);
        query.setParameter("manualBolId", search.getLoadId(), LongType.INSTANCE);
        query.setParameter("dateTo", search.getToDate(), DateType.INSTANCE);
        query.setParameter("carrierId", search.getCarrier(), LongType.INSTANCE);
        query.setParameter("bol", search.getBol(), StringType.INSTANCE);
        query.setParameter("orgId", search.getCustomer(), LongType.INSTANCE);
        query.setParameter("pro", search.getPro(), StringType.INSTANCE);
        query.setParameter("dateSearchField", search.getDateSearchField(), StringType.INSTANCE);
        query.setParameter("userId", userId, LongType.INSTANCE);
        query.setParameter("job", search.getJob(), StringType.INSTANCE);
        query.setResultTransformer(new ManualBolListItemBOResultTransformer());
        return query.list();
    }

    private class ManualBolListItemBOResultTransformer extends AliasToBeanResultTransformer {
        private static final long serialVersionUID = -3201341712953840348L;

        /**
         * Constructor.
         */
        ManualBolListItemBOResultTransformer() {
            super(ManualBolListItemBO.class);
        }

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            ManualBolListItemBO listItemBO = (ManualBolListItemBO) super.transformTuple(tuple, aliases);
            listItemBO.init();
            return listItemBO;
        }
    }

    @Override
    public boolean cancel(Long id) throws EntityNotFoundException {
        Query query = getCurrentSession().getNamedQuery(ManualBolEntity.Q_CANCEL_BOL_BY_ID);
        query.setParameter("id", id);
        if (query.executeUpdate() > 0) {
            return true;
        } else {
            throw new EntityNotFoundException("Manual BOL <" + id + "> not found");
        }
    }

}
