package com.pls.core.domain.organization;

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
 * Edi Rejected Customer Entity.
 *
 * <p>
 * Represents a list of Customers for whom EDI210 should be rejected.
 * </p>
 * 
 * @author Artem Arapov
 *
 */
@Entity
@Table(name = "EDI_REJECTED_CUSTOMERS")
public class EdiRejectedCustomerEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = 8753392813764598739L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rejected_customers_sequence")
    @SequenceGenerator(name = "rejected_customers_sequence", sequenceName = "EDI_REJECTED_CUSTOMERS_SEQ", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_ID", insertable = false, updatable = false)
    private CarrierEntity carrier;

    @Column(name = "CARRIER_ID", nullable = false)
    private Long carrierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", insertable = false, updatable = false)
    private CustomerEntity customer;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    @Version
    private Integer version;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @Override
    public PlainModificationObject getModification() {
        return this.modification;
    }

    @Override
    public Integer getVersion() {
        return version;
    }
}
