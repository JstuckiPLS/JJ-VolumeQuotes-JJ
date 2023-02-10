package com.pls.documentmanagement.dao.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.documentmanagement.dao.RequiredDocumentDao;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;

/**
 * {@link RequiredDocumentDao} implementation.
 *
 * @author Alexander Nalapko
 *
 */
@Repository
@Transactional
public class RequiredDocumentDaoImpl extends AbstractDaoImpl<RequiredDocumentEntity, Long> implements RequiredDocumentDao {

    @Override
    @SuppressWarnings("unchecked")
    public Map<LoadDocumentTypeEntity, RequiredDocumentEntity> getDocumentsForShipmentTypes(long billToId) {
        Map<LoadDocumentTypeEntity, RequiredDocumentEntity> resultMap = new HashMap<LoadDocumentTypeEntity, RequiredDocumentEntity>();
        List<LoadDocumentTypeEntity> types = getCurrentSession().getNamedQuery(LoadDocumentTypeEntity.Q_GET_DOCUMENT_TYPES).list();
        Criteria rdCriteria = getCurrentSession().createCriteria(RequiredDocumentEntity.class);
        rdCriteria.add(Restrictions.eq("billTo.id", billToId));
        rdCriteria.add(Restrictions.in("documentType", types));
        rdCriteria.setFetchMode("documentType", FetchMode.JOIN);
        List<RequiredDocumentEntity> requiredDocuments = rdCriteria.list();
        Map<String, RequiredDocumentEntity> existingDocsMap = new HashMap<String, RequiredDocumentEntity>(requiredDocuments.size());
        for (RequiredDocumentEntity requiredDocument : requiredDocuments) {
            existingDocsMap.put(requiredDocument.getDocumentType().getDocTypeString(), requiredDocument);
        }
        for (LoadDocumentTypeEntity type : types) {
            RequiredDocumentEntity requiredDocument;
            if (existingDocsMap.containsKey(type.getDocTypeString())) {
                requiredDocument = existingDocsMap.get(type.getDocTypeString());
            } else {
                requiredDocument = new RequiredDocumentEntity();
            }
            resultMap.put(type, requiredDocument);
        }

        return resultMap;
    }

    @Override
    public boolean isAllPaperworkRequiredForBillToInvoicePresent(Long loadId) {
        Query query = getCurrentSession().getNamedQuery(RequiredDocumentEntity.Q_COUNT_MISSING_REQUIRED_DOCUMENTS);
        query.setParameter("loadId", loadId);
        query.setMaxResults(1);

        // 0 means that all documents are present or no documents required
        return BigInteger.ZERO.equals(query.uniqueResult());
    }
}
