package com.pls.documentmanagement.dao;

import java.util.Map;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;

/**
 * DAO for Organization Required Documents.
 *
 * @author Alexander Nalapko
 *
 */
public interface RequiredDocumentDao extends AbstractDao<RequiredDocumentEntity, Long> {
    /**
     * Get map with shipment types and existing required documents for that types. There can be null for required documents meaning that there is
     * no document of this type for customer.
     *
     * @param billToId customer id to get requited documents
     * @return {@link Map<LoadDocumentTypeEntity,RequiredDocumentEntity>}
     */
    Map<LoadDocumentTypeEntity, RequiredDocumentEntity> getDocumentsForShipmentTypes(long billToId);

    /**
     * Check that all paperwork required for customer invoice has been uploaded for the load.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @return <code>true</code> if all documents are present. <code>false</code> otherwise.
     */
    boolean isAllPaperworkRequiredForBillToInvoicePresent(Long loadId);
}
