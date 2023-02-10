package com.pls.dtobuilder.address;

import com.pls.core.domain.TimeZoneEntity;
import com.pls.dto.address.TimezoneDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO Builder for building {@link TimeZoneEntity} from {@link TimezoneDTO} and vice versa.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class TimezoneDTOBuilder extends AbstractDTOBuilder<TimeZoneEntity, TimezoneDTO> {
    @Override
    public TimezoneDTO buildDTO(TimeZoneEntity entity) {
        TimezoneDTO dto = new TimezoneDTO();
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setLocalOffset(entity.getLocalOffset());
        return dto;
    }

    @Override
    public TimeZoneEntity buildEntity(TimezoneDTO timezoneDTO) {
        throw new UnsupportedOperationException("Building timezone entity doesn't supported");
    }
}
