package com.pls.dtobuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;

import com.pls.core.shared.Status;
import com.pls.dto.search.CustomerLibraryQueryDTO;
import com.pls.dtobuilder.util.DateUtils;
import com.pls.search.domain.co.GetCustomerCO;

/**
 * Test cases for {@link CustomerLibraryQueryDTOBuilder}.
 * 
 * @author Artem Arapov
 * 
 */
public class CustomerLibraryQueryDTOBuilderTest {

    private static final String ACTUAL_FROM_DATE = "2001-01-01";

    private static final String ACTUAL_TO_DATE = "2013-06-01";

    private static final String ACTUAL_LOAD_FROM_DATE = "2002-01-01";

    private static final String ACTUAL_LOAD_TO_DATE = "2012-06-01";

    private static final String ACTUAL_PERSON_ID = "1";

    private static final String ACTUAL_STATUS = "A";

    private static final String EMPTY_STRING = "";

    public static final Date EXPECTED_FROM_DATE = DateUtils.parseDateWithoutTimeZone(ACTUAL_FROM_DATE);

    public static final Date EXPECTED_TO_DATE = DateUtils.parseDateWithoutTimeZone(ACTUAL_TO_DATE);

    public static final Date EXPECTED_LOAD_FROM_DATE = DateUtils.parseDateWithoutTimeZone(ACTUAL_LOAD_FROM_DATE);

    public static final Date EXPECTED_LOAD_TO_DATE = DateUtils.parseDateWithoutTimeZone(ACTUAL_LOAD_TO_DATE);

    public static final Long EXPECTED_PERSON_ID = 1L;

    public static final Status EXPECTED_STATUS = Status.ACTIVE;

    private static final CustomerLibraryQueryDTOBuilder SUT = new CustomerLibraryQueryDTOBuilder();

    @Test
    public void testDTOWithFullFields() {
        CustomerLibraryQueryDTO dto = createFullCustomerCO();

        GetCustomerCO co = SUT.buildEntity(dto);

        assertNotNull(co);
        assertEquals(EXPECTED_FROM_DATE, co.getFromDate());
        assertEquals(EXPECTED_TO_DATE, co.getToDate());
        assertEquals(EXPECTED_LOAD_FROM_DATE, co.getFromLoadDate());
        assertEquals(EXPECTED_LOAD_TO_DATE, co.getToLoadDate());
        assertEquals(EXPECTED_PERSON_ID, co.getPersonId());
        assertEquals(EXPECTED_STATUS, co.getStatus());
    }

    @Test
    public void testDTOWithCreatedDatesFields() {
        CustomerLibraryQueryDTO dto = createCreatedDateCustomerCO();

        GetCustomerCO co = SUT.buildEntity(dto);

        assertNotNull(co);
        assertEquals(EXPECTED_FROM_DATE, co.getFromDate());
        assertEquals(EXPECTED_TO_DATE, co.getToDate());
        assertNull(co.getFromLoadDate());
        assertNull(co.getToLoadDate());
        assertNull(co.getPersonId());
        assertNull(co.getStatus());
    }

    @Test
    public void testDTOWithLastLoadDatesFields() {
        CustomerLibraryQueryDTO dto = createLastLoadDateCustomerCO();

        GetCustomerCO co = SUT.buildEntity(dto);

        assertNotNull(co);
        assertNull(co.getFromDate());
        assertNull(co.getToDate());
        assertEquals(EXPECTED_LOAD_FROM_DATE, co.getFromLoadDate());
        assertEquals(EXPECTED_LOAD_TO_DATE, co.getToLoadDate());
        assertNull(co.getPersonId());
        assertNull(co.getStatus());
    }

    @Test
    public void testDTOWithPersonIdField() {
        CustomerLibraryQueryDTO dto = createPersonIdCustomerCO();

        GetCustomerCO co = SUT.buildEntity(dto);

        assertNotNull(co);
        assertNull(co.getFromDate());
        assertNull(co.getToDate());
        assertNull(co.getFromLoadDate());
        assertNull(co.getToLoadDate());
        assertEquals(EXPECTED_PERSON_ID, co.getPersonId());
        assertNull(co.getStatus());
    }

    @Test
    public void testDTOWithStatusField() {
        CustomerLibraryQueryDTO dto = createStatusCustomerCO();

        GetCustomerCO co = SUT.buildEntity(dto);

        assertNotNull(co);
        assertNull(co.getFromDate());
        assertNull(co.getToDate());
        assertNull(co.getFromLoadDate());
        assertNull(co.getToLoadDate());
        assertNull(co.getPersonId());
        assertEquals(EXPECTED_STATUS, co.getStatus());
    }

    private CustomerLibraryQueryDTO createFullCustomerCO() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO();

        dto.setFromDate(ACTUAL_FROM_DATE);
        dto.setToDate(ACTUAL_TO_DATE);
        dto.setFromLoadDate(ACTUAL_LOAD_FROM_DATE);
        dto.setToLoadDate(ACTUAL_LOAD_TO_DATE);
        dto.setPersonId(ACTUAL_PERSON_ID);
        dto.setStatus(ACTUAL_STATUS);
        dto.setName(EMPTY_STRING);

        return dto;
    }

    private CustomerLibraryQueryDTO createCreatedDateCustomerCO() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO();

        dto.setFromDate(ACTUAL_FROM_DATE);
        dto.setToDate(ACTUAL_TO_DATE);
        dto.setFromLoadDate(EMPTY_STRING);
        dto.setToLoadDate(EMPTY_STRING);
        dto.setPersonId(EMPTY_STRING);
        dto.setStatus(EMPTY_STRING);
        dto.setName(EMPTY_STRING);

        return dto;
    }

    private CustomerLibraryQueryDTO createLastLoadDateCustomerCO() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO();

        dto.setFromDate(EMPTY_STRING);
        dto.setToDate(EMPTY_STRING);
        dto.setFromLoadDate(ACTUAL_LOAD_FROM_DATE);
        dto.setToLoadDate(ACTUAL_LOAD_TO_DATE);
        dto.setPersonId(EMPTY_STRING);
        dto.setStatus(EMPTY_STRING);
        dto.setName(EMPTY_STRING);

        return dto;
    }

    private CustomerLibraryQueryDTO createPersonIdCustomerCO() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO();

        dto.setFromDate(EMPTY_STRING);
        dto.setToDate(EMPTY_STRING);
        dto.setFromLoadDate(EMPTY_STRING);
        dto.setToLoadDate(EMPTY_STRING);
        dto.setPersonId(ACTUAL_PERSON_ID);
        dto.setStatus(EMPTY_STRING);
        dto.setName(EMPTY_STRING);

        return dto;
    }

    private CustomerLibraryQueryDTO createStatusCustomerCO() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO();

        dto.setFromDate(EMPTY_STRING);
        dto.setToDate(EMPTY_STRING);
        dto.setFromLoadDate(EMPTY_STRING);
        dto.setToLoadDate(EMPTY_STRING);
        dto.setPersonId(EMPTY_STRING);
        dto.setStatus(ACTUAL_STATUS);
        dto.setName(EMPTY_STRING);

        return dto;
    }
}
