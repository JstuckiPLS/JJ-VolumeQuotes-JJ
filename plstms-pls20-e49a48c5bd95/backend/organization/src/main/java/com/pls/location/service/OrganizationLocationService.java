package com.pls.location.service;

import java.util.List;

import com.pls.core.domain.bo.AssociatedCustomerLocationBO;
import com.pls.core.domain.bo.CustomerLocationListItemBO;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;

/**
 * Service for {@link OrganizationLocationEntity}.
 *
 * @author Aleksandr Leshchenko
 */
public interface OrganizationLocationService {

    /**
     * Get list of locations for specified customer with information for specified user that current user has access to.
     * 
     * @param customerId
     *            id of customer.
     * @param currentUserId
     *            id of currently logged in user
     * @param userId
     *            id of user to get modification information
     * @return list of locations for specified customer that current user has access to.
     */
    List<AssociatedCustomerLocationBO> getAssociatedCustomerLocations(Long customerId, Long currentUserId, Long userId);

    /**
     * Get list of customers' locations available for shipment creation for specified user.
     * 
     * @param customerId
     *            id of customer.
     * @param personId
     *            id of user
     * @param long1
     * @return list of shipment locations for customer.
     */
    List<ShipmentLocationBO> getShipmentLocations(Long customerId, Long personId);

    /**
     * Get list of locations for specified customer.
     * 
     * @param organizationId
     *            id of organization.
     * @return list of locations.
     */
    List<CustomerLocationListItemBO> getCustomerLocations(Long organizationId);

    /**
     * Returns {@link OrganizationLocationEntity} by specified Id.
     * 
     * @param locationId Location Id.
     * @return {@link OrganizationLocationEntity}.
     */
    OrganizationLocationEntity getOrganizationLocation(Long locationId);

    /**
     * Returns {@link UserAdditionalContactInfoBO } by location Id.
     * 
     * @param locationId
     *            id of location
     * @return {@link UserAdditionalContactInfoBO}.
     */
    UserAdditionalContactInfoBO getAccountExecutiveByLocationId(Long locationId);

    /**
     * Save specified {@link OrganizationLocationEntity}.
     * 
     * @param entity - entity to be saved.
     * @param oldPersonId - id of old user.
     */
    void saveOrganizationLocation(OrganizationLocationEntity entity, Long oldPersonId);
}
