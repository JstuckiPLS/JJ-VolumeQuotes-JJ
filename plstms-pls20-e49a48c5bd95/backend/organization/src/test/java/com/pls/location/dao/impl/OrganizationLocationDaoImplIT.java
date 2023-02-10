package com.pls.location.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.BillToDao;
import com.pls.core.dao.OrganizationDao;
import com.pls.core.domain.bo.CustomerLocationListItemBO;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.location.dao.OrganizationLocationDao;

/**
 * Test cases for {@link OrganizationLocationDaoImpl}.
 * 
 * @author Artem Arapov
 */
public class OrganizationLocationDaoImplIT extends AbstractDaoTest {

    @Autowired
    private OrganizationLocationDao sut;

    @Autowired
    private OrganizationDao orgDao;

    @Autowired
    private BillToDao billToDao;

    @Test
    public void testGetCustomerLocations() {
        List<CustomerLocationListItemBO> list = sut.getCustomerLocations(1L);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void testSaveOrganizationLocation() {
        OrganizationLocationEntity entity = createTestEntity();
        sut.saveOrUpdate(entity);
        OrganizationLocationEntity actualEntity = sut.find(entity.getId());
        Assert.assertNotNull(actualEntity);
    }

    @Test
    public void shouldCheckAEDoesNotExistForSpecifiedCustomer() {
        Boolean result = sut.isAEExistsForLocationsExcludeSpecified(1L, 1L, 1L);

        Assert.assertFalse(result.booleanValue());
    }

    @Test
    public void shouldCheckAEExistForSpecifiedCustomer() {
        Boolean result = sut.isAEExistsForLocationsExcludeSpecified(1L, 2L, 4L);

        Assert.assertTrue(result.booleanValue());
    }

    @Test
    public void shouldGetShipmentLocations() {
        getSession().createQuery("update OrganizationLocationEntity set billTo.id = 1 where id = 2").executeUpdate();

        List<ShipmentLocationBO> shipmentLocations = sut.getShipmentLocations(1L, 1L);

        Assert.assertEquals(2, shipmentLocations.size());
        ShipmentLocationBO location = getLocationById(shipmentLocations, 1L);
        Assert.assertNotNull(location);
        Assert.assertEquals("MAIN STEEL", location.getName());
        Assert.assertNull(location.getBillToId());
        Assert.assertTrue(location.isDefaultNode());

        location = getLocationById(shipmentLocations, 2L);
        Assert.assertNotNull(location);
        Assert.assertEquals("COLUMBIA, SC", location.getName());
        Assert.assertEquals(new Long(1), location.getBillToId());
        Assert.assertNull(location.isDefaultNode());
    }

    private ShipmentLocationBO getLocationById(List<ShipmentLocationBO> locations, Long locationId) {
        for (ShipmentLocationBO location : locations) {
            if (ObjectUtils.equals(location.getId(), locationId)) {
                return location;
            }
        }
        return null;
    }

    private OrganizationLocationEntity createTestEntity() {
        OrganizationEntity organization = orgDao.find(1L);
        OrganizationLocationEntity location = new OrganizationLocationEntity();
        location.setOrganization(organization);
        location.setLocationName(String.valueOf(Math.random()));
        location.setDefaultNode(Boolean.TRUE);
        AccountExecutiveEntity accExecEntity = createTestAccountExecutiveEntity(location);
        location.getAccountExecutives().add(accExecEntity);
        BillToEntity billTo = billToDao.find(1L);
        location.setBillTo(billTo);

        return location;
    }

    private AccountExecutiveEntity createTestAccountExecutiveEntity(OrganizationLocationEntity location) {
        UserEntity user = new UserEntity();
        user.setId(1L);
        AccountExecutiveEntity accExecEntity = new AccountExecutiveEntity(location, user);
        accExecEntity.setEffectiveDate(new Date());
        accExecEntity.setExpirationDate(new Date());

        return accExecEntity;
    }
}
