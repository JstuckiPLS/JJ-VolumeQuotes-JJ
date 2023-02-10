package com.pls.core.domain.organization;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.ShipmentDirection;

/**
 * Entity for billTo default values.
 * 
 * @author Davydenko Dmitriy
 */
@Entity
@Table(name = "BILL_TO_DEFAULT_VALUES")
public class BillToDefaultValuesEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = -9149156378733546336L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_to_sequence")
    @SequenceGenerator(name = "bill_to_sequence", sequenceName = "BILL_TO_DEFAULT_VALUES_SEQ", allocationSize = 1)
    @Column(name = "BILL_TO_DEFAULT_VALUE_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO_ID", nullable = false)
    private BillToEntity billTo;

    @Column(name = "BILL_TO_ID", insertable = false, updatable = false)
    private Long billToId;

    @Column(name = "INBOUND_OUTBOUND")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentDirection"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode")})
    private ShipmentDirection direction;

    @Column(name = "EDI_INBOUND_OUTBOUND")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentDirection"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode")})
    private ShipmentDirection ediDirection;

    @Column(name = "MANUAL_BOL_INBOUND_OUTBOUND")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentDirection"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentDirection manualBolDirection;

    @Column(name = "PAY_TERMS")
    @Enumerated(EnumType.STRING)
    private PaymentTerms payTerms;

    @Column(name = "EDI_PAY_TERMS")
    @Enumerated(EnumType.STRING)
    private PaymentTerms ediPayTerms;

    @Column(name = "MANUAL_BOL_PAY_TERMS")
    @Enumerated(EnumType.STRING)
    private PaymentTerms manualBolPayTerms;

    @Column(name = "EDI_CUSTOMS_BROKER")
    private String ediCustomsBroker;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "EDI_BROKER_PHONE_ID")
    private PhoneEntity ediCustomsBrokerPhone;

    @Column(name = "VERSION")
    @Version
    private Integer version = 1;

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

    public ShipmentDirection getDirection() {
        return direction;
    }

    public void setDirection(ShipmentDirection direction) {
        this.direction = direction;
    }

    public ShipmentDirection getEdiDirection() {
        return ediDirection;
    }

    public void setEdiDirection(ShipmentDirection ediDirection) {
        this.ediDirection = ediDirection;
    }

    public PaymentTerms getPayTerms() {
        return payTerms;
    }

    public void setPayTerms(PaymentTerms payTerms) {
        this.payTerms = payTerms;
    }

    public PaymentTerms getEdiPayTerms() {
        return ediPayTerms;
    }

    public void setEdiPayTerms(PaymentTerms ediPayTerms) {
        this.ediPayTerms = ediPayTerms;
    }

    public Integer getVersion() {
        return version;
    }

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    public ShipmentDirection getManualBolDirection() {
        return manualBolDirection;
    }

    public void setManualBolDirection(ShipmentDirection manualBolDirection) {
        this.manualBolDirection = manualBolDirection;
    }

    public PaymentTerms getManualBolPayTerms() {
        return manualBolPayTerms;
    }

    public void setManualBolPayTerms(PaymentTerms manualBolPayTerms) {
        this.manualBolPayTerms = manualBolPayTerms;
    }

    public String getEdiCustomsBroker() {
        return ediCustomsBroker;
    }

    public void setEdiCustomsBroker(String ediCustomsBroker) {
        this.ediCustomsBroker = ediCustomsBroker;
    }

    public PhoneEntity getEdiCustomsBrokerPhone() {
        return ediCustomsBrokerPhone;
    }

    public void setEdiCustomsBrokerPhone(PhoneEntity ediCustomsBrokerPhone) {
        this.ediCustomsBrokerPhone = ediCustomsBrokerPhone;
    }
}
