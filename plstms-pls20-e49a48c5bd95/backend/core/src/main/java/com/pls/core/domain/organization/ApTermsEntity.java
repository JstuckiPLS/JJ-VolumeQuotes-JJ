package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * Entity for AP_TERMS.
 *
 * @author Hima Bindu Challa
 */
@Entity
@Table(name = "AP_TERMS")
public class ApTermsEntity implements Identifiable<Long> {
    private static final long serialVersionUID = -7426594079163935268L;

    @Id
    @Column(name = "TERM_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DUE_DAYS", nullable = false)
    private int dueDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDueDays() {
        return dueDays;
    }

    public void setDueDays(int dueDays) {
        this.dueDays = dueDays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
