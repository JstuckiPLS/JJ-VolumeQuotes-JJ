package com.pls.invoice.domain.xml.finance.salesorder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.service.util.xml.adapter.DateXmlAdapter;
import com.pls.core.service.util.xml.adapter.TimeXmlAdapter;
import com.pls.invoice.domain.xml.finance.FinanceInfoTable;

/**
 * SalesOrder JAXB-oriented object.
 *
 * @author Denis Zhupinsky (Team International)
 */
@XmlRootElement(name = "SalesOrder")
@XmlAccessorType(XmlAccessType.FIELD)
public class SalesOrder implements IntegrationMessageBO, FinanceInfoTable {

    private static final long serialVersionUID = 415526478995748421L;

    @XmlElement(name = "Operation")
    private String operation = "CREATE";

    @XmlTransient
    private String carrier;

    @XmlElement(name = "Currency")
    private String currency;

    @XmlElement(name = "CustAccount")
    private String custAccount;

    @XmlElement(name = "BusinessUnit")
    private String businessUnit;

    @XmlElement(name = "CostCenter")
    private String costCenter;

    @XmlElement(name = "Department")
    private String department = "SL";

    @XmlElement(name = "LtlInvoice")
    private String ltlInvoice = "Yes";

    @XmlElement(name = "Addend")
    private String addend;

    @XmlElement(name = "AdjDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date adjDate;

    @XmlElement(name = "AdjType")
    private String adjType;

    @XmlElement(name = "BatchProcess")
    private String batchProcess = "No";

    @XmlElement(name = "BenchmarkRate")
    private Double benchmarkRate;

    @XmlElement(name = "BillToLoc")
    private String billToLoc;

    @XmlElement(name = "BillToName")
    private String billToName;

    @XmlElement(name = "BillToId")
    private String billToId;

    @XmlElement(name = "Bol")
    private String bol;

    @XmlElement(name = "BusinessPartnerName")
    private String businessPartnerName;

    @XmlElement(name = "BusinessPartner")
    private String businessPartner;

    @XmlElement(name = "CarrierCost")
    private Double carrierCost;

    @XmlElement(name = "CarrierName")
    private String carrierName;

    @XmlElement(name = "CarrierPremium")
    private Double carrierPremium;

    @XmlElement(name = "Scac")
    private String scac;

    @XmlElement(name = "Commodity")
    private String commodity;

    @XmlElement(name = "DestAddr1")
    private String destAddr1;

    @XmlElement(name = "DestAddr2")
    private String destAddr2;

    @XmlElement(name = "DestCity")
    private String destCity;

    @XmlElement(name = "DestLoc")
    private String destLoc;

    @XmlElement(name = "DestName")
    private String destName;

    @XmlElement(name = "DestNode")
    private String destNode;

    @XmlElement(name = "DestState")
    private String destState;

    @XmlElement(name = "DestZip")
    private String destZip;

    @XmlElement(name = "DestCountry")
    private String destCountry;

    @XmlElement(name = "Discount")
    private BigDecimal discount;

    @XmlElement(name = "DoNotInvoice")
    private String doNotInvoice;

    @XmlElement(name = "EquipmentType")
    private String equipmentType;

    @XmlElement(name = "EquipmentDesc")
    private String equipmentDesc;

    @XmlElement(name = "FreightClass")
    private String freightClass;

    @XmlElement(name = "FuelBenchmark")
    private Double fuelBenchmark;

    @XmlElement(name = "GlNumber")
    private String glNumber;

    @XmlElement(name = "GlDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date glDate;

    @XmlElement(name = "Hazmat")
    private String hazmat;

    @XmlElement(name = "Height")
    private Double height;

    @XmlElement(name = "InbOtb")
    private String inbOtb;

    @XmlElement(name = "InvoiceNo")
    private String invoiceNo;

    @XmlElement(name = "TotalStops")
    private Integer totalStops;

    @XmlElement(name = "JobNumbers")
    private String jobNumbers;

    @XmlElement(name = "JobPercents")
    private String jobPercents;

    @XmlElement(name = "LocationName")
    private String locationName;

    @XmlElement(name = "Length")
    private Double length;

    @XmlElement(name = "Margin")
    private Double margin;

    @XmlElement(name = "Miles")
    private Integer miles;

    @XmlElement(name = "NetworkId")
    private String networkId;

    @XmlElement(name = "OneTimeRate")
    private Long oneTimeRate;

    @XmlElement(name = "OpBol")
    private String opBol;

    @XmlElement(name = "LoadOrgID")
    private Long customerOrgId;

    @XmlElement(name = "LoadLocationId")
    private Long locationId;

    @XmlElement(name = "CustomerOrgId")
    private final String org = "PLS";

    @XmlElement(name = "OrigAddr1")
    private String origAddr1;

    @XmlElement(name = "OrigAddr2")
    private String origAddr2;

    @XmlElement(name = "OrigCity")
    private String origCity;

    @XmlElement(name = "OrigLoc")
    private String origLoc;

    @XmlElement(name = "OrigName")
    private String origName;

    @XmlElement(name = "OrigNode")
    private String origNode;

    @XmlElement(name = "OrigState")
    private String origState;

    @XmlElement(name = "OrigZip")
    private String origZip;

    @XmlElement(name = "OrigCountry")
    private String origCountry;

    @XmlElement(name = "OutPeriod")
    private String outPeriod;

    @XmlElement(name = "PartNum")
    private String partNum;

    @XmlElement(name = "Pieces")
    private Integer pieces;

    @XmlElement(name = "ProNumber")
    private String proNumber;

    @XmlElement(name = "Prose1")
    private String prose1;

    @XmlElement(name = "Prose2")
    private String prose2;

    @XmlElement(name = "ProtectedCarrier")
    private String protectedCarrier;

    @XmlElement(name = "SalesOrderType")
    private String salesOrderType;

    @XmlElement(name = "ShipCarr")
    private String shipCarr = "S";

    @XmlElement(name = "ShipmentNum")
    private String shipmentNo;

    @XmlElement(name = "ShipWith")
    private Integer shipWith;

    @XmlElement(name = "ShortPay")
    private String shortPay;

    @XmlElement(name = "SplitComb")
    private String splitComb;

    @XmlElement(name = "PayTerms")
    private String payTerms;

    @XmlElement(name = "TrailerNo")
    private String trailerNo;

    @XmlElement(name = "TotalBenchmark")
    private Double totalBenchmark;

    @XmlElement(name = "Weight")
    private Double weight;

    @XmlElement(name = "Width")
    private Double width;

    @XmlElement(name = "LoadNumber")
    private String loadNumber;

    @XmlElement(name = "DeliveryDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date deliveryDate;

    @XmlElement(name = "DeliveryTime")
    @XmlJavaTypeAdapter(TimeXmlAdapter.class)
    private Date deliveryTime;

    @XmlElement(name = "PickupDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date pickupDate;

    @XmlElement(name = "PickupTime")
    @XmlJavaTypeAdapter(TimeXmlAdapter.class)
    private Date pickupTime;

    @XmlElement(name = "BillerType")
    private String billerType;

    @XmlElement(name = "LoadId")
    private Long loadId;

    @XmlElement(name = "FaaDetailId")
    private String faaDetailId;

    @XmlElement(name = "RequestId")
    private String requestId;

    @XmlElement(name = "SalesLine")
    private List<SalesLine> salesLines;

    @XmlTransient
    private Long personId;

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Double getTotalBenchmark() {
        return totalBenchmark;
    }

    public void setTotalBenchmark(Double totalBenchmark) {
        this.totalBenchmark = totalBenchmark;
    }

    public Double getFuelBenchmark() {
        return fuelBenchmark;
    }

    public void setFuelBenchmark(Double fuelBenchmark) {
        this.fuelBenchmark = fuelBenchmark;
    }

    public String getDoNotInvoice() {
        return doNotInvoice;
    }

    public void setDoNotInvoice(String doNotInvoice) {
        this.doNotInvoice = doNotInvoice;
    }

    public Double getCarrierCost() {
        return carrierCost;
    }

    public void setCarrierCost(Double carrierCost) {
        this.carrierCost = carrierCost;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public String getBol() {
        return bol;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCustAccount() {
        return custAccount;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public String getDepartment() {
        return department;
    }

    public String getLtlInvoice() {
        return ltlInvoice;
    }

    public String getAddend() {
        return addend;
    }

    public Date getAdjDate() {
        return adjDate;
    }

    public String getAdjType() {
        return adjType;
    }

    public String getBatchProcess() {
        return batchProcess;
    }

    public Double getBenchmarkRate() {
        return benchmarkRate;
    }

    public String getBillToLoc() {
        return billToLoc;
    }

    public String getBillToName() {
        return billToName;
    }

    public String getBillToId() {
        return billToId;
    }

    public String getBusinessPartnerName() {
        return businessPartnerName;
    }

    public String getBusinessPartner() {
        return businessPartner;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public Double getCarrierPremium() {
        return carrierPremium;
    }

    @Override
    public String getScac() {
        return scac;
    }

    public String getCommodity() {
        return commodity;
    }

    public String getDestAddr1() {
        return destAddr1;
    }

    public String getDestAddr2() {
        return destAddr2;
    }

    public String getDestCity() {
        return destCity;
    }

    public String getDestLoc() {
        return destLoc;
    }

    public String getDestName() {
        return destName;
    }

    public String getDestNode() {
        return destNode;
    }

    public String getDestState() {
        return destState;
    }

    public String getDestZip() {
        return destZip;
    }

    public String getDestCountry() {
        return destCountry;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public String getEquipmentDesc() {
        return equipmentDesc;
    }

    public String getGlNumber() {
        return glNumber;
    }

    public Date getGlDate() {
        return glDate;
    }

    public String getHazmat() {
        return hazmat;
    }

    public Double getHeight() {
        return height;
    }

    public String getInbOtb() {
        return inbOtb;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public Integer getTotalStops() {
        return totalStops;
    }

    public String getJobNumbers() {
        return jobNumbers;
    }

    public String getJobPercents() {
        return jobPercents;
    }

    public String getLocationName() {
        return locationName;
    }

    public Double getLength() {
        return length;
    }

    public Double getMargin() {
        return margin;
    }

    public Integer getMiles() {
        return miles;
    }

    public String getNetworkId() {
        return networkId;
    }

    public Long getOneTimeRate() {
        return oneTimeRate;
    }

    public String getOpBol() {
        return opBol;
    }

    @Override
    public Long getCustomerOrgId() {
        return customerOrgId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public String getOrigAddr1() {
        return origAddr1;
    }

    public String getOrigAddr2() {
        return origAddr2;
    }

    public String getOrigCity() {
        return origCity;
    }

    public String getOrigLoc() {
        return origLoc;
    }

    public String getOrigName() {
        return origName;
    }

    public String getOrigNode() {
        return origNode;
    }

    public String getOrigState() {
        return origState;
    }

    public String getOrigZip() {
        return origZip;
    }

    public String getOrigCountry() {
        return origCountry;
    }

    public String getOutPeriod() {
        return outPeriod;
    }

    public String getPartNum() {
        return partNum;
    }

    public Integer getPieces() {
        return pieces;
    }

    public String getProNumber() {
        return proNumber;
    }

    public String getProse1() {
        return prose1;
    }

    public String getProse2() {
        return prose2;
    }

    public String getProtectedCarrier() {
        return protectedCarrier;
    }

    public String getSalesOrderType() {
        return salesOrderType;
    }

    public String getShipCarr() {
        return shipCarr;
    }

    public Integer getShipWith() {
        return shipWith;
    }

    public String getShortPay() {
        return shortPay;
    }

    public String getSplitComb() {
        return splitComb;
    }

    public String getPayTerms() {
        return payTerms;
    }

    public String getTrailerNo() {
        return trailerNo;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getWidth() {
        return width;
    }

    public String getLoadNumber() {
        return loadNumber;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public String getBillerType() {
        return billerType;
    }

    @Override
    public Long getLoadId() {
        return loadId;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getFaaDetailId() {
        return faaDetailId;
    }

    public String getRequestId() {
        return requestId;
    }

    public List<SalesLine> getSalesLines() {
        return salesLines;
    }

    public String getFreightClass() {
        return freightClass;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCustAccount(String custAccount) {
        this.custAccount = custAccount;
    }

    @Override
    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    @Override
    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    @Override
    public void setDepartment(String department) {
        this.department = department;
    }

    public void setLtlInvoice(String ltlInvoice) {
        this.ltlInvoice = ltlInvoice;
    }

    public void setAddend(String addend) {
        this.addend = addend;
    }

    @Override
    public void setAdjDate(Date adjDate) {
        this.adjDate = adjDate;
    }

    public void setAdjType(String adjType) {
        this.adjType = adjType;
    }

    public void setBatchProcess(String batchProcess) {
        this.batchProcess = batchProcess;
    }

    public void setBenchmarkRate(Double benchmarkRate) {
        this.benchmarkRate = benchmarkRate;
    }

    public void setBillToLoc(String billToLoc) {
        this.billToLoc = billToLoc;
    }

    public void setBillToName(String billToName) {
        this.billToName = billToName;
    }

    public void setBillToId(String billToId) {
        this.billToId = billToId;
    }

    public void setBusinessPartnerName(String businessPartnerName) {
        this.businessPartnerName = businessPartnerName;
    }

    public void setBusinessPartner(String businessPartner) {
        this.businessPartner = businessPartner;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public void setCarrierPremium(Double carrierPremium) {
        this.carrierPremium = carrierPremium;
    }

    @Override
    public void setScac(String scac) {
        this.scac = scac;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public void setDestAddr1(String destAddr1) {
        this.destAddr1 = destAddr1;
    }

    public void setDestAddr2(String destAddr2) {
        this.destAddr2 = destAddr2;
    }

    @Override
    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public void setDestLoc(String destLoc) {
        this.destLoc = destLoc;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public void setDestNode(String destNode) {
        this.destNode = destNode;
    }

    @Override
    public void setDestState(String destState) {
        this.destState = destState;
    }

    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }

    @Override
    public void setDestCountry(String destCountry) {
        this.destCountry = destCountry;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public void setEquipmentDesc(String equipmentDesc) {
        this.equipmentDesc = equipmentDesc;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }

    @Override
    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }

    public void setHazmat(String hazmat) {
        this.hazmat = hazmat;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * Sets the height to the exact decimal.
     *
     * @param height
     *            height to be set
     */
    public void setHeight(BigDecimal height) {
        if (height != null) {
            this.height = height.doubleValue();
        }
    }

    public void setInbOtb(String inbOtb) {
        this.inbOtb = inbOtb;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public void setTotalStops(Integer totalStops) {
        this.totalStops = totalStops;
    }

    public void setJobNumbers(String jobNumbers) {
        this.jobNumbers = jobNumbers;
    }

    public void setJobPercents(String jobPercents) {
        this.jobPercents = jobPercents;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    /**
     * Sets the length to the exact decimal.
     *
     * @param length
     *            length to be set
     */
    public void setLength(BigDecimal length) {
        if (length != null) {
            this.length = length.doubleValue();
        }
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    public void setMiles(Integer miles) {
        this.miles = miles;
    }

    @Override
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public void setOneTimeRate(Long oneTimeRate) {
        this.oneTimeRate = oneTimeRate;
    }

    public void setOpBol(String opBol) {
        this.opBol = opBol;
    }

    public void setOrigAddr1(String origAddr1) {
        this.origAddr1 = origAddr1;
    }

    public void setOrigAddr2(String origAddr2) {
        this.origAddr2 = origAddr2;
    }

    public void setOrigCity(String origCity) {
        this.origCity = origCity;
    }

    public void setOrigLoc(String origLoc) {
        this.origLoc = origLoc;
    }

    public void setOrigName(String origName) {
        this.origName = origName;
    }

    public void setOrigNode(String origNode) {
        this.origNode = origNode;
    }

    public void setOrigState(String origState) {
        this.origState = origState;
    }

    public void setOrigZip(String origZip) {
        this.origZip = origZip;
    }

    public void setOrigCountry(String origCountry) {
        this.origCountry = origCountry;
    }

    public void setOutPeriod(String outPeriod) {
        this.outPeriod = outPeriod;
    }

    public void setPartNum(String partNum) {
        this.partNum = partNum;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    @Override
    public void setProNumber(String proNumber) {
        if (proNumber != null) {
            this.proNumber = proNumber.replaceAll("\\n", "");
        }
    }

    public void setProse1(String prose1) {
        this.prose1 = prose1;
    }

    public void setProse2(String prose2) {
        this.prose2 = prose2;
    }

    public void setProtectedCarrier(String protectedCarrier) {
        this.protectedCarrier = protectedCarrier;
    }

    public void setSalesOrderType(String salesOrderType) {
        this.salesOrderType = salesOrderType;
    }

    public void setShipCarr(String shipCarr) {
        this.shipCarr = shipCarr;
    }

    public void setShipWith(Integer shipWith) {
        this.shipWith = shipWith;
    }

    public void setShortPay(String shortPay) {
        this.shortPay = shortPay;
    }

    public void setSplitComb(String splitComb) {
        this.splitComb = splitComb;
    }

    public void setPayTerms(String payTerms) {
        this.payTerms = payTerms;
    }

    public void setTrailerNo(String trailerNo) {
        this.trailerNo = trailerNo;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    /**
     * Sets the width to the exact decimal.
     *
     * @param width
     *            width to be set
     */
    public void setWidth(BigDecimal width) {
        if (width != null) {
            this.width = width.doubleValue();
        }
    }

    public void setLoadNumber(String loadNumber) {
        this.loadNumber = loadNumber;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    @Override
    public void setBillerType(String billerType) {
        this.billerType = billerType;
    }

    @Override
    public void setFaaDetailId(String faaDetailId) {
        if (faaDetailId == null) {
            this.faaDetailId = "-1";
        } else {
            this.faaDetailId = faaDetailId;
        }
    }

    @Override
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setSalesLines(List<SalesLine> salesLines) {
        this.salesLines = salesLines;
    }

    @Override
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Override
    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;
    }

    @Override
    public void setBol(String bol) {
        this.bol = bol;
    }

    @Override
    public void setCustomerOrgId(Long customerOrgId) {
        this.customerOrgId = customerOrgId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    @Override
    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNo = invoiceNum;
    }

    @Override
    public void setOriginCity(String originCity) {
        this.origCity = originCity;
    }

    @Override
    public void setOriginState(String originState) {
        this.origState = originState;
    }

    @Override
    public void setOriginCountry(String originCountry) {
        this.origCountry = originCountry;
    }

    @Override
    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    @Override
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setFreightClass(String freightClass) {
        this.freightClass = freightClass;
    }

    @Override
    public Boolean isArType() {
        return true;
    }

    @Override
    public String getMessageType() {
        return EDIMessageType.AR.getCode();
    }

    @Override
    public String getShipmentNo() {
        return shipmentNo;
    }
}
