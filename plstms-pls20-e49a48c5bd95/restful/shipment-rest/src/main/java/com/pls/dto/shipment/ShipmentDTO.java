package com.pls.dto.shipment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.dto.FreightBillPayToDTO;
import com.pls.dto.address.BillToDTO;
import com.pls.dto.address.CustomsBrokerDTO;
import com.pls.dto.adjustment.AdjustmentDTO;
import com.pls.dto.enums.PaymentTermsDTO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;


/**
 * Shipment DTO.
 *
 * @author Gleb Zgonikov
 */
public class ShipmentDTO {
    /**
     * This guid should be filled on UI side and passed to the server to distinguish between requests from the
     * same browser but from different tabs.
     */
    private String guid;

    private Long id;

    private Integer version;

    private Long matchedVendorBillId;

    private Long organizationId;

    private String customerName;

    private ProductListPrimarySort productListPrimarySort;

    private Date createdDate;

    private ShipmentStatus status;

    private String invoiceNumber;

    private Date invoiceDate;

    private String proNumber;

    private String bolNumber;

    private ShipmentFinishOrderDTO finishOrder = new ShipmentFinishOrderDTO();

    private ShipmentDetailsDTO originDetails;

    private ShipmentDetailsDTO destinationDetails;

    private ShipmentLocationBO location;

    private BillToDTO billTo;

    private ShipmentProposalBO selectedProposition;

    private List<ShipmentProposalBO> proposals;

    private CustomsBrokerDTO customsBroker;

    private Long quoteId;

    private Long guaranteedBy;

    private final List<UploadedDocumentDTO> uploadedDocuments = new ArrayList<UploadedDocumentDTO>();

    private final List<Long> removedDocumentsIds = new ArrayList<Long>();

    private PaymentTermsDTO paymentTerms;

    private String shipmentDirection;

    private FreightBillPayToDTO freightBillPayTo;

    private AdjustmentDTO adjustments;

    private Set<DocumentTypes> regeneratedDocTypes = new HashSet<DocumentTypes>(2);

    private String volumeQuoteID;

    private String savedQuoteID;

    private String quoteRef;

    private String quotedTotalRevenue;

    private String quotedTotalCost;

    private Boolean requirePermissionChecking;

    private Date freightBillDate;

    private Boolean isVendorBillMatched;

    private Date vendorBillDate;

    private Long markup;

    private BigDecimal cargoValue;

    private List<ShipmentNoteDTO> notes = new ArrayList<ShipmentNoteDTO>();

    private ShipmentFinancialStatus finalizationStatus;

    private List<PrepaidDetailDTO> prepaidDetails = new ArrayList<PrepaidDetailDTO>();

    private Boolean holdFinalizationStatus;

    private Boolean generateConsigneeInvoice;

    public Boolean isHoldFinalizationStatus() {
        return holdFinalizationStatus;
    }

    public void setHoldFinalizationStatus(Boolean holdFinalizationStatus) {
        this.holdFinalizationStatus = holdFinalizationStatus;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getMatchedVendorBillId() {
        return matchedVendorBillId;
    }

    public void setMatchedVendorBillId(Long matchedVendorBillId) {
        this.matchedVendorBillId = matchedVendorBillId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public ProductListPrimarySort getProductListPrimarySort() {
        return productListPrimarySort;
    }

    public void setProductListPrimarySort(ProductListPrimarySort productListPrimarySort) {
        this.productListPrimarySort = productListPrimarySort;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
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

    public ShipmentLocationBO getLocation() {
        return location;
    }

    public void setLocation(ShipmentLocationBO location) {
        this.location = location;
    }

    public BillToDTO getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToDTO billTo) {
        this.billTo = billTo;
    }

    public ShipmentProposalBO getSelectedProposition() {
        return selectedProposition;
    }

    public void setSelectedProposition(ShipmentProposalBO selectedProposition) {
        this.selectedProposition = selectedProposition;
    }

    public String getProNumber() {
        return proNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public ShipmentFinishOrderDTO getFinishOrder() {
        return finishOrder;
    }

    public void setFinishOrder(ShipmentFinishOrderDTO finishOrder) {
        this.finishOrder = finishOrder;
    }

    public ShipmentDetailsDTO getOriginDetails() {
        return originDetails;
    }

    public void setOriginDetails(ShipmentDetailsDTO originDetails) {
        this.originDetails = originDetails;
    }

    public ShipmentDetailsDTO getDestinationDetails() {
        return destinationDetails;
    }

    public void setDestinationDetails(ShipmentDetailsDTO destinationDetails) {
        this.destinationDetails = destinationDetails;
    }

    public CustomsBrokerDTO getCustomsBroker() {
        return customsBroker;
    }

    public void setCustomsBroker(CustomsBrokerDTO customsBroker) {
        this.customsBroker = customsBroker;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public Long getGuaranteedBy() {
        return guaranteedBy;
    }

    public void setGuaranteedBy(Long guaranteedBy) {
        this.guaranteedBy = guaranteedBy;
    }

    public List<Long> getRemovedDocumentsIds() {
        return removedDocumentsIds;
    }

    public List<UploadedDocumentDTO> getUploadedDocuments() {
        return uploadedDocuments;
    }

    public PaymentTermsDTO getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(PaymentTermsDTO paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getShipmentDirection() {
        return shipmentDirection;
    }

    public void setShipmentDirection(String shipmentDirection) {
        this.shipmentDirection = shipmentDirection;
    }

    public FreightBillPayToDTO getFreightBillPayTo() {
        return freightBillPayTo;
    }

    public void setFreightBillPayTo(FreightBillPayToDTO freightBillPayTo) {
        this.freightBillPayTo = freightBillPayTo;
    }

    public AdjustmentDTO getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(AdjustmentDTO adjustments) {
        this.adjustments = adjustments;
    }

    public Set<DocumentTypes> getRegeneratedDocTypes() {
        return regeneratedDocTypes;
    }

    public void setRegeneratedDocTypes(Set<DocumentTypes> regeneratedDocTypes) {
        this.regeneratedDocTypes = regeneratedDocTypes;
    }

    public String getVolumeQuoteID() {
        return volumeQuoteID;
    }

    public void setVolumeQuoteID(String volumeQuoteID) {
        this.volumeQuoteID = volumeQuoteID;
    }

    public String getSavedQuoteID() {
        return savedQuoteID;
    }

    public void setSavedQuoteID(String savedQuoteID) {
        this.savedQuoteID = savedQuoteID;
    }

    public String getQuoteRef() {
        return quoteRef;
    }

    public void setQuoteRef(String quoteRef) {
        this.quoteRef = quoteRef;
    }

    public String getQuotedTotalRevenue() {
        return quotedTotalRevenue;
    }

    public void setQuotedTotalRevenue(String quotedTotalRevenue) {
        this.quotedTotalRevenue = quotedTotalRevenue;
    }

    public String getQuotedTotalCost() {
        return quotedTotalCost;
    }

    public void setQuotedTotalCost(String quotedTotalCost) {
        this.quotedTotalCost = quotedTotalCost;
    }

    public Boolean getRequirePermissionChecking() {
        return requirePermissionChecking;
    }

    public void setRequirePermissionChecking(Boolean requirePermissionChecking) {
        this.requirePermissionChecking = requirePermissionChecking;
    }

    public List<ShipmentNoteDTO> getNotes() {
        return notes;
    }

    public void setNotes(List<ShipmentNoteDTO> notes) {
        this.notes = notes;
    }

    public List<ShipmentProposalBO> getProposals() {
        return proposals;
    }

    public void setProposals(List<ShipmentProposalBO> proposals) {
        this.proposals = proposals;
    }

    public Date getFreightBillDate() {
        return freightBillDate;
    }

    public void setFreightBillDate(Date freightBillDate) {
        this.freightBillDate = freightBillDate;
    }

    public Boolean getIsVendorBillMatched() {
        return isVendorBillMatched;
    }

    public void setIsVendorBillMatched(Boolean isVendorBillMatched) {
        this.isVendorBillMatched = isVendorBillMatched;
    }

    public Date getVendorBillDate() {
        return vendorBillDate;
    }

    public void setVendorBillDate(Date vendorBillDate) {
        this.vendorBillDate = vendorBillDate;
    }

    public Long getMarkup() {
        return markup;
    }

    public void setMarkup(Long markup) {
        this.markup = markup;
    }

    public BigDecimal getCargoValue() {
        return cargoValue;
    }

    public void setCargoValue(BigDecimal cargoValue) {
        this.cargoValue = cargoValue;
    }

    public ShipmentFinancialStatus getFinalizationStatus() {
        return finalizationStatus;
    }

    public void setFinalizationStatus(ShipmentFinancialStatus finalizationStatus) {
        this.finalizationStatus = finalizationStatus;
    }

    public List<PrepaidDetailDTO> getPrepaidDetails() {
        return prepaidDetails;
    }

    public void setPrepaidDetails(List<PrepaidDetailDTO> prepaidDetails) {
        this.prepaidDetails = prepaidDetails;
    }

    public Boolean isGenerateConsigneeInvoice() {
        return this.generateConsigneeInvoice;
    }

    public void setGenerateConsigneeInvoice(Boolean generateConsigneeInvoice) {
        this.generateConsigneeInvoice = generateConsigneeInvoice;
    }
}
