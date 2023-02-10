package com.pls.smc3.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test class for {@link LtlSMC3Validator}.
 * 
 * @author PAVANI CHALLA
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlSMC3ValidatorTestIt {

    @InjectMocks
    private LtlSMC3Validator validator;

    /**
     * Test Validate Country.
     */
    @Test
    public void testValidateCountry() {

        assertTrue(validator.validateCountry("USA"));

        assertTrue(validator.validateCountry("MEX"));

        assertTrue(validator.validateCountry("CAN"));

        assertFalse(validator.validateCountry("IND"));
    }

    /**
     * Test Validate ZipCode.
     */
    @Test
    public void testValidateZipCode() {

        // Tests to validate USA zip.
        assertTrue(validator.validateZipCode("500", "USA"));
        assertTrue(validator.validateZipCode("12312", "USA"));
        assertFalse(validator.validateZipCode("5001", "USA"));
        assertFalse(validator.validateZipCode("5001A", "USA"));
        assertFalse(validator.validateZipCode("ZIPCODE", "USA"));
        assertFalse(validator.validateZipCode("ZIPCOD", "USA"));
        assertFalse(validator.validateZipCode("123_12", "USA"));
        assertFalse(validator.validateZipCode("123126", "USA"));

        // Tests to validate MEX zip.
        assertFalse(validator.validateZipCode("5001", "MEX"));
        assertTrue(validator.validateZipCode("50015", "MEX"));

        assertFalse(validator.validateZipCode("ABC12", "MEX"));
        assertFalse(validator.validateZipCode("QWERTY", "MEX"));

        // Tests to validate CAN zip.
        assertFalse(validator.validateZipCode("5001", "CAN"));
        assertTrue(validator.validateZipCode("abc123", "CAN"));
        assertTrue(validator.validateZipCode("12312", "CAN"));
        assertTrue(validator.validateZipCode("ZIP", "CAN"));
        assertFalse(validator.validateZipCode("ZIP_12", "CAN"));
        assertTrue(validator.validateZipCode("123", "CAN"));
        assertFalse(validator.validateZipCode("12312", "IND"));
        assertFalse(validator.validateZipCode("ABCQWE", "IND"));
    }

}
