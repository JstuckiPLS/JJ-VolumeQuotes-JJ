package com.pls.core.service;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.OrgServiceDao;
import com.pls.core.dao.OrganizationDao;
import com.pls.core.dao.SimpleOrganizationDao;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.OrganizationServiceImpl;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.support.Validator;

/**
 * Test cases for {@link com.pls.core.service.impl.OrganizationServiceImpl} class.
 * 
 * @author Alexander Nalapko
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizationServiceImplTest {

    private static final Long ORG_ID = 1L;

    private static final Long USER_ID = 1L;

    private OrganizationAPIDetailsEntity detailsEntity;

    @InjectMocks
    private OrganizationServiceImpl sut;

    @Mock
    private OrganizationDao dao;

    @Mock
    private SimpleOrganizationDao simpleOrgDao;

    @Mock
    private OrgServiceDao orgServiceDao;

    @Mock
    private Validator<OrganizationEntity> validator;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", USER_ID);

        detailsEntity = createDetailsEntity();
    }

    @Test
    public void testUpdateLogoForOrganization() throws EntityNotFoundException {
        long docId = 1L;
        sut.updateLogoForOrganization(ORG_ID, docId);
        verify(dao).updateLogoForOrganization(ORG_ID, docId, USER_ID);
    }

    @Test
    public void testSaveCarrierAPIDetails() throws Exception {
        sut.saveCarrierAPIDetails(detailsEntity);
        verify(dao).saveCarrierAPIDetails(detailsEntity);
    }

    private OrganizationAPIDetailsEntity createDetailsEntity() {
        OrganizationAPIDetailsEntity entity = new OrganizationAPIDetailsEntity();
        entity.setUrl("URL");
        entity.setLogin("login");
        entity.setPassword("password");
        entity.setToken("token");
        entity.setOrganizationEntity(new SimpleOrganizationEntity());

        return entity;
    }

}
