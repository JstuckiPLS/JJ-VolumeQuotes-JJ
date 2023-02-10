package com.pls.invoice.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.enums.ProcessingPeriod;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.WeekDays;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.domain.BillToInvoiceProcessingTimeBO;
import com.pls.invoice.domain.FinancialInvoiceHistoryEntity;
import com.pls.invoice.domain.bo.InvoiceBO;
import com.pls.invoice.domain.bo.InvoiceProcessingItemBO;
import com.pls.invoice.domain.bo.InvoiceResultBO;
import com.pls.invoice.domain.bo.enums.InvoiceReleaseStatus;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadFinalizationHistoryEntity;

/**
 * {@link FinancialInvoiceDao} implementation.
 * 
 * @author Sergey Kirichenko
 */
@Repository
@Transactional
public class FinancialInvoiceDaoImpl extends AbstractDaoImpl<FinancialInvoiceHistoryEntity, Long> implements FinancialInvoiceDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<LoadEntity> getLoadsByInvoiceId(Long invoiceId) {
        return getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_GET_LOADS_BY_INVOICE_ID).setLong("invoiceId", invoiceId).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FinancialAccessorialsEntity> getAdjustmentsByInvoiceId(Long invoiceId) {
        return getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_GET_ADJUSTMENTS_BY_INVOICE_ID)
                .setLong("invoiceId", invoiceId)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BillToInvoiceProcessingTimeBO> getBillToIdsForAutomaticProcessing(ProcessingPeriod period, int dayOfWeek, Integer minutes) {
        Query query = getCurrentSession().getNamedQuery(CostDetailItemEntity.Q_GET_BILL_TO_FOR_AUTOMATIC_PROCESSING);
        query.setParameter("processingPeriod", period);
        query.setParameter("dayOfWeek", WeekDays.getWeekDayByCalendarWeekDay(dayOfWeek));
        query.setParameter("currentMinutes", minutes);
        query.setResultTransformer(new AliasToBeanResultTransformer(BillToInvoiceProcessingTimeBO.class));
        return query.list();
    }

    @Override
    public void updateLoadWithInvoiceNumber(Long loadId, String groupInvoiceNumber, String invoiceNumber, Date invoiceDate, Long userId) {
        getCurrentSession().getNamedQuery(LoadCostDetailsEntity.Q_SET_INVOICE_NUMBER)
                .setLong("loadId", loadId)
                .setDate("invoiceDate", invoiceDate)
                .setString("invoiceNumber", invoiceNumber)
                .setString("groupInvoiceNumber", groupInvoiceNumber)
                .setLong("modifiedBy", userId)
                .executeUpdate();
    }

    @Override
    public void updateAdjustmentWithInvoiceNumber(Long adjustmentId, String groupInvoiceNumber, String invoiceNumber, Date invoiceDate, Long userId) {
        getCurrentSession().getNamedQuery(FinancialAccessorialsEntity.Q_SET_INVOICE_NUMBER)
                .setLong("adjustmentId", adjustmentId)
                .setParameter("financialStatus", ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE)
                .setDate("invoiceDate", invoiceDate)
                .setString("invoiceNumber", invoiceNumber)
                .setString("groupInvoiceNumber", groupInvoiceNumber)
                .setLong("modifiedBy", userId)
                .executeUpdate();
    }

    @Override
    public void updateLoadsFinancialStatuses(List<Long> loadIds, Date invoiceDate, Long userId) {
        Query loadsQuery = getCurrentSession().getNamedQuery(LoadEntity.Q_UPDATE_INVOICED_LOADS);
        loadsQuery.setParameterList("loadIds", loadIds, LongType.INSTANCE);
        loadsQuery.setParameter("status", ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        loadsQuery.setParameter("modifiedBy", userId, LongType.INSTANCE);
        loadsQuery.setParameter("invoiceDate", invoiceDate, DateType.INSTANCE);

        loadsQuery.executeUpdate();
        getCurrentSession().flush();
    }

    @Override
    public Long getNextInvoiceId() {
        return (Long) getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_NEXT_INVOICE_ID_SEQ).uniqueResult();
    }

    @Override
    public Long getNextCBIInvoiceSequenceNumber() {
        return (Long) getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_NEXT_CBI_SEQ).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getLoadsByBillTo(Long billToId, Date filterDeliveryDate) {
        return getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_LOAD_IDS)
                .setLong("billToId", billToId)
                .setDate("filterDeliveryDate", filterDeliveryDate)
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getAdjustmentsByBillTo(Long billToId) {
        return getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_ADJUSTMENT_IDS)
                .setLong("billToId", billToId)
                .list();
    }

    @Override
    public void insertLoads(Long invoiceId, InvoiceReleaseStatus status, Collection<Long> loadsIds, Long userId) {
        Query query = getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.I_LOADS);
        query.setLong("userId", userId);
        query.setLong("invoiceId", invoiceId);
        query.setParameterList("loadsIds", loadsIds, LongType.INSTANCE);
        query.setParameter("releaseStatus", status);
        query.executeUpdate();
    }

    @Override
    public void insertAdjustments(Long invoiceId, InvoiceReleaseStatus status, Collection<Long> adjustmentIds, Long userId) {
        Query query = getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.I_ADJUSTMENTS);
        query.setLong("userId", userId);
        query.setLong("invoiceId", invoiceId);
        query.setParameterList("adjustmentIds", adjustmentIds, LongType.INSTANCE);
        query.setParameter("releaseStatus", status);
        query.executeUpdate();
    }

    @Override
    public void updateLoadsWithInvoiceNumbers(List<Long> loadIds, Date invoiceDate, Long userId) {
        getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.U_LOADS_INVOICE_NUMBERS)
                .setParameterList("loadIds", loadIds)
                .setDate("invoiceDate", invoiceDate)
                .setLong("userId", userId)
                .executeUpdate();
    }

    @Override
    public void updateAdjustmentsWithInvoiceNumbers(List<Long> adjustmentIds, Date invoiceDate, Long userId) {
        getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.U_ADJUSTMENTS_INVOICE_NUMBERS)
                .setParameterList("adjustmentIds", adjustmentIds)
                .setDate("invoiceDate", invoiceDate)
                .setParameter("financialStatus", ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE)
                .setLong("userId", userId)
                .executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InvoiceResultBO> getInvoiceResults(Long invoiceId) {
        Query query = getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_INVOICE_RESULTS);
        query.setLong("invoiceId", invoiceId);
        query.setResultTransformer(new AliasToBeanResultTransformer(InvoiceResultBO.class));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getAllLoadsIds(Long invoiceId, Long billToId) {
        return getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_ALL_LOADS_ID)
                .setLong("invoiceId", invoiceId)
                .setLong("billToId", billToId)
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getGroupInvoiceNumbers(Long invoiceId, Long billToId) {
        return getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_GROUP_INVOICE_NUMBERS)
                .setLong("invoiceId", invoiceId)
                .setLong("billToId", billToId)
                .list();
    }

    @Override
    public void insertLoadsForReProcess(Long invoiceId, Collection<Long> loads, Long userId) {
        Query query = getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.I_CBI_LOADS_HIST);
        query.setLong("userId", userId);
        query.setLong("invoiceId", invoiceId);
        query.setParameter("releaseStatus", InvoiceReleaseStatus.REPROCESS);
        query.setParameterList("loadsIds", loads, LongType.INSTANCE);
        query.executeUpdate();
    }

    @Override
    public void insertAdjustmentsForReProcess(Long invoiceId, Collection<Long> adjustments, Long userId) {
        Query query = getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.I_CBI_ADJUSTMENTS_HIST);
        query.setLong("userId", userId);
        query.setLong("invoiceId", invoiceId);
        query.setParameter("releaseStatus", InvoiceReleaseStatus.REPROCESS);
        query.setParameterList("adjustmentsIds", adjustments, LongType.INSTANCE);
        query.executeUpdate();
    }

    @Override
    public BillToEntity getBillToByInvoiceId(Long invoiceId) {
        return (BillToEntity) getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_BILL_TO_BY_INVOICE_ID)
                .setParameter("invoiceId", invoiceId)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public void updateLoadsInvoicedInFinancials(List<Long> loadsIds, Date glDate, Long userId) {
        getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.U_LOADS_INVOICED_IN_FIN)
                .setLong("userId", userId)
                .setDate("glDate", glDate)
                .setParameterList("loadsIds", loadsIds, LongType.INSTANCE)
                .executeUpdate();
    }

    @Override
    public void updateAdjustmentsInvoicedInFinancials(List<Long> adjustmentsIds, Date glDate, Long userId) {
        getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.U_ADJ_INVOICED_IN_FIN)
                .setLong("userId", userId)
                .setDate("glDate", glDate)
                .setParameter("finStatus", ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE)
                .setParameterList("adjustmentsIds", adjustmentsIds, LongType.INSTANCE)
                .executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InvoiceBO> getRebillAdjustmentsForAutomaticProcessing(Long billToId) {
        return getCurrentSession().getNamedQuery(FinancialAccessorialsEntity.Q_GET_REBILL_ADJ_FOR_AUTO_PROCESSING)
                .setLong("billToId", billToId)
                .setResultTransformer(Transformers.aliasToBean(InvoiceBO.class))
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InvoiceProcessingItemBO> getLoadsForProcessing(List<Long> loads) {
        return getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_GET_LOADS_FOR_PROCESSING)
                .setParameterList("ids", loads)
                .setResultTransformer(Transformers.aliasToBean(InvoiceProcessingItemBO.class))
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InvoiceProcessingItemBO> getAdjustmentsForProcessing(List<Long> adjustments) {
        return getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_GET_ADJ_FOR_PROCESSING)
                .setParameterList("ids", adjustments)
                .setResultTransformer(Transformers.aliasToBean(InvoiceProcessingItemBO.class))
                .list();
    }

    @Override
    public void updateAdjustmentsFinancialStatus(List<Long> adjustmentIds, ShipmentFinancialStatus status, Long userId) {
        Query adjQuery = getCurrentSession().getNamedQuery(FinancialAccessorialsEntity.Q_UPDATE_FIN_ADJ_FINANCIAL_STATUS);
        adjQuery.setParameterList("adjustmentIds", adjustmentIds);
        adjQuery.setParameter("status", status);
        adjQuery.setParameter("modifiedBy", userId, LongType.INSTANCE);
        adjQuery.executeUpdate();
    }

    @Override
    public void insertFinalizationHistoryLoads(List<Long> successfulLoads, Long requestId) {
        Query query = getCurrentSession().getNamedQuery(LoadFinalizationHistoryEntity.I_LOADS);
        query.setLong("requestId", requestId);
        query.setParameterList("loadsIds", successfulLoads, LongType.INSTANCE);
        query.setParameter("finalizationStatus", ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        query.setParameter("prevFinalizationStatus", ShipmentFinancialStatus.ACCOUNTING_BILLING);
        query.executeUpdate();
    }

    @Override
    public void insertFinalizationHistoryAdjustments(List<Long> successfulAdjustments, Long requestId) {
        Query query = getCurrentSession().getNamedQuery(LoadFinalizationHistoryEntity.I_ADJUSTMENTS);
        query.setLong("requestId", requestId);
        query.setParameterList("adjustmentsIds", successfulAdjustments, LongType.INSTANCE);
        query.setParameter("finalizationStatus", ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        query.setParameter("prevFinalizationStatus", ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<KeyValueBO> getAdjustmentReasons(List<Long> adjustments) {
        return getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_ADJ_REASONS)
                .setParameterList("ids", adjustments)
                .setResultTransformer(Transformers.aliasToBean(KeyValueBO.class))
                .list();
    }
}
