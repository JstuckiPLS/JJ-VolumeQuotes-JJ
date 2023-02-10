package com.pls.shipment.service.edi.parser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.shipment.dao.impl.CarrierEdiCostTypesDaoImpl;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.service.edi.EDIService;
import com.pls.shipment.service.edi.match.VendorBillEdiSaver;
import com.pls.shipment.service.impl.edi.handler.EDI210ParseResultHandlerImpl;
import com.pls.shipment.service.impl.edi.parser.AbstractEDIParser;
import com.pls.shipment.service.impl.edi.parser.EDI210Parser;
import com.pls.shipment.service.impl.edi.parser.EDI997Producer;
import com.pls.shipment.service.impl.edi.utils.EDIUtils;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * Test cases for {@link EDI210Parser} class.
 *
 * @author Mikhail Boldinov, 25/10/13
 */
@RunWith(MockitoJUnitRunner.class)
public class EDI210ParserTest extends AbstractEDIParserTest {
    private static final String CARRIER_SCAC = "RDWY";
    private static final String TRANSACTION_SET_ID = "210";
    private static final String EDI_FILE_NAME = "CTSI210.txt";
    private static final String EDI_WRONG_FILE_NAME = "CTSI210_wrong.txt";
    private static final int ENTITIES_COUNT = 99;

    private static final String INVOICE_NUMBER = "1108540566";
    private static final String REFERENCE_NUMBER = null;
    private static final String PAYMENT_TERMS = "CC";
    private static final BigDecimal NET_AMOUNT = new BigDecimal(140).setScale(2);
    private static final String DELIVERY_DATE = "20130819";
    private static final String BOL_NUMBER = "1108540566";
    private static final String PO_NUMBER = "NC1.10283.NC1.103086";
    private static final String SHIPPER_REFERENCE_NUMBER = null;
    private static final String PRO_NUMBER = "1108540566";
    private static final String EDI_ACCOUNT_NUM = "BOB BARKER";
    private static final String ACTUAL_PICKUP_DATE = "20130814";
    private static final BigDecimal TOTAL_WEIGHT = new BigDecimal(401);
    private static final BigDecimal TOTAL_CHARGES = new BigDecimal(140).setScale(2);
    private static final Integer TOTAL_QUANTITY = 1;
    private static final String ORIGIN_ADDRESS_NAME = "A PLUS PRODUCTS INC";
    private static final String ORIGIN_ADDRESS1 = "8 TIMBER LN";
    private static final String ORIGIN_ADDRESS2 = null;
    private static final String ORIGIN_CITY = "MARLBORO";
    private static final String ORIGIN_STATE = "NJ";
    private static final String ORIGIN_POSTAL_CODE = "07746";
    private static final String ORIGIN_COUNTRY_CODE = null;
    private static final String DESTINATION_ADDRESS_NAME = "BOB BARKER CO";
    private static final String DESTINATION_ADDRESS1 = "7925A PURFOY RD";
    private static final String DESTINATION_ADDRESS2 = null;
    private static final String DESTINATION_CITY = "FUQUAY VARINA";
    private static final String DESTINATION_STATE = "NC";
    private static final String DESTINATION_POSTAL_CODE = "27526";
    private static final String DESTINATION_COUNTRY_CODE = null;
    private static final BigDecimal LINE_ITEM1_WEIGHT = new BigDecimal(401);
    private static final Integer LINE_ITEM1_QUANTITY = 1;
    private static final String LINE_ITEM1_PACKAGING_CODE = "SKD";
    private static final BigDecimal LINE_ITEM1_CHARGE = new BigDecimal(140).setScale(2);
    private static final String LINE_ITEM1_SPECIAL_CHARGE_CODE = "400";
    private static final Integer LINE_ITEM1_ORDER_NUMBER = 1;
    private static final String LINE_ITEM1_DESCRIPTION = "METAL STAMPINS";
    private static final String LINE_ITEM1_NMFC = "10450000";
    private static final String LINE_ITEM1_COMMODITY_CLASS = "50";

    @Mock
    private CarrierEdiCostTypesDaoImpl carrierEdiCostTypesDaoImpl;

    @Mock
    private EDIEmailSender ediEmailSender;

    @Mock
    private CarrierEntity carrier;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private EDIService ediService;

    @Mock
    private EDI997Producer<CarrierInvoiceDetailsEntity> edi997Producer;

    @Mock
    private AbstractEDIParser.DataProvider dataProvider;

    @Mock
    private VendorBillEdiSaver vendorBillSaver;

    @InjectMocks
    private EDI210Parser sut;

    @InjectMocks
    private EDI210ParseResultHandlerImpl theEDI210ParseResultHandlerImpl;

    @Before
    public void setUp() {
        Mockito.when(sut.getCarrier().getScac()).thenReturn(CARRIER_SCAC);
    }

    @After
    public void tearDown() {
        deleteFile(EDI_FILE_NAME);
    }

    @Test
    public void testParseEDI210() throws Exception {
        EDIParseResult<CarrierInvoiceDetailsEntity> parseResult = sut.parse(getEdiFile(EDI_FILE_NAME, TRANSACTION_SET_ID, CARRIER_SCAC));
        Assert.assertNotNull(parseResult);
        Assert.assertNotNull(parseResult.getParsedEntities());
        Assert.assertEquals(ENTITIES_COUNT, parseResult.getParsedEntities().size());
        boolean first = true;
        for (CarrierInvoiceDetailsEntity entity : parseResult.getParsedEntities()) {
            Assert.assertNotNull(entity);
            if (first) {
                assertEntityValid(entity);
                first = false;
            }
        }
        List<Integer> unprocessedEntitiesInd = theEDI210ParseResultHandlerImpl.handle(parseResult);
        Assert.assertEquals(0, unprocessedEntitiesInd.size());
        Mockito.verify(vendorBillSaver, Mockito.times(99)).saveEdiVendorBill(Mockito.<CarrierInvoiceDetailsEntity>any(),
                Mockito.<Map<String, String>>any());
    }

    @Test
    public void testParseEDI210WithWrongData() throws Exception {
        EDIParseResult<CarrierInvoiceDetailsEntity> parseResult = sut.parse(getEdiFile(EDI_WRONG_FILE_NAME, TRANSACTION_SET_ID, CARRIER_SCAC));
        List<Integer> unprocessedEntitiesInd = theEDI210ParseResultHandlerImpl.handle(parseResult);
        Assert.assertEquals(0, unprocessedEntitiesInd.size());
        Mockito.verify(vendorBillSaver, Mockito.times(99)).saveEdiVendorBill(Mockito.<CarrierInvoiceDetailsEntity>any(),
                Mockito.<Map<String, String>>any());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateEDI210() {
        sut.create(Arrays.asList(new CarrierInvoiceDetailsEntity()), null);
    }

    private static void assertEntityValid(CarrierInvoiceDetailsEntity entity) {
        Assert.assertNotNull(entity.getCarrier());
        Assert.assertTrue(entity.getEdi());
        Assert.assertEquals(INVOICE_NUMBER, entity.getInvoiceNumber());
        Assert.assertNotNull(entity.getInvoiceDate());
        Assert.assertEquals(EDIUtils.toDateStr(new Date()), EDIUtils.toDateStr(entity.getInvoiceDate()));
        Assert.assertEquals(REFERENCE_NUMBER, entity.getReferenceNumber());
        Assert.assertNotNull(entity.getPaymentTerms());
        Assert.assertEquals(PAYMENT_TERMS, entity.getPaymentTerms().getPaymentTermsEdiCode());
        Assert.assertNotNull(entity.getNetAmount());
        Assert.assertEquals(NET_AMOUNT, entity.getNetAmount());
        Assert.assertNotNull(entity.getNetAmount());
        Assert.assertNotNull(entity.getDeliveryDate());
        Assert.assertEquals(DELIVERY_DATE, EDIUtils.toDateStr(entity.getDeliveryDate()));
        Assert.assertEquals(DELIVERY_DATE, EDIUtils.toDateStr(entity.getEstDeliveryDate()));
        Assert.assertEquals(BOL_NUMBER, entity.getBolNumber());
        Assert.assertEquals(PO_NUMBER, entity.getPoNumber());
        Assert.assertEquals(SHIPPER_REFERENCE_NUMBER, entity.getShipperRefNumber());
        Assert.assertEquals(PRO_NUMBER, entity.getProNumber());
        Assert.assertEquals(EDI_ACCOUNT_NUM, entity.getEdiAccount());
        Assert.assertNotNull(entity.getActualPickupDate());
        Assert.assertEquals(ACTUAL_PICKUP_DATE, EDIUtils.toDateStr(entity.getActualPickupDate()));
        Assert.assertNotNull(entity.getTotalWeight());
        Assert.assertEquals(TOTAL_WEIGHT, entity.getTotalWeight());
        Assert.assertNotNull(entity.getTotalCharges());
        Assert.assertEquals(TOTAL_CHARGES, entity.getTotalCharges());
        Assert.assertNotNull(entity.getTotalQuantity());
        Assert.assertEquals(TOTAL_QUANTITY, entity.getTotalQuantity());
        Assert.assertNotNull(entity.getOriginAddress());
        Assert.assertEquals(ORIGIN_ADDRESS_NAME, entity.getOriginAddress().getAddressName());
        Assert.assertEquals(ORIGIN_ADDRESS1, entity.getOriginAddress().getAddress1());
        Assert.assertEquals(ORIGIN_ADDRESS2, entity.getOriginAddress().getAddress2());
        Assert.assertEquals(ORIGIN_CITY, entity.getOriginAddress().getCity());
        Assert.assertEquals(ORIGIN_STATE, entity.getOriginAddress().getState());
        Assert.assertEquals(ORIGIN_POSTAL_CODE, entity.getOriginAddress().getPostalCode());
        Assert.assertEquals(ORIGIN_COUNTRY_CODE, entity.getOriginAddress().getCountryCode());
        Assert.assertNotNull(entity.getDestinationAddress());
        Assert.assertEquals(DESTINATION_ADDRESS_NAME, entity.getDestinationAddress().getAddressName());
        Assert.assertEquals(DESTINATION_ADDRESS1, entity.getDestinationAddress().getAddress1());
        Assert.assertEquals(DESTINATION_ADDRESS2, entity.getDestinationAddress().getAddress2());
        Assert.assertEquals(DESTINATION_CITY, entity.getDestinationAddress().getCity());
        Assert.assertEquals(DESTINATION_STATE, entity.getDestinationAddress().getState());
        Assert.assertEquals(DESTINATION_POSTAL_CODE, entity.getDestinationAddress().getPostalCode());
        Assert.assertEquals(DESTINATION_COUNTRY_CODE, entity.getDestinationAddress().getCountryCode());
        Assert.assertNotNull(entity.getCarrierInvoiceLineItems());
        Assert.assertEquals(1, entity.getCarrierInvoiceLineItems().size());
        CarrierInvoiceLineItemEntity carrierInvoiceLineItem = entity.getCarrierInvoiceLineItems().iterator().next();
        Assert.assertNotNull(carrierInvoiceLineItem);
        Assert.assertNotNull(carrierInvoiceLineItem.getWeight());
        Assert.assertEquals(LINE_ITEM1_WEIGHT, carrierInvoiceLineItem.getWeight());
        Assert.assertNotNull(carrierInvoiceLineItem.getQuantity());
        Assert.assertEquals(LINE_ITEM1_QUANTITY, carrierInvoiceLineItem.getQuantity());
        Assert.assertEquals(LINE_ITEM1_PACKAGING_CODE, carrierInvoiceLineItem.getPackagingCode());
        Assert.assertNotNull(carrierInvoiceLineItem.getCharge());
        Assert.assertEquals(LINE_ITEM1_CHARGE, carrierInvoiceLineItem.getCharge());
        Assert.assertEquals(LINE_ITEM1_SPECIAL_CHARGE_CODE, carrierInvoiceLineItem.getSpecialChargeCode());
        Assert.assertEquals(LINE_ITEM1_CHARGE, carrierInvoiceLineItem.getCharge());
        Assert.assertEquals(LINE_ITEM1_ORDER_NUMBER, carrierInvoiceLineItem.getOrderNumber());
        Assert.assertEquals(LINE_ITEM1_DESCRIPTION, carrierInvoiceLineItem.getDescription());
        Assert.assertEquals(LINE_ITEM1_NMFC, carrierInvoiceLineItem.getNmfc());
        Assert.assertNotNull(carrierInvoiceLineItem.getCommodityClass());
        Assert.assertEquals(LINE_ITEM1_COMMODITY_CLASS, carrierInvoiceLineItem.getCommodityClass().getDbCode());
    }
}
