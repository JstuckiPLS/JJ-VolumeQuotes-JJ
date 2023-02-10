package com.pls.shipment.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.ShipmentNoteEntity;

import java.util.List;

/**
 * Dao for {@link ShipmentNoteEntity}.
 *
 * @author Sergey Kirichenko
 */
public interface ShipmentNoteDao extends AbstractDao<ShipmentNoteEntity, Integer> {

    /**
     * Gets all notes for shipment.
     *
     * @param id - id of load
     * @return list of {@link ShipmentNoteEntity}
     */
    List<ShipmentNoteEntity> findShipmentNotes(Long id);

    /**
     *  Batch insert of shipment notes.
     * 
     * @param notes - notes to be inserted
     */
    void batchNotesInsert(List<ShipmentNoteEntity> notes);
}
