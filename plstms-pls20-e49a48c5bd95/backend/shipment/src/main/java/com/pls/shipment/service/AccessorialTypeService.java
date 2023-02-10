package com.pls.shipment.service;

import java.util.Collection;
import java.util.List;

import com.pls.core.domain.AccessorialTypeEntity;

/**
 * Service for shipment accessorials.
 *
 * @author Mikhail Boldinov, 16/05/13
 */
public interface AccessorialTypeService {
    /**
     * Get list of all available accessorial types.
     * 
     * @return {@link List} of {@link AccessorialTypeEntity}.
     */
    List<AccessorialTypeEntity> getAvailableAccessorialTypes();

    /**
     * Refresh accessorials with up-to-date state.
     * 
     * @param accessorials
     *            to refresh
     */
    void refreshAccessorials(Collection<AccessorialTypeEntity> accessorials);
}
