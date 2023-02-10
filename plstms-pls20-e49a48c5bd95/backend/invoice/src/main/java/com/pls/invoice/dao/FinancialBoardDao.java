package com.pls.invoice.dao;

import java.util.List;

import com.pls.invoice.domain.bo.ConsolidatedInvoiceBO;
import com.pls.invoice.domain.bo.InvoiceBO;

/**
 * DAO for CBI and Transactional invoices.
 *
 * @author Sergey Kirichenko
 */
public interface FinancialBoardDao {

    /**
     * Get data for consolidated invoices.
     * 
     * @param userId
     *            id of PLS User.
     * @return list of {@link ConsolidatedInvoiceBO}
     */
    List<ConsolidatedInvoiceBO> getConsolidatedInvoiceData(Long userId);

    /**
     * Get consolidated invoices based on bill to id.
     * 
     * @param userId
     *            id of PLS User.
     * @param billToId
     *            id of bill to entity
     * @return list of {@link InvoiceBO}
     */
    List<InvoiceBO> getConsolidatedLoads(Long userId, Long billToId);

    /**
     * Get consolidated invoices based on bill to ids.
     * 
     * @param userId
     *            id of PLS User.
     * @param billToId
     *            list of bill id
     * @return list of {@link InvoiceBO}
     */
    List<InvoiceBO> getConsolidatedLoads(Long userId, List<Long> billToId);

    /**
     * Get shipments which are ready to be invoiced to customer.
     * 
     * @param userId
     *            shipments account executive.
     * @return shipments which are ready to be invoiced to customer.
     */
    List<InvoiceBO> getTransactionalInvoices(Long userId);

    /**
     * Get another part of specified rebill adjustments.
     * 
     * @param userId
     *            id of PLS User.
     * @param rebillAdjIds
     *            list of adjustments ids
     * @return list of rebill adjustments
     */
    List<InvoiceBO> getRebillAdjustments(Long userId, List<Long> rebillAdjIds);
}
