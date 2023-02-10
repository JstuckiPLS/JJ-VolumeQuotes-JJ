package com.pls.core.service.validation;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link JobNumberValidator}.
 * 
 * @author Alexander Nalapko
 *
 */
public class JobNumberValidatorTest {

    private static final String[] JOB_WORD = { "BB", "BI", "BM", "BN", "BO", "BP", "BS", "FB", "FI", "FM", "FN", "FO",
            "FP", "FS", "W", "WM", "WME", "WMP", "WMS", "WS" };
    private static final String VALID_NUMBER = "BB2015";

    JobNumberValidator validator = new JobNumberValidator();

    @Before
    public void init() {
        validator = spy(new JobNumberValidator());
    }

    @Test
    public void shouldValidateTest() throws ValidationException {
        validator.validate(VALID_NUMBER);
        verify(validator).validateImpl(VALID_NUMBER);
    }

    @Test(expected = ValidationException.class)
    public void invalidTest() throws ValidationException {
        validator.validate(generateInvalidJobNumber());
    }

    @Test
    public void validTest() throws ValidationException {
        for (int i = 0; i < 100; i++) {
            validator.validate(generateValidJobNumber());
        }
    }

    private String generateValidJobNumber() {
        if (Math.random() > 0.5) {
            return JOB_WORD[(int) Math.round(Math.random() * (JOB_WORD.length - 1))]
                    + Math.round(Math.random() * (9999 - 100) + 100);
        } else {
            return Math.round(Math.random() * (99999 - 10000) + 10000)
                    + String.valueOf((char) (Math.random() * (90 - 65) + 65));
        }
    }

    private String generateInvalidJobNumber() {
        return String.valueOf(Math.round(Math.random() * (99999 - 100) + 100));
    }
}
