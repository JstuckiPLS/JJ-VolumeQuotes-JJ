package com.pls.scheduler.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pls.extint.service.TrackingService;
import com.pls.extint.shared.TrackingRequestVO;
import com.pls.extint.shared.TrackingResponseVO;
import com.pls.scheduler.util.EnableScheduler;
import com.pls.shipment.domain.LoadEventDataEntity;
import com.pls.shipment.domain.LoadEventDataPK;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.bo.ShipmentTrackingBO;
import com.pls.shipment.service.ShipmentEventService;
import com.pls.shipment.service.ShipmentService;

/**
 * Scheduler that tracks the Carrier API for shipments and updates the LOAD_GENERIC_EVENTS.
 * 
 * @author Pavani Challa
 */
@Service
@EnableScheduler
public class ShipmentTrackingScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShipmentTrackingScheduler.class);

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private TrackingService apiService;

    @Autowired
    private ShipmentEventService eventService;

    @Value("${admin.personId}")
    private Long adminUserId;

    private static final String EVENT_TYPE = "CARTRKNOTE";

    /**
     * Runs every half an hour to perform tracking using API.
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void trackShipments() {
        LOGGER.info("Tracking Carrier API for shipments to update load tracking events");

        List<ShipmentTrackingBO> shipments = null;
        try {
            shipments = shipmentService.getShipmentsToTrack();

            for (ShipmentTrackingBO shipment : shipments) {
                TrackingResponseVO response = apiService.getTrackingInformation(buildRequest(shipment));
                if (response != null && response.getNote() != null) {
                    eventService.save(getLoadEvent(shipment.getLoadId(), response.getNote()));
                }

                if (response != null && response.getCurrentStatus() != null && !response.getCurrentStatus().equals(shipment.getLoadStatus())) {
                    shipmentService.updateLoadStatus(response, adminUserId);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Can't track Carrier API for shipments to update load tracking events", e);
        }
        LOGGER.info("Finished tracking Carrier API for shipments to update load tracking events. {} shipments were processed",
                shipments == null ? 0 : shipments.size());
    }

    private TrackingRequestVO buildRequest(ShipmentTrackingBO shipment) {
        TrackingRequestVO request = new TrackingRequestVO();
        request.setLoadId(shipment.getLoadId());
        request.setCarrierOrgId(shipment.getCarrierOrgId());
        request.setShipperOrgId(shipment.getShipperOrgId());
        request.setCarrierScac(shipment.getCarrierScac());
        request.setBol(shipment.getBol());
        request.setCarrierRefNum(shipment.getCarrierRefNum());
        request.setShipperRefNum(shipment.getShipperRefNum());
        request.setPieces(shipment.getPieces());
        request.setWeight(shipment.getWeight());
        request.setPickupDate(shipment.getPickupDate());
        request.setOriginZip(shipment.getOriginZip());
        request.setDestZip(shipment.getDestZip());

        return request;
    }

    private LoadEventEntity getLoadEvent(Long loadId, String note) {
        LoadEventEntity event = new LoadEventEntity();
        event.setLoadId(loadId);
        event.setFailure(false);
        event.setEventTypeCode(EVENT_TYPE);

        LoadEventDataPK dataEntityPK = new LoadEventDataPK();
        dataEntityPK.setOrdinal((byte) 0);
        dataEntityPK.setEvent(event);

        LoadEventDataEntity data = new LoadEventDataEntity();
        data.setEventDataPK(dataEntityPK);
        data.setData(note);
        data.setDataType('S');
        event.setData(Collections.singletonList(data));

        return event;
    }
}
