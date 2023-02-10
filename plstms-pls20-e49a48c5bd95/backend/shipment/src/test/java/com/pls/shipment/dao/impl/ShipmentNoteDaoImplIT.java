package com.pls.shipment.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.shipment.dao.ShipmentNoteDao;
import com.pls.shipment.domain.ShipmentNoteEntity;

/**
 * Test for {@link ShipmentNoteDaoImpl}.
 *
 * @author Sergey Kirichenko
 */
public class ShipmentNoteDaoImplIT extends AbstractDaoTest {

    private static final Long SHIPMENT_WITH_NOTES_ID = 121L;
    private static final Long SHIPMENT_WITHOUT_NOTES_ID = 1L;
    private static final Long EXPECTED_LOAD_ID = 7010L;
    public static final int NOTES_COUNT = 2;

    @Autowired
    @Qualifier("shipmentNoteDaoImpl")
    private ShipmentNoteDao dao;

    @Test
    public void shouldGetNotesForShipment() {
        List<ShipmentNoteEntity> result = dao.findShipmentNotes(SHIPMENT_WITH_NOTES_ID);
        assertNotNull(result);
        assertEquals(NOTES_COUNT, result.size());
    }

    @Test
    public void shouldNotFoundNotes() {
        List<ShipmentNoteEntity> result = dao.findShipmentNotes(SHIPMENT_WITHOUT_NOTES_ID);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void shouldSaveNoteWithTransientLoad() {
        ShipmentNoteEntity note = new ShipmentNoteEntity();
        note.setNote("test note");
        note.setLoadId(7010L);
        dao.saveOrUpdate(note);
        getSession().flush();
    }

    @Test
    public void shouldProcessBatchNotesInsert() {
        final int expectedSize = 21;
        List<ShipmentNoteEntity> expectedList = getRandomShipmentNoteEntity(expectedSize);
        dao.batchNotesInsert(expectedList);

        List<ShipmentNoteEntity> actualList = dao.findShipmentNotes(EXPECTED_LOAD_ID);
        assertNotNull(actualList);
        assertEquals(expectedSize, actualList.size());
        assertShipmentNoteEntity(actualList);
    }

    private List<ShipmentNoteEntity> getRandomShipmentNoteEntity(int size) {
        List<ShipmentNoteEntity> list = new ArrayList<>();
        for (long i = 0; i < size; i++) {
            ShipmentNoteEntity note = new ShipmentNoteEntity();
            note.setNote(String.valueOf(Math.random()));
            note.setLoadId(EXPECTED_LOAD_ID);
            list.add(note);
        }

        return list;
    }

    private void assertShipmentNoteEntity(List<ShipmentNoteEntity> actualList) {
        for (ShipmentNoteEntity item : actualList) {
            assertNotNull(item);
            assertNotNull(item.getNote());
            Assert.assertFalse(item.getNote().isEmpty());
        }
    }
}
