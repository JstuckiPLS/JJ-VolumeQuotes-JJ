package com.pls.core.service.validation.support;

import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link com.pls.core.service.validation.support.AbstractValidator}.
 * 
 * @author Aleksandr Leshchenko
 */
public class AbstractValidatorTest {
    private static final String VALID_ENTITY = "ValidEntity" + Math.random();
    private static final String INVALID_ENTITY = "InvalidEntity" + Math.random();
    private static final String INVALID_ENTITY_2 = "InvalidEntity_2" + Math.random();
    private static final String INVALID_ENTITY_3 = "InvalidEntity_3" + Math.random();
    private static final String COMPONENT = "component" + Math.random();

    private TestValidator validator;

    @Before
    public void init() {
        validator = spy(new TestValidator());
    }

    @Test
    public void shouldValidateEntity() throws ValidationException {
        validator.validate(VALID_ENTITY);

        verify(validator).validateImpl(VALID_ENTITY);
    }

    @Test
    public void shouldFailWithInvalidEntity() throws ValidationException {
        try {
            validator.validate(INVALID_ENTITY);
            fail();
        } catch (ValidationException e) {
            assertSame(ValidationError.FORMAT, e.getErrors().get(INVALID_ENTITY));
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    public void shouldClearContextForComponent() throws ValidationException {
        try {
            validator.validate(INVALID_ENTITY_3);
            fail();
        } catch (ValidationException e) {
            assertEquals(3, e.getErrors().size());
            assertSame(ValidationError.FORMAT, e.getErrors().get(COMPONENT + "." + INVALID_ENTITY));
            assertSame(ValidationError.FORMAT, e.getErrors().get(COMPONENT + "." + INVALID_ENTITY_2));
            assertSame(ValidationError.FORMAT, e.getErrors().get(INVALID_ENTITY_3));
        }
    }

    @Test
    public void shouldClearSelfContext() throws ValidationException {
        try {
            validator.validate(INVALID_ENTITY);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertSame(ValidationError.FORMAT, e.getErrors().get(INVALID_ENTITY));
        }

        try {
            validator.validate(INVALID_ENTITY_2);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.getErrors().size());
            assertSame(ValidationError.FORMAT, e.getErrors().get(INVALID_ENTITY_2));
        }
    }

    /**
     * Validator for testing purposes.
     * 
     * @author Aleksandr Leshchenko
     */
    private static class TestValidator extends AbstractValidator<String> {
        @Override
        protected void validateImpl(String entity) {
            if (entity == INVALID_ENTITY || entity == INVALID_ENTITY_2) {
                asserts.fail(entity, ValidationError.FORMAT);
            } else if (entity == INVALID_ENTITY_3) {
                TestValidator testValidator = new TestValidator();
                validateComponent(testValidator, INVALID_ENTITY, COMPONENT);
                validateComponent(testValidator, INVALID_ENTITY_2, COMPONENT);
                asserts.fail(entity, ValidationError.FORMAT);
            }
        }

    }
}
