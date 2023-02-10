package com.pls.dtobuilder.shipment;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.enums.ApplicableToUnit;
import com.pls.dto.shipment.ShipmentAccessorialDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * Maps {@link AccessorialTypeEntity} from DB to {ShipmentAccessorialDTO} for UI layer.
 *
 * @author Mikhail Boldinov, 28/03/13
 */
public class AccessorialsDTOBuilder extends AbstractDTOBuilder<AccessorialTypeEntity, ShipmentAccessorialDTO> {

    @Override
    public ShipmentAccessorialDTO buildDTO(AccessorialTypeEntity entity) {
        ShipmentAccessorialDTO dto = new ShipmentAccessorialDTO();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setAccessorialGroup(entity.getAccessorialGroup());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    @Override
    public AccessorialTypeEntity buildEntity(ShipmentAccessorialDTO dto) {
        AccessorialTypeEntity accessorialType = new AccessorialTypeEntity(dto.getId());
        accessorialType.setDescription(dto.getDescription());
        accessorialType.setApplicableTo(ApplicableToUnit.valueOf(dto.getApplicableTo()));
        accessorialType.setAccessorialGroup(dto.getAccessorialGroup());
        accessorialType.setStatus(dto.getStatus());
        return accessorialType;
    }
}
