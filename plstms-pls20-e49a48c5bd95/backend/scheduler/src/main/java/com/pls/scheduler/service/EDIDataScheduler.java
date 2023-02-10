package com.pls.scheduler.service;

import com.pls.scheduler.util.EnableScheduler;
import com.pls.shipment.service.edi.EDIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduler which runs processing EDI files.
 *
 * @author Mikhail Boldinov, 04/09/13
 */
@Service
@EnableScheduler
public class EDIDataScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EDIDataScheduler.class);

    private static final int ONE_MINUTE_MILLIS = 60 * 1000;

    @Autowired
    private EDIService ediService;

    /**
     * Scheduled processing EDI data.
     *
     */
    @Scheduled(fixedRate = ONE_MINUTE_MILLIS)
    public void processEDI() {
        LOGGER.info("EDIDataScheduler started processing incoming EDI files");
        try {
            ediService.receiveEDI();
        } catch (Exception e) {
            LOGGER.error("Can't receive EDI", e);
        }
        LOGGER.info("EDIDataScheduler finished processing incoming EDI files");
    }
}
