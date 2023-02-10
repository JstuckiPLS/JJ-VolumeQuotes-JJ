package com.pls.scheduler.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.scheduler.util.EnableScheduler;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.impl.BillingAuditService;

/**
 * Scheduler that archived old unmatched vendor bills.
 *
 * @author Sergey Kirichenko
 */
@Service
@EnableScheduler
public class VendorBillScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VendorBillScheduler.class);

    private static final int ONE_HOUR_MILLIS = 60 * 60 * 1000;

    @Autowired
    private CarrierInvoiceService carrierInvoiceService;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private BillingAuditService billingAuditService;

    /**
     * Scheduled method that runs each midnight, find and generate shipment alerts.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void archiveOldUnmatched() {
        LOGGER.info("Archiving outdated unmatched vendor bills");
        try {
            carrierInvoiceService.archiveOldUnmatched();
        } catch (Exception e) {
            LOGGER.error("Can't archive outdated unmatched vendor bills", e);
        }
        LOGGER.info("Finished archiving outdated unmatched vendor bills");
    }

    /**
     * Update shipment financial status if VendorBill attachment to Load less than seven days.
     */
    @Scheduled(fixedRate = ONE_HOUR_MILLIS)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void updateShipmentFinancialStatus() {
        LOGGER.info("Update shipment financial status");
        try {
            List<LoadEntity> loadWithVendorBill = ltlShipmentDao.getLoadForMatchedVendorBill();
            for (LoadEntity loadEntity : loadWithVendorBill) {
                loadEntity.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
                billingAuditService.updateBillingAuditReasonForLoad(loadEntity, ShipmentFinancialStatus.NONE);
            }
        } catch (Exception e) {
            LOGGER.error("Can't update shipment financial status", e);
        }
        LOGGER.info("Finished update shipment financial status");
    }

}
