package com.pls.shipment.domain.bo;

import java.math.BigDecimal;

/**
 * BO for adjsutments product.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class ProductWeightBO {
    private Long id;

    private BigDecimal weight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
