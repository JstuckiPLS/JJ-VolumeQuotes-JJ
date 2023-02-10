package com.pls.scheduler.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pls.extint.service.DocumentApiService;
import com.pls.extint.shared.DocumentRequestVO;
import com.pls.scheduler.util.EnableScheduler;
import com.pls.shipment.domain.bo.ShipmentMissingPaperworkBO;
import com.pls.shipment.service.ShipmentService;

/**
 * Service that find shipments with missing paperwork and carrier provides document api (configured in api types) and pulls documents from carrier
 * website using the API. Works accordingly to schedule.
 * 
 * @author Pavani Challa
 */
@Service
@EnableScheduler
public class ShipmentDocumentsScheduler {
     private static final int ONE_HOUR_MILLIS = 60 * 60 * 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShipmentDocumentsScheduler.class);

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private DocumentApiService documentApiService;

    /**
     * Scheduled method that runs each #ONE_HOUR_MILLIS, find and download documents using carrier api.
     */
    @Scheduled(fixedRate = ONE_HOUR_MILLIS)
    public void getDocumentsForShipments() {
        LOGGER.info("Getting shipment documents from carriers web sites");

        List<ShipmentMissingPaperworkBO> shipments = null;
        try {
            shipments = shipmentService.getShipmentsWithMissingReqPaperwork(null);

            for (ShipmentMissingPaperworkBO shipment : shipments) {
                documentApiService.getDocuments(buildRequest(shipment));
            }
        } catch (Exception e) {
            LOGGER.error("Can't get shipment documents from carriers web sites", e);
        }

        LOGGER.info("Finished getting shipment documents from carriers web sites. {} shipments were processed", shipments == null ? 0
                : shipments.size());
    }

    private DocumentRequestVO buildRequest(ShipmentMissingPaperworkBO shipment) {
        DocumentRequestVO request = new DocumentRequestVO();
        request.setLoadId(shipment.getLoadId());
        request.setCarrierOrgId(shipment.getCarrierOrgId());
        request.setShipperOrgId(shipment.getShipperOrgId());
        request.setCarrierScac(shipment.getCarrierScac());
        request.setBol(shipment.getBol());
        request.setCarrierRefNum(shipment.getCarrierRefNum());
        request.setShipperRefNum(shipment.getShipperRefNum());
        request.setOriginZip(shipment.getOriginZip());
        request.setDestZip(shipment.getDestZip());
        request.setPickupDate(shipment.getPickupDate());

        return request;
    }
}
