package com.pls.dtobuilder.address;

import org.apache.commons.lang3.BooleanUtils;

import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.dto.address.ZipDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * Builder between ZipBO and ZipDTO.
 *
 * @author Gleb Zgonikov
 */
public class ZipDTOBuilder extends AbstractDTOBuilder<ZipCodeEntity, ZipDTO> {

    private final CountryDTOBuilder countryDTOBuilder = new CountryDTOBuilder();

    @Override
    public ZipDTO buildDTO(ZipCodeEntity zipCode) {
        ZipDTO dto = new ZipDTO();
        dto.setCity(zipCode.getCity());
        dto.setPrefCity(zipCode.getPrefCity());
        dto.setCountry(countryDTOBuilder.buildDTO(zipCode.getId().getCountry()));
        dto.setState(zipCode.getStateCode());
        dto.setZip(zipCode.getZipCode());
        if (BooleanUtils.isTrue(zipCode.getWarning())) {
            dto.setWarning(zipCode.getWarning());
        }
        return dto;
    }

    /**
     * Method is not supported.
     * 
     * @param zipDTO
     *            dto
     * @return nothing
     * @throws UnsupportedOperationException
     */
    @Override
    public ZipCodeEntity buildEntity(ZipDTO zipDTO) {
        throw new UnsupportedOperationException();
    }
}
