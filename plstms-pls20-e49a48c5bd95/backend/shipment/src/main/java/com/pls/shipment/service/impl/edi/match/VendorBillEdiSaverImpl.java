package com.pls.shipment.service.impl.edi.match;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CarrierDao;
import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.CarrierInvoiceDetailsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.CarrierEdiCostTypesEntity;
import com.pls.shipment.domain.CarrierInvoiceCostItemEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.SalesOrderService;
import com.pls.shipment.service.ShipmentDocumentService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.edi.match.VendorBillEdiSaver;

/**
 * Implementation of {@link VendorBillEdiSaver}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class VendorBillEdiSaverImpl implements VendorBillEdiSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(VendorBillEdiSaverImpl.class);

    @Autowired
    private VendorBillToLoadMatcherImpl loadMatcher;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private CarrierInvoiceService carrierInvoiceService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private CarrierInvoiceDetailsDao carrierInvoiceDao;

    @Autowired
    private LtlShipmentDao loadDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CarrierDao carrierDao;

    @Autowired
    private ShipmentDocumentService shipmentDocumentService;

    @Value("${admin.personId}")
    private Long ediUserId;

    @Override
    public LoadEntity saveEdiVendorBill(CarrierInvoiceDetailsEntity vendorBill, Map<String, String> carrierRefTypeMap) throws ApplicationException {
        createCostItemsFromLineItems(vendorBill, carrierRefTypeMap);
        LoadEntity load = loadMatcher.findMatchingLoad(vendorBill);
        if (load == null) {
            load = createLoadFromVendorBill(vendorBill);
        }

        if (load == null || isInvoiced(load)) {
            if (isDuplicateInvoice(load, vendorBill)) {
                vendorBill.setStatus(Status.INACTIVE);
            }
            carrierInvoiceDao.saveOrUpdate(vendorBill);
            return null;
        } else {
            shipmentService.checkPaperworkRequiredForCustomerInvoice(load);
            vendorBill.setMatchedLoadId(load.getId());
            carrierInvoiceService.saveVendorBillWithMatchedLoad(vendorBill, null);
            return load;
        }
    }

    @Override
    public void saveArchiveEdiVendorBill(CarrierInvoiceDetailsEntity vendorBill) {
            vendorBill.setStatus(Status.INACTIVE);
            carrierInvoiceDao.saveOrUpdate(vendorBill);
    }

    @Override
    public boolean isReject(String ediAccount, String scac) {
        if (ediAccount == null) {
            return false;
        }

        return carrierDao.rejectEdiForCustomer(scac, ediAccount);
    }

    private LoadEntity createLoadFromVendorBill(CarrierInvoiceDetailsEntity carrierInvoiceDetails) throws ApplicationException {
        CustomerEntity customer = customerDao.findCustomerByEDINumber(carrierInvoiceDetails.getEdiAccount());
        if (customer != null && BooleanUtils.isTrue(customer.getCreateOrdersFromVendorBills())) {
            LoadEntity load = salesOrderService.createNewOrder(carrierInvoiceDetails, customer);
            if (validateLoadBeforeSaving(load)) {
                shipmentService.updateTimeZoneInfo(load);
                load = loadDao.saveOrUpdate(load);
                generateShipmentDocuments(load, customer);
                return load;
            }
        }
        return null;
    }

    private void generateShipmentDocuments(LoadEntity load, CustomerEntity customer) {
        Long userId = ediUserId;

        if (customer.getDefaultLocations() != null && !customer.getDefaultLocations().isEmpty()) {
            AccountExecutiveEntity acExec = customer.getDefaultLocations().stream().findFirst().get()
                    .getActiveAccountExecutive();
            if (acExec != null && acExec.getUser() != null) {
                userId = acExec.getUser().getId();
            }
            shipmentDocumentService.generateShipmentDocumentsSafe(Collections.emptySet(), load, false, userId);
        }
    }

    private boolean validateLoadBeforeSaving(LoadEntity load) {
        if (load.getLocationId() == null) {
            LOGGER.error("Load can't be created from EDI because of missing location");
            return false;
        }
        if (load.getBillToId() == null) {
            LOGGER.error("Load can't be created from EDI because of missing bill to");
            return false;
        }
        if (!shipmentService.checkBillToCreditLimitSafe(load)) {
            LOGGER.error("Load can't be created from EDI because of Credit Limit");
            return false;
        }
        return true;
    }

    private boolean isDuplicateInvoice(LoadEntity load, CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        return isInvoiced(load)
                && load.getVendorBillDetails() != null
                && ObjectUtils.compare(load.getVendorBillDetails().getFrtBillAmount(),
                        carrierInvoiceDetails.getTotalCharges()) == 0;
    }

    private boolean isInvoiced(LoadEntity load) {
        return load != null && load.getFinalizationStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE;
    }

    private void createCostItemsFromLineItems(CarrierInvoiceDetailsEntity entity, Map<String, String> carrierRefTypeMap)
            throws ApplicationException {
        Map<String, CarrierInvoiceCostItemEntity> carrierCostItems = new HashMap<String, CarrierInvoiceCostItemEntity>();
        for (CarrierInvoiceLineItemEntity lineItemEntity : entity.getCarrierInvoiceLineItems()) {
            if (lineItemEntity.getCharge() != null) {
                String accRefType = carrierRefTypeMap.containsKey(lineItemEntity.getSpecialChargeCode()) ? carrierRefTypeMap.get(lineItemEntity
                        .getSpecialChargeCode()) : CarrierEdiCostTypesEntity.DEFAULT_ACC_TYPE;
                CarrierInvoiceCostItemEntity costItemEntity;
                if (carrierCostItems.containsKey(accRefType)) {
                    costItemEntity = carrierCostItems.get(accRefType);
                } else {
                    costItemEntity = new CarrierInvoiceCostItemEntity();
                    costItemEntity.setAccessorialType(accRefType);
                    costItemEntity.setCarrierInvoiceDetails(entity);
                    costItemEntity.setSubtotal(BigDecimal.ZERO);
                    carrierCostItems.put(accRefType, costItemEntity);
                }
                costItemEntity.setSubtotal(costItemEntity.getSubtotal().add(lineItemEntity.getCharge()));
            }
        }
        entity.setCarrierInvoiceCostItems(new HashSet<CarrierInvoiceCostItemEntity>(carrierCostItems.values()));
        validateTotalCostWithParsedItems(entity.getTotalCharges(), entity.getCarrierInvoiceCostItems());
    }

    private void validateTotalCostWithParsedItems(BigDecimal totalCharges, Set<CarrierInvoiceCostItemEntity> carrierInvoiceCostItems)
            throws ApplicationException {
        BigDecimal lineItemsCharges = BigDecimal.ZERO;
        for (CarrierInvoiceCostItemEntity costItem : carrierInvoiceCostItems) {
            if (costItem.getSubtotal() != null) {
                lineItemsCharges = lineItemsCharges.add(costItem.getSubtotal());
            }
        }
        if (ObjectUtils.compare(lineItemsCharges, totalCharges) != 0) {
            String errorMsg = String.format("Total charge of invoice line items %.2f is not equal to total invoice charges %.2f",
                    lineItemsCharges, totalCharges);
            throw new ApplicationException(errorMsg);
        }
    }
}
