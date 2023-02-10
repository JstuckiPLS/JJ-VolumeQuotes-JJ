package com.pls.shipment.domain;

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

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.Identifiable;

/**
 * LtlLoadAccessorial.
 *
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "LOADS_LTL_ACCESSORIALS")
public class LtlLoadAccessorialEntity implements Identifiable<Long> {

    private static final long serialVersionUID = -1042767922368215484L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOADS_LTL_ACCESSORIALS_SEQ")
    @SequenceGenerator(name = "LOADS_LTL_ACCESSORIALS_SEQ", sequenceName = "LOADS_LTL_ACCESSORIALS_SEQ",
            allocationSize = 1)
    @Column(name = "LTL_ACCESSORIAL_ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity load;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REF_TYPE", nullable = false)
    private AccessorialTypeEntity accessorial;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public AccessorialTypeEntity getAccessorial() {
        return accessorial;
    }

    public void setAccessorial(AccessorialTypeEntity accessorial) {
        this.accessorial = accessorial;
    }
}
