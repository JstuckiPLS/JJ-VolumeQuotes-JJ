package com.pls.emailhistory.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.service.UserPermissionsService;
import com.pls.emailhistory.dao.EmailHistoryDao;
import com.pls.emailhistory.domain.bo.EmailHistoryAttachmentBO;
import com.pls.emailhistory.domain.bo.EmailHistoryBO;
import com.pls.emailhistory.service.EmailHistoryService;

/**
 * Email history implementation.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@Service
@Transactional
public class EmailHistoryServiceImpl implements EmailHistoryService {

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private EmailHistoryDao emailHistoryDao;

    @Override
    public List<EmailHistoryBO> getEmailHistory(long loadId) {
        Set<EmailType> types = EnumSet.noneOf(EmailType.class);
        for (EmailType type : EmailType.values()) {
            if (type.getCapability() != null && userPermissionsService.hasCapability(type.getCapability().name())) {
                types.add(type);
            }
        }


        List<EmailHistoryBO> emailHistory = emailHistoryDao.getEmailHistoryByLoadIdAndTypes(loadId, types);
        List<EmailHistoryAttachmentBO> attachments = emailHistoryDao.getAttachmentsByLoadIdAndTypes(loadId, types);
        fillAttachments(emailHistory, attachments);
        return emailHistory;
    }

    private void fillAttachments(List<EmailHistoryBO> emailHistory, List<EmailHistoryAttachmentBO> attachments) {
        Comparator<EmailHistoryBO> emailHistoryIdComparator = new Comparator<EmailHistoryBO>() {
            @Override
            public int compare(EmailHistoryBO o1, EmailHistoryBO o2) {
                return -o1.getId().compareTo(o2.getId());
            }

        };
        Collections.sort(emailHistory, emailHistoryIdComparator);

        EmailHistoryBO searchedEntity = new EmailHistoryBO();
        for (EmailHistoryAttachmentBO entity : attachments) {
            searchedEntity.setId(entity.getEmailHistoryId());
            int index = Collections.binarySearch(emailHistory, searchedEntity, emailHistoryIdComparator);
            emailHistory.get(index).getAttachments().add(entity);
        }
    }
}
