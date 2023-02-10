package com.pls.dtobuilder.pricing;

import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.LtlZonesEntity;

/**
 * DTO Builder for zone items.
 * 
 * @author Artem Arapov
 * 
 */
public class ZoneItemDTOBuilder extends AbstractDTOBuilder<LtlZonesEntity, ValueLabelDTO> {

    @Override
    public ValueLabelDTO buildDTO(LtlZonesEntity bo) {
        return new ValueLabelDTO(bo.getId(), bo.getName());
    }

    @Override
    public LtlZonesEntity buildEntity(ValueLabelDTO dto) {
        throw new UnsupportedOperationException();
    }

}
