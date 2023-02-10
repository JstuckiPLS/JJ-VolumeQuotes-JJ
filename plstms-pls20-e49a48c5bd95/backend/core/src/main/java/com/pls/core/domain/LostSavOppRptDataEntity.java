package com.pls.core.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Lost Savings Opportunity Report Entity. Contains the information related to the lost saving's opportunities
 * for a customer in certain time period.
 * 
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LOST_SAV_OPP_RPT_DATA")
public class LostSavOppRptDataEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 1L;
    public static final String GET_LOST_SAVINGS_OPP_REPORT_QUERY = "com.pls.core.domain.LostSavOppRptDataEntity.GET_LOST_SAVINGS_OPP_REPORT_QUERY";
    public static final String Q_PREPARE_DATA = "com.pls.core.domain.LostSavOppRptDataEntity.Q_PREPARE_DATA";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LSO_RPT_DATA_ID_SEQ")
    @SequenceGenerator(name = "LSO_RPT_DATA_ID_SEQ", sequenceName = "LSO_RPT_DATA_ID_SEQ", allocationSize = 1)
    @Column(name = "LSO_RPT_DATA_ID")
    private Long id;

    @Column(name = "LOAD_ID", nullable = false)
    private Long loadId;

    @Column(name = "JOB_ID", nullable = false)
    private Long jobId;

    @Column(name = "BOL", nullable = false)
    private String bol;

    @Column(name = "PO_NUM")
    private String poNum;

    @Column(name = "SO_NUMBER")
    private String soNumber;

    @Column(name = "SHIPPER_REFERENCE_NUMBER")
    private Long shipperRefNum;

    @Column(name = "EST_PICKUP_DATE", nullable = false)
    private Date estPickupDate;

    @Column(name = "PICKUP_DATE")
    private Date pickupDate;

    @Column(name = "SHIPPER_ORG_ID", nullable = false)
    private Long shipperOrgId;

    @Column(name = "CARRIER_ORG_ID", nullable = false)
    private Long carrierOrgId;

    @Column(name = "SHIPPER_NAME")
    private String shipperName;

    @Column(name = "ORIG_CITY")
    private String origCity;

    @Column(name = "ORIG_STATE")
    private String origState;

    @Column(name = "ORIG_ZIP", nullable = false)
    private String origZip;

    @Column(name = "ORIG_COUNTRY")
    private String origCountry;

    @Column(name = "CONSIGNEE_NAME")
    private String consigneeName;

    @Column(name = "DEST_CITY")
    private String destCity;

    @Column(name = "DEST_STATE")
    private String destState;

    @Column(name = "DEST_ZIP", nullable = false)
    private String destZip;

    @Column(name = "DEST_COUNTRY")
    private String destCountry;

    @Column(name = "GUARANTEED_TIME")
    private Long guaranteedTime;

    @Column(name = "HAZMAT_FLAG")
    private String hazmatFlag;

    @Column(name = "TOTAL_WEIGHT")
    private BigDecimal totalWeight;

    @Column(name = "TOTAL_REVENUE")
    private double totalRevenue;

    @Column(name = "TOTAL_COST")
    private double totalCost;

    @Column(name = "TRANSIT_TIME")
    private int transitTime;

    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @Column(name = "LOAD_CREATED_BY", nullable = false)
    private Long loadCreatedBy;

    @Column(name = "LOAD_CREATED_DATE", nullable = false)
    private Date loadCreatedDate;

    @Column(name = "LC_CARRIER_ORG_ID")
    private Long lcCarrierorgId;

    @Column(name = "LC_CARR_TOTAL_REVENUE")
    private double lcCarrTotalRev;

    @Column(name = "LC_CARR_TOTAL_COST")
    private double lcCarrTotalCost;

    @Column(name = "LC_CARR_TRANSIT_TIME")
    private int lcCarrTransitTime;

    @Column(name = "LC_CARR_SERVICE_TYPE")
    private String lcCarrServiceType;

    @Column(name = "REVENUE_SAVINGS")
    private double revenueSavings;

    @Column(name = "REV_SAVINGS_PERC")
    private double revSavingsPerc;

    @Column(name = "DATE_CREATED", nullable = false)
    private Date dateCreated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
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

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public Long getShipperRefNum() {
        return shipperRefNum;
    }

    public void setShipperRefNum(Long shipperRefNum) {
        this.shipperRefNum = shipperRefNum;
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

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public Long getCarrierOrgId() {
        return carrierOrgId;
    }

    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getOrigCity() {
        return origCity;
    }

    public void setOrigCity(String origCity) {
        this.origCity = origCity;
    }

    public String getOrigState() {
        return origState;
    }

    public void setOrigState(String origState) {
        this.origState = origState;
    }

    public String getOrigZip() {
        return origZip;
    }

    public void setOrigZip(String origZip) {
        this.origZip = origZip;
    }

    public String getOrigCountry() {
        return origCountry;
    }

    public void setOrigCountry(String origCountry) {
        this.origCountry = origCountry;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public String getDestZip() {
        return destZip;
    }

    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }

    public String getDestCountry() {
        return destCountry;
    }

    public void setDestCountry(String destCountry) {
        this.destCountry = destCountry;
    }

    public Long getGuaranteedTime() {
        return guaranteedTime;
    }

    public void setGuaranteedTime(Long guaranteedTime) {
        this.guaranteedTime = guaranteedTime;
    }

    public String getHazmatFlag() {
        return hazmatFlag;
    }

    public void setHazmatFlag(String hazmatFlag) {
        this.hazmatFlag = hazmatFlag;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getTransitTime() {
        return transitTime;
    }

    public void setTransitTime(int transitTime) {
        this.transitTime = transitTime;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Long getLoadCreatedBy() {
        return loadCreatedBy;
    }

    public void setLoadCreatedBy(Long loadCreatedBy) {
        this.loadCreatedBy = loadCreatedBy;
    }

    public Date getLoadCreatedDate() {
        return loadCreatedDate;
    }

    public void setLoadCreatedDate(Date loadCreatedDate) {
        this.loadCreatedDate = loadCreatedDate;
    }

    public Long getLcCarrierorgId() {
        return lcCarrierorgId;
    }

    public void setLcCarrierorgId(Long lcCarrierorgId) {
        this.lcCarrierorgId = lcCarrierorgId;
    }

    public double getLcCarrTotalRev() {
        return lcCarrTotalRev;
    }

    public void setLcCarrTotalRev(double lcCarrTotalRev) {
        this.lcCarrTotalRev = lcCarrTotalRev;
    }

    public double getLcCarrTotalCost() {
        return lcCarrTotalCost;
    }

    public void setLcCarrTotalCost(double lcCarrTotalCost) {
        this.lcCarrTotalCost = lcCarrTotalCost;
    }

    public int getLcCarrTransitTime() {
        return lcCarrTransitTime;
    }

    public void setLcCarrTransitTime(int lcCarrTransitTime) {
        this.lcCarrTransitTime = lcCarrTransitTime;
    }

    public String getLcCarrServiceType() {
        return lcCarrServiceType;
    }

    public void setLcCarrServiceType(String lcCarrServiceType) {
        this.lcCarrServiceType = lcCarrServiceType;
    }

    public double getRevenueSavings() {
        return revenueSavings;
    }

    public void setRevenueSavings(double revenueSavings) {
        this.revenueSavings = revenueSavings;
    }

    public double getRevSavingsPerc() {
        return revSavingsPerc;
    }

    public void setRevSavingsPerc(double revSavingsPerc) {
        this.revSavingsPerc = revSavingsPerc;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

}
