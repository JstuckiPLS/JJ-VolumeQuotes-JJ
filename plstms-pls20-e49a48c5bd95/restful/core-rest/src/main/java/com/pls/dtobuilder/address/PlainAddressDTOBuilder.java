package com.pls.dtobuilder.address;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.dto.address.PlainAddressDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * Builder for conversion between {@link PlainAddressDTO} and {@link AddressEntity}.
 * 
 * @author Artem Arapov
 * 
 */
public class PlainAddressDTOBuilder extends AbstractDTOBuilder<AddressEntity, PlainAddressDTO> {

    private final CountryDTOBuilder countryDTOBuilder = new CountryDTOBuilder();
    private final ZipDTOBuilder zipDTOBuilder = new ZipDTOBuilder();

    @Override
    public PlainAddressDTO buildDTO(AddressEntity bo) {
        PlainAddressDTO dto = new PlainAddressDTO();

        dto.setId(bo.getId());
        dto.setAddress1(bo.getAddress1());
        dto.setAddress2(bo.getAddress2());
        ZipDTO zipDTO = zipDTOBuilder.buildDTO(bo.getZipCode());
        dto.setZip(zipDTO);
        dto.setCountry(zipDTO.getCountry());

        return dto;
    }

    @Override
    public AddressEntity buildEntity(PlainAddressDTO dto) {
        AddressEntity entity = new AddressEntity();

        entity.setId(dto.getId());
        entity.setAddress1(dto.getAddress1());
        entity.setAddress2(dto.getAddress2());
        entity.setCity(dto.getZip().getCity());
        entity.setCountry(countryDTOBuilder.buildEntity(dto.getZip().getCountry()));
        entity.setState(getState(dto.getZip()));
        entity.setZip(dto.getZip().getZip());

        return entity;
    }

    private StateEntity getState(ZipDTO zipDTO) {
        StateEntity state = null;
        if (StringUtils.isNotBlank(zipDTO.getState())) {
            state = new StateEntity();
            StatePK id = new StatePK();
            id.setStateCode(zipDTO.getState());
            id.setCountryCode(zipDTO.getCountry().getId());
            state.setStatePK(id);
        }
        return state;
    }
}
