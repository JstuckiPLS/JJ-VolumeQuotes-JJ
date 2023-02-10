package com.pls.invoice.domain.bo;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.shipment.domain.LoadEntity;

/**
 * Object that contains necessary for Invoice Audit data.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class InvoiceAuditBO {
    private LoadEntity load;
    private BigDecimal minAcceptMargin;
    private Long adjustmentId;

    /**
     * Constructor.
     *
     * @param load shipment that need to be shown at invoice audit
     * @param minAcceptMargin minimal acceptable margin
     */
    public InvoiceAuditBO(LoadEntity load, BigDecimal minAcceptMargin) {
        this.load = load;
        this.minAcceptMargin = minAcceptMargin;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public BigDecimal getMinAcceptMargin() {
        return minAcceptMargin;
    }

    public void setMinAcceptMargin(BigDecimal minAcceptMargin) {
        this.minAcceptMargin = minAcceptMargin;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.load).append(this.minAcceptMargin).append(this.adjustmentId).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InvoiceAuditBO other = (InvoiceAuditBO) obj;
        return new EqualsBuilder().append(this.load, other.load).append(this.minAcceptMargin, other.minAcceptMargin).append(this.adjustmentId,
                other.adjustmentId).isEquals();
    }
}
