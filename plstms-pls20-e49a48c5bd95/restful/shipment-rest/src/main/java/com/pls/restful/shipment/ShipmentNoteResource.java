package com.pls.restful.shipment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.dto.shipment.ShipmentNoteDTO;
import com.pls.shipment.service.ShipmentNoteService;

/**
 * Shipment note REST service.
 * 
 * @author Brichak Aleksandr
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/shipment/{shipmentId}/notes")
public class ShipmentNoteResource {

    @Autowired
    private ShipmentBuilderHelper shipmentBuilderUtils;

    @Autowired
    private ShipmentNoteService shipmentNoteService;


    /**
     * Adds new note to the shipment.
     *
     * @param newNote
     *            new shipment note
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addNewNote(@RequestBody ShipmentNoteDTO newNote) {
        shipmentNoteService.addNewNote(shipmentBuilderUtils.getShipmentNoteDTOBuilder().buildEntity(newNote));
    }

    /**
     * Returns all shipment notes.
     *
     * @param shipmentId
     *            shipment identifier
     * @return DTO list of shipment notes
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentNoteDTO> findShipmentNotes(@PathVariable("shipmentId") Long shipmentId) {
        return shipmentBuilderUtils.getShipmentNoteDTOBuilder()
                .buildList(shipmentNoteService.findShipmentNotes(shipmentId));
    }
}
