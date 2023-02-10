package com.pls.ltlrating.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.ltlrating.dao.impl.LtlPricingNotesDaoImpl;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;

/**
 * Test cases for {@link LtlPricingNotesDaoImpl}.
 *
 * @author Artem Arapov
 */
public class LtlPricingNotesDaoImplIT extends AbstractDaoTest {

    private static final Long PRICING_NOTE_ID = 1L;

    private static final Long PRICE_PROFILE_ID = 1L;

    private static final Long CREATED_USER_ID = 1L;

    @Autowired
    private LtlPricingNotesDao sut;

    @Test
    public void testFindByPrimaryKey() throws Exception {
        LtlPricingNotesEntity actualEntity = sut.find(PRICING_NOTE_ID);

        assertNotNull(actualEntity);
        assertEquals(PRICING_NOTE_ID, actualEntity.getId());
    }

    @Test
    public void testSaveNotes() throws Exception {
        LtlPricingNotesEntity entity = createEntity();

        LtlPricingNotesEntity afterSave = sut.saveOrUpdate(entity);

        assertNotNull(afterSave);
        assertNotNull(afterSave.getId());

        flushAndClearSession();

        LtlPricingNotesEntity actualEntity = sut.find(afterSave.getId());
        assertNotNull(actualEntity);
    }

    @Test
    public void testFindByProfileId() throws Exception {
        List<LtlPricingNotesEntity> results = sut.findByProfileId(PRICE_PROFILE_ID);

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    private LtlPricingNotesEntity createEntity() {
        LtlPricingNotesEntity entity = new LtlPricingNotesEntity();
        entity.setPricingProfileId(PRICE_PROFILE_ID);
        entity.setNotes("Some Long length text: " + Math.random());
        entity.setCreatedBy(CREATED_USER_ID);
        entity.setCreatedDate(new Date());

        return entity;
    }
}
