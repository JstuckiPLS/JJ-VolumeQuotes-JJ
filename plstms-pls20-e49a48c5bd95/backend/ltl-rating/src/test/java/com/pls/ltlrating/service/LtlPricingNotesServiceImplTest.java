package com.pls.ltlrating.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.support.Validator;
import com.pls.ltlrating.dao.LtlPricingNotesDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;
import com.pls.ltlrating.service.impl.LtlPricingNotesServiceImpl;

/**
 * Test cases for {@link LtlPricingNotesServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlPricingNotesServiceImplTest {

    private static final Long NOTE_ID = 1L;

    private static final Long USER_ID = 1L;

    private static final Long PROFILE_ID = 1L;

    private static final String BLANKET = "BLANKET";

    private LtlPricingNotesEntity entity;

    @InjectMocks
    private LtlPricingNotesServiceImpl sut;

    @Mock
    private LtlPricingNotesDao dao;

    @Mock
    private LtlPricingProfileDao profileDao;

    @Mock
    private Validator<LtlPricingNotesEntity> validator;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", USER_ID);

        entity = createEntity();
    }

    @Test
    public void testSaveNotes() throws Exception {
        when(dao.find(NOTE_ID)).thenReturn(entity);
        when(profileDao.findPricingTypeByProfileId(PROFILE_ID)).thenReturn(BLANKET);

        sut.saveNotes(entity);

        verify(dao).saveOrUpdate(entity);
    }

    @Test
    public void testGetNotesById() throws Exception {
        when(dao.find(NOTE_ID)).thenReturn(entity);

        LtlPricingNotesEntity result = sut.getNotesById(NOTE_ID);

        verify(dao).find(NOTE_ID);
        assertNotNull(result);
    }

    @Test
    public void testGetNotesByProfileId() throws Exception {
        List<LtlPricingNotesEntity> expectedList = new ArrayList<LtlPricingNotesEntity>();
        expectedList.add(createEntity());
        expectedList.add(createEntity());

        when(dao.findByProfileId(PROFILE_ID)).thenReturn(expectedList);

        List<LtlPricingNotesEntity> actualList = sut.getNotesByProfileId(PROFILE_ID);

        verify(dao).findByProfileId(PROFILE_ID);
        assertNotNull(actualList);
        assertFalse(actualList.isEmpty());
    }

    private LtlPricingNotesEntity createEntity() {
        LtlPricingNotesEntity obj = new LtlPricingNotesEntity();

        obj.setId(NOTE_ID);
        obj.setPricingProfileId(PROFILE_ID);
        obj.setNotes("Some long length text " + Math.random());

        return obj;
    }
}
