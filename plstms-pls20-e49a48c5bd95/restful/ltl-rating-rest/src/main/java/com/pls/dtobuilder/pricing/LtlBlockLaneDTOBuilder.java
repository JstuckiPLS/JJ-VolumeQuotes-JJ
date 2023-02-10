package com.pls.dtobuilder.pricing;

import com.pls.dto.LtlBlockLaneDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.LtlBlockLaneEntity;

/**
 * Builder for conversion between {@link LtlBlockLaneDTO} and {@link LtlBlockLaneEntity}.
 * 
 * @author Ashwini Neelgund
 * 
 */
public class LtlBlockLaneDTOBuilder extends AbstractDTOBuilder<LtlBlockLaneEntity, LtlBlockLaneDTO> {

    @Override
    public LtlBlockLaneDTO buildDTO(LtlBlockLaneEntity bo) {
        LtlBlockLaneDTO dto = new LtlBlockLaneDTO();
        if (bo == null) {
            return dto;
        }
        dto.setId(bo.getId());
        dto.setCarrierId(bo.getCarrierId());
        dto.setShipperId(bo.getShipperId());
        dto.setOrigin(bo.getOrigin());
        dto.setDestination(bo.getDestination());
        dto.setStatus(bo.getStatus());
        dto.setEffDate(bo.getEffDate());
        dto.setExpDate(bo.getExpDate());
        dto.setNotes(bo.getNotes());
        return dto;
    }

    @Override
    public LtlBlockLaneEntity buildEntity(LtlBlockLaneDTO dto) {
        LtlBlockLaneEntity result = new LtlBlockLaneEntity();
        result.setId(dto.getId());
        result.setCarrierId(dto.getCarrierId());
        result.setShipperId(dto.getShipperId());
        result.setOrigin(dto.getOrigin());
        result.setDestination(dto.getDestination());
        result.setStatus(dto.getStatus());
        result.setEffDate(dto.getEffDate());
        result.setExpDate(dto.getExpDate());
        result.setNotes(dto.getNotes());
        if (dto.getVersion() != null) {
            result.setVersion(dto.getVersion());
        }
        return result;
    }

}
