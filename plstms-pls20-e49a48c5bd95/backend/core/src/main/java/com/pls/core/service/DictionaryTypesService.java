package com.pls.core.service;

import java.util.List;

import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.bo.KeyValueBO;

/**
 * Service to obtain dictionary/enumeration data which are changed  very seldom.
 *
 * @author Alexander Kirichenko
 */
public interface DictionaryTypesService {

    /**
     * Get all {@link NotificationTypeEntity}.
     *
     * @return {@link List} of {@link NotificationTypeEntity}.
     */
    List<NotificationTypeEntity> getNotificationTypes();

    /**
     * Get {@link NotificationTypeEntity} by id.
     *
     * @param id id of notification type
     * @return {@link NotificationTypeEntity}.
     */
    NotificationTypeEntity getNotificationTypesById(String id);

    /**
     * Get customer payment terms.
     * 
     * @return customer payment terms
     */
    List<KeyValueBO> getCustomerPayTerms();

    /**
     * Get customer payment methods.
     * 
     * @return customer payment methods
     */
    List<LookupValueEntity> getCustomerPayMethod();

}
