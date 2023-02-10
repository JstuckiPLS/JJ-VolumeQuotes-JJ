package com.pls.core.domain.bo;

import java.util.Date;
import java.util.List;

/**
 * Lost Savings Opportunity Report BO.
 * 
 * @author Ashwini Neelgund
 */
public class LostSavingsReportBO {

    private String userName;
    private String bol;
    private String poNum;
    private String soNum;
    private String shipperRefNum;
    private String shipperName;
    private String originState;
    private String originCity;
    private String originZip;
    private String consigneeName;
    private String destState;
    private String destCity;
    private String destZip;
    private List<LostSavingsMaterialsBO> lostSavingsMaterials;
    private double totalWeight;
    private Date loadCreatedDate;
    private String loadCreatedDay;
    private Date estPickupDate;
    private Date pickupDate;
    private List<String> accessorials;
    private String carrSelected;
    private double carrAmt;
    private int carrTransitTime;
    private String leastCostCarr;
    private double leastCostAmt;
    private int leastCostTransitTime;
    private double potentialSavings;
    private double potSavingsPerc;
    private Date dateCreated;

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getBol() {
        return bol;
    }
    public void setBol(String bol) {
        this.bol = bol;
    }
    public String getPoNum() {
        return poNum;
    }
    public void setPoNum(String poNum) {
        this.poNum = poNum;
    }
    public String getSoNum() {
        return soNum;
    }
    public void setSoNum(String soNum) {
        this.soNum = soNum;
    }
    public String getShipperRefNum() {
        return shipperRefNum;
    }
    public void setShipperRefNum(String shipperRefNum) {
        this.shipperRefNum = shipperRefNum;
    }
    public String getShipperName() {
        return shipperName;
    }
    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }
    public String getOriginState() {
        return originState;
    }
    public void setOriginState(String originState) {
        this.originState = originState;
    }
    public String getOriginCity() {
        return originCity;
    }
    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }
    public String getOriginZip() {
        return originZip;
    }
    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }
    public String getConsigneeName() {
        return consigneeName;
    }
    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }
    public String getDestState() {
        return destState;
    }
    public void setDestState(String destState) {
        this.destState = destState;
    }
    public String getDestCity() {
        return destCity;
    }
    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }
    public String getDestZip() {
        return destZip;
    }
    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }
    public List<LostSavingsMaterialsBO> getLostSavingsMaterials() {
        return lostSavingsMaterials;
    }
    public void setLostSavingsMaterials(List<LostSavingsMaterialsBO> lostSavingsMaterials) {
        this.lostSavingsMaterials = lostSavingsMaterials;
    }
    public double getTotalWeight() {
        return totalWeight;
    }
    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }
    public Date getLoadCreatedDate() {
        return loadCreatedDate;
    }
    public void setLoadCreatedDate(Date loadCreatedDate) {
        this.loadCreatedDate = loadCreatedDate;
    }
    public String getLoadCreatedDay() {
        return loadCreatedDay;
    }
    public void setLoadCreatedDay(String loadCreatedDay) {
        this.loadCreatedDay = loadCreatedDay;
    }
    public Date getEstPickupDate() {
        return estPickupDate;
    }
    public void setEstPickupDate(Date estPickupDate) {
        this.estPickupDate = estPickupDate;
    }
    public Date getPickupDate() {
        return pickupDate;
    }
    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }
    public List<String> getAccessorials() {
        return accessorials;
    }
    public void setAccessorials(List<String> accessorials) {
        this.accessorials = accessorials;
    }
    public String getCarrSelected() {
        return carrSelected;
    }
    public void setCarrSelected(String carrSelected) {
        this.carrSelected = carrSelected;
    }
    public double getCarrAmt() {
        return carrAmt;
    }
    public void setCarrAmt(double carrAmt) {
        this.carrAmt = carrAmt;
    }
    public int getCarrTransitTime() {
        return carrTransitTime;
    }
    public void setCarrTransitTime(int carrTransitTime) {
        this.carrTransitTime = carrTransitTime;
    }
    public String getLeastCostCarr() {
        return leastCostCarr;
    }
    public void setLeastCostCarr(String leastCostCarr) {
        this.leastCostCarr = leastCostCarr;
    }
    public double getLeastCostAmt() {
        return leastCostAmt;
    }
    public void setLeastCostAmt(double leastCostAmt) {
        this.leastCostAmt = leastCostAmt;
    }
    public int getLeastCostTransitTime() {
        return leastCostTransitTime;
    }
    public void setLeastCostTransitTime(int leastCostTransitTime) {
        this.leastCostTransitTime = leastCostTransitTime;
    }
    public double getPotentialSavings() {
        return potentialSavings;
    }
    public void setPotentialSavings(double potentialSavings) {
        this.potentialSavings = potentialSavings;
    }
    public double getPotSavingsPerc() {
        return potSavingsPerc;
    }
    public void setPotSavingsPerc(double potSavingsPerc) {
        this.potSavingsPerc = potSavingsPerc;
    }
    public Date getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
