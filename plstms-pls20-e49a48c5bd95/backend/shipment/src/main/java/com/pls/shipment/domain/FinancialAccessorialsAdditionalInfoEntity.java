package com.pls.shipment.domain;

import javax.persistence.CascadeType;
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
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Additional adjustments information.
 * 
 * @author Dmitry Nikolaenko
 *
 */
@Entity
@Table(name = "FINAN_ADJ_ACC_DETAIL_ADDL_INFO")
public class FinancialAccessorialsAdditionalInfoEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 6109156954202682000L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "finan_adj_acc_detail_addl_sequence")
    @SequenceGenerator(name = "finan_adj_acc_detail_addl_sequence", sequenceName = "FINAN_ADJ_ACC_DETAIL_ADDL_SEQ", allocationSize = 1)
    @Column(name = "FAA_DETAIL_ADDL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FAA_DETAIL_ID")
    private FinancialAccessorialsEntity financialAccessorials;

    @Column(name = "PO_NUM", nullable = true)
    private String poNumber;

    @Column(name = "SHIPPER_REFERENCE_NUMBER", nullable = true)
    private String refNumber;

    @Column(name = "SO_NUMBER")
    private String soNumber;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
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

    public FinancialAccessorialsEntity getFinancialAccessorials() {
        return financialAccessorials;
    }

    public void setFinancialAccessorials(FinancialAccessorialsEntity financialAccessorials) {
        this.financialAccessorials = financialAccessorials;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
