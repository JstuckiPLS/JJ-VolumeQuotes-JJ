package com.pls.core.service.address;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.CountryDao;
import com.pls.core.domain.bo.PageQueryBO;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.service.address.impl.CountryServiceImpl;

/**
 * Tests for {@link CountryServiceImplTest}.
 * 
 * @author Andrey Kachur
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CountryServiceImplTest {

    @InjectMocks
    private CountryServiceImpl service;
    @Mock
    private CountryDao countryDao;

    @Test
    public void testFindCountries() {
        String search = new Object().toString();
        List<CountryEntity> list = new ArrayList<CountryEntity>();
        Mockito.when(countryDao.getCountriesByCodeOrName(search, new PageQueryBO(0, 10))).thenReturn(list);
        List<CountryEntity> result = service.findCountries(search, 10);

        Assert.assertEquals(list, result);
    }

}
