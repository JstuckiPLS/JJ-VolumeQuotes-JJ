package com.pls.restful.shipment;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.service.UserPermissionsService;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.ShipmentNoteService;
import com.pls.shipment.service.ShipmentService;

/**
 * Shipment related REST service.
 * 
 * @author Gleb Zgonikov
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/shipment")
public class ShipmentOperationResource {

    @Autowired
    private ShipmentNoteService shipmentNoteService;
    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private ShipmentBuilderHelper shipmentBuilder;

    /**
     * Cancel shipment by id.
     * 
     * @param shipmentId
     *            shipment identifier
     * @throws InternalJmsCommunicationException
     *             if load tender message can not be published to external integration message queue
     * @throws EntityNotFoundException
     *             if shipment with specified id does not exist
     * @throws EdiProcessingException
     *             thrown when EDI processing fails.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{shipmentId}/cancel", method = RequestMethod.POST)
    @ResponseBody
    public void cancelShipment(@PathVariable("shipmentId") Long shipmentId)
            throws InternalJmsCommunicationException, EntityNotFoundException, EdiProcessingException {
        shipmentService.cancelShipment(shipmentId);
    }

    /**
     * Close Load by id.
     * 
     * @param shipmentId
     *            Load identifier
     * 
     * @param note
     *            note identifier
     * 
     * @throws EntityNotFoundException
     *             if shipment with specified id does not exist
     */

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{shipmentId}/close", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void closeShipment(@PathVariable("shipmentId") Long shipmentId,
            @RequestParam(value = "note", required = false) String note) throws EntityNotFoundException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name());
        shipmentService.closeLoad(shipmentId);
        if (StringUtils.isNotBlank(note)) {
            shipmentNoteService.addNewNote(shipmentId, note);
        }
    }

    /**
     * Returns shipment.
     * 
     * @param customerId
     *            id of customer
     * @param shipmentId
     *            shipment identifier
     * @return shipment dto
     * @throws EntityNotFoundException
     *             if shipment not found
     */
    @RequestMapping(value = "/{shipmentId}", method = RequestMethod.GET)
    @ResponseBody
    public ShipmentDTO getShipment(@PathVariable("customerId") Long customerId,
            @PathVariable("shipmentId") Long shipmentId) throws EntityNotFoundException {
        userPermissionsService.checkOrganization(customerId);
        LoadEntity load = shipmentService.getShipmentWithAllDependencies(shipmentId);
        if (load == null) {
            throw new EntityNotFoundException(LoadEntity.class, shipmentId);
        }
        return shipmentBuilder.getShipmentDTOBuilder().buildDTO(load);
    }

    /**
     * Copies shipment.
     * 
     * @param customerId
     *            id of customer
     * @param shipmentId
     *            shipment identifier to copy
     * @return new shipment
     */
    @RequestMapping(value = "/{shipmentId}/copy", method = RequestMethod.GET)
    @ResponseBody
    public ShipmentDTO getCopiedShipment(@PathVariable("customerId") Long customerId,
            @PathVariable("shipmentId") Long shipmentId) {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.QUOTES_VIEW.name());

        return shipmentBuilder.getShipmentDTOBuilder().buildDTO(shipmentService.getCopyOfShipment(shipmentId));
    }
}
