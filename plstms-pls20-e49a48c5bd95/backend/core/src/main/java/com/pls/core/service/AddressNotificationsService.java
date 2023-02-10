package com.pls.core.service;

import com.pls.core.domain.address.AddressNotificationsEntity;

/**
 * The Interface AddressNotificationsService.
 * 
 * @author Sergii Belodon
 */
public interface AddressNotificationsService {
    /**
     * Gets the address notification by id.
     *
     * @param id the id
     * @return the address notification by id
     */
    AddressNotificationsEntity getAddressNotificationById(Long id);
}
