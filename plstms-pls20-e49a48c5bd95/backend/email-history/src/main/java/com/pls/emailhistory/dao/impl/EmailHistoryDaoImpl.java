/*
 * 
 */
package com.pls.emailhistory.dao.impl;

import java.util.List;
import java.util.Set;

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.enums.EmailType;
import com.pls.emailhistory.dao.EmailHistoryDao;
import com.pls.emailhistory.domain.EmailHistoryEntity;
import com.pls.emailhistory.domain.bo.EmailHistoryAttachmentBO;
import com.pls.emailhistory.domain.bo.EmailHistoryBO;

/**
 * Implementation of {@link EmailHistoryDao} interface.
 * 
 * @author Sergii Belodon
 */
@Repository
@Transactional
public class EmailHistoryDaoImpl extends AbstractDaoImpl<EmailHistoryEntity, Long> implements EmailHistoryDao {

    /**
     * Gets the email history by load id and types.
     *
     * @param loadId the load id
     * @param types the types
     * @return the email history by load id and types
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<EmailHistoryBO> getEmailHistoryByLoadIdAndTypes(Long loadId, Set<EmailType> types) {
        return (List<EmailHistoryBO>) getCurrentSession()
                .getNamedQuery(EmailHistoryEntity.Q_GET_EMAIL_HISTORY)
                .setParameter("loadId", loadId)
                .setParameterList("types", types)
                .setResultTransformer(new AliasToBeanResultTransformer(EmailHistoryBO.class)).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EmailHistoryAttachmentBO> getAttachmentsByLoadIdAndTypes(Long loadId, Set<EmailType> types) {
        return (List<EmailHistoryAttachmentBO>) getCurrentSession()
                .getNamedQuery(EmailHistoryEntity.Q_GET_ALL_ATTACHMENTS_FOR_LOAD)
                .setParameter("loadId", loadId)
                .setParameterList("types", types)
                .setResultTransformer(new AliasToBeanResultTransformer(EmailHistoryAttachmentBO.class)).list();
    }

    @Transactional
    @Override
    public EmailHistoryEntity saveOrUpdate(EmailHistoryEntity entity) {
        return super.saveOrUpdate(entity);
    }
}
