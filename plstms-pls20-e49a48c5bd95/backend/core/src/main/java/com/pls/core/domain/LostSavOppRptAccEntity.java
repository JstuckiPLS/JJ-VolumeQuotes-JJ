package com.pls.core.domain;

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
 * Lost Savings Opportunity Report Accessorial Entity. Contains the information related to selected
 * accessorial for a pricing proposal of a customer in certain time period. The list of accessorials for a
 * proposal would be concatenated and displayed against that proposal in the report.
 * 
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LOST_SAV_OPP_RPT_ACC")
public class LostSavOppRptAccEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LSO_RPT_ACCESSORIAL_ID_SEQ")
    @SequenceGenerator(name = "LSO_RPT_ACCESSORIAL_ID_SEQ", sequenceName = "LSO_RPT_ACCESSORIAL_ID_SEQ", allocationSize = 1)
    @Column(name = "LSO_RPT_ACCESSORIAL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LSO_RPT_DATA_ID", nullable = false)
    private LostSavOppRptDataEntity lostSavingsOppRpt;

    @Column(name = "ACCESSORIAL_TYPE", nullable = false)
    private String accType;

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

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

}
