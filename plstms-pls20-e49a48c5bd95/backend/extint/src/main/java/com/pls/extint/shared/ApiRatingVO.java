package com.pls.extint.shared;

import java.util.List;

/**
 * VO class for rating.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class ApiRatingVO {

    private String userName;
    private String password;
    private String originZip;
    private String destinationZip;
    private String customerNumber;
    private String terms;
    private List<ApiRatingProductVO> products;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOriginZip() {
        return originZip;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public String getDestinationZip() {
        return destinationZip;
    }

    public void setDestinationZip(String destinationZip) {
        this.destinationZip = destinationZip;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public List<ApiRatingProductVO> getProducts() {
        return products;
    }

    public void setProducts(List<ApiRatingProductVO> products) {
        this.products = products;
    }

}
