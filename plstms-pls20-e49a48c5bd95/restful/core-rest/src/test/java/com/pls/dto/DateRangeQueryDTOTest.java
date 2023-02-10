package com.pls.dto;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.pls.dto.query.DateRangeQueryDTO;

/**
 * Test cases for {@link DateRangeQueryDTO}.
 * 
 * @author Artem Arapov
 *
 */
public class DateRangeQueryDTOTest {
    private static final String FROM_DATE_STRING = "2012-12-01 -0500";
    private static final String TO_DATE_STRING = "2012-12-20 -0500";
    private static final String DEFAULT_STRING = "DEFAULT,2012-12-01 -0500,2012-12-20 -0500";
    private static final String DEFAULT_FROM_DATE_STRING = "DEFAULT,2012-12-01 -0500,";
    private static final String DEFAULT_TO_DATE_STRING = "DEFAULT,,2012-12-20 -0500";
    private static final String YEAR_STRING = "YEAR";
    private static final String EMPTY_STRING = "";

    @Test
    public void shouldCreateDTOWithDefaultString() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO(DEFAULT_STRING);

        assertNotNull(dto);
        assertEquals("DEFAULT", dto.getDateRange());
        assertEquals(FROM_DATE_STRING, dto.getFromDate());
        assertEquals(TO_DATE_STRING, dto.getToDate());
    }

    @Test
    public void shouldCreateDTOWithDefaultFromDateString() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO(DEFAULT_FROM_DATE_STRING);

        assertNotNull(dto);
        assertEquals("DEFAULT", dto.getDateRange());
        assertEquals(FROM_DATE_STRING, dto.getFromDate());
        assertEquals("", dto.getToDate());
    }

    @Test
    public void shouldCreateDTOWithDefaultToDateString() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO(DEFAULT_TO_DATE_STRING);

        assertNotNull(dto);
        assertEquals("DEFAULT", dto.getDateRange());
        assertEquals("", dto.getFromDate());
        assertEquals(TO_DATE_STRING, dto.getToDate());
    }

    @Test
    public void shouldCreateDTOWithYearString() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO(YEAR_STRING);

        assertNotNull(dto);
        assertTrue(dto.getFromDate().isEmpty());
        assertTrue(dto.getToDate().isEmpty());
    }

    @Test
    public void shouldCreateDTOWithEmptyString() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO(EMPTY_STRING);

        assertNotNull(dto);
        assertTrue(dto.getDateRange().isEmpty());
        assertTrue(dto.getFromDate().isEmpty());
        assertTrue(dto.getToDate().isEmpty());
    }

    @Test
    public void shouldCreateDTOWithDefaultSetters() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO();
        dto.setDateRange("DEFAULT");
        dto.setFromDate(FROM_DATE_STRING);
        dto.setToDate(TO_DATE_STRING);

        assertNotNull(dto);
        assertEquals(DEFAULT_STRING, dto.toString());
    }

    @Test
    public void shouldCreateDTOWithYearSetters() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO();
        dto.setDateRange("YEAR");

        assertNotNull(dto);
        assertEquals(YEAR_STRING, dto.toString());
    }

    @Test
    public void shouldCreateDTOWithDefaultFullSetters() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO();
        dto.setDateRange("DEFAULT");
        dto.setFromDate(FROM_DATE_STRING);
        dto.setToDate(TO_DATE_STRING);

        assertNotNull(dto);
        assertEquals(DEFAULT_STRING, dto.toString());
    }

    @Test
    public void shouldCreateDTOWithDefaultFromSetters() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO();
        dto.setDateRange("DEFAULT");
        dto.setFromDate(FROM_DATE_STRING);

        assertNotNull(dto);
        assertEquals(DEFAULT_FROM_DATE_STRING, dto.toString());
    }

    @Test
    public void shouldCreateDTOWithDefaultToSetters() {
        DateRangeQueryDTO dto = new DateRangeQueryDTO();
        dto.setDateRange("DEFAULT");
        dto.setToDate(TO_DATE_STRING);

        assertNotNull(dto);
        assertEquals(DEFAULT_TO_DATE_STRING, dto.toString());
    }
}
