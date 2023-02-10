package com.pls.email.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.MimeTypes;
import com.pls.core.domain.enums.EmailType;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.email.EmailMessage;
import com.pls.email.dto.EmailAttachmentDTO;
import com.pls.emailhistory.dao.EmailHistoryDao;
import com.pls.emailhistory.domain.EmailHistoryAttachmentEntity;
import com.pls.emailhistory.domain.EmailHistoryEntity;
import com.pls.emailhistory.domain.EmailHistoryLoadEntity;

/**
 * Implementation of {@link EmailSender}.
 * 
 * @author Aleksandr Leshchenko
 */
@Resource
@Transactional(isolation = Isolation.READ_COMMITTED)
class EmailSenderImpl extends JavaMailSenderImpl implements EmailSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderImpl.class);

    public static final String PACKING_GUIDE_FILE_PATH = "/pdf/GoShip_Packing_Guide.pdf";
    
    public static final String PACKING_GUIDE_FILE_NAME = "Goship Packing Guide.pdf";
    
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("(@plspro.com|@plslogistics.com|@goship.com>|goship.com)$");

    @Value("${email.from}")
    private String emailFrom;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EmailHistoryDao emailHistoryDao;

    @Override
    public void sendMessage(EmailMessage emailMessage) throws Exception {
        MimeMessage message = createMimeMessage();
        String from = getValidFromEmail(emailMessage);
        boolean hasAttachment = emailMessage.getAttachments().size() > 0;
        MimeMessageHelper helper = new MimeMessageHelper(message, hasAttachment);
        try {
            helper.setTo(emailMessage.getRecipients().toArray(new String[emailMessage.getRecipients().size()]));
            helper.setSubject(emailMessage.getSubject());
            helper.setFrom(from);
            helper.setText(emailMessage.getContent(), true);
            
            if (hasAttachment) {
                for (EmailAttachmentDTO attachment : emailMessage.getAttachments()) {
                    populateAttachment(helper, attachment);
                }
            }            
            if (emailMessage.getEmailType() == EmailType.CONFIRMATION) {
            	attachPackingGuide(helper);
            }
            if (StringUtils.isNotBlank(emailMessage.getBCC())) {
                helper.setBcc(emailMessage.getBCC());
            }
            
        } catch (Exception e) {
            LOGGER.error(getErrorMessage(emailMessage.getRecipients(), emailMessage.getSubject()));
            throw e;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending email...");
            LOGGER.debug("Using host:" + getHost() + " port:" + getPort());
        }
        send(message);
        saveEmailHistory(emailMessage);
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        LogStream out = new LogStream();
        getSession().setDebugOut(new PrintStream(out));
        super.send(mimeMessage);
        out.flush();
    }

    private String getValidFromEmail(EmailMessage emailMessage) {
    	
    	if (emailMessage.getFrom() != null) {
    		LOGGER.debug("FROM email: "+emailMessage.getFrom());
    	}
    	
        if (emailMessage.getFrom() != null && VALID_EMAIL_ADDRESS_REGEX.matcher(emailMessage.getFrom()).find()) {
            return emailMessage.getFrom();
        }
        return emailFrom;
    }

    private void saveEmailHistory(EmailMessage emailMessage) {
        if (emailMessage.getEmailType() == EmailType.NOT_AUDITABLE) {
            return;
        }
        EmailHistoryEntity emailHistoryEntity = new EmailHistoryEntity();
        if (emailMessage.getLoadIds() != null && !emailMessage.getLoadIds().isEmpty()) {
            emailHistoryEntity.setEmailHistoryLoadEntities(new HashSet<EmailHistoryLoadEntity>(emailMessage.getLoadIds().size()));
            for (Long id : emailMessage.getLoadIds()) {
                addLoadIfIdNotNull(emailHistoryEntity, id);
            }
        }
        emailHistoryEntity.setEmailType(emailMessage.getEmailType());
        emailHistoryEntity.setSendTo(StringUtils.join(emailMessage.getRecipients(), ";"));
        emailHistoryEntity.setSubject(emailMessage.getSubject());
        emailHistoryEntity.setText(emailMessage.getContent());
        emailHistoryEntity.setSendTime(Calendar.getInstance().getTime());
        emailHistoryEntity.setSendBy(emailMessage.getSendBy());
        emailHistoryEntity.setNotificationType(emailMessage.getNotificationType());
        emailHistoryEntity.setAttachments(new HashSet<EmailHistoryAttachmentEntity>(emailMessage.getAttachments().size()));
        for (EmailAttachmentDTO attachment : emailMessage.getAttachments()) {
            EmailHistoryAttachmentEntity emailHistoryAttachmentEntity = new EmailHistoryAttachmentEntity();
            emailHistoryAttachmentEntity.setImageMetadataId(attachment.getImageMetadataId());
            emailHistoryAttachmentEntity.setFilenameForUser(attachment.getAttachmentFileName());
            emailHistoryAttachmentEntity.setEmailHistory(emailHistoryEntity);
            emailHistoryEntity.getAttachments().add(emailHistoryAttachmentEntity);
        }
        try {
            emailHistoryDao.saveOrUpdate(emailHistoryEntity);
        } catch (Exception e) {
            LOGGER.error("Error: " + e.getMessage(), e);
        }
    }

    private void addLoadIfIdNotNull(EmailHistoryEntity emailHistoryEntity, Long id) {
        if (id != null) {
            EmailHistoryLoadEntity emailHistoryLoadEntity = new EmailHistoryLoadEntity();
            emailHistoryLoadEntity.setLoadId(id);
            emailHistoryLoadEntity.setEmailHistory(emailHistoryEntity);
            emailHistoryEntity.getEmailHistoryLoadEntities().add(emailHistoryLoadEntity);
        }
    }

    private void populateAttachment(MimeMessageHelper helper, final EmailAttachmentDTO emailAttachment) throws Exception {
        final LoadDocumentEntity document = documentService.loadDocumentWithoutContent(emailAttachment.getImageMetadataId());
        String fileName = emailAttachment.getAttachmentFileName();
        if (fileName == null) {
            fileName = document.getDocumentTypeEntity().getDescription() + "." + document.getFileType();
        }

        helper.addAttachment(fileName, new InputStreamSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return documentService.getDocumentInputStream(document);
            }
        }, document.getFileType().getMimeString());
    }
    
    private void attachPackingGuide(MimeMessageHelper helper) throws Exception { 
    	
        helper.addAttachment(PACKING_GUIDE_FILE_NAME, new InputStreamSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                InputStream inputStream = getClass().getResourceAsStream(PACKING_GUIDE_FILE_PATH);
                return inputStream;
            }
        }, MimeTypes.PDF.getMimeString());
    }

    private String getErrorMessage(List<String> recipients, String subject) {
        StringBuilder buffer = new StringBuilder(300);
        buffer.append("Failed to create email for ");
        buffer.append(StringUtils.join(recipients, ", "));
        buffer.append(" with subject ");
        buffer.append(subject);
        return buffer.toString();
    }
}
