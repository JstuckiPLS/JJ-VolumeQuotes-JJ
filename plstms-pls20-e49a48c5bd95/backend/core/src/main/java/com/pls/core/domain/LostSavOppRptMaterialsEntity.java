package com.pls.core.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Lost Savings Opportunity Report Materials Entity. Contains the information related to material selected for
 * a pricing proposal of a customer in certain time period. The list of material's weight and commodity class
 * type for a proposal would be displayed against that proposal in report.
 * 
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LOST_SAV_OPP_RPT_MATERIALS")
public class LostSavOppRptMaterialsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LSO_RPT_MATERIAL_ID_SEQ")
    @SequenceGenerator(name = "LSO_RPT_MATERIAL_ID_SEQ", sequenceName = "LSO_RPT_MATERIAL_ID_SEQ", allocationSize = 1)
    @Column(name = "LSO_RPT_MATERIAL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LSO_RPT_DATA_ID", nullable = false)
    private LostSavOppRptDataEntity lostSavingsOppRpt;

    @Column(name = "WEIGHT", nullable = false)
    private BigDecimal weight;

    @Column(name = "COMMODITY_CLASS_CODE", nullable = false)
    private String classType;

    @Column(name = "HAZMAT_FLAG")
    private char hazmatFlag;

    @Column(name = "DATE_CREATED")
    private Date dateCreated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LostSavOppRptDataEntity getLostSavingsOppRpt() {
        return lostSavingsOppRpt;
    }

    public void setLostSavingsOppRpt(LostSavOppRptDataEntity lostSavingsOppRpt) {
        this.lostSavingsOppRpt = lostSavingsOppRpt;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public char getHazmatFlag() {
        return hazmatFlag;
    }

    public void setHazmatFlag(char hazmatFlag) {
        this.hazmatFlag = hazmatFlag;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

}
