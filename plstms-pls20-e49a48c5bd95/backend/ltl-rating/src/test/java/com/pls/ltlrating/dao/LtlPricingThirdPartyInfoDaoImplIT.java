package com.pls.ltlrating.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.impl.LtlPricingThirdPartyInfoDaoImpl;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;

/**
 * Test cases for {@link LtlPricingThirdPartyInfoDaoImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlPricingThirdPartyInfoDaoImplIT extends AbstractDaoTest {

    private static final Long FOR_SAVE_PRICE_PROFILE_ID = 4L;
    private static final Long FOR_RETUTN_PRICE_PROFILE_ID = 1L;
    private static final Long BLANKET_ID = 1L;
    private static final Long PROFILE_DETAIL_ID = 1L;
    private static final Long MODIFIED_BY = 1L;
    private static final Long EXPECTED_PROFILE_ID = 8L;
    private static final Long EXPECTED_PROFILE_DETAIL_ID = 9L;
    private static final Long USER_ID = 1L;

    @Autowired
    private LtlPricingThirdPartyInfoDao sut;

    @Autowired
    private LtlPricingProfileDao profileDao;

    @Test
    public void testFindByPrimaryKey() throws Exception {
        LtlPricingThirdPartyInfoEntity result = sut.find(1L);

        assertNotNull(result);
        assertSame(1L, result.getId());
        assertNotNull(result.getPhone());
        assertNotNull(result.getFax());
    }

    @Test
    public void testSaveAndUpdateEntity() throws Exception {
        LtlPricingThirdPartyInfoEntity newEntity = createMinimalEntity();
        PhoneEntity phone = createPhoneEntity(PhoneType.VOICE);
        newEntity.setPhone(phone);
        PhoneEntity fax = createPhoneEntity(PhoneType.FAX);
        newEntity.setFax(fax);
        newEntity = sut.saveOrUpdate(newEntity);

        assertNotNull(newEntity);
        assertNotNull(newEntity.getId());

        flushAndClearSession();

        LtlPricingThirdPartyInfoEntity afterSave = (LtlPricingThirdPartyInfoEntity) getSession().get(
                LtlPricingThirdPartyInfoEntity.class, newEntity.getId());

        assertNotNull(afterSave);
        assertEquals(newEntity.getId(), afterSave.getId());
        assertEquals(newEntity.getPricProfDetailId(), afterSave.getPricProfDetailId());
        assertEquals(newEntity.getStatus(), afterSave.getStatus());
        assertPhoneEntity(phone, newEntity.getPhone());
        assertPhoneEntity(fax, newEntity.getFax());
    }

    @Test
    public void testGetActiveThirdPartyInfo() throws Exception {
        LtlPricingThirdPartyInfoEntity result = sut.findActiveByProfileDetailId(FOR_RETUTN_PRICE_PROFILE_ID);

        assertNotNull(result);
    }

    @Test
    public void testFindAllCspChildsCopyedFrom() {
        List<LtlPricingThirdPartyInfoEntity> actualResult = sut.findAllCspChildsCopyedFrom(BLANKET_ID);

        Assert.assertNotNull(actualResult);
        Assert.assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testUpdateStatus() {
        LtlPricingThirdPartyInfoEntity existingEntity = sut.find(BLANKET_ID);
        Assert.assertNotNull(existingEntity);
        Assert.assertEquals(existingEntity.getPricProfDetailId(), PROFILE_DETAIL_ID);
        Assert.assertEquals(Status.ACTIVE, existingEntity.getStatus());

        sut.updateStatus(PROFILE_DETAIL_ID, Status.INACTIVE, MODIFIED_BY);
        flushAndClearSession();

        LtlPricingThirdPartyInfoEntity actualEntity = sut.find(BLANKET_ID);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(Status.INACTIVE, actualEntity.getStatus());
    }

    @Test
    public void testFindByProfileId() {
        LtlPricingThirdPartyInfoEntity actual = sut.findByProfileId(EXPECTED_PROFILE_ID);
        Assert.assertNotNull(actual);
        Assert.assertEquals(EXPECTED_PROFILE_DETAIL_ID, actual.getPricProfDetailId());
    }

    @Test
    public void testFindByProfileDetailId() {
        List<LtlPricingThirdPartyInfoEntity> actualList = sut.findByProfileDetailId(PROFILE_DETAIL_ID);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlPricingThirdPartyInfoEntity entity : actualList) {
            Assert.assertEquals(PROFILE_DETAIL_ID, entity.getPricProfDetailId());
        }
    }

    @Test
    public void testInactivateByProfileDetailId() {
        sut.inactivateCSPByProfileDetailId(PROFILE_DETAIL_ID, USER_ID);
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
        List<LtlPricingThirdPartyInfoEntity> list = sut.findByProfileDetailId(profileDetailId);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());

        for (LtlPricingThirdPartyInfoEntity entity : list) {
            Assert.assertEquals(Status.INACTIVE, entity.getStatus());
        }
    }

    private LtlPricingThirdPartyInfoEntity createMinimalEntity() {
        LtlPricingThirdPartyInfoEntity entity = new LtlPricingThirdPartyInfoEntity();

        entity.setPricProfDetailId(FOR_SAVE_PRICE_PROFILE_ID);
        entity.setStatus(Status.ACTIVE);

        entity.setAddress(createAddressEntity());

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

    private AddressEntity createAddressEntity() {
        AddressEntity entity = new AddressEntity();
        entity.setId(1L);
        entity.setAddress1("SOME TEST ADDRESS1");
        entity.setAddress2("SOME TEST ADDRESS2");
        entity.setCity("TEST CITY");
        entity.setZip("33556");

        CountryEntity country = new CountryEntity();
        country.setId("USA");
        entity.setCountry(country);

        StateEntity stateEntity = new StateEntity();
        StatePK statepk = new StatePK();
        statepk.setStateCode("FL");
        statepk.setCountryCode("USA");
        stateEntity.setStatePK(statepk);
        entity.setState(stateEntity);

        setModificationInfo(entity);
        return entity;
    }

    private void setModificationInfo(HasModificationInfo entity) {
        entity.getModification().setCreatedBy(-1L);
        entity.getModification().setCreatedDate(new Date());
        entity.getModification().setModifiedBy(-1L);
        entity.getModification().setModifiedDate(new Date());
    }

}
