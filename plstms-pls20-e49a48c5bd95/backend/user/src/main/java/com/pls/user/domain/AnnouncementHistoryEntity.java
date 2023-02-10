package com.pls.user.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * This table contains the information about the announcements glance by the user.
 * 
 * @author Nalapko Alexander
 */
@Entity
@Table(name = "ANNOUNCEMENTS_HISTORY")
public class AnnouncementHistoryEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 4373870608475924519L;
    public static final String Q_GET_BY_PERSON_AND_ANNOUNCEMENT = "com.pls.user.domain.AnnouncementHistoryEntity.Q_GET_BY_PERSON_AND_ANNOUNCEMENT";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANNOUNCEMENTS_HIST_SEQ")
    @SequenceGenerator(name = "ANNOUNCEMENTS_HIST_SEQ", sequenceName = "ANNOUNCEMENTS_HIST_SEQ", allocationSize = 1)
    @Column(name = "ANNOUNCEMENT_HIST_ID", nullable = false)
    private Long id;

    @Column(name = "ANNOUNCEMENT_ID", nullable = false)
    private Long announcementId;

    @Column(name = "PERSON_ID", nullable = false)
    private Long personId;

    @Column(name = "READ_DATE", nullable = false)
    private Date readDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(Long announcementId) {
        this.announcementId = announcementId;
    }
}
