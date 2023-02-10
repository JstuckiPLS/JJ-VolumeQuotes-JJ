package com.pls.shipment.service.impl.edi.match;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.CarrierInvoiceDetailsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.CarrierEdiCostTypesEntity;
import com.pls.shipment.domain.CarrierInvoiceCostItemEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.enums.ShipmentSourceIndicator;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.SalesOrderService;
import com.pls.shipment.service.ShipmentAlertService;
import com.pls.shipment.service.ShipmentService;

/**
 * Test for {@link VendorBillEdiSaverImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class VendorBillEdiSaverImplTest {

    @Mock
    private VendorBillToLoadMatcherImpl loadMatcher;

    @Mock
    private SalesOrderService salesOrderService;

    @Mock
    private CarrierInvoiceService carrierInvoiceService;

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private CarrierInvoiceDetailsDao carrierInvoiceDao;

    @Mock
    private LtlShipmentDao loadDao;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private ShipmentAlertService shipmentAlertService;

    @InjectMocks
    private VendorBillEdiSaverImpl sut;


    @Test
    public void shouldMatchWithLoadAndSave() throws ApplicationException {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();
        LoadEntity load = getLoad();

        Mockito.when(loadMatcher.findMatchingLoad(vendorBill)).thenReturn(load);

        LoadEntity matchedLoad = sut.saveEdiVendorBill(vendorBill, new HashMap<String, String>());

        Assert.assertSame(load, matchedLoad);
        Assert.assertSame(load.getId(), vendorBill.getMatchedLoadId());
        Assert.assertEquals(ShipmentSourceIndicator.SYS.name(), load.getSourceInd());
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());

        Mockito.verify(carrierInvoiceService).saveVendorBillWithMatchedLoad(vendorBill, null);
        Mockito.verifyNoMoreInteractions(carrierInvoiceDao);
    }

    @Test
    public void shouldCreateCostItems() throws ApplicationException {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();
        addLineItem(vendorBill, "refType1", new BigDecimal("11.35"));
        addLineItem(vendorBill, "refType2", new BigDecimal("8.12"));
        addLineItem(vendorBill, "refType3", new BigDecimal("9"));
        addLineItem(vendorBill, "refType1", new BigDecimal("7.2"));
        addLineItem(vendorBill, "refType2", new BigDecimal("1.02"));
        vendorBill.setTotalCharges(new BigDecimal("36.69"));
        LoadEntity load = getLoad();
        addLoadCost(load, new BigDecimal("30"));

        HashMap<String, String> carrierRefTypeMap = new HashMap<String, String>();
        carrierRefTypeMap.put("refType1", "loadRefType1");
        carrierRefTypeMap.put("refType3", "loadRefType3");

        Mockito.when(loadMatcher.findMatchingLoad(vendorBill)).thenReturn(load);

        LoadEntity matchedLoad = sut.saveEdiVendorBill(vendorBill, carrierRefTypeMap);

        Assert.assertSame(load, matchedLoad);
        Assert.assertSame(load.getId(), vendorBill.getMatchedLoadId());
        Assert.assertEquals(ShipmentSourceIndicator.SYS.name(), load.getSourceInd());
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());

        Assert.assertEquals(3, vendorBill.getCarrierInvoiceCostItems().size());
        CarrierInvoiceCostItemEntity costItem = findCostItem(vendorBill, "loadRefType1");
        Assert.assertNotNull(costItem);
        Assert.assertEquals(new BigDecimal("18.55"), costItem.getSubtotal());
        costItem = findCostItem(vendorBill, CarrierEdiCostTypesEntity.DEFAULT_ACC_TYPE);
        Assert.assertNotNull(costItem);
        Assert.assertEquals(new BigDecimal("9.14"), costItem.getSubtotal());
        costItem = findCostItem(vendorBill, "loadRefType3");
        Assert.assertNotNull(costItem);
        Assert.assertEquals(new BigDecimal("9"), costItem.getSubtotal());

        Mockito.verify(carrierInvoiceService).saveVendorBillWithMatchedLoad(vendorBill, null);
        Mockito.verifyNoMoreInteractions(carrierInvoiceDao);
    }

    @Test
    public void shouldCheckForDuplicateInvoice() throws ApplicationException {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();
        addLineItem(vendorBill, "refType", new BigDecimal("36.69"));
        vendorBill.setTotalCharges(new BigDecimal("36.690"));
        LoadEntity load = getLoad();
        addLoadCost(load, new BigDecimal("36.69000"));
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);

        Mockito.when(loadMatcher.findMatchingLoad(vendorBill)).thenReturn(load);

        LoadEntity matchedLoad = sut.saveEdiVendorBill(vendorBill, new HashMap<String, String>());

        Assert.assertNull(matchedLoad);
        Assert.assertNull(vendorBill.getMatchedLoadId());
        Assert.assertEquals(ShipmentSourceIndicator.SYS.name(), load.getSourceInd());
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());

        Mockito.verify(carrierInvoiceDao).saveOrUpdate(vendorBill);
        Mockito.verifyNoMoreInteractions(carrierInvoiceDao);
    }

    @Test
    public void shouldCheckForDuplicateInvoiceWithDifferentCost() throws ApplicationException {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();
        addLineItem(vendorBill, "refType", new BigDecimal("36.69"));
        vendorBill.setTotalCharges(new BigDecimal("36.690"));
        LoadEntity load = getLoad();
        addLoadCost(load, new BigDecimal("36.68"));
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);

        Mockito.when(loadMatcher.findMatchingLoad(vendorBill)).thenReturn(load);

        LoadEntity matchedLoad = sut.saveEdiVendorBill(vendorBill, new HashMap<String, String>());

        Assert.assertNull(matchedLoad);
        Assert.assertNull(vendorBill.getMatchedLoadId());
        Assert.assertEquals(ShipmentSourceIndicator.SYS.name(), load.getSourceInd());
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());

        Mockito.verify(carrierInvoiceDao).saveOrUpdate(vendorBill);
        Mockito.verifyNoMoreInteractions(carrierInvoiceDao);
    }

    @Test
    public void shouldCreateLoadAndSave() throws ApplicationException {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();
        LoadEntity load = getLoad();
        load.setSourceInd(ShipmentSourceIndicator.EDI.name());
        CustomerEntity customer = new CustomerEntity();
        customer.setCreateOrdersFromVendorBills(true);
        Mockito.when(customerDao.findCustomerByEDINumber(vendorBill.getEdiAccount())).thenReturn(customer);
        Mockito.when(salesOrderService.createNewOrder(vendorBill, customer)).thenReturn(load);
        Mockito.when(loadDao.saveOrUpdate(load)).thenReturn(load);
        Mockito.when(shipmentService.checkBillToCreditLimitSafe(load)).thenReturn(true);

        LoadEntity matchedLoad = sut.saveEdiVendorBill(vendorBill, new HashMap<String, String>());

        Assert.assertSame(load, matchedLoad);
        Assert.assertSame(load.getId(), vendorBill.getMatchedLoadId());
        Assert.assertEquals(ShipmentSourceIndicator.EDI.name(), load.getSourceInd());
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());

        Mockito.verify(carrierInvoiceService).saveVendorBillWithMatchedLoad(vendorBill, null);
        Mockito.verifyNoMoreInteractions(carrierInvoiceDao);
    }

    @Test
    public void shouldNotSaveLoadWithMissingLocation() throws ApplicationException {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();
        LoadEntity load = getLoad();
        load.setLocationId(null);

        CustomerEntity customer = new CustomerEntity();
        customer.setCreateOrdersFromVendorBills(true);
        Mockito.when(customerDao.findCustomerByEDINumber(vendorBill.getEdiAccount())).thenReturn(customer);
        Mockito.when(salesOrderService.createNewOrder(vendorBill, customer)).thenReturn(load);
        Mockito.when(loadDao.saveOrUpdate(load)).thenReturn(load);
        Mockito.when(shipmentService.checkBillToCreditLimitSafe(load)).thenReturn(true);

        LoadEntity matchedLoad = sut.saveEdiVendorBill(vendorBill, new HashMap<String, String>());

        Assert.assertNull(matchedLoad);
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());

        Mockito.verifyNoMoreInteractions(carrierInvoiceService);
        Mockito.verify(carrierInvoiceDao).saveOrUpdate(vendorBill);
        Mockito.verifyNoMoreInteractions(carrierInvoiceDao);
    }

    @Test
    public void shouldNotSaveLoadWithCreditLimit() throws ApplicationException {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();
        LoadEntity load = getLoad();

        CustomerEntity customer = new CustomerEntity();
        customer.setCreateOrdersFromVendorBills(true);
        Mockito.when(customerDao.findCustomerByEDINumber(vendorBill.getEdiAccount())).thenReturn(customer);
        Mockito.when(salesOrderService.createNewOrder(vendorBill, customer)).thenReturn(load);
        Mockito.when(loadDao.saveOrUpdate(load)).thenReturn(load);

        LoadEntity matchedLoad = sut.saveEdiVendorBill(vendorBill, new HashMap<String, String>());

        Assert.assertNull(matchedLoad);
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());

        Mockito.verifyNoMoreInteractions(carrierInvoiceService);
        Mockito.verify(carrierInvoiceDao).saveOrUpdate(vendorBill);
        Mockito.verifyNoMoreInteractions(carrierInvoiceDao);
    }

    @Test
    public void shouldNotCreateLoadWhenNoCustomerFound() throws ApplicationException {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        LoadEntity matchedLoad = sut.saveEdiVendorBill(vendorBill, new HashMap<String, String>());

        Assert.assertNull(matchedLoad);
        Assert.assertNull(vendorBill.getMatchedLoadId());
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());

        Mockito.verify(carrierInvoiceDao).saveOrUpdate(vendorBill);
        Mockito.verifyNoMoreInteractions(carrierInvoiceDao);
    }

    @Test
    public void shouldNotCreateLoadWhenCustomerFlagIsNotSet() throws ApplicationException {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        CustomerEntity customer = new CustomerEntity();
        Mockito.when(customerDao.findCustomerByEDINumber(vendorBill.getEdiAccount())).thenReturn(customer);

        LoadEntity matchedLoad = sut.saveEdiVendorBill(vendorBill, new HashMap<String, String>());

        Assert.assertNull(matchedLoad);
        Assert.assertNull(vendorBill.getMatchedLoadId());
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());

        Mockito.verify(carrierInvoiceDao).saveOrUpdate(vendorBill);
        Mockito.verifyNoMoreInteractions(carrierInvoiceDao);
    }

    private void addLoadCost(LoadEntity load, BigDecimal totalCost) {
        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setTotalCost(totalCost);
        costDetails.setStatus(Status.ACTIVE);
        load.getActiveCostDetails().add(costDetails);
    }

    private CarrierInvoiceCostItemEntity findCostItem(CarrierInvoiceDetailsEntity vendorBill, String refType) {
        for (CarrierInvoiceCostItemEntity item : vendorBill.getCarrierInvoiceCostItems()) {
            if (StringUtils.equals(item.getAccessorialType(), refType)) {
                return item;
            }
        }
        return null;
    }

    private void addLineItem(CarrierInvoiceDetailsEntity vendorBill, String refType, BigDecimal cost) {
        CarrierInvoiceLineItemEntity item = new CarrierInvoiceLineItemEntity();
        item.setSpecialChargeCode(refType);
        item.setCharge(cost);
        vendorBill.getCarrierInvoiceLineItems().add(item);
    }

    private LoadEntity getLoad() {
        LoadEntity load = new LoadEntity();
        load.setId((long) (Math.random() * 100));
        load.setSourceInd(ShipmentSourceIndicator.SYS.name());
        load.setActiveCostDetails(new HashSet<LoadCostDetailsEntity>());
        load.setBillToId((long) (Math.random() * 100));
        load.setLocationId((long) (Math.random() * 100));
        return load;
    }

    private CarrierInvoiceDetailsEntity getVendorBill() {
        CarrierInvoiceDetailsEntity vendorBill = new CarrierInvoiceDetailsEntity();
        vendorBill.setTotalCharges(BigDecimal.ZERO);
        vendorBill.setStatus(Status.ACTIVE);
        vendorBill.setCarrierInvoiceLineItems(new HashSet<CarrierInvoiceLineItemEntity>());
        vendorBill.setEdiAccount("ediAccount" + Math.random());
        return vendorBill;
    }
}
