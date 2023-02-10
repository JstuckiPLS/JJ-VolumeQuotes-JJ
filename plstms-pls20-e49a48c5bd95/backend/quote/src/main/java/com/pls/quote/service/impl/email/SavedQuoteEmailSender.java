package com.pls.quote.service.impl.email;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.email.EmailTemplateRenderer;
import com.pls.email.dto.EmailAttachmentDTO;
import com.pls.email.producer.EmailInfo;
import com.pls.email.producer.EmailMessageProducer;
import com.pls.quote.dao.SavedQuoteDao;
import com.pls.shipment.domain.SavedQuoteEntity;

/**
 * Class for sending emails related to Saved Quotes functionality.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
public class SavedQuoteEmailSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(SavedQuoteEmailSender.class);

    @Autowired
    private EmailMessageProducer emailMessageProducer;

    @Autowired
    private EmailTemplateRenderer emailTemplateRenderer;

    @Autowired
    private SavedQuoteDao savedQuoteDao;

    @Autowired
    private ContactInfoService contactInfoService;

    @Value("${pls.client.index.url}")
    private String clientUrl;

    /**
     * Send email for Saved Quote.
     * 
     * @param recipients
     *            list of recipients
     * @param subject
     *            email subject
     * @param content
     *            email content
     * @param loadId - load id
     */
    public void sendQuoteEmail(String recipients, String subject, String content, Long loadId) {
        List<String> recipientsList = Arrays.asList(recipients.split(";"));
        UserAdditionalContactInfoBO contact = contactInfoService.getContactInfoForCurrentUser();
        EmailInfo emailInfo = new EmailInfo(contact.getEmail(), recipientsList, subject, content, Collections.<EmailAttachmentDTO>emptyList(),
                EmailType.NOTIFICATION, NotificationTypeEnum.DETAILS, SecurityUtils.getCurrentPersonId(), Collections.singletonList(loadId));
        emailMessageProducer.sendEmail(emailInfo);
    }

    /**
     * Get content of email for sending Saved Quote.
     * 
     * @param quoteId
     *            {@link SavedQuoteEntity#getId()}
     * @return email content or empty string
     */
    public String getPayloadForQuoteEmail(Long quoteId) {
        try {
            SavedQuoteEntity quote = savedQuoteDao.get(quoteId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("quote", quote);
            params.put("clientUrl", clientUrl);
            return emailTemplateRenderer.renderEmailTemplate("savedQuoteEmailTemplate.ftl", params);
        } catch (Exception e) {
            LOGGER.error("Can't get payload for quote email. Quote ID: " + quoteId, e);
            return StringUtils.EMPTY;
        }
    }
}
