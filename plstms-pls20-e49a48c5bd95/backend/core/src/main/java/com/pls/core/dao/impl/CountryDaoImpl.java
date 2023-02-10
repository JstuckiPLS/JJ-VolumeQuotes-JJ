package com.pls.core.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CountryDao;
import com.pls.core.domain.bo.PageQueryBO;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.shared.Status;

/**
 * {@link com.pls.core.dao.CountryDao} implementation.
 *
 * @author Artem Arapov
 *
 */
@Repository
@Transactional
public class CountryDaoImpl extends AbstractDaoImpl<CountryEntity, String> implements CountryDao {
    @Override
    public List<CountryEntity> getActiveCountries() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("status", Status.ACTIVE);

        return findByNamedQuery(CountryEntity.Q_GET_ALL_BY_STATUS, params);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CountryEntity> getCountriesByCodeOrName(String keyword, PageQueryBO page) {
        String likeCriteria = keyword + "%";

        Criteria criteria = getCurrentSession().createCriteria(CountryEntity.class);
        criteria.add(Restrictions.and(Restrictions.or(Restrictions.ilike("name", likeCriteria), Restrictions.ilike("id", likeCriteria)),
                Restrictions.eq("status", Status.ACTIVE)));
        setFetchSize(criteria, page);

        return criteria.list();
    }

    private void setFetchSize(Criteria criteria, PageQueryBO page) {
        if (page != null) {
            criteria.setFirstResult(page.getPageFirstIndex());
            criteria.setFetchSize(page.getPageSize());
        }
    }

    @Override
    public String findFullCountryCode(String shortCountryCode) {
        return (String) getCurrentSession().getNamedQuery(CountryEntity.Q_GET_ID_BY_SHORT_CODE)
                .setString("shortCountryCode", StringUtils.upperCase(shortCountryCode))
                .uniqueResult();
    }

    @Override
    public String findShortCountryCode(String fullCountryCode) {
        return (String) getCurrentSession().getNamedQuery(CountryEntity.Q_GET_SHORT_CODE_BY_ID)
                .setString("fullCountryCode", StringUtils.upperCase(fullCountryCode)).uniqueResult();
    }
}
