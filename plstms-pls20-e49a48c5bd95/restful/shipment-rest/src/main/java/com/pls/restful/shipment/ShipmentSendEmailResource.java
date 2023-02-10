package com.pls.restful.shipment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.dto.shipment.ShipmentDTO;
import com.pls.email.dto.EmailDetailsDTO;
import com.pls.emailhistory.domain.bo.EmailHistoryBO;
import com.pls.emailhistory.service.EmailHistoryService;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

/**
 * REST for sending emails related to shipments.
 * 
 * @author Stas Norochevskiy
 * @author Aleksandr Leshchenko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/shipment/email")
public class ShipmentSendEmailResource {
    @Autowired
    private ShipmentEmailSender emailSender;

    @Autowired
    private EmailHistoryService emailHistoryService;

    @Autowired
    private ShipmentBuilderHelper shipmentBuilder;

    /**
     * Send email with attachments.
     * 
     * @param emailDetails
     *            params with email information
     */
    @RequestMapping(value = "send", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void sendDocumentEmail(@RequestBody EmailDetailsDTO emailDetails) {
        emailSender.sendDocumentsEmail(emailDetails.getRecipients(), emailDetails.getSubject(), emailDetails.getContent(),
                emailDetails.getDocuments(), emailDetails.getLoadId(), emailDetails.getEmailType(), emailDetails.getNotificationType());
    }

    /**
     * Get email content for sending document.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @param docName
     *            name of document to be sent
     * @param isManualBol
     *            <code>TRUE</code> if corresponded document is applied for Manual Bol.
     *            By default it's <code>FALSE</code>
     * @return content
     */
    @RequestMapping(value = "/doc", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getDocumentEmailContentForSendDocument(@RequestParam("loadId") Long loadId,
            @RequestParam(value = "docName", required = false) String docName,
            @RequestParam(value = "isManualBol", required = false) boolean isManualBol) {
        if (isManualBol) {
            return emailSender.getManualBolPayLoadForSendDocument(loadId, docName);
        } else {
            return emailSender.getPayloadForSendDocument(loadId, docName);
        }
    }

    /**
     * Get email content for sending document.
     *
     * @param shipmentDTO
     *            {@link ShipmentDTO}
     * @return content
     */
    @RequestMapping(value = "/doc", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getDocumentEmailContentByDTO(@RequestBody ShipmentDTO shipmentDTO) {
        return emailSender.getPayloadForSendDocument(shipmentBuilder.getShipmentDTOBuilder().buildEntity(shipmentDTO), null);
    }

    /**
     * Get email content for sending document.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @param templateName
     *            name of the template to load
     * @return content
     */
    @RequestMapping(value = "/template", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getDocumentEmailContent(@RequestParam("loadId") Long loadId, @RequestParam("template") String templateName) {
        return emailSender.getPayloadForEmail(loadId, templateName);
    }

    /**
     * Get content for pls pay email.
     * 
     * @param locationId location Id
     * @return content
     */
    @RequestMapping(value = "/plspaytemplate", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getPLSPayContent(@RequestParam("locationId") Long locationId) {
        return emailSender.getPLSPayContent(locationId);
    }

    /**
     * Gel all email history for current user.
     * 
     * @param loadId  {@link LoadEntity#getId()}
     * @return list of shipment history emails.
     */
    @RequestMapping(value = "/history/{loadId}", method = RequestMethod.GET)
    @ResponseBody
    public List<EmailHistoryBO> getEmailHistory(@PathVariable("loadId") Long loadId) {
        return emailHistoryService.getEmailHistory(loadId);
    }
}
