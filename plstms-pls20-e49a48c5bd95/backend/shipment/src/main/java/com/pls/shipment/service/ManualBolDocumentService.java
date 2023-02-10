package com.pls.shipment.service;

import java.util.List;

import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InvalidArgumentException;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;
import com.pls.shipment.domain.ManualBolEntity;

/**
 * Service to manage Manual Bol documents.
 * 
 * @author Artem Arapov
 *
 */
public interface ManualBolDocumentService {

    /**
     * Get all documents for specified Manual BOl.
     *
     * @param manualBolId Manual Bol ID.
     *
     * @return Not <code>null</code> {@link List}.
     *
     * @throws InvalidArgumentException when null argument is passed.
     * @throws EntityNotFoundException when document not found.
     */
    List<ShipmentDocumentInfoBO> getDocumentsList(Long manualBolId) throws InvalidArgumentException, EntityNotFoundException;

    /**
     * Generate shipping labels and save it to the document table.
     *
     * @param entity used as data source for shipping labels PDF creation
     * @throws  PDFGenerationException when PDF generation failed
     */
    void createShippingLabelDocument(ManualBolEntity entity) throws PDFGenerationException;

    /**
     * Generate BOL and save it to the document table.
     *
     * @param entity used as data source for BOL PDF creation
     * @param hideCreatedTime option that responsible for display time for shipment created by.
     * @throws PDFGenerationException when PDF generation failed
     */
    void createBolDocument(ManualBolEntity entity, boolean hideCreatedTime) throws PDFGenerationException;
}
