package com.pls.dtobuilder.pricing;

import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.enums.LtlMarginType;

/**
 * DTO Builder for {@link LtlMarginType}.
 * 
 * @author Artem Arapov
 * 
 */
public class LtlMarginTypeDTOBuilder extends AbstractDTOBuilder<LtlMarginType, ValueLabelDTO> {

    @Override
    public ValueLabelDTO buildDTO(LtlMarginType bo) {
        return new ValueLabelDTO(bo.toString(), bo.getDescription());
    }

    @Override
    public LtlMarginType buildEntity(ValueLabelDTO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }

}
