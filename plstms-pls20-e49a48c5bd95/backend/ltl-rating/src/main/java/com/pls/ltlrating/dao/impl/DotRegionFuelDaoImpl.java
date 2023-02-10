package com.pls.ltlrating.dao.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.DotRegionFuelDao;
import com.pls.ltlrating.domain.DotRegionFuelEntity;

/**
 * Implementation for {@link DotRegionFuelDao}.
 *
 * @author Stas Norochevskiy
 *
 */
@Repository
@Transactional
public class DotRegionFuelDaoImpl extends AbstractDaoImpl<DotRegionFuelEntity, Long> implements DotRegionFuelDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<DotRegionFuelEntity> getByDateRangeAndIds(Date fromDate, Date toDate, List<Long> ids) {
        Query query;
        if (ids == null) {
          query = getCurrentSession().getNamedQuery(DotRegionFuelEntity.GET_BY_DATE);
          query.setParameter("fromDate", fromDate);
          query.setParameter("toDate", toDate);
        } else {
            query = getCurrentSession().getNamedQuery(DotRegionFuelEntity.GET_BY_DATE_AND_REGION_ID);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            query.setParameterList("ids", ids);
        }
        return query.list();
    }

    @Override
    public List<DotRegionFuelEntity> saveAll(List<DotRegionFuelEntity> entities) {
        for (DotRegionFuelEntity entity : entities) {
            getCurrentSession().saveOrUpdate(entity);
        }
        getCurrentSession().flush();
        return entities;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DotRegionFuelEntity> getActiveRegionRates() {
        Date weekAgoDate = Date.from(LocalDate.now().minus(7L, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return getCurrentSession().getNamedQuery(DotRegionFuelEntity.GET_ACTIVE_REGIONS_QUERY)
                .setParameter("weekAgoDate", weekAgoDate).list();
    }

    @Override
    public void expirateRates() {
        Query query = getCurrentSession().getNamedQuery(DotRegionFuelEntity.EXPIRATE_STATEMENT);
        query.executeUpdate();
    }
}
