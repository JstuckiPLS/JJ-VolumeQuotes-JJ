package com.pls.core.domain.bo;

/**
 * Class for Lost Savings Opportunity Report Materials data BO.
 * 
 * @author Ashwini Neelgund
 */
public class LostSavingsMaterialsBO {

    private String productDescription;
    private double weight;
    private String classType;

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    public String getClassType() {
        return classType;
    }
    public void setClassType(String classType) {
        this.classType = classType;
    }
    public String getProductDescription() {
        return productDescription;
    }
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
