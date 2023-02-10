package com.pls.shipment.domain;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.enums.PointType;

/**
 * Test cases for {@link ManualBolEntity}.
 * 
 * @author Artem Arapov
 *
 */
public class ManualBolEntityTest {

    @Test
    public void shouldAddOneOrigin() {
        ManualBolEntity entity = new ManualBolEntity();

        ManualBolAddressEntity origin1 = new ManualBolAddressEntity(PointType.ORIGIN);
        ManualBolAddressEntity origin2 = new ManualBolAddressEntity(PointType.ORIGIN);

        entity.addAddress(origin1);
        entity.addAddress(origin2);

        Assert.assertNotNull(entity.getOrigin());
        Assert.assertNull(entity.getDestination());
        Assert.assertEquals(1L, entity.getAddresses().size());
    }

    @Test
    public void shouldAddOneDestination() {
        ManualBolEntity entity = new ManualBolEntity();

        ManualBolAddressEntity destination1 = new ManualBolAddressEntity(PointType.DESTINATION);
        ManualBolAddressEntity destination2 = new ManualBolAddressEntity(PointType.DESTINATION);

        entity.addAddress(destination1);
        entity.addAddress(destination2);

        Assert.assertNotNull(entity.getDestination());
        Assert.assertNull(entity.getOrigin());
        Assert.assertEquals(1L, entity.getAddresses().size());
    }

    @Test
    public void shouldAddValidOrigin() {
        ManualBolEntity entity = new ManualBolEntity();
        ManualBolAddressEntity origin = new ManualBolAddressEntity(PointType.ORIGIN);
        entity.addAddress(origin);

        Assert.assertEquals(1L, entity.getAddresses().size());
        Assert.assertSame(origin, entity.getOrigin());
        Assert.assertNull(entity.getDestination());
    }

    @Test
    public void shouldAddValidDestination() {
        ManualBolEntity entity = new ManualBolEntity();
        ManualBolAddressEntity destination = new ManualBolAddressEntity(PointType.DESTINATION);
        entity.addAddress(destination);

        Assert.assertEquals(1L, entity.getAddresses().size());
        Assert.assertSame(destination, entity.getDestination());
        Assert.assertNull(entity.getOrigin());
    }

    @Test
    public void shouldAvoidAddingWrongAddresses() {
        ManualBolEntity entity = new ManualBolEntity();
        ManualBolAddressEntity address = new ManualBolAddressEntity(null);
        entity.addAddress(address);

        Assert.assertEquals(1L, entity.getAddresses().size());
        Assert.assertNull(entity.getOrigin());
        Assert.assertNull(entity.getDestination());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldAvoidModificationOfAddressList() {
        ManualBolEntity entity = new ManualBolEntity();
        ManualBolAddressEntity origin = new ManualBolAddressEntity(PointType.ORIGIN);
        ManualBolAddressEntity destination = new ManualBolAddressEntity(PointType.DESTINATION);
        entity.addAddress(origin);
        entity.addAddress(destination);

        entity.getAddresses().clear();
    }
}
