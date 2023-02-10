package com.pls.shipment.domain;

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

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Contains cost details breakup for materials of selected proposition.
 *
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LOAD_PRIC_MATERIAL_DTLS")
public class LoadPricMaterialDtlsEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = 7914727574829397162L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_pricing_material_details_item_sequence")
    @SequenceGenerator(name = "load_pricing_material_details_item_sequence", sequenceName = "LOAD_PRIC_MATERIAL_DTLS_SEQ", allocationSize = 1)
    @Column(name = "LOAD_PRIC_MATERIAL_DTLS_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_PRICING_DETAIL_ID", nullable = false)
    private LoadPricingDetailsEntity loadPricingDetails;

    @Column(name = "CHARGE")
    private String charge;

    @Column(name = "NMFC_CLASS")
    private String nmfcClass;

    @Column(name = "ENTERED_NMFC_CLASS")
    private String enteredNmfcClass;

    @Column(name = "RATE")
    private String rate;

    @Column(name = "WEIGHT")
    private String weight;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "QUANTITY")
    private String quantity;

    @Column(name = "NMFC")
    private String nmfc;
 
    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoadPricingDetailsEntity getLoadPricingDetails() {
        return loadPricingDetails;
    }

    public void setLoadPricingDetails(LoadPricingDetailsEntity loadPricingDetails) {
        this.loadPricingDetails = loadPricingDetails;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getNmfcClass() {
        return nmfcClass;
    }

    public void setNmfcClass(String nmfcClass) {
        this.nmfcClass = nmfcClass;
    }

    public String getEnteredNmfcClass() {
        return enteredNmfcClass;
    }

    public void setEnteredNmfcClass(String enteredNmfcClass) {
        this.enteredNmfcClass = enteredNmfcClass;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getNmfc() {
        return nmfc;
    }

    public void setNmfc(String nmfc) {
        this.nmfc = nmfc;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
