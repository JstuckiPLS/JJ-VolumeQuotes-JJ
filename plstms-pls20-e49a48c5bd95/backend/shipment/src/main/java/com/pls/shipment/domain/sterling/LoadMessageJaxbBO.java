package com.pls.shipment.domain.sterling;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.domain.sterling.utils.DateTimeAdapter;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.domain.sterling.enums.LoadStatus;
import com.pls.shipment.domain.sterling.enums.OperationType;
import com.pls.shipment.domain.sterling.enums.SelectCarrierBy;
import com.pls.shipment.domain.sterling.enums.YesNo;

/**
 * Load message BO for sending data to the ActiveMQ. Envelope JAXB-oriented object.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlRootElement(name = "LtlLoadMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoadMessageJaxbBO implements IntegrationMessageBO {

    private static final long serialVersionUID = -1494731122598556916L;

    @XmlElement(name = "CustomerName")
    private String customerName;

    @XmlElement(name = "MessageType")
    private String messageType;

    @XmlElement(name = "Scac")
    private String scac;

    @XmlElement(name = "ShipmentNo")
    private String shipmentNo;

    @XmlElement(name = "ProNumber")
    private String proNumber;

    @XmlElement(name = "InboundOutbound")
    private String inboundOutbound;

    @XmlElement(name = "OperationType")
    private OperationType operationType;

    @XmlElement(name = "LoadStatus")
    private LoadStatus loadStatus;

    @XmlElement(name = "LoadId")
    private Long loadId;

    @XmlElement(name = "Priority")
    private String priority;

    @XmlElement(name = "PayTerms")
    private String payTerms;

    @XmlElement(name = "InvoiceNumber")
    private String invoiceNumber;

    @XmlElement(name = "InvoiceDate")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date invoiceDate;

    @XmlElement(name = "InvoiceAmount")
    private BigDecimal invoiceAmount;

    @XmlElement(name = "InvoiceDueDate")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date invoiceDueDate;

    @XmlElement(name = "InvoicePayTerms")
    private String invoicePayTerms;

    @XmlElement(name = "GlNumber")
    private String glNumber;

    @XmlElement(name = "Bol")
    private String bol;

    @XmlElement(name = "PoNum")
    private String poNum;

    @XmlElement(name = "OriginatingSystem")
    private String originatingSystem;

    @XmlElement(name = "FileType")
    private String fileType;

    @XmlElement(name = "B2biFileName")
    private String b2biFileName;

    @XmlElement(name = "PickupNum")
    private String pickupNum;

    @XmlElement(name = "PreferredCarrier")
    private YesNo preferredCarrier;

    @XmlElement(name = "SoNum")
    private String soNum;

    @XmlElement(name = "TrailerNum")
    private String trailerNum;

    @XmlElement(name = "PickupNotes")
    private String pickupNotes;

    @XmlElement(name = "DeliveryNotes")
    private String deliveryNotes;

    @XmlElement(name = "ShippingLabelNotes")
    private String shippingLabelNotes;

    @XmlElement(name = "GuaranteedBy")
    private String guaranteedBy;

    @XmlElement(name = "ProhibitedCommodities")
    private String prohibitedCommodities;

    @XmlElement(name = "MessageByCarrier")
    private String messageByCarrier;

    @XmlElement(name = "MessageByCustomer")
    private String messageByCustomer;

    @XmlElement(name = "SelectCarrierBy")
    private SelectCarrierBy selectCarrierBy;

    @XmlElement(name = "TotalMiles")
    private int totalMiles;

    @XmlElement(name = "TotalWeight")
    private BigDecimal totalWeight;

    @XmlElement(name = "TotalPieces")
    private int totalPieces = 0;

    @XmlElement(name = "TotalQuantity")
    private Integer totalQuantity = 0;

    @XmlElement(name = "TotalCost")
    private BigDecimal totalCost;

    @XmlElement(name = "TotalRevenue")
    private BigDecimal totalRevenue;

    @XmlElement(name = "TotalBenchmark")
    private BigDecimal totalBenchmark;

    @XmlElement(name = "OverrideCost")
    private YesNo overrideCost;

    @XmlElement(name = "OverrideRevenue")
    private YesNo overrideRevenue;

    @XmlElement(name = "VolumeQuoteId")
    private String volumeQuoteId;

    @XmlElement(name = "UseHighValueFor")
    private UseHighValueForJaxbBO useHighValueFor;

    @XmlElement(name = "PersonId")
    private Long personId;

    @XmlElement(name = "CustomerOrgId")
    private Long customerOrgId;

    @XmlElement(name = "CustomerLocationId")
    private Long customerLocationId;

    @XmlElement(name = "CustomerLocName")
    private String customerLocName;

    @XmlElement(name = "CustomerBillToId")
    private Long customerBillToId;

    @XmlElement(name = "BillingCurrencyCode")
    private String billingCurrencyCode;

    @XmlElement(name = "PaymentCurrencyCode")
    private String paymentCurrencyCode;

    @XmlElement(name = "ExchangeRate")
    private BigDecimal exchangeRate;

    @XmlElement(name = "EdiAccountNum")
    private String ediAccountNum;

    @XmlElement(name = "RecvDateTime")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date recvDateTime;

    @XmlElement(name = "Notes")
    private String notes;

    @XmlElement(name = "NotesAuthor")
    private Long notesAuthor;

    @XmlElement(name = "NotesDate")
    private String notesDate;

    @XmlElement(name = "NotesTime")
    private String notesTime;

    @XmlElementWrapper(name = "AddlRefNumbers")
    @XmlElement(name = "AddlRefNumber")
    private List<AddlRefNumberJaxbBO> addlRefNumbers;

    @XmlElementWrapper(name = "Materials")
    @XmlElement(name = "Material")
    private List<MaterialJaxbBO> materials;

    @XmlElementWrapper(name = "Addresses")
    @XmlElement(name = "Address")
    private List<AddressJaxbBO> addresses;

    @XmlElementWrapper(name = "TransactionDates")
    @XmlElement(name = "TransactionDate")
    private List<TransactionDateJaxbBO> transactionDates;

    @XmlElementWrapper(name = "TrackingNotifications")
    @XmlElement(name = "TrackingNotification")
    private List<TrackingNotificationJaxbBO> trackingNotifications;

    @XmlElementWrapper(name = "BaseRates")
    @XmlElement(name = "BaseRate")
    private List<BaseRateJaxbBO> baseRates;

    @XmlElementWrapper(name = "Accessorials")
    @XmlElement(name = "Accessorial")
    private List<AccessorialJaxbBO> accessorials;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getShipmentNo() {
        return shipmentNo;
    }

    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;
    }

    public String getProNumber() {
        return proNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public String getInboundOutbound() {
        return inboundOutbound;
    }

    public void setInboundOutbound(String inboundOutbound) {
        this.inboundOutbound = inboundOutbound;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public LoadStatus getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(LoadStatus loadStatus) {
        this.loadStatus = loadStatus;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPayTerms() {
        return payTerms;
    }

    public void setPayTerms(String payTerms) {
        this.payTerms = payTerms;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Date getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public void setInvoiceDueDate(Date invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    public String getInvoicePayTerms() {
        return invoicePayTerms;
    }

    public void setInvoicePayTerms(String invoicePayTerms) {
        this.invoicePayTerms = invoicePayTerms;
    }

    public String getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
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

    public String getOriginatingSystem() {
        return originatingSystem;
    }

    public void setOriginatingSystem(String originatingSystem) {
        this.originatingSystem = originatingSystem;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getB2biFileName() {
        return b2biFileName;
    }

    public void setB2biFileName(String b2biFileName) {
        this.b2biFileName = b2biFileName;
    }

    public String getPickupNum() {
        return pickupNum;
    }

    public void setPickupNum(String pickupNum) {
        this.pickupNum = pickupNum;
    }

    public YesNo getPreferredCarrier() {
        return preferredCarrier;
    }

    public void setPreferredCarrier(YesNo preferredCarrier) {
        this.preferredCarrier = preferredCarrier;
    }

    public String getSoNum() {
        return soNum;
    }

    public void setSoNum(String soNum) {
        this.soNum = soNum;
    }

    public String getTrailerNum() {
        return trailerNum;
    }

    public void setTrailerNum(String trailerNum) {
        this.trailerNum = trailerNum;
    }

    public String getPickupNotes() {
        return pickupNotes;
    }

    public void setPickupNotes(String pickupNotes) {
        this.pickupNotes = pickupNotes;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public String getShippingLabelNotes() {
        return shippingLabelNotes;
    }

    public void setShippingLabelNotes(String shippingLabelNotes) {
        this.shippingLabelNotes = shippingLabelNotes;
    }

    public String getGuaranteedBy() {
        return guaranteedBy;
    }

    public void setGuaranteedBy(String guaranteedBy) {
        this.guaranteedBy = guaranteedBy;
    }

    public String getProhibitedCommodities() {
        return prohibitedCommodities;
    }

    public void setProhibitedCommodities(String prohibitedCommodities) {
        this.prohibitedCommodities = prohibitedCommodities;
    }

    public String getMessageByCarrier() {
        return messageByCarrier;
    }

    public void setMessageByCarrier(String messageByCarrier) {
        this.messageByCarrier = messageByCarrier;
    }

    public String getMessageByCustomer() {
        return messageByCustomer;
    }

    public void setMessageByCustomer(String messageByCustomer) {
        this.messageByCustomer = messageByCustomer;
    }

    public SelectCarrierBy getSelectCarrierBy() {
        return selectCarrierBy;
    }

    public void setSelectCarrierBy(SelectCarrierBy selectCarrierBy) {
        this.selectCarrierBy = selectCarrierBy;
    }

    public int getTotalMiles() {
        return totalMiles;
    }

    public void setTotalMiles(int totalMiles) {
        this.totalMiles = totalMiles;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getTotalPieces() {
        return totalPieces;
    }

    public void setTotalPieces(int totalPieces) {
        this.totalPieces = totalPieces;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalBenchmark() {
        return totalBenchmark;
    }

    public void setTotalBenchmark(BigDecimal totalBenchmark) {
        this.totalBenchmark = totalBenchmark;
    }

    public YesNo getOverrideCost() {
        return overrideCost;
    }

    public void setOverrideCost(YesNo overrideCost) {
        this.overrideCost = overrideCost;
    }

    public YesNo getOverrideRevenue() {
        return overrideRevenue;
    }

    public void setOverrideRevenue(YesNo overrideRevenue) {
        this.overrideRevenue = overrideRevenue;
    }

    public String getVolumeQuoteId() {
        return volumeQuoteId;
    }

    public void setVolumeQuoteId(String volumeQuoteId) {
        this.volumeQuoteId = volumeQuoteId;
    }

    public UseHighValueForJaxbBO getUseHighValueFor() {
        return useHighValueFor;
    }

    public void setUseHighValueFor(UseHighValueForJaxbBO useHighValueFor) {
        this.useHighValueFor = useHighValueFor;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getCustomerOrgId() {
        return customerOrgId;
    }

    public void setCustomerOrgId(Long customerOrgId) {
        this.customerOrgId = customerOrgId;
    }

    public Long getCustomerLocationId() {
        return customerLocationId;
    }

    public void setCustomerLocationId(Long customerLocationId) {
        this.customerLocationId = customerLocationId;
    }

    public String getCustomerLocName() {
        return customerLocName;
    }

    public void setCustomerLocName(String customerLocName) {
        this.customerLocName = customerLocName;
    }

    public Long getCustomerBillToId() {
        return customerBillToId;
    }

    public void setCustomerBillToId(Long customerBillToId) {
        this.customerBillToId = customerBillToId;
    }

    public String getBillingCurrencyCode() {
        return billingCurrencyCode;
    }

    public void setBillingCurrencyCode(String billingCurrencyCode) {
        this.billingCurrencyCode = billingCurrencyCode;
    }

    public String getPaymentCurrencyCode() {
        return paymentCurrencyCode;
    }

    public void setPaymentCurrencyCode(String paymentCurrencyCode) {
        this.paymentCurrencyCode = paymentCurrencyCode;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getEdiAccountNum() {
        return ediAccountNum;
    }

    public void setEdiAccountNum(String ediAccountNum) {
        this.ediAccountNum = ediAccountNum;
    }

    public Date getRecvDateTime() {
        return recvDateTime;
    }

    public void setRecvDateTime(Date recvDateTime) {
        this.recvDateTime = recvDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getNotesAuthor() {
        return notesAuthor;
    }

    public void setNotesAuthor(Long notesAuthor) {
        this.notesAuthor = notesAuthor;
    }

    public String getNotesDate() {
        return notesDate;
    }

    public void setNotesDate(String notesDate) {
        this.notesDate = notesDate;
    }

    public String getNotesTime() {
        return notesTime;
    }

    public void setNotesTime(String notesTime) {
        this.notesTime = notesTime;
    }

    public List<AddlRefNumberJaxbBO> getAddlRefNumbers() {
        return addlRefNumbers;
    }

    public void setAddlRefNumbers(List<AddlRefNumberJaxbBO> addlRefNumbers) {
        this.addlRefNumbers = addlRefNumbers;
    }

    /**
     * Adding AddlRefNumber to the list.
     * 
     * @param addlRefNumber
     *            AddlRefNumber to add
     * */
    public void addAddlRefNumber(AddlRefNumberJaxbBO addlRefNumber) {
        if (addlRefNumbers == null) {
            this.addlRefNumbers = new ArrayList<AddlRefNumberJaxbBO>();
        }
        this.addlRefNumbers.add(addlRefNumber);
    }

    public List<AddressJaxbBO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressJaxbBO> addresses) {
        this.addresses = addresses;
    }

    /**
     * Adding address to list.
     * 
     * @param address
     *            Address to add
     * */
    public void addAddress(AddressJaxbBO address) {
        if (addresses == null) {
            this.addresses = new ArrayList<AddressJaxbBO>();
        }
        this.addresses.add(address);
    }

    /**
     * Returns address by address type - origin, destination, bill to etc.
     * 
     * @param type
     *            Address type
     * @return Address for the address type
     */
    public AddressJaxbBO getAddress(AddressType type) {
        if (addresses != null) {
            for (AddressJaxbBO address : addresses) {
                if (address.getAddressType() == type) {
                    return address;
                }
            }
        }

        return null;
    }

    public List<TransactionDateJaxbBO> getTransactionDates() {
        return transactionDates;
    }

    public void setTransactionDates(List<TransactionDateJaxbBO> transactionDates) {
        this.transactionDates = transactionDates;
    }

    public List<TrackingNotificationJaxbBO> getTrackingNotifications() {
        return trackingNotifications;
    }

    public void setTrackingNotifications(List<TrackingNotificationJaxbBO> trackingNotifications) {
        this.trackingNotifications = trackingNotifications;
    }

    public List<BaseRateJaxbBO> getBaseRates() {
        return baseRates;
    }

    public void setBaseRates(List<BaseRateJaxbBO> baseRates) {
        this.baseRates = baseRates;
    }

    /**
     * Adding base rate to list.
     * 
     * @param baseRate
     *            adding base rate
     * */
    public void addBaseRate(BaseRateJaxbBO baseRate) {
        if (baseRates == null) {
            this.baseRates = new ArrayList<BaseRateJaxbBO>();
        }
        this.baseRates.add(baseRate);
    }

    public List<AccessorialJaxbBO> getAccessorials() {
        return accessorials;
    }

    public void setAccessorials(List<AccessorialJaxbBO> accessorials) {
        this.accessorials = accessorials;
    }

    /**
     * Adding accessorial to list.
     * 
     * @param accessorial
     *            accessorial for material
     * */
    public void addAccessorial(AccessorialJaxbBO accessorial) {
        if (accessorials == null) {
            this.accessorials = new ArrayList<AccessorialJaxbBO>();
        }
        this.accessorials.add(accessorial);
    }

    public List<MaterialJaxbBO> getMaterials() {
        return materials;
    }

    /**
     * Set the materials for the load. And calculate the total quantity and total pieces for the load from the materials.
     * 
     * @param materials
     *            materials for the load.
     */
    public void setMaterials(List<MaterialJaxbBO> materials) {
        this.materials = materials;

        for (MaterialJaxbBO material : materials) {
            totalQuantity += material.getQuantity() == null ? 0 : material.getQuantity();
            totalPieces += material.getPieces() == null ? 0 : material.getPieces();
        }
    }

    /**
     * Adding a materials to list.
     * 
     * @param material
     *            material object
     * */
    public void addMaterial(MaterialJaxbBO material) {
        if (materials == null) {
            this.materials = new ArrayList<MaterialJaxbBO>();
        }
        this.materials.add(material);
        totalQuantity += material.getQuantity() == null ? 0 : material.getQuantity();
        totalPieces += material.getPieces() == null ? 0 : material.getPieces();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getScac()).append(getShipmentNo()).append(getLoadId()).append(getBol())
                .append(getCustomerOrgId()).append(getCustomerName()).append(getProNumber()).append(getOperationType()).append(getInboundOutbound())
                .append(getLoadStatus()).append(getPriority()).append(getPayTerms()).append(getInvoiceNumber()).append(getInvoiceDate())
                .append(getInvoiceAmount()).append(getInvoiceDueDate()).append(getInvoicePayTerms()).append(getB2biFileName())
                .append(getBillingCurrencyCode()).append(getPaymentCurrencyCode()).append(getExchangeRate()).append(getEdiAccountNum())
                .append(getGlNumber()).append(getPoNum()).append(getOriginatingSystem()).append(getFileType()).append(getPickupNum())
                .append(getPreferredCarrier()).append(getSoNum()).append(getTrailerNum()).append(getPickupNotes()).append(getDeliveryNotes())
                .append(getShippingLabelNotes()).append(getGuaranteedBy()).append(getProhibitedCommodities()).append(getMessageByCarrier())
                .append(getMessageByCustomer()).append(getSelectCarrierBy()).append(getTotalMiles()).append(getTotalWeight())
                .append(getTotalPieces()).append(getTotalQuantity()).append(getTotalCost()).append(getTotalRevenue()).append(getTotalBenchmark())
                .append(getOverrideCost()).append(getOverrideRevenue()).append(getVolumeQuoteId()).append(getUseHighValueFor()).append(getPersonId())
                .append(getCustomerLocationId()).append(getCustomerLocName()).append(getCustomerBillToId()).append(getAddresses())
                .append(getMaterials()).append(getBaseRates()).append(getAccessorials()).append(getTransactionDates())
                .append(getTrackingNotifications()).append(getAddlRefNumbers());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LoadMessageJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                LoadMessageJaxbBO other = (LoadMessageJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getScac(), other.getScac()).append(getShipmentNo(), other.getShipmentNo())
                        .append(getLoadId(), other.getLoadId()).append(getBol(), other.getBol()).append(getCustomerOrgId(), other.getCustomerOrgId())
                        .append(getCustomerName(), other.getCustomerName()).append(getProNumber(), other.getProNumber())
                        .append(getOperationType(), other.getOperationType()).append(getInboundOutbound(), other.getInboundOutbound())
                        .append(getLoadStatus(), other.getLoadStatus()).append(getPriority(), other.getPriority())
                        .append(getPayTerms(), other.getPayTerms()).append(getInvoiceNumber(), other.getInvoiceNumber())
                        .append(getInvoiceDate(), other.getInvoiceDate()).append(getInvoiceAmount(), other.getInvoiceAmount())
                        .append(getInvoiceDueDate(), other.getInvoiceDueDate()).append(getInvoicePayTerms(), other.getInvoicePayTerms())
                        .append(getB2biFileName(), other.getB2biFileName()).append(getBillingCurrencyCode(), other.getBillingCurrencyCode())
                        .append(getPaymentCurrencyCode(), other.getPaymentCurrencyCode()).append(getExchangeRate(), other.getExchangeRate())
                        .append(getEdiAccountNum(), other.getEdiAccountNum()).append(getGlNumber(), other.getGlNumber())
                        .append(getPoNum(), other.getPoNum()).append(getOriginatingSystem(), other.getOriginatingSystem())
                        .append(getFileType(), other.getFileType()).append(getPickupNum(), other.getPickupNum())
                        .append(getPreferredCarrier(), other.getPreferredCarrier()).append(getSoNum(), other.getSoNum())
                        .append(getTrailerNum(), other.getTrailerNum()).append(getPickupNotes(), other.getPickupNotes())
                        .append(getDeliveryNotes(), other.getDeliveryNotes()).append(getShippingLabelNotes(), other.getShippingLabelNotes())
                        .append(getGuaranteedBy(), other.getGuaranteedBy()).append(getProhibitedCommodities(), other.getProhibitedCommodities())
                        .append(getMessageByCarrier(), other.getMessageByCarrier()).append(getMessageByCustomer(), other.getMessageByCustomer())
                        .append(getSelectCarrierBy(), other.getSelectCarrierBy()).append(getTotalMiles(), other.getTotalMiles())
                        .append(getTotalWeight(), other.getTotalWeight()).append(getTotalPieces(), other.getTotalPieces())
                        .append(getTotalQuantity(), other.getTotalQuantity()).append(getTotalCost(), other.getTotalCost())
                        .append(getTotalRevenue(), other.getTotalRevenue()).append(getTotalBenchmark(), other.getTotalBenchmark())
                        .append(getOverrideCost(), other.getOverrideCost()).append(getOverrideRevenue(), other.getOverrideRevenue())
                        .append(getVolumeQuoteId(), other.getVolumeQuoteId()).append(getUseHighValueFor(), other.getUseHighValueFor())
                        .append(getPersonId(), other.getPersonId()).append(getCustomerLocationId(), other.getCustomerLocationId())
                        .append(getCustomerLocName(), other.getCustomerLocName()).append(getCustomerBillToId(), other.getCustomerBillToId())
                        .append(getAddresses(), other.getAddresses()).append(getMaterials(), other.getMaterials())
                        .append(getBaseRates(), other.getBaseRates()).append(getAccessorials(), other.getAccessorials())
                        .append(getTransactionDates(), other.getTransactionDates())
                        .append(getTrackingNotifications(), other.getTrackingNotifications()).append(getAddlRefNumbers(), other.getAddlRefNumbers());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("Scac", getScac()).append("ShipmentNo", getShipmentNo())
                .append("LoadId", getLoadId()).append("Bol", getBol()).append("CustomerOrgId", getCustomerOrgId())
                .append("CustomerName", getCustomerName()).append("ProNumber", getProNumber()).append("OperationType", getOperationType())
                .append("InboundOutbound", getInboundOutbound()).append("LoadStatus", getLoadStatus()).append("Priority", getPriority())
                .append("PayTerms", getPayTerms()).append("InvoiceNumber", getInvoiceNumber()).append("InvoiceDate", getInvoiceDate())
                .append("InvoiceAmount", getInvoiceAmount()).append("InvoiceDueDate", getInvoiceDueDate())
                .append("InvoicePayTerms", getInvoicePayTerms()).append("B2biFileName", getB2biFileName())
                .append("BillingCurrencyCode", getBillingCurrencyCode()).append("PaymentCurrencyCode", getPaymentCurrencyCode())
                .append("ExchangeRate", getExchangeRate()).append("EdiAccountNum", getEdiAccountNum()).append("RecvDateTime", getRecvDateTime())
                .append("GlNumber", getGlNumber()).append("PoNum", getPoNum()).append("OriginatingSystem", getOriginatingSystem())
                .append("FileType", getFileType()).append("PickupNum", getPickupNum()).append("PreferredCarrier", getPreferredCarrier())
                .append("SoNum", getSoNum()).append("TrailerNum", getTrailerNum()).append("PickupNotes", getPickupNotes())
                .append("DeliveryNotes", getDeliveryNotes()).append("ShippingLabelNotes", getShippingLabelNotes())
                .append("GuaranteedBy", getGuaranteedBy()).append("ProhibitedCommodities", getProhibitedCommodities())
                .append("MessageByCarrier", getMessageByCarrier()).append("MessageByCustomer", getMessageByCustomer())
                .append("SelectCarrierBy", getSelectCarrierBy()).append("TotalMiles", getTotalMiles()).append("TotalWeight", getTotalWeight())
                .append("TotalPieces", getTotalPieces()).append("TotalQuantity", getTotalQuantity()).append("TotalCost", getTotalCost())
                .append("TotalRevenue", getTotalRevenue()).append("TotalBenchmark", getTotalBenchmark()).append("OverrideCost", getOverrideCost())
                .append("OverrideRevenue", getOverrideRevenue()).append("VolumeQuoteId", getVolumeQuoteId())
                .append("UseHighValueFor", getUseHighValueFor()).append("PersonId", getPersonId())
                .append("CustomerLocationId", getCustomerLocationId()).append("CustomerLocName", getCustomerLocName())
                .append("Addresses", getAddresses()).append("Materials", getMaterials()).append("BaseRates", getBaseRates())
                .append("Accessorials", getAccessorials()).append("TransactionDates", getTransactionDates())
                .append("TrackingNotifications", getTrackingNotifications()).append("AddlRefNumbers", getAddlRefNumbers());

        return builder.toString();
    }
}
