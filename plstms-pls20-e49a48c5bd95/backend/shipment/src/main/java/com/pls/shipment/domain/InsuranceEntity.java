package com.pls.shipment.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.pls.core.domain.Identifiable;

/**
 * Represents object of Falvey insurance coverage information for GoShip load
 *
 * @author Phil Maise
 */
@Entity
@Table(name = "INSURANCE")
public class InsuranceEntity implements Identifiable<Long> {

    public static final String Q_GET_INSURANCE_BY_LOAD_ID = "com.pls.shipment.domain.InsuranceEntity.Q_GET_INSURANCE_BY_LOAD_ID";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ins_sequence")
    @SequenceGenerator(name = "ins_sequence", sequenceName = "INS_SEQ", allocationSize = 1)
    @Column(name = "INSURANCE_ID", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity load;

    @Column(name = "FALVEY_SHIPMENT_ID", nullable = false)
    private int falveyShipmentID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SHIPMENT_DATE", nullable = false)
    private Date shipmentDate;

    @Column(name = "SUM_INSURED", nullable = false)
    private String shippingSumInsured;

    @Column(name = "SUM_INSURED_WORDS")
    private String shippingSumInsuredWords;

    @Column(name = "CURRENCY")
    private String shippingCurrency;

    @Column(name = "PREMIUM", nullable = false)
    private double premium;

    @Column(name = "CERTIFICATE_NUMBER", nullable = false)
    private String certificateNumber;

    @Column(name = "POLICY_NUMBER", nullable = false)
    private String policyNumber;

    @Column(name = "WAY_BILL_ADDITIONAL")
    private String wayBillAdditional;

    @Column(name = "SHIPMENT_UUID")
    private String shipmentUUID;

    @Transient
    private String certificatePDF;

    @Column(name = "RATE")
    private String rate;

    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public int getFalveyShipmentID() {
        return falveyShipmentID;
    }

    public void setFalveyShipmentID(int falveyShipmentID) {
        this.falveyShipmentID = falveyShipmentID;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getShippingSumInsured() {
        return shippingSumInsured;
    }

    public void setShippingSumInsured(String shippingSumInsured) {
        this.shippingSumInsured = shippingSumInsured;
    }

    public String getShippingSumInsuredWords() {
        return shippingSumInsuredWords;
    }

    public void setShippingSumInsuredWords(String shippingSumInsuredWords) {
        this.shippingSumInsuredWords = shippingSumInsuredWords;
    }

    public String getShippingCurrency() {
        return shippingCurrency;
    }

    public void setShippingCurrency(String shippingCurrency) {
        this.shippingCurrency = shippingCurrency;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getWayBillAdditional() {
        return wayBillAdditional;
    }

    public void setWayBillAdditional(String wayBillAdditional) {
        this.wayBillAdditional = wayBillAdditional;
    }

    public String getShipmentUUID() {
        return shipmentUUID;
    }

    public void setShipmentUUID(String shipmentUUID) {
        this.shipmentUUID = shipmentUUID;
    }

    public String getCertificatePDF() {
        return certificatePDF;
    }

    public void setCertificatePDF(String certificatePDF) {
        this.certificatePDF = certificatePDF;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
