package com.pls.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.emailhistory.dao.EmailHistoryDao;
import com.pls.emailhistory.domain.bo.EmailHistoryAttachmentBO;
import com.pls.emailhistory.domain.bo.EmailHistoryBO;
import com.pls.emailhistory.service.impl.EmailHistoryServiceImpl;

/**
 * Test cases for {@link EmailHistoryServiceImpl} class.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailHistoryServiceImplTest {

    private static final List<EmailHistoryBO> EMAIL_HISTORY = new ArrayList<EmailHistoryBO>();
    private static final List<EmailHistoryAttachmentBO> EMAIL_HISTORY_ATTACHMENT = new ArrayList<EmailHistoryAttachmentBO>();
    private static final Set<EmailType> EMAIL_TYPES = EnumSet.of(EmailType.DOCUMENT, EmailType.PEN_PAY, EmailType.INVOICE,
            EmailType.NOTIFICATION);

    private static final Long LOAD_ID = 10L;

    private static final Long USER_ID = (long) Math.random() * 100;

    @Mock
    private EmailHistoryDao emailHistoryDao;

    @Mock
    private UserPermissionsService userPermissionsService;

    @InjectMocks
    private EmailHistoryServiceImpl emailHistoryService;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", USER_ID);
    }

    @Test
    public void shouldReturnEmailHistoryList() {
        when(userPermissionsService.hasCapability(Capabilities.VIEW_INVOICES_EMAIL_HISTORY.name())).thenReturn(true);
        when(userPermissionsService.hasCapability(Capabilities.VIEW_NOTIFICATIONS_EMAIL_HISTORY.name())).thenReturn(true);
        when(userPermissionsService.hasCapability(Capabilities.VIEW_DOCUMENTS_EMAIL_HISTORY.name())).thenReturn(true);
        when(userPermissionsService.hasCapability(Capabilities.VIEW_PEN_PAY_EMAIL_HISTORY.name())).thenReturn(true);

        when(emailHistoryDao.getEmailHistoryByLoadIdAndTypes(LOAD_ID, EMAIL_TYPES)).thenReturn(EMAIL_HISTORY);
        when(emailHistoryDao.getAttachmentsByLoadIdAndTypes(LOAD_ID, EMAIL_TYPES)).thenReturn(EMAIL_HISTORY_ATTACHMENT);

        List<EmailHistoryBO> emailHistory = emailHistoryService.getEmailHistory(LOAD_ID);
        Assert.assertNotNull(emailHistory);

        verify(emailHistoryDao).getEmailHistoryByLoadIdAndTypes(LOAD_ID, EMAIL_TYPES);
        verify(emailHistoryDao).getAttachmentsByLoadIdAndTypes(LOAD_ID, EMAIL_TYPES);
        verify(userPermissionsService, times(1)).hasCapability(Capabilities.VIEW_INVOICES_EMAIL_HISTORY.name());
        verify(userPermissionsService, times(1)).hasCapability(Capabilities.VIEW_NOTIFICATIONS_EMAIL_HISTORY.name());
        verify(userPermissionsService, times(1)).hasCapability(Capabilities.VIEW_DOCUMENTS_EMAIL_HISTORY.name());
        verify(userPermissionsService, times(1)).hasCapability(Capabilities.VIEW_PEN_PAY_EMAIL_HISTORY.name());
    }
}
