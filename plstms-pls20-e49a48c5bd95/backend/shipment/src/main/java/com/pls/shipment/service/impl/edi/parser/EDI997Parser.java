package com.pls.shipment.service.impl.edi.parser;

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
import com.pls.shipment.domain.edi.EDIElement;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.impl.edi.parser.enums.EDI997Element;
/**
 * EDI Parser for X12 997 - Transportation Carrier Shipment Status Message.
 *
 * @author Alexander Nalapko
 */

public class EDI997Parser extends AbstractEDIParser<LoadTrackingEntity> {

    public static final String IDENTIFIERS = "AK1_Identifiers";
    public static final String TRAILER = "AK9_Trailer";
    public static final String TS_RESPONSE = "AK2_Transaction_Set_Response";

    private static final String TIMEZONE_CODE = "ET";

    private static final Long EDI997 = 997L;

    private Map<String, String> loadTrackingStatus;

    /**
     * Constructor.
     *
     * @param carrier
     *            EDI carrier
     * @param dataProvider
     *            data provider for EDI parser
     */
    public EDI997Parser(CarrierEntity carrier, DataProvider dataProvider) {
        super(carrier, dataProvider);
        loadTrackingStatus = getDataProvider().getLoadTrackingStatusTypes();
    }

    @Override
    protected Cf getTransactionConfig() {
        Cf transactionConfig = new Cf(TRANSACTION_SET_HEADER, "ST", EDITransactionSet._997.getId(), 1);
        Cf identifiers = new Cf(IDENTIFIERS, "AK1", "SM", 1);
        identifiers.addChild(TS_RESPONSE, "AK2");
        identifiers.addChild(TS_RESPONSE, "AK2");
        transactionConfig.addChild(identifiers);
        transactionConfig.addChild(TRAILER, "AK9");
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
            loadTracking.setCreatedBy(getDataProvider().getEdiUserId());
            loadTracking.setCarrier(getCarrier());
            loadTracking.setTrackingDate(new Date());
            loadTracking.setTimezoneCode(TIMEZONE_CODE);
            loadTracking.setSource(EDI997);

            EDIElement gs = getElementValue(tracking, EDI997Element.ID);
            parseResult.getEdiFile().setGs(Long.valueOf(gs.getValue()));

            String statusCode = getElementValue(tracking, EDI997Element.STATUS).getValue();
            loadTracking.setStatusCode(StringUtils.trimToNull(statusCode));

            StringBuilder freeFormMessage = new StringBuilder();
            freeFormMessage.append(statusCode);
            freeFormMessage.append(':');
            freeFormMessage.append(loadTrackingStatus.get(statusCode));
            freeFormMessage.append(" by carrier.");

            loadTracking.setFreeMessage(freeFormMessage.toString());

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

}
