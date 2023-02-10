
package com.pls.ltlrating.dao;

import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleStateException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.ltlrating.domain.DotRegionFuelEntity;

/**
 * Test for optimistic lock implemented in Hibernate.
 *
 * @author Stas Norochevskiy
 *
 */
@Transactional
@Rollback
@ContextConfiguration(locations = { "classpath*:spring/dao-*.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class OptimisticLockTestIT {

    @Autowired
    protected SessionFactory sessionFactory;

    private Long id = null;

    @BeforeClass
    public static void beforeClass() {
        SecurityTestUtils.login("sysadmin", "ROLE_PLS_USER");
    }

    @Before
    public void before() {
        Session session = sessionFactory.openSession();
        DotRegionFuelEntity dot = getFirstDotRegionFuelEntity(session);

        DotRegionFuelEntity dotCopy = new DotRegionFuelEntity();
        dotCopy.setDotRegion(dot.getDotRegion());
        dotCopy.setEffectiveDate(dot.getEffectiveDate());
        dotCopy.setExpirationDate(dot.getExpirationDate());
        dotCopy.setFuelCharge(dot.getFuelCharge());
        dotCopy.setStatus(dot.getStatus());

        session.save(dotCopy);
        id = dotCopy.getId();
        session.flush();
        session.close();
    }

    @After
    public void after() {
        Session session = sessionFactory.openSession();
        DotRegionFuelEntity dotCopy = (DotRegionFuelEntity) session.get(DotRegionFuelEntity.class, id);
        session.delete(dotCopy);
        session.flush();
        session.close();
    }

    @Test(expected = StaleStateException.class)
    public void testOptimisticBlocking() {

        Session session1 = sessionFactory.openSession();
        DotRegionFuelEntity dot1 = (DotRegionFuelEntity) session1.get(DotRegionFuelEntity.class, id);

        Session session2 = sessionFactory.openSession();
        DotRegionFuelEntity dot2 = (DotRegionFuelEntity) session2.get(DotRegionFuelEntity.class, id);
        dot2.setFuelCharge(new BigDecimal("1234"));
        session2.saveOrUpdate(dot2);
        session2.flush();
        session2.close();

        dot1.setFuelCharge(new BigDecimal("4321"));
        session1.saveOrUpdate(dot1);
        session1.flush();
        session1.close();
    }

    private DotRegionFuelEntity getFirstDotRegionFuelEntity(Session session) {
        return (DotRegionFuelEntity) session.createQuery("from DotRegionFuelEntity order by id").setMaxResults(1).uniqueResult();
    }
}
