package com.pls.shipment.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Load Additional fields Entity.
 * 
 * @author Alexander Nalapko
 *
 */

@Entity
@Table(name = "LOAD_ADDITIONAL_FIELDS")
public class LoadAdditionalFieldsEntity implements Identifiable<Long>, HasVersion, HasModificationInfo {

    private static final long serialVersionUID = -1887180897953823087L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_additional_fields_sequence")
    @SequenceGenerator(name = "load_additional_fields_sequence", sequenceName = "LOAD_ADDITIONAL_FIELDS_SEQ", allocationSize = 1)
    @Column(name = "LOAD_ADDITIONAL_FIELDS_ID", nullable = false)
    private Long id;

    @Column(name = "CARGO_VALUE")
    private BigDecimal cargoValue;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version = 1;

    @OneToOne
    @JoinColumn(name = "LOAD_ID", referencedColumnName = "LOAD_ID")
    private LoadEntity load;

    @OneToOne
    @JoinColumn(name = "MANUAL_BOL_ID", referencedColumnName = "MANUAL_BOL_ID")
    private ManualBolEntity manualBol;
    
    @Column(name = "DISPATCHED_VIA")
    private String dispatchedVia;
    
    @Column(name = "TRACKED_VIA")
    private String trackedVia;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCargoValue() {
        return cargoValue;
    }

    public void setCargoValue(BigDecimal cargoValue) {
        this.cargoValue = cargoValue;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public ManualBolEntity getManualBol() {
        return manualBol;
    }

    public void setManualBol(ManualBolEntity manualBol) {
        this.manualBol = manualBol;
    }

    public String getDispatchedVia() {
        return dispatchedVia;
    }

    public void setDispatchedVia(String dispatchedVia) {
        this.dispatchedVia = dispatchedVia;
    }

    public String getTrackedVia() {
        return trackedVia;
    }

    public void setTrackedVia(String trackedVia) {
        this.trackedVia = trackedVia;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

}
