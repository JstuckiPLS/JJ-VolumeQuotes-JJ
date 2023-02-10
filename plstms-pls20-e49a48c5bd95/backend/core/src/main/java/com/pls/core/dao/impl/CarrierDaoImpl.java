package com.pls.core.dao.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.bo.CarrierInfoBO;
import com.pls.core.domain.bo.SimpleCarrierInfoBO;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.organization.CarrierEntity;

/**
 * {@link com.pls.core.dao.CarrierDao} implementation.
 * 
 * @author Alexander Nalapko
 * @author Viacheslav Krot
 * 
 */

@Repository
@Transactional
public class CarrierDaoImpl extends AbstractDaoImpl<CarrierEntity, Long> implements CarrierDao {

    private static final String BASE_CARRIER_INFO_SQL =
            "SELECT org.ORG_ID \"carrierId\", org.NAME \"name\", org.SCAC \"scac\", org.STATUS \"statusChar\", "
                    + "api.ORGANIZATION_API_DETAIL_ID \"apiId\" "
             + "FROM ORGANIZATIONS org LEFT JOIN ORGANIZATION_API_DETAILS api ON org.ORG_ID = api.ORG_ID "
             + "WHERE org.ORG_TYPE = 'CARRIER'";

    @SuppressWarnings("unchecked")
    @Override
    public List<CarrierInfoBO> findCarrierByName(String name, int limit, int offset) {
        return getCurrentSession().getNamedQuery(CarrierEntity.Q_FIND_CARRIER_INFO_BY_NAME).
                setParameter("name", name.toLowerCase(Locale.getDefault()) + "%").setResultTransformer(Transformers.aliasToBean(CarrierInfoBO.class)).
                setFirstResult(offset).setMaxResults(limit).list();
    }

    @Override
    public CarrierEntity find() {
        return (CarrierEntity) getCriteria().setMaxResults(1).list().get(0);
    }

    @Override
    public CarrierEntity findByScac(String scac) {
        return (CarrierEntity) getCurrentSession().getNamedQuery(CarrierEntity.Q_FIND_CARRIER_BY_SCAC)
                .setParameter("scac", StringUtils.upperCase(scac), StringType.INSTANCE)
                .setMaxResults(1)
                .uniqueResult();
    }
    
    @Override
    public CarrierEntity findByScacIncludingActual(String scac) {
        return (CarrierEntity) getCurrentSession().getNamedQuery(CarrierEntity.Q_FIND_CARRIER_BY_SCAC_INCLUDING_ACTUAL)
                .setParameter("scac", StringUtils.upperCase(scac), StringType.INSTANCE)
                .setMaxResults(1)
                .uniqueResult();
    }
    
    @Override
    public CarrierEntity findByScacAndCurrency(String scac, Currency currency) {
        return (CarrierEntity) getCurrentSession().getNamedQuery(CarrierEntity.Q_FIND_CARRIER_BY_SCAC_AND_CURRENCY)
                .setParameter("scac", StringUtils.upperCase(scac), StringType.INSTANCE)
                .setParameter("currency", currency)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public CarrierEntity findByMcNumber(String mcNumber) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("mcNumber", mcNumber).ignoreCase());
        @SuppressWarnings("unchecked")
        List<CarrierEntity> result = criteria.list();
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    public CarrierEntity findByScacAndMC(String scac, String mcNumber) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("scac", scac).ignoreCase());
        if (!StringUtils.isEmpty(mcNumber)) {
            criteria.add(Restrictions.eq("mcNumber", mcNumber).ignoreCase());
        }
        @SuppressWarnings("unchecked")
        List<CarrierEntity> result = criteria.list();
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SimpleCarrierInfoBO> getCarrierInfos(String carrier, String scacCode, String status) {
        if (isBlank(carrier) && isBlank(scacCode) && isBlank(status)) {
            return Collections.emptyList();
        }

        StringBuilder queryBuilder = new StringBuilder(BASE_CARRIER_INFO_SQL);
        if (isNotBlank(carrier)) {
            queryBuilder.append(" AND name like UPPER('%" + carrier + "%')");
        }
        if (isNotBlank(scacCode)) {
            queryBuilder.append(" AND scac like UPPER('" + scacCode + "%')");
        }
        if (isNotBlank(status)) {
            queryBuilder.append(" AND org.status like UPPER('" + status + "%')");
        }
        return getCurrentSession().createSQLQuery(queryBuilder.toString()).
                setResultTransformer(Transformers.aliasToBean(SimpleCarrierInfoBO.class)).list();
    }

    @Override
    public CarrierEntity findById(Long id) {
        return (CarrierEntity) getCurrentSession().getNamedQuery(CarrierEntity.Q_FIND_CARRIER_BY_ID)
                .setParameter("id", id)
                .setMaxResults(1)
                .uniqueResult();
    }
    
    @Override
    public CarrierInfoBO getDefaultCarrier() {
        return (CarrierInfoBO) getCurrentSession().getNamedQuery(CarrierEntity.Q_FIND_DEFAULT_CARRIER).
                setResultTransformer(Transformers.aliasToBean(CarrierInfoBO.class)).uniqueResult();
    }

    @Override
    public Boolean rejectEdiForCustomer(String scac, String ediAccount) {
        Query query = getCurrentSession().getNamedQuery(CarrierEntity.Q_REJECT_EDI_FOR_CUSTOMER);
        query.setString("scac", scac);
        query.setString("ediAccount", ediAccount);

        return query.uniqueResult() != null;
    }
}
