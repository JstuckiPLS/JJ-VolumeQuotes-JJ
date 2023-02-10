package com.pls.restful.integration;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.exception.ApplicationException;
import com.pls.core.integration.AuditBO;
import com.pls.core.service.IntegrationAuditService;
import com.pls.dtobuilder.util.DateUtils;
import com.pls.extint.service.SterlingService;

/**
 * The Restful web service used for handling data requests from JS.
 * @author Yasaman Honarvar
 *
 */
@Controller
@Transactional
@RequestMapping("/logs")
public class IntegrationAuditResource {

    @Autowired
    protected IntegrationAuditService service;

    @Autowired
    protected SterlingService sterlingService;

    /**
     * Get Audit Entities that are matching the certain ID.
     * @param id searches for a specific log by the log id.
     * @return AuditEntity which matched the given ID.
     */
    @RequestMapping(value = "/getLog", method = RequestMethod.GET)
    @ResponseBody
    public AuditBO getAudits(Long id) {
        return service.getLogById(id);
    }

    /**
     * Get Audit Entities that are matching the certain ID.
     * 
     * @param auditId searches for a specific log by the log id.
     * @param message the modified xml message to be processed.
     * @throws ApplicationException throws exception if we couldn't successfully parse and store data.
     */
    @RequestMapping(value = "/resubmit", method = RequestMethod.POST)
    @ResponseBody
    public void getAudits(@RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "auditId", required = false) Long auditId) throws ApplicationException {
        sterlingService.resubmit(auditId, message);
    }

    /**
     * Get history of all successful invoices.
     *
     * @param dateFrom start invoice date
     * @param dateTo end invoice date
     * @param customer id of customer
     * @param bolNumber number of bol
     * @param carrier id of the carrier
     * @param shipmentNumber the shipment number of the related load
     * @param messageType the type of message
     * @param loadId id of load
     * @return logs of all integration messages as list of {@link AuditBO}.
     * @throws ApplicationException if the wrong Bol Number of inappropriate wildcard pattern was entered.
     */
    @RequestMapping(value = "/searchLogs", method = RequestMethod.GET)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @ResponseBody
    public List<AuditBO> getLogs(
            @RequestParam(value = "customer", required = false) Long customer,
            @RequestParam(value = "carrier", required = false) Long carrier,
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @RequestParam(value = "dateTo", required = false) String dateTo,
            @RequestParam(value = "bolNumber", required = false) String bolNumber,
            @RequestParam(value = "shipmentNum", required = false) String shipmentNumber,
            @RequestParam(value = "loadId", required = false) Long loadId,
            @RequestParam(value = "messageType", required = false) EDIMessageType messageType) throws ApplicationException {
        return service.getLogs(DateUtils.getDateFrom(dateFrom), DateUtils.getDateFrom(dateTo), customer, carrier,
                bolNumber, shipmentNumber, loadId, messageType);
    }

    /**
     * Searches for the Log details.
     * @param auditId the ID we are searching our documents based on.
     * @return AuditDetailEntity corresponding to the auditID.
     * @throws ApplicationException if the get log failed due to Application Exception.
     */
    @RequestMapping(value = "/getLogDetails", method = RequestMethod.GET)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @ResponseBody
    public AuditDetailEntity getLogDetails(@RequestParam(value = "auditId", required = false) Long auditId) throws ApplicationException {
        return  service.getLogDetails(auditId);
    }
}
