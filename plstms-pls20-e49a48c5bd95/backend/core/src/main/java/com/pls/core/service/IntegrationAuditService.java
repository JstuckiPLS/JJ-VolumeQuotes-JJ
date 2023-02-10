package com.pls.core.service;

import java.util.Date;
import java.util.List;

import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.integration.AuditBO;

/**
 * Service interface for calling right DAO functionality in order to add update or modify Integration Audits.
 * 
 * @author Yasaman Honarvar
 *
 */
public interface IntegrationAuditService {

    /**
     * Calls the DAO to Save the Integration Audits to the INT_AUDIT and INT_AUDIT_DETAILS database.
     * 
     * @param audit
     *            the audit record being saved.
     * @return saved entity
     */
    AuditEntity saveLog(AuditEntity audit);

    /**
     * calls DAO to get the AuditEntity from database.
     * 
     * @param id
     *            the audit record being saved.
     * @return the Audit entity matching the parameter id.
     */
    AuditBO getLogById(Long id);

    /**
     * This function calls the DAO to search for particular audit logs.
     * 
     * @param dateFrom
     *            starting date of search.
     * @param dateTo
     *            end date of search.
     * @param bolNumber
     *            BOL number of the shipment that the log belongs to.
     * @param shipmentNumber
     *            shipment number of the shipment that the log belongs to.
     * @param loadId
     *            the load ID of the related load.
     * @param messageType
     *            the message type.
     * @param carrierId
     *            the carrier of the related shipment.
     * @param customerId
     *            the customer of the shipment.
     * @return the list of AuditBO which matched the searched criteria.
     */
    List<AuditBO> getLogs(Date dateFrom, Date dateTo, Long customerId, Long carrierId, String bolNumber, String shipmentNumber, Long loadId,
            EDIMessageType messageType);

    /**
     * This function returns the log detail of a chosen log based on ID.
     * 
     * @param auditId
     *            the ID of the audit we are searching for.
     * @return corresponding audit Detail Entity.
     */
    AuditDetailEntity getLogDetails(Long auditId);

}
