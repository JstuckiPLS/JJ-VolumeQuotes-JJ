package com.pls.shipment.domain.edi;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.organization.CarrierEntity;

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

/**
 * EDI qualifier entity.
 *
 * @author Mikhail Boldinov, 29/08/13
 */
@Entity
@Table(name = "EDI_QUALIFIERS")
public class EDIQualifierEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 6708964812969299465L;

    public static final String Q_GET_QUALIFIER = "com.pls.shipment.domain.edi.EDIQualifierEntity.Q_GET_QUALIFIER";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edi_qualifiers_sequence")
    @SequenceGenerator(name = "edi_qualifiers_sequence", sequenceName = "EDI_QUALIFIERS_SEQ", allocationSize = 1)
    @Column(name = "EDI_QUAL_ID")
    private Long id;

    @Column(name = "TRANSACTION_SET_ID")
    private String transactionSetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID", nullable = false)
    private CarrierEntity carrier;

    @Column(name = "ELEMENT", nullable = false)
    private String element;

    @Column(name = "REFERENCE_QUAL", nullable = false)
    private String qualifier;

    @Column(name = "PLS_REFERENCE", nullable = false)
    private String plsReference;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionSetId() {
        return transactionSetId;
    }

    public void setTransactionSetId(String transactionSetId) {
        this.transactionSetId = transactionSetId;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getPlsReference() {
        return plsReference;
    }

    public void setPlsReference(String plsReference) {
        this.plsReference = plsReference;
    }
}
