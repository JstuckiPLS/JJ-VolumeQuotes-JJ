package com.pls.dtobuilder.pricing;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.shared.Status;
import com.pls.dto.LtlThirdPartyInfoDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.address.PlainAddressDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;

/**
 * Builder for conversion between {@link LtlThirdPartyInfoDTO} and {@link LtlPricingThirdPartyInfoEntity}.
 * 
 * @author Artem Arapov
 * 
 */
public class LtlThirdPartyInfoDTOBuilder extends
        AbstractDTOBuilder<LtlPricingThirdPartyInfoEntity, LtlThirdPartyInfoDTO> {

    private PlainAddressDTOBuilder addressBuilder = new PlainAddressDTOBuilder();

    @Override
    public LtlThirdPartyInfoDTO buildDTO(LtlPricingThirdPartyInfoEntity bo) {
        LtlThirdPartyInfoDTO dto = new LtlThirdPartyInfoDTO();

        if (bo == null) {
            return dto;
        }

        dto.setId(bo.getId());
        dto.setProfileDetailId(bo.getPricProfDetailId());
        dto.setCompany(bo.getCompany());
        dto.setContactName(bo.getContactName());

        if (bo.getAddress() != null) {
            dto.setAddress(addressBuilder.buildDTO(bo.getAddress()));
        }

        setUpPhoneDTO(bo.getPhone(), dto);
        setUpFaxDTO(bo.getFax(), dto);

        dto.setEmail(bo.getEmail());
        dto.setAccountNum(bo.getAccountNum());
        dto.setVersion(bo.getVersion());

        return dto;
    }

    @Override
    public LtlPricingThirdPartyInfoEntity buildEntity(LtlThirdPartyInfoDTO dto) {
        LtlPricingThirdPartyInfoEntity entity = new LtlPricingThirdPartyInfoEntity();

        entity.setId(dto.getId());
        entity.setPricProfDetailId(dto.getProfileDetailId());
        entity.setCompany(dto.getCompany());
        entity.setContactName(dto.getContactName());
        entity.setAddress(addressBuilder.buildEntity(dto.getAddress()));
        setUpPhoneEntity(dto.getPhone(), entity);
        setUpFax(dto.getFax(), entity);
        entity.setEmail(dto.getEmail());
        entity.setAccountNum(dto.getAccountNum());
        entity.setStatus(Status.ACTIVE);

        if (dto.getVersion() != null) {
            entity.setVersion(dto.getVersion());
        }

        return entity;
    }

    private void setUpPhoneDTO(PhoneEntity phone, LtlThirdPartyInfoDTO dto) {
        if (phone != null) {
            PhoneBO phoneDTO = new PhoneBO();
            phoneDTO.setCountryCode(phone.getCountryCode());
            phoneDTO.setAreaCode(phone.getAreaCode());
            phoneDTO.setNumber(phone.getNumber());
            dto.setPhone(phoneDTO);
        }
    }

    private void setUpFaxDTO(PhoneEntity phone, LtlThirdPartyInfoDTO dto) {
        if (phone != null) {
            PhoneBO faxDTO = new PhoneBO();
            faxDTO.setCountryCode(phone.getCountryCode());
            faxDTO.setAreaCode(phone.getAreaCode());
            faxDTO.setNumber(phone.getNumber());
            dto.setFax(faxDTO);
        }
    }

    private void setUpPhoneEntity(PhoneBO dto, LtlPricingThirdPartyInfoEntity entity) {
        if (!isPhoneEmpty(dto)) {
            PhoneEntity phone = entity.getPhone();

            if (phone == null) {
                phone = new PhoneEntity();
                phone.setType(PhoneType.VOICE);
            }

            phone.setAreaCode(dto.getAreaCode());
            phone.setCountryCode(dto.getCountryCode());
            phone.setNumber(dto.getNumber());
            entity.setPhone(phone);
        }
    }

    private void setUpFax(PhoneBO dto, LtlPricingThirdPartyInfoEntity entity) {
        if (!isPhoneEmpty(dto)) {
            PhoneEntity fax = entity.getFax();

            if (fax == null) {
                fax = new PhoneEntity();
                fax.setType(PhoneType.FAX);
            }

            fax.setAreaCode(dto.getAreaCode());
            fax.setCountryCode(dto.getCountryCode());
            fax.setNumber(dto.getNumber());
            entity.setFax(fax);
        }
    }

    private boolean isPhoneEmpty(PhoneBO phone) {
        return phone == null
                || (phone.getCountryCode() == null || phone.getCountryCode().isEmpty())
                && (phone.getAreaCode() == null || phone.getAreaCode().isEmpty())
                && (phone.getNumber() == null || phone.getNumber().isEmpty());
    }
}
