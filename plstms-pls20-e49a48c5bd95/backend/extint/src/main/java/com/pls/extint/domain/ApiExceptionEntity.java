package com.pls.extint.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Entity class for API_EXCEPTIONS.Stores the mismatches between load fields in PLSPRO and the values received from from API when API throws an
 * exception.
 * 
 * @author Pavani Challa
 * 
 */
@Entity
@Table(name = "API_EXCEPTIONS")
public class ApiExceptionEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -5488587856039249649L;

    private static final int NUMBER_145 = 145;

    private static final int NUMBER_131 = 131;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "API_EXCEPTION_SEQUENCE")
    @SequenceGenerator(name = "API_EXCEPTION_SEQUENCE", sequenceName = "API_EXCEPTION_SEQ", allocationSize = 1)
    @Column(name = "API_EXCEPTION_ID")
    private Long id;

    @Column(name = "API_TYPE_ID", nullable = false)
    private Long apiTypeId;

    @Column(name = "LOAD_ID", nullable = false)
    private Long loadId;

    @Column(name = "FIELD", nullable = false)
    private String fieldName;

    @Column(name = "OLD_VALUE", nullable = true)
    private String oldValue;

    @Column(name = "NEW_VALUE", nullable = true)
    private String newValue;

    @Column(name = "BOL", nullable = true)
    private String bol;

    @Column(name = "CARRIER_REFERENCE_NUMBER", nullable = true)
    private String carrierReferenceNumber;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    /**
     * Default Constructor.
     */
    public ApiExceptionEntity() {
        // Do Nothing
    }

    /**
     * Constructor with params to create the exception object with required data.
     * 
     * @param apiTypeId
     *            api type id of the api call during which the exception raised
     * @param loadId
     *            load id for which the exception belongs.
     * @param fieldName
     *            field name in which the exception raised
     * @param oldValue
     *            old value of the field
     * @param newValue
     *            new value from api.
     */
    public ApiExceptionEntity(Long apiTypeId, Long loadId, String fieldName, String oldValue, String newValue) {
        this.apiTypeId = apiTypeId;
        this.loadId = loadId;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApiTypeId() {
        return apiTypeId;
    }

    public void setApiTypeId(Long apiTypeId) {
        this.apiTypeId = apiTypeId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getCarrierReferenceNumber() {
        return carrierReferenceNumber;
    }

    public void setCarrierReferenceNumber(String carrierReferenceNumber) {
        this.carrierReferenceNumber = carrierReferenceNumber;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer pVersion) {
        this.version = pVersion;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(NUMBER_131, NUMBER_145).append(getApiTypeId()).append(getLoadId()).append(getFieldName()).append(getNewValue())
                .append(getOldValue()).append(getBol()).append(getCarrierReferenceNumber()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof ApiExceptionEntity) {
            if (obj == this) {
                result = true;
            } else {
                ApiExceptionEntity other = (ApiExceptionEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getApiTypeId(), other.getApiTypeId()).append(getBol(), other.getBol())
                        .append(getCarrierReferenceNumber(), other.getCarrierReferenceNumber()).append(getLoadId(), other.getLoadId())
                        .append(getFieldName(), other.getFieldName()).append(getOldValue(), other.getOldValue())
                        .append(getNewValue(), other.getNewValue()).append(getModification(), other.getModification());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("apiTypeId", getApiTypeId()).append("loadId", getLoadId()).append("bol", getBol())
                .append("carrierReferenceNumber", getCarrierReferenceNumber()).append("fieldName", getFieldName()).append("oldValue", getOldValue())
                .append("newValue", getNewValue()).append("modification", getModification());

        return builder.toString();
    }

}
