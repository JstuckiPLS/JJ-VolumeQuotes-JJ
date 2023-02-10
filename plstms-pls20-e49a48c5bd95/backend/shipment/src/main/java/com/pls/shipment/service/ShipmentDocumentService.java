package com.pls.shipment.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InvalidArgumentException;
import com.pls.core.exception.fileimport.InvalidFormatException;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.core.service.util.exception.FileSizeLimitException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.pdf.Printable;

/**
 * Service to manage shipment order documents.
 *
 * @author Maxim Medvedev
 */
public interface ShipmentDocumentService {
    /**
     * Get all documents for specified shipment.
     *
     * @param shipmentId
     *            Shipment order ID.
     *
     * @return Not <code>null</code> {@link List}.
     *
     * @throws InvalidArgumentException
     *             when null argument is passed.
     * @throws EntityNotFoundException
     *             when document not found.
     */
    List<ShipmentDocumentInfoBO> getDocumentList(Long shipmentId) throws InvalidArgumentException,
            EntityNotFoundException;

    /**
     * Generate BOL and save it to the document table.
     *
     * @param load used as data source for BOL PDF creation
     * @param hideCreatedTime option that responsible for display time for shipment created by.
     * @param userId user id
     * @throws PDFGenerationException when PDF generation failed
     * @return document id
     */
    Long prepareBolDocument(LoadEntity load, boolean hideCreatedTime, Long userId) throws PDFGenerationException;

    /**
     * Generate shipping labels and save it to the document table.
     *
     * @param load
     *            used as data source for shipping labels PDF creation
     * @throws  PDFGenerationException when PDF generation failed
     * @return document id
     */
    Long prepareShippingLabelsDocument(LoadEntity load) throws PDFGenerationException;

    /**
     * Generate temp bol document and store it to temporary table in database.
     *
     * @param load load for documents generation
     * @param customerId customer id
     * @param hideCreatedTime option that responsible for display time for shipment created by.
     * @param isManualBol this flag shows if temp BOL was generated from Manual Bol Wizard.
     * @return map of entity ids to which documents are stored and related to them document type
     * @throws PDFGenerationException when generation fails.
     */
    Long generateAndStoreTempBol(LoadEntity load, Long customerId, boolean hideCreatedTime, boolean isManualBol) throws PDFGenerationException;

    /**
     * Generate temp shipping labels document and store it to temporary table in database.
     *
     * @param load load for documents generation
     * @param loadId load id
     * @param customerId customer id
     * @param printType format for printing document
     * @return map of entity ids to which documents are stored and related to them document type
     * @throws PDFGenerationException when generation fails.
     */
    Long generateAndStoreTempShippingLabels(LoadEntity load, Long loadId, Long customerId, Printable printType) throws PDFGenerationException;

    /**
     * Save document to temporary storage.
     *
     * @param docItemStream document item stream
     * @return Document saved at temp storage
     * @throws IOException if reading from stream fail
     * @throws InvalidFormatException if format of uploaded file are not supported
     * @throws FileSizeLimitException if file is bigger than supported
     * @throws DocumentSaveException if document cannot be saved
     * @throws DocumentException if PDF document cannot be created
     */
    LoadDocumentEntity saveTemporaryDoc(
            MultipartFile docItemStream) throws IOException, InvalidFormatException, FileSizeLimitException, DocumentSaveException, DocumentException;

    /**
     * Find possible for shipment document types.
     *
     * @return list of document types
     */
    List<LoadDocumentTypeEntity> findDocumentTypes();

    /**
     * Get document type entity by its name representation. Currently, we use this relation as some kind of foreign key.
     *
     * @param documentType string representation of document type
     * @return {@link LoadDocumentTypeEntity}
     */
    LoadDocumentTypeEntity getDocumentTypeByStringName(String documentType);

    /**
     * Find list of documents for specified load of specified document type.
     *
     * @param loadId load id to which document meta data must belong to
     * @return list of document metadata for specified load and document type
     */
    List<LoadDocumentEntity> findReqDocumentsForLoad(Long loadId);

    /**
     * Regenerate documents for shipment if load is in appropriate status or for specified document types.
     * 
     * @param regenerateDocTypes
     *            document types to be regenerated
     * @param load
     *            to be used for regenerating documents
     * @param userId user id
     * @param hideCreatedTime
     *            if <code>true</code> then no creation time will be specified on BOL
     * @throws PDFGenerationException
     *             if file was not generated
     * @return list with type and id pair
     */
    Map<DocumentTypes, Long> generateShipmentDocuments(Set<DocumentTypes> regenerateDocTypes, LoadEntity load,
            boolean hideCreatedTime, Long userId) throws PDFGenerationException;

    /**
     * Regenerate documents for shipment if load is in appropriate status or for specified document types,
     * without exception.
     * 
     * @param regenerateDocTypes
     *            document types to be regenerated
     * @param load
     *            to be used for regenerating documents
     * @param userId
     *            user id
     * @param hideCreatedTime
     *            if <code>true</code> then no creation time will be specified on BOL
     * @return return <code>true</code> if success
     */
    boolean generateShipmentDocumentsSafe(Set<DocumentTypes> regenerateDocTypes, LoadEntity load,
            boolean hideCreatedTime, Long userId);


    /**
     * Generate Consignee Invoice document and store it to temporary table in database.
     * 
     * @param load
     *            {@link LoadEntity}
     * @return id of document which was save
     * @throws PDFGenerationException
     *             if PDF generation failed
     */
    Long generateAndStoreTempConsigneeInvoice(LoadEntity load) throws PDFGenerationException;

    /**
     * Generate Consignee Invoice and save it to the document table.
     * 
     * @param load
     *            {@link LoadEntity}
     * @throws PDFGenerationException
     *             if PDF generation failed
     * @return document id
     */
    Long prepareConsigneeInvoiceDocument(LoadEntity load) throws PDFGenerationException;

}
