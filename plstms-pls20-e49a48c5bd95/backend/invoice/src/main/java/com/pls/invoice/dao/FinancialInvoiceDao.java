package com.pls.invoice.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.enums.ProcessingPeriod;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.invoice.domain.BillToInvoiceProcessingTimeBO;
import com.pls.invoice.domain.FinancialInvoiceHistoryEntity;
import com.pls.invoice.domain.bo.InvoiceBO;
import com.pls.invoice.domain.bo.InvoiceProcessingItemBO;
import com.pls.invoice.domain.bo.InvoiceResultBO;
import com.pls.invoice.domain.bo.enums.InvoiceReleaseStatus;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * DAO for invoice processing.
 * 
 * @author Sergey Kirichenko
 */
public interface FinancialInvoiceDao extends AbstractDao<FinancialInvoiceHistoryEntity, Long> {

    /**
     * Get loads for invoicing to customer by invoice ID.
     * 
     * @param invoiceId
     *            invoice ID
     * @return list of {@link LoadEntity}
     */
    List<LoadEntity> getLoadsByInvoiceId(Long invoiceId);

    /**
     * Get adjustments for invoicing to customer by invoice ID.
     * 
     * @param invoiceId
     *            invoice ID
     * @return list of {@link LoadEntity}
     */
    List<FinancialAccessorialsEntity> getAdjustmentsByInvoiceId(Long invoiceId);

    /**
     * Returns BillTo's with automatic processing by period and day of week or time of day.
     * 
     * @param period
     *            processing period daily or weekly
     * @param dayOfWeek
     *            processing day of week
     * @param minutes
     *            current minutes in GMT TimeZone
     * @return {@link List} Bill To's
     */
    List<BillToInvoiceProcessingTimeBO> getBillToIdsForAutomaticProcessing(ProcessingPeriod period, int dayOfWeek, Integer minutes);

    /**
     * Update finalization status for specified loads.
     * 
     * @param loadIds
     *            ids of loads to update
     * @param invoiceDate
     *            GL Date
     * @param userId
     *            id of user responsible for update
     */
    void updateLoadsFinancialStatuses(List<Long> loadIds, Date invoiceDate, Long userId);

    /**
     * Get next invoice ID from sequence.
     * 
     * @return next invoice ID
     */
    Long getNextInvoiceId();

    /**
     * Get next value from sequence for CBI.
     * 
     * @return next sequence value
     */
    Long getNextCBIInvoiceSequenceNumber();

    /**
     * Get Load IDs by Bill To ID for invoicing.
     * 
     * @param billToId
     *            bill to ID
     * @param filterDeliveryDate
     *            filter loads by specified delivery date
     * @return list of Load IDs
     */
    List<Long> getLoadsByBillTo(Long billToId, Date filterDeliveryDate);

    /**
     * Get Adjustment IDs by Bill To ID for invoicing.
     * 
     * @param billToId
     *            bill to ID
     * @return list of Adjustment IDs
     */
    List<Long> getAdjustmentsByBillTo(Long billToId);

    /**
     * Insert data into invoice history table for processed loads.
     * 
     * @param invoiceId
     *            id of invoice transaction
     * @param status
     *            {@link InvoiceReleaseStatus} status of invoice
     * @param loadsIds
     *            list of loads ID's to be processed
     * @param userId
     *            id of user
     */
    void insertLoads(Long invoiceId, InvoiceReleaseStatus status, Collection<Long> loadsIds, Long userId);

    /**
     * Insert data into invoice history table for processed adjustments.
     * 
     * @param invoiceId
     *            id of invoice transaction
     * @param status
     *            {@link InvoiceReleaseStatus} status of invoice
     * @param adjustmentsIds
     *            list of adjustments ID's to be processed
     * @param userId
     *            id of user
     */
    void insertAdjustments(Long invoiceId, InvoiceReleaseStatus status, Collection<Long> adjustmentsIds, Long userId);

    /**
     * Get list of loads and adjustments by invoice ID.
     * 
     * @param invoiceId
     *            {@link FinancialInvoiceHistoryEntity#getInvoiceId()}
     * @return results of invoice
     */
    List<InvoiceResultBO> getInvoiceResults(Long invoiceId);

    /**
     * Get list of all loads ID's by Invoice ID and Bill To ID. Even loads ID's from adjustments. But
     * excluding loads ID's from Do Not Invoice adjustments.
     * 
     * @param invoiceId
     *            invoice ID
     * @param billToId
     *            Bill To ID
     * @return list of loads ID's
     */
    List<Long> getAllLoadsIds(Long invoiceId, Long billToId);

    /**
     * Get list of group invoice numbers (without extension) by Invoice ID and Bill To ID. It excludes Invoice
     * Numbers of Do Not Invoice adjustments.
     * 
     * @param invoiceId
     *            invoice ID
     * @param billToId
     *            Bill To ID
     * @return list of Group Invoice Numbers
     */
    List<String> getGroupInvoiceNumbers(Long invoiceId, Long billToId);

    /**
     * Insert data into invoice history table for loads to be re-processed.
     * 
     * @param invoiceId
     *            invoice ID
     * @param loads
     *            list of loads ID's to be re-processed
     * @param userId
     *            id of user
     */
    void insertLoadsForReProcess(Long invoiceId, Collection<Long> loads, Long userId);

    /**
     * Insert data into invoice history table for adjustments to be re-processed.
     * 
     * @param invoiceId
     *            invoice ID
     * @param adjustments
     *            list of adjustments ID's to be re-processed
     * @param userId
     *            id of user
     */
    void insertAdjustmentsForReProcess(Long invoiceId, Collection<Long> adjustments, Long userId);

    /**
     * Get bill to entity with eagerly fetched properties by invoice ID.<br/>
     * <h1>!!!This method works correctly only when there is only one bill to for invoice!!!</h1><br/>
     *
     * @param invoiceId
     *            invoice ID
     * @return {@link com.pls.core.domain.organization.BillToEntity}
     */
    BillToEntity getBillToByInvoiceId(Long invoiceId);

    /**
     * Update loads which should be invoiced in financials.
     * 
     * @param loadsIds
     *            IDs of loads that should be updated.
     * @param glDate
     *            Invoice Date
     * @param userId
     *            ID of user responsible for invoicing
     */
    void updateLoadsInvoicedInFinancials(List<Long> loadsIds, Date glDate, Long userId);

    /**
     * Update adjustments which should be invoiced in financials.
     * 
     * @param adjustmentsIds
     *            IDs of adjustments that should be updated.
     * @param glDate
     *            Invoice Date
     * @param userId
     *            ID of user responsible for invoicing
     */
    void updateAdjustmentsInvoicedInFinancials(List<Long> adjustmentsIds, Date glDate, Long userId);

    /**
     * Get rebill adjustments for automatic invoice processing.
     * 
     * @param billToId
     *            id of Bill To for which rebill adjustment exist.
     * @return list of rebill adjustments for other bill to's than specified
     */
    List<InvoiceBO> getRebillAdjustmentsForAutomaticProcessing(Long billToId);

    /**
     * Get loads with details for processing.
     * 
     * @param loads
     *            ids
     * @return loads detailed info
     */
    List<InvoiceProcessingItemBO> getLoadsForProcessing(List<Long> loads);

    /**
     * Get adjustments with details for processing.
     * 
     * @param adjustments
     *            ids
     * @return adjustments detailed info
     */
    List<InvoiceProcessingItemBO> getAdjustmentsForProcessing(List<Long> adjustments);

    /**
     * Update financial status for adjustments.
     *
     * @param adjustmentIds
     *            ids of adjustments to update
     * @param status
     *            new financial status for adjustment
     * @param userId
     *            id of user performing the update
     */
    void updateAdjustmentsFinancialStatus(List<Long> adjustmentIds, ShipmentFinancialStatus status, Long userId);

    /**
     * Set invoice number for loads.
     * 
     * @param loadIds
     *            ids of loads to update
     * @param invoiceDate
     *            GL Date
     * @param userId
     *            id of user responsible for update
     */
    void updateLoadsWithInvoiceNumbers(List<Long> loadIds, Date invoiceDate, Long userId);

    /**
     * Set invoice number for adjustments.
     * 
     * @param adjustmentIds
     *            ids of adjustments to update
     * @param invoiceDate
     *            GL Date
     * @param userId
     *            id of user responsible for update
     */
    void updateAdjustmentsWithInvoiceNumbers(List<Long> adjustmentIds, Date invoiceDate, Long userId);

    /**
     * Set invoice number for load.
     * 
     * @param loadId
     *            ID of load
     * @param groupInvoiceNumber
     *            invoice number of CBI group
     * @param invoiceNumber
     *            invoice number of adjustment
     * @param invoiceDate
     *            GL Date
     * @param userId
     *            id of user responsible for update
     */
    void updateLoadWithInvoiceNumber(Long loadId, String groupInvoiceNumber, String invoiceNumber, Date invoiceDate, Long userId);

    /**
     * Set invoice number for adjustment.
     * 
     * @param adjustmentId
     *            ID of adjustment
     * @param groupInvoiceNumber
     *            invoice number of CBI group
     * @param invoiceNumber
     *            invoice number of adjustment
     * @param invoiceDate
     *            GL Date
     * @param userId
     *            id of user responsible for update
     */
    void updateAdjustmentWithInvoiceNumber(Long adjustmentId, String groupInvoiceNumber, String invoiceNumber, Date invoiceDate, Long userId);

    /**
     * Insert data into load finalization history table for processed loads.
     * 
     * @param successfulLoads
     *            list of loads ID's to insert
     * @param requestId
     *            ID of financial request
     */
    void insertFinalizationHistoryLoads(List<Long> successfulLoads, Long requestId);

    /**
     * Insert data into load finalization history table for processed adjustments.
     * 
     * @param successfulAdjustments
     *            list of adjustments ID's to insert
     * @param requestId
     *            ID of financial request
     */
    void insertFinalizationHistoryAdjustments(List<Long> successfulAdjustments, Long requestId);

    /**
     * Get adjustment reasons by list of adjustments IDs.
     * 
     * @param adjustments
     *            list of adjustments IDs
     * @return distinct list of adjustments reasons (key is Load ID, value is adjustment reason)
     */
    List<KeyValueBO> getAdjustmentReasons(List<Long> adjustments);
}
