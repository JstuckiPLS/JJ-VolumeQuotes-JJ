package com.pls.email.consumer;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.email.EmailMessage;
import com.pls.email.dto.EmailAttachmentDTO;
import com.pls.emailhistory.dao.EmailHistoryDao;
import com.pls.emailhistory.domain.bo.EmailHistoryBO;

/**
 * Integration test for {@link EmailSender}.
 * 
 * @author Aleksandr Leshchenko
 */
public class EmailSenderIT extends BaseServiceITClass {
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private EmailHistoryDao emailHistoryDao;

    @Test
    public void testSendEmailWithAttachment() throws Exception {
        rewriteJavaMailPropertiesForGreenMail(emailSender);
        GreenMail greenMail = new GreenMail(new ServerSetup(2225, null, ServerSetup.PROTOCOL_SMTP));
        greenMail.start();

        String bodyText = "Body text";

        EmailMessage emailMessage = new EmailMessage(
                Arrays.asList("testPLSEmail@localhost.com"),
                "Test emailwith attachment",
                bodyText,
                Arrays.asList(new EmailAttachmentDTO(1L, "text.txt")),
                null,
                EmailType.DOCUMENT,
                NotificationTypeEnum.DETAILS,
                Collections.singletonList(1L),
                1L);
        emailSender.sendMessage(emailMessage);

        greenMail.waitForIncomingEmail(1);
        MimeMessage message = greenMail.getReceivedMessages()[0];

        Assert.assertTrue(message.getContent() instanceof  MimeMultipart);

        MimeMultipart mimePart = (MimeMultipart) message.getContent();
        Assert.assertEquals(2, mimePart.getCount());

        // Email message
        Assert.assertTrue(mimePart.getBodyPart(0).getContent() instanceof MimeMultipart);
        MimeMultipart contentPart = (MimeMultipart) mimePart.getBodyPart(0).getContent();
        Assert.assertEquals(bodyText, GreenMailUtil.getBody(contentPart.getBodyPart(0)));

        // Attachment
        Assert.assertEquals(18546, GreenMailUtil.getBody(mimePart.getBodyPart(1)).getBytes().length);

        List<EmailHistoryBO> emailHistory = emailHistoryDao.getEmailHistoryByLoadIdAndTypes(1L, EnumSet.of(EmailType.DOCUMENT));
        Assert.assertEquals(1L, emailHistory.size());

        EmailHistoryBO entity = emailHistory.get(0);
        Assert.assertEquals(EmailType.DOCUMENT, entity.getEmailType());
        Assert.assertEquals("testPLSEmail@localhost.com", entity.getSendTo());
        Assert.assertEquals("Test emailwith attachment", entity.getSubject());
        Assert.assertEquals(bodyText, entity.getText());
        Assert.assertEquals(NotificationTypeEnum.DETAILS, entity.getNotificationType());
        Assert.assertEquals("admin sysadmin", entity.getSendBy());
        Assert.assertNotNull(entity.getSendTime());

        greenMail.stop();
        restoreJavaMailPropertiesForGreenMail(emailSender);
    }

    private Properties previousValues;

    private void rewriteJavaMailPropertiesForGreenMail(EmailSender eSender) {
        previousValues = new Properties(eSender.getJavaMailProperties());
        eSender.setPort(2225);
        eSender.setHost("localhost");
        Properties testMailProperties = new Properties();
        testMailProperties.put("email.socketFactory.class", "javax.net.SocketFactory");
        eSender.setJavaMailProperties(testMailProperties);
    }

    private void restoreJavaMailPropertiesForGreenMail(EmailSender eSender) {
        eSender.setJavaMailProperties(previousValues);
    }

}
