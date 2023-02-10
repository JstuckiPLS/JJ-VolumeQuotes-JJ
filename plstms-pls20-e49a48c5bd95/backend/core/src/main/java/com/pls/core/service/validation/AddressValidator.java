package com.pls.core.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.service.validation.support.AbstractValidator;

import java.util.Arrays;
import java.util.List;

/**
 * Address validator.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
public class AddressValidator extends AbstractValidator<AddressEntity> {

    public static final String CANADA_COUNTRY_CODE = "CAN";

    public static final String MEXICO_COUNTRY_CODE = "MEX";

    public static final String USA_COUNTRY_CODE = "USA";

    public static final List<String> COUNTRIES_CODES_WITH_STATE = Arrays.asList(CANADA_COUNTRY_CODE, MEXICO_COUNTRY_CODE,
            USA_COUNTRY_CODE);

    @Override
    protected void validateImpl(AddressEntity entity) {
        asserts.notEmpty(entity.getCity(), "city");
        asserts.notNull(entity.getCountry(), "country");
        if (entity.getCountry() != null) {
            asserts.notEmpty(entity.getCountry().getId(), "countryId");
        }
        if (entity.getState() != null) {
            asserts.notNull(entity.getState().getStatePK(), "statePK");
            if (entity.getState().getStatePK() != null) {
                String stateCode = entity.getState().getStatePK().getStateCode();
                String countryCode = entity.getState().getStatePK().getCountryCode();

                //We validate is stateCode empty only for 3 countries (Usa, Canada, Mexico)
                asserts.isTrue((COUNTRIES_CODES_WITH_STATE.contains(countryCode) && stateCode != null && !stateCode.isEmpty())
                        || (!COUNTRIES_CODES_WITH_STATE.contains(countryCode)), "stateCode", ValidationError.IS_EMPTY);
                asserts.notEmpty(countryCode, "countryCode");
            }
        }
    }
}
