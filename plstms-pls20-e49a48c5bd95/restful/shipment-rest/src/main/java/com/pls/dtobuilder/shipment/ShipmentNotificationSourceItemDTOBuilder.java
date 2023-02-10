package com.pls.dtobuilder.shipment;

import com.pls.core.domain.PhoneNumber;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.dto.shipment.ShipmentNotificationSourceItemDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.bo.ShipmentNotificationSourceItemBo;

/**
 * DTO builder to build {@link ShipmentNotificationSourceItemDTO} from {@link CustomerUserEntity}.
 * 
 * @author Alexander Kirichenko
 */
public class ShipmentNotificationSourceItemDTOBuilder extends AbstractDTOBuilder<ShipmentNotificationSourceItemBo,
        ShipmentNotificationSourceItemDTO> {

    @Override
    public ShipmentNotificationSourceItemDTO buildDTO(ShipmentNotificationSourceItemBo entity) {
        ShipmentNotificationSourceItemDTO dto = new ShipmentNotificationSourceItemDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setName(entity.getName());
        dto.setContactName(entity.getContactName());
        dto.setOrigin(entity.getOrigin());
        dto.setPhone(createDto(entity.getPhone()));
        return dto;
    }

    @Override
    public ShipmentNotificationSourceItemBo buildEntity(ShipmentNotificationSourceItemDTO shipmentNotificationSourceItemDTO) {
        throw new UnsupportedOperationException();
    }

    private PhoneBO createDto(PhoneNumber phone) {
        PhoneBO result = null;
        if (phone != null) {
            result = new PhoneBO();
            result.setCountryCode(phone.getCountryCode());
            result.setAreaCode(phone.getAreaCode());
            result.setNumber(phone.getNumber());
            result.setExtension(phone.getExtension());
        }
        return result;
    }
}
