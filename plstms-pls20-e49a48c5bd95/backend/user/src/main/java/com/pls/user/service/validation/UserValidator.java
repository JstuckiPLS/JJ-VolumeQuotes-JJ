package com.pls.user.service.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.domain.user.PromoCodeEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.domain.user.UserPhoneEntity;
import com.pls.core.service.validation.EmailValidator;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.core.shared.Status;
import com.pls.user.dao.UserDao;

/**
 * User validator.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
public class UserValidator extends AbstractValidator<UserEntity> {

    private static final int DB_LENGTH_30 = 30;

    private static final int DB_LENGTH_50 = 50;

    private static final String FIELD_LOGIN = "userId";

    private static final int MAX_PROMO_CODE_DISCOUNT = 99;

    private static final int MIN_PROMO_CODE_DISCOUNT = 1;

    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private UserAddressValidator addressValidator;

    @Autowired
    private UserPhoneValidator phoneValidator;

    @Autowired
    private UserDao userDao;

    @Override
    protected void validateImpl(UserEntity entity) {
        validateUserId(entity);
        validateEmail(entity);

        asserts.notEmpty(entity.getFirstName(), "firstName");
        asserts.isTrue(entity.getFirstName().length() <= DB_LENGTH_30, "firstName",
                ValidationError.GREATER_THAN);
        asserts.notEmpty(entity.getLastName(), "lastName");
        asserts.isTrue(entity.getLastName().length() <= DB_LENGTH_30, "lastName",
                ValidationError.GREATER_THAN);

        asserts.notNull(entity.getParentOrgId(), "parentOrgId");

        validateAddresses(entity);
        validatePhone(entity.getActiveUserPhoneByType(PhoneType.VOICE));
        UserPhoneEntity fax = entity.getActiveUserPhoneByType(PhoneType.FAX);
        if (fax != null) {
            validatePhone(fax);
        }
        if (entity.getCustomerUsers() != null && !entity.getCustomerUsers().isEmpty()) {
            validateCustomerUser(entity.getCustomerUsers());
        }
        validatePromoCode(entity);
    }

    private void validatePromoCode(UserEntity entity) {
        if (!entity.getPromoCodes().isEmpty()) {
            for (PromoCodeEntity promoCode : entity.getPromoCodes()) {
                if (Status.ACTIVE == promoCode.getStatus()) {
                    asserts.notEmpty(promoCode.getCode(), "promoCode");
                    asserts.notNull(promoCode.getPercentage(), "percentage");
                    validateCode(promoCode);
                    validatePercentage(promoCode);
                }
            }
        }
    }

    private void validatePercentage(PromoCodeEntity promoCode) {
        if (promoCode.getPercentage() != null) {
            asserts.isTrue(promoCode.getPercentage().intValue() <= MAX_PROMO_CODE_DISCOUNT, "percentage",
                    ValidationError.GREATER_THAN);
            asserts.isTrue(promoCode.getPercentage().intValue() >= MIN_PROMO_CODE_DISCOUNT, "percentage",
                    ValidationError.LESS_THAN);
        }
    }

    private void validateCode(PromoCodeEntity promoCode) {
        if (promoCode.getCode() != null) {
            asserts.isTrue(promoCode.getCode().length() <= DB_LENGTH_30, "promoCode",
                    ValidationError.GREATER_THAN);
        }
    }

    private void validateEmail(UserEntity entity) {
        asserts.notNull(entity.getEmail(), "email");
        if (entity.getEmail() != null) {
            validateComponent(emailValidator, entity.getEmail(), "email");
        }
    }

    private void validateCustomerUser(List<CustomerUserEntity> customerUsers) {
        for (CustomerUserEntity customerUser : customerUsers) {
            asserts.notNull(customerUser.getCustomerId(), "customerUser.organization");
        }
    }

    private void validatePhone(UserPhoneEntity phone) {
        asserts.notNull(phone, "customerUser.phones");
        validateComponent(phoneValidator, phone, "phone");
    }


    private void validateAddresses(UserEntity entity) {
        asserts.isTrue(!entity.getAddresses().isEmpty(), "address",  ValidationError.IS_EMPTY);
        if (entity.getUserAddress() != null) {
                validateComponent(addressValidator, entity.getUserAddress(), "address");
        }
    }

    private void validateUserId(UserEntity entity) {
        asserts.notEmpty(entity.getLogin(), FIELD_LOGIN);
        asserts.isTrue(entity.getLogin().length() <= DB_LENGTH_50, FIELD_LOGIN, ValidationError.GREATER_THAN);

        asserts.isTrue(userDao.isValidNewUserId(entity.getLogin(), entity.getId()), FIELD_LOGIN,
                ValidationError.LESS_THAN);
    }
}
