package com.pls.shipment.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.shipment.dao.BillingAuditReasonCodeDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.domain.LdBillAuditReasonCodeEntity;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.service.audit.LoadEventBuilder;
import com.pls.shipment.service.audit.LoadFinancialStatusTrackingService;

/**
 * The Class LoadTrackingServiceImpl.
 * 
 * @author Sergii Belodon
 */
@Service
@Transactional
public class LoadFinancialStatusTrackingServiceImpl implements LoadFinancialStatusTrackingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFinancialStatusTrackingServiceImpl.class);

    @Autowired
    private LtlShipmentDao dao;

    @Autowired
    private ShipmentEventDao eventDao;

    @Autowired
    private BillingAuditReasonCodeDao billingAuditReasonCodeDao;

    @Override
    public void logLoadFinancialStatusEvent(AuditReasonBO auditReason, ShipmentFinancialStatus loadStatus) {
        if (ShipmentFinancialStatus.ACCOUNTING_BILLING != loadStatus
                && ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD != loadStatus
                && ShipmentFinancialStatus.PRICING_AUDIT_HOLD != loadStatus) {
            return;
        }
        try {
            InvoiceType invoiceType = dao.getLoadInvoiceType(auditReason.getLoadId());
            String codeDescription = null;
            if (auditReason.getCode() != null) {
                LdBillAuditReasonCodeEntity reasonEntity = billingAuditReasonCodeDao.getReasonEntityForReasonCode(auditReason.getCode());
                codeDescription = reasonEntity.getDescription();
            }
            String note = auditReason.getNote();
            if (note == null) {
                note = "";
            }
            LoadEventEntity event = LoadEventBuilder.buildFinancialStatusEvent(auditReason.getLoadId(), invoiceType, loadStatus, codeDescription,
                    note);
            eventDao.saveOrUpdate(event);
        } catch (Exception e) {
            LOGGER.error("Exception while tracking load: ", e);
        }
    }

}
