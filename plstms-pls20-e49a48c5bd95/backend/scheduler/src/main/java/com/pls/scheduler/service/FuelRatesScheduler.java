package com.pls.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.ltlrating.service.LtlFuelService;
import com.pls.scheduler.util.EnableScheduler;

/**
 * Scheduler which starts receiving Fuel Rates from EIA.GOV every Tuesday at 23:00.
 * 
 * @author Artem Arapov
 *
 */

@Service
@EnableScheduler
public class FuelRatesScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FuelRatesScheduler.class);

    private static final String EVERY_MONDAY_NIGHT = "0 0 23 ? * MON";

    @Autowired
    private LtlFuelService service;

    /**
     * Receiving Fuel Rates.
     * 
     * @throws Exception thrown if any communication or reading errors occur.
     */
    @Scheduled(cron = EVERY_MONDAY_NIGHT)
    @Transactional(rollbackFor = Exception.class)
    public void receiveFuelRates() throws Exception {
        LOGGER.info("Recieving Fuel Rates From EIA.GOV");
        try {
            service.receiveRegionsFuelRates();
        } catch (Exception e) {
            LOGGER.error("Can't recieve Fuel Rates From EIA.GOV", e);
        }
        LOGGER.info("Finished recieving Fuel Rates From EIA.GOV");
    }
}
