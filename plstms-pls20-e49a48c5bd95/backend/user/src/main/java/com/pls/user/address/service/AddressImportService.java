package com.pls.user.address.service;

import java.io.InputStream;

import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.exception.DocumentReadException;

/**
 * Service that handles of addresses import.
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface AddressImportService {
    /**
     * Imports addresses from excel file to {@link com.pls.core.domain.address.UserAddressBookEntity} and save in database.<br/>
     * @param customerId id of customer
     * @param userId id of customer user
     * @param inputStream
     *            {@link java.io.InputStream} excel file data.
     * @param extension extension of import file
     * @return {@link com.pls.core.domain.bo.ImportFileResults}.
     * @throws ImportException
     *             when imported file contains invalid data.
     */
    ImportFileResults importAddresses(Long customerId, Long userId, InputStream inputStream,
 String extension) throws ImportException;

    /**
     * Returns document with data about addresses records which was failed to import during importAddresses()
     * method processing.
     *
     * @param id
     *            id of document.
     * @return {@link LoadDocumentEntity}.
     * @throws EntityNotFoundException when document by given id was not found.
     * @throws DocumentReadException if imported document cannot be read
     */
    LoadDocumentEntity getImportFailedDocument(Long id) throws EntityNotFoundException, DocumentReadException;

    /**
     * Remove document with specified id.
     *
     * @param id id of document.
     * @throws EntityNotFoundException when document by given id was not found.
     */
    void removeImportFailedDocument(Long id) throws EntityNotFoundException;
}
