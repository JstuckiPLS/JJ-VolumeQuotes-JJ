package com.pls.shipment.domain;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;

/**
 * Test cases for {@link LoadEntity} class.
 * 
 * @author Maxim Medvedev
 */
public class LoadEntityTest {

    @Test
    public void testAddLoadDetailsWithDuplicatedDestination() {
        LoadEntity sut = new LoadEntity();
        LoadDetailsEntity dest1 = createDestination();
        LoadDetailsEntity dest2 = createDestination();

        sut.addLoadDetails(dest1);
        sut.addLoadDetails(dest2);

        Assert.assertEquals(1, sut.getLoadDetails().size());
        Assert.assertSame(dest2, sut.getLoadDetails().iterator().next());
        Assert.assertSame(dest2, sut.getDestination());
        Assert.assertNull(sut.getOrigin());
    }

    @Test
    public void testAddLoadDetailsWithDuplicatedOrigin() {
        LoadEntity sut = new LoadEntity();
        LoadDetailsEntity origin1 = createOrigin();
        LoadDetailsEntity origin2 = createOrigin();

        sut.addLoadDetails(origin1);
        sut.addLoadDetails(origin2);

        Assert.assertEquals(1, sut.getLoadDetails().size());
        Assert.assertSame(origin2, sut.getLoadDetails().iterator().next());
        Assert.assertSame(origin2, sut.getOrigin());
        Assert.assertNull(sut.getDestination());
    }

    @Test
    public void testAddLoadDetailsWithNull() {
        LoadEntity sut = new LoadEntity();

        sut.addLoadDetails(null);

        Assert.assertEquals(0, sut.getLoadDetails().size());
    }

    @Test
    public void testAddLoadDetailsWithUnexpectedLoadDetails() {
        LoadEntity sut = new LoadEntity();

        sut.addLoadDetails(createInvalidLoadDetails(null, null));

        Assert.assertEquals(1, sut.getLoadDetails().size());
        Assert.assertNull(sut.getOrigin());
        Assert.assertNull(sut.getDestination());
    }

    @Test
    public void testAddLoadDetailsWithValidDestination() {
        LoadEntity sut = new LoadEntity();
        LoadDetailsEntity dest = createDestination();

        sut.addLoadDetails(dest);

        Assert.assertEquals(1, sut.getLoadDetails().size());
        Assert.assertSame(dest, sut.getLoadDetails().iterator().next());
        Assert.assertSame(dest, sut.getDestination());
        Assert.assertNull(sut.getOrigin());
    }

    @Test
    public void testAddLoadDetailsWithValidOrigin() {
        LoadEntity sut = new LoadEntity();
        LoadDetailsEntity origin = createOrigin();

        sut.addLoadDetails(origin);

        Assert.assertEquals(1, sut.getLoadDetails().size());
        Assert.assertSame(origin, sut.getLoadDetails().iterator().next());
        Assert.assertSame(origin, sut.getOrigin());
        Assert.assertNull(sut.getDestination());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetLoadDetails() {
        LoadEntity sut = new LoadEntity();

        sut.getLoadDetails().clear();
    }

    private LoadDetailsEntity createDestination() {
        return createInvalidLoadDetails(LoadAction.DELIVERY, PointType.DESTINATION);
    }

    private LoadDetailsEntity createInvalidLoadDetails(LoadAction action, PointType point) {
        LoadDetailsEntity result = new LoadDetailsEntity(action, point);
        result.setId(System.nanoTime());
        return result;
    }

    private LoadDetailsEntity createOrigin() {
        return createInvalidLoadDetails(LoadAction.PICKUP, PointType.ORIGIN);
    }

}
