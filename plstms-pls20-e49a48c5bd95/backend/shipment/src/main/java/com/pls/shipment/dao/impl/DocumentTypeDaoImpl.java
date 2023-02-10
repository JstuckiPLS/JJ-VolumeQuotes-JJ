package com.pls.shipment.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.document.DocumentTypeEntity;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.shipment.dao.DocumentTypeDao;

/**
 * {@link com.pls.shipment.dao.DocumentTypeDao} implementation.
 *
 * @author Alexander Nalapko
 *
 */
@Repository
@Transactional
public class DocumentTypeDaoImpl extends AbstractDaoImpl<DocumentTypeEntity, Long> implements DocumentTypeDao {
    @Override
    public <T extends DocumentTypeEntity> T findByDocTypeString(String docType, Class<T> clazz) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);
        criteria.add(Restrictions.eq("docTypeString", docType));
        @SuppressWarnings("unchecked") List<T> docTypes = criteria.list();
        if (!docTypes.isEmpty()) {
            return docTypes.get(0); //should be unique, but it isn't limited by database
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DocumentTypeEntity> List<T> find(Class<T> clazz) {
        return getCurrentSession().createCriteria(clazz).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoadDocumentTypeEntity> getLoadDocumentType() {
        return getCurrentSession().getNamedQuery(LoadDocumentTypeEntity.Q_GET_DOCUMENT_TYPES).list();
    }
}
