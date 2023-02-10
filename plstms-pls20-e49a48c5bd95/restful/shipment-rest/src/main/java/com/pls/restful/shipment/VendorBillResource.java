package com.pls.restful.shipment;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.dto.shipment.AdditionalCostDetailsItemsDTO;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.dto.shipment.VendorBillDTO;
import com.pls.restful.util.ResourceParamsUtils;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.ReasonBO;
import com.pls.shipment.domain.bo.CarrierInvoiceDetailsListItemBO;
import com.pls.shipment.domain.bo.CostDetailTransfeBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.SalesOrderService;
import com.pls.shipment.service.ShipmentService;

/**
 * Vendor Bills REST resources.
 *
 * @author Mikhail Boldinov, 02/10/13
 */
@Controller
@Transactional
@RequestMapping("/vendor-bills")
public class VendorBillResource {

    @Autowired
    private CarrierInvoiceService carrierInvoiceService;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentBuilderHelper shipmentBuilderUtils;

    /**
     * REST service to retrieve list of all unmatched Carrier Invoices.
     *
     * @return list of {@link VendorBillDTO} which are not matched to shipment orders
     */
    @RequestMapping(value = "/unmatched", method = RequestMethod.GET)
    @ResponseBody
    public List<CarrierInvoiceDetailsListItemBO> unmatched() {
        return carrierInvoiceService.getUnmatched();
    }

    /**
     * REST service to retrieve list of all archived Carrier Invoices.
     *
     * @return list of {@link VendorBillDTO} which are archived
     */
    @RequestMapping(value = "/archived", method = RequestMethod.GET)
    @ResponseBody
    public List<CarrierInvoiceDetailsListItemBO> archived() {
        return carrierInvoiceService.getArchived();
    }

    /**
     * REST service to archive specified Carrier Invoice.
     *
     * @param vendorBillId id of archived entity
     * @throws EntityNotFoundException thrown if entity is not found
     */
    @RequestMapping(value = "/{vendorBillId}/archive", method = RequestMethod.POST)
    @ResponseBody
    public void archive(@PathVariable("vendorBillId") Long vendorBillId) throws EntityNotFoundException {
        carrierInvoiceService.archive(vendorBillId);
    }

    /**
     * REST service to unArchive specified Carrier Invoice.
     *
     * @param vendorBillId id of unArchived entity
     * @throws EntityNotFoundException thrown if entity is not found
     */
    @RequestMapping(value = "/{vendorBillId}/unArchive", method = RequestMethod.POST)
    @ResponseBody
    public void unArchive(@PathVariable("vendorBillId") Long vendorBillId) throws EntityNotFoundException {
        carrierInvoiceService.unArchive(vendorBillId);
    }

    /**
     * REST service to archive specified Carrier Invoices.
     *
     * @param reason {@link ReasonBO}
     * @throws EntityNotFoundException thrown if entity is not found
     */
    @RequestMapping(value = "/archiveList", method = RequestMethod.POST)
    @ResponseBody
    public void archive(@RequestBody ReasonBO reason) throws EntityNotFoundException {
        carrierInvoiceService.archive(reason);
    }

    /**
     * REST service to retrieve specified Carrier Invoice.
     *
     * @param vendorBillId id of entity
     * @return desired {@link VendorBillDTO}
     * @throws EntityNotFoundException thrown if entity is not found
     */
    @RequestMapping(value = "/{vendorBillId}", method = RequestMethod.GET)
    @ResponseBody
    public VendorBillDTO get(@PathVariable("vendorBillId") Long vendorBillId) throws EntityNotFoundException {
        return shipmentBuilderUtils.getVendorBillDTOBuilder().buildDTO(carrierInvoiceService.getById(vendorBillId));
    }

    /**
     * REST service to add manually created vendor bill to load.
     * 
     * @param vendorBill
     *            {@link VendorBillDTO} to persist.
     * 
     * @throws ValidationException
     *             thrown if entity is not valid
     * @return version's load.
     */
    @RequestMapping(method = RequestMethod.POST)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @ResponseBody
    public AdditionalCostDetailsItemsDTO addVendorBill(@RequestBody VendorBillDTO vendorBill)
            throws ValidationException {
        CostDetailTransfeBO invoiceItems = carrierInvoiceService.saveVendorBillWithMatchedLoad(
                shipmentBuilderUtils.getVendorBillDTOBuilder().buildEntity(vendorBill), vendorBill.getLoadVersion());
        return new AdditionalCostDetailsItemsDTO(
                shipmentBuilderUtils.getCostDetailItemDTOBuilder().buildList(invoiceItems.getCostDetailItems()),
                invoiceItems.getLoadVersion(), invoiceItems.getActiveCostDetailId());
    }

    /**
     * REST service to retrieve list of shipments which can be matched with specified Carrier Invoice.
     * 
     * @param vendorBillId
     *            id of vendor bill to match data
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
     * @return list of {@link VendorBillDTO} which are not matched to shipment orders
     * @throws ApplicationException
     *             if the wrong Bol Number of inappropriate wildcard pattern was entered.
     */
    @RequestMapping(value = "/{vendorBillId}/sale-order", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentListItemBO> findSalesOrders(@PathVariable("vendorBillId") Long vendorBillId,
            @RequestParam(value = "bol", required = false) String bol,
            @RequestParam(value = "pro", required = false) String pro,
            @RequestParam(value = "originZip", required = false) String originZip,
            @RequestParam(value = "destinationZip", required = false) String destinationZip,
            @RequestParam(value = "carrierId", required = false) Long carrierId,
            @RequestParam(value = "actualPickupDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date actualPickupDate)
            throws ApplicationException {
        String verifiedBol = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(bol);
        return shipmentService.findDeliveredShipmentsForVendorBill(verifiedBol, pro, originZip,
                destinationZip, carrierId, actualPickupDate);
    }

    /**
     * REST service to match vendor bill with sale order.
     *
     * @param vendorBillId id of vendor bill to match with shipment
     * @param shipmentId id of shipment to match with shipment
     */
    @RequestMapping(value = "/{vendorBillId}/match/{shipmentId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void matchSaleOrder(@PathVariable("vendorBillId") Long vendorBillId,
            @PathVariable("shipmentId") Long shipmentId) {
        carrierInvoiceService.match(vendorBillId, shipmentId);
    }

    /**
     * REST service to detach vendor bills from sales order.
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
    @RequestMapping(value = "/{loadId}/detach", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void detachVendorBillsFromSalesOrder(@PathVariable("loadId") Long loadId,
            @RequestParam("loadVersion") Integer loadVersion) throws ApplicationException {
        carrierInvoiceService.detach(loadId, loadVersion);
    }

    /**
     * REST service to retrieve list of all Carrier Invoices matched with specified shipment.
     *
     * @param shipmentId id of matched shipment
     * @return list of {@link VendorBillDTO} matched to shipment order
     */
    @RequestMapping(value = "/list-by-shipment/{shipmentId}", method = RequestMethod.GET)
    @ResponseBody
    public VendorBillDTO getForShipment(@PathVariable("shipmentId") Long shipmentId) {
        VendorBillDTO result = null;
        CarrierInvoiceDetailsEntity vendorBill = carrierInvoiceService.getForShipment(shipmentId);
        if (vendorBill != null) {
            result = shipmentBuilderUtils.getVendorBillDTOBuilder().buildDTO(vendorBill);
        }
        return result;
    }

    /**
     * REST service to create load from vendor bill.
     *
     * @param vendorBillId
     *            id of unArchived entity
     * @param customerId
     *            id of customer
     * @return new sales order {@link ShipmentDTO}
     * @throws ApplicationException
     *             thrown if entity is not created
     */
    @RequestMapping(value = "/{vendorBillId}/create/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    public ShipmentDTO createSalesOrder(@PathVariable("vendorBillId") Long vendorBillId,
            @PathVariable("customerId") Long customerId) throws ApplicationException {
        if (!SecurityUtils.isPlsUser()) {
            throw new AccessDeniedException("You aren't PLS user and don't have permissions to fulfill this request.");
        }

        userPermissionsService.checkOrganization(customerId);
        LoadEntity load = salesOrderService.createNewOrder(vendorBillId, customerId);
        ShipmentDTO shipment = shipmentBuilderUtils.getShipmentDTOBuilder().buildDTO(load);
        shipment.setMatchedVendorBillId(vendorBillId);
        return shipment;
    }

}
