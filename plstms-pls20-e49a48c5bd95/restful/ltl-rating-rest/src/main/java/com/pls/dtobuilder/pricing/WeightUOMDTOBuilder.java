package com.pls.dtobuilder.pricing;

import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.enums.WeightUOM;

/**
 * DTO Builder for {@link WeightUOM}.
 * 
 * @author Artem Arapov
 * 
 */
public class WeightUOMDTOBuilder extends AbstractDTOBuilder<WeightUOM, ValueLabelDTO> {

    @Override
    public ValueLabelDTO buildDTO(WeightUOM bo) {
        return new ValueLabelDTO(bo.toString(), bo.getDescription());
    }

    @Override
    public WeightUOM buildEntity(ValueLabelDTO dto) {
        throw new UnsupportedOperationException();
    }

}
