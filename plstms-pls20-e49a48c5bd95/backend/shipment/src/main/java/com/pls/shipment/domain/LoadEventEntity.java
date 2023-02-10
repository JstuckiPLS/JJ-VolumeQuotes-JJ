package com.pls.shipment.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.util.SpringApplicationContext;

/**
 * Load event.
 * 
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "LOAD_GENERIC_EVENTS")
@Inheritance(strategy = InheritanceType.JOINED)
public class LoadEventEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 3499090750729703333L;

    public static final String Q_BY_LOAD_ID = "com.pls.shipment.domain.LoadEventEntity.Q_BY_LOAD_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loads_events_sequence")
    @SequenceGenerator(name = "loads_events_sequence", sequenceName = "LGEV_SEQ", allocationSize = 1)
    @Column(name = "EVENT_ID")
    private Long id;

    @Column(name = "EVENT_TYPE")
    private String eventTypeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_TYPE", nullable = false, insertable = false, updatable = false)
    private LoadEventTypeEntity eventType;

    @OneToMany(mappedBy = "eventDataPK.event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoadEventDataEntity> data;

    @Column(name = "LOAD_ID", nullable = false)
    private Long loadId;

    @Column(name = "IS_FAILURE", nullable = false)
    @Type(type = "yes_no")
    private Boolean isFailure;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate = new Date();

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy = SecurityUtils.getCurrentPersonId() == null ? SpringApplicationContext.getAdminUserId()
            : SecurityUtils.getCurrentPersonId();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", insertable = false, updatable = false)
    private UserEntity createdUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventTypeCode() {
        return eventTypeCode;
    }

    public void setEventTypeCode(String eventTypeCode) {
        this.eventTypeCode = eventTypeCode;
    }

    public LoadEventTypeEntity getEventType() {
        return eventType;
    }

    public void setEventType(LoadEventTypeEntity eventType) {
        this.eventType = eventType;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Boolean getFailure() {
        return isFailure;
    }

    public void setFailure(Boolean failure) {
        isFailure = failure;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public UserEntity getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(UserEntity createdUser) {
        this.createdUser = createdUser;
    }

    public List<LoadEventDataEntity> getData() {
        return data;
    }

    public void setData(List<LoadEventDataEntity> data) {
        this.data = data;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
