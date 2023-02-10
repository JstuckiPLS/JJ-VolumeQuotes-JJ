package com.pls.ltlrating.domain.analysis;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.organization.CarrierEntity;

/**
 * Results of Freight Analysis processing.
 * 
 * @author Svetlana Kulish
 */
@Entity
@Table(name = "FA_OUTPUT_DETAILS")
public class FAOutputDetailsEntity implements Identifiable<Long> {
    private static final long serialVersionUID = -407007253617700514L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FA_OUTPUT_DETAILS_SEQUENCE")
    @SequenceGenerator(name = "FA_OUTPUT_DETAILS_SEQUENCE", sequenceName = "FA_OUTPUT_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "OUTPUT_DETAIL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORG_ID", insertable = false, updatable = false)
    private CarrierEntity carrier;

    @Column(name = "ORG_ID")
    private Long carrierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INPUT_DETAIL_ID", nullable = false)
    private FAInputDetailsEntity inputDetails;

    @Column(name = "COST_DETAIL_OWNER")
    @Enumerated(EnumType.STRING)
    private CostDetailOwner owner;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", updatable = false)
    private Date createdDate = new Date();

    @Column(name = "SUBTOTAL")
    private BigDecimal subtotal;

    @Column(name = "TRANSIT_DAYS")
    private Integer transitDays;

    @Column(name = "SEQ_NUMBER")
    private Integer seq;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FA_TARIFF_ID")
    private FATariffsEntity tariff;

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

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public FAInputDetailsEntity getInputDetails() {
        return inputDetails;
    }

    public void setInputDetails(FAInputDetailsEntity inputDetails) {
        this.inputDetails = inputDetails;
    }

    public CostDetailOwner getOwner() {
        return owner;
    }

    public void setOwner(CostDetailOwner owner) {
        this.owner = owner;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Integer getTransitDays() {
        return transitDays;
    }

    public void setTransitDays(Integer transitDays) {
        this.transitDays = transitDays;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public FATariffsEntity getTariff() {
        return tariff;
    }

    public void setTariff(FATariffsEntity tariff) {
        this.tariff = tariff;
    }

}
