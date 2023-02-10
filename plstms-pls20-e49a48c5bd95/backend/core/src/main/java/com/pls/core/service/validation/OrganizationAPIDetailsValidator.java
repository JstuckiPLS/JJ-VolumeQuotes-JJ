package com.pls.core.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Currier API details validator.
 * 
 * @author Alexander Nalapko
 * 
 */
@Component
public class OrganizationAPIDetailsValidator extends AbstractValidator<OrganizationAPIDetailsEntity> {

    @Override
    protected void validateImpl(OrganizationAPIDetailsEntity entity) {
        asserts.notEmpty(entity.getUrl(), "URL");
        asserts.notEmpty(entity.getLogin(), "login");
        asserts.notEmpty(entity.getPassword(), "password");
        asserts.notEmpty(entity.getToken(), "token");
        asserts.notNull(entity.getModification(), "modification");
        asserts.notNull(entity.getOrganizationEntity(), "carrier");
    }
}
