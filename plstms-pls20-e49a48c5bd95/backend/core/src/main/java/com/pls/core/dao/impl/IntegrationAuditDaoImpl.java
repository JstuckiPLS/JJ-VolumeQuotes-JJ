package com.pls.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.IntegrationAuditDao;
import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.integration.AuditBO;

/**
 * For performing DB operation on Integration Audit tables.
 *
 * @author Yasaman Honarvar
 *
 */
@Transactional
@Repository
public class IntegrationAuditDaoImpl extends AbstractDaoImpl<AuditEntity, Long> implements IntegrationAuditDao {

    @Override
    @SuppressWarnings("unchecked")
    public AuditDetailEntity getLogDetailsByAuditId(Long auditId) {
        List<AuditDetailEntity> result = getCurrentSession().getNamedQuery(AuditDetailEntity.Q_GET_INTEGRATION_LOG_DETAILS)
                .setParameter("auditId", auditId).setResultTransformer(new AuditDetailEntityResultTransformer()).list();
        return result.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AuditBO> getLogsByCriteria(Date dateFrom, Date dateTo, Long customerId, String scac, String bolNumber, String shipmentNumber,
            Long loadId, EDIMessageType messageType) {
        List<AuditBO> result = getCurrentSession().getNamedQuery(AuditEntity.Q_GET_INTEGRATION_LOGS).setDate("dateFrom", dateFrom)
                .setDate("dateTo", dateTo).setParameter("shipperOrgId", customerId, LongType.INSTANCE)
                .setBoolean("datesNotEntered", dateFrom == null && dateTo == null).setString("scac", scac).setString("bolNumber", bolNumber)
                .setString("shipmentNumber", shipmentNumber).setParameter("loadId", loadId, LongType.INSTANCE)
                .setString("messageType", messageType == null ? null : messageType.name())
                .setResultTransformer(new AliasToBeanResultTransformer(AuditBO.class)).list();
        return result;
    }

    private class AuditDetailEntityResultTransformer extends BasicTransformerAdapter {

        private static final long serialVersionUID = 6578461327781346145L;

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {

            AuditDetailEntity detail = new AuditDetailEntity();
            detail.setId((Long) tuple[0]);
            detail.setMessage((String) tuple[1]);
            return detail;
        }
    }

    @Override
    public String getLastEdi204XMLByLoadIdAndScac(Long loadId, String scac) {
        return (String) getCurrentSession().getNamedQuery(AuditEntity.Q_GET_EDI_204_XML).setParameter("loadId", loadId)
                .setParameter("scac", scac).setMaxResults(1).setResultTransformer(new XMLEdiResultTransformer()).uniqueResult();
    }

    private class XMLEdiResultTransformer extends BasicTransformerAdapter {
        private static final long serialVersionUID = -5336545689741905L;

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            return ((String) tuple[0]);
        }
    }
}
