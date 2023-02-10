package com.pls.dtobuilder.organization;

import com.pls.core.domain.bo.CarrierInfoBO;
import com.pls.dto.organization.CarrierInfoDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO Builder for carrier.
 * 
 * @author Alexander Nalapko
 */
public class CarrierInfoDTOBuilder extends AbstractDTOBuilder<CarrierInfoBO, CarrierInfoDTO> {

    @Override
    public CarrierInfoDTO buildDTO(CarrierInfoBO entity) {
        CarrierInfoDTO dto = new CarrierInfoDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCurrencyCode(entity.getCurrencyCode());
        dto.setApiCapable(entity.getApiCapable());
        return dto;
    }

    @Override
    public CarrierInfoBO buildEntity(CarrierInfoDTO dto) {
        CarrierInfoBO bo = new CarrierInfoBO();
        bo.setId(dto.getId());
        bo.setName(dto.getName());
        bo.setCurrencyCode(dto.getCurrencyCode());
        bo.setApiCapable(dto.getApiCapable());
        return bo;
    }
}
