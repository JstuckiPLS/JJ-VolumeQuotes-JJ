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
 * Entity that contains different numbers fields for Manual BOL.
 * 
 * @author Aleksandr Leshchenko
 */
@Embeddable
public class ManualBolNumbersEntity {
    @Column(name = "BOL", nullable = true)
    private String bolNumber;

    @Column(name = "GL_NUMBER")
    private String glNumber;

    @Column(name = "OP_BOL")
    private String opBolNumber;

    @Column(name = "PART_NUM")
    private String partNumber;

    @Column(name = "PO_NUMBER", nullable = true)
    private String poNumber;

    @Column(name = "PRO_NUMBER", nullable = true)
    private String proNumber;

    @Column(name = "PU_NUMBER", nullable = true)
    private String puNumber;

    @Column(name = "SHIPPER_REF_NUM", nullable = true)
    private String refNumber;

    @Column(name = "SO_NUMBER")
    private String soNumber;

    @Column(name = "trailer_num")
    private String trailerNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MANUAL_BOL_ID")
    @OrderBy("id")
    private Set<ManualBolJobNumberEntity> jobNumbers;

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

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
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

    public Set<ManualBolJobNumberEntity> getJobNumbers() {
        return jobNumbers;
    }

    public void setJobNumbers(Set<ManualBolJobNumberEntity> jobNumbers) {
        this.jobNumbers = jobNumbers;
    }
}
