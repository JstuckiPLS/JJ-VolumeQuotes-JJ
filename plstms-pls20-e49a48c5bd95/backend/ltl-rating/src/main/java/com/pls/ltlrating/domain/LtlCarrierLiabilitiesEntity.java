package com.pls.ltlrating.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CommodityClass;

/**
 * Ltl Carrier Liabilities.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(schema = "FLATBED", name = "LTL_CARRIER_LIABILITIES")
public class LtlCarrierLiabilitiesEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -6069210557148863372L;

    public static final String DELETE_FOR_PROFILE = "com.pls.ltlrating.domain.GroupCapabilitiesEntity.DELETE_FOR_PROFILE";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_CARRIER_LIABILITIES_SEQUENCE")
    @SequenceGenerator(name = "LTL_CARRIER_LIABILITIES_SEQUENCE", sequenceName = "LTL_CARRIER_LIABILITIES_SEQ", allocationSize = 1)
    @Column(name = "LTL_CARRIER_LIABILITY_ID")
    private Long id;

    @Column(name = "LTL_PRICING_PROFILE_ID")
    private Long pricingProfileId;

    @Column(name = "CLASS")
    @Type(type = "com.pls.core.domain.usertype.CommodityClassUserType")
    private CommodityClass freightClass;

    @Column(name = "NEW_PROD_LIAB_AMT")
    private BigDecimal newProdLiabAmt;

    @Column(name = "USED_PROD_LIAB_AMT")
    private BigDecimal usedProdLiabAmt;

    @Column(name = "MAX_NEW_PROD_LIAB_AMT")
    private BigDecimal maxNewProdLiabAmt;

    @Column(name = "MAX_USED_PROD_LIAB_AMT")
    private BigDecimal maxUsedProdLiabAmt;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPricingProfileId() {
        return pricingProfileId;
    }

    public void setPricingProfileId(Long pricingProfileId) {
        this.pricingProfileId = pricingProfileId;
    }

    public CommodityClass getFreightClass() {
        return freightClass;
    }

    public void setFreightClass(CommodityClass freightClass) {
        this.freightClass = freightClass;
    }

    public BigDecimal getNewProdLiabAmt() {
        return newProdLiabAmt;
    }

    public void setNewProdLiabAmt(BigDecimal newProdLiabAmt) {
        this.newProdLiabAmt = newProdLiabAmt;
    }

    public BigDecimal getUsedProdLiabAmt() {
        return usedProdLiabAmt;
    }

    public void setUsedProdLiabAmt(BigDecimal usedProdLiabAmt) {
        this.usedProdLiabAmt = usedProdLiabAmt;
    }

    public BigDecimal getMaxNewProdLiabAmt() {
        return maxNewProdLiabAmt;
    }

    public void setMaxNewProdLiabAmt(BigDecimal maxNewProdLiabAmt) {
        this.maxNewProdLiabAmt = maxNewProdLiabAmt;
    }

    public BigDecimal getMaxUsedProdLiabAmt() {
        return maxUsedProdLiabAmt;
    }

    public void setMaxUsedProdLiabAmt(BigDecimal maxUsedProdLiabAmt) {
        this.maxUsedProdLiabAmt = maxUsedProdLiabAmt;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                    .append(getPricingProfileId())
                    .append(getFreightClass())
                    .append(getMaxNewProdLiabAmt())
                    .append(getMaxUsedProdLiabAmt())
                    .append(getNewProdLiabAmt())
                    .append(getUsedProdLiabAmt())
                    .append(getModification()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof LtlCarrierLiabilitiesEntity) {
            if (obj == this) {
                result = true;
            } else {
                LtlCarrierLiabilitiesEntity rhs = (LtlCarrierLiabilitiesEntity) obj;

                result = new EqualsBuilder()
                            .append(getPricingProfileId(), rhs.getPricingProfileId())
                            .append(getFreightClass(), rhs.getFreightClass())
                            .append(getMaxNewProdLiabAmt(), rhs.getMaxNewProdLiabAmt())
                            .append(getMaxUsedProdLiabAmt(), rhs.getMaxUsedProdLiabAmt())
                            .append(getNewProdLiabAmt(), rhs.getNewProdLiabAmt())
                            .append(getUsedProdLiabAmt(), rhs.getUsedProdLiabAmt())
                            .append(getModification(), rhs.getModification())
                            .isEquals();
            }
        }

        return result;
    }
}
