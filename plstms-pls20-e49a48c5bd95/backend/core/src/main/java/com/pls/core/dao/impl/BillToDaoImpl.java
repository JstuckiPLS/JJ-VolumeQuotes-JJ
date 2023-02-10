package com.pls.core.dao.impl;

import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToDao;
import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.organization.BillToDefaultValuesEntity;
import com.pls.core.domain.organization.BillToEntity;

/**
 * {@link com.pls.core.dao.BillToDao} implementation.
 * @author Andrey Kachur
 *
 */
@Repository
@Transactional
public class BillToDaoImpl extends AbstractDaoImpl<BillToEntity, Long> implements BillToDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<BillToEntity> findBillTo(Long orgId, Currency currency) {
        return getCurrentSession().getNamedQuery(BillToEntity.Q_GET_FOR_SHIPMENT)
                .setLong("orgId", orgId)
                .setString("currency", currency == null ? null : currency.name())
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<BillToEntity> getBillToAddresses(Long customerId) {
        return getCurrentSession().getNamedQuery(BillToEntity.Q_GET_FOR_CUSTOMER).setLong("orgId", customerId).list();
    }

    @Override
    public boolean validateDuplicateName(String nameToBeValidated, Long orgId) {
        Long rowCount = (Long) getCriteria().setProjection(rowCount()).add(eq("name", nameToBeValidated))
                .add(eq("organization.id", orgId)).uniqueResult();
        return rowCount.intValue() != 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<KeyValueBO> getIdAndNameByOrgId(Long orgId) {
        Query query = getCurrentSession().getNamedQuery(BillToEntity.Q_GET_ID_AND_NAME_BY_ORG_ID);
        query.setParameter("orgId", orgId);
        return query.setResultTransformer(new AliasToBeanResultTransformer(KeyValueBO.class)).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<KeyValueBO> getBillToEmails(List<Long> billToIds) {
        return getCurrentSession().getNamedQuery(BillToEntity.Q_GET_EMAILS).setParameterList("billToIds", billToIds)
               .setResultTransformer(new AliasToBeanResultTransformer(KeyValueBO.class)).list();
    }

    @Override
    public BillToEntity findByCustomerAndBillToName(String billToName, Long customerOrgId) {
        return (BillToEntity) getCurrentSession().getNamedQuery(BillToEntity.Q_GET_BILLTO_BY_NAME_AND_ORG_ID)
                .setParameter("orgId", customerOrgId).setParameter("name", billToName).uniqueResult();
    }

    @Override
    public BillToDefaultValuesEntity getDefaultEntityById(Long defaultEntityId) {
        return (BillToDefaultValuesEntity) getCurrentSession().getNamedQuery(BillToEntity.Q_GET_DEFAULT_VALUES_BY_ID)
                .setParameter("id", defaultEntityId).uniqueResult();
    }
}
