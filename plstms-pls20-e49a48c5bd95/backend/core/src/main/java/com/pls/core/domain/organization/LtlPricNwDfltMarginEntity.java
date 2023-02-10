package com.pls.core.domain.organization;

import java.math.BigDecimal;

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
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * This class contains default margin percentage and dollar amount values for various networks.
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LTL_PRIC_NW_DFLT_MARGIN")
public class LtlPricNwDfltMarginEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 615610395789375205L;

    public static final String Q_BY_NETWORK_ID = "com.pls.core.domain.organization.LtlPricNwDfltMarginEntity.Q_BY_NETWORK_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pric_dflt_mrgn_seq")
    @SequenceGenerator(name = "ltl_pric_dflt_mrgn_seq", sequenceName = "LTL_PRIC_NW_DFLT_MRGN_SEQ", allocationSize = 1)
    @Column(name = "NW_DFLT_MRGN_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NETWORK_ID")
    private NetworkEntity nwEntity;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "MARGIN_PERC")
    private BigDecimal marginPerc;

    @Column(name = "MIN_MARGIN_AMT")
    private BigDecimal minMarginAmt;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    private PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NetworkEntity getNwEntity() {
        return nwEntity;
    }

    public void setNwEntity(NetworkEntity nwEntity) {
        this.nwEntity = nwEntity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getMarginPerc() {
        return marginPerc;
    }

    public void setMarginPerc(BigDecimal marginPerc) {
        this.marginPerc = marginPerc;
    }

    public BigDecimal getMinMarginAmt() {
        return minMarginAmt;
    }

    public void setMinMarginAmt(BigDecimal minMarginAmt) {
        this.minMarginAmt = minMarginAmt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject modification) {
        this.modification = modification;
    }

}
