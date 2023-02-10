package com.pls.dtobuilder.pricing;

import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.enums.LtlCostType;

/**
 * DTO Builder for {@link LtlCostType}.
 * 
 * @author Artem Arapov
 * 
 */
public class LtlCostTypeDTOBuilder extends AbstractDTOBuilder<LtlCostType, ValueLabelDTO> {

    @Override
    public ValueLabelDTO buildDTO(LtlCostType bo) {
        return new ValueLabelDTO(bo.toString(), bo.getDescription());
    }

    /**
     * Method throws {@link UnsupportedOperationException}.
     * 
     * @param dto
     *            {@link ValueLabelDTO}.
     * @return LtlCostType
     */
    @Override
    public LtlCostType buildEntity(ValueLabelDTO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }

}
