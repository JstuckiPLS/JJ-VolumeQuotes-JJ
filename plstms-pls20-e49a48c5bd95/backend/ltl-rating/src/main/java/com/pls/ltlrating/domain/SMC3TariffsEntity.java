package com.pls.ltlrating.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.user.UserEntity;

/**
 * SMC3 tariffs entity.
 *
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "SMC3_TARIFFS")
public class SMC3TariffsEntity implements Identifiable<Long> {

    private static final long serialVersionUID = -7470193982581681964L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "smc3_tariffs_sequence")
    @SequenceGenerator(name = "smc3_tariffs_sequence", sequenceName = "SMC3_TARIFFS_SEQ", allocationSize = 1)
    @Column(name = "SMC3_TARIFF_ID", nullable = false)
    private Long id;

    @Column(name = "TARIFF_NAME", nullable = false)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", nullable = false, updatable = false)
    private UserEntity createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }
}
