package com.pls.dtobuilder;

import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.organization.CarrierEntity;

/**
 * DTO builder for Carrier information from SMC3.
 * 
 * @author Aleksandr Leshchenko
 */
public class CarrierDTOBuilder extends AbstractDTOBuilder<CarrierEntity, CarrierDTO> {
    /**
     * Get path to download carrier logo.
     * 
     * @param carrierId
     *            {@link CarrierEntity#getId()}
     * @return logo URL
     */
    public static String getCarrierLogoPath(Long carrierId) {
        return String.format("/restful/organization/%1$d/logo", carrierId);
    }

    @Override
    public CarrierDTO buildDTO(CarrierEntity entity) {
        CarrierDTO dto = new CarrierDTO();
        dto.setId(entity.getId());
        dto.setScac(entity.getScac());
        dto.setCurrencyCode(entity.getCurrencyCode());
        dto.setName(entity.getName());
        dto.setLogoPath(getCarrierLogoPath(entity.getId()));
        dto.setApiCapable(entity.getOrgServiceEntity() != null && entity.getOrgServiceEntity().getPickup() == CarrierIntegrationType.API);
        return dto;
    }

    /**
     * Method is not supported.
     * 
     * @param dto
     *            {@link CarrierDTO}
     * @return nothing
     * @throws UnsupportedOperationException
     */
    @Override
    public CarrierEntity buildEntity(CarrierDTO dto) {
        throw new UnsupportedOperationException();
    }
}
