package com.pls.core.dao;

import java.util.Date;
import java.util.List;

import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.integration.AuditBO;

/**
 * The interface of Integration DAO extending the Abstract DAO.
 * 
 * @author Yasaman Palumbo
 *
 */
public interface IntegrationAuditDao extends AbstractDao<AuditEntity, Long> {

    /**
     * Finds the corresponding detail data to the Integration log which user wants to examine.
     * 
     * @param auditId
     *            The selected logs ID.
     * @return Full Details of the selected Log.
     */
    AuditDetailEntity getLogDetailsByAuditId(Long auditId);

    /**
     * This function looks for a particular audit log in the database.
     * 
     * @param dateFrom
     *            starting date of search.
     * @param dateTo
     *            end date of search.
     * @param scac
     *            id of the carrier.
     * @param customerId
     *            id of the customer.
     * @param bolNumber
     *            bol number of the shipment that the log belongs to.
     * @param shipmentNumber
     *            shipment number of the shipment that the log belongs to.
     * @param loadId
     *            the load ID of the related load.
     * @param messageType
     *            the message type.
     * @return the list of AuditBO which matched the searched criteria.
     */
    List<AuditBO> getLogsByCriteria(Date dateFrom, Date dateTo, Long customerId, String scac, String bolNumber, String shipmentNumber, Long loadId,
            EDIMessageType messageType);

    /**
     * Get last EDI 204 XML by Load id and SCAC.
     *
     * @param loadId
     *            Id of Load.
     * @param scac
     *            carrier SCAC
     * @return XML of EDI 204.
     */
    String getLastEdi204XMLByLoadIdAndScac(Long loadId, String scac);
}
