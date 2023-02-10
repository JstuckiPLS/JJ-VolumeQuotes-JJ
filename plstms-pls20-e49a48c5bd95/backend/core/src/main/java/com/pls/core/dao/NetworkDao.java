package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.bo.NetworkListItemBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.UnitAndCostCenterCodesBO;
import com.pls.core.domain.organization.NetworkEntity;

/**
 * DAO for {@link NetworkEntity}.
 * 
 * @author Brichak Aleksandr
 */

public interface NetworkDao extends AbstractDao<NetworkEntity, Long> {

    /**
     * Get list Networks.
     * 
     * @return all Networks.
     */
    List<NetworkListItemBO> getAllNetworks();

    /**
     * Get list Network Unit Code and Cost Center.
     * 
     * @param orgId
     *            id of Organizations.
     * @return Network Unit Code and Cost Center Code.
     */
    UnitAndCostCenterCodesBO getUnitAndCostCenterCodes(Long orgId);

    /**
     * Get list active Networks by personId.
     * 
     * @param personId
     *            id of user.
     * 
     * @return list active networks by user.
     */
    List<SimpleValue> getActiveNetworksByUser(Long personId);

}