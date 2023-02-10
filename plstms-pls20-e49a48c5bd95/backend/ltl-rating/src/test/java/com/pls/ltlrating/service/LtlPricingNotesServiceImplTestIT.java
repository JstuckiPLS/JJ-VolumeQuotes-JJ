package com.pls.ltlrating.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;
import com.pls.ltlrating.service.impl.LtlPricingNotesServiceImpl;

/**
 * Integration tests for {@link LtlPricingNotesServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlPricingNotesServiceImplTestIT extends BaseServiceITClass {

    private static final Long BLANKED_PROFILE_ID = 1L;

    private static final Long BLANKET_CSP_PROFILE_ID = 8L;

    @Autowired
    private LtlPricingNotesService sut;

    @Before
    public void setUp() {
        SecurityTestUtils.login("sysadmin");
    }

    @Test
    public void testSaveNotes() throws Exception {
        LtlPricingNotesEntity expectedEntity = createRandomEmtity();
        expectedEntity.setPricingProfileId(BLANKED_PROFILE_ID);

        sut.saveNotes(expectedEntity);
        Assert.assertNotNull(expectedEntity);

        List<LtlPricingNotesEntity> blanketProfileList = sut.getNotesByProfileId(BLANKED_PROFILE_ID);
        Assert.assertNotNull(blanketProfileList);
        Assert.assertFalse(blanketProfileList.isEmpty());

        List<LtlPricingNotesEntity> blanketCSPProfileList = sut.getNotesByProfileId(BLANKET_CSP_PROFILE_ID);
        Assert.assertNotNull(blanketCSPProfileList);
        Assert.assertFalse(blanketCSPProfileList.isEmpty());
        Assert.assertEquals(blanketProfileList.size(), blanketCSPProfileList.size());
        for (LtlPricingNotesEntity expected: blanketProfileList) {
            Assert.assertTrue(blanketCSPProfileList.contains(expected));
        }
    }

    private LtlPricingNotesEntity createRandomEmtity() {
        LtlPricingNotesEntity entity = new LtlPricingNotesEntity();
        entity.setNotes(String.valueOf(Math.random()));

        return entity;
    }
}
