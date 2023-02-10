package com.pls.core.service.impl.security;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 * Password encoder for PLS application. It uses MD5 based algorithm with two differences:
 * <ul>
 * <li>Input password string is transformed into upper case</li>
 * <li>Output result hash is transformed into upper case</li>
 * </ul>
 *
 * @author Maxim Medvedev
 */
public class PlsMD5Encoder extends Md5PasswordEncoder {

    @Override
    public String encodePassword(String pRawPass, Object pSalt) {
        // Hash should be in upper case
        return StringUtils.upperCase(
                super.encodePassword(StringUtils.upperCase(pRawPass, Locale.ENGLISH), pSalt), Locale.ENGLISH);
    }
}
