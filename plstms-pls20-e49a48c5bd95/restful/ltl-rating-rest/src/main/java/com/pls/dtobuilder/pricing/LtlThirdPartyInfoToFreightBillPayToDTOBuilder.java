package com.pls.dtobuilder.pricing;

import com.pls.dto.FreightBillPayToDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.PhoneDTOBuilder;
import com.pls.dtobuilder.address.AddressBookEntryDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;

/**
 * Builder for conversion from {@link LtlPricingThirdPartyInfoEntity} to {@link FreightBillPayToDTO}.
 * 
 * @author Aleksandr Leshchenko
 */
public class LtlThirdPartyInfoToFreightBillPayToDTOBuilder extends AbstractDTOBuilder<LtlPricingThirdPartyInfoEntity, FreightBillPayToDTO> {
    private final AddressBookEntryDTOBuilder addressBookEntryDTOBuilder = new AddressBookEntryDTOBuilder();
    private final PhoneDTOBuilder phoneDTOBuilder = new PhoneDTOBuilder();

    @Override
    public FreightBillPayToDTO buildDTO(LtlPricingThirdPartyInfoEntity entity) {
        FreightBillPayToDTO dto = new FreightBillPayToDTO();
        dto.setAccountNum(entity.getAccountNum());
        dto.setCompany(entity.getCompany());
        dto.setContactName(entity.getContactName());
        dto.setPhone(phoneDTOBuilder.buildDTO(entity.getPhone()));
        dto.setAddress(addressBookEntryDTOBuilder.buildDTO(entity.getAddress()));
        dto.setEmail(entity.getEmail());
        return dto;
    }

    /**
     * This method always throws {@link UnsupportedOperationException}.
     * 
     * @param dto
     *            to convert
     * @return nothing
     */
    @Override
    public LtlPricingThirdPartyInfoEntity buildEntity(FreightBillPayToDTO dto) {
        throw new UnsupportedOperationException();
    }
}
