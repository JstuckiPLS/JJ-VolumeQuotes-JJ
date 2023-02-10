package com.pls.shipment.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.bo.CalendarDayBO;
import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.extint.shared.TrackingResponseVO;
import com.pls.organization.domain.bo.PaperworkEmailBO;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.LocationDetailsReportBO;
import com.pls.shipment.domain.bo.LocationLoadDetailsReportBO;
import com.pls.shipment.domain.bo.QuotedBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.domain.bo.ShipmentMissingPaperworkBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardBookedListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardListItemBO;

/**
 * DAO that works with {@link LoadEntity}. Current DAO work with shipments that belong to LTL module.
 *
 * @author Alexey Tarasyuk
 */
public interface LtlShipmentDao extends AbstractDao<LoadEntity, Long> {
    /**
     * Get shipment by id with all dependencies fetched.
     *
     * @param shipmentId
     *            shipment id
     * @return shipment
     */
    LoadEntity getShipmentWithAllDependencies(Long shipmentId);

    /**
     * Find shipment info BO by query params in specified statuses.
     *
     * @param bol
     *            bol number to search
     * @param pro
     *            pro number to search
     * @param originZip
     *            origin zip to search
     * @param destinationZip
     *            destination zip to search
     * @param carrierId
     *            id of carrier to search
     * @param actualPickupDate
     *            actual date of pickup to search
     * @return list of {@link ShipmentListItemBO}
     */
    List<ShipmentListItemBO> findMatchedShipmentsInfo(String bol, String pro, String originZip,
            String destinationZip, Long carrierId, Date actualPickupDate);

    /**
     * Finds shipments.
     *
     * @param organizationId
     *            actual organization id
     * @param dateRangeStatus
     *            shipment status to apply the date range
     * @param dateRange
     *            date range object
     * @param statuses
     *            shipment statuses to find
     * @return list of recent shipments
     */
    List<ShipmentListItemBO> findShipmentsInfo(Long organizationId, ShipmentStatus dateRangeStatus,
            DateRangeQueryBO dateRange, ShipmentStatus... statuses);

    /**
     * Performs shipment search by multiple criterias.
     * 
     * @param search
     *            - object holding all necessary search parameters
     * @param statuses
     *            - shipment statuses to be applied for search
     * @param userId
     *            id of user
     * @return list of found shipments
     */
    List<ShipmentListItemBO> findShipmentInfo(RegularSearchQueryBO search, Long userId,
            ShipmentStatus... statuses);

    /**
     * Method returns summary of shipments by specified parameters. All data is grouped by appropriate date.
     *
     * @param customerId
     *            customer id
     * @param fromDate
     *            from date when shipment status was set to value defined in the groupByStatus parameter
     * @param toDate
     *            to date when shipment status was set to value defined in the groupByStatus parameter
     * @param statuses
     *            the list of acceptable statuses of loads
     * @param groupByStatus
     *            the grouping status
     * @return list of {@link com.pls.core.domain.bo.CalendarDayBO}
     */
    List<CalendarDayBO> getOrganizationCalendarActivity(Long customerId, Date fromDate, Date toDate,
            List<ShipmentStatus> statuses, ShipmentStatus groupByStatus);

    /**
     * Find shipments for given customer user within a given status.
     *
     * @param userId
     *            id of PLS User.
     * @param search
     *            RegularSearchQueryBO object
     * @return shipments for given customer user within a given status.
     */
    List<ShipmentTrackingBoardListItemBO> findOpenShipments(Long userId, RegularSearchQueryBO search);

    /**
     * Find shipments for given customer user which are delivered but we don't have any vendor bill for it
     * yet.
     *
     * @param userId
     *            id of PLS User.
     * @return list of {@link ShipmentListItemBO}.
     */
    List<ShipmentListItemBO> findUnbilledShipments(Long userId);

    /**
     * Find shipments by PRO number and Carrier SCAC.
     *
     * @param scac
     *            carrier's SCAC
     * @param proNumber
     *            PRO number
     * @return {@link List} of found {@link LoadEntity}
     */
    List<LoadEntity> findAllShipmentsByScacAndProNumber(String scac, String proNumber);

    /**
     * Find shipments by BOL number, Carrier SCAC, ZIP codes and with empty PRO number.
     *
     * @param bol
     *            shipment bol
     * @param scac
     *            carrier's SCAC
     * @param originZip
     *            origin zip
     * @param destinationZip
     *            destination zip
     * @return {@link List} of found {@link LoadEntity}
     */
    List<LoadEntity> findShipmentByScacAndBolNumberAndZip(String bol, String scac, String originZip,
            String destinationZip);

    /**
     * Find shipments by BOL number, Carrier SCAC, Address information and with empty PRO number.
     *
     * @param bol
     *            shipment bol
     * @param scac
     *            carrier's SCAC
     * @param originCity
     *            origin city
     * @param originState
     *            origin state code
     * @param destCity
     *            destination city
     * @param destState
     *            destination state code
     * @return {@link List} of found {@link LoadEntity}
     */
    List<LoadEntity> findShipmentByScacAndBolNumberAndCityAndState(String bol, String scac, String originCity,
            String originState, String destCity, String destState);

    /**
     * Finds shipments by both awarded carrier scac and bol number.
     * <p/>
     * Method used to identify a load for matching with {@link com.pls.shipment.domain.LoadTrackingEntity}
     * created from EDI.
     *
     * @param scac
     *            awarded carrier scac
     * @param bol
     *            bol
     * @return found {@link LoadEntity}
     */
    List<LoadEntity> findShipmentsByScacAndBolNumber(String scac, String bol);

    /**
     * Finds unique shipment by both customer org id and shipment number.
     * <p/>
     * Method used to identify a load for matching with {@link com.pls.shipment.domain.LoadTrackingEntity}
     * created from EDI.
     *
     * @param customerOrgId
     *            customer org id
     * @param shipmentNo
     *            shipment number
     * @return found {@link LoadEntity}
     */
    LoadEntity findShipmentByShipmentNumber(Long customerOrgId, String shipmentNo);
    
    /**
     * Finds shipments by both customer org id and shipment number.
     *
     * @param customerOrgId
     *            customer org id
     * @param shipmentNo
     *            shipment number
     * @return List of found {@link LoadEntity}.
     */
    List<LoadEntity> findShipmentsByShipmentNumber(Long customerOrgId, String shipmentNo);

    /**
     * Get the n last shipments.
     *
     * @param organizationId
     *            actual organization id
     * @param personId
     *            User ID.
     * @param count
     *            how many shipments should be returned.
     * @return list of {@link ShipmentListItemBO}
     */
    List<ShipmentListItemBO> findLastNShipments(Long organizationId, Long personId, int count);

    /**
     * Finds all shipments that do not have required paperwork. This method returns only those shipments for
     * which the documents can be downloaded using API (Imaging should be set to 'API' in organization
     * services).
     *
     * @param shipmentId
     *            if null, returns all shipments with missing paperwor. Else checks if that shipment is
     *            missing paperwork
     * @return the shipments with missing paperwork for which carriers have provided the api to download them.
     */
    List<ShipmentMissingPaperworkBO> getShipmentsWithMissingReqPaperwork(Long shipmentId);

    /**
     * Find status of specific shipment.
     * 
     * @param shipmentId
     *            Shipment ID
     * @return status of shipment.
     */
    ShipmentStatus getShipmentStatus(Long shipmentId);

    /**
     * Find shipments for given customer user within a booked status.
     *
     * @param userId
     *            id of PLS User.
     * @return shipments for given customer user within a booked status.
     */
    List<ShipmentTrackingBoardBookedListItemBO> findBookedShipments(Long userId);

    /**
     * Find shipments for given customer within a undelivered status.
     *
     * @return shipments for given customer within a undelivered status.
     */
    List<ShipmentTrackingBoardListItemBO> findUndeliveredShipments();

    /**
     * Finds all shipments that are not yet delivered for tracking. This method returns only those shipments
     * which can be tracked using API.
     *
     * @return the shipments not yet delivered for which carriers have provided the api to track them.
     */
    List<ShipmentTrackingBO> getShipmentsToTrack();

    /**
     * Sets the load to confirm pickup status (IN TRANSIT) and updates the pickup date.
     *
     * @param load
     *            load information to be updated
     * @param personId
     *            User performing the update
     */
    void confirmPickup(TrackingResponseVO load, Long personId);

    /**
     * Sets the load to confirm delivery status (DELIVERED) and updates the delivery date.
     *
     * @param load
     *            load information to be updated
     * @param personId
     *            User performing the update
     */
    void confirmDelivery(TrackingResponseVO load, Long personId);

    /**
     * Shipment search by estimated and actual pickup date.
     * 
     * @param search
     *            - object holding all necessary search parameters
     * @param statuses
     *            - all statuses
     * @param userId
     *            id of user
     * @return list of found shipments
     */
    List<ShipmentListItemBO> findShipmentByEstimatedAndActualDate(RegularSearchQueryBO search,
            List<ShipmentStatus> statuses, Long userId);

    /**
     * Shipment search by search criterias.
     * 
     * @param search
     *            - object holding all necessary search parameters
     * @param userId
     *            id of user
     * @return list of found shipmentsR
     */
    List<ShipmentListItemBO> findAllShipments(RegularSearchQueryBO search, Long userId);

    /**
     * Gets shipment's carrier.
     *
     * @param shipmentId
     *            shipment ID
     * @return shipment's {@link CarrierEntity}
     */
    CarrierEntity getShipmentCarrier(Long shipmentId);

    /**
     * Change status for shipment with specified Id.
     *
     * @param shipmentId
     *            id of load which status should be changed.
     * @param status
     *            new status
     */
    void updateStatus(Long shipmentId, ShipmentStatus status);

    /**
     * Gets load pro number for specified loadId.
     *
     * @param loadId
     *            - Id of Load
     * @return pro number.
     */
    String getLoadProNumber(Long loadId);

    /**
     * Get Primary Load Cost Detail for Load.
     *
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @return {@link QuotedBO}
     */
    QuotedBO getPrimaryLoadCostDetail(Long loadId);

    /**
     * Get matched Load for VendorBill where attachment is less than seven days.
     *
     * @return {@link LoadEntity}
     */
    List<LoadEntity> getLoadForMatchedVendorBill();

    /**
     * Update financial status for load.
     *
     * @param auditRecord
     *            audit record
     * @param loadStatus
     *            new financial status for loads
     */
    void updateLoadFinancialStatuses(AuditReasonBO auditRecord, ShipmentFinancialStatus loadStatus);

    /**
     * Finds a shipment by shipper reference number and Customer Organization Id.
     * 
     * @param customerOrgId
     *            organization Id of the customer
     * @param shipmentNo
     *            shipment reference number
     * @param bol
     *            number of the shipment
     * @return loadEntity matching shipment
     */
    LoadEntity findShipmentByBolAndShipmentNumber(Long customerOrgId, String shipmentNo, String bol);

    /**
     * Returns {@link BillToEntity} related to shipment.
     * 
     * @param shipmentId
     *            - Not <code>null</code> {@link LoadEntity#getId()}.
     * @return related to shipment {@link BillToEntity#getId()}.
     */
    Long getShipmentBillTo(Long shipmentId);

    /**
     * Returns List of load ids matched by specified pro number and carrier org id.
     * 
     * @param proNum
     *            - Not <code>null</code> Pro Number.
     * @param orgId
     *            - Not <code>null</code> Carrier org id.
     * @return List of load ids.
     */
    List<BigDecimal> findMatchedLoadsByProAndOrgId(String proNum, Long orgId);

    /**
     * Gets the load invoice type.
     *
     * @param loadId
     *            the load id
     * @return the load invoice type
     */
    InvoiceType getLoadInvoiceType(Long loadId);

    /**
     * Find shipments for given customer within a hold status.
     *
     * @return shipments for given customer within a hold status.
     */
    List<ShipmentListItemBO> findHoldShipments();

    /**
     * Returns list of paperwork emails for carriers.
     * 
     * @param days
     *            the number of days.
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
    List<LocationLoadDetailsReportBO> getLocationLoadDetails(String zip, String city, Boolean origin,
            int dateType, Long personId);
}
