package com.pls.dtobuilder.pricing;

import com.pls.core.domain.enums.LtlServiceType;
import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO Builder for {@link LtlServiceType}.
 * 
 * @author Artem Arapov
 * 
 */
public class LtlServiceTypeDTOBuilder extends AbstractDTOBuilder<LtlServiceType, ValueLabelDTO> {

    @Override
    public ValueLabelDTO buildDTO(LtlServiceType bo) {
        return new ValueLabelDTO(bo.toString(), bo.getDescription());
    }

    @Override
    public LtlServiceType buildEntity(ValueLabelDTO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }

}
