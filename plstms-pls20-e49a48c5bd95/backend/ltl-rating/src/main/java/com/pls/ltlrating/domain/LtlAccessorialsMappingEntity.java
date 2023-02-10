package com.pls.ltlrating.domain;

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

import org.hibernate.annotations.Type;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.CarrierEntity;
/**
 * LTL Accessorials Mapping (LTL_ACCESSORIALS_MAPPING) entity. Used for storing PLS to Carriers accessorials mapping data.
 *
 * @author Davydenko Dmitriy
 */
@Entity
@Table(name = "LTL_ACCESSORIALS_MAPPING")
public class LtlAccessorialsMappingEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {
    private static final long serialVersionUID = -3104183191724780315L;

    public static final String GET_ACCESSORIALS_MAPPING = "com.pls.ltlrating.domain.LtlAccessorialsMappingEntity.GET_ACCESSORIALS_MAPPING";
    public static final String GET_ACCESSORIALS_MAPPING_BY_SCAC =
            "com.pls.ltlrating.domain.LtlAccessorialsMappingEntity.GET_ACCESSORIALS_MAPPING_BY_SCAC";

    /**
     * Required empty constructor for Hibernate to be able to use reflection on this consructor to instantiate objects.
     */
    public LtlAccessorialsMappingEntity() {
    }

    /**
     * Constuctor to write tests more conveniently.
     * 
     * @param plsCode - pls code
     * @param carrierCode -carrier code
     * @param defaultAccessorial - is this accessorial default or not
     * @param carrierId - carrier id.
     */
    public LtlAccessorialsMappingEntity(String plsCode, String carrierCode, Boolean defaultAccessorial, Long carrierId) {
        this.plsCode = plsCode;
        this.carrierCode = carrierCode;
        this.defaultAccessorial = defaultAccessorial;
        this.carrierId = carrierId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acc_mapping_seq")
    @SequenceGenerator(name = "acc_mapping_seq", sequenceName = "acc_mapping_seq", allocationSize = 1)
    @Column(name = "MAPPING_ID")
    private Long id;

    @Column(name = "PLS_CODE")
    private String plsCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLS_CODE", insertable = false, updatable = false)
    private AccessorialTypeEntity accessorialType;

    @Column(name = "CARRIER_CODE")
    private String carrierCode;

    @Column(name = "DEFAULT_ACC")
    @Type(type = "yes_no")
    private Boolean defaultAccessorial;

    @Column(name = "CARRIER_ID")
    private Long carrierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_ID", insertable = false, updatable = false)
    private CarrierEntity carrier;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    public String getPlsCode() {
        return plsCode;
    }

    public void setPlsCode(String plsCode) {
        this.plsCode = plsCode;
    }

    public AccessorialTypeEntity getAccessorialType() {
        return accessorialType;
    }

    public void setAccessorialType(AccessorialTypeEntity accessorialType) {
        this.accessorialType = accessorialType;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public Boolean getDefaultAccessorial() {
        return defaultAccessorial;
    }

    public void setDefaultAccessorial(Boolean defaultAccessorial) {
        this.defaultAccessorial = defaultAccessorial;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        this.id = pId;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    @Override
    public Integer getVersion() {
        return this.version;
    }
}
