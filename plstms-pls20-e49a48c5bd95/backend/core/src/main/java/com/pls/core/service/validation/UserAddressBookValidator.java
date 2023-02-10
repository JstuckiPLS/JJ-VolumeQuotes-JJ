package com.pls.core.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pls.core.dao.CountryDao;
import com.pls.core.dao.StateDao;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * LTL Address validator.
 *
 * @author Aleksandr Leshchenko
 */
@Component
@Scope("prototype")
public class UserAddressBookValidator extends AbstractValidator<UserAddressBookEntity> {
    @Autowired
    private PhoneValidator phoneValidator;

    @Autowired
    private AddressValidator addressValidator;

    @Autowired
    private UserAddressBookDao addressDao;

    @Autowired
    private StateDao stateDao;

    @Autowired
    private CountryDao countryDao;

    @Override
    protected void validateImpl(UserAddressBookEntity entity) {
        if (!isImportValidator()) {
            asserts.notEmpty(entity.getAddressName(), "addressName");
        }
        asserts.notEmpty(entity.getContactName(), "contactName");

        if (entity.getId() == null
                && entity.getAddressCode() != null
                && ((entity.getOrgId() != null && !addressDao.isAddressUnique(
                entity.getOrgId(), entity.getAddressName(), entity.getAddressCode())))) {
            asserts.fail("addressCode", ValidationError.UNIQUE);
            asserts.fail("addressName", ValidationError.UNIQUE);
        }
        validateUserAddress(entity);
        validateCountry(entity);
    }

    private void validateUserAddress(UserAddressBookEntity entity) {
        if (entity.getFax() != null) {
            validateComponent(phoneValidator, entity.getFax(), "fax");
        }
        validateComponent(addressValidator, entity.getAddress(), "address");
    }

    private void validateCountry(UserAddressBookEntity entity) {

        boolean isValidCountry = AddressValidator.COUNTRIES_CODES_WITH_STATE.contains(entity.getAddress().getCountry().getId())
                ? (stateDao.getState(entity.getAddress().getStateCode(), entity.getAddress().getCountry().getId()) != null)
                : isValidCountryWithoutState(entity);

        if (!isValidCountry) {
            asserts.fail("stateCode", ValidationError.FORMAT);
            asserts.fail("countryCode", ValidationError.FORMAT);
        }
    }

    private boolean isValidCountryWithoutState(UserAddressBookEntity entity) {
        return countryDao.find(entity.getAddress().getCountry().getId()) != null
                && entity.getAddress().getCity() != null && entity.getAddress().getCountry() != null
                && entity.getAddress().getZip() != null;
    }
}
