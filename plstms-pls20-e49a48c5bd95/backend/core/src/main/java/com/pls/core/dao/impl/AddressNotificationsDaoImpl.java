package com.pls.core.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.AddressNotificationsDao;
import com.pls.core.domain.address.AddressNotificationsEntity;

/**
 * The Class AddressNotificationsDaoImpl.
 * 
 * @author Sergii Belodon
 */
@Repository
@Transactional
public class AddressNotificationsDaoImpl extends AbstractDaoImpl<AddressNotificationsEntity, Long> implements AddressNotificationsDao {
}
