package com.pls.shipment.service.impl.edi;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.enums.CarrierInvoiceAddressType;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.MaterialJaxbBO;
import com.pls.shipment.domain.sterling.TransactionDateJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.domain.sterling.enums.TransactionDateType;
import com.pls.shipment.service.edi.match.VendorBillEdiSaver;

/**
 * These test cases cover different scenarios that can happen in Invoice Service after receiving a 210 invoice
 * EDI.
 * 
 * @author Yasaman Honarvar
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class CarrierInvoiceIntegrationServiceImplTest {
    private static final String SCAC = "RCLA";

    private static final Date ACTUAL_PICKUP = DateUtility.addDays(new Date(), -1);
    private static final Date ACTUAL_DELIVERY = DateUtility.addDays(new Date(), -2);
    private static final Date ESTIMATED_DELIVERY = DateUtility.addDays(new Date(), -3);

    @Mock
    private CarrierDao carrierDao;

    @Mock
    private VendorBillEdiSaver vendorBillSaver;

    @InjectMocks
    private CarrierInvoiceIntegrationServiceImpl sut;

    @Test
    public void shouldProcessMessage() throws ApplicationException {
        CarrierEntity carrier = Mockito.mock(CarrierEntity.class);
        when(carrierDao.findByScac(SCAC)).thenReturn(carrier);

        LoadMessageJaxbBO message = createLoadMessageBO();

        sut.processMessage(message);

        ArgumentCaptor<CarrierInvoiceDetailsEntity> vendorBillCaptor = ArgumentCaptor.forClass(CarrierInvoiceDetailsEntity.class);
        Mockito.verify(vendorBillSaver).saveEdiVendorBill(vendorBillCaptor.capture(), Mockito.eq(new HashMap<String, String>()));
        CarrierInvoiceDetailsEntity vendorBill = vendorBillCaptor.getValue();

        Assert.assertNotNull(vendorBill);
        Assert.assertSame(carrier, vendorBill.getCarrier());
        Assert.assertEquals(Status.ACTIVE, vendorBill.getStatus());
        Assert.assertTrue(vendorBill.getEdi());
        Assert.assertNotSame(message.getInvoiceDate(), vendorBill.getInvoiceDate());
        Assert.assertEquals(PaymentTerms.getByCode(message.getInvoicePayTerms()), vendorBill.getPaymentTerms());
        Assert.assertSame(message.getInvoiceAmount(), vendorBill.getNetAmount());
        Assert.assertSame(message.getTotalWeight(), vendorBill.getTotalWeight());
        Assert.assertSame(message.getTotalCost(), vendorBill.getTotalCharges());
        Assert.assertSame(message.getTotalQuantity(), vendorBill.getTotalQuantity());

        Assert.assertSame(message.getBol(), vendorBill.getBolNumber());
        Assert.assertSame(message.getProNumber(), vendorBill.getProNumber());
        Assert.assertSame(message.getPickupNum(), vendorBill.getReferenceNumber());
        Assert.assertSame(message.getPoNum(), vendorBill.getPoNumber());
        Assert.assertSame(message.getShipmentNo(), vendorBill.getShipperRefNumber());
        Assert.assertSame(message.getInvoiceNumber(), vendorBill.getInvoiceNumber());
        Assert.assertSame(message.getEdiAccountNum(), vendorBill.getEdiAccount());

        Assert.assertTrue(DateUtils.isSameDay(ACTUAL_PICKUP, vendorBill.getActualPickupDate()));
        Assert.assertTrue(DateUtils.isSameDay(ACTUAL_DELIVERY, vendorBill.getDeliveryDate()));
        Assert.assertTrue(DateUtils.isSameDay(ESTIMATED_DELIVERY, vendorBill.getEstDeliveryDate()));

        compareAddresses(message.getAddresses().get(0), vendorBill.getOriginAddress(), CarrierInvoiceAddressType.ORIGIN);
        compareAddresses(message.getAddresses().get(1), vendorBill.getDestinationAddress(), CarrierInvoiceAddressType.DESTINATION);

        Assert.assertEquals(3, vendorBill.getCarrierInvoiceLineItems().size());
        compareMaterials(message.getMaterials().get(0), getMaterial(vendorBill, 0));
        compareMaterials(message.getMaterials().get(1), getMaterial(vendorBill, 1));
        compareMaterials(message.getMaterials().get(2), getMaterial(vendorBill, 2));
    }

    private void compareMaterials(MaterialJaxbBO material, CarrierInvoiceLineItemEntity vendorMaterial) {
        Assert.assertNotNull(vendorMaterial);
        Assert.assertNotNull(vendorMaterial.getCarrierInvoiceDetails());
        Assert.assertEquals(material.getWeight(), vendorMaterial.getWeight());
        Assert.assertEquals(material.getSubtotal(), vendorMaterial.getCharge());
        Assert.assertEquals(material.getNmfc(), vendorMaterial.getNmfc());
        Assert.assertEquals(material.getProductDesc(), vendorMaterial.getDescription());
        Assert.assertEquals(material.getOrderNum(), vendorMaterial.getOrderNumber());
        Assert.assertEquals(material.getPackagingType(), vendorMaterial.getPackagingCode());
        Assert.assertEquals(material.getQuantity(), vendorMaterial.getQuantity());
        Assert.assertEquals(material.getSpecialChargeCode(), vendorMaterial.getSpecialChargeCode());
        Assert.assertEquals(Status.ACTIVE, vendorMaterial.getStatus());
        Assert.assertEquals(CommodityClass.convertFromDbCode(material.getCommodityClassCode()), vendorMaterial.getCommodityClass());
    }

    private CarrierInvoiceLineItemEntity getMaterial(CarrierInvoiceDetailsEntity vendorBill, int index) {
        for (CarrierInvoiceLineItemEntity item : vendorBill.getCarrierInvoiceLineItems()) {
            if (item.getOrderNumber() == index) {
                return item;
            }
        }
        return null;
    }

    private void compareAddresses(AddressJaxbBO address, CarrierInvoiceAddressDetailsEntity vendorAddress, CarrierInvoiceAddressType type) {
        Assert.assertEquals(type, vendorAddress.getAddressType());
        Assert.assertSame(address.getAddress1(), vendorAddress.getAddress1());
        Assert.assertSame(address.getAddress2(), vendorAddress.getAddress2());
        Assert.assertSame(address.getName(), vendorAddress.getAddressName());
        Assert.assertNotNull(vendorAddress.getCarrierInvoiceDetails());
        Assert.assertSame(address.getCity(), vendorAddress.getCity());
        Assert.assertSame(address.getPostalCode(), vendorAddress.getPostalCode());
        Assert.assertSame(address.getStateCode(), vendorAddress.getState());
        Assert.assertSame(address.getCountryCode(), vendorAddress.getCountryCode());
    }

    private List<TransactionDateJaxbBO> createDates() {
        List<TransactionDateJaxbBO> dates = new ArrayList<TransactionDateJaxbBO>();
        dates.add(getTransactionDate(TransactionDateType.CONFIRM_PICKUP, ACTUAL_PICKUP));
        dates.add(getTransactionDate(TransactionDateType.CONFIRM_DELIVERY, ACTUAL_DELIVERY));
        dates.add(getTransactionDate(TransactionDateType.ESTIMATED_DELIVERY, ESTIMATED_DELIVERY));
        dates.add(getTransactionDate(TransactionDateType.GATE_ADMIT, DateUtility.addDays(new Date(), -4)));
        return dates;
    }

    private TransactionDateJaxbBO getTransactionDate(TransactionDateType dateType, Date date) {
        TransactionDateJaxbBO pickUp = new TransactionDateJaxbBO();
        pickUp.setTransDateType(dateType);
        pickUp.setTransDate(date);
        return pickUp;
    }

    private List<AddressJaxbBO> createAddresses() {
        List<AddressJaxbBO> addresses = new ArrayList<AddressJaxbBO>();
        addresses.add(createAdderss(AddressType.ORIGIN));
        addresses.add(createAdderss(AddressType.DESTINATION));
        addresses.add(createAdderss(AddressType.BILL_TO));
        addresses.add(createAdderss(AddressType.FREIGHT_CHARGE_TO));
        return addresses;
    }

    private AddressJaxbBO createAdderss(AddressType addressType) {
        AddressJaxbBO add = new AddressJaxbBO();
        add.setName("Test Name" + addressType);
        add.setAddress1("15678 Welthdom Road " + addressType);
        add.setAddress2("Cranberry " + addressType);
        add.setAddressCode("Test Code " + addressType);
        add.setAddressType(addressType);
        add.setCity("Pittsburgh " + addressType);
        add.setStateCode("PA " + addressType);
        add.setCountryCode("USA " + addressType);
        add.setPostalCode("15144 " + addressType);
        return add;
    }

    private List<MaterialJaxbBO> createMaterials() {
        List<MaterialJaxbBO> materials = new ArrayList<MaterialJaxbBO>();
        materials.add(createMaterial(0));
        materials.add(createMaterial(1));
        materials.add(createMaterial(2));
        return materials;
    }

    private MaterialJaxbBO createMaterial(int index) {
        MaterialJaxbBO material = new MaterialJaxbBO();
        material.setWeight(new BigDecimal(Math.random()));
        material.setSubtotal(new BigDecimal(Math.random()));
        material.setNmfc("nmfc" + index);
        material.setProductDesc("productDesc" + index);
        material.setOrderNum(index);
        material.setPackagingType("packagingType" + index);
        material.setCommodityClassCode("50");
        material.setQuantity((int) (Math.random() * 100) + 1);
        material.setSpecialChargeCode("specialChargeCode" + index);
        material.setCommodityClassCode(CommodityClass.values()[index].getDbCode());
        return material;
    }

    private LoadMessageJaxbBO createLoadMessageBO() {
        LoadMessageJaxbBO invoice = new LoadMessageJaxbBO();
        invoice.setPersonId(0L);
        invoice.setScac(SCAC);
        invoice.setTransactionDates(createDates());
        invoice.setInvoiceAmount(BigDecimal.valueOf(180L));
        invoice.setAddresses(createAddresses());
        invoice.setMaterials(createMaterials());
        invoice.setProNumber("150pro");
        invoice.setBol("150bol");
        invoice.setPoNum("150");
        invoice.setTotalCost(BigDecimal.valueOf(180L));
        invoice.setPickupNum("150");
        invoice.setShipmentNo("150");
        invoice.setInvoiceNumber("255558");
        invoice.setEdiAccountNum("2803226441");
        invoice.setInvoicePayTerms("COL");
        invoice.setInvoiceDate(DateUtility.addDays(new Date(), -2));
        invoice.setTotalWeight(new BigDecimal(Math.random()));
        invoice.setTotalQuantity((int) (Math.random() * 100));
        return invoice;
    }

}
