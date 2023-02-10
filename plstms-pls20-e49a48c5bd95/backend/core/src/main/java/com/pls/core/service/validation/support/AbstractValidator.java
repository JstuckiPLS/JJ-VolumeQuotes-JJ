package com.pls.core.service.validation.support;

import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * Base class for validators. It holds state, thus implementation is not thread safe. Include your checks in
 * <code>validateImpl</code> method. Do not override <code>validate</code> method.
 * 
 * @author Viacheslav Krot
 * 
 * @param <Type>
 */
public abstract class AbstractValidator<Type> implements Validator<Type> {
    private final ValidationContext context = new ValidationContext();
    protected final Asserts asserts = new Asserts(context);
    protected boolean importValidator = false;

    @Override
    public void validate(Type entity) throws ValidationException {
        if (entity == null) {
            return;
        }
        context.clearContext();

        validateImpl(entity);

        if (context.hasErrors()) {
            throw new ValidationException(new HashMap<String, ValidationError>(context.getErrors()));
        }
    }

    /**
     * Validate component aggregated inside entity with another validator. Component name is used to prefix
     * failed constraints names.
     * <p>
     * So, if <code>componentName</code> is innerEntity, and validator fails constraint "id", then context
     * will hold "innerEntity.id" constraint.
     * 
     * @param <T>
     *            type of validator and component
     * @param validator
     *            another validator
     * @param component
     *            to validate
     * @param componentName
     *            name of component
     */
    protected final <T> void validateComponent(AbstractValidator<T> validator, T component, String componentName) {
        if (validator == null) {
            throw new IllegalArgumentException("Validatior may not be null");
        }

        if (StringUtils.isEmpty(componentName)) {
            throw new IllegalArgumentException("Component name should not be null or empty");
        }

        validator.context.clearContext();
        validator.validateImpl(component);
        validator.context.prefixErrors(componentName);

        context.addAllErrors(validator.context);
    }

    /**
     * Implementation of validation rules.
     * 
     * @param entity
     *            entity to validate.
     */
    protected abstract void validateImpl(Type entity);

    @Override
    public boolean isImportValidator() {
        return this.importValidator;
    }

    @Override
    public void setImportValidator(boolean isForImport) {
        this.importValidator = isForImport;
    }
}
