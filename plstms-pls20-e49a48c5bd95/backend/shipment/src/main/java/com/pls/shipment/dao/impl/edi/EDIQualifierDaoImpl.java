package com.pls.shipment.dao.impl.edi;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.edi.EDIQualifierDao;
import com.pls.shipment.domain.edi.EDIQualifierEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link EDIQualifierDao}.
 *
 * @author Mikhail Boldinov, 29/08/13
 */
@Repository
@Transactional
public class EDIQualifierDaoImpl extends AbstractDaoImpl<EDIQualifierEntity, Long> implements EDIQualifierDao {

    @Override
    public List<EDIQualifierEntity> getQualifiersForCarrier(Long carrierId, String transactionSetId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("carrierId", carrierId);
        params.put("transactionSetId", transactionSetId);
        return findByNamedQuery(EDIQualifierEntity.Q_GET_QUALIFIER, params);
    }
}
