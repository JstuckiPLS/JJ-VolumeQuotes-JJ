package com.pls.invoice.service;

import java.util.List;

import com.pls.core.exception.EntityNotFoundException;
import com.pls.invoice.domain.CustomerInvoiceErrorEntity;

/**
 * Service to handle invoice errors.
 *
 * @author Alexander Kirichenko
 */
public interface InvoiceErrorService {

    /**
     * Get list of active invoice errors.
     *
     * @return list of {@link com.pls.invoice.domain.CustomerInvoiceErrorEntity}
     */
    List<CustomerInvoiceErrorEntity> getInvoiceErrors();

    /**
     * Get count of active invoice errors.
     *
     * @return count of {@link CustomerInvoiceErrorEntity}
     */
    Long getCountOfInvoiceErrors();

    /**
     * Cancel specified invoice error.
     *
     * @param errorId id of invoice error
     * @throws com.pls.core.exception.EntityNotFoundException if invoice error with specified id was not found
     */
    void cancelError(long errorId) throws EntityNotFoundException;

    /**
     * Reprocess invoice considering error information from previous invoice processing.
     *
     * @param errorId
     *            id of invoice error
     * @param subject
     *            subject of email
     * @param comments
     *            user comments to be added to email
     * @param personId
     *            id of user responsible for reprocessing
     * @return error message if reprocessing failed
     */
    String reprocessError(long errorId, String subject, String comments, Long personId);

    /**
     * Method saves invoice error and update invoice status.
     * 
     * @param invoiceId
     *            - invoice ID
     * @param message
     *            - error message
     * @param e
     *            - exception that occurs
     * @param wasSentToFinance
     *            - whether invoice was sent to finance system or not
     * @param wasSentByEmail
     *            - whether invoice was sent to customer by email or not
     * @param wasSentByEdi
     *            - whether invoice was sent to customer by EDI or not
     * @param sentDocuments
     *            - whether invoice documents were sent to finance system
     * @param personId
     *            - active user ID
     */
    void saveInvoiceError(Long invoiceId, String message, Throwable e, boolean wasSentToFinance, boolean wasSentByEmail,
            boolean wasSentByEdi, boolean sentDocuments, Long personId);

    /**
     * Get subject of invoice email that should be sent to customer.
     * 
     * @param errorId
     *            id of invoice error
     * @return email subject or <code>null</code>
     */
    String getEmailSubjectForReprocessError(long errorId);

}
