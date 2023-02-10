package com.pls.core.domain.organization;


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
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.AddressPriority;
import com.pls.core.domain.enums.DefaultValuesAction;
import com.pls.core.domain.enums.RequiredFieldPointType;
import com.pls.core.domain.enums.RequiredFieldShipmentDirection;
import com.pls.core.shared.BillToRequiredField;
import com.pls.core.shared.Status;

/**
 * Entity for billTo required fields.
 * 
 * @author Brichak Aleksandr
 */
@Entity
@Table(name = "BILL_TO_REQ_FIELD")
public class BillToRequiredFieldEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = 316534915516290245L;

    public static final String Q_CREATE_REQUIRED_FIELD = "com.pls.core.domain.organization.BillToEntity.Q_CREATE_REQUIRED_FIELD";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_to_sequence")
    @SequenceGenerator(name = "bill_to_sequence", sequenceName = "BILL_TO_REQ_FIELD_SEQ", allocationSize = 1)
    @Column(name = "BILL_TO_REQ_FIELD_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;

    @Column(name = "FIELD_NAME")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.shared.BillToRequiredField"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode")})
    private BillToRequiredField fieldName;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "DIRECTION")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.RequiredFieldShipmentDirection"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private RequiredFieldShipmentDirection shipmentDirection = RequiredFieldShipmentDirection.BOTH;

    @Column(name = "ZIP")
    private String zip;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    @Column(name = "REQUIRED")
    @Type(type = "yes_no")
    private Boolean required;

    @Column(name = "ADDRESS_DIRECTION")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.RequiredFieldPointType"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode")})
    private RequiredFieldPointType addressDirection = RequiredFieldPointType.BOTH;

    @Column(name = "START_WITH")
    private String startWith;

    @Column(name = "END_WITH")
    private String endWith;

    @Column(name = "VERSION")
    private Integer version = 1;

    @Column(name = "ACTION")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.DefaultValuesAction"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private DefaultValuesAction actionForDefaultValues;

    @Column(name = "RULE_EXP")
    private String ruleExp;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    public BillToRequiredField getFieldName() {
        return fieldName;
    }

    public void setFieldName(BillToRequiredField fieldName) {
        this.fieldName = fieldName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public RequiredFieldShipmentDirection getShipmentDirection() {
        return shipmentDirection;
    }

    public void setShipmentDirection(RequiredFieldShipmentDirection shipmentDirection) {
        this.shipmentDirection = shipmentDirection;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public RequiredFieldPointType getAddressDirection() {
        return addressDirection;
    }

    public void setAddressDirection(RequiredFieldPointType addressDirection) {
        this.addressDirection = addressDirection;
    }

    public String getStartWith() {
        return startWith;
    }

    public void setStartWith(String startWith) {
        this.startWith = startWith;
    }

    public String getEndWith() {
        return endWith;
    }

    public void setEndWith(String endWith) {
        this.endWith = endWith;
    }

    public DefaultValuesAction getActionForDefaultValues() {
        return this.actionForDefaultValues;
    }

    public void setActionForDefaultValues(DefaultValuesAction actionForDefaultValues) {
        this.actionForDefaultValues = actionForDefaultValues;
    }

    public String getRuleExp() {
        return ruleExp;
    }

    public void setRuleExp(String ruleExp) {
        this.ruleExp = ruleExp;
    }

    @Transient
    private AddressPriority addressPriority;

    public AddressPriority getMatchedBy() {
        return addressPriority;
    }

    public void setAddressPriority(AddressPriority addressPriority) {
        this.addressPriority = addressPriority;
    }
}
