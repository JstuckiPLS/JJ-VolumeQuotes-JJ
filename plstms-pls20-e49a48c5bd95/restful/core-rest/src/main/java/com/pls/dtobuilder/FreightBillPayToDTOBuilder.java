package com.pls.dtobuilder;

import com.pls.core.dao.impl.FreightBillPayToDaoImpl;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.PhoneType;
import com.pls.dto.FreightBillPayToDTO;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dtobuilder.address.AddressBookEntryDTOBuilder;

/**
 * FreightBillPayToDTO Builder.
 * 
 * @author Aleksandr Leshchenko
 */
public class FreightBillPayToDTOBuilder extends AbstractDTOBuilder<FreightBillPayToEntity, FreightBillPayToDTO> {
    private AddressBookEntryDTOBuilder addressDTOBuilder = new AddressBookEntryDTOBuilder();
    private PhoneDTOBuilder phoneDTOBuilder = new PhoneDTOBuilder();

    private DataProvider dataProvider;

    @Override
    public FreightBillPayToDTO buildDTO(FreightBillPayToEntity entity) {
        FreightBillPayToDTO dto = new FreightBillPayToDTO();
        dto.setId(entity.getId());
        dto.setAccountNum(entity.getAccountNum());
        dto.setCompany(entity.getCompany());
        dto.setContactName(entity.getContactName());
        dto.setPhone(phoneDTOBuilder.buildDTO(entity.getPhone()));
        dto.setAddress(addressDTOBuilder.buildDTO(entity.getAddress()));
        dto.setEmail(entity.getEmail());
        return dto;
    }

    @Override
    public FreightBillPayToEntity buildEntity(FreightBillPayToDTO dto) {
        final FreightBillPayToEntity entity = getEntity();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setAccountNum(dto.getAccountNum());
        entity.setCompany(dto.getCompany());
        entity.setContactName(dto.getContactName());
        entity.setPhone(buildPhone(entity.getPhone(), dto.getPhone(), PhoneType.VOICE));
        entity.setAddress(new AddressBookEntryDTOBuilder(new AddressBookEntryDTOBuilder.DataProvider() {
            @Override
            public AddressEntity getAddress() {
                return entity.getAddress();
            }

            @Override
            public AddressBookEntryDTO getDto() {
                return null;
            }
        }).buildEntity(dto.getAddress()));
        entity.setEmail(dto.getEmail());
        return entity;
    }

    private FreightBillPayToEntity getEntity() {
        FreightBillPayToEntity entity = null;
        if (dataProvider != null) {
            entity = dataProvider.getFreightBillPayTo();
        }
        if (entity == null || FreightBillPayToDaoImpl.DEFAULT_FREIGHT_BILL_PAY_TO_ID.equals(entity.getId())) {
            entity = new FreightBillPayToEntity();
        }
        return entity;
    }

    private PhoneEntity buildPhone(final PhoneEntity phone, PhoneBO dto, PhoneType type) {
        PhoneEntity entity = null;
        if (dto != null) {
            entity = new PhoneDTOBuilder(new PhoneDTOBuilder.DataProvider() {
                @Override
                public PhoneEntity getPhone() {
                    return phone;
                }
            }).buildEntity(dto);
            if (entity != null) {
                entity.setType(type);
            }
        }

        return entity;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * Data provider to build entity.
     */
    public interface DataProvider {
        /**
         * Get {@link FreightBillPayToEntity}.
         * 
         * @return {@link FreightBillPayToEntity} or <code>null</code>
         */
        FreightBillPayToEntity getFreightBillPayTo();
    }
}
