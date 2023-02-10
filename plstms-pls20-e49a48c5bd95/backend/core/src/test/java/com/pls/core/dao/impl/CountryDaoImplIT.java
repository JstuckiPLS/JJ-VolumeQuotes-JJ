package com.pls.core.dao.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.CountryDao;
import com.pls.core.domain.bo.PageQueryBO;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.exception.EntityNotFoundException;

/**
 * Test cases for {@link com.pls.core.dao.impl.CountryDaoImpl}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class CountryDaoImplIT extends AbstractDaoTest {

    private static final String COUNTRY_CODE = "USA";

    private static final String COUNTRY_KEYWORD = "Un";

    @Autowired
    private CountryDao sut;

    @Test
    public void testGetExistingEntity() throws EntityNotFoundException {
        CountryEntity entity = sut.get(COUNTRY_CODE);
        Assert.assertNotNull(entity);
        Assert.assertEquals(COUNTRY_CODE, entity.getId());
    }

    @Test
    public void testGetAllEntities() {
        List<CountryEntity> entities = sut.getAll();
        Assert.assertNotNull(entities);
        Assert.assertFalse(entities.isEmpty());
    }

    @Test
    public void testGetActiveCountries() {
        List<CountryEntity> entities = sut.getActiveCountries();
        Assert.assertNotNull(entities);
        Assert.assertFalse(entities.isEmpty());
    }

    @Test
    public void testGetCountriesByCodeOrName() {
        List<CountryEntity> entities = sut.getCountriesByCodeOrName(COUNTRY_KEYWORD, new PageQueryBO(0, 10));
        Assert.assertNotNull(entities);
        Assert.assertFalse(entities.isEmpty());
        Assert.assertTrue(entities.size() >= 3);

        for (CountryEntity entity : entities) {
            Assert.assertTrue(
                    entity.getId().startsWith(COUNTRY_KEYWORD) || entity.getName().startsWith(COUNTRY_KEYWORD));
        }
    }
}
