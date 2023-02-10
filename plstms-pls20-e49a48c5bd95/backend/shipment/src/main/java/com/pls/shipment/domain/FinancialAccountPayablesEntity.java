package com.pls.shipment.domain;

import com.pls.core.domain.Identifiable;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Hibernate mapping for finance system integration for Carrier part.
 *
 * @author Alexander Kirichenko
 */
@Entity
@Table(name = "FINAN_ACCOUNT_PAYABLES")
@Immutable  //Do not remove this annotation. We can't change data in this table within PLS PRO 2.0
public class FinancialAccountPayablesEntity extends AbstractFinancialAccountableEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 2303451366591991423L;
    @Id
    @Column(name = "FINAN_AP_ID")
    private Long id;
    @OneToMany(mappedBy = "financialAccountPayable", fetch = FetchType.LAZY)
    private Set<FinancialAPDetailsEntity> details = new HashSet<FinancialAPDetailsEntity>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Set<FinancialAPDetailsEntity> getDetails() {
        return details;
    }

    public void setDetails(Set<FinancialAPDetailsEntity> details) {
        this.details = details;
    }
}
