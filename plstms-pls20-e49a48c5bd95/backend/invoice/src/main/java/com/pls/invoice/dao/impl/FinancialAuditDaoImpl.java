package com.pls.invoice.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.invoice.dao.FinancialAuditDao;
import com.pls.invoice.domain.bo.AuditBO;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.InvoiceAuditViewEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * {@link FinancialAuditDao} implementation.
 *
 * @author Sergey Kirichenko
 */
@Repository
@Transactional
public class FinancialAuditDaoImpl extends AbstractDaoImpl<LoadEntity, Long> implements FinancialAuditDao {

    public static final ShipmentFinancialStatus[] INVOICE_AUDIT_STATUSES = new ShipmentFinancialStatus[] {
            ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD,
            ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD_ADJUSTMENT_ACCESSORIAL };
    public static final ShipmentFinancialStatus[] PRICE_AUDIT_STATUSES = new ShipmentFinancialStatus[] {
            ShipmentFinancialStatus.PRICING_AUDIT_HOLD };

    @Override
    public boolean isLoadHasCostItems(Long loadId) {
        Query query = getCurrentSession().getNamedQuery(LoadCostDetailsEntity.Q_GET_COST_ITEMS_COUNT);
        query.setParameter("loadId", loadId);
        Integer count = (Integer) query.uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public boolean isAdjustmentHasCostItems(Long adjustmentId) {
        Query query = getCurrentSession().getNamedQuery(FinancialAccessorialsEntity.Q_GET_COST_ITEMS_COUNT);
        query.setParameter("adjustmentId", adjustmentId);
        Integer count = (Integer) query.uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public void updateAdjustmentFinancialStatus(Long adjustmentId, ShipmentFinancialStatus adjustmentStatus) {
        if (adjustmentId != null) {
            Query adjQuery = getCurrentSession().getNamedQuery(FinancialAccessorialsEntity.Q_UPDATE_FIN_ADJ_FINANCIAL_STATUS);
            adjQuery.setParameterList("adjustmentIds", Collections.singletonList(adjustmentId));
            adjQuery.setParameter("status", adjustmentStatus);
            adjQuery.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId(), LongType.INSTANCE);

            adjQuery.executeUpdate();
        }
    }

    @Override
    public void updateInvoiceApproved(List<Long> loadIds, boolean approved) {
        Query loadsQuery = getCurrentSession().getNamedQuery(LoadEntity.Q_UPDATE_LOADS_INVOICE_APPROVED);
        loadsQuery.setParameterList("loadIds", loadIds);
        loadsQuery.setParameter("invoiceApproved", approved);
        loadsQuery.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId(), LongType.INSTANCE);

        loadsQuery.executeUpdate();
    }

    @Override
    public void updateAdjustmentInvoiceApproved(List<Long> adjustmentIds, boolean approved) {
        Query adjQuery = getCurrentSession().getNamedQuery(FinancialAccessorialsEntity.Q_UPDATE_FIN_ADJ_INVOICE_APPROVED);
        adjQuery.setParameterList("adjustmentIds", adjustmentIds);
        adjQuery.setParameter("invoiceApproved", approved);
        adjQuery.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId(), LongType.INSTANCE);

        adjQuery.executeUpdate();
    }

    @Override
    public List<AuditBO> getInvoiceAuditData(Long userId) {
        Collection<AuditBO> invoices = getInvoiceAuditDetails(userId,
                InvoiceAuditViewEntity.Q_GET_LOADS_FOR_BILLING_INVOICE, INVOICE_AUDIT_STATUSES);
        return new ArrayList<AuditBO>(invoices);
    }

    @Override
    public List<AuditBO> getPriceAuditData(Long userId) {
        Collection<AuditBO> invoices = getInvoiceAuditDetails(userId,
                InvoiceAuditViewEntity.Q_GET_LOADS_FOR_BILLING_INVOICE, PRICE_AUDIT_STATUSES);
        return new ArrayList<AuditBO>(invoices);
    }

    @SuppressWarnings("unchecked")
    private Collection<AuditBO> getInvoiceAuditDetails(Long userId, String auditQuery, ShipmentFinancialStatus... statuses) {
        List<AuditBO> data = getCurrentSession().getNamedQuery(auditQuery)
                .setLong("userId", userId)
                .setParameterList("statuses", Arrays.asList(statuses))
                .setResultTransformer(new AliasToBeanResultTransformer(AuditBO.class)).list();
        Iterator<AuditBO> iterator = data.iterator();
        Map<Long, AuditBO> auditMap = new HashMap<Long, AuditBO>(data.size());
        while (iterator.hasNext()) {
            AuditBO bo = iterator.next();
            Long key = bo.getAdjustmentId() == null ? bo.getLoadId() : bo.getAdjustmentId();
            if (auditMap.containsKey(key)) {
                AuditBO financialInvoiceAuditBO = auditMap.get(key);
                financialInvoiceAuditBO.setReason(getJoinedString(financialInvoiceAuditBO.getReason(), bo.getReason()));
            } else {
                auditMap.put(key, bo);
            }
        }
        return auditMap.values();
    }

    private String getJoinedString(String str1, String str2) {
        if (StringUtils.isNotEmpty(str1)) {
            return StringUtils.isEmpty(str2) ? str1 : StringUtils.join(str1, ", ", str2);
        }
        return str2;
    }
}
