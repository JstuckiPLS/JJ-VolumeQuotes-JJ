package com.pls.core.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.NotificationTypesDao;
import com.pls.core.domain.NotificationTypeEntity;

/**
 * Very basic test cases for {@link NotificationTypesDaoImpl} class. Just checks that it
 * works.
 *
 * @author Alexander Kirichenko
 */
public class NotificationTypesDaoImplIT extends AbstractDaoTest {
    @Autowired
    private NotificationTypesDao sut;

    @Test
    public void testGetAll() {
        List<NotificationTypeEntity> all = sut.getAll();
        assertNotNull(all);
        assertEquals(all.size(), 5);
    }
}
