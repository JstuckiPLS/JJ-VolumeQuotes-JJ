package com.pls.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.invoice.service.processing.AutomaticInvoiceProcessingService;
import com.pls.scheduler.util.EnableScheduler;

/**
 * Scheduler performing automatic invoice processing.
 *
 * @author Sergey Kirichenko
 */
@Service
@EnableScheduler
@Transactional(rollbackFor = Exception.class)
public class AutomaticInvoiceProcessingScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutomaticInvoiceProcessingScheduler.class);

    @Autowired
    private AutomaticInvoiceProcessingService invoiceProcessingService;

    /**
     * Runs every day at 5:00 am to perform weekly invoice processing.
     * 
     * DEV: If you are going to change default processing time, please consider that this value is also set in
     * {@link com.pls.dto.address.InvoicePreferencesDTO#processingTimeInMinutes} and saved in DB.
     */
    @Scheduled(cron = "0 0 5 * * ?", zone = "EST")
    public void processWeeklyInvoices() {
        LOGGER.info("Run processing of weekly invoices");
        try {
            invoiceProcessingService.processInvoicesWeekly();
        } catch (Exception e) {
            LOGGER.error("Can't process weekly invoices", e);
        }
        LOGGER.info("Finished processing of weekly invoices");
    }

    /**
     * Runs every half an hour to perform daily invoice processing.
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void processDailyInvoices() {
        LOGGER.info("Run processing of daily invoices");
        try {
            invoiceProcessingService.processInvoicesDaily();
        } catch (Exception e) {
            LOGGER.error("Can't process daily invoices", e);
        }
        LOGGER.info("Finished processing of daily invoices");
    }
}
