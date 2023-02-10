package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.pls.core.domain.Identifiable;

/**
 * Hibernate mapping for finance system integration for Carrier part(details).
 *
 * @author Alexander Kirichenko
 */
@Entity
@Table(name = "FINAN_AP_DETAILS")
@Immutable  //Do not remove this annotation. We can't change data in this table within PLS PRO 2.0
public class FinancialAPDetailsEntity extends AbstractFinancialAccountableDetailsEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 5480370478913337410L;

    @Id
    @Column(name = "FINAN_AP_DETAIL_ID")
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "FINAN_AP_ID")
    private FinancialAccountPayablesEntity financialAccountPayable;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public FinancialAccountPayablesEntity getFinancialAccountPayable() {
        return financialAccountPayable;
    }

    public void setFinancialAccountPayable(FinancialAccountPayablesEntity financialAccountPayable) {
        this.financialAccountPayable = financialAccountPayable;
    }
}
