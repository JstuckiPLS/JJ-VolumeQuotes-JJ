package com.pls.dtobuilder.address;

import com.pls.core.domain.organization.CountryEntity;
import com.pls.dto.address.CountryDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * Builder for conversation between CountryEntity and CountryDTO.
 * 
 * @author Artem Arapov
 * 
 */
public class CountryDTOBuilder extends AbstractDTOBuilder<CountryEntity, CountryDTO> {

    @Override
    public CountryDTO buildDTO(CountryEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException(NULL_ENTITY);
        }
        CountryDTO dto = new CountryDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDialingCode(entity.getPhoneCode());

        return dto;
    }

    @Override
    public CountryEntity buildEntity(CountryDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException(NULL_DTO);
        }
        CountryEntity entity = new CountryEntity();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPhoneCode(dto.getDialingCode());

        return entity;
    }

}
