package com.pls.dtobuilder.pricing;

import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.enums.FuelWeekDays;

/**
 * DTO Builder for {@link FuelWeekDays}.
 * 
 * @author Hima Bindu Challa
 * 
 */
public class FuelWeekDaysDTOBuilder extends AbstractDTOBuilder<FuelWeekDays, ValueLabelDTO> {

    @Override
    public ValueLabelDTO buildDTO(FuelWeekDays bo) {
        return new ValueLabelDTO(bo.toString(), bo.getDescription());
    }

    /**
     * Method throws {@link UnsupportedOperationException}.
     * 
     * @param dto
     *            {@link ValueLabelDTO}.
     * @return FuelWeekDays
     */
    @Override
    public FuelWeekDays buildEntity(ValueLabelDTO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }

}
