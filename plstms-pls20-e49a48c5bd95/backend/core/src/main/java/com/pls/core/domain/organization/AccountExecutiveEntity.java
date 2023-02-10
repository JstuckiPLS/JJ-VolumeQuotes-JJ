package com.pls.core.domain.organization;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.dao.impl.hibernate.AccountExecutiveEventListeners;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.shared.Status;

/**
 * Mapping of account executive for organization location.
 * <p>
 * Account Executive is a PLS employee accountable for specific organization, e.g. tracking org's loads, solving loads or payment issues etc.<br/>
 * Account Executive is active until in period between effective date and expiration date.
 * Current record in always active, no matter what eff and exp dates are, until it is 6 month since person had left PLS. Only in this case record
 * is deactivated.<br/>
 * Status and tracking ID should not be changed inside code, only {@link AccountExecutiveEventListeners} does that.
 * </p>
 * <p>
 * TRACKING_ID is used for maintaining history. In this case, whenever we make a change to a record, we inactivate existing one and create new
 * one with updated information. To identify which was the original record which we have been updating,
 * we use TRACKING_ID with value equal to ID and this gives us the very first record that was created for this scenario.
 * Whenever new record is created its value is automatically set to value equal to ID. Whenever existing record is updates - it is
 * automatically deactivated and saved without changes, instead new one with new values and same TRACKING_ID is created.
 * See {@link AccountExecutiveEventListeners} class for details.
 * </p>
 * <p>
 * <b>Caution:</b> Do not change any properties after records has been deactivated, otherwise Hibernate will not notice them. This
 * means, that when any change is made and session is flushed, record is deactivated and instead new one becomes active. If you cache reference
 * to original inactive record and change it - nothing happens on next session flush. Always get most recent active version.
 * </p>
 *
 * @author Viacheslav Krot
 */
@Entity
@Table(name = "USER_CUSTOMER")
public class AccountExecutiveEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = -3321235090590126398L;

    private static final Date DEFAULT_EXPIRATION_DATE;
    static {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(3000, Calendar.JANUARY, 1);
        DEFAULT_EXPIRATION_DATE = DateUtility.truncateMilliseconds(date.getTime());
    }

    /**
     * Default constructor for Hibernate.
     */
    private AccountExecutiveEntity() {
    }

    /**
     * Create account executive.
     * 
     * @param location
     *            location. Required.
     * @param user
     *            user. Required.
     */
    public AccountExecutiveEntity(OrganizationLocationEntity location, UserEntity user) {
        this();
        checkNotNull(location, "Location is required");
        checkNotNull(location.getOrganization(), "Location customer is required");
        checkNotNull(user, "User is required");
        this.location = location;
        this.customer = location.getOrganization();
        this.user = user;
    }

    /**
     * Copy constructor that returns a copy of given clone.
     *
     * @param clone entity to copy.
     */
    public AccountExecutiveEntity(AccountExecutiveEntity clone) {
        this(clone.getLocation(), clone.getUser());
        effectiveDate = clone.getEffectiveDate();
        expirationDate = clone.getExpirationDate();
        status = clone.getStatus();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "uc_sequence")
    @SequenceGenerator(name = "uc_sequence", sequenceName = "USER_CUSTOMER_SEQ", allocationSize = 1)
    @Column(name = "USER_CUSTOMER_ID")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EFF_DATE")
    private Date effectiveDate = DateUtility.truncateMilliseconds(new Date());

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXP_DATE")
    private Date expirationDate = DEFAULT_EXPIRATION_DATE;

    @ManyToOne(optional = false)
    @JoinColumn(name = "LOCATION_ID", updatable = false)
    private OrganizationLocationEntity location;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PERSON_ID", updatable = false)
    private UserEntity user;

    @Column(name = "PERSON_ID", insertable = false, updatable = false)
    private Long personId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORG_ID", nullable = false, updatable = false)
    private OrganizationEntity customer;

    @Column(name = "USER_CUSTOMER_TRACKING_ID", updatable = false)
    private Long trackingId;

    @Version
    private Long version = 1L;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "createdDate", column = @Column(name = "CREATED_DATE")),
            @AttributeOverride(name = "modifiedDate", column = @Column(name = "MODIFIED_DATE"))
    })
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public OrganizationLocationEntity getLocation() {
        return location;
    }

    public void setLocation(OrganizationLocationEntity location) {
        this.location = location;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getPersonId() {
        return this.personId;
    }

    public OrganizationEntity getCustomer() {
        return customer;
    }

    public void setCustomer(OrganizationEntity customer) {
        this.customer = customer;
    }

    public Long getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(Long trackingId) {
        this.trackingId = trackingId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }
}
