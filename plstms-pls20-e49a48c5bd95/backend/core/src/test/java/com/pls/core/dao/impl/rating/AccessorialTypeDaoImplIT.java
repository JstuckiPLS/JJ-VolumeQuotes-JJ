package com.pls.core.dao.impl.rating;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.rating.AccessorialTypeDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.enums.ApplicableToUnit;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.shared.Status;

/**
 * Test cases for {@link AccessorialTypeDaoImpl} class.
 * 
 * @author Artem Arapov
 * 
 */
public class AccessorialTypeDaoImplIT extends AbstractDaoTest {

    private static final Long CURRENT_USER = 1L;

    @Autowired
    private AccessorialTypeDao sut;

    @Test
    public void testFindAccessorialTypesByStatus() {
        Status expectedStatus = Status.ACTIVE;

        List<AccessorialTypeEntity> actualList = sut.findAccessorialTypesByStatus(expectedStatus);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }

    @Test
    public void testUpdateStatus() {
        Status expectedStatus = Status.INACTIVE;
        String[] ids = { "HCD", "NC", "ECA" };
        List<String> expectedCodes = Arrays.asList(ids);

        sut.updateStatus(expectedCodes, expectedStatus, CURRENT_USER);
        flushAndClearSession();

        for (String code : expectedCodes) {
            AccessorialTypeEntity actualEntity = sut.find(code);

            Assert.assertNotNull(actualEntity);
            Assert.assertEquals(expectedStatus, actualEntity.getStatus());
        }
    }

    @Test
    public void shouldCheckAccessorialTypeCodeExistence() {
        boolean exists = sut.checkAccessorialCodeExists("some definitely non existent code");
        assertFalse(exists);

        List<AccessorialTypeEntity> accessorialTypes = sut.getAll();
        assertFalse(accessorialTypes.isEmpty());

        exists = sut.checkAccessorialCodeExists(accessorialTypes.iterator().next().getId());
        assertTrue(exists);
    }

    @Test
    public void shouldListPickupAccessorialTypes() {
        List<AccessorialTypeEntity> accessorialTypes = sut.listPickupAccessorialTypes(LtlAccessorialGroup.PICKUP.name());
        assertFalse(accessorialTypes.isEmpty());

        Iterator<AccessorialTypeEntity> iterator = accessorialTypes.iterator();
        while (iterator.hasNext()) {
            AccessorialTypeEntity accessorialType = iterator.next();
            assertTrue(accessorialType.getAccessorialGroup() == LtlAccessorialGroup.PICKUP);
            assertTrue(accessorialType.getStatus() == Status.ACTIVE);
            assertTrue(accessorialType.getApplicableTo() == ApplicableToUnit.LTL);
        }
    }

    @Test
    public void shouldListDeliveryAccessorialTypes() {
        List<AccessorialTypeEntity> accessorialTypes = sut.listPickupAccessorialTypes(LtlAccessorialGroup.DELIVERY.name());
        assertFalse(accessorialTypes.isEmpty());

        for (AccessorialTypeEntity accessorialType: accessorialTypes) {
            assertTrue(accessorialType.getAccessorialGroup() == LtlAccessorialGroup.DELIVERY);
            assertTrue(accessorialType.getStatus() == Status.ACTIVE);
            assertTrue(accessorialType.getApplicableTo() == ApplicableToUnit.LTL);
        }
    }

    @Test
    public void shouldGetAllApplicableToAccessorialTypes() {
        List<AccessorialTypeEntity> accessorialTypes = sut.getAllApplicableAccessorialTypes();
        assertFalse(accessorialTypes.isEmpty());

        for (AccessorialTypeEntity accessorialType: accessorialTypes) {
            assertTrue(accessorialType.getStatus() == Status.ACTIVE);
            /**
             * Issue #6055: Benjamin Shniper: This screen should now return PLS types.
             * Removed assertion that types are not PLS from this and
             * shouldGetPickupAndDeliveryAccessorialTypes
             */
        }
    }

    @Test
    public void shouldGetPickupAndDeliveryAccessorialTypes() {
        List<AccessorialTypeEntity> accessorialTypes = sut.getAll();
        assertFalse(accessorialTypes.isEmpty());

        Set<String> accessorialTypeIds = new HashSet<String>();
        for (AccessorialTypeEntity accessorialType: accessorialTypes) {
            accessorialTypeIds.add(accessorialType.getId());
        }

        accessorialTypes = sut.getPickupAndDeliveryAccessorials(accessorialTypeIds);
        assertFalse(accessorialTypes.isEmpty());
        for (AccessorialTypeEntity accessorialType: accessorialTypes) {
            assertTrue(accessorialType.getStatus() == Status.ACTIVE);
            assertTrue(accessorialType.getAccessorialGroup() == LtlAccessorialGroup.PICKUP
                    || accessorialType.getAccessorialGroup() == LtlAccessorialGroup.DELIVERY);
            assertTrue(accessorialTypeIds.contains(accessorialType.getId()));
        }
    }

    @Test
    public void shouldCheckAccessorialTypeCodeUniqueness() {
        boolean exists = sut.isAccessorialTypeUnique("definitely not existing code");
        assertTrue(exists);
    }

    @Test
    public void testGetLtlAccessorialTypes() {
        List<AccessorialTypeEntity> accTypeEntities = sut.getLtlAccessorialTypes();
        Assert.assertNotNull(accTypeEntities);
        Assert.assertFalse(accTypeEntities.isEmpty());
    }
}
