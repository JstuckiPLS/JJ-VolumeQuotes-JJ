package com.pls.shipment.service.impl.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.DictionaryTypesService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.email.EmailTemplateRenderer;
import com.pls.email.dto.EmailAttachmentDTO;
import com.pls.email.producer.EmailInfo;
import com.pls.email.producer.EmailMessageProducer;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadNotificationsEntity;

import freemarker.template.TemplateException;

/**
 * IT test for sending shipments emails.
 * 
 * @author Stas Norochevskiy
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentEmailSenderTest {

    private static final String APP_URL = "www.plslogistics.com";

    private static final Long ADMIN_PERSON_ID = 120052L;

    @Mock
    private EmailMessageProducer emailMessageProducer;

    @Mock
    private DictionaryTypesService dictionaryTypesService;

    @Mock
    private EmailTemplateRenderer emailTemplateRenderer;

    @Mock
    private ContactInfoService contactInfoService;

    @InjectMocks
    private ShipmentEmailSender shipmentEmailSender;

    @Captor
    private ArgumentCaptor<EmailInfo> argumentCaptor;

    @Before
    public void init() {
        shipmentEmailSender.setClientUrl(APP_URL);
        shipmentEmailSender.setAdminPersonId(ADMIN_PERSON_ID);
    }

    @Test
    public void shouldSendDocuments() throws Exception {
        List<String> recipients = getEmails();
        String subject = "subject" + Math.random();
        String content = "content" + Math.random();
        List<EmailAttachmentDTO> docs = getDocs();

        UserAdditionalContactInfoBO contact = createRandomContactInfo();
        Mockito.when(contactInfoService.getContactInfoForCurrentUser()).thenReturn(contact);

        shipmentEmailSender.sendDocumentsEmail(StringUtils.join(recipients, ';'), subject, content, docs, 1L, EmailType.DOCUMENT.toString(), null);

        Mockito.verify(emailMessageProducer).sendEmail(argumentCaptor.capture());

        List<EmailAttachmentDTO> attachments = argumentCaptor.getValue().getAttachments();
        Assert.assertSame(docs, attachments);
    }

    @Test
    public void shouldSendLoadStatusChangedNotification1() throws IOException, TemplateException, InterruptedException {
        SecurityTestUtils.logout();
        SecurityTestUtils.login("TestUser");

        sendLoadStatusChangeNotification(ShipmentStatus.DISPATCHED.name(), ShipmentStatus.DISPATCHED, NotificationTypeEnum.DISPATCHED);
    }

    @Test
    public void shouldSendLoadStatusChangedNotification2() throws IOException, TemplateException, InterruptedException {
        SecurityTestUtils.logout();
        SecurityTestUtils.login("TestUser");

        sendLoadStatusChangeNotification(ShipmentStatus.OUT_FOR_DELIVERY.name(), ShipmentStatus.OUT_FOR_DELIVERY,
                NotificationTypeEnum.OUT_FOR_DELIVERY);
    }

    @Test
    public void shouldSendLoadStatusChangedNotification3() throws IOException, TemplateException, InterruptedException {
        SecurityTestUtils.logout();
        SecurityTestUtils.login("TestUser");

        sendLoadStatusChangeNotification(ShipmentStatus.DELIVERED.name(), ShipmentStatus.DELIVERED, NotificationTypeEnum.DELIVERED);
    }

    @Test
    public void shouldSendLoadStatusChangedNotification4() throws IOException, TemplateException, InterruptedException {
        SecurityTestUtils.logout();
        SecurityTestUtils.login("TestUser");

        sendLoadStatusChangeNotification("PICK_UP", ShipmentStatus.IN_TRANSIT, NotificationTypeEnum.PICK_UP);
    }

    @Test
    public void shouldNotSendLoadStatusChangedNotification() throws IOException, TemplateException, InterruptedException {
        for (ShipmentStatus status : ShipmentStatus.values()) {
            if (status != ShipmentStatus.DISPATCHED && status != ShipmentStatus.OUT_FOR_DELIVERY && status != ShipmentStatus.DELIVERED
                    && status != ShipmentStatus.IN_TRANSIT) {
                sendLoadStatusChangeNotificationForInvalidStatus(status.name(), status);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void sendLoadStatusChangeNotificationForInvalidStatus(String notificationType, ShipmentStatus newStatus) throws IOException,
            TemplateException {
        LoadEntity load = new LoadEntity();
        final String bol = "bol" + Math.random();
        load.getNumbers().setBolNumber(bol);
        load.setId(1L);
        load.setLoadNotifications(getLoadNotifications(load));

        NotificationTypeEntity notificationTypeEntity = getNotificationType(notificationType);
        Mockito.when(dictionaryTypesService.getNotificationTypesById(notificationType)).thenReturn(notificationTypeEntity);

        final String payload = "payload" + Math.random();
        Mockito.when(emailTemplateRenderer.renderEmailTemplate(Mockito.anyString(), Mockito.anyMap())).thenReturn(payload);

        shipmentEmailSender.sendLoadStatusChangedNotification(load, newStatus);

        Mockito.verifyNoMoreInteractions(emailTemplateRenderer);
        Mockito.verifyNoMoreInteractions(emailMessageProducer);
    }

    private void sendLoadStatusChangeNotification(String notificationType, ShipmentStatus newStatus, NotificationTypeEnum notificationTypeEnum)
            throws IOException, TemplateException {
        LoadEntity load = new LoadEntity();
        final String bol = "bol" + Math.random();
        load.getNumbers().setBolNumber(bol);
        load.setId(1L);
        load.setLoadNotifications(getLoadNotifications(load));
        load.addLoadDetails(new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN));
        load.addLoadDetails(new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION));
        load.getOrigin().setAddress(new AddressEntity());
        load.getDestination().setAddress(new AddressEntity());

        NotificationTypeEntity notificationTypeEntity = getNotificationType(notificationType);
        String description = notificationTypeEntity.getDescription();
        List<String> emails = getEmailsByNotificationType(load.getLoadNotifications(), notificationType);

        UserAdditionalContactInfoBO contact = createRandomContactInfo();
        Mockito.when(contactInfoService.getContactInfoForCurrentUser()).thenReturn(contact);
        Mockito.when(dictionaryTypesService.getNotificationTypesById(notificationType)).thenReturn(notificationTypeEntity);

        final String payload = "payload" + Math.random();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("load", load);
        params.put("notificationDescription", newStatus.getDescription());
        params.put("clientUrl", APP_URL);
        params.put("contact", contact);
        params.put("isBlindBol", false);
        Mockito.when(emailTemplateRenderer.renderEmailTemplate("loadStatusChangedTemplate.ftl", params)).thenReturn(payload);

        shipmentEmailSender.sendLoadStatusChangedNotification(load, newStatus);

        Mockito.verify(emailTemplateRenderer).renderEmailTemplate("loadStatusChangedTemplate.ftl", params);

        String subject = "PLS PRO Order for BOL " + bol + " is " + description;
        Mockito.verify(emailMessageProducer).sendEmail(argumentCaptor.capture());
        List<EmailInfo> allValues = argumentCaptor.getAllValues();
        EmailInfo emailInfo = allValues.get(allValues.size() - 1);
        Assert.assertEquals(contact.getEmail(), emailInfo.getFrom());
        Assert.assertEquals(emails.size(), emailInfo.getRecipients().size());
        for (String email : emails) {
            Assert.assertTrue(emailInfo.getRecipients().contains(email));
        }
        Assert.assertEquals(subject, emailInfo.getSubject());
        Assert.assertEquals(payload, emailInfo.getContent());
        Assert.assertNull(emailInfo.getAttachments());
        Assert.assertEquals(EmailType.NOTIFICATION, emailInfo.getEmailType());
        Assert.assertEquals(notificationTypeEnum, emailInfo.getNotificationType());
        Assert.assertEquals(1L, (long) emailInfo.getSendBy());
        Assert.assertEquals(Collections.singletonList(1L), emailInfo.getLoadIds());
    }

    private List<String> getEmailsByNotificationType(Set<LoadNotificationsEntity> loadNotifications, String notificationType) {
        ArrayList<String> emails = new ArrayList<String>();
        for (LoadNotificationsEntity notification : loadNotifications) {
            if (notificationType.equals(notification.getNotificationType().getId())) {
                emails.add(notification.getEmailAddress());
            }
        }
        return emails;
    }

    private Set<LoadNotificationsEntity> getLoadNotifications(LoadEntity load) {
        Set<LoadNotificationsEntity> notifications = new HashSet<LoadNotificationsEntity>();
        for (ShipmentStatus status : ShipmentStatus.values()) {
            if (status == ShipmentStatus.IN_TRANSIT) {
                notifications.add(getLoadNotification("PICK_UP", load));
                notifications.add(getLoadNotification("PICK_UP", load));
            }
            notifications.add(getLoadNotification(status.name(), load));
            notifications.add(getLoadNotification(status.name(), load));
        }
        return notifications;
    }

    private LoadNotificationsEntity getLoadNotification(String notificationType, LoadEntity load) {
        LoadNotificationsEntity notification = new LoadNotificationsEntity();
        notification.setLoad(load);
        notification.setEmailAddress("emailAddress" + Math.random());
        notification.setNotificationType(getNotificationType(notificationType));
        return notification;
    }

    private NotificationTypeEntity getNotificationType(String notificationType) {
        NotificationTypeEntity notificationTypeEntity = new NotificationTypeEntity();
        notificationTypeEntity.setId(notificationType);
        notificationTypeEntity.setDescription("description" + Math.random());
        return notificationTypeEntity;
    }

    private List<EmailAttachmentDTO> getDocs() {
        List<EmailAttachmentDTO> docs = new ArrayList<EmailAttachmentDTO>();
        do {
            docs.add(new EmailAttachmentDTO((long) (Math.random() * 1000), "fileName" + Math.random()));
        } while (0.25 < Math.random());
        return Collections.unmodifiableList(docs);
    }

    private List<String> getEmails() {
        List<String> emails = new ArrayList<String>();
        do {
            emails.add("email" + Math.random());
        } while (0.25 < Math.random());
        return Collections.unmodifiableList(emails);
    }

    private UserAdditionalContactInfoBO createRandomContactInfo() {
        UserAdditionalContactInfoBO result = new UserAdditionalContactInfoBO();
        result.setContactName(String.valueOf(Math.random()));
        result.setEmail(String.valueOf(Math.random()));
        result.setPhone(new PhoneBO());

        return result;
    }
}
