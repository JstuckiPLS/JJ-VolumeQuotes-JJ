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
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.AddressVO;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingThirdPartyInfoDao;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;
import com.pls.ltlrating.service.impl.LtlPricingThirdPartyInfoServiceImpl;

/**
 * Integration test for {@link LtlPricingThirdPartyInfoServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlPricingThirdPartyInfoServiceImplTestIT extends BaseServiceITClass {

    private static final String USERNAME = "sysadmin";

    private static final Long COPY_FROM_PROFILE_ID = 1L;

    private static final Long COPY_TO_PROFILE_ID = 2L;

    private static final Long THIRD_PARTY_INFO_ID = 1L;

    private static final AddressVO ADDRESSVO = createTestAddress();

    private static final String UPDATED_COMPANY_NAME = "Some company";

    @Before
    public void setUp() {
        SecurityTestUtils.login(USERNAME);
    }

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Autowired
    private LtlPricingThirdPartyInfoService sut;

    @Autowired
    private LtlPricingThirdPartyInfoDao dao;

    @Autowired
    private AddressService addressService;

    @Test
    public void testCopyFrom() {
        LtlPricingThirdPartyInfoEntity copyFromEntity = sut.getActiveThirdPartyInfoByProfileDetailId(COPY_FROM_PROFILE_ID);
        assertNotNull(copyFromEntity);

        sut.copyFrom(COPY_FROM_PROFILE_ID, COPY_TO_PROFILE_ID, false);

        LtlPricingThirdPartyInfoEntity oldEntity = sut.getThirdPartyInfoById(COPY_TO_PROFILE_ID);
        assertNotNull(oldEntity);
        assertEquals(Status.INACTIVE, oldEntity.getStatus());

        LtlPricingThirdPartyInfoEntity newEntity = sut.getActiveThirdPartyInfoByProfileDetailId(COPY_TO_PROFILE_ID);
        assertNotNull(newEntity);
        assertEquals(Status.ACTIVE, newEntity.getStatus());
    }

    @Test
    public void testSaveThirdPartyInfoWithNewAddress() throws ValidationException {
        LtlPricingThirdPartyInfoEntity entity = sut.getThirdPartyInfoById(THIRD_PARTY_INFO_ID);

        entity.setCompany(UPDATED_COMPANY_NAME);

        sut.saveThirdPartyInfo(entity);
        assertNotNull(entity);
        assertEquals(entity.getCompany(), UPDATED_COMPANY_NAME);
    }

    @Test
    public void testSaveThirdPartyInfoWithUpdatedAddress() throws ValidationException {
        LtlPricingThirdPartyInfoEntity entity = sut.getThirdPartyInfoById(THIRD_PARTY_INFO_ID);

        entity.getAddress().setAddress1(ADDRESSVO.getAddress1());

        sut.saveThirdPartyInfo(entity);
        assertNotNull(entity);
        assertEquals(entity.getAddress().getAddress1(), ADDRESSVO.getAddress1());
    }

    @Test
    public void testSaveThirdPartyInfoAddCSPEntity() throws Exception {
        LtlPricingThirdPartyInfoEntity expectedEntity = populateEntityWithRandomValues(new LtlPricingThirdPartyInfoEntity());
        expectedEntity = sut.saveThirdPartyInfo(expectedEntity);
        assertNotNull(expectedEntity);

        List<LtlPricingThirdPartyInfoEntity> actualList = dao.findAllCspChildsCopyedFrom(expectedEntity.getId());
        assertNotNull(actualList);
        assertFalse(actualList.isEmpty());

        LtlPricingThirdPartyInfoEntity actualCspEntity = actualList.get(0);
        assertNotNull(actualCspEntity);
        assertEquals(expectedEntity.getId(), actualCspEntity.getCopiedFrom());
        assertEntity(expectedEntity, actualCspEntity);
    }

    @Test
    public void testSaveThirdPartyInfoUpdateCSPEntity() throws Exception {
        LtlPricingThirdPartyInfoEntity existingEntity = sut.getThirdPartyInfoById(THIRD_PARTY_INFO_ID);
        assertNotNull(existingEntity);

        LtlPricingThirdPartyInfoEntity expectedEntity = populateEntityWithRandomValues(existingEntity);
        expectedEntity = sut.saveThirdPartyInfo(expectedEntity);
        assertNotNull(expectedEntity);

        List<LtlPricingThirdPartyInfoEntity> actualList = dao.findAllCspChildsCopyedFrom(expectedEntity.getId());
        assertNotNull(actualList);
        assertFalse(actualList.isEmpty());

        LtlPricingThirdPartyInfoEntity actualCspEntity = actualList.get(0);
        assertNotNull(actualCspEntity);
        assertEquals(expectedEntity.getId(), actualCspEntity.getCopiedFrom());
        assertEntity(expectedEntity, actualCspEntity);
    }

    private LtlPricingThirdPartyInfoEntity populateEntityWithRandomValues(LtlPricingThirdPartyInfoEntity entity) {
        entity.setAccountNum(String.valueOf(Math.random()));
        entity.setCompany(String.valueOf(Math.random()));
        entity.setEmail(String.valueOf(Math.random()));
        entity.setStatus(Status.ACTIVE);
        entity.setPricProfDetailId(1L);

        entity.setAddress(createTestAddressEntity());
        return entity;
    }

    private void assertEntity(LtlPricingThirdPartyInfoEntity expected, LtlPricingThirdPartyInfoEntity actual) {
        assertEquals(expected.getAccountNum(), actual.getAccountNum());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getCompany(), actual.getCompany());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFax(), actual.getFax());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    private static AddressVO createTestAddress() {
        AddressVO address = new AddressVO();

        address.setAddress1("SOME TEST ADDRESS1");
        address.setAddress2("SOME TEST ADDRESS2");
        address.setCity("ODESSA");
        address.setCountryCode("USA");
        address.setPostalCode("33556");
        address.setStateCode("FL");

        return address;
    }

    private static AddressEntity createTestAddressEntity() {
        AddressEntity address = new AddressEntity();

        address.setAddress1("SOME TEST ADDRESS1");
        address.setAddress2("SOME TEST ADDRESS2");
        address.setCity("TEST CITY");
        address.setZip("33556");
        address.setStateCode("FL");

        CountryEntity country = new CountryEntity();
        country.setId("USA");
        address.setCountry(country);

        return address;
    }
}
