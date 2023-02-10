package com.pls.invoice.service;

import java.io.IOException;
import java.util.List;

import com.pls.core.exception.ApplicationException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.invoice.domain.bo.ConsolidatedInvoiceBO;
import com.pls.invoice.domain.bo.InvoiceBO;
import com.pls.invoice.domain.bo.InvoiceResultBO;
import com.pls.invoice.domain.bo.ProcessedLoadsReportBO;
import com.pls.invoice.domain.bo.SendToFinanceBO;

/**
 * Service getting shipments for financial board. Gets shipments with low benefits etc.
 * 
 * @author Aleksandr Leshchenko
 */
public interface FinancialBoardService {

    /**
     * Get shipments which are ready to be invoiced to customer.
     * 
     * @param personId
     *            id of PLS User.
     * @return shipments which are ready to be invoiced to customer.
     */
    List<InvoiceBO> getTransactionalInvoices(Long personId);

    /**
     * Get consolidated shipments which are ready to be invoiced to customer.
     * 
     * @param userId
     *            id of PLS User.
     * @return list of {@link ConsolidatedInvoiceBO}
     */
    List<ConsolidatedInvoiceBO> getConsolidatedInvoiceData(Long userId);

    /**
     * Get consolidated invoices based on bill to id and invoice date.
     * 
     * @param userId
     *            id of PLS User.
     * @param billToId
     *            id of bill to entity
     * @return list of {@link InvoiceBO}
     */
    List<InvoiceBO> getConsolidatedLoads(Long userId, Long billToId);

    /**
     * Get consolidated invoices based on bill to ids and invoice date.
     * 
     * @param userId
     *            id of PLS User.
     * @param billToIds
     *            list of bill id
     * @return list of {@link InvoiceBO}
     */
    List<InvoiceBO> getConsolidatedLoads(Long userId, List<Long> billToIds);

    /**
     * Get another part of specified rebill adjustments.
     * 
     * @param userId
     *            id of PLS User.
     * @param ids
     *            list of adjustments ids
     * @return list of rebill adjustments
     */
    List<InvoiceBO> getRebillAdjustments(Long userId, List<Long> ids);

    /**
     * Process Invoices to finance and customer.
     * 
     * @param bo
     *            information for processing invoices
     * @param userId
     *            id of user responsible for invoicing
     * @return processed invoices
     * @throws ApplicationException
     *             if specified invoice id is already used for existing invoices
     */
    List<InvoiceResultBO> processInvoices(SendToFinanceBO bo, Long userId) throws ApplicationException;

    /**
     * Returns record of Transactional Report as XLSX document with data.
     * 
     * @param bo
     *              information required to build XLSX
     * 
     * @return text data file or empty response.
     * @throws IOException when can't write file content to input stream
     */
    FileInputStreamResponseEntity getTransactionalProcessedReport(ProcessedLoadsReportBO bo) throws IOException;

    /**
     * Returns record of CBI Report as XLSX document with data.
     * 
     * @param bo
     *            information required to build XLSX
     * 
     * @return text data file or empty response.
     * @throws IOException when can't write file content to input stream
     */
    FileInputStreamResponseEntity getCBIProcessedReport(ProcessedLoadsReportBO bo) throws IOException;
}
