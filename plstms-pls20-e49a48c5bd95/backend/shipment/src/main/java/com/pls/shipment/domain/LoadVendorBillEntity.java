package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

/**
 * Entity that contains fields for load and vendor bill mapping.
 * 
 * @author Aleksandr Leshchenko
 */
@Embeddable
public class LoadVendorBillEntity {
    @Column(name = "FRT_BILL_NUMBER")
    private String frtBillNumber;

    @Column(name = "FRT_BILL_RECV_DATE")
    private Date frtBillRecvDate;

    @Column(name = "FRT_BILL_AMOUNT")
    private BigDecimal frtBillAmount;

    @Column(name = "FRT_BILL_RECV_FLAG", nullable = false)
    @Type(type = "yes_no")
    private Boolean frtBillRecvFlag = false;

    @Column(name = "FRT_BILL_RECV_BY")
    private Long frtBillRecvBy;

    @OneToMany(mappedBy = "matchedLoadId", fetch = FetchType.LAZY)
    @Where(clause = "status = 'A'")
    private List<CarrierInvoiceDetailsEntity> carrierInvoiceDetails;

    public String getFrtBillNumber() {
        return frtBillNumber;
    }

    public void setFrtBillNumber(String frtBillNumber) {
        this.frtBillNumber = frtBillNumber;
    }

    public Date getFrtBillRecvDate() {
        return frtBillRecvDate;
    }

    public void setFrtBillRecvDate(Date frtBillRecvDate) {
        this.frtBillRecvDate = frtBillRecvDate;
    }

    public BigDecimal getFrtBillAmount() {
        return frtBillAmount;
    }

    public void setFrtBillAmount(BigDecimal frtBillAmount) {
        this.frtBillAmount = frtBillAmount;
    }

    public Boolean getFrtBillRecvFlag() {
        return frtBillRecvFlag;
    }

    public void setFrtBillRecvFlag(Boolean frtBillRecvFlag) {
        this.frtBillRecvFlag = frtBillRecvFlag;
    }

    public Long getFrtBillRecvBy() {
        return frtBillRecvBy;
    }

    public void setFrtBillRecvBy(Long frtBillRecvBy) {
        this.frtBillRecvBy = frtBillRecvBy;
    }

    public List<CarrierInvoiceDetailsEntity> getCarrierInvoiceDetails() {
        return carrierInvoiceDetails;
    }

    public void setCarrierInvoiceDetails(List<CarrierInvoiceDetailsEntity> carrierInvoiceDetails) {
        this.carrierInvoiceDetails = carrierInvoiceDetails;
    }
}