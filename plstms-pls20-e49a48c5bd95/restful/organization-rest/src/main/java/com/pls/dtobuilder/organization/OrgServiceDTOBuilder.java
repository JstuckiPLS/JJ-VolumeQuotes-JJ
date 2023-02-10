package com.pls.dtobuilder.organization;

import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.organization.OrgServiceEntity;
import com.pls.dto.organization.OrgServiceDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO Builder for conversion between {@link OrgServiceEntity} and {@link OrgServiceDTO}.
 * 
 * @author Artem Arapov
 *
 */
public class OrgServiceDTOBuilder extends AbstractDTOBuilder<OrgServiceEntity, OrgServiceDTO> {

    @Override
    public OrgServiceDTO buildDTO(OrgServiceEntity bo) {
        OrgServiceDTO dto = new OrgServiceDTO();

        dto.setId(bo.getId());
        dto.setOrgId(bo.getOrgId());
        dto.setRating(bo.getRating());
        dto.setImaging(defaultIntegrationType(bo.getImaging()));
        dto.setInvoice(defaultIntegrationType(bo.getInvoice()));
        dto.setPickup(defaultIntegrationType(bo.getPickup()));
        dto.setTracking(defaultIntegrationType(bo.getTracking()));
        dto.setVersion(bo.getVersion());

        if (bo.getManualTypeEmail() != null) {
            dto.setManualTypeEmail(bo.getManualTypeEmail());
        }

        return dto;
    }

    @Override
    public OrgServiceEntity buildEntity(OrgServiceDTO dto) {
        OrgServiceEntity entity = new OrgServiceEntity();

        entity.setId(dto.getId());
        entity.setOrgId(dto.getOrgId());
        entity.setImaging(CarrierIntegrationType.valueOf(dto.getImaging()));
        entity.setInvoice(CarrierIntegrationType.valueOf(dto.getInvoice()));
        entity.setPickup(CarrierIntegrationType.valueOf(dto.getPickup()));
        entity.setRating(dto.getRating());
        entity.setTracking(CarrierIntegrationType.valueOf(dto.getTracking()));
        entity.setVersion(dto.getVersion());
        entity.setManualTypeEmail(dto.getManualTypeEmail());

        return entity;
    }

    private String defaultIntegrationType(CarrierIntegrationType type) {
        return type != null ? type.name() : CarrierIntegrationType.EDI.name();
    }
}
