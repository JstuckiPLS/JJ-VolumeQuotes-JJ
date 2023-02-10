package com.pls.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.AddressNotificationsDao;
import com.pls.core.domain.address.AddressNotificationsEntity;
import com.pls.core.service.AddressNotificationsService;

/**
 * The Class AddressNotificationsServiceImpl.
 * 
 * @author Sergii Belodon
 */
@Service
@Transactional
public class AddressNotificationsServiceImpl implements AddressNotificationsService {
    @Autowired
    private AddressNotificationsDao addressNotificationsDao;
    @Override
    public AddressNotificationsEntity getAddressNotificationById(Long id) {
        return addressNotificationsDao.find(id);
    }
}
