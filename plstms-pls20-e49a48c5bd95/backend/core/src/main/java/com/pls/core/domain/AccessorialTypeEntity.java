package com.pls.core.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.enums.ApplicableToUnit;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.shared.Status;

/**
 * AccessorialType entity for cost detail items.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "RATER.ACCESSORIAL_TYPES")
public class AccessorialTypeEntity implements Identifiable<String>, HasModificationInfo {
    private static final long serialVersionUID = -4706390207460184309L;

    public static final String Q_ALL_ACCESSORIALS_BY_STATUS = "com.pls.core.domain.AccessorialTypeEntity.Q_ALL_ACCESSORIALS_BY_STATUS";
    public static final String Q_UPDATE_ACCESSORIALS_STATUS = "com.pls.core.domain.AccessorialTypeEntity.Q_UPDATE_ACCESSORIALS_STATUS";
    public static final String QUERY_ACCESSORIALS_BY_GROUP = "com.pls.core.domain.AccessorialTypeEntity.QUERY_ACCESSORIALS_BY_GROUP";
    public static final String Q_PICKUP_AND_DELIVERY_ACCESSORIALS = "com.pls.core.domain.AccessorialTypeEntity.Q_PICKUP_AND_DELIVERY_ACCESSORIALS";
    public static final String QUERY_SAVED_QUOTES_FOR_CODE_USAGE = "com.pls.core.domain.AccessorialTypeEntity.QUERY_SAVED_QUOTES_FOR_CODE_USAGE";
    public static final String QUERY_LOADS_FOR_CODE_USAGE = "com.pls.core.domain.AccessorialTypeEntity.QUERY_LOADS_FOR_CODE_USAGE";

    /**
     * Default Constructor.
     */
    public AccessorialTypeEntity() {
    }

    /**
     * Constructor with ACCESSORIAL_TYPE_CODE as a parameter.
     * 
     * @param accessorialTypeCode
     *            to set as ID
     */
    public AccessorialTypeEntity(String accessorialTypeCode) {
        this.id = accessorialTypeCode;
    }

    @Id
    @Column(name = "ACCESSORIAL_TYPE_CODE")
    private String id;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "APPLICABLE_TO")
    @Enumerated(EnumType.STRING)
    private ApplicableToUnit applicableTo;

    @Column(name = "ACCESSORIAL_GROUP")
    @Enumerated(EnumType.STRING)
    private LtlAccessorialGroup accessorialGroup;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    private Long version = 1L;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApplicableToUnit getApplicableTo() {
        return applicableTo;
    }

    public void setApplicableTo(ApplicableToUnit applicableTo) {
        this.applicableTo = applicableTo;
    }

    public LtlAccessorialGroup getAccessorialGroup() {
        return accessorialGroup;
    }

    public void setAccessorialGroup(LtlAccessorialGroup accessorialGroup) {
        this.accessorialGroup = accessorialGroup;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
