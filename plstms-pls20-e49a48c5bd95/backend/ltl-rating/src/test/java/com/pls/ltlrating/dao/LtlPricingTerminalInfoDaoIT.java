package com.pls.ltlrating.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;

/**
 * Test cases for {@link LtlPricingTerminalInfoDao}. Use test data from LTL_PRICING_TERMINAL_INFO.XML
 *
 * @author Artem Arapov
 *
 */
public class LtlPricingTerminalInfoDaoIT extends AbstractDaoTest {

    private static final Long FOR_SAVE_PRICE_PROFILE_ID = 4L;
    private static final Long FOR_RETUTN_PRICE_PROFILE_ID = 1L;
    private static final Long BLANKET_ID = 1L;
    private static final Long MODIFIED_BY = 1L;
    private static final Long PROFILE_DETAIL_ID = 1L;

    @Autowired
    private LtlPricingTerminalInfoDao sut;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Test
    public void testFindByPrimaryKey() throws Exception {
        LtlPricingTerminalInfoEntity result = sut.find(1L);

        assertNotNull(result);
        assertSame(1L, result.getId());
        assertNotNull(result.getPhone());
        assertNotNull(result.getFax());
        assertTrue(result.getVisible());
    }

    @Test
    public void testSaveAndUpdateEntity() throws Exception {
        LtlPricingTerminalInfoEntity newEntity = createMinimalEntity();
        PhoneEntity phone = createPhoneEntity(PhoneType.VOICE);
        newEntity.setPhone(phone);
        PhoneEntity fax = createPhoneEntity(PhoneType.FAX);
        newEntity.setFax(fax);
        newEntity = sut.saveOrUpdate(newEntity);

        assertNotNull(newEntity);
        assertNotNull(newEntity.getId());

        flushAndClearSession();

        LtlPricingTerminalInfoEntity afterSave = (LtlPricingTerminalInfoEntity) getSession().get(
                LtlPricingTerminalInfoEntity.class, newEntity.getId());

        assertNotNull(afterSave);
        assertEquals(newEntity.getId(), afterSave.getId());
        assertEquals(newEntity.getPriceProfileId(), afterSave.getPriceProfileId());
        assertEquals(newEntity.getStatus(), afterSave.getStatus());
        assertPhoneEntity(phone, newEntity.getPhone());
        assertPhoneEntity(fax, newEntity.getFax());
    }

    @Test
    public void testGetActiveTerminalInfo() throws Exception {
        LtlPricingTerminalInfoEntity result = sut.findActiveByProfileDetailId(FOR_RETUTN_PRICE_PROFILE_ID);

        assertNotNull(result);
    }

    @Test
    public void testFindAllCspChildsCopyedFrom() {
        List<LtlPricingTerminalInfoEntity> actualResult = sut.findAllCspChildsCopyedFrom(BLANKET_ID);

        Assert.assertNotNull(actualResult);
        Assert.assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testUpdateStatus() {
        LtlPricingTerminalInfoEntity existingEntity = sut.find(BLANKET_ID);
        Assert.assertNotNull(existingEntity);
        Assert.assertEquals(Status.ACTIVE, existingEntity.getStatus());

        sut.updateStatus(BLANKET_ID, Status.INACTIVE, MODIFIED_BY);
        flushAndClearSession();

        LtlPricingTerminalInfoEntity actualEntity = sut.find(BLANKET_ID);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(Status.INACTIVE, actualEntity.getStatus());
    }

    @Test
    public void testFindByProfileDetailId() {
        List<LtlPricingTerminalInfoEntity> actualList = sut.findByProfileDetailId(PROFILE_DETAIL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlPricingTerminalInfoEntity entity : actualList) {
            Assert.assertNotNull(entity);
            Assert.assertEquals(PROFILE_DETAIL_ID, entity.getPriceProfileId());
        }
    }

    @Test
    public void testInactivateByProfileDetailId() {
        sut.inactivateCSPByProfileDetailId(PROFILE_DETAIL_ID, MODIFIED_BY);
        flushAndClearSession();

        assertChildCSPEntitiesStatusByProfileDetailId(PROFILE_DETAIL_ID);
    }

    private void assertChildCSPEntitiesStatusByProfileDetailId(Long parentDetailId) {
        List<BigInteger> childCSPDetailIdsList = profileDao.findChildCSPDetailByParentDetailId(parentDetailId);
        Assert.assertNotNull(childCSPDetailIdsList);
        Assert.assertFalse(childCSPDetailIdsList.isEmpty());

        for (BigInteger childDetailId : childCSPDetailIdsList) {
            assertChildCSPEntityStatusByProfileDetailId(childDetailId.longValue());
        }
    }

    private void assertChildCSPEntityStatusByProfileDetailId(Long profileDetailId) {
        List<LtlPricingTerminalInfoEntity> list = sut.findByProfileDetailId(profileDetailId);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());

        for (LtlPricingTerminalInfoEntity entity : list) {
            Assert.assertEquals(Status.INACTIVE, entity.getStatus());
        }
    }

    private LtlPricingTerminalInfoEntity createMinimalEntity() {
        LtlPricingTerminalInfoEntity entity = new LtlPricingTerminalInfoEntity();

        entity.setPriceProfileId(FOR_SAVE_PRICE_PROFILE_ID);
        entity.setVisible(Boolean.TRUE);
        entity.setStatus(Status.ACTIVE);

        return entity;
    }

    private PhoneEntity createPhoneEntity(PhoneType phoneType) {
        PhoneEntity entity = new PhoneEntity();

        entity.setAreaCode("067");
        entity.setCountryCode("038");
        entity.setType(phoneType);
        entity.setNumber("1234567");

        return entity;
    }

    private void assertPhoneEntity(final PhoneEntity expected, final PhoneEntity actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
