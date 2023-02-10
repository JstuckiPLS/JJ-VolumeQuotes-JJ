package com.pls.dtobuilder.shipment;

import com.pls.core.domain.NotificationTypeEntity;
import com.pls.dto.ShipmentNotificationsDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.LoadNotificationsEntity;

/**
 * Builder to build between {@link LoadNotificationsEntity} and {@link ShipmentNotificationsDTO}.
 *
 * @author Alexander Kirichenko
 */
public class ShipmentNotificationsDTOBuilder
        extends AbstractDTOBuilder<LoadNotificationsEntity, ShipmentNotificationsDTO> {
    @Override
    public ShipmentNotificationsDTO buildDTO(LoadNotificationsEntity entity) {
        ShipmentNotificationsDTO dto = new ShipmentNotificationsDTO();
        dto.setNotificationType(entity.getNotificationType().getId());
        dto.setEmailAddress(entity.getEmailAddress());
        dto.setNotificationSource(entity.getNotificationSource());
        return dto;
    }

    @Override
    public LoadNotificationsEntity buildEntity(ShipmentNotificationsDTO dto) {
        LoadNotificationsEntity entity = new LoadNotificationsEntity();
        entity.setNotificationType(new NotificationTypeEntity());
        entity.getNotificationType().setId(dto.getNotificationType());
        entity.setEmailAddress(dto.getEmailAddress());
        entity.setNotificationSource(dto.getNotificationSource());
        return entity;
    }
}
