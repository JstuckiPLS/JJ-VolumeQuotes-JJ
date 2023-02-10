package com.pls.shipment.service;

import java.util.List;

import com.pls.shipment.domain.ShipmentNoteEntity;

/**
 * Service for operation on shipment notes.
 *
 * @author Sergey Kirichenko
 */
public interface ShipmentNoteService {

    /**
     * Add new note for load.
     *
     * @param newNote
     *            - new note.
     * @return {@link ShipmentNoteEntity} with id
     */
    ShipmentNoteEntity addNewNote(ShipmentNoteEntity newNote);

    /**
     * Add new note for load.
     * 
     * @param loadId
     *            shipment Id
     * @param note
     *            {@link String}
     */
    void addNewNote(Long loadId, String note);

    /**
     * Find notes for specified shipment.
     *
     * @param shipmentId
     *            - id of shipment
     * @return list of {@link ShipmentNoteEntity}
     */
    List<ShipmentNoteEntity> findShipmentNotes(Long shipmentId);

    /**
     * Updating notes within specific load.
     * 
     * @param loadId - ID of load containing notes
     * @param notes - actual notes
     */
    void updateNotes(Long loadId, List<ShipmentNoteEntity> notes);
}
