package com.pls.invoice.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.invoice.dao.HistoryInvoiceDao;
import com.pls.invoice.domain.FinancialInvoiceHistoryEntity;
import com.pls.invoice.domain.bo.CBIHistoryBO;
import com.pls.invoice.domain.bo.InvoiceHistoryBO;
import com.pls.shipment.domain.LoadEntity;

/**
 * {@link HistoryInvoiceDao} implementation.
 *
 * @author Sergey Kirichenko
 */
@Repository
@Transactional
public class HistoryInvoiceDaoImpl extends AbstractDaoImpl<LoadEntity, Long> implements HistoryInvoiceDao {
    public static final Pattern INVOICE_PATTERN = Pattern.compile("^C-\\d+-\\d+$");

    @Override
    @SuppressWarnings("unchecked")
    public List<InvoiceHistoryBO> getInvoiceHistory(RegularSearchQueryBO search, Long userId) {
        Query query = getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_GET_INVOICE_HISTORY);
        query.setParameter("dateFrom", search.getFromDate(), DateType.INSTANCE);
        query.setParameter("dateTo", search.getToDate(), DateType.INSTANCE);
        query.setParameter("customerId", search.getCustomer(), LongType.INSTANCE);
        query.setParameter("invoiceNumber", null, StringType.INSTANCE);
        query.setParameter("customerInvoiceNumber", null, StringType.INSTANCE);
        if (search.getInvoiceNumber() != null) {
            if (INVOICE_PATTERN.matcher(search.getInvoiceNumber()).matches()) {
                query.setParameter("customerInvoiceNumber", search.getInvoiceNumber(), StringType.INSTANCE);
            } else {
                query.setParameter("invoiceNumber", search.getInvoiceNumber(), StringType.INSTANCE);
            }
        }
        query.setParameter("bolNumber", StringUtils.upperCase(search.getBol()), StringType.INSTANCE);
        query.setParameter("proNumber", StringUtils.upperCase(search.getPro()), StringType.INSTANCE);
        query.setParameter("loadId", search.getLoadId(), LongType.INSTANCE);
        query.setLong("userId", userId);
        query.setResultTransformer(new AliasToBeanResultTransformer(InvoiceHistoryBO.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CBIHistoryBO> getInvoiceHistoryCBIDetails(Long invoiceId, String groupInvoiceNumber) {
        List<CBIHistoryBO> result = new ArrayList<CBIHistoryBO>();
        result.addAll(
                getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_GET_INVOICE_HISTORY_CBI_DETAILS_LOADS)
                        .setLong("invoiceId", invoiceId).setString("groupInvoiceNumber", groupInvoiceNumber)
                        .setResultTransformer(new InvoiceHistoryCBIDetailsTransformer()).list());
        result.addAll(
                getCurrentSession().getNamedQuery(FinancialInvoiceHistoryEntity.Q_GET_INVOICE_HISTORY_CBI_DETAILS_ADJ)
                        .setLong("invoiceId", invoiceId).setString("groupInvoiceNumber", groupInvoiceNumber)
                        .setResultTransformer(new InvoiceHistoryCBIDetailsTransformer()).list());
        return result;
    }

    private class InvoiceHistoryCBIDetailsTransformer extends AliasToBeanResultTransformer {

        InvoiceHistoryCBIDetailsTransformer() {
            super(CBIHistoryBO.class);
        }

        private static final long serialVersionUID = 167672060062754106L;

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            CBIHistoryBO listItemBO = (CBIHistoryBO) super.transformTuple(tuple, aliases);
            listItemBO.init();
            return listItemBO;
        }
    }
}
