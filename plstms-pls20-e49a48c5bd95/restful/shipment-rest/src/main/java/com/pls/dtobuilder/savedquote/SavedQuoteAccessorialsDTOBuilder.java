package com.pls.dtobuilder.savedquote;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.enums.ApplicableToUnit;
import com.pls.dto.shipment.ShipmentAccessorialDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.SavedQuoteAccessorialEntity;

/**
 * Maps {@link SavedQuoteAccessorialEntity} from DB to {ShipmentAccessorialDTO} for UI layer.
 *
 * @author Mikhail Boldinov, 14/05/13
 */
public class SavedQuoteAccessorialsDTOBuilder extends AbstractDTOBuilder<SavedQuoteAccessorialEntity, ShipmentAccessorialDTO> {
    @Override
    public ShipmentAccessorialDTO buildDTO(SavedQuoteAccessorialEntity entity) {
        ShipmentAccessorialDTO dto = new ShipmentAccessorialDTO();
        dto.setId(entity.getAccessorialType().getId());
        dto.setDescription(entity.getAccessorialType().getDescription());
        dto.setAccessorialGroup(entity.getAccessorialType().getAccessorialGroup());
        return dto;
    }

    @Override
    public SavedQuoteAccessorialEntity buildEntity(ShipmentAccessorialDTO dto) {
        SavedQuoteAccessorialEntity entity = new SavedQuoteAccessorialEntity();

        AccessorialTypeEntity accessorial = new AccessorialTypeEntity(dto.getId());
        accessorial.setAccessorialGroup(dto.getAccessorialGroup());
        accessorial.setApplicableTo(ApplicableToUnit.valueOf(dto.getApplicableTo()));
        accessorial.setDescription(dto.getDescription());
        accessorial.setStatus(dto.getStatus());
        entity.setAccessorialType(accessorial);
        return entity;
    }
}
