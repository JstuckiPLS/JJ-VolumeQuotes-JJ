package com.pls.core.dao.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.ZipCodeDao;
import com.pls.core.domain.address.ZipCodeEntity;

/**
 * Test cases for {@link com.pls.core.dao.impl.ZipCodeDaoImpl}.
 *
 * @author Sergey Kirichenko
 */
public class ZipCodeDaoImplIT extends AbstractDaoTest {

    private static final String COUNTRY_CODE = "USA";

    private static final String ZIP_SEARCH = "9002";

    private static final String CITY_SEARCH = "LOS ANG";

    private static final String STATE_SEARCH = "WV";

    private static final String CITY_NAME = "LOS ANGELES";

    @Autowired
    private ZipCodeDao sut;

    @Test
    public void testFindZipCodesByCountryCodeAndSearchCriteria() {
        List<ZipCodeEntity> zipCodes = sut.findZipCodesByCountryCodeAndSearchCriteria(COUNTRY_CODE, ZIP_SEARCH, 1);
        Assert.assertNotNull(zipCodes);
        Assert.assertFalse(zipCodes.isEmpty());
        Assert.assertEquals(1, zipCodes.size());
        Assert.assertTrue(StringUtils.contains(zipCodes.get(0).getZipCode(), ZIP_SEARCH));

        zipCodes = sut.findZipCodesByCountryCodeAndSearchCriteria(COUNTRY_CODE, CITY_SEARCH, 1);
        Assert.assertNotNull(zipCodes);
        Assert.assertFalse(zipCodes.isEmpty());
        Assert.assertEquals(1, zipCodes.size());
        Assert.assertTrue(StringUtils.contains(zipCodes.get(0).getCity(), CITY_SEARCH));

        zipCodes = sut.findZipCodesByCountryCodeAndSearchCriteria(COUNTRY_CODE, STATE_SEARCH, 1);
        Assert.assertNotNull(zipCodes);
        Assert.assertFalse(zipCodes.isEmpty());
        Assert.assertEquals(1, zipCodes.size());
        Assert.assertTrue(StringUtils.contains(zipCodes.get(0).getStateCode(), STATE_SEARCH));

        zipCodes = sut.findZipCodesByCountryCodeAndSearchCriteria(null, CITY_NAME, 1);
        Assert.assertNotNull(zipCodes);
        Assert.assertFalse(zipCodes.isEmpty());
        Assert.assertEquals(1, zipCodes.size());
        Assert.assertTrue(StringUtils.contains(zipCodes.get(0).getCity(), CITY_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindZipCodesByCountryCodeAndSearchCriteriaWithoutCriteria() {
        sut.findZipCodesByCountryCodeAndSearchCriteria(COUNTRY_CODE, null, null);
    }

    @Test
    public void testValidateAddresses() {
        Assert.assertTrue(sut.isZipCodesExist(Collections.singletonList("65000")).contains("65000"));
        Assert.assertTrue(sut.isZipCodesExist(Collections.singletonList("11111")).isEmpty());
        Assert.assertTrue(sut.isZipCodesExist(Collections.singletonList(" 65000 ")).contains("65000"));

        Assert.assertTrue(sut.isStateCodesExist(Collections.singletonList("IL")).contains("IL"));
        Assert.assertTrue(sut.isStateCodesExist(Collections.singletonList("XX")).isEmpty());
        Assert.assertTrue(sut.isStateCodesExist(Collections.singletonList(" PA ")).contains("PA"));

        Assert.assertTrue(sut.isCitiesExist(Collections.singletonList("New York")).contains("NEW YORK"));
        Assert.assertTrue(sut.isCitiesExist(Collections.singletonList("blabla")).isEmpty());
        Assert.assertTrue(sut.isCitiesExist(Collections.singletonList(" NEW YORK ")).contains("NEW YORK"));

        Assert.assertTrue(sut.isCountryCodesExist(Collections.singletonList("USA")).contains("USA"));
        Assert.assertTrue(sut.isCountryCodesExist(Collections.singletonList("MEX")).contains("MEX"));
        Assert.assertTrue(sut.isCountryCodesExist(Collections.singletonList("CAN")).contains("CAN"));
        Assert.assertTrue(sut.isCountryCodesExist(Collections.singletonList("XXX")).isEmpty());
        Assert.assertTrue(sut.isCountryCodesExist(Collections.singletonList(" XXX ")).isEmpty());

        Assert.assertTrue(sut.isCountryCodesExist(Collections.singletonList("USa")).contains("USA"));
        Assert.assertTrue(sut.isCountryCodesExist(Collections.singletonList("MeX")).contains("MEX"));
        Assert.assertTrue(sut.isCountryCodesExist(Collections.singletonList(" can ")).contains("CAN"));
    }
}
