package com.pls.dtobuilder.dictionary;

import com.pls.dto.ValueLabelDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.FinancialReasonsEntity;

/**
 * Maps {@link FinancialReasonsEntity} from DB to {@link ValueLabelDTO} for UI layer.
 * 
 * @author Aleksandr Leshchenko
 */
public class FinancialReasonDTOBuilder extends AbstractDTOBuilder<FinancialReasonsEntity, ValueLabelDTO> {
    @Override
    public ValueLabelDTO buildDTO(FinancialReasonsEntity bo) {
        return new ValueLabelDTO(bo.getId(), bo.getDescription());
    }

    @Override
    public FinancialReasonsEntity buildEntity(ValueLabelDTO dto) {
        FinancialReasonsEntity entity = new FinancialReasonsEntity();
        entity.setId((Long) dto.getValue());
        return entity;
    }
}
