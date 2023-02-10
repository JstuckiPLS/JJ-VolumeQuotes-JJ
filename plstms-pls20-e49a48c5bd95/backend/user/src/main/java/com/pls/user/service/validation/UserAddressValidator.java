package com.pls.user.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.domain.user.UserAddressEntity;
import com.pls.core.service.validation.AddressValidator;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * User address validator.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
public class UserAddressValidator extends AbstractValidator<UserAddressEntity> {
    @Autowired
    private AddressValidator addressValidator;

    @Override
    protected void validateImpl(UserAddressEntity entity) {
        asserts.notNull(entity.getUser(), "user");
        validateComponent(addressValidator, entity.getAddress(), "address");
    }
}
