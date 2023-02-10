/**
 * 
 */
package com.pls.dto.shipment;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for Prepaid Detail.
 * 
 * @author Alexander Nalapko
 *
 */
public class PrepaidDetailDTO {

    private Long id;

    private String paymentId;

    private Date date;

    private BigDecimal amount;

    /**
     * Constructor.
     */
    public PrepaidDetailDTO() {
    }

    /**
     * Constructor.
     * 
     * @param id
     *            id
     * @param paymentId
     *            payment number
     * @param date
     *            date
     * @param amount
     *            amount
     */
    public PrepaidDetailDTO(Long id, String paymentId, Date date, BigDecimal amount) {
        super();
        this.id = id;
        this.paymentId = paymentId;
        this.date = date;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

}
