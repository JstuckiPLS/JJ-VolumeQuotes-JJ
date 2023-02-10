package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingTerminalInfoDao;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;

/**
 * Integration test for {@link LtlPricingTerminalInfoService}.
 *
 * @author Artem Arapov
 *
 */
public class LtlPricingTerminalInfoServiceImplTestIT extends BaseServiceITClass {

    private static final String USERNAME = "sysadmin";

    private static final Long COPY_FROM_PROFILE_ID = 1L;

    private static final Long COPY_TO_PROFILE_ID = 2L;

    private static final Long TERMINAL_INFO_ID = 1L;

    @Before
    public void setUp() {
        SecurityTestUtils.login(USERNAME);
    }

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Autowired
    private LtlPricingTerminalInfoService sut;

    @Autowired
    private LtlPricingTerminalInfoDao dao;

    @Test
    public void testCopyFrom() {
        LtlPricingTerminalInfoEntity copyFromEntity = sut.getActiveCarrierTerminalInfoByProfileDetailId(COPY_FROM_PROFILE_ID);
        assertNotNull(copyFromEntity);

        sut.copyFrom(COPY_FROM_PROFILE_ID, COPY_TO_PROFILE_ID, false);
        flushAndClearSession();

        LtlPricingTerminalInfoEntity actualOldEntity = sut.getCarrierTerminalInfoById(COPY_TO_PROFILE_ID);

        assertEquals(Status.INACTIVE, actualOldEntity.getStatus());

        LtlPricingTerminalInfoEntity newEntity = sut.getActiveCarrierTerminalInfoByProfileDetailId(COPY_TO_PROFILE_ID);
        assertNotNull(newEntity);
        assertEquals(Status.ACTIVE, newEntity.getStatus());
    }

    @Test
    public void testSaveTerminalAddCSPEntity() throws Exception {
        LtlPricingTerminalInfoEntity expectedEntity = populateEntityWithRandomValues(new LtlPricingTerminalInfoEntity());
        expectedEntity = sut.saveCarrierTerminalInfo(expectedEntity);
        assertNotNull(expectedEntity);

        List<LtlPricingTerminalInfoEntity> actualList = dao.findAllCspChildsCopyedFrom(expectedEntity.getId());
        assertNotNull(actualList);
        assertFalse(actualList.isEmpty());

        LtlPricingTerminalInfoEntity actualCspEntity = actualList.get(0);
        assertNotNull(actualCspEntity);
        assertEquals(expectedEntity.getId(), actualCspEntity.getCopiedFrom());
        assertEntity(expectedEntity, actualCspEntity);
    }

    @Test
    public void testSaveTerminalUpdateCSPEntity() throws Exception {
        LtlPricingTerminalInfoEntity existingEntity = sut.getActiveCarrierTerminalInfoByProfileDetailId(TERMINAL_INFO_ID);
        assertNotNull(existingEntity);

        LtlPricingTerminalInfoEntity expectedEntity = populateEntityWithRandomValues(existingEntity);
        expectedEntity = sut.saveCarrierTerminalInfo(expectedEntity);
        assertNotNull(expectedEntity);

        List<LtlPricingTerminalInfoEntity> actualList = dao.findAllCspChildsCopyedFrom(expectedEntity.getId());
        assertNotNull(actualList);
        assertFalse(actualList.isEmpty());

        LtlPricingTerminalInfoEntity actualCspEntity = actualList.get(0);
        assertNotNull(actualCspEntity);
        assertEquals(expectedEntity.getId(), actualCspEntity.getCopiedFrom());
        assertEntity(expectedEntity, actualCspEntity);
    }

    private LtlPricingTerminalInfoEntity populateEntityWithRandomValues(LtlPricingTerminalInfoEntity entity) {
        entity.setAccountNum(String.valueOf(Math.random()));
        entity.setTerminal(String.valueOf(Math.random()));
        entity.setEmail(String.valueOf(Math.random()));
        entity.setTransiteTime((long) Math.random() * 100);
        entity.setContactName(String.valueOf(Math.random()));
        entity.setVisible(Boolean.TRUE);
        entity.setStatus(Status.ACTIVE);
        entity.setPriceProfileId(1L);

        return entity;
    }

    private void assertEntity(LtlPricingTerminalInfoEntity expected, LtlPricingTerminalInfoEntity actual) {
        assertEquals(expected.getAccountNum(), actual.getAccountNum());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getTerminal(), actual.getTerminal());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFax(), actual.getFax());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getContactName(), actual.getContactName());
        assertEquals(expected.getVisible(), actual.getVisible());
    }
}
