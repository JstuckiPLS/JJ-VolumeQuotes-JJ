package com.pls.invoice.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.enums.InvoiceProcessingType;
import com.pls.core.domain.enums.ProcessingPeriod;
import com.pls.invoice.dao.FinancialBoardDao;
import com.pls.invoice.domain.bo.ConsolidatedInvoiceBO;
import com.pls.invoice.domain.bo.InvoiceBO;
import com.pls.invoice.domain.bo.enums.ApprovedStatus;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * {@link FinancialBoardDao} implementation.
 *
 * @author Sergey Kirichenko
 */
@Repository
@Transactional
public class FinancialBoardDaoImpl extends AbstractDaoImpl<LoadEntity, Long> implements FinancialBoardDao {

    @Override
    public List<ConsolidatedInvoiceBO> getConsolidatedInvoiceData(Long userId) {
        List<ConsolidatedInvoiceBO> consolidatedBillToDetails = new ArrayList<ConsolidatedInvoiceBO>();
        consolidatedBillToDetails.addAll(getConsolidatedBillToDetails(userId, LoadEntity.Q_GET_CBI_DATA_LOADS));
        consolidatedBillToDetails.addAll(getConsolidatedBillToDetails(userId, FinancialAccessorialsEntity.Q_GET_CBI_DATA_ADJ));
        return consolidatedBillToDetails.stream().collect(Collectors.groupingBy(ConsolidatedInvoiceBO::getBillToId)).values().stream()
                .map(this::joinItems).peek(this::prepareItem)
                .sorted(Comparator.comparing(ConsolidatedInvoiceBO::getCustomerName).thenComparing(ConsolidatedInvoiceBO::getBillToName))
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBO> getConsolidatedLoads(Long userId, Long billToId) {
       return getConsolidatedLoads(userId, Arrays.asList(billToId));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InvoiceBO> getConsolidatedLoads(Long userId, List<Long> billToIds) {
        List<InvoiceBO> invoiceBOs = new ArrayList<InvoiceBO>();
        invoiceBOs.addAll(getCurrentSession().getNamedQuery(LoadEntity.Q_GET_CONSOLIDATED_INVOICES)
                .setParameter("userId", userId, LongType.INSTANCE).setParameterList("billToIds", billToIds)
                .setResultTransformer(new InvoiceBoResultTransformer()).list());
        invoiceBOs.addAll(getCurrentSession()
                .getNamedQuery(FinancialAccessorialsEntity.Q_GET_ADJ_FOR_CONSOLIDATED_INVOICES)
                .setParameter("userId", userId, LongType.INSTANCE).setParameterList("billToIds", billToIds)
                .setResultTransformer(new InvoiceBoResultTransformer()).list());
        return sortingInvoices(invoiceBOs);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InvoiceBO> getRebillAdjustments(Long userId, List<Long> ids) {
        List<InvoiceBO> invoiceBOs = new ArrayList<InvoiceBO>();
        invoiceBOs.addAll(getCurrentSession()
                .getNamedQuery(FinancialAccessorialsEntity.Q_GET_OTHER_REBILL_ADJUSTMENTS)
                .setParameter("userId", userId, LongType.INSTANCE).setParameterList("rebillAdjIds", ids, LongType.INSTANCE)
                .setResultTransformer(new InvoiceBoResultTransformer()).list());
        return sortingInvoices(invoiceBOs);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InvoiceBO> getTransactionalInvoices(Long userId) {
        List<InvoiceBO> invoiceBOs = new ArrayList<InvoiceBO>();
        invoiceBOs.addAll(getCurrentSession().getNamedQuery(LoadEntity.Q_GET_TRANSACTIONAL_INVOICES)
                .setParameter("userId", userId, LongType.INSTANCE)
                .setResultTransformer(new InvoiceBoResultTransformer()).list());
        invoiceBOs.addAll(getCurrentSession()
                .getNamedQuery(FinancialAccessorialsEntity.Q_GET_ADJ_FOR_TRANSACTIONAL_INVOICES)
                .setParameter("userId", userId, LongType.INSTANCE)
                .setResultTransformer(new InvoiceBoResultTransformer()).list());
        return sortingInvoices(invoiceBOs);
    }

    private List<InvoiceBO> sortingInvoices(List<InvoiceBO> invoiceBOs) {
        invoiceBOs.sort((o1, o2) -> new CompareToBuilder().append(o1.getBillToId(), o2.getBillToId()).append(o1.getLoadId(), o2.getLoadId())
                .append(o1.getAdjustmentId(), o2.getAdjustmentId()).toComparison());
        return invoiceBOs;
    }

    private class InvoiceBoResultTransformer extends AliasToBeanResultTransformer {
        private static final long serialVersionUID = -6025423146328367578L;

        /**
         * Constructor.
         */
        InvoiceBoResultTransformer() {
            super(InvoiceBO.class);
        }

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            InvoiceBO listItemBO = (InvoiceBO) super.transformTuple(tuple, aliases);
            listItemBO.init();
            return listItemBO;
        }
    }

    @SuppressWarnings("unchecked")
    private List<ConsolidatedInvoiceBO> getConsolidatedBillToDetails(Long userId, String namedQuery) {
        Query query = getCurrentSession().getNamedQuery(namedQuery);
        query.setParameter("userId", userId, new LongType());
        query.setResultTransformer(Transformers.aliasToBean(ConsolidatedInvoiceBO.class));
        return query.list();
    }

    private ConsolidatedInvoiceBO joinItems(List<ConsolidatedInvoiceBO> items) {
        ConsolidatedInvoiceBO result = items.get(0);
        if (items.size() > 1) {
            for (int i = 1; i < items.size(); i++) {
                ConsolidatedInvoiceBO item = items.get(i);
                result.setAllCount(result.getAllCount() + item.getAllCount());
                result.setApprovedCount(result.getApprovedCount() + item.getApprovedCount());
                result.setTotalRevenue(result.getTotalRevenue().add(item.getTotalRevenue()));
                result.setTotalCost(result.getTotalCost().add(item.getTotalCost()));
            }
        }
        return result;
    }

    private void prepareItem(ConsolidatedInvoiceBO item) {
        item.setInvoiceDateInfo(getInvoiceDateInfo(item));
        item.setSendBy(getSendBy(item.isNoInvoiceDocument(), item.isInvoiceInFinancials(), item.isEdi(), item.isEmail()));
        if (item.getTotalCost() != null && item.getTotalRevenue() != null) {
            item.setTotalMargin(item.getTotalRevenue().subtract(item.getTotalCost()));
        }
        item.setApproved(getStatus(item.getApprovedCount(), item.getAllCount()));
    }

    private String getSendBy(boolean noInvoiceDocument, boolean invoiceInFinancials, boolean edi, boolean email) {
        List<String> sendByList = new ArrayList<>();
        if (edi) {
            sendByList.add("EDI");
        }
        if (email) {
            sendByList.add("Email");
        }
        if (invoiceInFinancials) {
            sendByList.add("Financials");
        }
        if (noInvoiceDocument) {
            sendByList.add("No document");
        }
        return sendByList.stream().collect(Collectors.joining("/"));
    }

    private String getInvoiceDateInfo(ConsolidatedInvoiceBO item) {
        String result;
        if (InvoiceProcessingType.MANUAL == item.getProcessingType()) {
            result = "Manual";
        } else if (ProcessingPeriod.DAILY == item.getProcessingPeriod()) {
            result = "Daily @ " + convertMinutesToTimeString(item.getProcessingTime(), item.getTimeZone());
        } else {
            result = item.getProcessingDayOfWeek().name() + " @ " + convertMinutesToTimeString(item.getProcessingTime(), item.getTimeZone());
        }
        return result;
    }

    private String convertMinutesToTimeString(Integer minutes, String timeZone) {
        return String.format("%02d:%02d %s %s", minutes % 720 / 60, minutes % 60, minutes < 720 ? "AM" : "PM", timeZone);
    }

    private ApprovedStatus getStatus(long approved, long all) {
        ApprovedStatus result;
        if (approved == all) {
            result = ApprovedStatus.ALL;
        } else if (approved == 0) {
            result = ApprovedStatus.NONE;
        } else {
            result = ApprovedStatus.SOME;
        }
        return result;
    }
}
