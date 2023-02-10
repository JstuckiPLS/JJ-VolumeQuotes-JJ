package com.pls.shipment.service.impl;

import com.pls.shipment.dao.ShipmentNoteDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.ShipmentNoteEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for {@link ShipmentNoteServiceImpl}.
 *
 * @author Sergey Kirichenko
 */
public class ShipmentNoteServiceImplTest {

    public static final String TEST_NOTE = "Test note";

    private ShipmentNoteServiceImpl sut;

    @Mock
    private ShipmentNoteDao shipmentNoteDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sut = new ShipmentNoteServiceImpl();
        sut.setShipmentNoteDao(shipmentNoteDao);
    }

    @Test
    public void shouldAddNewNote() {
        ShipmentNoteEntity note = createNote();
        Mockito.when(shipmentNoteDao.saveOrUpdate(Mockito.any(ShipmentNoteEntity.class))).thenReturn(note);
        sut.addNewNote(note);
        Mockito.verify(shipmentNoteDao).saveOrUpdate(Mockito.any(ShipmentNoteEntity.class));
    }

    @Test
    public void shouldFindNoteForShipment() {
        List<ShipmentNoteEntity> result = new ArrayList<ShipmentNoteEntity>();
        result.add(createNote());
        Mockito.when(shipmentNoteDao.findShipmentNotes(Mockito.anyLong())).thenReturn(result);
        sut.findShipmentNotes(1L);
        Mockito.verify(shipmentNoteDao).findShipmentNotes(Mockito.anyLong());
    }

    private ShipmentNoteEntity createNote() {
        ShipmentNoteEntity result = new ShipmentNoteEntity();
        result.setId(1);
        LoadEntity load = new LoadEntity();
        load.setId(1L);
        result.setLoad(load);
        result.setNote(TEST_NOTE);
        return result;
    }
}
