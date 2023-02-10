package com.pls.dtobuilder.address;

import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dtobuilder.util.PickUpAndDeliveryDTOBuilder;
import org.junit.Test;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link PickUpAndDeliveryDTOBuilder}.
 * 
 * @author Aleksandr Leshchenko
 */
@SuppressWarnings("deprecation")
public class PickUpDTOBuilderTest {
    private PickUpAndDeliveryDTOBuilder builder = new PickUpAndDeliveryDTOBuilder();

    @Test
    public void shouldBuildDTO() {
        for (int i = 0; i < 24; i++) {
            checkDTO(i, (int) (Math.random() * 59));
        }
    }

    private void checkDTO(int hours, int minutes) {
        Time entity = new Time(hours, minutes, 30);
        DateFormat format = new SimpleDateFormat("h:mm a");
        String timeStr = format.format(entity);
        PickupAndDeliveryWindowDTO dto = builder.buildDTO(entity);
        assertEquals(timeStr, dto.toString());
    }

    @Test
    public void shouldBuildEntity() {
        for (int i = 0; i < 24; i++) {
            checkEntity(i, (int) (Math.random() * 59));
        }
    }

    private void checkEntity(int hours, int minutes) {
        PickupAndDeliveryWindowDTO dto = new PickupAndDeliveryWindowDTO(hours > 12 ? hours - 12 : hours, minutes, hours < 12);
        Time entity = builder.buildEntity(dto);
        DateFormat format = new SimpleDateFormat("h:mm a");
        String timeStr = format.format(entity);
        assertEquals(dto.toString(), timeStr);
    }
}
