package com.pls.shipment.service;

import java.util.List;

import com.pls.core.exception.ApplicationException;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceCostItemEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.ReasonBO;
import com.pls.shipment.domain.bo.CarrierInvoiceDetailsListItemBO;
import com.pls.shipment.domain.bo.CostDetailTransfeBO;

/**
 * Business Service for Carrier Invoices (EDI 210).
 *
 * @author Mikhail Boldinov, 01/10/13
 */
public interface CarrierInvoiceService {

    /**
     * Retrieves list of all unmatched Carrier Invoice Details entities.
     *
     * @return list of {@link CarrierInvoiceDetailsListItemBO}
     */
    List<CarrierInvoiceDetailsListItemBO> getUnmatched();

    /**
     * Retrieves list of all archived Carrier Invoice Details entities.
     *
     * @return list of {@link CarrierInvoiceDetailsListItemBO}
     */
    List<CarrierInvoiceDetailsListItemBO> getArchived();

    /**
     * Archives the Carrier Invoice.
     *
     * @param carrierInvoiceId Carrier Invoice details ID
     */
    void archive(Long carrierInvoiceId);

    /**
     * Archives the Carrier Invoices.
     *
     * @param reason {@link ReasonBO}
     */
    void archive(ReasonBO reason);

    /**
     * UnArchives the Carrier Invoice.
     *
     * @param carrierInvoiceId Carrier Invoice details ID
     */
    void unArchive(Long carrierInvoiceId);

    /**
     * Retrieves specified Carrier Invoice Details entities.
     * @param carrierInvoiceId Carrier Invoice details ID
     *
     * @return {@link CarrierInvoiceDetailsEntity}
     */
    CarrierInvoiceDetailsEntity getById(Long carrierInvoiceId);

    /**
     * Save vendor bill created manually for load or matched with load and update load with appropriate info.
     * Cost details may be updated if no low margin.
     * 
     * @param carrierInvoiceDetailsEntity
     *            - to persist
     * @param loadVersion
     *            load Version
     * @return {@link CostDetailTransfeBO}.
     */
    CostDetailTransfeBO saveVendorBillWithMatchedLoad(CarrierInvoiceDetailsEntity carrierInvoiceDetailsEntity,
            Integer loadVersion);

    /**
     * Match Carrier Invoice Details (Vendor Bill) with shipment by id.
     * 
     * @param carrierInvoiceDetailsId
     *            carrier invoice details id to match
     * @param shipmentId
     *            shipment id to match
     */
    void match(Long carrierInvoiceDetailsId, Long shipmentId);

    /**
     * Detach Carrier Invoice Details (Vendor Bills) from shipment.
     * 
     * @param loadId
     *            id of load to detach vendor bills
     * 
     * @param loadVersion
     *            version's load.
     * 
     * @throws ApplicationException
     *             if load was created from vendor bill
     */
    void detach(Long loadId, Integer loadVersion) throws ApplicationException;

    /**
     * Method finds {@link CarrierInvoiceAddressDetailsEntity} by ID.
     * @param id - of {@link CarrierInvoiceAddressDetailsEntity}
     * @return found {@link CarrierInvoiceAddressDetailsEntity}
     */
    CarrierInvoiceAddressDetailsEntity getCarrierInvoiceAddressDetailsEntityById(Long id);

    /**
     * Retrieves Carrier Invoice Details entity matched with specified shipment.
     * 
     * @param shipmentId
     *            id of matched shipment
     * @return {@link CarrierInvoiceDetailsEntity} or <code>null</code>
     */
    CarrierInvoiceDetailsEntity getForShipment(Long shipmentId);

    /**
     * Finds {@link CarrierInvoiceLineItemEntity} by ID.
     *
     * @param id id of {@link CarrierInvoiceLineItemEntity}
     * @return found entity
     */
    CarrierInvoiceLineItemEntity getCarrierInvoiceLineItemById(Long id);

    /**
     * Finds {@link CarrierInvoiceCostItemEntity} by ID.
     *
     * @param id id of {@link CarrierInvoiceCostItemEntity}
     * @return found entity
     */
    CarrierInvoiceCostItemEntity getCarrierInvoiceCostItemById(Long id);

    /**
     * Turns unmatched Carrier Invoice Details entities into archived if they older than <days> days.
     */
    void archiveOldUnmatched();

    /**
     * Update Freight Bill Received date for load if it's empty and can be updated.
     * 
     * @param load
     *            to update
     */
    void updateFreightBillDate(LoadEntity load);
}
