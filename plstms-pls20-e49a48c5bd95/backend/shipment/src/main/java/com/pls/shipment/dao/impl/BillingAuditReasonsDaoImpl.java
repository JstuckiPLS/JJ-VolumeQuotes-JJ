package com.pls.shipment.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.ReasonType;
import com.pls.shipment.dao.BillingAuditReasonsDao;
import com.pls.shipment.domain.LdBillingAuditReasonsEntity;

/**
 * {@link BillingAuditReasonsDao} implementation.
 * 
 * @author Brichak Aleksandr
 */
@Repository
@Transactional
public class BillingAuditReasonsDaoImpl extends AbstractDaoImpl<LdBillingAuditReasonsEntity, Long> implements BillingAuditReasonsDao {
    private static final List<String> MANUAL_REASONS = Arrays.asList(ReasonType.INVOICE.getCode(), ReasonType.PRICE.getCode());

    @Override
    public void deactivateBillingAuditReasonsStatusForLoad(Long loadId) {
        if (loadId != null) {
            getCurrentSession()
                    .getNamedQuery(LdBillingAuditReasonsEntity.Q_DEACTIVATE_LOAD_REASONS)
                    .setLong("loadId", loadId)
                    .setLong("modifiedBy", SecurityUtils.getCurrentPersonId())
                    .executeUpdate();
        }
    }

    @Override
    public void deactivateManualBillingAuditReasonsStatusForLoad(Long loadId) {
        if (loadId != null) {
            getCurrentSession()
                    .getNamedQuery(LdBillingAuditReasonsEntity.Q_DEACTIVATE_MANUAL_LOAD_REASONS)
                    .setLong("loadId", loadId)
                    .setLong("modifiedBy", SecurityUtils.getCurrentPersonId())
                    .setParameterList("manualReasonTypes", MANUAL_REASONS, StringType.INSTANCE)
                    .executeUpdate();
        }
    }

    @Override
    public void deactivateAuditReasonsStatusForAdjustment(Long adjustmentId) {
        if (adjustmentId != null) {
            getCurrentSession()
                    .getNamedQuery(LdBillingAuditReasonsEntity.Q_DEACTIVATE_ADJUSTMENT_REASONS)
                    .setLong("adjustmentId", adjustmentId)
                    .setLong("modifiedBy", SecurityUtils.getCurrentPersonId())
                    .executeUpdate();
        }
    }

    @Override
    public LdBillingAuditReasonsEntity create(String reasonCd, Long loadId, String comment, Long finAccDetailId) {
        LdBillingAuditReasonsEntity entity = new LdBillingAuditReasonsEntity();
        entity.setLoadId(loadId);
        entity.setReasonCd(reasonCd);
        entity.setComment(comment);
        entity.setFinancialAccessorialDetailId(finAccDetailId);
        return entity;
    }

    @Override
    public void createAndSave(String reasonCd, Long loadId, String comment, Long finAccDetailId) {
        this.saveOrUpdate(create(reasonCd, loadId, comment, finAccDetailId));
        getCurrentSession().flush();
    }
}
