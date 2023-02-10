package com.pls.dtobuilder;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.PhoneType;

/**
 * DTO builder for {@link PhoneEntity}.
 *
 * @author Mikhail Boldinov, 30/05/13
 */
public class PhoneDTOBuilder extends AbstractDTOBuilder<PhoneEntity, PhoneBO> {
    private final DataProvider dataProvider;

    /**
     * Default empty constructor.
     */
    public PhoneDTOBuilder() {
        this(null);
    }

    /**
     * Constructor.
     * Should be used for update operations.
     *
     * @param dataProvider
     *            data provider for update
     */
    public PhoneDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }


    @Override
    public PhoneBO buildDTO(PhoneEntity entity) {
        PhoneBO dto = new PhoneBO();
        if (entity != null) {
            dto.setCountryCode(entity.getCountryCode());
            dto.setAreaCode(entity.getAreaCode());
            dto.setNumber(entity.getNumber());
            dto.setExtension(entity.getExtension());
        }
        return dto;
    }

    @Override
    public PhoneEntity buildEntity(PhoneBO dto) {
        PhoneEntity entity = null;
        if (isPhoneEmpty(dto)) {
            return entity;
        }
        if (dataProvider != null) {
            entity = dataProvider.getPhone();
        }

        if (entity == null) {
            entity = new PhoneEntity();
        }

        try {
            entity.setCountryCode(String.valueOf(Integer.valueOf(dto.getCountryCode())));
        } catch (NumberFormatException e) {
            entity.setCountryCode(dto.getCountryCode());
        }
        entity.setAreaCode(dto.getAreaCode());
        entity.setNumber(dto.getNumber());
        entity.setExtension(dto.getExtension());
        entity.setType(PhoneType.VOICE);
        return entity;
    }

    private boolean isPhoneEmpty(PhoneBO phone) {
        return phone == null || StringUtils.isEmpty(phone.getCountryCode()) && StringUtils.isEmpty(phone.getAreaCode())
                && StringUtils.isEmpty(phone.getNumber());
    }

    /**
     * Phone data provider for update.
     */
    public interface DataProvider {
        /**
         * Get Address book entry for update.
         *
         * @return {@link PhoneEntity}
         */
        PhoneEntity getPhone();
    }
}
