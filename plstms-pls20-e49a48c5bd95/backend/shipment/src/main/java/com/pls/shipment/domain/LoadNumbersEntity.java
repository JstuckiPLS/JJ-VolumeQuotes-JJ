package com.pls.shipment.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * Entity that contains different numbers fields for load.
 * 
 * @author Aleksandr Leshchenko
 */
@Embeddable
public class LoadNumbersEntity {
    @Column(name = "BOL", nullable = true)
    private String bolNumber;

    @Column(name = "GL_NUMBER")
    private String glNumber;

    @Column(name = "OP_BOL")
    private String opBolNumber;

    @Column(name = "PART_NUM")
    private String partNumber;

    @Column(name = "PO_NUM", nullable = true)
    private String poNumber;

    @Column(name = "CARRIER_REFERENCE_NUMBER", nullable = true)
    private String proNumber;

    @Column(name = "PICKUP_NUM", nullable = true)
    private String puNumber;

    @Column(name = "SHIPPER_REFERENCE_NUMBER", nullable = true)
    private String refNumber;

    @Column(name = "SO_NUMBER")
    private String soNumber;

    @Column(name = "TRAILER")
    private String trailerNumber;
    
    @Column(name = "CARRIER_QUOTE_NUMBER")
    private String carrierQuoteNumber;
    
    /** In case of LTLLC quote, the service level (e.g. ACCELERATED, EXPEDITE, CAPLOAD), which needs to be used when dispatching */
    @Column(name = "SERVICE_LEVEL_CODE")
    private String serviceLevelCode;
    
    /** In case of LTLLC quote, the service level (e.g. ACCELERATED, EXPEDITE, CAPLOAD) description, which needs to be used when generating bol */
    @Column(name = "SERVICE_LEVEL_DESC")
    private String serviceLevelDescription;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LOAD_ID")
    @OrderBy("id")
    private Set<LoadJobNumbersEntity> jobNumbers;

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public String getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }

    public String getOpBolNumber() {
        return opBolNumber;
    }

    public void setOpBolNumber(String opBolNumber) {
        this.opBolNumber = opBolNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getProNumber() {
        return proNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public String getPuNumber() {
        return puNumber;
    }

    public void setPuNumber(String puNumber) {
        this.puNumber = puNumber;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }

    public void setTrailerNumber(String trailerNumber) {
        this.trailerNumber = trailerNumber;
    }

    public Set<LoadJobNumbersEntity> getJobNumbers() {
        return jobNumbers;
    }

    public void setJobNumbers(Set<LoadJobNumbersEntity> jobNumbers) {
        this.jobNumbers = jobNumbers;
    }

    public String getCarrierQuoteNumber() {
        return carrierQuoteNumber;
    }

    public void setCarrierQuoteNumber(String carrierQuoteNumber) {
        this.carrierQuoteNumber = carrierQuoteNumber;
    }

    public String getServiceLevelCode() {
        return serviceLevelCode;
    }

    public void setServiceLevelCode(String serviceLevelCode) {
        this.serviceLevelCode = serviceLevelCode;
    }

    public String getServiceLevelDescription() {
        return serviceLevelDescription;
    }

    public void setServiceLevelDescription(String serviceLevelDescription) {
        this.serviceLevelDescription = serviceLevelDescription;
    }

}
