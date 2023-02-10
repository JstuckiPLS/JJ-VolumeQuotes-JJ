package com.pls.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pls.scheduler.util.EnableScheduler;
import com.pls.shipment.service.ShipmentAlertService;

/**
 * Service find shipments and generate minute based alerts ('30M', 'TDY', 'MSD', 'NDL') accordingly to them,
 * for user to be notified about state of shipment. Works accordingly to schedule.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Service
@EnableScheduler
public class ShipmentTimeAlertsMinuteBasedScheduler {
    private static final int ONE_MINUTE_MILLIS = 60 * 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShipmentTimeAlertsMinuteBasedScheduler.class);

    @Autowired
    private ShipmentAlertService shipmentAlertService;

    /**
     * Scheduled method that runs each #ONE_MINUTE_MILLIS, find and generate shipment alerts of '30M' type.
     */
    @Scheduled(fixedRate = ONE_MINUTE_MILLIS)
    public void findShipmentFor30MAlerts() {
        LOGGER.info("Finding and generating shipment 30M alerts");
        try {
            shipmentAlertService.generateMinuteBasedShipmentAlerts();
        } catch (Exception e) {
            LOGGER.error("Can't find and generate shipment 30M alerts", e);
        }
        LOGGER.info("Finished finding and generating shipment 30M alerts");
    }
}
