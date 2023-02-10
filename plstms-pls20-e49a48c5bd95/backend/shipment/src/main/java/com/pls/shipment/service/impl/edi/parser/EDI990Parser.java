package com.pls.shipment.service.impl.edi.parser;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pb.x12.Cf;
import org.pb.x12.FormatException;
import org.pb.x12.Loop;

import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.EDIValidationException;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.edi.parser.EDIQualifiersPlsReference;
import com.pls.shipment.service.impl.edi.parser.enums.EDI990Element;

/**
 * EDI Parser for X12 990 - Transportation Carrier Shipment Status Message.
 *
 * @author Alexander Nalapko
 */
public class EDI990Parser extends AbstractEDIParser<LoadTrackingEntity> {

    public static final String IDENTIFIERS = "B1_Identifiers";
    public static final String REFERENCE_IDENTIFIERS = "N9_ReferenceIdentifiers";
    public static final String EQUIPMENT = "N7_Equipment";

    // all carriers wont use K1 segment. Some carriers use V9 segment.
    public static final String REMARKS = "K1_Remarks";
    public static final String EVENT_DETAIL = "V9_EventDetail";
    public static final String PICKUP_CONFIRMATION = "N9_Carier's_Pickup_Confirmation";

    private static final Long EDI990 = 990L;

    private static final String TIMEZONE_CODE = "ET";

    private Map<String, String> loadTrackingStatus;
    private Map<String, String> loadReasonTrackingStatus;

    /**
     * Constructor.
     *
     * @param carrier
     *            EDI carrier
     * @param dataProvider
     *            data provider for EDI parser
     */
    public EDI990Parser(CarrierEntity carrier, DataProvider dataProvider) {
        super(carrier, dataProvider);
        loadTrackingStatus = getDataProvider().getLoadTrackingStatusTypes();
        loadReasonTrackingStatus = getDataProvider().getLoadReasonTrackingStatusTypes();

    }

    @Override
    protected Cf getTransactionConfig() {
        Cf transactionConfig = new Cf(TRANSACTION_SET_HEADER, "ST", EDITransactionSet._990.getId(), 1);
        transactionConfig.addChild(IDENTIFIERS, "B1");
        transactionConfig.addChild(REFERENCE_IDENTIFIERS, "N9", getQualifier(Qualifiers.PRO_NUMBER), 1);
        transactionConfig.addChild(REFERENCE_IDENTIFIERS, "N9", getQualifier(Qualifiers.PICKUP_CONFIRMATION), 1);
        transactionConfig.addChild(EQUIPMENT, "N7");
        transactionConfig.addChild(REMARKS, "K1");
        transactionConfig.addChild(EVENT_DETAIL, "V9");
        return transactionConfig;
    }

    @Override
    public EDIParseResult<LoadTrackingEntity> parse(EDIFile file) throws IOException, FormatException,
            EDIValidationException {

        EDIParseResult<LoadTrackingEntity> parseResult = new EDIParseResult<LoadTrackingEntity>(file);
        List<LoadTrackingEntity> loadTrackingEntities = new ArrayList<LoadTrackingEntity>();
        List<Loop> trackingList = getX12(file).findLoop(TRANSACTION_SET_HEADER);
        for (Loop tracking : trackingList) {
            LoadTrackingEntity loadTracking = new LoadTrackingEntity();
            loadTracking.setTrackingDate(new Date());
            loadTracking.setTimezoneCode(TIMEZONE_CODE);
            loadTracking.setCreatedBy(getDataProvider().getEdiUserId());
            loadTracking.setCarrier(getCarrier());
            loadTracking.setSource(EDI990);
            loadTracking.setScac(getElementValue(tracking, EDI990Element.SCAC).getValue());
            loadTracking.setBol(getElementValue(tracking, EDI990Element.BOL).getValue());
            loadTracking.setPro(getElementValue(tracking, EDI990Element.PRO_NUMBER).getValue());
            String pickupConfirmation = getElementValue(tracking, EDI990Element.PICKUP_CONFIRMATION).getValue();
            loadTracking.setPickupConfirmation(pickupConfirmation);

            String statusCode = StringUtils.trimToNull(getElementValue(tracking, EDI990Element.STATE).getValue());

            String  k1Code = getElementValue(tracking, EDI990Element.K1_MESSAGE_CODE).getValue();
            if (isNotEmpty(k1Code)) {
                loadTracking.setStatusReasonCode(k1Code);
            }

            String v9Code = getElementValue(tracking, EDI990Element.V9_STATUS_REASON_CODE).getValue();
            if (isNotEmpty(v9Code)) {
                loadTracking.setStatusReasonCode(v9Code);
            }

            String eventCode = getElementValue(tracking, EDI990Element.V9_EVENT_CODE).getValue();
            if (isNotEmpty(eventCode)) {
                switch (EventCode.setValue(eventCode)) {
                case ACCEPTED:
                    statusCode = ActionCode.RESERVATION_ACCEPTED.value;
                    break;
                case DECLINED:
                    statusCode = ActionCode.RESERVATION_CANCELLED.value;
                    break;
                default:
                    break;
                }
            }

            loadTracking.setStatusCode(statusCode);

            loadTracking.setFreeMessage(addFreeFormMessage(statusCode, k1Code,
                    getElementValue(tracking, EDI990Element.K1_MESSAGE).getValue(), v9Code, pickupConfirmation));

            loadTrackingEntities.add(loadTracking);
        }
        parseResult.setParsedEntities(loadTrackingEntities);
        parseResult.setStatus(EDIParseResult.Status.SUCCESS);
        return parseResult;
    }

    @Override
    public EDIFile create(List<LoadTrackingEntity> entities, Map<String, Object> params) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    private String addFreeFormMessage(String statusCode, String k1Code, String k1Message, String v9Code,
            String pickupConfirmation) {
        StringBuilder result = new StringBuilder();
        result.append(statusCode);
        result.append(':');
        result.append(loadTrackingStatus.get(statusCode));
        result.append(" by carrier.");
        if (isNotEmpty(k1Code)) {
            result.append(' ');
            result.append(loadReasonTrackingStatus.get(k1Code));
        }
        if (isNotEmpty(k1Message)) {
            result.append(" - ");
            result.append(k1Message);
            result.append('.');
        }
        if (isNotEmpty(v9Code)) {
            result.append(' ');
            result.append(loadReasonTrackingStatus.get(v9Code));
            result.append('.');
        }
        if (isNotEmpty(pickupConfirmation)) {
            result.append(" Carrier Pickup Confirmation #: ");
            result.append(pickupConfirmation);
            result.append('.');
        }
        return result.toString();
    }

    /**
     * PLS references enumeration for EDI 990 qualifiers. N901 Segment.
     */
    enum Qualifiers implements EDIQualifiersPlsReference {

        PRO_NUMBER("CN"),
        PICKUP_CONFIRMATION("P8");

        private String defaultValue;

        Qualifiers(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return this.name();
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    /**
     * PLS references enumeration for EDI 990 Action Code. B101 Segment.
     */
    enum ActionCode {

        RESERVATION_ACCEPTED("A"),
        CONDITIONAL_ACCEPTANCE("B"),
        COUNTER_PROPOSAL_MADE("C"),
        RESERVATION_CANCELLED("D"),
        RESERVATION_DECLINED("E"),
        NEW("N"),
        DELETE("R"),
        SPLIT_BOOKING("S"),
        CHANGE("U"),
        CHANGE_IN_VESSEL("V"),
        DEFAULT("");

        private String value;

        ActionCode(String value) {
            this.value = value;
        }

        /**
         * Compare string value.
         * 
         * @param value string
         * @return Boolean
         */
        public Boolean equalsValue(String value) {
            return this.value.equals(value);
        }

        /**
         * Set string value.
         * 
         * @param value string
         * @return ActionCode
         */
        public static ActionCode setValue(String value) {
            for (ActionCode code : values()) {
                if (code.equalsValue(value)) {
                    return code;
                }
            }
            return ActionCode.DEFAULT;
        }
    }

    /**
     * PLS references enumeration for EDI 990 Event Code. V901 Segment.
     */
    enum EventCode {

        ACCEPTED("ACC"),
        DECLINED("DEC"),
        DEFAULT("");

        private String value;

        EventCode(String value) {
            this.value = value;
        }

        /**
         * Compare string value.
         * 
         * @param str string
         * @return Boolean
         */
        public Boolean equalsValue(String str) {
            return value.equals(str);
        }

        /**
         * Set string value.
         * 
         * @param value string
         * @return ActionCode
         */
        public static EventCode setValue(String value) {
            for (EventCode code : values()) {
                if (code.equalsValue(value)) {
                    return code;
                }
            }
            return EventCode.DEFAULT;
        }
    }
}
