package com.pls.extint.shared;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This VO stores the weight and class of a product.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class ApiRatingProductVO {

    private String productClass;
    private String productWeight;

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
