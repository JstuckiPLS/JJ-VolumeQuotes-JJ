package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Load Dispatch Information Entity.
 * 
 * @author Brichak Aleksandr
 *
 */

@Entity
@Table(name = "LOAD_DISPATCH_INFORMATION")
@Immutable
public class LoadDispatchInformationEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -8770162873393806227L;

    @Id
    @Column(name = "LOAD_DISPATCH_INFO_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity load;

    @Column(name = "STATUS")
    private String status;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }
}

