package com.pls.emailhistory.dao;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.emailhistory.dao.impl.EmailHistoryDaoImpl;
import com.pls.emailhistory.domain.EmailHistoryEntity;
import com.pls.emailhistory.domain.bo.EmailHistoryAttachmentBO;
import com.pls.emailhistory.domain.bo.EmailHistoryBO;

/**
 * Test cases for {@link EmailHistoryDaoImpl} class.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
public class EmailHistoryDaoImplIT extends AbstractDaoTest {

    private static final Long LOAD_ID = 10L;

    @Autowired
    private EmailHistoryDao sut;

    @Test
    public void shouldFindUserInfo() {
        List<EmailHistoryBO> emails = sut.getEmailHistoryByLoadIdAndTypes(LOAD_ID,
                EnumSet.of(EmailType.DOCUMENT, EmailType.INVOICE, EmailType.NOTIFICATION));
        Assert.assertEquals(3, emails.size());

        EmailHistoryBO bo = emails.stream().filter(item -> item.getId() == 1).findFirst().orElse(null);
        Assert.assertNotNull(bo);
        Assert.assertEquals(EmailType.DOCUMENT.getName(), bo.getEmailType().getName());
        Assert.assertEquals(NotificationTypeEnum.DISPATCHED, bo.getNotificationType());
        Assert.assertEquals("admin sysadmin", bo.getSendBy());
        Assert.assertEquals("test1@example.com", bo.getSendTo());
        Assert.assertEquals("test subject 1", bo.getSubject());
        Assert.assertEquals("test text 1", bo.getText());
    }

    @Test
    public void shouldFindUserInfo2() {
        List<EmailHistoryAttachmentBO> attachments = sut.getAttachmentsByLoadIdAndTypes(LOAD_ID,
                EnumSet.of(EmailType.DOCUMENT, EmailType.INVOICE, EmailType.NOTIFICATION));
        Assert.assertEquals(4, attachments.size());
    }

    @Test
    public void shouldSaveEmailHistoryEntity() {
        EmailHistoryEntity email = createEmailHistory();
        EmailHistoryEntity savedEmail = sut.saveOrUpdate(email);

        Assert.assertNotNull(savedEmail);
        Assert.assertEquals(savedEmail.getEmailType(), email.getEmailType());
        Assert.assertEquals(savedEmail.getSendTo(), email.getSendTo());
        Assert.assertEquals(savedEmail.getSubject(), email.getSubject());
        Assert.assertEquals(savedEmail.getText(), email.getText());
        Assert.assertEquals(savedEmail.getNotificationType(), email.getNotificationType());
        Assert.assertEquals(savedEmail.getSendBy(), email.getSendBy());
        Assert.assertEquals(savedEmail.getSendTime(), email.getSendTime());
    }

    public EmailHistoryEntity createEmailHistory() {
        EmailHistoryEntity email = new EmailHistoryEntity();
        email.setEmailType(EmailType.NOTIFICATION);
        email.setSendTo("testuser@mail.com");
        email.setSubject("Test subject");
        email.setText("Test body text");
        email.setNotificationType(NotificationTypeEnum.DETAILS);
        email.setSendBy(1L);
        email.setSendTime(new Date());
        return email;
    }
}
