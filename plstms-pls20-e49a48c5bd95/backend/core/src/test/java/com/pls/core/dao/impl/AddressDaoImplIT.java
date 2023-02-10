package com.pls.core.dao.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.address.AddressDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.shared.AddressVO;
import com.pls.core.shared.Status;

/**
 * Very basic test cases for {@link com.pls.core.dao.impl.address.AddressDaoImpl} class. Just checks that it
 * works.
 * 
 * List of Dao methods that need to be covered: ----------------------------------------------
 * saveOrUpdate(DuplicatedAddressEntity) -- This is available in AbstractDaoImpl class findByPrimaryKey(Long
 * id) findAddressesByAddressVO(AddressVO)
 * 
 * 
 * @author Artem Arapov
 * @author Hima Bindu Challa
 */
public class AddressDaoImplIT extends AbstractDaoTest {
    private static final String ADDRESS1 = "TEST_ADDRESS_1";
    private static final String ADDRESS2 = "TEST_ADDRESS_2";

    private static final String CITY = "TEST_CITY";

    private static final String COUNTRY = "USA";

    private static final String COUNTRY_CODE = "USA";

    private static final long ID = 1L;

    private static final String POSTAL_CODE = "15237";

    private static final String STATE = "AL";

    private static final String STATE_CODE = "PA";

    @Autowired
    private AddressDao sut;

    @Test
    public void testCoordinates() {
        AddressEntity address = sut.find(ID);
        Assert.assertNotNull(address);

        Assert.assertNotNull(address.getLatitude());
        Assert.assertNotNull(address.getLongitude());

        Assert.assertEquals(37.844601, address.getLatitude().doubleValue(), 0.0);
        Assert.assertEquals(-122.251793, address.getLongitude().doubleValue(), 0.0);
    }

    @Test
    public void testCreate() {
        AddressEntity address = new AddressEntity();
        address.setAddress1("address1");
        address.setAddress2("address2");
        address.setCity("city");
        address.setZip("65000");

        CountryEntity country = (CountryEntity) getSession().get(CountryEntity.class, COUNTRY);
        Assert.assertNotNull(country);
        address.setCountry(country);

        StatePK statePK = new StatePK();
        statePK.setCountryCode(COUNTRY);
        statePK.setStateCode(STATE);

        StateEntity state = (StateEntity) getSession().get(StateEntity.class, statePK);
        Assert.assertNotNull(state);
        address.setState(state);


        sut.saveOrUpdate(address);

        flushAndClearSession();

        AddressEntity other = sut.find(address.getId());
        Assert.assertNotNull(other);
        Assert.assertEquals(address, other);
    }

    /**
     * AbstractDaoImpl DAO Method: saveOrUpdate(Type entity).
     * 
     */
    @Test
    public void testCreateAddressWithCompleteData() {
        AddressEntity newEntity = createAddress();

        Assert.assertNull(newEntity.getId());

        newEntity = sut.saveOrUpdate(newEntity);

        assertCreatedAddress(newEntity);

    }

    /**
     * AbstractDaoImpl DAO Method: findAddressesByAddressVO(AddressVO address).
     * 
     */
    @Test
    public void testFindAddressesByAddressVO() {
        AddressEntity newEntity = createAddress();

        Assert.assertNull(newEntity.getId());

        newEntity = sut.saveOrUpdate(newEntity);

        assertCreatedAddress(newEntity);

        AddressVO address = new AddressVO();
        address.setAddress1(ADDRESS1);
        address.setAddress2(ADDRESS2);
        address.setCity(CITY);
        address.setStateCode(STATE_CODE);
        address.setPostalCode(POSTAL_CODE);
        address.setCountryCode(COUNTRY_CODE);

        List<AddressEntity> existingAddresses = sut.findAddressesByAddressVO(address);

        Assert.assertNotNull(existingAddresses);
        Assert.assertEquals(1, existingAddresses.size());
        Assert.assertNotNull(existingAddresses.size());

        Assert.assertEquals(existingAddresses.get(0).getId(), newEntity.getId());
        Assert.assertEquals(existingAddresses.get(0), newEntity);
    }

    @Test
    public void testGetById() {
        AddressEntity address = sut.find(ID);
        Assert.assertNotNull(address);
        // check that all associations are present
        Assert.assertNotNull(address.getCountry());
        Assert.assertEquals("USA", address.getCountry().getId());

        Assert.assertNotNull(address.getState());
        Assert.assertEquals("CA", address.getState().getStatePK().getStateCode());
        Assert.assertEquals("USA", address.getState().getStatePK().getCountryCode());
    }

    @Test
    public void shouldFindRouteByAddresses() {
        long originAddressId = 23L;
        long destinationAddressId = 24L;

        RouteEntity route = sut.findRouteByAddresses(originAddressId, destinationAddressId);

        Assert.assertEquals(new Long(577), route.getId());
    }

    private void assertCreatedAddress(AddressEntity newEntity) {
        Assert.assertNotNull(newEntity);
        Assert.assertNotNull(newEntity.getId());

        flushAndClearSession();
        // Using findByprimaryKey DAO method to test the same method.
        AddressEntity afterSave = sut.find(newEntity.getId());
        Assert.assertNotNull(afterSave);

        Assert.assertEquals(newEntity, afterSave);
    }

    private AddressEntity createAddress() {
        AddressEntity entity = new AddressEntity();

        entity.setAddress1(ADDRESS1);
        entity.setAddress2(ADDRESS2);
        entity.setCity(CITY);
        entity.setStateCode(STATE_CODE);
        entity.setZip(POSTAL_CODE);
        CountryEntity country = new CountryEntity();
        country.setId(COUNTRY_CODE);
        entity.setCountry(country);
        entity.setStatus(Status.ACTIVE);

        return entity;
    }

}