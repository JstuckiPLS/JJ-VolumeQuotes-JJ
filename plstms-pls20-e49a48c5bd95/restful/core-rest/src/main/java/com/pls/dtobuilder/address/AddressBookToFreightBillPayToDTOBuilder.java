package com.pls.dtobuilder.address;

import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.dto.FreightBillPayToDTO;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.PhoneDTOBuilder;

/**
 * DTO Builder for Freight Bill Pay To.
 * 
 * @author Aleksandr Leshchenko
 */
public class AddressBookToFreightBillPayToDTOBuilder extends AbstractDTOBuilder<UserAddressBookEntity, FreightBillPayToDTO> {
    private static final PhoneDTOBuilder PHONE_BUILDER = new PhoneDTOBuilder();
    private static final CountryDTOBuilder COUNTRY_BUILDER = new CountryDTOBuilder();
    private static final ZipDTOBuilder ZIP_BUILDER = new ZipDTOBuilder();

    @Override
    public FreightBillPayToDTO buildDTO(UserAddressBookEntity bo) {
        FreightBillPayToDTO dto = new FreightBillPayToDTO();
        dto.setCompany(bo.getAddressName());
        dto.setContactName(bo.getContactName());
        dto.setEmail(bo.getEmail());
        dto.setPhone(PHONE_BUILDER.buildDTO(bo.getPhone()));
        AddressBookEntryDTO address = new AddressBookEntryDTO();
        address.setAddressCode(bo.getAddressCode());
        address.setAddress1(bo.getAddress().getAddress1());
        address.setAddress2(bo.getAddress().getAddress2());
        address.setCountry(COUNTRY_BUILDER.buildDTO(bo.getAddress().getCountry()));
        address.setZip(ZIP_BUILDER.buildDTO(bo.getAddress().getZipCode()));
        dto.setAddress(address);
        return dto;
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     */
    @Override
    public UserAddressBookEntity buildEntity(FreightBillPayToDTO dto) {
        throw new UnsupportedOperationException();
    }
}
