package com.pls.dtobuilder.util;

import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;

/**
 * DTO Builder for {@link PickupAndDeliveryWindowDTO}.
 *
 * @author Aleksandr Leshchenko
 */
public class PickUpAndDeliveryDTOBuilder extends AbstractDTOBuilder<Time, PickupAndDeliveryWindowDTO> {
    private static final int TWELVE = 12;

    @Override
    public PickupAndDeliveryWindowDTO buildDTO(Time bo) {
        if (bo == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(bo);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        boolean am = calendar.get(Calendar.AM_PM) == Calendar.AM;
        return new PickupAndDeliveryWindowDTO(hour, minute, am);
    }

    @Override
    public Time buildEntity(PickupAndDeliveryWindowDTO dto) {
        if (dto == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, dto.getMinutes());
        calendar.set(Calendar.HOUR, dto.getHours() == TWELVE ? 0 : dto.getHours());
        calendar.set(Calendar.AM_PM, dto.getAm() ? Calendar.AM : Calendar.PM);
        return new Time(calendar.getTimeInMillis());
    }

}
