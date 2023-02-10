package com.pls.core.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.OrganizationFreightBillPayToDao;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.OrganizationFreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.shared.Status;

/**
 * Test cases for {@link OrganizationFreightBillPayToDao}.
 * 
 * @author Artem Arapov
 *
 */
public class OrganizationFreightBillPayToDaoImplIT extends AbstractDaoTest {

    private static final Long EXPECTED_ORG_ID = 1L;

    @Autowired
    private OrganizationFreightBillPayToDao sut;

    @Test
    public void shouldFindFreightBillPayToByOrganization() {
        FreightBillPayToEntity entity = sut.getActiveFreightBillPayToByOrgId(EXPECTED_ORG_ID);

        Assert.assertNotNull(entity);
    }

    @Test
    public void shouldAddNewEntity() {
        OrganizationFreightBillPayToEntity expectedEntity = getRandomEntity();

        sut.saveOrUpdate(expectedEntity);
        flushAndClearSession();

        OrganizationFreightBillPayToEntity actualEntity = sut.find(expectedEntity.getId());
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(expectedEntity.getId(), actualEntity.getId());
    }

    @Test
    public void shouldInactivateExistingByOrgId() {
        sut.inactivateExistingByOrgId(EXPECTED_ORG_ID);

        List<OrganizationFreightBillPayToEntity> actualList = getListByOrgId(EXPECTED_ORG_ID);
        for (OrganizationFreightBillPayToEntity item : actualList) {
            Assert.assertEquals(Status.INACTIVE, item.getStatus());
        }
    }

    @SuppressWarnings("unchecked")
    private List<OrganizationFreightBillPayToEntity> getListByOrgId(Long orgId) {
        return getSession().createCriteria(OrganizationFreightBillPayToEntity.class).add(Restrictions.eq("orgId", orgId)).list();
    }

    private OrganizationFreightBillPayToEntity getRandomEntity() {
        FreightBillPayToEntity payToEntity = new FreightBillPayToEntity();
        payToEntity.setAccountNum(String.valueOf(Math.random()));
        payToEntity.setCompany(String.valueOf(Math.random()));
        payToEntity.setContactName(String.valueOf(Math.random()));
        payToEntity.setEmail(String.valueOf(Math.random()));
        payToEntity.setAddress(getRandomAddress());

        OrganizationFreightBillPayToEntity entity = new OrganizationFreightBillPayToEntity();
        entity.setFreightBillPayTo(payToEntity);
        entity.setOrgId(EXPECTED_ORG_ID);
        entity.setStatus(Status.ACTIVE);

        return entity;
    }

    private AddressEntity getRandomAddress() {
        AddressEntity address = new AddressEntity();
        address.setAddress1(String.valueOf(Math.random()));
        address.setAddress2(String.valueOf(Math.random()));
        address.setCity(String.valueOf(Math.random()));
        address.setZip(StringUtils.left(String.valueOf(Math.random()), 10));

        CountryEntity country = new CountryEntity();
        country.setId("USA");
        address.setCountry(country);

        StateEntity stateEntity = new StateEntity();
        StatePK statepk = new StatePK();
        statepk.setStateCode("FL");
        statepk.setCountryCode("USA");
        stateEntity.setStatePK(statepk);
        address.setState(stateEntity);

        return address;
    }
}
