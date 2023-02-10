package com.pls.dtobuilder.shipment;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.dto.shipment.ShipmentNoteDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.ShipmentNoteEntity;

/**
 * DTO Builder for shipment note.
 *
 * @author Sergey Kirichenko
 */
public class ShipmentNoteDTOBuilder extends AbstractDTOBuilder<ShipmentNoteEntity, ShipmentNoteDTO> {

    @Override
    public ShipmentNoteDTO buildDTO(ShipmentNoteEntity entity) {
        ShipmentNoteDTO result = new ShipmentNoteDTO();
        result.setId(entity.getId());
        result.setShipmentId(entity.getLoad().getId());
        result.setUserId(entity.getModification().getCreatedBy());
        result.setUsername(UserNameBuilder.buildFullName(entity.getModification().getCreatedUser()));
        result.setCreatedDate(ZonedDateTime.ofInstant(entity.getModification().getCreatedDate().toInstant(), ZoneOffset.UTC));
        result.setText(entity.getNote());
        return result;
    }

    @Override
    public ShipmentNoteEntity buildEntity(ShipmentNoteDTO dto) {
        ShipmentNoteEntity result = new ShipmentNoteEntity();
        result.setId(dto.getId());
        result.setLoadId(dto.getShipmentId());
        result.setNote(dto.getText());
        return result;
    }
}
