/**
 * 
 */
package com.pls.shipment.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.dao.CustomerDao;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.FreightBillPayToService;
import com.pls.ltlrating.service.LtlRatingEngineService;
import com.pls.ltlrating.shared.LtlPricingAccessorialResult;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceCostItemEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.ShipmentService;

/**
 * Test for {@link SalesOrderServiceImpl}.
 * 
 * @author Alexander Nalapko
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SalesOrderServiceImplTest {

    @InjectMocks
    private SalesOrderServiceImpl sut;

    @Mock
    private CarrierInvoiceService carrierInvoiceService;

    @Mock
    private FreightBillPayToService freightBillPayToService;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private UserAddressBookDao userAddressBookDao;

    @Mock
    private LtlRatingEngineService ratingService;

    private static final String EDI_NUMBER = "edi" + Math.random();
    private static final Long ID_NUMBER = (long) (Math.random() * 100);
    private static final Long CUSTOMER_ID = (long) (Math.random() * 100);
    private static final Long CARRIER_ID = (long) (Math.random() * 100);

    @Test
    public void createNewOrderTest() throws Exception {
        CarrierInvoiceDetailsEntity detailsEntity = getCarrierInvoiceDetailsEntity();
        when(carrierInvoiceService.getById(ID_NUMBER)).thenReturn(detailsEntity);

        CustomerEntity customer = getCustomer();
        when(customerDao.get(CUSTOMER_ID)).thenReturn(customer);

        when(customerDao.isActiveCustomer(CUSTOMER_ID)).thenReturn(true);

        LtlPricingResult pricingResult = getPricingResult();
        when(ratingService.getRatesSafe(Mockito.any())).thenReturn(Arrays.asList(pricingResult));

        LoadEntity load = sut.createNewOrder(ID_NUMBER, CUSTOMER_ID);
        assertNotNull(load);

        assertNotNull(load.getCarrier());
        assertNotNull(load.getCarrier().getId());
        assertEquals(load.getCarrier().getId(), detailsEntity.getCarrier().getId());

        assertNotNull(load.getOrganization());
        assertNotNull(load.getOrganization().getId());
        assertEquals(load.getOrganization().getId(), customer.getId());
        assertNotNull(load.getOrganization().getDefaultLocations());
        assertNotNull(load.getOrganization().getDefaultBillTo());

        assertEquals(load.getCostDetails().size(), detailsEntity.getCarrierInvoiceCostItems().size());

        assertNotNull(load.getOrigin());
        assertEquals(detailsEntity.getOriginAddress().getCity(), load.getOrigin().getAddress().getCity());
        assertNotNull(load.getOrigin().getAddress().getCountry());
        assertEquals(detailsEntity.getOriginAddress().getCountryCode(), load.getOrigin().getAddress().getCountry().getId());
        assertEquals(detailsEntity.getOriginAddress().getState(), load.getOrigin().getAddress().getStateCode());
        assertEquals(2, load.getOrigin().getLoadMaterials().size());

        assertNotNull(load.getDestination());
        assertEquals(detailsEntity.getDestinationAddress().getCity(), load.getDestination().getAddress().getCity());
        assertNotNull(load.getDestination().getAddress().getCountry());
        assertEquals(detailsEntity.getDestinationAddress().getCountryCode(), load.getDestination().getAddress().getCountry().getId());
        assertEquals(detailsEntity.getDestinationAddress().getState(), load.getDestination().getAddress().getStateCode());

        assertEquals(load.getPayTerms(), detailsEntity.getPaymentTerms());

        assertEquals(pricingResult.getTransitTime() * 24 * 60, load.getTravelTime().intValue());
        assertEquals(pricingResult.getTotalMiles(), load.getMileage());
        assertEquals(pricingResult.getTransitDate(), load.getDestination().getEarlyScheduledArrival());
        assertEquals(pricingResult.getTransitDate(), load.getDestination().getScheduledArrival());

        LoadCostDetailsEntity costDetail = load.getActiveCostDetail();
        assertNotNull(costDetail);
        assertEquals(pricingResult.getProfileId(), costDetail.getPricingProfileDetailId());
        assertEquals(pricingResult.getServiceType(), costDetail.getServiceType());
        assertEquals(pricingResult.getNewProdLiability(), costDetail.getNewLiability());
        assertEquals(pricingResult.getUsedProdLiability(), costDetail.getUsedLiability());
        assertEquals(pricingResult.getProhibitedCommodities(), costDetail.getProhibitedCommodities());
        assertEquals(pricingResult.getBolCarrierName(), costDetail.getGuaranteedNameForBOL());
        assertEquals(pricingResult.getTotalCarrierCost(), costDetail.getTotalCost());
        assertEquals(pricingResult.getTotalShipperCost(), costDetail.getTotalRevenue());
        assertEquals(12, costDetail.getCostDetailItems().size());
    }

    @Test(expected = ApplicationException.class)
    public void createNewOrderWithUserAddressBookTest() throws ApplicationException {
        CarrierInvoiceDetailsEntity detailsEntity = getCarrierInvoiceDetailsEntity();
        when(carrierInvoiceService.getById(ID_NUMBER)).thenReturn(detailsEntity);

        CustomerEntity customer = getCustomer();
        when(customerDao.get(CUSTOMER_ID)).thenReturn(customer);

        when(customerDao.isActiveCustomer(CUSTOMER_ID)).thenReturn(false);

        sut.createNewOrder(ID_NUMBER, CUSTOMER_ID);
    }

    @Test
    public void createNewOrderWithoutActiveCustomerTest() throws ApplicationException {
        CarrierInvoiceDetailsEntity detailsEntity = getCarrierInvoiceDetailsEntity();
        when(carrierInvoiceService.getById(ID_NUMBER)).thenReturn(detailsEntity);

        CustomerEntity customer = getCustomer();
        when(customerDao.get(CUSTOMER_ID)).thenReturn(customer);

        when(customerDao.isActiveCustomer(CUSTOMER_ID)).thenReturn(true);

        List<UserAddressBookEntity> userAdressesBook = new ArrayList<UserAddressBookEntity>();
        userAdressesBook.add(new UserAddressBookEntity());
        when(userAddressBookDao.searchUserAddressBookEntries(null, null, detailsEntity.getOriginAddress().getCity(), null,
                    detailsEntity.getOriginAddress().getCountryCode(), detailsEntity.getOriginAddress().getState(), CUSTOMER_ID))
                .thenReturn(userAdressesBook);

        when(userAddressBookDao.searchUserAddressBookEntries(null, null, detailsEntity.getDestinationAddress().getCity(), null,
                    detailsEntity.getDestinationAddress().getCountryCode(), detailsEntity.getDestinationAddress().getState(), CUSTOMER_ID))
                .thenReturn(userAdressesBook);

        LoadEntity load = sut.createNewOrder(ID_NUMBER, CUSTOMER_ID);
        assertNotNull(load);
        assertNotNull(load.getLoadDetails());
        assertEquals(load.getLoadDetails().size(), 2);
    }

    @Test
    public void shouldCreateNewOrderWithoutCustomer() throws Exception {
        CarrierInvoiceDetailsEntity detailsEntity = getCarrierInvoiceDetailsEntity();
        when(carrierInvoiceService.getById(ID_NUMBER)).thenReturn(detailsEntity);

        LtlPricingResult pricingResult = getPricingResult();
        when(ratingService.getRates(Mockito.any())).thenReturn(Arrays.asList(pricingResult));

        LoadEntity load = sut.createNewOrder(ID_NUMBER, CUSTOMER_ID);
        assertNotNull(load);

        assertNotNull(load.getCarrier());
        assertNotNull(load.getCarrier().getId());
        assertEquals(load.getCarrier().getId(), detailsEntity.getCarrier().getId());

        assertNull(load.getOrganization());

        assertEquals(load.getCostDetails().size(), detailsEntity.getCarrierInvoiceCostItems().size());

        assertNotNull(load.getOrigin());
        assertEquals(detailsEntity.getOriginAddress().getCity(), load.getOrigin().getAddress().getCity());
        assertNotNull(load.getOrigin().getAddress().getCountry());
        assertEquals(detailsEntity.getOriginAddress().getCountryCode(), load.getOrigin().getAddress().getCountry().getId());
        assertEquals(detailsEntity.getOriginAddress().getState(), load.getOrigin().getAddress().getStateCode());
        assertEquals(2, load.getOrigin().getLoadMaterials().size());

        assertNotNull(load.getDestination());
        assertEquals(detailsEntity.getDestinationAddress().getCity(), load.getDestination().getAddress().getCity());
        assertNotNull(load.getDestination().getAddress().getCountry());
        assertEquals(detailsEntity.getDestinationAddress().getCountryCode(), load.getDestination().getAddress().getCountry().getId());
        assertEquals(detailsEntity.getDestinationAddress().getState(), load.getDestination().getAddress().getStateCode());

        assertEquals(load.getPayTerms(), detailsEntity.getPaymentTerms());

        assertEquals(0, load.getTravelTime().intValue());
        assertEquals(new Integer(0), load.getMileage());
        Calendar actualPickupDate = Calendar.getInstance();
        actualPickupDate.setTime(detailsEntity.getActualPickupDate());
        actualPickupDate.set(Calendar.HOUR, 0);
        actualPickupDate.set(Calendar.MINUTE, 0);
        actualPickupDate.set(Calendar.SECOND, 0);
        actualPickupDate.set(Calendar.MILLISECOND, 0);
        actualPickupDate.set(Calendar.AM_PM, Calendar.AM);
        assertEquals(actualPickupDate.getTime(), load.getOrigin().getEarlyScheduledArrival());
        actualPickupDate.set(Calendar.HOUR, 11);
        actualPickupDate.set(Calendar.MINUTE, 30);
        actualPickupDate.set(Calendar.AM_PM, Calendar.PM);
        assertEquals(actualPickupDate.getTime(), load.getOrigin().getScheduledArrival());

        LoadCostDetailsEntity costDetail = load.getActiveCostDetail();
        assertNotNull(costDetail);
        assertNull(costDetail.getPricingProfileDetailId());
        assertNull(costDetail.getServiceType());
        assertNull(costDetail.getNewLiability());
        assertNull(costDetail.getUsedLiability());
        assertNull(costDetail.getProhibitedCommodities());
        assertNull(costDetail.getGuaranteedNameForBOL());
        assertEquals(BigDecimal.ZERO, costDetail.getTotalCost());
        assertEquals(BigDecimal.ZERO, costDetail.getTotalRevenue());
        assertEquals(0, costDetail.getCostDetailItems().size());
    }

    private CustomerEntity getCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(CUSTOMER_ID);
        Set<OrganizationLocationEntity> location = new HashSet<OrganizationLocationEntity>();
        location.add(new OrganizationLocationEntity());
        customer.setDefaultLocations(location);
        Set<BillToEntity> billTo = new HashSet<BillToEntity>();
        billTo.add(new BillToEntity());
        customer.setDefaultBillTo(billTo);
        return customer;
    }

    private CarrierInvoiceDetailsEntity getCarrierInvoiceDetailsEntity() {
        CarrierInvoiceDetailsEntity bo = new CarrierInvoiceDetailsEntity();
        Set<CarrierInvoiceCostItemEntity> carrierInvoiceCostItems = new HashSet<CarrierInvoiceCostItemEntity>();
        CarrierInvoiceCostItemEntity item = new CarrierInvoiceCostItemEntity();
        item.setSubtotal(new BigDecimal(Math.random() + 1));
        carrierInvoiceCostItems.add(item);
        bo.setCarrierInvoiceCostItems(carrierInvoiceCostItems);
        bo.setCarrierInvoiceLineItems(new HashSet<CarrierInvoiceLineItemEntity>());
        bo.getCarrierInvoiceLineItems().add(getLineItem());
        CarrierInvoiceLineItemEntity lineItem = getLineItem();
        lineItem.setCharge(new BigDecimal(Math.random() + 1));
        lineItem.setWeight(null);
        bo.getCarrierInvoiceLineItems().add(lineItem);
        bo.getCarrierInvoiceLineItems().add(getLineItem());
        bo.setEdiAccount(EDI_NUMBER);
        CarrierEntity carrier = new CarrierEntity();
        carrier.setId(CARRIER_ID);
        bo.setCarrier(carrier);
        bo.setOriginAddress(getCarrierInvoiceAddressDetailsEntity());
        bo.setDestinationAddress(getCarrierInvoiceAddressDetailsEntity());
        bo.setDeliveryDate(new Date());
        bo.setActualPickupDate(new Date());
        bo.setPaymentTerms(PaymentTerms.PREPAID);
        return bo;
    }

    private CarrierInvoiceLineItemEntity getLineItem() {
        CarrierInvoiceLineItemEntity item = new CarrierInvoiceLineItemEntity();
        item.setWeight(new BigDecimal(Math.random() + 1));
        item.setCommodityClass(CommodityClass.values()[(int) ((CommodityClass.values().length - 1) * Math.random())]);
        item.setDescription("description" + Math.random());
        item.setNmfc("nmfc" + Math.random());
        item.setPackagingCode("packagingCode" + Math.random());
        item.setQuantity((int) (Math.random() * 100));
        return item;
    }

    private CarrierInvoiceAddressDetailsEntity getCarrierInvoiceAddressDetailsEntity() {
        CarrierInvoiceAddressDetailsEntity bo = new CarrierInvoiceAddressDetailsEntity();
        bo.setCity("CITY" + Math.random());
        bo.setCountryCode("USA");
        bo.setState("SC" + Math.random());
        bo.setPostalCode(String.valueOf(Math.random()).replaceAll("[^\\d]", ""));
        return bo;
    }

    private LtlPricingResult getPricingResult() {
        LtlPricingResult result = new LtlPricingResult();
        result.setTransitTime((int) (Math.random() * 10 + 1));
        result.setTotalMiles((int) (Math.random() * 100));
        result.setTransitDate(DateUtility.addDays(new Date(), -3));
        result.setProfileId((long) (Math.random() * 100));
        result.setServiceType(LtlServiceType.DIRECT.name());
        result.setNewProdLiability(new BigDecimal(Math.random() + 1));
        result.setUsedProdLiability(new BigDecimal(Math.random() + 1));
        result.setProhibitedCommodities("probibitedCommodities" + Math.random());
        result.setBolCarrierName("bolCarrierName" + Math.random());
        result.setTotalCarrierCost(new BigDecimal(Math.random() + 1));
        result.setTotalShipperCost(new BigDecimal(Math.random() + 1));

        result.setCarrierFinalLinehaul(new BigDecimal(Math.random() + 1));
        result.setCarrierFuelSurcharge(new BigDecimal(Math.random() + 1));
        result.setShipperFinalLinehaul(new BigDecimal(Math.random() + 1));
        result.setShipperFuelSurcharge(new BigDecimal(Math.random() + 1));
        result.setBenchmarkFinalLinehaul(new BigDecimal(Math.random() + 1));
        result.setBenchmarkFuelSurcharge(new BigDecimal(Math.random() + 1));

        LtlPricingAccessorialResult accessorial = new LtlPricingAccessorialResult();
        accessorial.setAccessorialType("accessorialType" + Math.random());
        accessorial.setShipperAccessorialCost(new BigDecimal(Math.random() + 1));
        accessorial.setCarrierAccessorialCost(new BigDecimal(Math.random() + 1));
        accessorial.setBenchmarkAccessorialCost(new BigDecimal(Math.random() + 1));
        LtlPricingAccessorialResult accessorial2 = new LtlPricingAccessorialResult();
        accessorial2.setAccessorialType("accessorialType" + Math.random());
        accessorial2.setShipperAccessorialCost(new BigDecimal(Math.random() + 1));
        accessorial2.setCarrierAccessorialCost(new BigDecimal(Math.random() + 1));
        accessorial2.setBenchmarkAccessorialCost(new BigDecimal(Math.random() + 1));
        result.setAccessorials(Arrays.asList(accessorial, accessorial2));
        return result;
    }

//    @Test
//    public void test1() throws ApplicationException{
//        CarrierInvoiceDetailsEntity detailsEntity = getCarrierInvoiceDetailsEntity();
//        when(carrierInvoiceService.getById(ID_NUMBER)).thenReturn(detailsEntity);
//
//        CustomerEntity customer = getCustomer();
//        when(customerDao.get(CUSTOMER_ID)).thenReturn(customer);
//
//        when(customerDao.isActiveCustomer(CUSTOMER_ID)).thenReturn(true);
//
//        LtlPricingResult pricingResult = getPricingResult();
//        when(ratingService.getRatesSafe(Mockito.any())).thenReturn(Arrays.asList(pricingResult));
//
//        LoadEntity load = sut.createNewOrder(ID_NUMBER, CUSTOMER_ID);
//
//        Set<BillToRequiredFieldEntity> billToRequiredFields = new HashSet<BillToRequiredFieldEntity>();
//        for (int i = 0; i < 100; i++) {
//            billToRequiredFields.add(generateBillToRequiredFieldEntity(i));
//        }
//        load.getBillTo().setBillToRequiredField(billToRequiredFields);
//
//        Map<BillToRequiredField, BillToRequiredFieldEntity> map = sut.getMatchedRules(load);
//        assertNotNull(map);
//
//    }
//
//    private BillToRequiredFieldEntity generateBillToRequiredFieldEntity(int i){
//        BillToRequiredFieldEntity entity = new BillToRequiredFieldEntity();
//        entity.setCity("CITY" + Math.random());
//        entity.setState("SC" + Math.random());
//        entity.setZip(String.valueOf(Math.random()).replaceAll("[^\\d]", ""));
//        entity.setCountry("CC" + Math.random());
//        return entity;
//    }


}
