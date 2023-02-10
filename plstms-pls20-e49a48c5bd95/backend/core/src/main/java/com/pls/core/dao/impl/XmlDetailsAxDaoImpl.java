/**
 * 
 */
package com.pls.core.dao.impl;

import com.pls.core.dao.XmlDetailsAxDao;
import com.pls.core.domain.bo.XmlDetailsAxBO;
import com.pls.core.domain.organization.BillToEntity;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link com.pls.core.dao.XmlDetailsAxDao}.
 * 
 * @author Alexander Nalapko
 * 
 */
@Repository
@Transactional
public class XmlDetailsAxDaoImpl implements XmlDetailsAxDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public XmlDetailsAxBO getDetails(Long billToId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(BillToEntity.Q_GET_DETAILS_AX);
        query.setParameter("billTo", billToId);
        query.setResultTransformer(Transformers.aliasToBean(XmlDetailsAxBO.class));
        return (XmlDetailsAxBO) query.uniqueResult();
    }
}
