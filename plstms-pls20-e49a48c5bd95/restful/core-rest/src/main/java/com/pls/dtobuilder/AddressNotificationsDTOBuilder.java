package com.pls.dtobuilder;

import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.address.AddressNotificationsEntity;
import com.pls.dto.ShipmentNotificationsDTO;

/**
 * The Class AddressNotificationsDTOBuilder.
 * 
 * @author Sergii Belodon
 */
public class AddressNotificationsDTOBuilder extends AbstractDTOBuilder<AddressNotificationsEntity, ShipmentNotificationsDTO> {
    private final DataProvider dataProvider;

    /**
     * Instantiates a new address notifications dto builder.
     *
     * @param provider the provider
     */
    public AddressNotificationsDTOBuilder(DataProvider provider) {
        this.dataProvider = provider;
    }

    @Override
    public ShipmentNotificationsDTO buildDTO(AddressNotificationsEntity entity) {
        ShipmentNotificationsDTO dto = new ShipmentNotificationsDTO();
        dto.setId(entity.getId());
        dto.setNotificationType(entity.getNotificationType().getId());
        dto.setDirection(entity.getDirection());
        dto.setEmailAddress(entity.getEmail());
        return dto;
    }

    @Override
    public AddressNotificationsEntity buildEntity(ShipmentNotificationsDTO dto) {
        AddressNotificationsEntity entity;
        if (dto.getId() != null) {
            entity = dataProvider.getAddressNotification(dto.getId());
        } else {
            entity = new AddressNotificationsEntity();
        }

        entity.setNotificationType(new NotificationTypeEntity());
        entity.getNotificationType().setId(dto.getNotificationType());
        entity.setDirection(dto.getDirection());
        entity.setEmail(dto.getEmailAddress());
        return entity;
    }

    /**
     * The Interface DataProvider.
     * 
     * @author Sergii Belodon
     */
    public interface DataProvider {
        /**
         * Gets the address notification.
         *
         * @param id the id
         * @return the address notification
         */
        AddressNotificationsEntity getAddressNotification(Long id);
    }
}