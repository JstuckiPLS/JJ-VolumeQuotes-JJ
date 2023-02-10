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
 * Contains cost details breakup for materials of selected saved quote proposition.
 *
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "SAVED_QUOTE_PRIC_MAT_DTLS")
public class SavedQuotePricMatDtlsEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = 639870362571559276L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saved_quote_pric_mat_dtls_item_sequence")
    @SequenceGenerator(name = "saved_quote_pric_mat_dtls_item_sequence", sequenceName = "SAVED_QUOTE_PRIC_MAT_DTLS_SEQ", allocationSize = 1)
    @Column(name = "SAVED_QUOTE_PRIC_MAT_DTLS_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SAVED_QUOTE_PRIC_DTLS_ID", nullable = false)
    private SavedQuotePricDtlsEntity savedQuotePricDtls;

    @Column(name = "ENTERED_NMFC_CLASS")
    private String enteredNmfcClass;

    @Column(name = "NMFC_CLASS")
    private String nmfcClass;

    @Column(name = "CHARGE")
    private String charge;

    @Column(name = "RATE")
    private String rate;

    @Column(name = "WEIGHT")
    private String weight;

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

    public SavedQuotePricDtlsEntity getSavedQuotePricDtls() {
        return savedQuotePricDtls;
    }

    public void setSavedQuotePricDtls(SavedQuotePricDtlsEntity savedQuotePricDtls) {
        this.savedQuotePricDtls = savedQuotePricDtls;
    }

    public String getNmfcClass() {
        return nmfcClass;
    }

    public void setNmfcClass(String nmfcClass) {
        this.nmfcClass = nmfcClass;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getEnteredNmfcClass() {
        return enteredNmfcClass;
    }

    public void setEnteredNmfcClass(String enteredNmfcClass) {
        this.enteredNmfcClass = enteredNmfcClass;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

}
