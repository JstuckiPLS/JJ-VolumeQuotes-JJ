package com.pls.shipment.domain;

import com.pls.core.domain.Identifiable;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Hibernate mapping for finance system integration for Customer part(details).
 *
 * @author Alexander Kirichenko
 */
@Entity
@Table(name = "FINAN_AR_DETAILS")
@Immutable  //Do not remove this annotation. We can't change data in this table within PLS PRO 2.0
public class FinancialARDetailsEntity extends AbstractFinancialAccountableDetailsEntity implements Identifiable<Long> {

    private static final long serialVersionUID = -6592932419174698469L;
    @Id
    @Column(name = "FINAN_AR_DETAIL_ID")
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "FINAN_AR_ID")
    private FinancialAccountReceivablesEntity financialAccountReceivable;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public FinancialAccountReceivablesEntity getFinancialAccountReceivable() {
        return financialAccountReceivable;
    }

    public void setFinancialAccountReceivable(FinancialAccountReceivablesEntity financialAccountReceivable) {
        this.financialAccountReceivable = financialAccountReceivable;
    }
}
