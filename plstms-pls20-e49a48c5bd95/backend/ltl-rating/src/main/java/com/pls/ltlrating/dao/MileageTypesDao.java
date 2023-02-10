package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.MileageTypePK;
import com.pls.ltlrating.domain.MileageTypesEntity;

/**
 * DAO for {@link MileageTypesEntity}.
 *
 * @author Sergey Kirichenko
 */
public interface MileageTypesDao extends AbstractDao<MileageTypesEntity, MileageTypePK> {

    /**
     * To get All MileageTypes by Status.
     * We inactivate mileage types or respective versions when they are not needed. This is done in the backend.
     * When we display Mileage Types in the UI, we should display only the Active Mileage types.
     * Also in future if we have to get the inactive mileage types for maintenance screen then also
     * we can use this method.
     *
     * @param status
     *            - Status of the accessorial - Active/Inactive = A/I
     * @return List<MileageTypesEntity> - List of all MileageTypesEntity for selected status
     */
    List<MileageTypesEntity> findAllByStatus(Status status);
}
