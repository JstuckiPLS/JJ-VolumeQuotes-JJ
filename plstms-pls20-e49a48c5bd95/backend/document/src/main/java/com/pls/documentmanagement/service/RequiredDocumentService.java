package com.pls.documentmanagement.service;

import com.pls.documentmanagement.domain.RequiredDocumentEntity;

import java.util.Collection;
import java.util.List;

/**
 * Service that handles customer required documents.
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface RequiredDocumentService {
    /**
     * Get required document by id.
     *
     * @param id id of required document to get
     * @return {@link RequiredDocumentEntity}
     */
    RequiredDocumentEntity getRequiredDocument(Long id);

    /**
     * Save collection of required documents.
     *
     * @param billToId id of billTo
     * @param requiredDocuments required documents to save
     */
    void saveRequiredDocuments(Collection<RequiredDocumentEntity> requiredDocuments, Long billToId);

    /**
     * Get required document of all available shipment document types. If required document doesn't exist new instance with populate document type
     * information will be returned.
     *
     * @param billToId id of billToId
     * @return {@link List<RequiredDocumentEntity>}
     */
    List<RequiredDocumentEntity> getRequiredDocumentsOfShipmentTypes(long billToId);


}
