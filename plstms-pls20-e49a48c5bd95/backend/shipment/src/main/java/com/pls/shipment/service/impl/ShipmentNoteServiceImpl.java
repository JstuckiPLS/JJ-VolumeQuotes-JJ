package com.pls.shipment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.shipment.dao.ShipmentNoteDao;
import com.pls.shipment.domain.ShipmentNoteEntity;
import com.pls.shipment.service.ShipmentNoteService;

/**
 * Implementation of {@link com.pls.shipment.service.ShipmentNoteService}.
 *
 * @author Sergey Kirichenko
 */
@Service
@Transactional
public class ShipmentNoteServiceImpl implements ShipmentNoteService {

    @Autowired
    private ShipmentNoteDao shipmentNoteDao;

    @Override
    public ShipmentNoteEntity addNewNote(ShipmentNoteEntity newNote) {
        return shipmentNoteDao.saveOrUpdate(newNote);
    }

    @Override
    public void addNewNote(Long loadId, String note) {
        ShipmentNoteEntity shipmentNote = new ShipmentNoteEntity();
        shipmentNote.setNote(note);
        shipmentNote.setLoadId(loadId);
        shipmentNoteDao.saveOrUpdate(shipmentNote);
    }

    @Override
    public List<ShipmentNoteEntity> findShipmentNotes(Long shipmentId) {
        return shipmentNoteDao.findShipmentNotes(shipmentId);
    }

    public void setShipmentNoteDao(ShipmentNoteDao shipmentNoteDao) {
        this.shipmentNoteDao = shipmentNoteDao;
    }

    @Override
    public void updateNotes(Long loadId, List<ShipmentNoteEntity> notes) {
        notes.forEach(note -> note.setLoadId(loadId));
        shipmentNoteDao.batchNotesInsert(notes);
    }
}
