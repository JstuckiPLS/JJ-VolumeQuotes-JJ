package com.pls.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pls.documentmanagement.service.DocumentService;
import com.pls.scheduler.util.EnableScheduler;

/**
 * Scheduler that perform operations related to temp documents.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Service
@EnableScheduler
public class TempDocumentsScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TempDocumentsScheduler.class);

    private static final int REMOVE_AFTER_COUNT_DAYS = 2;

    @Autowired
    private DocumentService documentService;

    /**
     * Scheduled clear of stale data. Runs each midnight.
     * 
     * Will delete data that were created {@link #REMOVE_AFTER_COUNT_DAYS} days ago
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearStaleLtlBinaryData() {
        LOGGER.info("Removing outdated temporary documents");
        try {
            documentService.deleteStaleTempDocuments(REMOVE_AFTER_COUNT_DAYS);
        } catch (Exception e) {
            LOGGER.error("Can't remove outdated temporary documents", e);
        }
        LOGGER.info("Finished removing outdated temporary documents");
    }
}
