package com.pls.dtobuilder.pricing;

import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.enums.DistanceUOM;

/**
 * DTO Builder for {@link DistanceUOM}.
 * 
 * @author Artem Arapov
 * 
 */
public class DistanceUOMDTOBuilder extends AbstractDTOBuilder<DistanceUOM, ValueLabelDTO> {

    @Override
    public ValueLabelDTO buildDTO(DistanceUOM bo) {
        return new ValueLabelDTO(bo.toString(), bo.getDescription());
    }

    @Override
    public DistanceUOM buildEntity(ValueLabelDTO dto) {
        throw new UnsupportedOperationException();
    }

}
