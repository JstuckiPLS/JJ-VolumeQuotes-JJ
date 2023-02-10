package com.pls.user.service.impl.email;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.user.UserEntity;
import com.pls.email.EmailTemplateRenderer;
import com.pls.email.producer.EmailMessageProducer;

import freemarker.template.TemplateException;

/**
 * JMS Message producer for email notifications when user's password was changed.
 * 
 * @author Aleksandr Leshchenko
 * @author Stas Norochevskiy
 */
@Component
public class UserManagementEmailSender {
    private static final String PASSWORD_RESET_SUBJECT = "Your password in PLS system has been reset";
    private static final String USER_REGISTRATION_SUBJECT = "Registration in PLS PRO system";

    @Autowired
    private EmailMessageProducer emailMessageProducer;

    @Autowired
    private EmailTemplateRenderer emailTemplateRenderer;

    private String clientUrl;

    /**
     * Send email to registered user.
     * 
     * @param user
     *            {@link UserEntity}.
     * @param newPassword
     *            new password text.
     * @throws TemplateException
     *             if passwordResetEmail.ftl template is invalid
     * @throws IOException
     *             if passwordResetEmail.ftl template is missing
     */
    public void sendUserRegistredEmail(UserEntity user, String newPassword) throws IOException, TemplateException {
        emailMessageProducer.sendEmail(
                Arrays.asList(user.getEmail()),                 // recipient
                USER_REGISTRATION_SUBJECT,                      // subject
                getUserRegistredEmailContent(user, newPassword), // contents
                EmailType.NOT_AUDITABLE, null, null, null);
    }

    /**
     * Send email to user that password has been reset by administrator.
     * 
     * @param user
     *            {@link UserEntity}.
     * @param newPassword
     *            new password text.
     * @throws TemplateException
     *             if passwordResetEmail.ftl template is invalid
     * @throws IOException
     *             if passwordResetEmail.ftl template is missing
     */
    public void sendPasswordResetEmail(UserEntity user, String newPassword) throws IOException, TemplateException {
        emailMessageProducer.sendEmail(
                  Arrays.asList(user.getEmail()),                 // recipient
                  PASSWORD_RESET_SUBJECT,                         // subject
                  getPasswordResetEmailContent(user, newPassword), // contents
                  EmailType.NOT_AUDITABLE, null, null, null);
    }

    private String getUserRegistredEmailContent(UserEntity user, String newPassword) throws IOException, TemplateException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("user", user);
        data.put("newPassword", newPassword);
        data.put("clientUrl", clientUrl);
        return emailTemplateRenderer.renderEmailTemplate("userRegisteredEmail.ftl", data);
    }

    private String getPasswordResetEmailContent(UserEntity user, String newPassword) throws IOException, TemplateException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("user", user);
        data.put("newPassword", newPassword);
        data.put("clientUrl", clientUrl);
        return emailTemplateRenderer.renderEmailTemplate("passwordResetEmail.ftl", data);
    }

    @Value("${pls.client.index.url}")
    public void setClientUrl(String clientUrl) {
        this.clientUrl = clientUrl;
    }

}
