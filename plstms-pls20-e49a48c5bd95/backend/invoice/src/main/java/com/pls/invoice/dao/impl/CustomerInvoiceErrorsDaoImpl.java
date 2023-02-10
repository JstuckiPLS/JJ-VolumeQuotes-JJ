package com.pls.invoice.dao.impl;

import java.util.List;

import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.invoice.dao.CustomerInvoiceErrorsDao;
import com.pls.invoice.domain.CustomerInvoiceErrorEntity;
import com.pls.invoice.domain.bo.InvoiceErrorDetailsBO;

/**
 * Implementation of {@link CustomerInvoiceErrorsDao}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Repository
@Transactional
public class CustomerInvoiceErrorsDaoImpl extends AbstractDaoImpl<CustomerInvoiceErrorEntity, Long> implements CustomerInvoiceErrorsDao {
    @Override
    public List<CustomerInvoiceErrorEntity> getActiveErrors() {
        return findByNamedQuery(CustomerInvoiceErrorEntity.Q_ACTIVE_ERRORS_QUERY);
    }

    @Override
    public Long getActiveErrorsCount() {
        return findUniqueObjectByNamedQuery(CustomerInvoiceErrorEntity.Q_ACTIVE_ERRORS_COUNT_QUERY);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InvoiceErrorDetailsBO> getInvoiceErrorDetails(long errorId) {
        return getCurrentSession().getNamedQuery(CustomerInvoiceErrorEntity.Q_INVOICE_DETAILS)
                .setLong("errorId", errorId)
                .setResultTransformer(Transformers.aliasToBean(InvoiceErrorDetailsBO.class))
                .list();
    }
}
