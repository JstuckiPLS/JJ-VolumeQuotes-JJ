package com.pls.shipment.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CarrierInvoiceReasons;

/**
 * Carrier Invoice Detail Reason entity.
 *
 * @author Alexander Nalapko
 */
@Entity
@Table(name = "CARRIER_INVOICE_REASONS")
public class CarrierInvoiceDetailReasonsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -84999365846108275L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrier_invoice_reason_sequence")
    @SequenceGenerator(name = "carrier_invoice_reason_sequence", sequenceName = "INVOICE_REASONS_SEQ", allocationSize = 1)
    @Column(name = "REASON_ID")
    private Long id;

    @Column(name = "REASON_CODE")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.CarrierInvoiceReasons"),
            @Parameter(name = "identifierMethod", value = "getReasonCode"),
            @Parameter(name = "valueOfMethod", value = "getReasonByCode") })
    private CarrierInvoiceReasons reasonCode;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "LOAD_ID")
    private String loadId;

    @OneToMany(mappedBy = "reason", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CarrierInvoiceDetailReasonLinksEntity> reasonLinks;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public Set<CarrierInvoiceDetailReasonLinksEntity> getReasonLinks() {
        return reasonLinks;
    }

    public void setReasonLinks(Set<CarrierInvoiceDetailReasonLinksEntity> reasonLinks) {
        this.reasonLinks = reasonLinks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarrierInvoiceReasons getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(CarrierInvoiceReasons reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getLoadId() {
        return loadId;
    }

    public void setLoadId(String loadId) {
        this.loadId = loadId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

}
