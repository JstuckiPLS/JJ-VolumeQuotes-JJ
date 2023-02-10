package com.pls.dto.shipment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.enums.ManualBolStatus;
import com.pls.dto.FreightBillPayToDTO;
import com.pls.dto.address.BillToDTO;
import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dto.enums.PaymentTermsDTO;

/**
 * Manual Bol DTO.
 *
 * @author Alexander Nalapko
 */
public class ManualBolDTO {

    private Long id;

    private ManualBolStatus status;

    private Long organizationId;

    private String customerName;

    private CarrierDTO carrier;

    private String bol;

    private String pro;

    private String po;

    private String pu;

    private String ref;

    private FreightBillPayToDTO freightBillPayTo;

    private String glNumber;

    private String opBolNumber;

    private String partNumber;

    private String soNumber;

    private String trailer;

    private BillToDTO billTo;

    private PaymentTermsDTO paymentTerms;

    private ShipmentLocationBO location;

    private ManualBolAddressDTO origin;

    private ManualBolAddressDTO destination;

    private Date pickupDate;

    private String pickupNotes;

    private String shippingNotes;

    private String deliveryNotes;

    private String requestedBy;

    private PickupAndDeliveryWindowDTO pickupWindowFrom;

    private PickupAndDeliveryWindowDTO pickupWindowTo;

    private PickupAndDeliveryWindowDTO deliveryWindowFrom;

    private PickupAndDeliveryWindowDTO deliveryWindowTo;

    private String customsBroker;

    private PhoneBO customsBrokerPhone;

    private List<ManualBolMaterialDTO> materials;

    private List<JobNumberDTO> jobNumbers;

    private BigDecimal cargoValue;

    private Integer version;

    private String shipmentDirection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ManualBolStatus getStatus() {
        return status;
    }

    public void setStatus(ManualBolStatus status) {
        this.status = status;
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

    public CarrierDTO getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierDTO carrier) {
        this.carrier = carrier;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getPu() {
        return pu;
    }

    public void setPu(String pu) {
        this.pu = pu;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public FreightBillPayToDTO getFreightBillPayTo() {
        return freightBillPayTo;
    }

    public void setFreightBillPayTo(FreightBillPayToDTO freightBillPayTo) {
        this.freightBillPayTo = freightBillPayTo;
    }

    public String getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }

    public String getOpBolNumber() {
        return opBolNumber;
    }

    public void setOpBolNumber(String opBolNumber) {
        this.opBolNumber = opBolNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public BillToDTO getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToDTO billTo) {
        this.billTo = billTo;
    }

    public PaymentTermsDTO getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(PaymentTermsDTO paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public ShipmentLocationBO getLocation() {
        return location;
    }

    public void setLocation(ShipmentLocationBO location) {
        this.location = location;
    }

    public ManualBolAddressDTO getOrigin() {
        return origin;
    }

    public void setOrigin(ManualBolAddressDTO origin) {
        this.origin = origin;
    }

    public ManualBolAddressDTO getDestination() {
        return destination;
    }

    public void setDestination(ManualBolAddressDTO destination) {
        this.destination = destination;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupNotes() {
        return pickupNotes;
    }

    public void setPickupNotes(String pickupNotes) {
        this.pickupNotes = pickupNotes;
    }

    public String getShippingNotes() {
        return shippingNotes;
    }

    public void setShippingNotes(String shippingNotes) {
        this.shippingNotes = shippingNotes;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public PickupAndDeliveryWindowDTO getPickupWindowFrom() {
        return pickupWindowFrom;
    }

    public void setPickupWindowFrom(PickupAndDeliveryWindowDTO pickupWindowFrom) {
        this.pickupWindowFrom = pickupWindowFrom;
    }

    public PickupAndDeliveryWindowDTO getPickupWindowTo() {
        return pickupWindowTo;
    }

    public void setPickupWindowTo(PickupAndDeliveryWindowDTO pickupWindowTo) {
        this.pickupWindowTo = pickupWindowTo;
    }

    public PickupAndDeliveryWindowDTO getDeliveryWindowFrom() {
        return deliveryWindowFrom;
    }

    public void setDeliveryWindowFrom(PickupAndDeliveryWindowDTO deliveryWindowFrom) {
        this.deliveryWindowFrom = deliveryWindowFrom;
    }

    public PickupAndDeliveryWindowDTO getDeliveryWindowTo() {
        return deliveryWindowTo;
    }

    public void setDeliveryWindowTo(PickupAndDeliveryWindowDTO deliveryWindowTo) {
        this.deliveryWindowTo = deliveryWindowTo;
    }

    public String getCustomsBroker() {
        return customsBroker;
    }

    public void setCustomsBroker(String customsBroker) {
        this.customsBroker = customsBroker;
    }

    public PhoneBO getCustomsBrokerPhone() {
        return customsBrokerPhone;
    }

    public void setCustomsBrokerPhone(PhoneBO customsBrokerPhone) {
        this.customsBrokerPhone = customsBrokerPhone;
    }

    public List<ManualBolMaterialDTO> getMaterials() {
        return materials;
    }

    public void setMaterials(List<ManualBolMaterialDTO> materials) {
        this.materials = materials;
    }

    public List<JobNumberDTO> getJobNumbers() {
        return jobNumbers;
    }

    public void setJobNumbers(List<JobNumberDTO> jobNumbers) {
        this.jobNumbers = jobNumbers;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigDecimal getCargoValue() {
        return cargoValue;
    }

    public void setCargoValue(BigDecimal cargoValue) {
        this.cargoValue = cargoValue;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getShipmentDirection() {
        return shipmentDirection;
    }

    public void setShipmentDirection(String shipmentDirection) {
        this.shipmentDirection = shipmentDirection;
    }

}
