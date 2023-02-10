package com.pls.dtobuilder.product;

import com.pls.dto.PackageTypeDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.PackageTypeEntity;

/**
 * Maps {@link PackageTypeEntity} from DB to {@link PackageTypeDTO} for UI layer.
 * 
 * @author Aleksandr Leshchenko
 */
public class PackageTypeDTOBuilder extends AbstractDTOBuilder<PackageTypeEntity, PackageTypeDTO> {
    @Override
    public PackageTypeDTO buildDTO(PackageTypeEntity dataValue) {
        return dataValue == null ? null : new PackageTypeDTO(dataValue.getId(), dataValue.getDescription());
    }

    @Override
    public PackageTypeEntity buildEntity(PackageTypeDTO dto) {
        if (dto == null) {
            return null;
        }
        PackageTypeEntity entity = new PackageTypeEntity();
        entity.setId(dto.getCode());
        entity.setDescription(dto.getLabel());
        return entity;
    }
}
