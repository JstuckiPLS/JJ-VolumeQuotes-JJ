package com.pls.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.dao.UserInfoDao;
import com.pls.core.domain.enums.CustomerServiceContactInfoType;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.util.PhoneUtils;

/**
 * Contact Info Service implementation.
 * 
 * @author Alexander Nalapko
 */
@Service
@Transactional
public class ContactInfoServiceImpl implements ContactInfoService {

    @Value("${email.from}")
    private String defaultEmail;

    @Value("${email.defaultContactPhone}")
    private String defaultContactPhone;

    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public UserAdditionalContactInfoBO getContactInfoForCurrentUser() {
        UserEntity user = null;
        if (SecurityUtils.getCurrentPersonId() != null) {
            user = userInfoDao.getUserEntityById(SecurityUtils.getCurrentPersonId());
        }
        return getContactInfo(user);
    }

    @Override
    public UserAdditionalContactInfoBO getContactInfo(UserEntity user) {
        UserAdditionalContactInfoBO contact = new UserAdditionalContactInfoBO();
        CustomerServiceContactInfoType customerServiceContactInfoType = CustomerServiceContactInfoType.DEFAULT;
        if (user != null && user.getCustomerServiceContactInfoType() != null) {
            customerServiceContactInfoType = user.getCustomerServiceContactInfoType();
        }
        if (user != null) {
            contact.setId(user.getId());
        }
        switch (customerServiceContactInfoType) {
        case CUSTOM:
            contact.setContactName(user.getAdditionalInfo().getContactName());
            contact.setPhone(PhoneUtils.parse(PhoneUtils.format(user.getAdditionalInfo().getPhone())));
            contact.setEmail(user.getAdditionalInfo().getEmail());
            break;
        case SAME_AS_USER_PROFILE:
            contact.setContactName(UserNameBuilder.buildFullName(user.getFirstName(), user.getLastName()));
            contact.setPhone(PhoneUtils.parse(PhoneUtils.format(user.getActiveUserPhoneByType(PhoneType.VOICE))));
            contact.setEmail(user.getEmail());
            break;
        default:
            String[] additionalData = defaultEmail.split("<|>");
            contact.setContactName(additionalData[0].trim());
            contact.setPhone(PhoneUtils.parse(defaultContactPhone));
            contact.setEmail(additionalData[1].trim());
            break;
        }
        return contact;
    }
}
