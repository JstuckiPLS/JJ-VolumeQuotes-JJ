package com.pls.shipment.dao.model;

import java.util.Date;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;
import com.pls.shipment.domain.PackageTypeEntity;

/**
 * Test cases to check the mapping for {@link LtlProductEntity} class.
 * 
 * @author Maxim Medvedev
 */
public class LtlProductEntityMappingIT extends AbstractDaoTest {

    @Before
    public void tesUp() {
        SecurityTestUtils.logout();
    }
    @Test
    public void testPersistNewEntity() {
        LtlProductEntity entity = prepareMinimalEntity();

        getSession().save(entity);
        flushAndClearSession();

        LtlProductEntity dbEntity = (LtlProductEntity) getSession().get(LtlProductEntity.class, entity.getId());

        Assert.assertEquals(entity, dbEntity);

        Assert.assertEquals(dbEntity.getModification(), entity.getModification());

        Assert.assertEquals(Status.ACTIVE, dbEntity.getStatus());
    }

    @Test
    public void testUpdateCreatedDateField() {
        LtlProductEntity entity = prepareMinimalEntity();

        getSession().save(entity);
        flushAndClearSession();

        LtlProductEntity entityToModifyDate = (LtlProductEntity) getSession().get(LtlProductEntity.class,
                entity.getId());
        entityToModifyDate.getModification().setCreatedDate(new Date(10));
        getSession().saveOrUpdate(entityToModifyDate);
        flushAndClearSession();

        LtlProductEntity dbEntity = (LtlProductEntity) getSession().get(LtlProductEntity.class, entity.getId());
        Assert.assertEquals(entity, dbEntity);
        // CreateDate should not be modified
        Assert.assertEquals(dbEntity.getModification(), entity.getModification());

        Assert.assertEquals(Status.ACTIVE, dbEntity.getStatus());
    }

    @Test
    public void testWithCommodityClass() {
        LtlProductEntity entity = prepareMinimalEntity();
        entity.setCommodityClass(CommodityClass.CLASS_110);

        getSession().save(entity);
        flushAndClearSession();

        Query query = getSession().createSQLQuery(
                "SELECT LP.COMMODITY_CLASS_CODE FROM LTL_PRODUCT LP WHERE LP.LTL_PRODUCT_ID = :id");
        query.setParameter("id", entity.getId());

        Assert.assertEquals(1, query.list().size());
        Assert.assertEquals(CommodityClass.CLASS_110.getDbCode(), query.list().get(0));
    }

    @Test
    public void testWithHazmatFlag() {
        // See data in DataSet.xml
        LtlProductEntity entity = (LtlProductEntity) getSession().get(LtlProductEntity.class, 1L);

        Assert.assertNotNull(entity.getHazmatInfo());
        Assert.assertTrue(entity.isHazmat());

        Assert.assertEquals("Test", entity.getHazmatInfo().getUnNumber());
    }

    @Test
    public void testWithHazmatInfo() {
        LtlProductEntity entity = prepareMinimalEntity();
        entity.setHazmatInfo(new LtlProductHazmatInfo());
        entity.getHazmatInfo().setUnNumber("Test");

        getSession().save(entity);
        flushAndClearSession();

        Assert.assertNotNull(entity.getHazmatInfo());
        Assert.assertTrue(entity.isHazmat());

        Query query1 = getSession()
                .createSQLQuery("SELECT LP.UN_NUM FROM LTL_PRODUCT LP WHERE LP.LTL_PRODUCT_ID = :id");
        query1.setParameter("id", entity.getId());
        Assert.assertEquals(1, query1.list().size());
        Assert.assertEquals("Test", query1.list().get(0));

        Query query2 = getSession().createSQLQuery(
                "SELECT LP.HAZMAT_FLAG FROM LTL_PRODUCT LP WHERE LP.LTL_PRODUCT_ID = :id");
        query2.setParameter("id", entity.getId());
        Assert.assertEquals(1, query2.list().size());
        Assert.assertEquals('Y', query2.list().get(0));
    }

    @Test
    public void testWithoutCommodityClass() {
        LtlProductEntity entity = prepareMinimalEntity();
        entity.setCommodityClass(null);

        getSession().save(entity);
        flushAndClearSession();

        Query query = getSession().createSQLQuery(
                "SELECT LP.COMMODITY_CLASS_CODE FROM LTL_PRODUCT LP WHERE LP.LTL_PRODUCT_ID = :id");
        query.setParameter("id", entity.getId());

        Assert.assertEquals(1, query.list().size());
        Assert.assertNull(query.list().get(0));
    }

    @Test
    public void testWithoutHazmatFlag() {
        // See data in DataSet.xml
        LtlProductEntity entity = (LtlProductEntity) getSession().get(LtlProductEntity.class, 4L);

        Assert.assertNull(entity.getHazmatInfo());
        Assert.assertFalse(entity.isHazmat());
    }

    @Test
    public void testWithoutHazmatInfo() {
        LtlProductEntity entity = prepareMinimalEntity();

        getSession().save(entity);
        flushAndClearSession();

        Assert.assertNull(entity.getHazmatInfo());
        Assert.assertFalse(entity.isHazmat());

        Query query1 = getSession()
                .createSQLQuery("SELECT LP.UN_NUM FROM LTL_PRODUCT LP WHERE LP.LTL_PRODUCT_ID = :id");
        query1.setParameter("id", entity.getId());
        Assert.assertEquals(1, query1.list().size());
        Assert.assertEquals(null, query1.list().get(0));

        Query query2 = getSession().createSQLQuery(
                "SELECT LP.HAZMAT_FLAG FROM LTL_PRODUCT LP WHERE LP.LTL_PRODUCT_ID = :id");
        query2.setParameter("id", entity.getId());
        Assert.assertEquals(1, query2.list().size());
        Assert.assertEquals('N', query2.list().get(0));
    }

    @Test
    public void testWithoutPackageType() {
        LtlProductEntity entity = prepareMinimalEntity();

        getSession().save(entity);

        Query query = getSession().createQuery("SELECT lpe.packageType FROM LtlProductEntity lpe WHERE  lpe.id = :id");
        query.setParameter("id", entity.getId());

        Assert.assertTrue(query.list().isEmpty() || query.list().get(0) == null);
    }

    @Test
    public void testWithPackageType() {
        LtlProductEntity entity = prepareMinimalEntity();

        getSession().save(entity);
        PackageTypeEntity packageType = new PackageTypeEntity();
        packageType.setId("BDL");
        entity.setPackageType(packageType);
        getSession().saveOrUpdate(entity);

        Query query = getSession().createQuery("SELECT lpe.packageType FROM LtlProductEntity lpe WHERE  lpe.id = :id");
        query.setParameter("id", entity.getId());

        Assert.assertEquals(1, query.list().size());
        Assert.assertEquals("BDL", ((PackageTypeEntity) query.list().get(0)).getId());
    }

    private LtlProductEntity prepareMinimalEntity() {
        LtlProductEntity result = new LtlProductEntity();
        // There are set of mandatory fields
        result.setDescription("TestDescription");

        result.setTrackingId(-1L);
        result.setCustomerId(1L);
        result.setLocationId(1L);

        result.getModification().setCreatedBy(1L);
        result.getModification().setCreatedDate(new Date());
        result.getModification().setModifiedDate(new Date());
        return result;
    }
}
