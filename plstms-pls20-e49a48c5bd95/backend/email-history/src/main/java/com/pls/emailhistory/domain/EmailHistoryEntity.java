package com.pls.emailhistory.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.core.domain.user.UserEntity;

/**
 * Email audit item.
 * 
 * @author Sergii Belodon
 */
@Entity
@Table(name = "EMAIL_HISTORY")
public class EmailHistoryEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 1244029030700924303L;

    public static final String Q_GET_EMAIL_HISTORY = "com.pls.emailhistory.domain.EmailHistoryEntity.Q_GET_EMAIL_HISTORY";
    public static final String Q_GET_ALL_ATTACHMENTS_FOR_LOAD = "com.pls.emailhistory.domain.EmailHistoryEntity.Q_GET_ALL_ATTACHMENTS_FOR_LOAD";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_history_sequence")
    @SequenceGenerator(name = "email_history_sequence", sequenceName = "EMAIL_HISTORY_SEQ", allocationSize = 1)
    @Column(name = "EMAIL_HISTORY_ID")
    private Long id;

    @OneToMany(mappedBy = "emailHistory", cascade = CascadeType.PERSIST)
    private Set<EmailHistoryLoadEntity> emailHistoryLoadEntities;

    @Column(name = "EMAIL_TYPE")
    @Enumerated(EnumType.STRING)
    private EmailType emailType;

    @Column(name = "SEND_TO")
    private String sendTo;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "TEXT")
    private String text;

    @Column(name = "NOTIFICATION_TYPE")
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum notificationType;

    @Column(name = "SEND_BY")
    private Long sendBy;

    @Column(name = "SEND_TIME")
    private Date sendTime;

    @OneToMany(mappedBy = "emailHistory", cascade = CascadeType.PERSIST)
    private Set<EmailHistoryAttachmentEntity> attachments;

    @ManyToOne
    @JoinColumn(name = "SEND_BY", referencedColumnName = "PERSON_ID", insertable = false, updatable = false)
    private UserEntity user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<EmailHistoryLoadEntity> getEmailHistoryLoadEntities() {
        return emailHistoryLoadEntities;
    }

    public void setEmailHistoryLoadEntities(Set<EmailHistoryLoadEntity> emailHistoryLoadEntities) {
        this.emailHistoryLoadEntities = emailHistoryLoadEntities;
    }


    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public Long getSendBy() {
        return sendBy;
    }

    public void setSendBy(Long sendBy) {
        this.sendBy = sendBy;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Set<EmailHistoryAttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<EmailHistoryAttachmentEntity> attachments) {
        this.attachments = attachments;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}