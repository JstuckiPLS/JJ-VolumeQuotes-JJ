package com.pls.invoice.dao.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.invoice.dao.CustomerInvoiceDao;
import com.pls.invoice.domain.bo.CustomerInvoiceBO;
import com.pls.invoice.domain.bo.CustomerInvoiceCO;
import com.pls.invoice.domain.bo.CustomerInvoiceSummaryBO;
import com.pls.invoice.domain.bo.enums.CustomerInvoiceType;
import com.pls.shipment.domain.FinancialAccountReceivablesEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * DAO implementation for {@link CustomerInvoiceDao}.
 *
 * @author Alexander Kirichenko
 */
@Repository
@Transactional
public class CustomerInvoiceDaoImpl extends AbstractDaoImpl<LoadEntity, Long> implements CustomerInvoiceDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<CustomerInvoiceBO> findCustomerInvoices(Long customerId, CustomerInvoiceCO queryParams) {
        Query query = getCurrentSession().getNamedQuery(FinancialAccountReceivablesEntity.Q_GET_CUSTOMER_INVOICES);
        query.setLong("customerId", customerId);
        query.setParameter("carrierId", queryParams.getCarrierId(), LongType.INSTANCE);
        query.setParameter("bookedBy", queryParams.getBookedBy(), LongType.INSTANCE);
        query.setString("invoiceType", queryParams.getInvoiceType() == null ? StringUtils.EMPTY : queryParams.getInvoiceType().name());
        query.setDate("paidFrom", queryParams.getPaidFrom());
        query.setDate("paidTo", queryParams.getPaidTo());
        addDueParameters(query, queryParams);
        return query.setResultTransformer(new AliasToBeanResultTransformer(CustomerInvoiceBO.class)).list();
    }

    @Override
    public CustomerInvoiceSummaryBO getCustomerInvoiceSummary(Long customerId) {
        CustomerInvoiceSummaryBO summaryBO = new CustomerInvoiceSummaryBO();
        Object[] summaryRow = (Object[]) getCurrentSession().getNamedQuery(FinancialAccountReceivablesEntity.Q_GET_CUST_INV_SUM_INFO)
                .setParameter("ownerId", customerId).uniqueResult();
        if (summaryRow != null) {
            summaryBO.setOpenInvoices((BigDecimal) summaryRow[0]);
            summaryBO.setPastDueTotalInvoices((BigDecimal) summaryRow[1]);
            summaryBO.setPastDue1to30Invoices((BigDecimal) summaryRow[2]);
            summaryBO.setPastDue31to60Invoices((BigDecimal) summaryRow[3]);
            summaryBO.setPastDue61to90Invoices((BigDecimal) summaryRow[4]);
            summaryBO.setPastDue91Invoices((BigDecimal) summaryRow[5]);
        }
        return summaryBO;
    }

    @Override
    public BigDecimal getCustomerUnpaidAmount(Long customerId) {
        return (BigDecimal) getCurrentSession().getNamedQuery(FinancialAccountReceivablesEntity.Q_GET_CUST_UNPAID_AMOUNT)
                .setParameter("ownerId", customerId).uniqueResult();
    }

    private void addDueParameters(Query query, CustomerInvoiceCO queryParams) {
        Map<String, Date> params = new HashMap<String, Date>();
        if (queryParams.getInvoiceType() == CustomerInvoiceType.PAST_DUE) {
            Calendar dueDate = Calendar.getInstance();
            addDueParameter(0, dueDate, params, queryParams.isIncludeFirstThirtyDays());
            addDueParameter(1, dueDate, params, queryParams.isIncludeSecondThirtyDays());
            addDueParameter(2, dueDate, params, queryParams.isIncludeThirdThirtyDays());
            addDueParameter(3, dueDate, params, queryParams.isIncludeLastDays());
        }
        for (int i = 0; i < 4; i++) {
            query.setDate("dueFrom" + i, params.get("dueFrom" + i));
            query.setDate("dueTo" + i, params.get("dueFrom" + i));
        }
    }

    private void addDueParameter(int paramIndex, Calendar dueDate, Map<String, Date> params, boolean apply) {
        dueDate.add(Calendar.DAY_OF_YEAR, -1);
        if (apply) {
            params.put("dueFrom" + paramIndex, dueDate.getTime());
            dueDate.add(Calendar.DAY_OF_YEAR, -29);
            params.put("dueTo" + paramIndex, dueDate.getTime());
        } else {
            dueDate.add(Calendar.DAY_OF_YEAR, -29);
        }
    }
}
