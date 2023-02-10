package com.pls.dtobuilder.shipment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.pls.dto.shipment.ShipmentEventDTO;
import com.pls.shipment.domain.bo.ShipmentEventBO;

/**
 * Test for {@link ShipmentEventDTOBuilder}.
 * 
 * @author Aleksandr Leshchenko
 */
public class ShipmentEventDTOBuilderTest {
    private final ShipmentEventDTOBuilder builder = new ShipmentEventDTOBuilder();

    @Test
    public void shouldBuildDTO() {
        List<ShipmentEventBO> bos = prepareTestData();
        Collection<ShipmentEventDTO> result = builder.buildList(bos);
        Assert.assertEquals(2, result.size());

        ShipmentEventDTO dto = getDtoByEventId(result, bos.get(0).getLoadId());
        Assert.assertNotNull(dto);

        Assert.assertEquals(bos.get(0).getFirstName(), dto.getFirstName());
        Assert.assertEquals(bos.get(0).getLastName(), dto.getLastName());
        Assert.assertEquals(LocalDateTime.ofInstant(bos.get(0).getCreatedDate().toInstant(), ZoneId.systemDefault()), dto.getDate());
        String description = "test " + bos.get(0).getData() + " description " + bos.get(1).getData();
        Assert.assertEquals(description, dto.getEvent());

        dto = getDtoByEventId(result, bos.get(2).getLoadId());
        Assert.assertNotNull(dto);

        Assert.assertEquals(bos.get(2).getFirstName(), dto.getFirstName());
        Assert.assertEquals(bos.get(2).getLastName(), dto.getLastName());
        Assert.assertEquals(LocalDateTime.ofInstant(bos.get(2).getCreatedDate().toInstant(), ZoneId.systemDefault()), dto.getDate());
        Assert.assertEquals("test description", dto.getEvent());
    }

    private ShipmentEventDTO getDtoByEventId(Collection<ShipmentEventDTO> result, Long shipmentId) {
        for (ShipmentEventDTO dto : result) {
            if (dto.getShipmentId().equals(shipmentId)) {
                return dto;
            }
        }
        return null;
    }

    private List<ShipmentEventBO> prepareTestData() {
        List<ShipmentEventBO> bo = new ArrayList<ShipmentEventBO>();
        Long eventId = (long) (Math.random() * 100);
        Long loadId = (long) (Math.random() * 100);
        String description = "test {0} description {1}";
        String firstName = "firstName" + Math.random();
        String lastName = "lastName" + Math.random();
        Date createdDate = new Date();
        bo.add(getBO(eventId, loadId, description, firstName, lastName, createdDate, (byte) 0, String.valueOf(Math.random())));
        bo.add(getBO(eventId, loadId, description, firstName, lastName, createdDate, (byte) 1, String.valueOf(Math.random())));

        eventId = (long) (Math.random() * 100) + 101;
        // actually load id should be the same in real data, but for testing purposes it will be different.
        loadId = (long) (Math.random() * 100) + 101;
        description = "test description";
        firstName = "firstName" + Math.random();
        lastName = "lastName" + Math.random();
        createdDate = new Date();
        bo.add(getBO(eventId, loadId, description, firstName, lastName, createdDate, null, null));
        return bo;
    }

    private ShipmentEventBO getBO(Long eventId, Long loadId, String description, String firstName, String lastName, Date createdDate,
            Byte ordinal, String data) {
        ShipmentEventBO bo = new ShipmentEventBO();
        bo.setEventId(eventId);
        bo.setLoadId(loadId);
        bo.setDescription(description);
        bo.setFirstName(firstName);
        bo.setLastName(lastName);
        bo.setCreatedDate(createdDate);
        bo.setOrdinal(ordinal);
        bo.setData(data);
        return bo;
    }
}
