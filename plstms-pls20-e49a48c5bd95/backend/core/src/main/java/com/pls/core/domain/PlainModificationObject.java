package com.pls.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;

/**
 * Abstract entity that contains fields and methods for storing creation and last modification data.
 * 
 * @author Denis Zhupinsky
 */
@Embeddable
public class PlainModificationObject implements Serializable {

    private static final long serialVersionUID = -1051534363727476268L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate = new Date();

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy = SecurityUtils.getCurrentPersonId();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_MODIFIED", nullable = true)
    private Date modifiedDate = new Date();

    @Column(name = "MODIFIED_BY", nullable = true)
    private Long modifiedBy = SecurityUtils.getCurrentPersonId();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", insertable = false, updatable = false)
    @JsonIgnore
    private UserEntity createdUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODIFIED_BY", insertable = false, updatable = false)
    @JsonIgnore
    private UserEntity modifiedUser;

    /**
     * Get object creation date.
     * 
     * @return object creation date.
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Get id of user who created the object.
     * 
     * @return id of user who created the object.
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Get object last modify date.
     * 
     * @return the object last modify date.
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Get id of user who modified the object last time.
     * 
     * @return id of user who modified the object last time.
     */
    public Long getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Set object date creation.
     * 
     * @param created
     *            the object creation date.
     */
    public void setCreatedDate(Date created) {
        this.createdDate = DateUtility.truncateMilliseconds(created);
    }

    /**
     * Set id of user who created the object.
     * 
     * @param createdBy
     *            id of user who created the object.
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set object last modify date.
     * 
     * @param modified
     *            the object last modify date.
     */
    public void setModifiedDate(Date modified) {
        this.modifiedDate = DateUtility.truncateMilliseconds(modified);
    }

    /**
     * Set id of user who modified the object last time.
     * 
     * @param modifiedBy
     *            id of user who modified the object last time.
     */
    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public UserEntity getCreatedUser() {
        return createdUser;
    }

    public UserEntity getModifiedUser() {
        return modifiedUser;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getModifiedBy())
                .append(getCreatedBy()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof PlainModificationObject) {
            if (obj == this) {
                result = true;
            } else {
                PlainModificationObject other = (PlainModificationObject) obj;

                result = new EqualsBuilder()
                        //Cannot compare createdDate as current date is set as field value and it will change everytime.
                        //.append(getDateCreated().getTime(),other.getDateCreated().getTime())
                        .append(getCreatedBy(), other.getCreatedBy())
                        //.append(getDateModified(), other.getDateModified())
                        .append(getModifiedBy(), other.getModifiedBy()).isEquals();
            }
        }
        return result;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("createdDate", getCreatedDate().getTime())
                .append("createdBy", getCreatedBy())
                .append("modifiedDate", getModifiedDate())
                .append("modifiedBy", getModifiedBy()).toString();
    }

}
