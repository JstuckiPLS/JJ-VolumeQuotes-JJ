package com.pls.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.LookupValueDao;
import com.pls.core.dao.NotificationTypesDao;
import com.pls.core.dao.customer.PlsCustomerTermsDao;
import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.service.DictionaryTypesService;

/**
 * Implementation of {@link com.pls.core.service.DictionaryTypesService} service.
 *
 * @author Alexander Kirichenko
 */
@Service
@Transactional
public class DictionaryTypesServiceImpl implements DictionaryTypesService {

    @Autowired
    private NotificationTypesDao notificationTypesDao;

    @Autowired
    private PlsCustomerTermsDao plsCustomerTermsDao;

    @Autowired
    private LookupValueDao lookupValueDao;

    @Override
    public NotificationTypeEntity getNotificationTypesById(String id) {
        return notificationTypesDao.getNotificationTypesById(id);
    }

    @Override
    public List<NotificationTypeEntity> getNotificationTypes() {
        return notificationTypesDao.getAll();
    }

    @Override
    public List<KeyValueBO> getCustomerPayTerms() {
        return plsCustomerTermsDao.getCustomerPayTerms();
    }

    @Override
    public List<LookupValueEntity> getCustomerPayMethod() {
        return lookupValueDao.findLookupValuesForPayMethod();
    }
}
