package com.pls.invoice.domain.xml.finance.vendinvoice;

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
import com.pls.invoice.domain.xml.finance.FinanceInfoTable;

/**
 * VendInvoiceInfoTable JAXB-oriented object.
 *
 * @author Denis Zhupinsky (Team International)
 */
@XmlRootElement(name = "VendorInvoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class VendInvoiceInfoTable implements IntegrationMessageBO, FinanceInfoTable {

    private static final long serialVersionUID = 7452085486419056280L;

    @XmlElement(name = "Operation")
    private String operation = "CREATE";

    @XmlElement(name = "Carrier")
    private String carrier;

    @XmlElement(name = "Approved")
    private String approved = "Yes";

    @XmlElement(name = "Currency")
    private String currency;

    @XmlElement(name = "BusinessUnit")
    private String businessUnit;

    @XmlElement(name = "CostCenter")
    private String costCenter;

    @XmlElement(name = "Department")
    private String department = "SL";

    @XmlElement(name = "FrtBillRecvDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date frtBillRecvDate;

    @XmlElement(name = "ShipmentNum")
    private String shipmentNo;

    @XmlElement(name = "LoadNumber")
    protected String loadNumber;

    @XmlElement(name = "ApTerms")
    private String apTerms;

    @XmlElement(name = "AdjDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date adjDate;

    @XmlElement(name = "Scac")
    private String scac;

    @XmlElement(name = "DestCity")
    private String destCity;

    @XmlElement(name = "DestState")
    private String destState;

    @XmlElement(name = "DestCountry")
    private String destCountry;

    @XmlElement(name = "GlDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date glDate;

    @XmlElement(name = "InvoiceNum")
    private String invoiceNum;

    @XmlElement(name = "NetworkId")
    private String networkId;

    @XmlElement(name = "OriginCity")
    private String originCity;

    @XmlElement(name = "OriginState")
    private String originState;

    @XmlElement(name = "OriginCountry")
    private String originCountry;

    @XmlElement(name = "PoNumber")
    private String poNumber;

    @XmlElement(name = "ProNumber")
    private String proNumber;

    @XmlElement(name = "ShipDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date shipDate;

    @XmlElement(name = "CarrierName")
    protected String carrierName;

    @XmlElement(name = "BillerType")
    private String billerType;

    @XmlElement(name = "LoadId")
    private Long loadId;

    @XmlElement(name = "FaaDetailId")
    private String faaDetailId;

    @XmlElement(name = "RequestId")
    private String requestId;

    @XmlElement(name = "VendInvoiceLineItem")
    private List<VendInvoiceInfoLine> vendInvoiceInfoLines;

    @XmlTransient
    private Long personId;

    @XmlElement(name = "Bol")
    private String bol;

    @XmlElement(name = "CustomerOrgId")
    private Long customerOrgId;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDestCountry() {
        return destCountry;
    }

    public void setDestCountry(String destCountry) {
        this.destCountry = destCountry;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public List<VendInvoiceInfoLine> getVendInvoiceInfoLines() {
        return vendInvoiceInfoLines;
    }

    public void setVendInvoiceInfoLines(List<VendInvoiceInfoLine> vendInvoiceInfoLines) {
        this.vendInvoiceInfoLines = vendInvoiceInfoLines;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public Date getFrtBillRecvDate() {
        return frtBillRecvDate;
    }

    public void setFrtBillRecvDate(Date frtBillRecvDate) {
        this.frtBillRecvDate = frtBillRecvDate;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getLoadNumber() {
        return loadNumber;
    }

    public void setLoadNumber(String loadNumber) {
        this.loadNumber = loadNumber;
    }

    public Date getGlDate() {
        return glDate;
    }

    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }

    public String getBillerType() {
        return billerType;
    }

    public void setBillerType(String billerType) {
        this.billerType = billerType;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFaaDetailId() {
        return faaDetailId;
    }

    @Override
    public void setFaaDetailId(String faaDetailId) {
        if (faaDetailId == null) {
            this.faaDetailId = "-1";
        } else {
            this.faaDetailId = faaDetailId;
        }
    }

    public String getShipmentNo() {
        return shipmentNo;
    }

    @Override
    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;

    }

    public String getBol() {
        return bol;
    }

    @Override
    public void setBol(String bol) {
        this.bol = bol;
    }

    public Long getCustomerOrgId() {
        return customerOrgId;
    }

    @Override
    public void setCustomerOrgId(Long customerOrgId) {
        this.customerOrgId = customerOrgId;
    }

    public String getApTerms() {
        return apTerms;
    }

    public void setApTerms(String apTerms) {
        this.apTerms = apTerms;
    }

    public Date getAdjDate() {
        return adjDate;
    }

    public void setAdjDate(Date adjDate) {
        this.adjDate = adjDate;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
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

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginState() {
        return originState;
    }

    public void setOriginState(String originState) {
        this.originState = originState;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;

    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public String getProNumber() {
        return proNumber;
    }

    /**
     * Set the pro number/carrier reference number on the load.
     * 
     * @param proNumber
     *            pro number to set
     */
    public void setProNumber(String proNumber) {
        if (proNumber != null) {
            this.proNumber = proNumber.replaceAll("\\n", "");
        }
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    @Override
    public void setCarrier(String carrier) {
        this.carrier = carrier;

    }

    @Override
    public Boolean isArType() {
        return false;
    }

    public String getMessageType() {
        return EDIMessageType.AP.getCode();
    }
}
