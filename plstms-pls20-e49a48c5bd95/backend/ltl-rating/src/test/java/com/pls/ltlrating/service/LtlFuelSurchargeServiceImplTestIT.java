package com.pls.ltlrating.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.bo.SurchargeFileImportResult;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlFuelSurchargeDao;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;
import com.pls.ltlrating.service.impl.LtlFuelSurchargeServiceImpl;

/**
 * Test for {@link LtlFuelSurchargeServiceImpl}.
 *
 * @author Stas Norochevskiy
 *
 */
public class LtlFuelSurchargeServiceImplTestIT extends BaseServiceITClass {

    private final List<InputStream> openedFiles = new ArrayList<InputStream>();

    private static final String USERNAME = "SPARTAN1";

    private static final Long BLANKET_PROFILE_DETAIL_ID = 1L;

    private static final Long EXISTING_BLANKET_ENTITY_ID = 1L;

    private static final Long EXISTING_CSP_CHILD_ENTITY_ID = 133L;

    @Autowired
    private LtlFuelSurchargeService sut;

    @Autowired
    private LtlFuelSurchargeDao dao;

    @Before
    public void before() {
        SecurityTestUtils.login(USERNAME);
    }

    @After
    public void after() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testImportExcel() throws ImportException {
        Long profileDetailsId = 1L;

        List<LtlFuelSurchargeEntity> oldList =
                sut.getActiveFuelSurchargeByProfileDetailId(profileDetailsId);

        InputStream file = loadFile("ltl_fuel_surcharge.xlsx");
        List<LtlFuelSurchargeEntity> importedList = sut.importFuelSurchargeByProfileDetailIdFromFile(file, "xlsx", profileDetailsId,
                new SurchargeFileImportResult());

        List<LtlFuelSurchargeEntity> newList =
                sut.getActiveFuelSurchargeByProfileDetailId(profileDetailsId);

        newList.removeAll(oldList);

        Assert.assertEquals(importedList.size(), newList.size());
        Assert.assertEquals(1, importedList.size());
        Assert.assertNotNull(importedList.get(0).getId());
        Assert.assertNotNull(importedList.get(0).getLtlPricingProfileId());
    }

    @Test
    public void testSaveFuelSurchargeAddNewEntity() {
        LtlFuelSurchargeEntity newEntity = populateWithRandomValues(new LtlFuelSurchargeEntity());
        newEntity.setLtlPricingProfileId(BLANKET_PROFILE_DETAIL_ID);

        LtlFuelSurchargeEntity actualBlanketEntity = sut.saveFuelSurcharge(newEntity);
        Assert.assertNotNull(actualBlanketEntity);

        List<LtlFuelSurchargeEntity> actualCopyList = dao.findAllCspChildsCopyedFrom(actualBlanketEntity.getId());
        Assert.assertNotNull(actualCopyList);
        Assert.assertFalse(actualCopyList.isEmpty());

        LtlFuelSurchargeEntity actualCopiedEntity = actualCopyList.get(0);
        Assert.assertNotNull(actualCopiedEntity);
        Assert.assertEquals(actualBlanketEntity.getId(), actualCopiedEntity.getCopiedFrom());
    }

    @Test
    public void testSaveFuelSurchargeUpdateEntity() {
        LtlFuelSurchargeEntity existingEntity = dao.find(EXISTING_BLANKET_ENTITY_ID);
        Assert.assertNotNull(existingEntity);

        LtlFuelSurchargeEntity expectedEntity = populateWithRandomValues(existingEntity);
        LtlFuelSurchargeEntity actualEntity = sut.saveFuelSurcharge(expectedEntity);
        Assert.assertNotNull(actualEntity);

        LtlFuelSurchargeEntity actualChildEntity = dao.find(EXISTING_CSP_CHILD_ENTITY_ID);
        Assert.assertNotNull(actualChildEntity);
        assertEntity(actualEntity, actualChildEntity);
    }

    private InputStream loadFile(String string) {
        InputStream result = ClassLoader.getSystemResourceAsStream("ltl_fuel_surcharge" + File.separator + string);
        assertNotNull(result);
        openedFiles.add(result);
        return result;
    }

    private LtlFuelSurchargeEntity populateWithRandomValues(LtlFuelSurchargeEntity entity) {
        entity.setMaxRate(new BigDecimal(Math.random()));
        entity.setMinRate(new BigDecimal(Math.random()));
        entity.setStatus(Status.ACTIVE);
        entity.setSurcharge(new BigDecimal(Math.random()));

        return entity;
    }

    private void assertEntity(LtlFuelSurchargeEntity existing, LtlFuelSurchargeEntity actual) {
        Assert.assertEquals(existing.getMaxRate(), actual.getMaxRate());
        Assert.assertEquals(existing.getMinRate(), actual.getMinRate());
        Assert.assertEquals(existing.getStatus(), actual.getStatus());
        Assert.assertEquals(existing.getSurcharge(), actual.getSurcharge());
    }
}
