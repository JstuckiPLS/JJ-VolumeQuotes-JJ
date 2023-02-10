package com.pls.shipment.domain;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.PlainModificationObject;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Base class for financial accountable details entities.
 *
 * @author Alexander Kirichenko
 */
@MappedSuperclass
public class AbstractFinancialAccountableDetailsEntity implements Serializable, HasModificationInfo {

    private static final long serialVersionUID = -816498381300685528L;

    @Column(name = "AMT_APPLIED")
    private BigDecimal amountApplied;

    @Column(name = "CHECK_NUM")
    private String checkNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CHECK_DATE")
    private Date checkDate;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public BigDecimal getAmountApplied() {
        return amountApplied;
    }

    public void setAmountApplied(BigDecimal amountApplied) {
        this.amountApplied = amountApplied;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }
}
