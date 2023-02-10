package com.pls.shipment.service.impl.edi.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.exception.ApplicationException;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.service.ShipmentAlertService;
import com.pls.shipment.service.edi.EDIService;
import com.pls.shipment.service.edi.handler.EDI214ParseResultHandler;
import com.pls.shipment.service.edi.handler.EDIParseResultHandler;
import com.pls.shipment.service.impl.edi.parser.EDI997Producer;
import com.pls.shipment.service.impl.email.EDIEmailSender;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

/**
 * {@link EDIParseResultHandler} implementation for EDI 214.
 *
 * @author Mikhail Boldinov, 05/03/14
 */
@Service
public class EDI214ParseResultHandlerImpl implements EDI214ParseResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EDI214ParseResultHandlerImpl.class);

    private static final String EDI214 = "214";

    private static final String TIMEZONE_CODE = "ET";

    @Value("${admin.personId}")
    private Long ediUserId;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private LoadTrackingDao loadTrackingDao;

    @Autowired
    private ShipmentEmailSender shipmentEmailSender;

    @Autowired
    private ShipmentAlertService shipmentAlertService;

    @Autowired
    private EDIEmailSender ediEmailSender;

    @Autowired
    private EDI997Producer<LoadTrackingEntity> edi997Producer;

    @Autowired
    EDIService ediService;

    @Override
    public List<Integer> handle(EDIParseResult<LoadTrackingEntity> parseResult) throws Exception {
        List<Integer> unprocessedEntitiesInd = new ArrayList<Integer>();
        List<LoadEntity> savedLoadTracking = new ArrayList<LoadEntity>();
        if (parseResult.getStatus() == EDIParseResult.Status.SUCCESS) {
            try {
                for (LoadTrackingEntity loadTracking : parseResult.getParsedEntities()) {
                    List<LoadEntity> loads = ltlShipmentDao.findShipmentsByScacAndBolNumber(loadTracking.getScac(), loadTracking.getBol());
                    if (loads.size() == 1) {
                        saveLoadTracking(loadTracking, loads.get(0));
                        savedLoadTracking.add(loads.get(0));
                    } else {
                        unprocessedEntitiesInd.add(parseResult.getParsedEntities().indexOf(loadTracking));
                    }
                }
            } catch (Exception e) {
                ediEmailSender.loadTrackingFailed(parseResult.getEdiFile().getName(), e.getMessage(), EDI214, null);
                throw e;
            }
            if (!unprocessedEntitiesInd.isEmpty()) {
                List<String> bolNumbers = new ArrayList<String>();
                for (Integer index : unprocessedEntitiesInd) {
                    bolNumbers.add(parseResult.getParsedEntities().get(index).getBol());
                }
                String errorMsg = String.format("Saving Shipment Tracking failed. Unable to find Shipment by BOL# '%s' for Carrier '%s'",
                        StringUtils.join(bolNumbers, "','"), parseResult.getParsedEntities().get(0).getScac());
                ediEmailSender.loadTrackingFailed(parseResult.getEdiFile().getName(), errorMsg, EDI214, null);
                if (unprocessedEntitiesInd.size() == parseResult.getParsedEntities().size()) {
                    throw new ApplicationException(errorMsg);
                }
            }
        } else {
            ediEmailSender.loadTrackingFailed(parseResult.getEdiFile().getName(), parseResult.getErrorMsg(), EDI214, null);
        }

        LOGGER.info(String.format("Creating EDI 997 in response to EDI 214 file: %s", parseResult.getEdiFile().getName()));
        try {
            EDIFile edi997 = edi997Producer.create(parseResult);
            LOGGER.info(String.format("Sending EDI 997 file: %s in response to EDI 214 file: %s", edi997.getName(),
                    parseResult.getEdiFile().getName()));
            ediService.sendEDIFile(edi997);
            sendTracking(savedLoadTracking);
        } catch (Exception e) {
            LOGGER.error(String.format("Error sending EDI 997 in response to EDI 214 file: %s. Error message: %s",
                    parseResult.getEdiFile().getName(), e.getMessage()), e);
        }
        return unprocessedEntitiesInd;
    }

    private void saveLoadTracking(LoadTrackingEntity loadTracking, LoadEntity load) {
        loadTracking.setLoad(load);
        loadTrackingDao.saveOrUpdate(loadTracking);
        // if the load is already in Confirmed Delivered status, don’t update any information on load from the 214
        // 214 can be for any status but still should not update load with information
        boolean shouldSendLoadStatusChangedNotification = false;
        if (load.getStatus() != ShipmentStatus.DELIVERED) {
            ShipmentStatus oldStatus = load.getStatus();
            shouldSendLoadStatusChangedNotification = updateLoadFromTrackingData(load, loadTracking);
            shipmentEmailSender.sendGoShipTrackingUpdateEmail(load, oldStatus);
        }
        if (shouldSendLoadStatusChangedNotification) {
            shipmentEmailSender.sendLoadStatusChangedNotification(load, load.getStatus());
            shipmentAlertService.processShipmentAlerts(load);
        }
        if (loadTracking.getStatus() != null) {
            shipmentEmailSender.sendLoadDetailsNotification(load, loadTracking.getStatus().getDescription());
        }
    }

    private boolean updateLoadFromTrackingData(LoadEntity load, LoadTrackingEntity loadTracking) {
        if (load.getNumbers().getProNumber() == null) {
            load.getNumbers().setProNumber(loadTracking.getPro());
        }

        String statusCode = loadTracking.getStatusCode();
        //out for delivery
        if (StringUtils.equals(statusCode, "X6")) {
            load.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);
            return true;
        }
        // confirm pickup
        if (StringUtils.equals(statusCode, "CP") || StringUtils.equals(statusCode, "AF")) {
            load.setStatus(ShipmentStatus.IN_TRANSIT);
            if (loadTracking.getDepartureTime() != null) {
                load.getOrigin().setDeparture(loadTracking.getDepartureTime());
            }
            return true;
        }
        // confirm delivery
        if (StringUtils.equals(statusCode, "D1")) {
            load.setStatus(ShipmentStatus.DELIVERED);
            if (loadTracking.getDepartureTime() != null) {
                load.getDestination().setDeparture(loadTracking.getDepartureTime());
            }
            return true;
        }
        return false;
    }

    private void sendTracking(List<LoadEntity> loads) {
        for (LoadEntity load : loads) {
            try {
                LoadTrackingEntity loadTracking = new LoadTrackingEntity();
                loadTracking.setSource(997L);
                loadTracking.setLoad(load);
                loadTracking.setCreatedBy(ediUserId);
                loadTracking.setCarrier(load.getCarrier());
                loadTracking.setPro(load.getNumbers().getProNumber());
                loadTracking.setBol(load.getNumbers().getBolNumber());
                loadTracking.setScac(load.getCarrier().getScac());
                loadTracking.setFreeMessage("997 has been sent");
                loadTracking.setTrackingDate(new Date());
                loadTracking.setTimezoneCode(TIMEZONE_CODE);
                loadTrackingDao.saveOrUpdate(loadTracking);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
