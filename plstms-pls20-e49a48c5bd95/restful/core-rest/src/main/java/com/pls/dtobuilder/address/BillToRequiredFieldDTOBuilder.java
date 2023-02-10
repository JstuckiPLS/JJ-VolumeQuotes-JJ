package com.pls.dtobuilder.address;

import com.pls.core.domain.bo.AddressBO;
import com.pls.core.domain.enums.DefaultValuesAction;
import com.pls.core.domain.enums.RequiredFieldPointType;
import com.pls.core.domain.enums.RequiredFieldShipmentDirection;
import com.pls.core.domain.organization.BillToRequiredFieldEntity;
import com.pls.core.shared.BillToRequiredField;
import com.pls.core.shared.Status;
import com.pls.dto.BillToRequiredFieldDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * The Class BillToRequiredFieldDTOBuilder.
 * 
 * @author Sergii Belodon
 */
public class BillToRequiredFieldDTOBuilder  extends AbstractDTOBuilder<BillToRequiredFieldEntity, BillToRequiredFieldDTO> {

    private DataProvider dataProvider;

    /**
     * Default constructor.
     */
    public BillToRequiredFieldDTOBuilder() {
    }

    /**
     * Constructor.
     *
     * @param dataProvider {@link DataProvider}
     */
    public BillToRequiredFieldDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public BillToRequiredFieldDTO buildDTO(BillToRequiredFieldEntity bo) {
        BillToRequiredFieldDTO dto = new BillToRequiredFieldDTO();
        dto.setId(bo.getId());
        dto.setName(bo.getFieldName().getCode());
        dto.setRequired(bo.getRequired());
        dto.setDefaultValue(bo.getDefaultValue());
        if (bo.getShipmentDirection() != null) {
            dto.setInboundOutbound(bo.getShipmentDirection().getCode());
        }
        dto.setAddress(new AddressBO());
        dto.getAddress().setCity(bo.getCity());
        dto.getAddress().setCountry(bo.getCountry());
        dto.getAddress().setState(bo.getState());
        dto.getAddress().setZip(bo.getZip());
        if (bo.getAddressDirection() != null) {
            dto.setOriginDestination(bo.getAddressDirection().getCode());
        }
        dto.setStartWith(bo.getStartWith());
        dto.setEndWith(bo.getEndWith());
        if (bo.getActionForDefaultValues() != null) {
            dto.setActionForDefaultValues(bo.getActionForDefaultValues().getCode());
        }
        dto.setRuleExp(bo.getRuleExp());
        return dto;
    }

    @Override
    public BillToRequiredFieldEntity buildEntity(BillToRequiredFieldDTO dto) {
        BillToRequiredFieldEntity entity;
        if (dto.getId() != null) {
            entity = dataProvider.getRequiredFieldById(dto.getId());
        } else {
            entity = new BillToRequiredFieldEntity();
        }
        entity.setFieldName(BillToRequiredField.getByCode(dto.getName()));
        entity.setRequired(dto.getRequired());
        entity.setDefaultValue(dto.getDefaultValue());
        entity.setShipmentDirection(RequiredFieldShipmentDirection.getByCode(dto.getInboundOutbound()));
        if (dto.getAddress() != null) {
            entity.setCity(dto.getAddress().getCity());
            entity.setCountry(dto.getAddress().getCountry());
            entity.setState(dto.getAddress().getState());
            entity.setZip(dto.getAddress().getZip());
        }
        entity.setStatus(Status.ACTIVE);
        entity.setAddressDirection(RequiredFieldPointType.getByCode(dto.getOriginDestination()));
        entity.setStartWith(dto.getStartWith());
        entity.setEndWith(dto.getEndWith());
        if (dto.getActionForDefaultValues() != null) {
            entity.setActionForDefaultValues(DefaultValuesAction.getByCode(dto.getActionForDefaultValues()));
        } else {
            entity.setActionForDefaultValues(null);
        }
        entity.setRuleExp(dto.getRuleExp());

        return entity;
    }

    /**
     * Bill To Required Fields Data Provider.
     * 
     * @author Artem Arapov
     *
     */
    public interface DataProvider {
        /**
         * Gets the required field by id.
         *
         * @param id the id
         * @return the required field by id
         */
        BillToRequiredFieldEntity getRequiredFieldById(Long id);
    }
}
