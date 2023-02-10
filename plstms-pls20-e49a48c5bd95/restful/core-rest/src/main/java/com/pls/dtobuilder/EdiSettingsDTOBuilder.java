package com.pls.dtobuilder;

import com.pls.EdiSettingsDTO;
import com.pls.core.domain.organization.EdiSettingsEntity;

/**
 * DTO builder for {@link EdiSettingsEntity}.
 * 
 * @author Brichak Aleksandr
 */
public class EdiSettingsDTOBuilder extends AbstractDTOBuilder<EdiSettingsEntity, EdiSettingsDTO> {

    @Override
    public EdiSettingsDTO buildDTO(EdiSettingsEntity entity) {
        EdiSettingsDTO dto = new EdiSettingsDTO();
        if (entity != null) {
            dto.setEdiStatus(entity.getEdiStatus());
            dto.setEdiType(entity.getEdiType());
            dto.setBolUnique(entity.isUniqueRefAndBol());
        }
        return dto;
    }

    @Override
    public EdiSettingsEntity buildEntity(EdiSettingsDTO dto) {
        EdiSettingsEntity ediSettingsEntity = new EdiSettingsEntity();
        if (dto == null) {
            return ediSettingsEntity;
        }
        ediSettingsEntity.setEdiStatus(dto.getEdiStatus());
        ediSettingsEntity.setEdiType(dto.getEdiType());
        ediSettingsEntity.setIsUniqueRefAndBol(dto.isBolUnique());
        return ediSettingsEntity;
    }

}
