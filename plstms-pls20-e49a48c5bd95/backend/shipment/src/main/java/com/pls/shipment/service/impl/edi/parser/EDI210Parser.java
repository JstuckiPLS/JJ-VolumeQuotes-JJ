package com.pls.shipment.service.impl.edi.parser;

import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toBigDecimal;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toCurrency;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toDate;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toInteger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.pb.x12.Cf;
import org.pb.x12.FormatException;
import org.pb.x12.Loop;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.EDIValidationException;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.domain.enums.CarrierInvoiceAddressType;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.edi.parser.EDIQualifiersPlsReference;
import com.pls.shipment.service.impl.edi.parser.enums.EDI210Element;

/**
 * EDI Parser for X12 210 - Motor Carrier Freight Details and Invoice.
 *
 * @author Mikhail Boldinov, 28/08/13
 */
public class EDI210Parser extends AbstractEDIParser<CarrierInvoiceDetailsEntity> {

    private static final String LINE_ITEM_GROUP = "LX";
    public static final String LINE_ITEM_GROUP_SPO = "SPO";
    private static final String REFERENCE_IDENTIFICATION = "N9";
    private static final String PROOF_OF_DELIVERY = "POD";

    public static final String INVOICE_DETAILS = "B3_InvoiceDetails";
    public static final String BOL_NUMBER = "N9_BolNumber";
    public static final String PO_NUMBER = "N9_PoNumber";
    public static final String SHIPPER_REF_NUMBER = "N9_ShipperReferenceNumber";
    public static final String PRO_NUMBER = "N9_ProNumber";
    public static final String PU_NUMBER = "N9_PuNumber";
    public static final String EDI_ACCOUNT = "N9_EdiAccount";
    public static final String ACTUAL_PICKUP_DATE = "G62_ActualPickupDate";
    public static final String LINE_ITEMS_L0 = "L0_LineItems0";
    public static final String LINE_ITEMS_L1 = "L1_LineItems1";
    public static final String MEASUREMENT = "L4_Measurement";
    public static final String LINE_ITEMS_L5 = "L5_LineItems5";
    public static final String LINE_ITEMS_L7 = "L7_LineItems7";
    public static final String CHARGE_DETAIL = "L9_ChargeDetail";

    public static final String TOTAL = "L3_Total";
    public static final String ORIGIN_ADDRESS = "N1_OriginAddress";
    public static final String DESTINATION_ADDRESS = "N1_DestinationAddress";
    public static final String BILL_TO_ADDRESS = "N1_BillToAddress";
    public static final String PARTY_IDENTIFICATION = "N1_PartyIdentification";
    public static final String ADDRESS = "N3_Address";
    public static final String PARTY_LOCATION = "N3_PartyLocation";
    public static final String ADDITIONAL_NAME_INFORMATION = "N2_AdditionalNameInformation";
    public static final String CITY_ST_ZIP = "N4_CityStateZip";
    public static final String GEOGRAPHIC_LOCATION = "N4_GeographicLocation";
    public static final String HAZARDOUS_MATERIAL = "H1_HazardousMaterial";
    public static final String HAZARDOUS_MATERIAL_DESCRIPTION = "H2_AdditionalHazardousMaterialDescription";
    public static final String SPECIAL_SERVICES = "H6_Special Services";
    public static final String DESTINATION_QUANTITY = "SDQ_DestinationQuantity";
    public static final String DATE_TIME = "G62_Date/Time";
    public static final String PACKAGE_DETAIL = "CD3_PackageDetail";
    public static final String REMARKS = "K1_Remarks";

    /**
     * Constructor.
     *
     * @param carrier      EDI carrier
     * @param dataProvider data provider for EDI parser
     */
    public EDI210Parser(CarrierEntity carrier, DataProvider dataProvider) {
        super(carrier, dataProvider);
    }

    @Override
    public Cf getTransactionConfig() {
        Cf transactionConfig = new Cf(TRANSACTION_SET_HEADER, "ST", EDITransactionSet._210.getId(), 1);
        transactionConfig.addChild(INVOICE_DETAILS, "B3");
        transactionConfig.addChild(BOL_NUMBER, "N9", getQualifier(Qualifiers.BOL_NUMBER), 1);
        transactionConfig.addChild(PO_NUMBER, "N9", getQualifier(Qualifiers.PO_NUMBER), 1);
        transactionConfig.addChild(SHIPPER_REF_NUMBER, "N9", getQualifier(Qualifiers.SHIPPER_REFERENCE_NUMBER), 1);
        transactionConfig.addChild(PRO_NUMBER, "N9", getQualifier(Qualifiers.PRO_NUMBER), 1);
        transactionConfig.addChild(PU_NUMBER, "N9", getQualifier(Qualifiers.PU_NUMBER), 1);
        transactionConfig.addChild(EDI_ACCOUNT, "N9", getQualifier(Qualifiers.CUSTOMER_EDI_NUM), 1);
        transactionConfig.addChild(ACTUAL_PICKUP_DATE, "G62", getQualifier(Qualifiers.ACTUAL_PICKUP_DATE, Qualifiers.SHIPPED_ON_THIS_DATE), 1);
        transactionConfig.addChild(TOTAL, "L3");

        Cf originAddress = transactionConfig.addChild(ORIGIN_ADDRESS, "N1",
                getQualifier(Qualifiers.SHIPPER_ADDRESS, Qualifiers.SHIPPER_ADDITIONAL_ADDRESS), 1);
        originAddress.addChild(ADDRESS, "N3");
        originAddress.addChild(CITY_ST_ZIP, "N4");
        originAddress.addChild(ADDITIONAL_NAME_INFORMATION, "N2");
        originAddress.addChild(REFERENCE_IDENTIFICATION, "N9");

        Cf destinationAddress = transactionConfig.addChild(DESTINATION_ADDRESS, "N1",
                getQualifier(Qualifiers.CONSIGNEE_ADDRESS, Qualifiers.CONSIGNEE_ADDITIONAL_ADDRESS), 1);
        destinationAddress.addChild(ADDRESS, "N3");
        destinationAddress.addChild(CITY_ST_ZIP, "N4");
        originAddress.addChild(ADDITIONAL_NAME_INFORMATION, "N2");
        originAddress.addChild(REFERENCE_IDENTIFICATION, "N9");

        Cf billToAddress = transactionConfig.addChild(BILL_TO_ADDRESS, "N1", getQualifier(Qualifiers.BILL_TO_ADDRESS), 1);
        billToAddress.addChild(ADDRESS, "N3");
        billToAddress.addChild(CITY_ST_ZIP, "N4");
        originAddress.addChild(ADDITIONAL_NAME_INFORMATION, "N2");
        originAddress.addChild(REFERENCE_IDENTIFICATION, "N9");

        Cf lx = transactionConfig.addChild(LINE_ITEM_GROUP, "LX");
        lx.addChild(PROOF_OF_DELIVERY, "POD");
        lx.addChild(REFERENCE_IDENTIFICATION, "N9");
        lx.addChild(LINE_ITEMS_L0, "L0");
        lx.addChild(LINE_ITEMS_L1, "L1");
        lx.addChild(LINE_ITEMS_L5, "L5");
        lx.addChild(LINE_ITEMS_L7, "L7");
        lx.addChild(HAZARDOUS_MATERIAL, "H1");
        lx.addChild(HAZARDOUS_MATERIAL_DESCRIPTION, "H2");
        lx.addChild(MEASUREMENT, "L4");
        lx.addChild(REMARKS, "K1");
        lx.addChild(LINE_ITEM_GROUP_SPO, "SPO");
        lx.addChild(DESTINATION_QUANTITY, "SDQ");
        lx.addChild(PARTY_IDENTIFICATION, "N1");
        lx.addChild(ADDITIONAL_NAME_INFORMATION, "N2");
        lx.addChild(PARTY_LOCATION, "N3");
        lx.addChild(GEOGRAPHIC_LOCATION, "N4");
        lx.addChild(PACKAGE_DETAIL, "CD3");
        lx.addChild(SPECIAL_SERVICES, "H6");
        lx.addChild(CHARGE_DETAIL, "L9");
        lx.addChild(DATE_TIME, "G62");

        return transactionConfig;
    }

    @Override
    public EDIParseResult<CarrierInvoiceDetailsEntity> parse(EDIFile file) throws IOException, FormatException, EDIValidationException {
        EDIParseResult<CarrierInvoiceDetailsEntity> parseResult = new EDIParseResult<CarrierInvoiceDetailsEntity>(file);
        List<CarrierInvoiceDetailsEntity> carrierInvoices = new ArrayList<CarrierInvoiceDetailsEntity>();
        parseResult.setReceiverId(getReceiverId(file));
        List<Loop> invoices = getX12(file).findLoop(TRANSACTION_SET_HEADER);
        for (Loop invoice : invoices) {
            CarrierInvoiceDetailsEntity carrierInvoiceDetails = new CarrierInvoiceDetailsEntity();
            carrierInvoiceDetails.getModification().setCreatedBy(getDataProvider().getEdiUserId());
            carrierInvoiceDetails.setStatus(Status.ACTIVE);
            carrierInvoiceDetails.setCarrier(getCarrier());
            carrierInvoiceDetails.setEdi(true);

            carrierInvoiceDetails.setInvoiceDate(new Date());
            setNumbers(invoice, carrierInvoiceDetails);

            PaymentTerms paymentTerms;
            try {
                paymentTerms = PaymentTerms.getByEdiCode(getElementValue(invoice, EDI210Element.PAY_TERMS).getValue());
            } catch (IllegalArgumentException e) {
                paymentTerms = null;
            }
            carrierInvoiceDetails.setPaymentTerms(paymentTerms);
            carrierInvoiceDetails.setNetAmount(toCurrency(getElementValue(invoice, EDI210Element.NET_AMOUNT).getValue(), true));
            carrierInvoiceDetails.setDeliveryDate(toDate(getElementValue(invoice, EDI210Element.DELIVERY_DATE).getValue()));
            carrierInvoiceDetails.setEstDeliveryDate(carrierInvoiceDetails.getDeliveryDate());

            carrierInvoiceDetails.setActualPickupDate(toDate(getElementValue(invoice, EDI210Element.ACTUAL_PICKUP_DATE).getValue()));
            carrierInvoiceDetails.setTotalWeight(toBigDecimal(getElementValue(invoice, EDI210Element.TOTAL_WEIGHT).getValue()));
            carrierInvoiceDetails.setTotalCharges(toCurrency(getElementValue(invoice, EDI210Element.TOTAL_CHARGES).getValue(), true));
            carrierInvoiceDetails.setTotalQuantity(toInteger(getElementValue(invoice, EDI210Element.TOTAL_QUANTITY).getValue()));

            carrierInvoiceDetails.setOriginAddress(parseAddress(invoice, EDI210Element.ORIGIN_ADDRESS,
                    CarrierInvoiceAddressType.ORIGIN, carrierInvoiceDetails));
            carrierInvoiceDetails.setDestinationAddress(parseAddress(invoice, EDI210Element.DESTINATION_ADDRESS,
                    CarrierInvoiceAddressType.DESTINATION, carrierInvoiceDetails));

            carrierInvoiceDetails.setCarrierInvoiceLineItems(new HashSet<CarrierInvoiceLineItemEntity>());
            List<Loop> lineItems = invoice.findLoop(LINE_ITEM_GROUP);
            for (Loop lineItem : lineItems) {
                CarrierInvoiceLineItemEntity carrierInvoiceLineItem = parseCarrierInvoiceLineItem(lineItem);
                carrierInvoiceLineItem.setCarrierInvoiceDetails(carrierInvoiceDetails);
                carrierInvoiceDetails.getCarrierInvoiceLineItems().add(carrierInvoiceLineItem);
            }
            carrierInvoices.add(carrierInvoiceDetails);
        }
        parseResult.setParsedEntities(carrierInvoices);
        parseResult.setEdiData(getEdiData());
        parseResult.setStatus(EDIParseResult.Status.SUCCESS);
        return parseResult;
    }

    private CarrierInvoiceLineItemEntity parseCarrierInvoiceLineItem(Loop lineItem) {
        CarrierInvoiceLineItemEntity carrierInvoiceLineItem = new CarrierInvoiceLineItemEntity();
        carrierInvoiceLineItem.getModification().setCreatedBy(getDataProvider().getEdiUserId());
        carrierInvoiceLineItem.setStatus(Status.ACTIVE);
        carrierInvoiceLineItem.setWeight(toBigDecimal(getElementValue(lineItem, EDI210Element.WEIGHT).getValue()));
        carrierInvoiceLineItem.setQuantity(toInteger(getElementValue(lineItem, EDI210Element.QUANTITY).getValue()));
        carrierInvoiceLineItem.setPackagingCode(getElementValue(lineItem, EDI210Element.PACKAGING_CODE).getValue());
        carrierInvoiceLineItem.setCharge(toCurrency(getElementValue(lineItem, EDI210Element.CHARGE).getValue(), true));
        carrierInvoiceLineItem.setSpecialChargeCode(getElementValue(lineItem, EDI210Element.SPECIAL_CHARGE_CODE).getValue());
        carrierInvoiceLineItem.setOrderNumber(toInteger(getElementValue(lineItem, EDI210Element.ORDER_NUM).getValue()));
        carrierInvoiceLineItem.setDescription(getElementValue(lineItem, EDI210Element.DESCRIPTION).getValue());
        carrierInvoiceLineItem.setNmfc(getElementValue(lineItem, EDI210Element.COMMODITY_CODE).getValue());
        CommodityClass commodityClass;
        try {
            commodityClass = CommodityClass.convertFromDbCode(getElementValue(lineItem, EDI210Element.COMMODITY_CLASS_CODE).getValue());
        } catch (IllegalArgumentException e) {
            commodityClass = null;
        }
        carrierInvoiceLineItem.setCommodityClass(commodityClass);
        return carrierInvoiceLineItem;
    }

    @Override
    public EDIFile create(List<CarrierInvoiceDetailsEntity> entities, Map<String, Object> params) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    private CarrierInvoiceAddressDetailsEntity parseAddress(Loop root, EDI210Element addressElement, CarrierInvoiceAddressType addressType,
                                                            CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        List<Loop> addressLoops = root.findLoop(addressElement.getConfigName());
        if (addressLoops != null && !addressLoops.isEmpty()) {
            Loop addressLoop = addressLoops.get(0);
            CarrierInvoiceAddressDetailsEntity addressDetailsEntity = new CarrierInvoiceAddressDetailsEntity();
            addressDetailsEntity.setCarrierInvoiceDetails(carrierInvoiceDetails);
            addressDetailsEntity.getModification().setCreatedBy(getDataProvider().getEdiUserId());
            addressDetailsEntity.setAddressType(addressType);
            addressDetailsEntity.setAddressName(getElementValue(root, addressElement).getValue());
            addressDetailsEntity.setAddress1(getElementValue(addressLoop, EDI210Element.ADDRESS1).getValue());
            addressDetailsEntity.setAddress2(getElementValue(addressLoop, EDI210Element.ADDRESS2).getValue());
            addressDetailsEntity.setCity(getElementValue(addressLoop, EDI210Element.CITY).getValue());
            addressDetailsEntity.setState(getElementValue(addressLoop, EDI210Element.STATE).getValue());
            addressDetailsEntity.setPostalCode(getElementValue(addressLoop, EDI210Element.ZIP).getValue());
            addressDetailsEntity.setCountryCode(getElementValue(addressLoop, EDI210Element.COUNTRY).getValue());
            return addressDetailsEntity;
        }
        return null;
    }

    private void setNumbers(Loop invoice, CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        String bolNumber = getElementValue(invoice, EDI210Element.BOL_NUMBER).getValue();
        if (bolNumber == null) {
            bolNumber = getElementValue(invoice, EDI210Element.REF_NUMBER).getValue();
        }
        String proNumber = getElementValue(invoice, EDI210Element.PRO_NUMBER).getValue();
        if (proNumber == null) {
            proNumber = getElementValue(invoice, EDI210Element.INVOICE_NUMBER).getValue();
        }
        String puNumber = null;
        if (getElementValue(invoice, EDI210Element.PU_NUMBER).getValue() != null) {
            puNumber = getElementValue(invoice, EDI210Element.REF_NUMBER).getValue();
        }

        carrierInvoiceDetails.setBolNumber(bolNumber);
        carrierInvoiceDetails.setProNumber(proNumber);
        carrierInvoiceDetails.setReferenceNumber(puNumber);

        carrierInvoiceDetails.setPoNumber(getElementValue(invoice, EDI210Element.PO_NUMBER).getValue());
        carrierInvoiceDetails.setShipperRefNumber(getElementValue(invoice, EDI210Element.SHIPPER_REF_NUMBER).getValue());
        carrierInvoiceDetails.setInvoiceNumber(getElementValue(invoice, EDI210Element.INVOICE_NUMBER).getValue());
        carrierInvoiceDetails.setEdiAccount(getElementValue(invoice, EDI210Element.EDI_NUMBER).getValue());
    }

    /**
     * PLS references enumeration for EDI 210 qualifiers.
     */
    enum Qualifiers implements EDIQualifiersPlsReference {
        BOL_NUMBER("BM"),
        PO_NUMBER("PO"),
        SHIPPER_REFERENCE_NUMBER("CR"),
        PRO_NUMBER("CN"),
        PU_NUMBER("PU"),
        CUSTOMER_EDI_NUM("23"),
        ACTUAL_PICKUP_DATE("86"),
        SHIPPED_ON_THIS_DATE("11"),
        SHIPPER_ADDRESS("SH"),
        SHIPPER_ADDITIONAL_ADDRESS("SF"),
        CONSIGNEE_ADDRESS("CN"),
        CONSIGNEE_ADDITIONAL_ADDRESS("ST"),
        BILL_TO_ADDRESS("BT"),
        PAYER_ADDRESS(""),
        SHIP_FROM_ADDRESS("");

        private String defaultValue;

        Qualifiers(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }
    }
}
