package com.pls.user.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.user.UserEntity;
import com.pls.user.domain.enums.AnnouncementStatus;

/**
 * Entity for announcement.
 * 
 * @author Nalapko Alexander
 */
@Entity
@Table(name = "ANNOUNCEMENTS")
public class AnnouncementEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -3282186446890529018L;
    public static final String Q_UPDATE_STATUS = "com.pls.user.domain.AnnouncementEntity.Q_UPDATE_STATUS";
    public static final String Q_GET_UNPUBLISHED = "com.pls.user.domain.AnnouncementEntity.Q_GET_UNPUBLISHED";
    public static final String Q_GET_UNREAD_COUNT = "com.pls.user.domain.AnnouncementEntity.Q_GET_UNREAD_COUNT";
    public static final String Q_GET_BY_STATUS_AND_PERIOD = "com.pls.user.domain.AnnouncementEntity.Q_GET_BY_STATUS_AND_PERIOD";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANNOUNCEMENTS_SEQ")
    @SequenceGenerator(name = "ANNOUNCEMENTS_SEQ", sequenceName = "ANNOUNCEMENTS_SEQ", allocationSize = 1)
    @Column(name = "ANNOUNCEMENT_ID", nullable = false)
    private Long id;

    @Column(name = "THEME", nullable = false)
    private String theme;

    @Column(name = "TEXT", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANNOUNCER_ID")
    private UserEntity announcer;

    @Column(name = "START_DATE")
    private Date startDate;

    @Column(name = "END_DATE")
    private Date endDate;

    @Column(name = "PUBLISHED_DATE")
    private Date publishedDate;

    @Column(name = "STATUS", nullable = false)
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "ANNOUNCEMENT_ID")
    private Set<AnnouncementHistoryEntity> announcementHistory;

    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.user.domain.enums.AnnouncementStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"),
            @Parameter(name = "valueOfMethod", value = "getStatusByValue") })
    @Column(name = "STATUS", nullable = false)
    private AnnouncementStatus status = AnnouncementStatus.UNPUBLISHED;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public PlainModificationObject getModification() {
        return modification;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserEntity getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(UserEntity announcer) {
        this.announcer = announcer;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public AnnouncementStatus getStatus() {
        return status;
    }

    public void setStatus(AnnouncementStatus status) {
        this.status = status;
    }

    public Set<AnnouncementHistoryEntity> getAnnouncementHistory() {
        return announcementHistory;
    }

    public void setAnnouncementHistory(Set<AnnouncementHistoryEntity> announcementHistory) {
        this.announcementHistory = announcementHistory;
    }

    /**
     * Copy the announcement.
     * 
     * @return {@link AnnouncementEntity}
     */
    public AnnouncementEntity copy() {
        AnnouncementEntity copy = new AnnouncementEntity();
        copy.setEndDate(getEndDate());
        copy.setPublishedDate(getPublishedDate());
        copy.setStartDate(getStartDate());
        copy.setStatus(AnnouncementStatus.UNPUBLISHED);
        copy.setText(getText());
        copy.setTheme(getTheme());
        copy.setAnnouncer(null);
        return copy;
    }
}
