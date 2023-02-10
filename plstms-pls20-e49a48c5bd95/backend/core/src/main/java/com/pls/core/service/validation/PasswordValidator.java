package com.pls.core.service.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Validator for user password.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
public class PasswordValidator extends AbstractValidator<String> {
    private static final String CONSTRAINT_NAME = "password";
    private static final Pattern PATTERN = Pattern.compile("\\d+");
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 50;

    @Override
    protected void validateImpl(String password) {
        asserts.notNull(password, CONSTRAINT_NAME);
        if (password != null) {
            asserts.isTrue(password.length() >= MIN_LENGTH, CONSTRAINT_NAME, ValidationError.LESS_THAN);
            asserts.isTrue(password.length() <= MAX_LENGTH, CONSTRAINT_NAME, ValidationError.GREATER_THAN);

            Matcher matcher = PATTERN.matcher(password);
            boolean hasDigits = matcher.find();
            asserts.isTrue(hasDigits, CONSTRAINT_NAME, ValidationError.FORMAT);
            if (hasDigits) {
                boolean notAllSymbolsAreDigits = matcher.start() != 0 || matcher.end() != password.length();
                asserts.isTrue(notAllSymbolsAreDigits, CONSTRAINT_NAME, ValidationError.FORMAT);
            }

            // password has lower and upper case letters
            asserts.isTrue(!password.equals(password.toLowerCase()) && !password.equals(password.toUpperCase()), CONSTRAINT_NAME,
                    ValidationError.FORMAT);
        }
    }
}
