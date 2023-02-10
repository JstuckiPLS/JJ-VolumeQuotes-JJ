package com.pls.shipment.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.ReasonType;
import com.pls.shipment.dao.BillingAuditReasonCodeDao;
import com.pls.shipment.domain.LdBillAuditReasonCodeEntity;

/**
 * {@link BillingAuditReasonCodeDao} implementation.
 * 
 * @author Brichak Aleksandr
 */
@Transactional
@Repository
public class BillAuditReasonCodeDaoImpl extends AbstractDaoImpl<LdBillAuditReasonCodeEntity, String> implements BillingAuditReasonCodeDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<LdBillAuditReasonCodeEntity> getReasonCodeEntityForReasonType() {
        return getCurrentSession().getNamedQuery(LdBillAuditReasonCodeEntity.GET_REASON_CODE_FOR_REASON_TYPE)
                .setParameterList("reasonType", Arrays.asList(ReasonType.INVOICE, ReasonType.PRICE)).list();
    }

    @Override
    public LdBillAuditReasonCodeEntity getReasonEntityForReasonCode(String code) {
        return (LdBillAuditReasonCodeEntity) getCurrentSession().getNamedQuery(LdBillAuditReasonCodeEntity.GET_REASON_FOR_REASON_CODE)
                .setParameter("reasonCode", code).uniqueResult();
    }
}
