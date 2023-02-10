package com.pls.core.dao.impl.customer;

import java.util.List;

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.customer.PlsCustomerTermsDao;
import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.organization.PlsCustomerTermsEntity;

/**
 * {@link com.pls.core.dao.BillToDao} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class PlsCustomerTermsDaoImpl extends AbstractDaoImpl<PlsCustomerTermsEntity, Long> implements PlsCustomerTermsDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<KeyValueBO> getCustomerPayTerms() {
        return getCurrentSession().getNamedQuery(PlsCustomerTermsEntity.Q_GET_ALL)
                .setResultTransformer(new AliasToBeanResultTransformer(KeyValueBO.class))
                .list();
    }
}
