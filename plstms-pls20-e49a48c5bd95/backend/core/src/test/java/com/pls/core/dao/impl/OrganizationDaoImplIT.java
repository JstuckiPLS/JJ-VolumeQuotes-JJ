package com.pls.core.dao.impl;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.OrganizationDao;
import com.pls.core.dao.SimpleOrganizationDao;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.exception.EntityNotFoundException;

/**
 * IT for {@link com.pls.core.dao.impl.OrganizationDaoImpl} .
 *
 * @author Alexander Nalapko
 */
public class OrganizationDaoImplIT extends AbstractDaoTest {
    private static final Long USER = 1L;

    // this organization has one active location
    private static final Long ORGANIZATION_ID = 5L;

    @Autowired
    private OrganizationDao dao;

    @Autowired
    private SimpleOrganizationDao simpleOrgDao;

    @Test
    public void shouldFetchLocation() throws EntityNotFoundException {
        OrganizationEntity org = dao.get(ORGANIZATION_ID);
        Assert.assertNotNull(org);
        Assert.assertNotNull(org.getLocations());
    }

    @Test
    public void updateLogoForOrganization() throws EntityNotFoundException {
        OrganizationEntity org = dao.get(1L);
        Long orgId = org.getId();
        org.setLogoId(null);

        // added logo
        Long logoId = 10L;
        dao.updateLogoForOrganization(orgId, logoId, USER);
        flushAndClearSession();

        Long actualLogoId = dao.get(orgId).getLogoId();
        Assert.assertNotNull("Image id is null", actualLogoId);
        Assert.assertEquals(logoId, actualLogoId);

    }

    @Test
    public void saveCarrierAPIDetails() throws EntityNotFoundException {
        OrganizationAPIDetailsEntity details = new OrganizationAPIDetailsEntity();
        details.setApiName("apiName");
        details.setUrl("URL");
        details.setLogin("login");
        details.setPassword("password");
        details.setToken("token");

        SimpleOrganizationEntity orgEntity = new SimpleOrganizationEntity();
        orgEntity.setId(new Long(1L));

        details.setOrganizationEntity(orgEntity);
        dao.saveCarrierAPIDetails(details);
    }
}
