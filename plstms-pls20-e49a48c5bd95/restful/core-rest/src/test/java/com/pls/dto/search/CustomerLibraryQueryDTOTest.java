package com.pls.dto.search;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test cases for {@link CustomerLibraryQueryDTO}.
 * 
 * @author Artem Arapov
 * 
 */
public class CustomerLibraryQueryDTOTest {

    private static final String FULL_QUERY_STRING = "2001-01-01,2013-06-01,2002-01-01,2012-06-01,1,A,";

    private static final String CREATED_DATE_QUERY_STRING = "2001-01-01,2013-06-01,,,,,";

    private static final String LAST_LOAD_QUERY_STRING = ",,2002-01-01,2012-06-01,,,";

    private static final String PERSON_ID_QUERY_STRING = ",,,,1,,";

    private static final String STATUS_QUERY_STRING = ",,,,,A,";

    private static final String EXPECTED_FROM_DATE = "2001-01-01";

    private static final String EXPECTED_TO_DATE = "2013-06-01";

    private static final String EXPECTED_LOAD_FROM_DATE = "2002-01-01";

    private static final String EXPECTED_LOAD_TO_DATE = "2012-06-01";

    private static final String EXPECTED_PERSON_ID = "1";

    private static final String EXPECTED_STATUS = "A";

    @Test
    public void testFullQueryString() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO(FULL_QUERY_STRING);

        assertEquals(EXPECTED_FROM_DATE, dto.getFromDate());
        assertEquals(EXPECTED_TO_DATE, dto.getToDate());
        assertEquals(EXPECTED_LOAD_FROM_DATE, dto.getFromLoadDate());
        assertEquals(EXPECTED_LOAD_TO_DATE, dto.getToLoadDate());
        assertEquals(EXPECTED_PERSON_ID, dto.getPersonId());
        assertEquals(EXPECTED_STATUS, dto.getStatus());
    }

    @Test
    public void testCreatedDateQueryString() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO(CREATED_DATE_QUERY_STRING);

        assertEquals(EXPECTED_FROM_DATE, dto.getFromDate());
        assertEquals(EXPECTED_TO_DATE, dto.getToDate());
        assertEquals(0, dto.getFromLoadDate().length());
        assertEquals(0, dto.getToLoadDate().length());
        assertEquals(0, dto.getPersonId().length());
        assertEquals(0, dto.getStatus().length());
    }

    @Test
    public void testLastLoadQueryString() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO(LAST_LOAD_QUERY_STRING);

        assertEquals(0, dto.getFromDate().length());
        assertEquals(0, dto.getToDate().length());
        assertEquals(EXPECTED_LOAD_FROM_DATE, dto.getFromLoadDate());
        assertEquals(EXPECTED_LOAD_TO_DATE, dto.getToLoadDate());
        assertEquals(0, dto.getPersonId().length());
        assertEquals(0, dto.getStatus().length());
    }

    @Test
    public void testPersonQueryString() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO(PERSON_ID_QUERY_STRING);

        assertEquals(0, dto.getFromDate().length());
        assertEquals(0, dto.getToDate().length());
        assertEquals(0, dto.getFromLoadDate().length());
        assertEquals(0, dto.getToLoadDate().length());
        assertEquals(EXPECTED_PERSON_ID, dto.getPersonId());
        assertEquals(0, dto.getStatus().length());
    }

    @Test
    public void testStatusQueryString() {
        CustomerLibraryQueryDTO dto = new CustomerLibraryQueryDTO(STATUS_QUERY_STRING);

        assertEquals(0, dto.getFromDate().length());
        assertEquals(0, dto.getToDate().length());
        assertEquals(0, dto.getFromLoadDate().length());
        assertEquals(0, dto.getToLoadDate().length());
        assertEquals(0, dto.getPersonId().length());
        assertEquals(EXPECTED_STATUS, dto.getStatus());
    }
}
