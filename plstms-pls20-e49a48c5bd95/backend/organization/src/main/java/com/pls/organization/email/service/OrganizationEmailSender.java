package com.pls.organization.email.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.email.EmailTemplateRenderer;
import com.pls.email.producer.EmailInfo;
import com.pls.email.producer.EmailMessageProducer;
import com.pls.organization.domain.bo.PaperworkEmailBO;
import com.pls.organization.utils.PLSCryptoHelper;

/**
 * Service for sending emails related to organizations.
 * 
 * @author Dmitriy Davydenko
 *
 */
@Component
public class OrganizationEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationEmailSender.class);

    private static final String PAPERWORK_SUBJECT = "Paperwork not received alert";

    private static final String UNSUBSCRIBE_SUBJECT = "Carrier has unsubscribed from paperwork emails";

    @Autowired
    private EmailMessageProducer emailMessageProducer;
    @Autowired
    private EmailTemplateRenderer emailTemplateRenderer;

    @Value("${pls.client.unsubscribe.url}")
    private String clientUnsubscribeUrl;

    @Value("${pls.encryptionKey.key}")
    private String encryptionKey;

    @Value("${email.from.paperworkSender}")
    private String paperworkSender;

    @Value("${email.to.carrierUnsubscribedEmailsRecipient}")
    private String carrierUnsubscribedEmailsRecipient;

    @Value("${email.carrierPaperworkBCC}")
    private String carrierPaperworkBCC;

    /**
     * Send paperwork email to carrier.
     * 
     * @param paperwork
     *            that should be send to the carrier.
     */
    public void sendCarrierEmailPaperwork(List<PaperworkEmailBO> paperwork) {
        Map<Long, List<PaperworkEmailBO>> result = paperwork.stream().collect(Collectors.groupingBy(PaperworkEmailBO::getCarrierId));

        for (Map.Entry<Long, List<PaperworkEmailBO>> entry : result.entrySet()) {
            Long carrierId = entry.getKey();
            List<PaperworkEmailBO> paperworkEmails = entry.getValue();

            try {
                String encryptedData = PLSCryptoHelper.encrypt(encryptionKey, carrierId.toString());

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("carrierName", paperworkEmails.get(0).getCarrierName());
                params.put("paperworkEmails", paperworkEmails);
                params.put("unsubscribe", clientUnsubscribeUrl + "?id=" + encryptedData);

                String sendTo = paperworkEmails.get(0).getCarrierEmail();
                String content = emailTemplateRenderer.renderEmailTemplate("PaperworkEmailtemplate.ftl", params);

                EmailInfo paperworkEmail = new EmailInfo(paperworkSender, Arrays.asList(sendTo), PAPERWORK_SUBJECT, content, null,
                        EmailType.NOT_AUDITABLE, null, SecurityUtils.getCurrentPersonId(), Collections.emptyList());
                paperworkEmail.setBCC(carrierPaperworkBCC);
                emailMessageProducer.sendEmail(paperworkEmail);
            } catch (Exception e) {
                LOGGER.error("Can't send email paperwork for carrier ID: {}, {}", carrierId, e.getMessage(), e);
            }
        }
    }

    /**
     * Sends an email to the carrier, when unsubscribe from emails process is done.
     * 
     * @param orgName
     *            - the name of the Carrier
     */
    public void sendUnsubscribeSuccessEmail(String orgName) {

        Map<String, Object> params = new HashMap<>();
        params.put("carrierName", orgName);

        List<String> sendToList = Arrays.asList(new String[] {carrierUnsubscribedEmailsRecipient});
        String subject = UNSUBSCRIBE_SUBJECT;
        try {
            String content = emailTemplateRenderer.renderEmailTemplate("UnsubscribeEmailtemplate.ftl", params);

            EmailInfo unsubscribeSuccessEmail = new EmailInfo(paperworkSender, sendToList, subject, content, null, EmailType.NOT_AUDITABLE, null,
                    null, Collections.emptyList());
            emailMessageProducer.sendEmail(unsubscribeSuccessEmail);

        } catch (Exception e) {
            LOGGER.error("Can't send email paperwork to Carrier: {}, {}", orgName, e.getMessage(), e);
        }
    }
}
