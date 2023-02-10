package com.pls.core.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.TimeZoneDao;
import com.pls.core.domain.TimeZoneEntity;

/**
 * Implementation of {@link TimeZoneDao}.
 *
 * @author Sergey Kirichenko
 */
@Transactional
@Repository
public class TimeZoneDaoImpl extends DictionaryDaoImpl<TimeZoneEntity> implements TimeZoneDao {

    @Override
    public TimeZoneEntity findByCountryZip(String countryCode, String zipCode) {
        Query query = getCurrentSession().getNamedQuery(TimeZoneEntity.Q_FIND_BY_COUNTRY_ZIP);
        query.setParameter("countryCode", countryCode).setParameter("zipCode", zipCode);
        return (TimeZoneEntity) query.uniqueResult();
    }
}
