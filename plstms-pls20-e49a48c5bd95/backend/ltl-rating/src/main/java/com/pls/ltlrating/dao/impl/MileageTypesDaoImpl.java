package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.MileageTypesDao;
import com.pls.ltlrating.domain.MileageTypePK;
import com.pls.ltlrating.domain.MileageTypesEntity;

/**
 * {@link MileageTypesDao} implementation.
 *
 * @author Sergey Kirichenko
 */
@Transactional
@Repository
public class MileageTypesDaoImpl extends AbstractDaoImpl<MileageTypesEntity, MileageTypePK> implements MileageTypesDao {

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
    @Override
    @SuppressWarnings("unchecked")
    public List<MileageTypesEntity> findAllByStatus(Status status) {
        return getCurrentSession().createCriteria(MileageTypesEntity.class)
                .add(Restrictions.and(Restrictions.eq("status", status))).list();
    }
}
