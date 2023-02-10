package com.pls.core.service.validation.support;

import java.util.Map;

import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.ValidationException;

/**
 * Base interface for entity validators. Throws {@link ValidationException} when validation fails. Returns
 * void in normal flow.
 * <p>
 * Validation errors are put to ValidationException.errors field. It contains a {@link Map} with String key
 * and {@link ValidationError} value, key holds constraint name that failed.
 * 
 * 
 * @author Viacheslav Krot
 * 
 * @param <Type>
 *            type of entity to validate.
 */
public interface Validator<Type> {
    /**
     * Validate entity. Throws {@link ValidationException} when validation fails. Returns void in normal flow.
     * 
     * @param entity
     *            entity to validate.
     * @throws ValidationException
     *             when validation fails.
     */
    void validate(Type entity) throws ValidationException;

    /**
     * Marks validator as used during import. This flag can change validation logic.
     * @param isForImport true if used during import, false otherwise
     */
    void setImportValidator(boolean isForImport);

    /**
     * Checks is validator purpose is import.
     * @return true if used for import
     */
    boolean isImportValidator();
}
