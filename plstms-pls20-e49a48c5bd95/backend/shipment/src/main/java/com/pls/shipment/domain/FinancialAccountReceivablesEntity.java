package com.pls.shipment.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.pls.core.domain.Identifiable;

/**
 * Hibernate mapping for finance system integration for Customer part.
 *
 * @author Alexander Kirichenko
 */
@Entity
@Table(name = "FINAN_ACCOUNT_RECEIVABLES")
@Immutable  //Do not remove this annotation. We can't change data in this table within PLS PRO 2.0
public class FinancialAccountReceivablesEntity extends AbstractFinancialAccountableEntity implements Identifiable<Long> {

    private static final long serialVersionUID = -5201528623095002920L;

    public static final String Q_GET_CUST_INV_SUM_INFO = "com.pls.shipment.domain.FinancialAccountReceivablesEntity.Q_GET_CUST_INV_SUM_INFO";
    public static final String Q_GET_CUST_UNPAID_AMOUNT = "com.pls.shipment.domain.FinancialAccountReceivablesEntity.Q_GET_CUST_UNPAID_AMOUNT";
    public static final String Q_GET_CUSTOMER_INVOICES = "com.pls.shipment.domain.FinancialAccountReceivablesEntity.Q_GET_CUSTOMER_INVOICES";

    @Id
    @Column(name = "FINAN_AR_ID")
    private Long id;
    @OneToMany(mappedBy = "financialAccountReceivable", fetch = FetchType.LAZY)
    private Set<FinancialARDetailsEntity> details = new HashSet<FinancialARDetailsEntity>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Set<FinancialARDetailsEntity> getDetails() {
        return details;
    }

    public void setDetails(Set<FinancialARDetailsEntity> details) {
        this.details = details;
    }
}
