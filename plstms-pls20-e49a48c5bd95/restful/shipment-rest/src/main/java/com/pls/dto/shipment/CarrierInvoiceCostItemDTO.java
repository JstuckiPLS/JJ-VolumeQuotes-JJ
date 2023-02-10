package com.pls.dto.shipment;

import java.math.BigDecimal;

/**
 * DTO for carrier invoice (vendor bill) cost item.
 *
 * @author Sergey Kirichenko
 */
public class CarrierInvoiceCostItemDTO {

    private Long id;

    private String refType;

    private BigDecimal subTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
