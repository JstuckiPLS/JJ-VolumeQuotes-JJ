package com.pls.core.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.OrganizationAPIDetailsDao;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;

/**
 * {@link OrganizationAPIDetailsDaoImpl} IT tests.
 * @author Stas Norochevskiy
 *
 */
public class OrganizationAPIDetailsDaoImplIT extends AbstractDaoTest {

    @Autowired
    private OrganizationAPIDetailsDao organizationAPIDetailsDao;

    @Test
    public void testGetCarrierAPIDetailsByOrgId() {
        OrganizationAPIDetailsEntity entity = organizationAPIDetailsDao.getCarrierAPIDetailsByOrgId(13L);
        Assert.assertNotNull(entity);
    }
}
