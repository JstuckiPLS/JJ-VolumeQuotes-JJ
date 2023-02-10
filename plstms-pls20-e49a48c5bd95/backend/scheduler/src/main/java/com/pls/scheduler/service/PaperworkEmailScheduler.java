package com.pls.scheduler.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pls.organization.email.service.OrganizationEmailSender;
import com.pls.scheduler.util.EnableScheduler;
import com.pls.shipment.service.ShipmentService;

/**
 * Scheduler that will send paperwork requests for selected shipments to carriers.
 * In case when loads pickup date more than 13 days and load don’t have vendor bill (but not more than 35 days).
 * 
 * @author Dmitry Nikolaenko
 *
 */
@Service
@EnableScheduler
public class PaperworkEmailScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaperworkEmailScheduler.class);

    @Autowired
    private OrganizationEmailSender emailSender;

    @Autowired
    private ShipmentService shipmentService;

    /**
     * Runs on weekdays at 10:00 pm to send paperwork emails.
     */
    @Scheduled(cron = "0 0 22 * * TUE,WED,THU,FRI", zone = "EST")
    public void sendPaperworkEmailsDaily() {
        LOGGER.info("Finding and sending shipments to the carrier");
        emailSender.sendCarrierEmailPaperwork(shipmentService.getPaperworkEmails(1));
    }

    /**
     * Runs every monday night at 10:00 pm to send paperwork emails.
     */
    @Scheduled(cron = "0 0 22 * * MON", zone = "EST")
    public void sendPaperworkEmailsEveryMonday() {
        LOGGER.info("Finding and sending shipments to the carrier");
        emailSender.sendCarrierEmailPaperwork(shipmentService.getPaperworkEmails(3));
    }

    /**
     * Sent paperwork emails once a week on Monday at 1:00 am.
     */
    @Scheduled(cron = "0 0 1 * * MON", zone = "EST")
    public void sendPaperworkEmailsWeekly() {
        LOGGER.info("Finding and sending shipments to the carrier");
        emailSender.sendCarrierEmailPaperwork(shipmentService.getPaperworkEmails(7));
    }
}
