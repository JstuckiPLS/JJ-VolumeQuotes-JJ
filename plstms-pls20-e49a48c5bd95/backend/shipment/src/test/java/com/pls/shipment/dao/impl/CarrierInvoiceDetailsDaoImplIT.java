package com.pls.shipment.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.CarrierInvoiceDetailsDao;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.bo.CarrierInvoiceDetailsListItemBO;
import com.pls.shipment.domain.enums.CarrierInvoiceAddressType;

/**
 * Test cases for {@link CarrierInvoiceDetailsDao} class.
 *
 * @author Mikhail Boldinov, 03/10/13
 */
public class CarrierInvoiceDetailsDaoImplIT extends AbstractDaoTest {
    private static final Long EXISTING_CARRIER_INVOICE_DETAILS_ID = 1L;
    private static final Long CARRIER_ID = 58L;
    private static final String INVOICE_NUMBER = "INV#1";
    private static final Date INVOICE_DATE = new Date();
    private static final String REFERENCE_NUMBER = "REF#1";
    private static final PaymentTerms PAYMENT_TERMS = PaymentTerms.COLLECT;
    private static final BigDecimal NET_AMOUNT = new BigDecimal(100.42);
    private static final Date DELIVERY_DATE = new Date();
    private static final Date EST_DELIVERY_DATE = new Date();
    private static final String BOL_NUMBER = "BOL#1";
    private static final String PO_NUMBER = "PO#1";
    private static final String SHIPPER_REF_NUMBER = "SHIP#1";
    private static final String PRO_NUMBER = "PRO#1";
    private static final Date ACTUAL_PICKUP_DATE = new Date();
    private static final BigDecimal TOTAL_WEIGHT = new BigDecimal("123.45");
    private static final BigDecimal TOTAL_CHARGES = new BigDecimal("322.00");
    private static final Integer TOTAL_QUANTITY = 437;
    private static final Long MATCHED_LOAD_ID = 101L;
    private static final Status STATUS = Status.ACTIVE;
    private static final Integer ORDER_NUMBER = 1;
    private static final String DESCRIPTION = "Line Item 1";
    private static final BigDecimal WEIGHT = new BigDecimal("12.34");
    private static final Integer QUANTITY = 120;
    private static final String PACKAGING_CODE = "PC#1";
    private static final String NMFC = "NMFC#1";
    private static final CommodityClass COMMODITY_CLASS = CommodityClass.CLASS_100;
    private static final BigDecimal CHARGE = new BigDecimal("321.00");
    private static final String SPECIAL_CHARGE_CODE = "A1";
    private static final String ORIGIN_ADDRESS_NAME = "Origin Address";
    private static final String ORIGIN_ADDRESS1 = "Origin Address 1";
    private static final String ORIGIN_ADDRESS2 = "Origin Address 2";
    private static final String ORIGIN_CITY = "Washington";
    private static final String ORIGIN_STATE = "DC";
    private static final String ORIGIN_POSTAL_CODE = "20001";
    private static final String ORIGIN_COUNTRY_CODE = "USA";
    private static final String DESTINATION_ADDRESS_NAME = "Destination Address";
    private static final String DESTINATION_ADDRESS1 = "Destination Address 1";
    private static final String DESTINATION_ADDRESS2 = "Destination Address 2";
    private static final String DESTINATION_CITY = "New-York";
    private static final String DESTINATION_STATE = "NY";
    private static final String DESTINATION_POSTAL_CODE = "10001";
    private static final String DESTINATION_COUNTRY_CODE = "USA";

    @Autowired
    private CarrierInvoiceDetailsDao sut;

    @Autowired
    private CarrierDao carrierDao;

    @Test
    public void testPersistCarrierInvoiceDetailsCascade() {
        CarrierInvoiceDetailsEntity expected = buildCarrierInvoiceDetailsEntity();
        Assert.assertNull(expected.getId());
        sut.persist(expected);
        flushAndClearSession();
        Assert.assertNotNull(expected.getId());
        CarrierInvoiceDetailsEntity actual = sut.find(expected.getId());
        Assert.assertNotNull(actual);
        assertCarrierInvoiceDetailsEntity(actual);
    }

    @Test
    public void testUpdateCarrierInvoiceDetailsCascade() {
        CarrierInvoiceDetailsEntity entity = sut.find(EXISTING_CARRIER_INVOICE_DETAILS_ID);
        Assert.assertNotNull(entity);
        Assert.assertNotEquals(BOL_NUMBER, entity.getBolNumber());
        entity.setBolNumber(BOL_NUMBER);

        sut.saveOrUpdate(entity);
        flushAndClearSession();

        CarrierInvoiceDetailsEntity updated = sut.find(EXISTING_CARRIER_INVOICE_DETAILS_ID);
        Assert.assertNotNull(updated);
        Assert.assertEquals(BOL_NUMBER, entity.getBolNumber());
    }

    @Test
    public void testGetUnmatched() {
        List<CarrierInvoiceDetailsListItemBO> actualList = sut.getUnmatched();
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }

    @Test
    public void testGetArchived() {
        CarrierInvoiceDetailsEntity entity = buildCarrierInvoiceDetailsEntity();
        entity.setStatus(Status.INACTIVE);
        sut.persist(entity);
        flushAndClearSession();

        List<CarrierInvoiceDetailsListItemBO> actualList = sut.getArchived();
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }

    @Test
    public void testUpdateStatus() {
        CarrierInvoiceDetailsEntity entity = buildCarrierInvoiceDetailsEntity();
        Assert.assertEquals(Status.ACTIVE, entity.getStatus());
        sut.persist(entity);
        flushAndClearSession();
        Assert.assertNotNull(entity.getId());

        Date modifiedDate = new Date();
        Long modifiedBy = 1L;
        sut.updateStatus(entity.getId(), Status.INACTIVE, modifiedDate, modifiedBy);
        flushAndClearSession();

        CarrierInvoiceDetailsEntity updated = sut.find(entity.getId());
        Assert.assertNotNull(updated);
        Assert.assertEquals(Status.INACTIVE, updated.getStatus());
        assertDatesEqual(modifiedDate, updated.getModification().getModifiedDate());
        Assert.assertEquals(modifiedBy, updated.getModification().getModifiedBy());
    }

    @Test
    public void testUpdateMatched() {
        CarrierInvoiceDetailsEntity entity = buildCarrierInvoiceDetailsEntity();
        Assert.assertNull(entity.getMatchedLoadId());
        Date modifiedDate = new Date();
        Long modifiedBy = 1L;
        entity.setMatchedLoadId(MATCHED_LOAD_ID);
        entity.getModification().setModifiedBy(modifiedBy);
        entity.getModification().setModifiedDate(modifiedDate);
        sut.persist(entity);
        flushAndClearSession();
        Assert.assertNotNull(entity.getId());

        CarrierInvoiceDetailsEntity matched = sut.find(entity.getId());
        Assert.assertNotNull(matched);
        Assert.assertNotNull(matched.getMatchedLoadId());
        Assert.assertEquals(MATCHED_LOAD_ID, matched.getMatchedLoadId());

        matched.setMatchedLoadId(null);
        sut.update(matched);
        flushAndClearSession();

        CarrierInvoiceDetailsEntity unMatched = sut.find(entity.getId());
        Assert.assertNotNull(unMatched);
        Assert.assertNull(unMatched.getMatchedLoadId());
    }

    @Test
    public void testArchiveOldUnmatched() throws EntityNotFoundException {
        CarrierInvoiceDetailsEntity entity = buildCarrierInvoiceDetailsEntity();
        sut.persist(entity);
        flushAndClearSession();
        Long id = entity.getId();
        Assert.assertNotNull(id);

        getSession().createSQLQuery("update CARRIER_INVOICE_DETAILS set DATE_CREATED=current_date-91, date_modified=current_date-20 "
                + "where INVOICE_DET_ID=:id").setParameter("id", id).executeUpdate();

        sut.archiveOldUnmatched();
        flushAndClearSession();

        entity = sut.get(id);
        Assert.assertNotNull(entity);
        Assert.assertEquals(Status.INACTIVE, entity.getStatus());
        Assert.assertEquals(2, entity.getVersion());
        Assert.assertTrue(DateUtils.isSameDay(entity.getModification().getModifiedDate(), new Date()));
    }

    private CarrierInvoiceDetailsEntity buildCarrierInvoiceDetailsEntity() {
        CarrierInvoiceDetailsEntity entity = new CarrierInvoiceDetailsEntity();
        entity.setCarrier(getCarrier());
        entity.setInvoiceNumber(INVOICE_NUMBER);
        entity.setInvoiceDate(INVOICE_DATE);
        entity.setReferenceNumber(REFERENCE_NUMBER);
        entity.setPaymentTerms(PAYMENT_TERMS);
        entity.setNetAmount(NET_AMOUNT);
        entity.setDeliveryDate(DELIVERY_DATE);
        entity.setEstDeliveryDate(EST_DELIVERY_DATE);
        entity.setBolNumber(BOL_NUMBER);
        entity.setPoNumber(PO_NUMBER);
        entity.setShipperRefNumber(SHIPPER_REF_NUMBER);
        entity.setProNumber(PRO_NUMBER);
        entity.setActualPickupDate(ACTUAL_PICKUP_DATE);
        entity.setTotalWeight(TOTAL_WEIGHT);
        entity.setTotalCharges(TOTAL_CHARGES);
        entity.setTotalQuantity(TOTAL_QUANTITY);
        entity.setStatus(STATUS);
        entity.setEdi(Boolean.TRUE);
        Set<CarrierInvoiceLineItemEntity> lineItems = buildLineItems();
        entity.setCarrierInvoiceLineItems(lineItems);
        CarrierInvoiceAddressDetailsEntity originAddress = buildOriginAddress();
        originAddress.setCarrierInvoiceDetails(entity);
        entity.setOriginAddress(originAddress);
        CarrierInvoiceAddressDetailsEntity destinationAddress = buildDestinationAddress();
        destinationAddress.setCarrierInvoiceDetails(entity);
        entity.setDestinationAddress(destinationAddress);
        return entity;
    }

    private CarrierEntity getCarrier() {
        return carrierDao.find(CARRIER_ID);
    }

    private Set<CarrierInvoiceLineItemEntity> buildLineItems() {
        Set<CarrierInvoiceLineItemEntity> lineItems = new HashSet<CarrierInvoiceLineItemEntity>();
        CarrierInvoiceLineItemEntity carrierInvoiceLineItemEntity = new CarrierInvoiceLineItemEntity();
        carrierInvoiceLineItemEntity.setOrderNumber(ORDER_NUMBER);
        carrierInvoiceLineItemEntity.setDescription(DESCRIPTION);
        carrierInvoiceLineItemEntity.setWeight(WEIGHT);
        carrierInvoiceLineItemEntity.setQuantity(QUANTITY);
        carrierInvoiceLineItemEntity.setPackagingCode(PACKAGING_CODE);
        carrierInvoiceLineItemEntity.setNmfc(NMFC);
        carrierInvoiceLineItemEntity.setCommodityClass(COMMODITY_CLASS);
        carrierInvoiceLineItemEntity.setCharge(CHARGE);
        carrierInvoiceLineItemEntity.setSpecialChargeCode(SPECIAL_CHARGE_CODE);
        carrierInvoiceLineItemEntity.setStatus(STATUS);
        lineItems.add(carrierInvoiceLineItemEntity);
        return lineItems;
    }

    private CarrierInvoiceAddressDetailsEntity buildOriginAddress() {
        CarrierInvoiceAddressDetailsEntity originAddress = new CarrierInvoiceAddressDetailsEntity();
        originAddress.setAddressType(CarrierInvoiceAddressType.ORIGIN);
        originAddress.setAddressName(ORIGIN_ADDRESS_NAME);
        originAddress.setAddress1(ORIGIN_ADDRESS1);
        originAddress.setAddress2(ORIGIN_ADDRESS2);
        originAddress.setCity(ORIGIN_CITY);
        originAddress.setState(ORIGIN_STATE);
        originAddress.setPostalCode(ORIGIN_POSTAL_CODE);
        originAddress.setCountryCode(ORIGIN_COUNTRY_CODE);
        return originAddress;
    }

    private CarrierInvoiceAddressDetailsEntity buildDestinationAddress() {
        CarrierInvoiceAddressDetailsEntity destinationAddress = new CarrierInvoiceAddressDetailsEntity();
        destinationAddress.setAddressType(CarrierInvoiceAddressType.DESTINATION);
        destinationAddress.setAddressName(DESTINATION_ADDRESS_NAME);
        destinationAddress.setAddress1(DESTINATION_ADDRESS1);
        destinationAddress.setAddress2(DESTINATION_ADDRESS2);
        destinationAddress.setCity(DESTINATION_CITY);
        destinationAddress.setState(DESTINATION_STATE);
        destinationAddress.setPostalCode(DESTINATION_POSTAL_CODE);
        destinationAddress.setCountryCode(DESTINATION_COUNTRY_CODE);
        return destinationAddress;
    }

    private static void assertCarrierInvoiceDetailsEntity(CarrierInvoiceDetailsEntity entity) {
        Assert.assertEquals(CARRIER_ID, entity.getCarrier().getId());
        Assert.assertEquals(INVOICE_NUMBER, entity.getInvoiceNumber());
        assertDatesEqual(INVOICE_DATE, entity.getInvoiceDate());
        Assert.assertEquals(REFERENCE_NUMBER, entity.getReferenceNumber());
        Assert.assertEquals(PAYMENT_TERMS, entity.getPaymentTerms());
        Assert.assertEquals(NET_AMOUNT.setScale(2, RoundingMode.HALF_UP), entity.getNetAmount().setScale(2, RoundingMode.HALF_UP));
        assertDatesEqual(DELIVERY_DATE, entity.getDeliveryDate());
        assertDatesEqual(EST_DELIVERY_DATE, entity.getEstDeliveryDate());
        Assert.assertEquals(BOL_NUMBER, entity.getBolNumber());
        Assert.assertEquals(PO_NUMBER, entity.getPoNumber());
        Assert.assertEquals(SHIPPER_REF_NUMBER, entity.getShipperRefNumber());
        Assert.assertEquals(PRO_NUMBER, entity.getProNumber());
        assertDatesEqual(ACTUAL_PICKUP_DATE, entity.getActualPickupDate());
        Assert.assertEquals(TOTAL_WEIGHT, entity.getTotalWeight());
        Assert.assertEquals(TOTAL_CHARGES, entity.getTotalCharges());
        Assert.assertEquals(TOTAL_QUANTITY, entity.getTotalQuantity());
        Assert.assertEquals(STATUS, entity.getStatus());
        assertCarrierInvoiceLineItems(entity.getCarrierInvoiceLineItems());
        assertOriginAddress(entity.getOriginAddress());
        assertDestinationAddress(entity.getDestinationAddress());
    }

    private static void assertCarrierInvoiceLineItems(Set<CarrierInvoiceLineItemEntity> lineItems) {
        Assert.assertEquals(1, lineItems.size());
        CarrierInvoiceLineItemEntity lineItem = lineItems.iterator().next();
        Assert.assertNotNull(lineItem);
        Assert.assertEquals(ORDER_NUMBER, lineItem.getOrderNumber());
        Assert.assertEquals(DESCRIPTION, lineItem.getDescription());
        Assert.assertEquals(WEIGHT, lineItem.getWeight());
        Assert.assertEquals(QUANTITY, lineItem.getQuantity());
        Assert.assertEquals(PACKAGING_CODE, lineItem.getPackagingCode());
        Assert.assertEquals(NMFC, lineItem.getNmfc());
        Assert.assertEquals(COMMODITY_CLASS, lineItem.getCommodityClass());
        Assert.assertEquals(CHARGE, lineItem.getCharge());
        Assert.assertEquals(SPECIAL_CHARGE_CODE, lineItem.getSpecialChargeCode());
        Assert.assertEquals(STATUS, lineItem.getStatus());
    }

    private static void assertOriginAddress(CarrierInvoiceAddressDetailsEntity originAddress) {
        Assert.assertEquals(CarrierInvoiceAddressType.ORIGIN, originAddress.getAddressType());
        Assert.assertEquals(ORIGIN_ADDRESS_NAME, originAddress.getAddressName());
        Assert.assertEquals(ORIGIN_ADDRESS1, originAddress.getAddress1());
        Assert.assertEquals(ORIGIN_ADDRESS2, originAddress.getAddress2());
        Assert.assertEquals(ORIGIN_CITY, originAddress.getCity());
        Assert.assertEquals(ORIGIN_STATE, originAddress.getState());
        Assert.assertEquals(ORIGIN_POSTAL_CODE, originAddress.getPostalCode());
        Assert.assertEquals(ORIGIN_COUNTRY_CODE, originAddress.getCountryCode());
    }

    private static void assertDestinationAddress(CarrierInvoiceAddressDetailsEntity destinationAddress) {
        Assert.assertEquals(CarrierInvoiceAddressType.DESTINATION, destinationAddress.getAddressType());
        Assert.assertEquals(DESTINATION_ADDRESS_NAME, destinationAddress.getAddressName());
        Assert.assertEquals(DESTINATION_ADDRESS1, destinationAddress.getAddress1());
        Assert.assertEquals(DESTINATION_ADDRESS2, destinationAddress.getAddress2());
        Assert.assertEquals(DESTINATION_CITY, destinationAddress.getCity());
        Assert.assertEquals(DESTINATION_STATE, destinationAddress.getState());
        Assert.assertEquals(DESTINATION_POSTAL_CODE, destinationAddress.getPostalCode());
        Assert.assertEquals(DESTINATION_COUNTRY_CODE, destinationAddress.getCountryCode());
    }

    private static void assertDatesEqual(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);

        Assert.assertEquals(calendar1.get(Calendar.YEAR), calendar2.get(Calendar.YEAR));
        Assert.assertEquals(calendar1.get(Calendar.MONTH), calendar2.get(Calendar.MONTH));
        Assert.assertEquals(calendar1.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(calendar1.get(Calendar.HOUR), calendar2.get(Calendar.HOUR));
        Assert.assertEquals(calendar1.get(Calendar.MINUTE), calendar2.get(Calendar.MINUTE));
        Assert.assertEquals(calendar1.get(Calendar.SECOND), calendar2.get(Calendar.SECOND));
    }
}
