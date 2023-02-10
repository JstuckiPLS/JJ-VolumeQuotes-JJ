package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Version;

import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;

/**
 * Prepaid Detail Entity.
 * 
 * @author Alexander Nalapko
 *
 */
@Entity
@Table(name = "PREPAID_DETAILS")
public class PrepaidDetailEntity implements Identifiable<Long>, HasVersion {

    private static final long serialVersionUID = -8770162403938062247L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity load;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prepaid_details_item_sequence")
    @SequenceGenerator(name = "prepaid_details_item_sequence", sequenceName = "LOAD_PREPAID_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "PAYMENT_ID", nullable = false)
    private String paymentId;

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "PAYMENT_DATE")
    private Date paymentDate;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    public LoadEntity getLoad() {
        return load;
    }

    /**
     * Constructor.
     */
    public PrepaidDetailEntity() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param load
     *            Load Entity
     * @param id
     *            id
     * @param paymentId
     *            payment number
     * @param paymentDate
     *            date
     * @param amount
     *            amount
     */
    public PrepaidDetailEntity(LoadEntity load, Long id, String paymentId, Date paymentDate, BigDecimal amount) {
        super();
        this.load = load;
        this.id = id;
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
