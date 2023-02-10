package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.pls.core.domain.Identifiable;

/**
 * Entity for PLS_CUSTOMER_TERMS.
 *
 * @author Aleksandr Leshchenko
 */
@Entity
@Immutable
@Table(name = "PLS_CUSTOMER_TERMS")
public class PlsCustomerTermsEntity implements Identifiable<Long> {
    private static final long serialVersionUID = -7426594079163935268L;

    public static final String Q_GET_ALL = "com.pls.core.domain.organization.PlsCustomerTermsEntity.Q_GET_ALL";

    @Id
    @Column(name = "TERM_ID")
    private Long id;

    @Column(name = "TERM_NAME")
    private String termName;

    @Column(name = "DUE_DAYS")
    private int dueDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public int getDueDays() {
        return dueDays;
    }

    public void setDueDays(int dueDays) {
        this.dueDays = dueDays;
    }
}
