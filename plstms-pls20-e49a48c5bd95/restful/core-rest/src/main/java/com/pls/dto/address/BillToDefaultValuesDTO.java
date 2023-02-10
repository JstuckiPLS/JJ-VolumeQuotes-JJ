package com.pls.dto.address;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.PaymentTerms;

/**
 * DTO for BillToDefaultValuesEntity.
 * 
 * @author Davydenko Dmitriy
 *
 */
public class BillToDefaultValuesDTO {

    private String direction;

    private String ediDirection;

    private String manualBolDirection;

    private PaymentTerms payTerms;

    private PaymentTerms ediPayTerms;

    private PaymentTerms manualBolPayTerms;

    private PhoneBO ediCustomsBrokerPhone;

    private String ediCustomsBroker;

    private Long id;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getEdiDirection() {
        return ediDirection;
    }

    public void setEdiDirection(String ediDirection) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManualBolDirection() {
        return manualBolDirection;
    }

    public void setManualBolDirection(String manualBolDirection) {
        this.manualBolDirection = manualBolDirection;
    }

    public PaymentTerms getManualBolPayTerms() {
        return manualBolPayTerms;
    }

    public void setManualBolPayTerms(PaymentTerms manualBolPayTerms) {
        this.manualBolPayTerms = manualBolPayTerms;
    }

    public PhoneBO getEdiCustomsBrokerPhone() {
        return ediCustomsBrokerPhone;
    }

    public void setEdiCustomsBrokerPhone(PhoneBO ediCustomsBrokerPhone) {
        this.ediCustomsBrokerPhone = ediCustomsBrokerPhone;
    }

    public String getEdiCustomsBroker() {
        return ediCustomsBroker;
    }

    public void setEdiCustomsBroker(String ediCustomsBroker) {
        this.ediCustomsBroker = ediCustomsBroker;
    }
}