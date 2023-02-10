package com.pls.core.domain.organization;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Entity for billTo threshold settings.
 *
 * @author Brichak Aleksandr
 *
 */
@Entity
@Table(name = "BILL_TO_THRESHOLD_SETTINGS")
public class BillToThresholdSettingsEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = 472234975536890245L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_to_audit_threshold_sequence")
    @SequenceGenerator(name = "bill_to_audit_threshold_sequence", sequenceName = "BILL_TO_AUDIT_THRESHOLD_SEQ", allocationSize = 1)
    @Column(name = "BILL_TO_AUDIT_THRESHOLD_ID")
    private Long id;

    @Column(name = "BILL_TO_ID", insertable = false, updatable = false)
    private Long billToId;

    @Column(name = "THRESHOLD_VALUE")
    private BigDecimal threshold;

    @Column(name = "MARGIN", nullable = false)
    private BigDecimal margin;

    @Column(name = "TOTAL_REVENUE", nullable = false)
    private BigDecimal totalRevenue;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;

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

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

}
