package com.pls.core.dao.address;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.bo.ContactInfoSetBO;
import com.pls.core.shared.AddressVO;

/**
 * DAO for {@link AddressEntity} entities.
 *
 * @author Gleb Zgonikov
 */
public interface AddressDao extends AbstractDao<AddressEntity, Long> {

    /**
     * Find contact information.
     *
     * @param userId User ID.
     * @return Required {@link ContactInfoSetBO} of null.
     */
    ContactInfoSetBO findContactInfoSet(Long userId);

    /**
     * To get address by the given parameters.
     *
     * @param address
     *       - LTL Profile Detail Id (Buy/Sell/None) to which the accessorials should be retrieved
     * @return List<AddressEntity>
     *       - List of all AddressEntity for selected profile
     */
    List<AddressEntity> findAddressesByAddressVO(AddressVO address);

    /**
     * Find existing route by fields from addresses.
     * 
     * @param originAddressId
     *            id of origin address
     * @param destinationAddressId
     *            id of destination address
     * @return {@link RouteEntity} or <code>null</code>
     */
    RouteEntity findRouteByAddresses(Long originAddressId, Long destinationAddressId);
}
