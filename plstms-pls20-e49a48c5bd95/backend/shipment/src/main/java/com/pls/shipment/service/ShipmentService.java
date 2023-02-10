package com.pls.shipment.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.extint.shared.TrackingResponseVO;
import com.pls.organization.domain.bo.PaperworkEmailBO;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadPricingDetailsEntity;
import com.pls.shipment.domain.bo.LocationDetailsReportBO;
import com.pls.shipment.domain.bo.LocationLoadDetailsReportBO;
import com.pls.shipment.domain.bo.QuotedBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.domain.bo.ShipmentMissingPaperworkBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBO;

/**
 * Shipment Service.
 * 
 * @author Gleb Zgonikov
 */
public interface ShipmentService {
    /**
     * REF_TYPE for Guaranteed service constant.
     */
    String GUARANTEED_SERVICE_REF_TYPE = "GD";

    /**
     * Find {@link LoadEntity} by its ID.
     * 
     * @param shipmentId
     *            Not <code>null</code> ID.
     * 
     * @return {@link LoadEntity} or <code>null</code>.
     */
    LoadEntity findById(Long shipmentId);

    /**
     * Used to get list of shipments with cost of each according to parameters.
     * 
     * @param customerId
     *            customer id
     * @param userId
     *            id of user to select appropriate items
     * @param count
     *            max results.
     * @return ShipmentWithCost BO
     */
    List<ShipmentListItemBO> findLastShipments(Long customerId, Long userId, int count);

    /**
     * Return load with specified id with cleared ID, pick-up date, selected carrier info and status changed to Open.
     * 
     * @param loadId
     *            ID of load that should be copied
     * @return copied load {@link LoadEntity}
     */
    LoadEntity getCopyOfShipment(Long loadId);

    /**
     * Get shipment with already fetched data that is commonly used.
     * 
     * @param shipmentId
     *            id of shipment
     * @return shipment
     */
    LoadEntity getShipmentWithAllDependencies(Long shipmentId);

    /**
     * Cancel shipment.
     * 
     * @param shipmentId
     *            shipment id
     * @throws InternalJmsCommunicationException
     *             if load tender message can not be published to external integration message queue
     * @throws EntityNotFoundException
     *             if shipment with specified id does not exist
     * @throws EdiProcessingException thrown when EDI processing fails.
     * 
     */
    void cancelShipment(long shipmentId) throws InternalJmsCommunicationException, EntityNotFoundException, EdiProcessingException;

    /**
     * Check that all paperwork required for customer invoice has been uploaded for the load. Set appropriate flag to the load and save load.
     * 
     * @param load
     *            to check.
     */
    void checkPaperworkRequiredForCustomerInvoice(LoadEntity load);

    /**
     * Find all delivered and not invoiced shipments by query params.
     * 
     * @param bol bol number to search
     * @param pro pro number to search
     * @param originZip origin zip to search
     * @param destinationZip destination zip to search
     * @param carrierId id of carrier to search
     * @param actualPickupDate actual date of pickup to search
     * @throws EntityNotFoundException if vendor bill with specified id doesn't exist
     * @return list of {@link ShipmentListItemBO}
     */
    List<ShipmentListItemBO> findDeliveredShipmentsForVendorBill(String bol, String pro,
            String originZip, String destinationZip, Long carrierId, Date actualPickupDate);

    /**
     * Finds all shipments that do not have required paperwork. This method returns only those shipments for which the documents can be downloaded
     * using API.
     * 
     * @param shipmentId
     *            if null, returns all shipments with missing paperwork. Else checks if that shipment is missing paperwork
     * @return the shipments with missing paperwork for which carriers have provided the api to download them.
     */
    List<ShipmentMissingPaperworkBO> getShipmentsWithMissingReqPaperwork(Long shipmentId);

    /**
     * Dispatches shipment and sends Shipment tender EDI to carrier.
     * 
     * @param shipmentId
     *            shipment id to dispatch
     * @throws EntityNotFoundException
     *             if shipment with specified id does not exist
     * @throws InternalJmsCommunicationException
     *             if load tender message can not be published to external integration message queue
     * @throws EdiProcessingException  thrown when EDI processing fails.
     * 
     */
    void dispatchShipment(Long shipmentId) throws ApplicationException;

    /**
     * Finds all shipments that are not yet delivered for tracking. This method returns only those shipments which can be tracked using API.
     * 
     * @return the shipments not yet delivered for which carriers have provided the api to track them.
     */
    List<ShipmentTrackingBO> getShipmentsToTrack();

    /**
     * Updates the load to IN TRANSIT or DELIVERED status and updates the pickup/delivery dates accordingly.
     * 
     * @param load
     *            load information to be updated
     * @param personId
     *            User performing the update
     */
    void updateLoadStatus(TrackingResponseVO load, Long personId);

    /**
     * Get Primary Load Cost Detail for Load.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @return {@link QuotedBO}
     */
    QuotedBO getPrimaryLoadCostDetail(Long loadId);

    /**
     * Close load by Id.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}.
     * @throws EntityNotFoundException
     *             if shipment with specified id does not exist.
     */
    void closeLoad(long loadId) throws EntityNotFoundException;

    /**
     * Override date hold for load (Send load to Financial Board and calculate audit reasons).
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}.
     */
    void overrideDateHold(Long loadId);

    /**
     * Update Freight Bill Date for Load.
     * 
     * @param freightBillDate
     *            - freight bill date for update
     * @param loadId
     *            - {@link LoadEntity#getId()}
     */
    void updateFrtBillDate(Date freightBillDate, Long loadId);

    /**
     * Regenerate docs for shipment.
     *
     * @param loadId - load id
     * @param markup - markup
     * @param hideCreatedTime - true if needed hide created time
     * @return map with documentType and dovument id pair
     * @throws PDFGenerationException the PDF generation exception
     */
    Map<DocumentTypes, Long> regenerateDocsForShipment(Long loadId, Long markup, Boolean hideCreatedTime) throws PDFGenerationException;

    /**
     * Checks if selected billto credit settings allows to book or update specified shipment.
     * 
     * <p>
     * <li> Verify if selected {@link BillToEntity#getCreditHold()} is <code>true</code> then throws {@link ApplicationException}.</li>
     * <li> Verify if selected {@link BillToEntity#getAvailableCreditAmount()} is less then {@link LoadCostDetailsEntity#getTotalRevenue()}
     *      then throws {@link ApplicationException} </li>
     * </p>
     * 
     * @param load - {@link LoadEntity} which should be checked. Not <code>null</code>.
     * @throws ApplicationException when selected billTo doesn't has enough credit limit capabilities.
     */
    void checkBillToCreditLimit(LoadEntity load) throws ApplicationException;

    /**
     * See {@link #checkBillToCreditLimit(LoadEntity)}.
     * 
     * @param load
     *            - {@link LoadEntity} which should be checked. Not <code>null</code>.
     * @return <code>true</code> if bill to credit limit is not exceeded. <code>false</code> otherwise
     */
    boolean checkBillToCreditLimitSafe(LoadEntity load);

    /**
     * Returns List of load ids matched by specified pro number and carrier org id.
     * 
     * @param proNum - Not <code>null</code> Pro Number.
     * @param orgId - Not <code>null</code> Carrier org id.
     * @return List of load ids.
     */
    List<BigDecimal> getMatchedLoadsByProAndOrgId(String proNum, Long orgId);

    /**
     * Get shipment's pricing details.
     * 
     * @param shipmentId
     *            id of shipment
     * @return pricing details
     */
    LoadPricingDetailsEntity getShipmentPricingDetails(Long shipmentId);

    /**
     * Adds OverDimentional accessorial if it's absent in the shipment, but appropriate cost exits.
     * 
     * @param loadEntity
     *            {@link LoadEntity}.
     */
    void addImplicitOverdimentionalAccessorial(LoadEntity loadEntity);

    /**
     * Update Time Zones for load.
     * 
     * @param load
     *            to update
     */
    void updateTimeZoneInfo(LoadEntity load);

    /**
     * Returns list of paperwork emals for carriers.
     * 
     * @param days the number of days.
     * @return List of paperwork emails.
     */
    List<PaperworkEmailBO> getPaperworkEmails(int days);

    /**
     * Get details for Pickups And Deliveries customer report.
     * 
     * @param personId
     *            id of current user
     * @return list of details
     */
    List<LocationDetailsReportBO> getLocationDetails(Long personId);

    /**
     * Get list of load details for Pickups And Deliveries reports.
     * 
     * @param zip
     *            zip (can be <code>null</code>)
     * @param city
     *            city (can be <code>null</code>)
     * @param origin
     *            <code>true</code> if specified zip is for origin address
     * @param dateType
     *            date type (-1 means late, 0 - today, 1 - tomorrow)
     * @param personId
     *            id of current user
     * @return list of load details
     */
    List<LocationLoadDetailsReportBO> getLocationLoadDetails(String zip, String city, Boolean origin, int dateType, Long personId);
    
    void addOrganizationNotifications(LoadEntity load);
}
