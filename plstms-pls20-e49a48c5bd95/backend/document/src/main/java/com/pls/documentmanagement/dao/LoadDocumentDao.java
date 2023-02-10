package com.pls.documentmanagement.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;

/**
 * Dao for saving or loading the load documents from database.
 * 
 * @author Pavani Challa
 * 
 */
public interface LoadDocumentDao extends AbstractDao<LoadDocumentEntity, Long> {
    /**
     * Updates the CUST_REQ_DOC_RECV_FLAG in LOADS table if the documents required by customer are available for the load.
     * 
     * @param loadId
     *            Load for which the flag has to be updated.
     */
    void updatePaperworkReceived(Long loadId);

    /**
     * Get the missing documents for the load.
     * 
     * @param loadId
     *            Load for which missing document types has to be loaded
     * @return missing document types for the load.
     */
    List<String> getMissingPaperworkForLoad(Long loadId);

    /**
     * Provides full Image objects collection by load id.
     *
     * @param loadId
     *            id of load (shipment).
     * @return Image objects collection for specified load (shipment).
     */
    List<Long> getDocumentIdsForLoad(Long loadId);

    /**
     * Get list of active documents for specified shipment.
     *
     * @param shipmentId
     *            Not <code>null</code> ID of shipment order.
     *
     * @return Not <code>null</code> {@link List}.
     *
     */
    List<ShipmentDocumentInfoBO> getDocumentsInfoForShipment(Long shipmentId);

    /**
     * Get list of active documents for specified Manual Bol.
     * 
     * @param manualBolId
     *            Not <code>null</code>. Id of Manual BOL.
     * @return List of {@link ShipmentDocumentInfoBO}
     */
    List<ShipmentDocumentInfoBO> getDocumentsInfoForManualBol(Long manualBolId);

    /**
     * Delete document by id.
     *
     * @param docIds ids of document
     */
    void deleteDocuments(List<Long> docIds);

    /**
     * Find list of documents for specified load of specified document type.
     *
     * @param loadId load id to which document meta data must belong to
     * @param docType type of document to search
     * @return list of document metadata for specified load and document type
     */
    List<LoadDocumentEntity> findDocumentsForLoad(Long loadId, String docType);

    /**
     * Find list of documents for specified load of specified document type.
     *
     * @param loadId load id to which document meta data must belong to
     * @return list of document metadata for specified load and document type
     */
    List<LoadDocumentEntity> findReqDocumentsForLoad(Long loadId);

    /**
     * Find list of documents for specified manual bol of specified document type.
     *
     * @param manualBolId manual bol id to which document meta data must belong to
     * @param docType type of document to search
     * @return list of document metadata for specified manual bol and document type
     */
    List<LoadDocumentEntity> findDocumentsForManualBol(Long manualBolId, String docType);

    /**
     * Delete permanently temp document.
     *
     * @param loadDocument document to delete
     */
    void deleteTempDocument(LoadDocumentEntity loadDocument);


    /**
     * Find documents that were created early than specified date.
     *
     * @param date date of creation before which all rows must be returned
     * @return list of document that were created before specified date
     */
    List<LoadDocumentEntity> findTempDocumentsOlderThanSpecifiedDate(Date date);

    /**
     * Find logo for customer if it should be placed on BOL document.
     * 
     * @param customerId
     *            id of customer
     * @return logo or <code>null</code>
     */
    LoadDocumentEntity findCustomerLogoForBOL(Long customerId);

    /**
     * Find logo for customer if it should be placed on Shipment Label document.
     * 
     * @param customerId
     *            id of customer
     * @return logo or <code>null</code>
     */
    LoadDocumentEntity findCustomerLogoForShipLabel(Long customerId);

    /**
     * Returns list of documents ids by specified load id.
     * 
     * @param loadId - id of load
     * @return list of documents ids
     */
    List<BigInteger> findRequiredAndAvailableDocumentsByLoadId(Long loadId);

    /**
     * Find document by id and security token.
     *
     * @param id document id
     * @param downloadToken security token
     * @return list of document metadata for specified load and document type
     */
    LoadDocumentEntity findDocumentByIdAndToken(Long id, String downloadToken);

    /**
     * Returns list of documents created dates by specified load id.
     * 
     * @param loadId
     *            id of load
     * @return list of created dates
     */
    List<Date> findCreatedDatesForReqDocsByLoadId(Long loadId);
}
