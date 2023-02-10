package com.pls.dtobuilder.pricing;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.shared.Status;
import com.pls.dto.LtlTerminalInfoDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.address.PlainAddressDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;

/**
 * Builder for conversion between {@link LtlTerminalInfoDTO} and {@link LtlPricingTerminalInfoEntity}.
 * 
 * @author Artem Arapov
 * 
 */
public class LtlTerminalInfoDTOBuilder extends AbstractDTOBuilder<LtlPricingTerminalInfoEntity, LtlTerminalInfoDTO> {

    private PlainAddressDTOBuilder addressBuilder = new PlainAddressDTOBuilder();

    @Override
    public LtlTerminalInfoDTO buildDTO(LtlPricingTerminalInfoEntity bo) {
        LtlTerminalInfoDTO dto = new LtlTerminalInfoDTO();

        if (bo == null) {
            return dto;
        }

        dto.setId(bo.getId());
        dto.setProfileId(bo.getPriceProfileId());
        dto.setContactName(bo.getContactName());
        dto.setTerminal(bo.getTerminal());
        dto.setVisible(bo.getVisible());
        dto.setEmail(bo.getEmail());
        dto.setAccountNum(bo.getAccountNum());
        dto.setVersion(bo.getVersion());
        dto.setTransiteTime(bo.getTransiteTime());

        if (bo.getAddress() != null) {
            dto.setAddress(addressBuilder.buildDTO(bo.getAddress()));
        }

        if (bo.getPhone() != null) {
            PhoneBO phone = new PhoneBO();
            phone.setCountryCode(bo.getPhone().getCountryCode());
            phone.setAreaCode(bo.getPhone().getAreaCode());
            phone.setNumber(bo.getPhone().getNumber());
            dto.setPhone(phone);
        }

        if (bo.getFax() != null) {
            PhoneBO fax = new PhoneBO();
            fax.setCountryCode(bo.getFax().getCountryCode());
            fax.setAreaCode(bo.getFax().getAreaCode());
            fax.setNumber(bo.getFax().getNumber());
            dto.setFax(fax);
        }

        return dto;
    }

    @Override
    public LtlPricingTerminalInfoEntity buildEntity(LtlTerminalInfoDTO dto) {
        LtlPricingTerminalInfoEntity result = new LtlPricingTerminalInfoEntity();

        result.setId(dto.getId());
        result.setPriceProfileId(dto.getProfileId());
        result.setTerminal(dto.getTerminal());
        result.setContactName(dto.getContactName());
        result.setAddress(addressBuilder.buildEntity(dto.getAddress()));
        setUpPhone(dto.getPhone(), result);
        setUpFax(dto.getFax(), result);
        result.setEmail(dto.getEmail());
        result.setAccountNum(dto.getAccountNum());
        result.setStatus(Status.ACTIVE);
        result.setTransiteTime(dto.getTransiteTime());
        result.setVisible(dto.getVisible());

        if (dto.getVersion() != null) {
            result.setVersion(dto.getVersion());
        }

        return result;
    }

    private void setUpPhone(PhoneBO dto, LtlPricingTerminalInfoEntity entity) {
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

    private void setUpFax(PhoneBO dto, LtlPricingTerminalInfoEntity entity) {
        if (!isPhoneEmpty(dto)) {
            PhoneEntity faxObj = entity.getFax();

            if (faxObj == null) {
                faxObj = new PhoneEntity();
                faxObj.setType(PhoneType.FAX);
            }

            faxObj.setAreaCode(dto.getAreaCode());
            faxObj.setCountryCode(dto.getCountryCode());
            faxObj.setNumber(dto.getNumber());
            entity.setFax(faxObj);
        }
    }

    private boolean isPhoneEmpty(PhoneBO phone) {
        return phone == null || (phone.getCountryCode() == null || phone.getCountryCode().isEmpty())
                && (phone.getAreaCode() == null || phone.getAreaCode().isEmpty())
                && (phone.getNumber() == null || phone.getNumber().isEmpty());
    }
}
