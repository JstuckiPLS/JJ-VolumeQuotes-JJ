package com.pls.dtobuilder.address;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.AddressNotificationsEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.shared.Status;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.AddressNotificationsDTOBuilder;
import com.pls.dtobuilder.PhoneDTOBuilder;
import com.pls.dtobuilder.util.PickUpAndDeliveryDTOBuilder;

/**
 * DTO Builder for Address Book.<br>
 * {@link DataProvider} must be passed as constructor parameter to build entity before update.
 * 
 * @author Aleksandr Leshchenko
 */
public class AddressBookDTOBuilder extends AbstractDTOBuilder<UserAddressBookEntity, AddressBookEntryDTO> {
    private static final PickUpAndDeliveryDTOBuilder PICK_UP_AND_DELIVERY_DTO_BUILDER = new PickUpAndDeliveryDTOBuilder();
    private AddressNotificationsDTOBuilder addressNotificationsDTOBuilder;

    private final DataProvider dataProvider;

    /**
     * Default empty constructor.
     */
    public AddressBookDTOBuilder() {
        this(null, null);
    }

    /**
     * Constructor.
     * Should be used for update operations.
     * 
     * @param dataProvider
     *            data provider for update
     * @param notificationsProvider
     *            data provider for notifications
     */
    public AddressBookDTOBuilder(DataProvider dataProvider, AddressNotificationsDTOBuilder.DataProvider notificationsProvider) {
        this.dataProvider = dataProvider;
        this.addressNotificationsDTOBuilder = new AddressNotificationsDTOBuilder(notificationsProvider);
    }

    @Override
    public AddressBookEntryDTO buildDTO(UserAddressBookEntity bo) {
        AddressBookEntryDTO dto = new AddressBookEntryDTOBuilder().buildDTO(bo.getAddress());
        dto.setId(bo.getId());
        dto.setAddressName(bo.getAddressName());
        dto.setContactName(bo.getContactName());
        dto.setPickupWindowFrom(PICK_UP_AND_DELIVERY_DTO_BUILDER.buildDTO(bo.getPickupFrom()));
        dto.setPickupWindowTo(PICK_UP_AND_DELIVERY_DTO_BUILDER.buildDTO(bo.getPickupTo()));
        dto.setAddressCode(bo.getAddressCode());
        dto.setEmail(bo.getEmail());
        dto.setPickupNotes(bo.getPickupNotes());
        dto.setDeliveryNotes(bo.getDeliveryNotes());
        dto.setInternalPickupNotes(bo.getInternalPickupNotes());
        dto.setInternalDeliveryNotes(bo.getInternalDeliveryNotes());
        dto.setPhone(new PhoneDTOBuilder().buildDTO(bo.getPhone()));
        if (bo.getFax() != null) {
            if (StringUtils.isNotBlank(bo.getFax().getExtension())) {
                bo.getFax().setExtension(null);
            }
            dto.setFax(new PhoneDTOBuilder().buildDTO(bo.getFax()));
        }
        dto.setSharedAddress(bo.getPersonId() == null);
        dto.setCreatedBy(bo.getModification().getCreatedBy());
        dto.setVersion(bo.getVersion());
        dto.setDeliveryWindowFrom(PICK_UP_AND_DELIVERY_DTO_BUILDER.buildDTO(bo.getDeliveryFrom()));
        dto.setDeliveryWindowTo(PICK_UP_AND_DELIVERY_DTO_BUILDER.buildDTO(bo.getDeliveryTo()));
        dto.setIsDefault(bo.getIsDefault());
        if (bo.getType() != null) {
            dto.setType(bo.getType().name());
        }
        if (bo.getAddressNotifications() != null) {
            dto.setShipmentNotifications(addressNotificationsDTOBuilder.buildList(bo.getAddressNotifications()));
        }
        return dto;
    }

    @Override
    public UserAddressBookEntity buildEntity(AddressBookEntryDTO dto) {
        UserAddressBookEntity entity = getNewOrExistingUserAddressBookEntity(dto.getId());
        entity.setAddressName(dto.getAddressName());
        entity.setAddress(getAddressBuilder(entity.getAddress()).buildEntity(dto));
        entity.setContactName(dto.getContactName());
        buildPickupAndDeliveryWindow(dto, entity);
        entity.setAddressCode(dto.getAddressCode());
        entity.setEmail(dto.getEmail());
        entity.setPickupNotes(dto.getPickupNotes());
        entity.setDeliveryNotes(dto.getDeliveryNotes());
        entity.setInternalPickupNotes(dto.getInternalPickupNotes());
        entity.setInternalDeliveryNotes(dto.getInternalDeliveryNotes());
        entity.setPhone(buildPhoneEntity(dto.getPhone(), entity.getPhone()));
        PhoneEntity fax = buildPhoneEntity(dto.getFax(), entity.getFax());
        if (fax != null) {
            fax.setType(PhoneType.FAX);
        }
        entity.setFax(fax);

        if (entity.getId() == null) {
            entity.setStatus(Status.ACTIVE);
        }
        if (dto.getVersion() != null) {
            entity.setVersion(dto.getVersion());
        }
        if (dto.getType() != null) {
            entity.setType(AddressType.getByName(dto.getType()));
        }
        entity.setIsDefault(dto.getIsDefault());
        List<AddressNotificationsEntity> notifications = null;
        if (dto.getShipmentNotifications() != null) {
            notifications = addressNotificationsDTOBuilder.buildEntityList(dto.getShipmentNotifications());
        }
        if (entity.getAddressNotifications() == null) {
            entity.setAddressNotifications(new HashSet<AddressNotificationsEntity>());
        }
        entity.getAddressNotifications().clear();
        if (notifications != null) {
            entity.getAddressNotifications().addAll(notifications.stream().collect(Collectors.toSet()));
            notifications.stream().forEach((notification) -> notification.setAddress(entity));
        }
        return entity;
    }

    private void buildPickupAndDeliveryWindow(AddressBookEntryDTO dto, UserAddressBookEntity entity) {
        entity.setPickupFrom(PICK_UP_AND_DELIVERY_DTO_BUILDER.buildEntity(dto.getPickupWindowFrom()));
        entity.setPickupTo(PICK_UP_AND_DELIVERY_DTO_BUILDER.buildEntity(dto.getPickupWindowTo()));
        entity.setDeliveryFrom(PICK_UP_AND_DELIVERY_DTO_BUILDER.buildEntity(dto.getDeliveryWindowFrom()));
        entity.setDeliveryTo(PICK_UP_AND_DELIVERY_DTO_BUILDER.buildEntity(dto.getDeliveryWindowTo()));
    }

    private PhoneEntity buildPhoneEntity(PhoneBO phoneDTO, final PhoneEntity phoneEntity) {
        PhoneEntity phoneEntityResult = null;
        if (phoneDTO != null) {
            phoneEntityResult = new PhoneDTOBuilder(new PhoneDTOBuilder.DataProvider() {
                @Override
                public PhoneEntity getPhone() {
                    return phoneEntity;
                }
            }).buildEntity(phoneDTO);
        }
        return phoneEntityResult;
    }

    private UserAddressBookEntity getNewOrExistingUserAddressBookEntity(Long id) {
        UserAddressBookEntity entity = null;
        if (id != null) {
            if (dataProvider != null) {
                entity = dataProvider.getAddress(id);
            } else {
                throw new IllegalArgumentException("For update dataProvider must be used");
            }
        }
        if (entity == null) {
            entity = new UserAddressBookEntity();
            //id with value -1 means address must isn't modified adn this id should be kept for shipment processing
            if (id != null && id == -1L) {
                entity.setId(id);
            }
        }
        return entity;
    }

    private AddressBookEntryDTOBuilder getAddressBuilder(final AddressEntity addressEntity) {
        if (addressEntity == null) {
            return new AddressBookEntryDTOBuilder();
        } else {
            return new AddressBookEntryDTOBuilder(new AddressBookEntryDTOBuilder.DataProvider() {
                @Override
                public AddressEntity getAddress() {
                    return addressEntity;
                }

                @Override
                public AddressBookEntryDTO getDto() {
                    return null;
                }
            });
        }
    }

    /**
     * Address book data provider for update.
     * 
     * @author Aleksandr Leshchenko
     */
    public interface DataProvider {
        /**
         * Get Address book entry for update.
         * 
         * @param id
         *            {@link com.pls.core.domain.address.UserAddressBookEntity#getId()}
         * @return {@link com.pls.core.domain.address.UserAddressBookEntity}
         */
        UserAddressBookEntity getAddress(Long id);
    }
}
