package com.pls.emailhistory.dao;

import java.util.List;
import java.util.Set;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.enums.EmailType;
import com.pls.emailhistory.domain.EmailHistoryEntity;
import com.pls.emailhistory.domain.bo.EmailHistoryAttachmentBO;
import com.pls.emailhistory.domain.bo.EmailHistoryBO;

/**
 * Dao class for email history.
 * @author Sergii Belodon
 */
public interface EmailHistoryDao extends AbstractDao<EmailHistoryEntity, Long> {
    /**
     * Gets the email history by load id and types.
     *
     * @param loadId the load id
     * @param types the types
     * @return the email history by load id and types
     */
    List<EmailHistoryBO> getEmailHistoryByLoadIdAndTypes(Long loadId, Set<EmailType> types);

    /**
     * Gets the attachments by load id and types.
     *
     * @param loadId the load id
     * @param types the types
     * @return the attachments by load id and types
     */
    List<EmailHistoryAttachmentBO> getAttachmentsByLoadIdAndTypes(Long loadId, Set<EmailType> types);
}
