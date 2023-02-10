package com.pls.location.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.bo.CustomerLocationListItemBO;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.organization.OrganizationLocationEntity;

/**
 * DAO for {@link OrganizationLocationEntity}.
 *
 * @author Aleksandr Leshchenko
 */
public interface OrganizationLocationDao extends AbstractDao<OrganizationLocationEntity, Long> {

    /**
     * Get list of locations for specified customer.
     * 
     * @param organizationId
     *            id of organization.
     * @return list of locations.
     */
    List<CustomerLocationListItemBO> getCustomerLocations(Long organizationId);

    /**
     * Get list of customers' locations available for shipment creation for specified user.
     * 
     * @param customerId
     *            id of customer.
     * @param personId
     *            id of user
     * @return list of shipment locations for customer.
     */
    List<ShipmentLocationBO> getShipmentLocations(Long customerId, Long personId);

    /**
     * Checks is specified personId assigned for locations of specified orgId excluding specified locationId.
     * 
     * @param personId
     *            - id of user which should be checked
     * @param orgId
     *            - id of customer
     * @param locationId
     *            - id of location which should be excluded
     * @return True if there is another location assigned to customer. Otherwise returns False.
     */
    Boolean isAEExistsForLocationsExcludeSpecified(Long personId, Long orgId, Long locationId);
}
