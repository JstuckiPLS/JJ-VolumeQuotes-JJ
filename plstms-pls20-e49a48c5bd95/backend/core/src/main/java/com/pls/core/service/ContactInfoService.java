package com.pls.core.service;

import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;

/**
 * Contact Info service.
 * 
 * @author Alexander Nalapko
 *
 */
public interface ContactInfoService {

    /**
     * Returns current user contact information for current user.
     * 
     * @return {@link UserAdditionalContactInfoBO}
     */
    UserAdditionalContactInfoBO getContactInfoForCurrentUser();

    /**
     * Get contact information for user.
     * 
     * @param user
     *            to get contact information. When <code>null</code> - returns default contact info.
     * @return contact information
     */
    UserAdditionalContactInfoBO getContactInfo(UserEntity user);

}
