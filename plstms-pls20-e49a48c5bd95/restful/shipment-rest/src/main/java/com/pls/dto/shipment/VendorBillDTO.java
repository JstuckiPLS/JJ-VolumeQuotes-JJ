package com.pls.dto.shipment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.dto.address.PlainAddressDTO;
import com.pls.dto.enums.PaymentTermsDTO;

/**
 * DTO for Vendor Bill (Carrier Invoice).
 *
 * @author Mikhail Boldinov, 01/10/13
 */
public class VendorBillDTO {

    private Long id;

    private CarrierDTO carrier;

    private PlainAddressDTO originAddress;

    private PlainAddressDTO destinationAddress;

    private Date estimatedDeliveryDate;

    private Date actualDeliveryDate;

    private Date vendorBillDate;

    private Date actualPickupDate;

    private BigDecimal weight;

    private String vendorBillNumber;

    private PaymentTermsDTO payTerm;

    private String pro;

    private String bol;

    private String po;

    private String pu;

    private String quoteId;

    private BigDecimal amount;

    private Long matchedLoadId;

    private Boolean edi;

    private List<CarrierInvoiceLineItemDTO> lineItems = new ArrayList<CarrierInvoiceLineItemDTO>();

    private List<CarrierInvoiceCostItemDTO> costItems = new ArrayList<CarrierInvoiceCostItemDTO>();

    private Integer loadVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarrierDTO getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierDTO carrier) {
        this.carrier = carrier;
    }

    public PlainAddressDTO getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(PlainAddressDTO originAddress) {
        this.originAddress = originAddress;
    }

    public PlainAddressDTO getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(PlainAddressDTO destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Date getActualPickupDate() {
        return actualPickupDate;
    }

    public void setActualPickupDate(Date actualPickupDate) {
        this.actualPickupDate = actualPickupDate;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public Date getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public Date getVendorBillDate() {
        return vendorBillDate;
    }

    public void setVendorBillDate(Date vendorBillDate) {
        this.vendorBillDate = vendorBillDate;
    }

    public PaymentTermsDTO getPayTerm() {
        return payTerm;
    }

    public void setPayTerm(PaymentTermsDTO payTerm) {
        this.payTerm = payTerm;
    }

    public String getPu() {
        return pu;
    }

    public void setPu(String pu) {
        this.pu = pu;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public List<CarrierInvoiceLineItemDTO> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<CarrierInvoiceLineItemDTO> lineItems) {
        this.lineItems = lineItems;
    }

    public String getVendorBillNumber() {
        return vendorBillNumber;
    }

    public void setVendorBillNumber(String vendorBillNumber) {
        this.vendorBillNumber = vendorBillNumber;
    }

    public Long getMatchedLoadId() {
        return matchedLoadId;
    }

    public void setMatchedLoadId(Long matchedLoadId) {
        this.matchedLoadId = matchedLoadId;
    }

    public Boolean getEdi() {
        return edi;
    }

    public void setEdi(Boolean edi) {
        this.edi = edi;
    }

    public List<CarrierInvoiceCostItemDTO> getCostItems() {
        return costItems;
    }

    public void setCostItems(List<CarrierInvoiceCostItemDTO> costItems) {
        this.costItems = costItems;
    }

    public Integer getLoadVersion() {
        return loadVersion;
    }

    public void setLoadVersion(Integer loadVersion) {
        this.loadVersion = loadVersion;
    }
}
