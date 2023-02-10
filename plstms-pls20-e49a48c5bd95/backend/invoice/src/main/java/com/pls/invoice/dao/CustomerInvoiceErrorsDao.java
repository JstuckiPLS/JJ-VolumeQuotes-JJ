package com.pls.invoice.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.invoice.domain.CustomerInvoiceErrorEntity;
import com.pls.invoice.domain.bo.InvoiceErrorDetailsBO;

/**
 * DAO for {@link CustomerInvoiceErrorEntity}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface CustomerInvoiceErrorsDao extends AbstractDao<CustomerInvoiceErrorEntity, Long> {
    /**
     * Get list of active errors.
     *
     * @return list of {@link CustomerInvoiceErrorEntity}
     */
    List<CustomerInvoiceErrorEntity> getActiveErrors();

    /**
     * Get count of active invoice errors.
     *
     * @return count of invoice errors
     */
    Long getActiveErrorsCount();

    /**
     * Get invoice details for invoice error processing.
     * 
     * @param errorId
     *            ID of error
     * @return list of invoice error details
     */
    List<InvoiceErrorDetailsBO> getInvoiceErrorDetails(long errorId);
}
