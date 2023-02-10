package com.pls.dtobuilder.address;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO Builder for Address book.<br>
 * {@link DataProvider} must be passed as constructor parameter to build entity before update.
 * 
 * @author Aleksandr Leshchenko
 */
public class AddressBookEntryDTOBuilder extends AbstractDTOBuilder<AddressEntity, AddressBookEntryDTO> {
    private final DataProvider dataProvider;

    private final ZipDTOBuilder zipDTOBuilder = new ZipDTOBuilder();
    private final CountryDTOBuilder countryDTOBuilder = new CountryDTOBuilder();

    /**
     * Default constructor without data provider.<br>
     * Should be used for create operations and for building DTO.
     */
    public AddressBookEntryDTOBuilder() {
        this(null);
    }

    /**
     * Constructor.<br>
     * Should be used for update operations.
     * 
     * @param dataProvider
     *            data provider for update
     */
    public AddressBookEntryDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public AddressBookEntryDTO buildDTO(AddressEntity bo) {
        AddressBookEntryDTO dto = null;
        if (bo != null) {
            dto = getNewOrExistingDTO();
            dto.setAddressId(bo.getId());
            dto.setAddress1(bo.getAddress1());
            dto.setAddress2(bo.getAddress2());
            ZipDTO zipDTO = zipDTOBuilder.buildDTO(bo.getZipCode());
            dto.setZip(zipDTO);
            dto.setCountry(zipDTO.getCountry());
            dto.setLatitude(bo.getLatitude());
            dto.setLongitude(bo.getLongitude());
        }
        return dto;
    }

    @Override
    public AddressEntity buildEntity(AddressBookEntryDTO dto) {
        AddressEntity entity = null;
        if (dto != null) {
            entity = getNewOrExistingAddress();
            entity.setAddress1(dto.getAddress1());
            entity.setAddress2(dto.getAddress2());
            entity.setCity(dto.getZip().getCity());
            entity.setCountry(countryDTOBuilder.buildEntity(dto.getCountry()));
            entity.setState(getState(dto.getZip()));
            entity.setZip(dto.getZip().getZip());
            entity.setLatitude(dto.getLatitude());
            entity.setLongitude(dto.getLongitude());
        }
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

    private AddressEntity getNewOrExistingAddress() {
        AddressEntity entity = null;
        if (dataProvider != null) {
            entity = dataProvider.getAddress();
        }
        if (entity == null) {
            entity = new AddressEntity();
        }
        return entity;
    }

    private AddressBookEntryDTO getNewOrExistingDTO() {
        AddressBookEntryDTO dto = null;
        if (dataProvider != null) {
            dto = dataProvider.getDto();
        }
        if (dto == null) {
            dto = new AddressBookEntryDTO();
        }
        return dto;
    }

    /**
     * Address data provider for update.
     * 
     * @author Aleksandr Leshchenko
     */
    public interface DataProvider {
        /**
         * Get Address for update.
         * 
         * @return {@link AddressEntity}
         */
        AddressEntity getAddress();

        /**
         * Get dto to fill.
         *
         * @return {@link com.pls.dto.address.AddressBookEntryDTO}
         */
        AddressBookEntryDTO getDto();
    }
}
