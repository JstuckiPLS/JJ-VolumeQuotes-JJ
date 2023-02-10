package com.pls.shipment.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.google.common.collect.ImmutableSet;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.ManualBolStatus;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.documentmanagement.domain.LoadDocumentEntity;

/**
 * Manual BOL Entity.
 * 
 * @author Alexander Nalapko
 */
@Entity
@Table(name = "MANUAL_BOL")
public class ManualBolEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = -8564582573704168783L;

    public static final String Q_FIND_FOR_CRITERIA = "com.pls.shipment.domain.ManualBolEntity.Q_FIND_FOR_CRITERIA";

    public static final String Q_CANCEL_BOL_BY_ID = "com.pls.shipment.domain.ManualBolEntity.Q_CANCEL_BOL_BY_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manual_bol_sequence")
    @SequenceGenerator(name = "manual_bol_sequence", sequenceName = "MANUAL_BOL_SEQ", allocationSize = 1)
    @Column(name = "MANUAL_BOL_ID")
    private Long id;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ManualBolStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ManualBolStatus status = ManualBolStatus.CUSTOMER_TRUCK;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID", nullable = false)
    private CustomerEntity organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_ORG_ID", nullable = false)
    private CarrierEntity carrier;

    @Embedded
    private ManualBolNumbersEntity numbers = new ManualBolNumbersEntity();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FRT_BILL_PAY_TO_ID")
    private FreightBillPayToEntity freightBillPayTo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO")
    private BillToEntity billTo;

    @Column(name = "PAY_TERMS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.PaymentTerms"),
            @Parameter(name = "identifierMethod", value = "getPaymentTermsCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private PaymentTerms paymentTerms = PaymentTerms.COLLECT;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID")
    private OrganizationLocationEntity location;

    @OneToMany(mappedBy = "manualBol", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ManualBolAddressEntity> addresses = new HashSet<ManualBolAddressEntity>();

    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "MANUAL_BOL_ID", referencedColumnName = "MANUAL_BOL_ID",
                    unique = true, nullable = true, insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "POINT_TYPE", value = "'O'")) })
    private ManualBolAddressEntity origin;

    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "MANUAL_BOL_ID", referencedColumnName = "MANUAL_BOL_ID",
                    unique = true, nullable = true, insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "POINT_TYPE", value = "'D'")) })
    private ManualBolAddressEntity destination;

    @OneToOne(mappedBy = "manualBol", cascade = CascadeType.ALL)
    private ManualBolRequestedByNoteEntity requestedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PICKUP_DATE")
    private Date pickupDate;

    @Column(name = "PICKUP_NOTES")
    private String pickupNotes;

    @Column(name = "SHIPPING_LABEL_NOTES")
    private String shippingNotes;

    @Column(name = "DELIVERY_NOTES")
    private String deliveryNotes;

    @Column(name = "SHIPPING_HOURS_OPERATION_TO")
    private Date pickupWindowTo;

    @Column(name = "SHIPPING_HOURS_OPERATION_FROM")
    private Date pickupWindowFrom;

    @Column(name = "RECEIVING_HOURS_OPERATION_FROM")
    private Date deliveryWindowFrom;

    @Column(name = "RECEIVING_HOURS_OPERATION_TO")
    private Date deliveryWindowTo;

    @Column(name = "CUSTOMS_BROKER")
    private String customsBroker;

    @Column(name = "CUSTOMS_BROKER_PHONE")
    private String customsBrokerPhone;

    @OneToMany(mappedBy = "manualBol", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ManualBolMaterialEntity> materials;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "MANUAL_BOL_ID")
    private List<LoadDocumentEntity> documents;

    @OneToMany
    @JoinColumns({ @JoinColumn(name = "ORG_ID", referencedColumnName = "ORG_ID"),
            @JoinColumn(name = "LOCATION_ID", referencedColumnName = "LOCATION_ID") })
    private Set<CustomerUserEntity> customerLocationUsers;

    @OneToOne(mappedBy = "manualBol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LoadAdditionalFieldsEntity loadAdditionalFields;

    @Column(name = "MANUAL_BOL_INBOUND_OUTBOUND")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentDirection"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentDirection shipmentDirection;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public PlainModificationObject getModification() {
        return this.modification;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ManualBolStatus getStatus() {
        return status;
    }

    public void setStatus(ManualBolStatus status) {
        this.status = status;
    }

    public CustomerEntity getOrganization() {
        return organization;
    }

    public void setOrganization(CustomerEntity organization) {
        this.organization = organization;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public ManualBolNumbersEntity getNumbers() {
        return numbers;
    }

    public void setNumbers(ManualBolNumbersEntity numbers) {
        this.numbers = numbers;
    }

    public FreightBillPayToEntity getFreightBillPayTo() {
        return freightBillPayTo;
    }

    public void setFreightBillPayTo(FreightBillPayToEntity freightBillPayTo) {
        this.freightBillPayTo = freightBillPayTo;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    public PaymentTerms getPayTerms() {
        return paymentTerms;
    }

    public void setPayTerms(PaymentTerms paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public OrganizationLocationEntity getLocation() {
        return location;
    }

    public void setLocation(OrganizationLocationEntity location) {
        this.location = location;
    }

    public ManualBolAddressEntity getOrigin() {
        return origin;
    }

    public ManualBolAddressEntity getDestination() {
        return destination;
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

    public Date getPickupWindowTo() {
        return pickupWindowTo;
    }

    public void setPickupWindowTo(Date pickupWindowTo) {
        this.pickupWindowTo = pickupWindowTo;
    }

    public Date getPickupWindowFrom() {
        return pickupWindowFrom;
    }

    public void setPickupWindowFrom(Date pickupWindowFrom) {
        this.pickupWindowFrom = pickupWindowFrom;
    }

    public Date getDeliveryWindowFrom() {
        return deliveryWindowFrom;
    }

    public void setDeliveryWindowFrom(Date deliveryWindowFrom) {
        this.deliveryWindowFrom = deliveryWindowFrom;
    }

    public Date getDeliveryWindowTo() {
        return deliveryWindowTo;
    }

    public void setDeliveryWindowTo(Date deliveryWindowTo) {
        this.deliveryWindowTo = deliveryWindowTo;
    }

    public Set<ManualBolMaterialEntity> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<ManualBolMaterialEntity> materials) {
        this.materials = materials;
    }

    public List<LoadDocumentEntity> getDocuments() {
        return documents;
    }

    public void setDocuments(List<LoadDocumentEntity> documents) {
        this.documents = documents;
    }

    public Set<ManualBolAddressEntity> getAddresses() {
        return ImmutableSet.copyOf(addresses);
    }

    public String getCustomsBroker() {
        return customsBroker;
    }

    public void setCustomsBroker(String customsBroker) {
        this.customsBroker = customsBroker;
    }

    public String getCustomsBrokerPhone() {
        return customsBrokerPhone;
    }

    public void setCustomsBrokerPhone(String customsBrokerPhone) {
        this.customsBrokerPhone = customsBrokerPhone;
    }

    /**
     * Add {@link ManualBolAddressEntity} to a load.
     * <p>
     * Load should has two addresses. One {@link ManualBolAddressEntity} should has
     * {@link ManualBolAddressEntity#getPointType()} equal to {@link PointType#ORIGIN} and one to
     * {@link PointType#DESTINATION}.
     * 
     * @param address
     *            - Not <code>null</code> instance of {@link ManualBolAddressEntity}.
     */
    public void addAddress(ManualBolAddressEntity address) {
        if (address != null) {
            if (isOrigin(address)) {
                addresses.remove(origin);
                origin = address;
            } else if (isDestination(address)) {
                addresses.remove(destination);
                destination = address;
            }
            addresses.add(address);
        }
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Add {@link #getOrigin()} and {@link #getDestination()} to {@link #getAddresses()} to save list of
     * addresses.
     * 
     */
    @PreUpdate
    @PrePersist
    protected void initAddresses() {
        if (origin != null) {
            addresses.add(origin);
        }
        if (destination != null) {
            addresses.add(destination);
        }
    }

    /**
     * If all fields in Embedded entity are null, then Hibernate doesn't initialize whole entity. So we do it
     * manually.
     */
    @PostLoad
    protected void initNumbers() {
        if (this.numbers == null) {
            numbers = new ManualBolNumbersEntity();
        }
    }

    private boolean isOrigin(ManualBolAddressEntity address) {
        return address != null && PointType.ORIGIN.equals(address.getPointType());
    }

    private boolean isDestination(ManualBolAddressEntity address) {
        return address != null && PointType.DESTINATION.equals(address.getPointType());
    }

    public Set<CustomerUserEntity> getCustomerLocationUsers() {
        return customerLocationUsers;
    }

    public void setCustomerLocationUsers(Set<CustomerUserEntity> customerLocationUsers) {
        this.customerLocationUsers = customerLocationUsers;
    }

    public LoadAdditionalFieldsEntity getLoadAdditionalFields() {
        return loadAdditionalFields;
    }

    public void setLoadAdditionalFields(LoadAdditionalFieldsEntity loadAdditionalFields) {
        this.loadAdditionalFields = loadAdditionalFields;
    }

    public ManualBolRequestedByNoteEntity getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(ManualBolRequestedByNoteEntity requestedBy) {
        this.requestedBy = requestedBy;
    }

    public ShipmentDirection getShipmentDirection() {
        return shipmentDirection;
    }

    public void setShipmentDirection(ShipmentDirection shipmentDirection) {
        this.shipmentDirection = shipmentDirection;
    }
}
