package com.pls.user.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.user.dao.AnnouncementDao;
import com.pls.user.domain.bo.AnnouncementBO;
import com.pls.user.domain.enums.AnnouncementStatus;

/**
 * Test cases for {@link AnnouncementDaoImpl} class.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class AnnouncementDaoImplIT extends AbstractDaoTest {

    @Autowired
    private AnnouncementDao dao;

    @Before
    public void setUp() {
        executeScript("addAnnouncements.sql");
    }

    @Test
    public void testGetAllUnpublishedAnnouncements() {
        List<AnnouncementBO> result = dao.getAllUnpublished();

        Assert.assertNotNull(result);
        Assert.assertEquals("Number of unpublished announcements returned", 2, result.size());
    }

    @Test
    public void testGetAllPublishedAnnouncements() {
        List<AnnouncementBO> result = dao.getAll(
                new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L),
                new Date(), Arrays.asList(AnnouncementStatus.PUBLISHED));

        Assert.assertNotNull(result);
        Assert.assertEquals("Number of published announcements returned", 1, result.size());
    }

    @Test
    public void testCopyAnnouncement() throws EntityNotFoundException {
        dao.copy(3L);
        Assert.assertEquals("Number of announcements returned", 3, dao.getAllUnpublished().size());
    }

    @Test
    public void testGetUnreadAnnouncementsCount() {
        Long count = dao.getUnreadAnnouncementsCount();
        Assert.assertEquals("Number of unread announcements announcements", 1, count.longValue());
    }

    @Test
    public void testCancel() {
        dao.cancel(3L);
        List<AnnouncementBO> result = dao.getAll(
                new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L),
                new Date(), Arrays.asList(AnnouncementStatus.PUBLISHED));

        Assert.assertTrue(result.isEmpty());
        Assert.assertEquals("Number of published announcements", 0, result.size());
    }

    @Test
    public void testDelete() {
        dao.delete(1L);
        Assert.assertEquals("Number of announcements returned", 1,  dao.getAllUnpublished().size());
    }
}
