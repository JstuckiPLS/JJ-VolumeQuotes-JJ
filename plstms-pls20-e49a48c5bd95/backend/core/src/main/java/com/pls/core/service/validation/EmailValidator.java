package com.pls.core.service.validation;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Email validator.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
public class EmailValidator extends AbstractValidator<String> {
    private static final int MAX_LENGTH = 255;
    private static final Pattern EMAIL_PATTERN = Pattern
        .compile("^[a-zA-Z0-9_#$%&’*+/=?^.-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");

    @Override
    protected void validateImpl(String email) {
        asserts.notNull(email, "email");
        if (email != null) {
            asserts.isTrue(EMAIL_PATTERN.matcher(email).matches(), "email", ValidationError.FORMAT);
            asserts.isTrue(email.length() <= MAX_LENGTH, "email", ValidationError.GREATER_THAN);
        }
    }
}
