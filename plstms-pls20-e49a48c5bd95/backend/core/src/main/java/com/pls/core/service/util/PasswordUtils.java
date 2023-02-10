package com.pls.core.service.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.PlsMD5Encoder;
import com.pls.core.service.validation.EmailValidator;
import com.pls.core.service.validation.PasswordValidator;
import com.pls.core.service.validation.ValidationException;

/**
 * Utility for generate password.
 * 
 * @author Brichak Aleksandr
 *
 */
@Controller
public class PasswordUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordUtils.class);

    @Autowired
    private EmailValidator emailValidator;

    private final PlsMD5Encoder passwordEncoder = new PlsMD5Encoder();

    @Autowired
    private PasswordValidator passwordValidator;

    private static final int DEFAULT_PASSWORD_LENGTH = 15;

    /**
     * Generate new password for user.
     * 
     * @param userEntity
     *            {link@ UserEntity}.
     * @return new password.
     * @throws ApplicationException
     *             if user not found or some error occurred while generated new password.
     */
    public String generateNewPasswordForUser(UserEntity userEntity) throws ApplicationException {
        if (userEntity == null) {
            throw new EntityNotFoundException("User not found");
        }
        try {
            emailValidator.validate(userEntity.getEmail());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ApplicationException("Email not found or incorrect.");
        }
        return generatePassword();
}

    /**
     * Generate new password for user.
     * 
     * @return new password.
     */
    public String generatePassword() {
        while (true) {
            String newPassword = RandomStringUtils.randomAlphanumeric(DEFAULT_PASSWORD_LENGTH);
            try {
                passwordValidator.validate(newPassword);
                return newPassword;
            } catch (ValidationException e) {
                continue;
            }
        }
    }

    /**
     * Password encoder. It uses MD5 based algorithm.
     * 
     * @param password
     *            password to encode.
     * @return encoded password.
     */
    public String encodePassword(String password) {
        return passwordEncoder.encodePassword(password, null);
    }

}
