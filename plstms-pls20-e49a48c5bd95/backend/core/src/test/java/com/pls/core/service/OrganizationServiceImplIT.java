
package com.pls.core.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.organization.CompanyCodeEntity;

/**
 * Integration tests for {@link OrganizationService}.
 * 
 * @author Stas Norochevskiy
 *
 */
public class OrganizationServiceImplIT extends BaseServiceITClass {

    @Autowired
    private OrganizationService organizationServiceImpl;

    @Test
    public void testGetCompanyCodes() {
        List<CompanyCodeEntity> companyCodes = organizationServiceImpl.getCompanyCodes();
        Assert.assertNotNull(companyCodes);
        Assert.assertEquals(14, companyCodes.size());
    }
}
