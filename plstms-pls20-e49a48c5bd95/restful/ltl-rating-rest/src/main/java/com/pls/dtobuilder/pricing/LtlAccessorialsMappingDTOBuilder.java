package com.pls.dtobuilder.pricing;

import com.pls.dto.LtlAccessorialsMappingDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.LtlAccessorialsMappingEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;

/**
 * DTO builder for {@link LtlAccessorialsMappingDTO}.
 * 
 * @author Dmitriy Davydenko
 *
 */
public class LtlAccessorialsMappingDTOBuilder extends AbstractDTOBuilder<LtlAccessorialsMappingEntity, LtlAccessorialsMappingDTO> {

    private DataProvider dataProvider;

    /**
     * Constructor.
     * 
     * @param dataProvider
     *            data provider
     */
    public LtlAccessorialsMappingDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public LtlAccessorialsMappingDTO buildDTO(LtlAccessorialsMappingEntity bo) {
        LtlAccessorialsMappingDTO dto = new LtlAccessorialsMappingDTO();
        dto.setId(bo.getId());
        dto.setCarrierCode(bo.getCarrierCode());
        dto.setCarrierId(bo.getCarrierId());
        dto.setDefaultAccessorial(bo.getDefaultAccessorial());
        dto.setDescription(bo.getAccessorialType().getDescription());
        dto.setPlsCode(bo.getPlsCode());
        return dto;
    }

    @Override
    public LtlAccessorialsMappingEntity buildEntity(LtlAccessorialsMappingDTO dto) {
        LtlAccessorialsMappingEntity entity;
        if (dto.getId() != null) {
            entity = dataProvider.getById(dto.getId());
        } else {
            entity = new LtlAccessorialsMappingEntity();
        }
        entity.setCarrierCode(dto.getCarrierCode());
        entity.setCarrierId(dto.getCarrierId());
        entity.setDefaultAccessorial(dto.getDefaultAccessorial());
        entity.setPlsCode(dto.getPlsCode());
        return entity;
    }

    /**
     * Data provider to build entity.
     */
    public interface DataProvider {
        /**
         * Get pricing detail by id.
         * 
         * @param id
         *            {@link LtlPricingDetailsEntity#getId()}
         * @return {@link LtlPricingDetailsEntity}
         */
        LtlAccessorialsMappingEntity getById(Long id);
    }
}
