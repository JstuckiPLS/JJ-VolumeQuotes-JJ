package com.pls.shipment.service.impl.edi.match;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Test for {@link VendorBillToLoadMatcherImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class VendorBillToLoadMatcherImplTest {

    @Mock
    private LtlShipmentDao ltlShipmentDao;

    @InjectMocks
    private VendorBillToLoadMatcherImpl sut;

    @Test
    public void shouldFindMatchingLoadByPro() {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        List<LoadEntity> loads = Arrays.asList(Mockito.mock(LoadEntity.class));
        Mockito.when(ltlShipmentDao.findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber()))
                .thenReturn(loads);

        LoadEntity matchingLoad = sut.findMatchingLoad(vendorBill);
        Assert.assertSame(loads.get(0), matchingLoad);

        Mockito.verify(ltlShipmentDao).findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
    }

    @Test
    public void shouldFindMatchingLoadByBolAndZip() {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        List<LoadEntity> loads = Arrays.asList(Mockito.mock(LoadEntity.class));
        Mockito.when(ltlShipmentDao.findShipmentByScacAndBolNumberAndZip(vendorBill.getBolNumber(), vendorBill.getCarrier().getScac(),
                vendorBill.getOriginAddress().getPostalCode(), vendorBill.getDestinationAddress().getPostalCode())).thenReturn(loads);

        LoadEntity matchingLoad = sut.findMatchingLoad(vendorBill);
        Assert.assertSame(loads.get(0), matchingLoad);

        Mockito.verify(ltlShipmentDao).findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        Mockito.verify(ltlShipmentDao).findShipmentByScacAndBolNumberAndZip(vendorBill.getBolNumber(), vendorBill.getCarrier().getScac(),
                vendorBill.getOriginAddress().getPostalCode(), vendorBill.getDestinationAddress().getPostalCode());
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
    }

    @Test
    public void shouldFindMatchingLoadByBolAddresses() {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        List<LoadEntity> loads = Arrays.asList(Mockito.mock(LoadEntity.class));
        Mockito.when(ltlShipmentDao.findShipmentByScacAndBolNumberAndCityAndState(vendorBill.getBolNumber(), vendorBill.getCarrier().getScac(),
                vendorBill.getOriginAddress().getCity(), vendorBill.getOriginAddress().getState(),
                vendorBill.getDestinationAddress().getCity(), vendorBill.getDestinationAddress().getState())).thenReturn(loads);

        LoadEntity matchingLoad = sut.findMatchingLoad(vendorBill);
        Assert.assertSame(loads.get(0), matchingLoad);

        Mockito.verify(ltlShipmentDao).findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        Mockito.verify(ltlShipmentDao).findShipmentByScacAndBolNumberAndZip(vendorBill.getBolNumber(), vendorBill.getCarrier().getScac(),
                vendorBill.getOriginAddress().getPostalCode(), vendorBill.getDestinationAddress().getPostalCode());
        Mockito.verify(ltlShipmentDao).findShipmentByScacAndBolNumberAndCityAndState(vendorBill.getBolNumber(), vendorBill.getCarrier().getScac(),
                vendorBill.getOriginAddress().getCity(), vendorBill.getOriginAddress().getState(),
                vendorBill.getDestinationAddress().getCity(), vendorBill.getDestinationAddress().getState());
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
    }

    @Test
    public void shouldFindMatchingLoadByProAndFilterByBolNumber() {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        LoadEntity load = new LoadEntity();
        load.getNumbers().setBolNumber(vendorBill.getBolNumber());
        load.setStatus(ShipmentStatus.BOOKED);

        List<LoadEntity> loads = Arrays.asList(load, new LoadEntity());
        Mockito.when(ltlShipmentDao.findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber()))
                .thenReturn(loads);

        LoadEntity matchingLoad = sut.findMatchingLoad(vendorBill);
        Assert.assertSame(load, matchingLoad);

        Mockito.verify(ltlShipmentDao).findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
    }

    @Test
    public void shouldFindMatchingLoadByProAndFilterByZipCodes() {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        LoadEntity load = new LoadEntity();
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        load.addLoadDetails(getLoadDetails(LoadAction.PICKUP, PointType.ORIGIN, vendorBill.getOriginAddress()));
        load.addLoadDetails(getLoadDetails(LoadAction.DELIVERY, PointType.DESTINATION, vendorBill.getDestinationAddress()));

        List<LoadEntity> loads = Arrays.asList(load, new LoadEntity());
        Mockito.when(ltlShipmentDao.findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber()))
                .thenReturn(loads);

        LoadEntity matchingLoad = sut.findMatchingLoad(vendorBill);
        Assert.assertSame(load, matchingLoad);

        Mockito.verify(ltlShipmentDao).findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
    }

    @Test
    public void shouldFindMatchingLoadByProAndFilterByStatus() {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        LoadEntity load = new LoadEntity();
        load.setStatus(ShipmentStatus.IN_TRANSIT);
        LoadEntity load2 = new LoadEntity();
        load2.setStatus(ShipmentStatus.CANCELLED);

        List<LoadEntity> loads = Arrays.asList(load, load2);
        Mockito.when(ltlShipmentDao.findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber()))
                .thenReturn(loads);

        LoadEntity matchingLoad = sut.findMatchingLoad(vendorBill);
        Assert.assertSame(load, matchingLoad);

        Mockito.verify(ltlShipmentDao).findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
    }

    @Test
    public void shouldFindMatchingLoadByProAndFilterByFinStatus() {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        LoadEntity load = new LoadEntity();
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING);

        List<LoadEntity> loads = Arrays.asList(load);
        Mockito.when(ltlShipmentDao.findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber()))
                .thenReturn(loads);

        LoadEntity matchingLoad = sut.findMatchingLoad(vendorBill);
        Assert.assertSame(load, matchingLoad);

        Mockito.verify(ltlShipmentDao).findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
    }

    @Test
    public void shouldFindMatchingLoadByProAndFilterByAllFields() {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        LoadEntity load = new LoadEntity();
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING);
        load.getNumbers().setBolNumber(vendorBill.getBolNumber());
        load.setStatus(ShipmentStatus.DISPATCHED);
        load.addLoadDetails(getLoadDetails(LoadAction.PICKUP, PointType.ORIGIN, vendorBill.getOriginAddress()));
        load.addLoadDetails(getLoadDetails(LoadAction.DELIVERY, PointType.DESTINATION, vendorBill.getDestinationAddress()));
        LoadEntity load2 = new LoadEntity();
        load2.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING);
        load2.getNumbers().setBolNumber(vendorBill.getBolNumber());
        load2.setStatus(ShipmentStatus.BOOKED); // incorrect status
        load2.addLoadDetails(getLoadDetails(LoadAction.PICKUP, PointType.ORIGIN, vendorBill.getOriginAddress()));
        load2.addLoadDetails(getLoadDetails(LoadAction.DELIVERY, PointType.DESTINATION, vendorBill.getDestinationAddress()));
        LoadEntity load3 = new LoadEntity();
        load3.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING);
        load3.getNumbers().setBolNumber(vendorBill.getBolNumber() + "1"); // incorrect BOL
        load3.setStatus(ShipmentStatus.IN_TRANSIT);
        load3.addLoadDetails(getLoadDetails(LoadAction.PICKUP, PointType.ORIGIN, vendorBill.getOriginAddress()));
        load3.addLoadDetails(getLoadDetails(LoadAction.DELIVERY, PointType.DESTINATION, vendorBill.getDestinationAddress()));
        LoadEntity load5 = new LoadEntity();
        load5.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING);
        load5.getNumbers().setBolNumber(vendorBill.getBolNumber());
        load5.setStatus(ShipmentStatus.DISPATCHED);
        load5.addLoadDetails(getLoadDetails(LoadAction.PICKUP, PointType.ORIGIN, vendorBill.getOriginAddress()));
        load5.addLoadDetails(getLoadDetails(LoadAction.DELIVERY, PointType.DESTINATION, vendorBill.getDestinationAddress()));
        load5.getDestination().getAddress().setZip(load5.getDestination().getAddress().getZip() + "1"); // incorrect zip code

        List<LoadEntity> loads = Arrays.asList(load, load2, load3, load5);
        Mockito.when(ltlShipmentDao.findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber()))
                .thenReturn(loads);

        LoadEntity matchingLoad = sut.findMatchingLoad(vendorBill);
        Assert.assertSame(load, matchingLoad);

        Mockito.verify(ltlShipmentDao).findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
    }

    @Test
    public void shouldNotFindMatchingLoadWhenMoreThanOneLoadFound() {
        CarrierInvoiceDetailsEntity vendorBill = getVendorBill();

        LoadEntity load = new LoadEntity();
        load.getNumbers().setBolNumber(vendorBill.getBolNumber());
        LoadEntity load2 = new LoadEntity();
        load2.getNumbers().setBolNumber(vendorBill.getBolNumber());

        List<LoadEntity> loads = Arrays.asList(load, load2);
        Mockito.when(ltlShipmentDao.findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber()))
                .thenReturn(loads);

        LoadEntity matchingLoad = sut.findMatchingLoad(vendorBill);
        Assert.assertNull(matchingLoad);

        Mockito.verify(ltlShipmentDao).findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        Mockito.verifyNoMoreInteractions(ltlShipmentDao);
    }

    private LoadDetailsEntity getLoadDetails(LoadAction loadAction, PointType pointType, CarrierInvoiceAddressDetailsEntity address) {
        LoadDetailsEntity loadDetails = new LoadDetailsEntity(loadAction, pointType);
        loadDetails.setAddress(new AddressEntity());
        loadDetails.getAddress().setZip(address.getPostalCode());
        return loadDetails;
    }

    private CarrierInvoiceDetailsEntity getVendorBill() {
        CarrierInvoiceDetailsEntity vendorBill = new CarrierInvoiceDetailsEntity();
        CarrierEntity carrier = new CarrierEntity();
        carrier.setScac("scac" + Math.random());
        vendorBill.setCarrier(carrier);
        vendorBill.setProNumber("proNumber" + Math.random());
        vendorBill.setBolNumber("bolNumber" + Math.random());
        vendorBill.setOriginAddress(getAddress());
        vendorBill.setDestinationAddress(getAddress());
        return vendorBill;
    }

    private CarrierInvoiceAddressDetailsEntity getAddress() {
        CarrierInvoiceAddressDetailsEntity address = new CarrierInvoiceAddressDetailsEntity();
        address.setPostalCode("postalCode" + Math.random());
        address.setState("state" + Math.random());
        address.setCity("city" + Math.random());
        return address;
    }
}
