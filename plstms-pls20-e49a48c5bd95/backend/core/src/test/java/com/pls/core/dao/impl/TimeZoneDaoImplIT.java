package com.pls.core.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.TimeZoneDao;
import com.pls.core.domain.TimeZoneEntity;

/**
 * Test case for {@link TimeZoneDaoImpl}.
 *
 * @author Sergey Kirichenko
 */
public class TimeZoneDaoImplIT extends AbstractDaoTest {

    public static final byte EXISTING_TIME_ZONE = (byte) 5;
    public static final byte EXISTING_LOCAL_OFFSET = (byte) 0;
    public static final String EXISTING_ZIP = "44136";
    public static final String NOT_EXISTING_ZIP = "NOT_EXISTING_ZIP";
    public static final String COUNTRY_CODE = "USA";
    public static final String NOT_EXISTING_COUNTRY = "NOT_EXISTING_COUNTRY";

    @Autowired
    private TimeZoneDao sut;

    @Test
    public void shouldFindByCountryZip() {
        TimeZoneEntity timeZoneEntity = sut.findByCountryZip(COUNTRY_CODE, EXISTING_ZIP);
        Assert.assertNotNull(timeZoneEntity);
        Assert.assertEquals(EXISTING_TIME_ZONE, timeZoneEntity.getTimezone());
        Assert.assertEquals(EXISTING_LOCAL_OFFSET, timeZoneEntity.getLocalOffset());
    }

    @Test
    public void shouldNotFindByWrongZip() {
        TimeZoneEntity timeZoneEntity = sut.findByCountryZip(COUNTRY_CODE, NOT_EXISTING_ZIP);
        Assert.assertNull(timeZoneEntity);
    }

    @Test
    public void shouldNotFindByWrongCountry() {
        TimeZoneEntity timeZoneEntity = sut.findByCountryZip(NOT_EXISTING_COUNTRY, EXISTING_ZIP);
        Assert.assertNull(timeZoneEntity);
    }
}
