package com.pls.search.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.search.dao.CustomerLibraryDao;
import com.pls.search.domain.co.GetCustomerCO;
import com.pls.search.domain.vo.CustomerLibraryVO;

/**
 * Implementation of {@link CustomerLibraryDao}.
 * 
 * @author Artem Arapov
 * 
 */
@Transactional
@Repository
public class CustomerLibraryDaoImpl extends AbstractDaoImpl<OrganizationEntity, Long> implements CustomerLibraryDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<CustomerLibraryVO> findCustomerLibraryByCO(GetCustomerCO co) {
        SQLQuery query = (SQLQuery) getCurrentSession().getNamedQuery(OrganizationEntity.Q_FIND_CUSTOMERS_BY_VALUES);
        query.setDate("fromDate", co.getFromDate());
        query.setDate("toDate", co.getToDate());
        query.setDate("fromLoadDate", co.getFromLoadDate());
        query.setDate("toLoadDate", co.getToLoadDate());
        query.setString("status", co.getStatus().getStatusCode());
        query.setLong("currentPersonId", SecurityUtils.getCurrentPersonId());
        if (co.getPersonId() != null) {
            query.setLong("personId", co.getPersonId());
        } else {
            query.setParameter("personId", null, LongType.INSTANCE);
        }
        String name = co.getName() != null ? co.getName().toUpperCase().replaceAll("\\*", "%") + "%" : null;
        query.setString("orgName", name);
        query.setResultTransformer(new AliasToBeanResultTransformer(CustomerLibraryVO.class));
        query.addScalar("customerId", StandardBasicTypes.LONG);
        query.addScalar("customerName", StandardBasicTypes.STRING);
        query.addScalar("accountExecId", StandardBasicTypes.LONG);
        query.addScalar("accountExecName", StandardBasicTypes.STRING);
        query.addScalar("createdDate", StandardBasicTypes.DATE);
        query.addScalar("lastLoadDate", StandardBasicTypes.DATE);
        return query.list();
    }
}
