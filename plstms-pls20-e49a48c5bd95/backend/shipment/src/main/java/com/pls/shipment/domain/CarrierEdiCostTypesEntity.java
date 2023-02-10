package com.pls.shipment.domain;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.CarrierEntity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Carrier Edi Cost Types Entity.
 *
 * @author Alexander Kirichenko
 */
@Entity
@Table(name = "CARR_EDI_COST_TYPES")
public class CarrierEdiCostTypesEntity implements Identifiable<Long>, HasModificationInfo {

    public static final String DEFAULT_ACC_TYPE = "MS";

    public static final String Q_ACC_TYPE_BY_CARRIER_EDI_COST_TYPE =
            "com.pls.shipment.domain.CarrierEdiCostTypesEntity.Q_ACC_TYPE_BY_CARRIER_EDI_COST_TYPE";
    public static final String Q_MAP_FOR_CARRIER = "com.pls.shipment.domain.CarrierEdiCostTypesEntity.Q_MAP_FOR_CARRIER";

    private static final long serialVersionUID = 4644940242545404615L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrier_edi_cost_types_sequence")
    @SequenceGenerator(name = "carrier_edi_cost_types_sequence", sequenceName = "CARR_EDI_CST_TP_SEQ", allocationSize = 1)
    @Column(name = "CARR_EDI_COST_TYPE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARR_ORG_ID", nullable = false)
    private CarrierEntity carrier;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "REF_TYPE", nullable = false)
    private String accessorialType;

    @Column(name = "CARR_COST_REF_TYPE", nullable = false)
    private String carrierCostRefType;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private long version = 1L;

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccessorialType() {
        return accessorialType;
    }

    public void setAccessorialType(String accessorialType) {
        this.accessorialType = accessorialType;
    }

    public String getCarrierCostRefType() {
        return carrierCostRefType;
    }

    public void setCarrierCostRefType(String carrierCostRefType) {
        this.carrierCostRefType = carrierCostRefType;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
