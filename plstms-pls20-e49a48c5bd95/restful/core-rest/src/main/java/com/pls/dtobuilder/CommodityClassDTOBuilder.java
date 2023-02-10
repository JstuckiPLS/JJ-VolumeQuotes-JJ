package com.pls.dtobuilder;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.dto.enums.CommodityClassDTO;

/**
 * Builder for {@link CommodityClassDTO} objects.
 * 
 * @author Maxim Medvedev
 */
public class CommodityClassDTOBuilder extends AbstractDTOBuilder<CommodityClass, CommodityClassDTO> {

    @Override
    public CommodityClassDTO buildDTO(CommodityClass entity) {
        CommodityClassDTO result = null;
        if (entity != null) {
            result = CommodityClassDTO.valueOf(entity.name());
        }
        return result;
    }

    @Override
    public CommodityClass buildEntity(CommodityClassDTO dto) {
        CommodityClass result = null;
        if (dto != null) {
            result = CommodityClass.valueOf(dto.name());
        }
        return result;
    }

}
