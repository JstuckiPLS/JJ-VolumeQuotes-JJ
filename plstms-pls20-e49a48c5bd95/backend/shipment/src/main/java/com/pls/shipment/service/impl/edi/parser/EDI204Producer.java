package com.pls.shipment.service.impl.edi.parser;

import static com.pls.shipment.service.impl.edi.utils.EDIUtils.adjustLength;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.element;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.getCurrentDateShortStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.getCurrentDateStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.getCurrentTimeStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.refineString;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.replaceLineFeed;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toCurrencyStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toDateStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toDateTimeStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toStr;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toTimeStr;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.pb.x12.Cf;
import org.pb.x12.Context;
import org.pb.x12.FormatException;
import org.pb.x12.Loop;
import org.pb.x12.X12;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.EDIValidationException;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.domain.enums.ShipmentOperationType;
import com.pls.shipment.service.ShipmentUtils;
import com.pls.shipment.service.edi.parser.EDIQualifiersPlsReference;
import com.pls.shipment.service.impl.LoadTenderServiceImpl;

/**
 * EDI Producer for X12 204 - Motor Carrier Load Tender.
 * 
 * @author Mikhail Boldinov, 16/10/13
 */
public class EDI204Producer extends AbstractEDIParser<LoadEntity> {

    private static final char SEGMENT_SEPARATOR = '~';
    private static final char ELEMENT_SEPARATOR = '*';
    private static final char COMPOSITE_ELEMENT_SEPARATOR = ':';

    private static final String[] PHONE_FORBIDDEN_SYMBOLS = {" ", "+", "-", "(", ")"};

    private static final String PLS_SENDER_ID = "789507209";

    private static final String TRANSACTION_HEADER = "Shipment";

    private static final String HAZMAT_WARNING_MESSAGE = "THERE IS ONE OR MORE PRODUCTS THAT ARE HAZARDOUS. ";

    //Segment B2
    private static final String SHIPMENT_INFORMATION = "shipmentInformation";

    //Segment B2A
    private static final String PURPOSE = "purpose";

    //Segment L11
    private static final String CLIENT_NUMBER = "clientNumber";
    private static final String BOL_NUMBER = "bolNumber";
    private static final String SHIPPER_REFERENCE_NUMBER = "shipperRefNum";
    private static final String PICKUP_REFERENCE_NUMBER = "pickupReferenceNumber";
    private static final String PO_NUMBER = "poNumber";

    //Segment N1
    private static final String ORIGIN_ADDRESS = "originAddress";
    private static final String DESTINATION_ADDRESS = "destinationAddress";
    private static final String BILL_TO_ADDRESS = "billToAddress";
    private static final String ADDRESS_NAME = "addressName";

    //Segment N3
    private static final String ADDRESS = "address";

    //Segment N4
    private static final String CITY_ST_ZIP = "cityStateZip";

    //Segment G61
    private static final String CONTACT = "contact";

    //Segment N7
    private static final String EQUIPMENT_DETAILS = "equipmentDetails";

    //Segment S5
    private static final String ORIGIN_STOP_OFF = "stopOffOrigin";
    private static final String DESTINATION_STOP_OFF = "stopOffDestination";

    //Segment G62
    private static final String EARLIEST_PICKUP_DATE = "earliestPickupDate";
    private static final String LATEST_PICKUP_DATE = "latestPickupDate";
    private static final String SCHEDULED_DELIVERY_DATE = "scheduledDeliveryDate";

    //Segment AT8
    private static final String PACKAGING_DATA = "packagingData";

    //Segment LAD
    private static final String LADING_DETAILS = "ladingDetails";

    //Segment PLD
    private static final String PALLET_INFORMATION = "palletInformation";

    //Segment NTE
    private static final String PICKUP_NOTES = "pickupNotes";
    private static final String DELIVERY_NOTES = "deliveryNotes";

    //Segment OID
    private static final String ORDER_IDENTIFICATION_DETAILS = "orderIdentificationDetails";

    //Segment L5
    private static final String DESCRIPTION_MARKS_NUMBERS = "descriptionMarksNumbers";

    //Segment L3
    private static final String WEIGHT_CHARGES = "weightCharges";
    public static final String PALLET_PACKAGE_TYPE = "PLT";

    /**
     * Constructor.
     *
     * @param carrier      EDI carrier
     * @param dataProvider data provider for EDI producer
     */
    public EDI204Producer(CarrierEntity carrier, DataProvider dataProvider) {
        super(carrier, dataProvider);
    }

    @Override
    protected Cf getTransactionConfig() {
        Cf transactionConfig = new Cf(TRANSACTION_HEADER, "ST", EDITransactionSet._204.getId(), 1);
        transactionConfig.addChild(SHIPMENT_INFORMATION, "B2");
        transactionConfig.addChild(PURPOSE, "B2A");
        transactionConfig.addChild(CLIENT_NUMBER, "L11", getQualifier(Qualifiers.CLIENT_NUMBER), 2);
        transactionConfig.addChild(BOL_NUMBER, "L11", getQualifier(Qualifiers.BOL_NUMBER), 2);
        transactionConfig.addChild(SHIPPER_REFERENCE_NUMBER, "L11", getQualifier(Qualifiers.SHIPPER_REFERENCE_NUMBER), 2);
        transactionConfig.addChild(PICKUP_REFERENCE_NUMBER, "L11", getQualifier(Qualifiers.PICKUP_REFERENCE_NUMBER), 2);
        transactionConfig.addChild(PO_NUMBER, "L11", getQualifier(Qualifiers.PO_NUMBER), 2);

        Cf originAddress = transactionConfig.addChild(ORIGIN_ADDRESS, "N1", getQualifier(Qualifiers.SHIPPER_ADDRESS), 1);
        originAddress.addChild(ADDRESS_NAME, "N1");
        originAddress.addChild(ADDRESS, "N3");
        originAddress.addChild(CITY_ST_ZIP, "N4");
        originAddress.addChild(CONTACT, "G61");
        transactionConfig.addChild(originAddress);

        Cf destinationAddress = transactionConfig.addChild(DESTINATION_ADDRESS, "N1", getQualifier(Qualifiers.CONSIGNEE_ADDRESS), 1);
        destinationAddress.addChild(ADDRESS_NAME, "N1");
        destinationAddress.addChild(ADDRESS, "N3");
        destinationAddress.addChild(CITY_ST_ZIP, "N4");
        destinationAddress.addChild(CONTACT, "G61");
        transactionConfig.addChild(destinationAddress);

        Cf billToAddress = transactionConfig.addChild(BILL_TO_ADDRESS, "N1", getQualifier(Qualifiers.BILL_TO_ADDRESS), 1);
        billToAddress.addChild(ADDRESS_NAME, "N1");
        billToAddress.addChild(ADDRESS, "N3");
        billToAddress.addChild(CITY_ST_ZIP, "N4");
        billToAddress.addChild(CONTACT, "G61");
        transactionConfig.addChild(billToAddress);

        transactionConfig.addChild(EQUIPMENT_DETAILS, "N7");
        transactionConfig.addChild(ORIGIN_STOP_OFF, "S5", getQualifier(Qualifiers.ORIGIN_STOPOFF), 1);
        transactionConfig.addChild(DESTINATION_STOP_OFF, "S5", getQualifier(Qualifiers.DESTINATION_STOPOFF), 1);
        transactionConfig.addChild(EARLIEST_PICKUP_DATE, "G62", getQualifier(Qualifiers.EARLIEST_PICKUP_DATE), 1);
        transactionConfig.addChild(LATEST_PICKUP_DATE, "G62", getQualifier(Qualifiers.LATEST_PICKUP_DATE), 1);
        transactionConfig.addChild(SCHEDULED_DELIVERY_DATE, "G62", getQualifier(Qualifiers.SCHEDULED_DELIVERY_DATE), 1);
        transactionConfig.addChild(PACKAGING_DATA, "AT8");
        transactionConfig.addChild(LADING_DETAILS, "LAD");
        transactionConfig.addChild(PALLET_INFORMATION, "PLD");
        transactionConfig.addChild(PICKUP_NOTES, "NTE", getQualifier(Qualifiers.PICKUP_NOTES), 1);
        transactionConfig.addChild(DELIVERY_NOTES, "NTE", getQualifier(Qualifiers.DELIVERY_NOTES), 1);
        transactionConfig.addChild(ORDER_IDENTIFICATION_DETAILS, "OID");
        transactionConfig.addChild(DESCRIPTION_MARKS_NUMBERS, "L5");
        transactionConfig.addChild(WEIGHT_CHARGES, "L3");
        return transactionConfig;
    }

    @Override
    public EDIParseResult<LoadEntity> parse(EDIFile file) throws IOException, FormatException, EDIValidationException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public EDIFile create(List<LoadEntity> entities, Map<String, Object> params) throws IOException {
        validateLoad(entities);
        LoadEntity load = entities.get(0);

        String scac = getCarrier().getActualScac();
        String currentDate = getCurrentDateStr();
        String currentDateShort = getCurrentDateShortStr();
        String currentTime = getCurrentTimeStr();

        TreeSet<LoadMaterialEntity> materials = new TreeSet<LoadMaterialEntity>(new Comparator<LoadMaterialEntity>() {
            @Override
            public int compare(LoadMaterialEntity o1, LoadMaterialEntity o2) {
                return o1.getProductDescription().compareTo(o2.getProductDescription());
            }
        });
        materials.addAll(load.getOrigin().getLoadMaterials());

        Integer weight = load.getWeight();
        Integer quantity = load.getPieces();
        BigDecimal totalCost = load.getActiveCostDetail() != null ? load.getActiveCostDetail().getTotalCost() : null;

        String isaId = element(toStr(getDataProvider().getNextIsaId()), 9, "0", true);
        String gsId = element(toStr(getDataProvider().getNextGSId()), 9, "0", true);
        String transactionSetId = element("1", 4, "0", true);
        Context context = new Context(SEGMENT_SEPARATOR, ELEMENT_SEPARATOR, COMPOSITE_ELEMENT_SEPARATOR);
        X12 x12 = new X12(context);

        Loop loopIsa = addInterchangeControlHeader(x12, scac, currentDateShort, currentTime, isaId, isProductionMode(params)); //ISA
        Loop loopGs = addFunctionalGroupHeader(loopIsa, scac, currentDate, currentTime, gsId); //GS
        Loop loopSt = addTransactionSetHeader(loopGs, transactionSetId); //ST
        addBeginningShipmentInformation(loopSt, scac, load); //B2
        addPurpose(loopSt, getShipmentOperationType(params)); //B2A
        addNumbers(loopSt, load); //L11
        addEarliestPickupDate(loopSt, load.getOrigin().getArrivalWindowStart()); //G62
        addBillToContactInformation(loopSt, load.getFreightBillPayTo()); //N1, N3, N4, G61
        addOriginContactInformation(loopSt, load.getOrigin()); //N1, N3, N4, G61
        addDestinationContactInformation(loopSt, load.getDestination()); //N1, N3, N4, G61
        addEquipmentDetails(loopSt, toStr(weight)); //N7
        addOriginStopOff(loopSt, toStr(weight)); //S5
        addNumbers(loopSt, load); //L11
        addEarliestPickupDate(loopSt, load.getOrigin().getArrivalWindowStart()); //G62
        addLatestPickupDate(loopSt, load.getOrigin().getArrivalWindowEnd()); //G62
        addWeightPackQuantityData(loopSt, toStr(weight), toStr(quantity)); //AT8
        addLadingDetails(loopSt, materials); //LAD
        addPickupNotes(loopSt, getPickupNotes(load)); // NTE
        addOriginContactInformation(loopSt, load.getOrigin()); //N1, N3, N4, G61
        addDestinationStopOff(loopSt, toStr(weight)); //S5
        addScheduledDeliveryDate(loopSt, getAvailableDeliveryDate(load.getDestination())); // G62
        addWeightPackQuantityData(loopSt, toStr(weight), toStr(quantity)); //AT8
        addLadingDetails(loopSt, materials); //LAD
        addDeliveryNotes(loopSt, ShipmentUtils.getAggregatedNotes(load.getLtlAccessorials(), false, load.getDestination().getNotes(),
                load.getDeliveryNotes())); // NTE
        addDestinationContactInformation(loopSt, load.getDestination()); //N1, N3, N4, G61
        addMaterials(loopSt, materials); //L5
        addWeightPackQuantityData(loopSt, toStr(weight), toStr(quantity)); //AT8
        addTotalWeightAndCharges(loopSt, toStr(weight), toStr(quantity), totalCost); //L3
        addTransactionSetTrailer(loopGs, loopSt, transactionSetId); //SE
        addFunctionalGroupTrailer(loopIsa, gsId); //GE
        addInterchangeControlTrailer(x12, isaId); //IEA
        return getEDIFile(getFileName(load.getNumbers().getBolNumber()), scac, x12);
    }

    private Date getAvailableDeliveryDate(LoadDetailsEntity destination) {
        Date date = destination.getDeparture() == null ? destination.getScheduledArrival() : destination.getDeparture();
        if (destination.getArrivalWindowEnd() != null && date != null) {
            Calendar arrivalWindowEndCalendar = Calendar.getInstance(Locale.US);
            arrivalWindowEndCalendar.setTime(destination.getArrivalWindowEnd());
            int hour = arrivalWindowEndCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = arrivalWindowEndCalendar.get(Calendar.MINUTE);
            Calendar dateCalendar = Calendar.getInstance(Locale.US);
            dateCalendar.set(Calendar.HOUR_OF_DAY, hour);
            dateCalendar.set(Calendar.MINUTE, minute);
            return dateCalendar.getTime();
        }
        return date;
    }

    private void validateLoad(List<LoadEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("EDI 204 must contain at least one Shipment");
        }
        if (entities.size() != 1) {
            throw new IllegalArgumentException(String.format("EDI 204 must contain just one Shipment, found %s", entities.size()));
        }
        LoadEntity load = entities.get(0);
        if (load.getOrigin() == null) {
            throw new IllegalArgumentException(String.format("Shipment Origin cannot be null. (id: %s, bol: %s)", load.getId(), load
                    .getNumbers().getBolNumber()));
        }
        if (load.getDestination() == null) {
            throw new IllegalArgumentException(String.format("Shipment Destination cannot be null. (id: %s, bol: %s)", load.getId(), load
                    .getNumbers().getBolNumber()));
        }
        if (load.getOrigin().getLoadMaterials() == null || load.getOrigin().getLoadMaterials().isEmpty()) {
            throw new IllegalArgumentException(String.format(
"Shipment materials list cannot be empty. (id: %s, bol: %s)", load.getId(), load
                    .getNumbers().getBolNumber()));
        }
    }

    private String getShipmentOperationType(Map<String, Object> params) {
        String shipmentOperationTypeCode = null;
        if (params != null && params.containsKey(LoadTenderServiceImpl.EDI_OPERATION_TYPE_KEY)) {
            ShipmentOperationType shipmentOperationType = (ShipmentOperationType) params.get(LoadTenderServiceImpl.EDI_OPERATION_TYPE_KEY);
            if (shipmentOperationType != null) {
                shipmentOperationTypeCode = shipmentOperationType.getCode();
            }
        }
        return shipmentOperationTypeCode;
    }

    private boolean isProductionMode(Map<String, Object> params) {
        if (params != null && params.containsKey(LoadTenderServiceImpl.PRODUCTION_MODE_KEY)) {
            Boolean productionMode = (Boolean) params.get(LoadTenderServiceImpl.PRODUCTION_MODE_KEY);
            return productionMode != null && productionMode;
        }
        return false;
    }

    private String getPickupNotes(LoadEntity load) {
        StringBuilder result = new StringBuilder();
        if (hasHazmat(load.getOrigin().getLoadMaterials())) {
            result.append(HAZMAT_WARNING_MESSAGE);
        }
        result.append(ShipmentUtils.getAggregatedNotes(load.getLtlAccessorials(), true, load.getOrigin().getNotes(),
                load.getSpecialInstructions()));
        return result.toString().trim();
    }

    private boolean hasHazmat(Set<LoadMaterialEntity> materials) {
        for (LoadMaterialEntity material : materials) {
            if (material.isHazmat()) {
                return true;
            }
        }
        return false;
    }

    /*
     * ISA
     */
    private Loop addInterchangeControlHeader(Loop loop, String scac, String currentDateShort, String currentTime, String isaId,
                                             boolean productionMode) {
        return addSegment(loop, "ISA", "00", element(10), "00", element(10), "ZZ", element(PLS_SENDER_ID, 15), "02", element(scac, 15),
                element(currentDateShort, 6), element(currentTime, 4), "U", "00401", isaId, "0", productionMode ? "P" : "T", "^");
    }

    /*
     * GS
     */
    private Loop addFunctionalGroupHeader(Loop loop, String scac, String currentDate, String currentTime, String gsId) {
        return addSegment(loop, "GS", "SM", element(PLS_SENDER_ID, 2, 15), element(scac, 2, 15), element(currentDate, 8),
                element(currentTime, 4, 8), gsId, "X", "004010");
    }

    /*
     * ST
     */
    private Loop addTransactionSetHeader(Loop loop, String transactionSetId) {
        return addSegment(loop, "ST", EDITransactionSet._204.getId(), transactionSetId);
    }

    /*
     * B2
     */
    private Loop addBeginningShipmentInformation(Loop loop, String scac, LoadEntity load) {
        return addSegment(loop, "B2", null, element(scac, 2, 4), null, element(load.getNumbers().getBolNumber(), 0, 30), null,
                element(load.getPayTerms().getPaymentTermsEdiCode(), 2), "B");
    }

    /*
     * B2A
     */
    private Loop addPurpose(Loop loop, String operationType) {
        return addSegment(loop, "B2A", element(operationType, 2), "LT");
    }

    /*
     * L11
     */
    private Loop addNumbers(Loop loop, LoadEntity load) {
        addSegment(load.getOrganization() != null && isNotBlank(load.getOrganization().getEdiAccount()), loop, "L11",
                element(load.getOrganization().getEdiAccount(), 1, 30),
                element(getQualifier(Qualifiers.CLIENT_NUMBER), 2, 3));
        addSegment(isNotBlank(load.getNumbers().getBolNumber()), loop, "L11", element(load.getNumbers().getBolNumber(), 1, 30),
                element(getQualifier(Qualifiers.BOL_NUMBER), 2, 3));
        addSegment(isNotBlank(load.getNumbers().getRefNumber()), loop, "L11", element(load.getNumbers().getRefNumber(), 1, 30),
                element(getQualifier(Qualifiers.SHIPPER_REFERENCE_NUMBER), 2, 3));
        addSegment(isNotBlank(load.getNumbers().getPuNumber()), loop, "L11", element(load.getNumbers().getPuNumber(), 1, 30),
                element(getQualifier(Qualifiers.PICKUP_REFERENCE_NUMBER), 2, 3));
        addSegment(isNotBlank(load.getNumbers().getPoNumber()), loop, "L11", element(load.getNumbers().getPoNumber(), 1, 30),
                element(getQualifier(Qualifiers.PO_NUMBER), 2, 3));
        return null;
    }

    /*
     * G62
     */
    private Loop addEarliestPickupDate(Loop loop, Date date) {
        return addDate(loop, getQualifier(Qualifiers.EARLIEST_PICKUP_DATE), "I", date);
    }

    /*
     * G62
     */
    private Loop addLatestPickupDate(Loop loop, Date date) {
        return addDate(loop, getQualifier(Qualifiers.LATEST_PICKUP_DATE), "K", date);
    }

    /*
     * G62
     */
    private Loop addScheduledDeliveryDate(Loop loop, Date date) {
        return addDate(loop, getQualifier(Qualifiers.SCHEDULED_DELIVERY_DATE), "X", date);
    }

    /*
     * G62
     */
    private Loop addDate(Loop loop, String qualifier, String code, Date date) {
        return addSegment(loop, "G62", element(qualifier, 2), element(toDateStr(date), 8), code, element(toTimeStr(date), 4, 8));
    }

    /*
     * N1, N3, N4, G61 (Bill To)
     */
    private Loop addBillToContactInformation(Loop loop, FreightBillPayToEntity billPayTo) {
        if (billPayTo == null) {
            return null;
        }
        PhoneEntity phoneEntity = billPayTo.getPhone();
        String phone = phoneEntity != null ? (phoneEntity.getCountryCode() + phoneEntity.getAreaCode() + phoneEntity.getNumber()) : null;
        return addContactInformation(loop, getQualifier(Qualifiers.BILL_TO_ADDRESS), billPayTo.getCompany(), billPayTo.getAccountNum(),
                billPayTo.getAddress(), billPayTo.getContactName(), billPayTo.getEmail(), phone);
    }

    /*
     * N1, N3, N4, G61 (Origin)
     */
    private Loop addOriginContactInformation(Loop loop, LoadDetailsEntity loadDetails) {
        return addLoadDetailsContactInformation(loop, loadDetails, getQualifier(Qualifiers.SHIPPER_ADDRESS));
    }

    /*
     * N1, N3, N4, G61 (Destination)
     */
    private Loop addDestinationContactInformation(Loop loop, LoadDetailsEntity loadDetails) {
        return addLoadDetailsContactInformation(loop, loadDetails, getQualifier(Qualifiers.CONSIGNEE_ADDRESS));
    }

    /*
     * N1, N3, N4, G61 (Origin/Destination)
     */
    private Loop addLoadDetailsContactInformation(Loop loop, LoadDetailsEntity loadDetails, String qualifier) {
        return addContactInformation(loop, qualifier, loadDetails.getContact(), StringUtils.EMPTY, loadDetails.getAddress(),
                loadDetails.getContactName(), loadDetails.getContactEmail(), refineString(loadDetails.getContactPhone(), PHONE_FORBIDDEN_SYMBOLS));
    }

    /*
     * N1, N3, N4, G61
     */
    private Loop addContactInformation(Loop loop, String qualifier, String name, String accountNum, AddressEntity address,
            String contact, String email, String phone) {
        String contactBy = "TE";
        String contactInfo = null;
        String g6101 = "CN";
        if (StringUtils.isNotBlank(phone)) {
            contactInfo = phone;
        } else if (StringUtils.isNotBlank(email)) {
            contactBy = "EM";
            contactInfo = email;
        }
        if (isNotBlank(accountNum)) {
            addSegment(loop, "N1", element(qualifier, 2, 3), element(name, 0, 60), "ZZ",
                    element(StringUtils.trimToEmpty(accountNum), 2, 80));
        } else {
            addSegment(loop, "N1", element(qualifier, 2, 3), element(name, 0, 60));
        }
        if (isNotBlank(address.getAddress2())) {
            addSegment(loop, "N3", element(address.getAddress1(), 1, 55), element(address.getAddress2(), 0, 55));
        } else {
            addSegment(loop, "N3", element(address.getAddress1(), 1, 55));
        }
        addSegment(loop, "N4", element(address.getCity(), 0, 30), element(address.getStateCode(), 0, 2), element(address.getZip(), 0, 15),
                element(address.getCountry().getId(), 0, 3));
        switch (qualifier) {
        case "BT":
            g6101 = "BI";
            break;
        case "CN":
            g6101 = "CN";
            break;
        case "SH":
            g6101 = "SH";
            break;
        default:
        break;
        }
        if (isNotBlank(contactInfo)) {
            addSegment(loop, "G61", g6101, element(contact, 0, 60), contactBy, element(contactInfo, 0, 80));
        }
        return null;
    }

    /*
     * N7
     */
    private Loop addEquipmentDetails(Loop loop, String weight) {
        return addSegment(loop, "N7", null, "None", weight, "N");
    }

    /*
     * S5 (Origin)
     */
    private Loop addOriginStopOff(Loop loop, String weight) {
        return addSegment(loop, "S5", getQualifier(Qualifiers.ORIGIN_STOPOFF), "LD", element(weight, 1, 10), "L");
    }

    /*
     * S5 (Destination)
     */
    private Loop addDestinationStopOff(Loop loop, String weight) {
        return addSegment(loop, "S5", getQualifier(Qualifiers.DESTINATION_STOPOFF), "UL", element(weight, 1, 10), "L");
    }

    /*
     * AT8
     */
    private Loop addWeightPackQuantityData(Loop loop, String weight, String quantity) {
        return addSegment(loop, "AT8", "G", "L", element(weight, 1, 10), element(quantity, 1, 7));
    }

    /*
     * LAD
     */
    private Loop addLadingDetails(Loop loop, Set<LoadMaterialEntity> materials) {
        for (LoadMaterialEntity material : materials) {
            String packageType = material.getPackageType() != null ? checkOnNstPackageType(material.getPackageType()
                    .getId()) : null;
            String quantity = StringUtils.defaultIfBlank(material.getQuantity(), "1");
            addSegment(loop, "LAD", packageType, element(quantity, 1, 7), "L", element(toStr(material.getWeight()), 1, 8),
                    null, null, null, null, null, null, null, null, element(material.getProductDescription(), 1, 50));
        }
        return null;
    }

    /*
     * NTE (Pickup)
     */
    private Loop addPickupNotes(Loop loop, String notes) {
        return addNotes(loop, getQualifier(Qualifiers.PICKUP_NOTES), notes);
    }

    /*
     * NTE (Delivery)
     */
    private Loop addDeliveryNotes(Loop loop, String notes) {
        return addNotes(loop, getQualifier(Qualifiers.DELIVERY_NOTES), notes);
    }

    /*
     * NTE
     */
    private Loop addNotes(Loop loop, String qualifier, String notes) {
        return addSegment(isNotBlank(notes), loop, "NTE", qualifier, element(adjustLength(replaceLineFeed(notes), 80), 1, 80));
    }

    /*
     * L5
     */
    private Loop addMaterials(Loop loop, Set<LoadMaterialEntity> materials) {
        int num = 0;
        for (LoadMaterialEntity material : materials) {
            String packageType = material.getPackageType() != null ? checkOnNstPackageType(material.getPackageType()
                    .getId()) : "";
            addSegment(loop, "L5", element(toStr(++num), 1, 3), element(material.getProductDescription(), 1, 50), null, null,
                    material.getPackageType() != null ? element(packageType, 2, 5) : null, null, null, "U",
                    element(material.getCommodityClass().getDbCode(), 1, 30));
        }
        return null;
    }

    /*
     * L3
     */
    private Loop addTotalWeightAndCharges(Loop loop, String weight, String quantity, BigDecimal totalCost) {
        return addSegment(loop, "L3", element(weight, 1, 10), "N", element(toStr(totalCost), 1, 12), "FL",
                element(toCurrencyStr(totalCost, true), 1, 12), null, null, null, null, null, element(quantity, 1, 12), "L");
    }

    /*
     * SE
     */
    private Loop addTransactionSetTrailer(Loop loop, Loop loopSt, String transactionSetId) {
        int loopStChildSegmentsCount = loopSt.getLoops().size();
        loopStChildSegmentsCount += 2; //Add ST and SE segment themselves
        return addSegment(loop, "SE", element(toStr(loopStChildSegmentsCount), 1, 10), transactionSetId);

    }

    /*
     * GE
     */
    private Loop addFunctionalGroupTrailer(Loop loop, String gsId) {
        return addSegment(loop, "GE", "1", gsId);
    }

    /*
     *IEA
     */
    private Loop addInterchangeControlTrailer(Loop loop, String isaId) {
        return addSegment(loop, "IEA", "1", isaId);
    }

    private String getFileName(String loadIdentifier) {
        return String.format("EDI204_%s_%s.txt", loadIdentifier.replaceAll("['\\s]", ""), toDateTimeStr(new Date()));
    }

    private String checkOnNstPackageType(String packageType) {
        if ("NST".equalsIgnoreCase(packageType)) {
            return "PLT";
        } else {
            return packageType;
        }
    }

    /**
     * PLS references enumeration for EDI 204 qualifiers.
     */
    enum Qualifiers implements EDIQualifiersPlsReference {
        BOL_NUMBER("BM"),
        PO_NUMBER("PO"),
        CLIENT_NUMBER("23"),
        PICKUP_REFERENCE_NUMBER("P8"),
        SHIPPER_REFERENCE_NUMBER("CR"),
        SHIPPER_ADDRESS("SH"),
        CONSIGNEE_ADDRESS("CN"),
        BILL_TO_ADDRESS("BT"),
        PAYER_ADDRESS(""),
        SHIP_FROM_ADDRESS(""),
        ORIGIN_STOPOFF("1"),
        DESTINATION_STOPOFF("2"),
        EARLIEST_PICKUP_DATE("EP"),
        LATEST_PICKUP_DATE("LP"),
        SCHEDULED_DELIVERY_DATE("70"),
        PICKUP_NOTES("LOI"),
        DELIVERY_NOTES("DEL");

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
