package com.pls.core.service.address;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.ZipCodeDao;
import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.bo.AddressBO;
import com.pls.core.service.address.impl.ZipServiceImpl;
import com.pls.core.service.validation.ValidationException;

/**
 * Test class for {@link com.pls.core.service.address.impl.ZipServiceImpl}.
 *
 * @author Sergey Kirichenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ZipServiceImplTest {

    private static final String COUNTRY_CODE = "USA";

    private static final String ZIP_CODE = "90026";

    @InjectMocks
    private ZipServiceImpl zipService;

    @Mock
    private ZipCodeDao zipCodeDao;

    @Before
    public void setUp() {
        ArrayList<ZipCodeEntity> zipCodeEntities = new ArrayList<ZipCodeEntity>();
        zipCodeEntities.add(new ZipCodeEntity());
        when(zipCodeDao.findZipCodesByCountryCodeAndSearchCriteria(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(
                zipCodeEntities);
    }

    @Test
    public void testFindZips() throws ValidationException {
        List<ZipCodeEntity> zips = zipService.findZips(COUNTRY_CODE, ZIP_CODE, 1);
        Assert.assertNotNull(zips);
        Assert.assertFalse(zips.isEmpty());
        Assert.assertEquals(1, zips.size());

        verify(zipCodeDao).findZipCodesByCountryCodeAndSearchCriteria(COUNTRY_CODE, ZIP_CODE, 1);
    }

    @Test(expected = ValidationException.class)
    public void testFindZipsWithoutSearchCriteria() throws ValidationException {
        zipService.findZips(COUNTRY_CODE, null, 1);
    }

    @Test
    public void testValidateAddresses() {
        AddressBO bo = new AddressBO();
        bo.setZip("65000,11111");
        bo.setState("IL,OO");
        bo.setCountry("MEX,HHH");
        bo.setCity("Cranberry,New New York");

        Mockito.when(zipCodeDao.isZipCodesExist(Arrays.asList("65000", "11111"))).thenReturn(Collections.singletonList("65000"));

        Mockito.when(zipCodeDao.isStateCodesExist(Arrays.asList("IL", "OO"))).thenReturn(Collections.singletonList("IL"));

        Mockito.when(zipCodeDao.isCountryCodesExist(Arrays.asList("MEX", "HHH"))).thenReturn(Collections.singletonList("MEX"));
 
        Mockito.when(zipCodeDao.isCitiesExist(Arrays.asList("CRANBERRY", "NEW NEW YORK"))).thenReturn(Collections.singletonList("CRANBERRY"));

        AddressBO validationResult = zipService.validateAddress(bo);
        Assert.assertEquals("11111", validationResult.getZip());
        Assert.assertEquals("OO", validationResult.getState());
        Assert.assertEquals("HHH", validationResult.getCountry());
        Assert.assertEquals("NEW NEW YORK", validationResult.getCity());
    }
}
