package com.pls.shipment.service.impl.edi.parser;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.pls.shipment.service.impl.edi.parser.enums.EDI214Element;
import com.pls.shipment.service.impl.edi.utils.EDIValidationUtils;

/**
 * EDI Parser for X12 214 - Transportation Carrier Shipment Status Message.
 *
 * @author Mikhail Boldinov, 04/03/14
 */
public class EDI214Parser extends AbstractEDIParser<LoadTrackingEntity> {

    public static final String IDENTIFIERS = "B10_Identifiers";
    public static final String LINE_NUMBER = "LX_LineNumber";
    public static final String TRACKING_DETAILS = "AT7_TrackingDetails";
    public static final String TRACKING_POINT = "MS1_TrackingPoint";
    public static final String REFERENCE_NUMBER = "L11";
    private static final Long EDI214 = 214L;

    /**
     * Constructor.
     *
     * @param carrier      EDI carrier
     * @param dataProvider data provider for EDI parser
     */
    public EDI214Parser(CarrierEntity carrier, DataProvider dataProvider) {
        super(carrier, dataProvider);
    }

    @Override
    protected Cf getTransactionConfig() {
        Cf transactionConfig = new Cf(TRANSACTION_SET_HEADER, "ST", EDITransactionSet._214.getId(), 1);
        transactionConfig.addChild(IDENTIFIERS, "B10");
        transactionConfig.addChild(LINE_NUMBER, "LX");
        transactionConfig.addChild(TRACKING_DETAILS, "AT7");
        transactionConfig.addChild(TRACKING_POINT, "MS1");
        transactionConfig.addChild(REFERENCE_NUMBER, "L11", "23", 2);

        return transactionConfig;
    }

    @Override
    public EDIParseResult<LoadTrackingEntity> parse(EDIFile file) throws IOException, FormatException, EDIValidationException {
        EDIParseResult<LoadTrackingEntity> parseResult = new EDIParseResult<LoadTrackingEntity>(file);
        List<LoadTrackingEntity> loadTrackingEntities = new ArrayList<LoadTrackingEntity>();
        parseResult.setReceiverId(getReceiverId(file));
        List<Loop> trackingList = getX12(file).findLoop(TRANSACTION_SET_HEADER);
        for (Loop tracking : trackingList) {
            EDIElement scac = getElementValue(tracking, EDI214Element.SCAC);
            EDIElement bol = getElementValue(tracking, EDI214Element.BOL);
            EDIElement pro = getElementValue(tracking, EDI214Element.PRO_NUM);
            try {
                EDIValidationUtils.validateElement(getCarrier().getScac(), scac.getValue(), String.format("Invalid SCAC for Load Tracking record. "
                        + "Skipped. BOL: '%s', PRO#: '%s', Expected SCAC: '%s', Actual SCAC: '%s'", bol, pro, getCarrier().getScac(), scac));
            } catch (EDIValidationException e) {
                logger.warn(e.getMessage());
                continue;
            }

            int trackingDetailsCount = getLoopsCount(tracking, TRACKING_DETAILS);
            for (int i = 0; i < trackingDetailsCount; i++) {
                LoadTrackingEntity loadTracking = new LoadTrackingEntity();
                loadTracking.setSource(EDI214);
                loadTracking.setCreatedBy(getDataProvider().getEdiUserId());
                loadTracking.setCarrier(getCarrier());
                loadTracking.setPro(pro.getValue());
                loadTracking.setBol(bol.getValue());
                loadTracking.setScac(scac.getValue());
                loadTracking.setStatusCode(StringUtils.trimToNull(getElementValue(tracking, EDI214Element.STATUS, i).getValue()));
                loadTracking.setStatusReasonCode(StringUtils.trimToNull(getElementValue(tracking, EDI214Element.STATUS_REASON, i).getValue()));
                String trackDateStr = getElementValue(tracking, EDI214Element.TRACK_DATE, i).getValue();
                String trackTimeStr = getElementValue(tracking, EDI214Element.TRACK_TIME, i).getValue();
                Date trackingDate = null;
                if (trackDateStr != null && trackTimeStr != null) {
                    trackingDate = toDateTime(trackDateStr + trackTimeStr);
                }
                if (trackingDate == null) {
                    trackingDate = new Date();
                }
                loadTracking.setDepartureTime(trackingDate);
                loadTracking.setTrackingDate(new Date());
                loadTracking.setTimezoneCode(getElementValue(tracking, EDI214Element.TRACK_TIMEZONE, i).getValue());
                loadTracking.setCity(getElementValue(tracking, EDI214Element.TRACK_CITY, i).getValue());
                loadTracking.setState(getElementValue(tracking, EDI214Element.TRACK_STATE, i).getValue());
                loadTracking.setCountry(getElementValue(tracking, EDI214Element.TRACK_COUNTRY, i).getValue());
                loadTracking.setEdiAccount(getElementValue(tracking, EDI214Element.EDI_ACCOUNT, i).getValue());

                loadTrackingEntities.add(loadTracking);
            }
        }
        parseResult.setParsedEntities(loadTrackingEntities);
        parseResult.setEdiData(getEdiData());
        parseResult.setStatus(EDIParseResult.Status.SUCCESS);
        return parseResult;
    }

    @Override
    protected List<Integer> getIndexesOfTransactionByResultIndexes(Loop gsLoop, List<Integer> resultIndexes) {
        List<Loop> transactions = gsLoop.findLoop(TRANSACTION_SET_HEADER);
        List<Loop> trackingDetails = gsLoop.findLoop(TRACKING_DETAILS);
        if (transactions.size() == trackingDetails.size()) {
            return resultIndexes;
        } else {
            Set<Integer> transactionIndexes = new HashSet<Integer>();
            int trackingIndexes = 0;
            for (int transactionIndex = 0; transactionIndex < transactions.size(); transactionIndex++) {
                trackingDetails = transactions.get(transactionIndex).findLoop(TRACKING_DETAILS);
                for (Iterator<Loop> it = trackingDetails.iterator(); it.hasNext(); it.next()) {
                    if (resultIndexes.contains(trackingIndexes)) {
                        transactionIndexes.add(transactionIndex);
                    }
                    trackingIndexes++;
                }
            }
            return new ArrayList<Integer>(transactionIndexes);
        }
    }

    @Override
    public EDIFile create(List<LoadTrackingEntity> entities, Map<String, Object> params) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
