package com.pls.shipment.domain;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.shipment.domain.customs.CustomsEntity;
import com.pls.shipment.domain.customs.CustomsLoadDetailsEntity;

/**
 * Load entity.
 * <p>
 * Load can contain multiple cost details, but only one of them is active, all others are history. If you need
 * to get estimated cost - sort them by date and get first one.
 * </p>
 * <p/>
 * <p>
 * ORIGINAL_SCHED_PICKUP_DATE - PNLT (Pickup not later than). <b>We don't use it in PLS 2.0!</b> <br/>
 * ORIGINAL_SCHED_DELIVERY_DATE - DNLT (Delivery not later than). <b>We don't use it in PLS 2.0!</b> <br/>
 * AWARD_CARRIER_ORG_ID - Carrier moving the load. <br/>
 * SHIP_DATE - estimated pickup date or actual pickup date (if available). Populated by LOAD_DETAILS_TRG.
 * </p>
 *
 * @author Denis Zhupinsky (TEAM International)
 * @author Vicheslav Krot
 */
@Entity
@Table(name = "LOADS")
@Where(clause = "CONTAINER_CD='VANLTL' and ORIGINATING_SYSTEM in ('PLS2_LT', 'GS')")
public class LoadEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    public static final String Q_LOAD_BY_ID = "com.pls.shipment.domain.LoadEntity.Q_LOAD_BY_ID";
    public static final String Q_UNBILLED_LOADS_BY_AE = "com.pls.shipment.domain.LoadEntity.Q_UNBILLED_LOADS_BY_AE";
    public static final String Q_LAST_N_LOADS_BY_USER = "com.pls.shipment.domain.LoadEntity.Q_LAST_N_LOADS_BY_USER";
    public static final String Q_GET_LOAD_STATUS = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_STATUS";
    public static final String Q_GET_CBI_DATA_LOADS = "com.pls.shipment.domain.LoadEntity.Q_GET_CBI_DATA_LOADS";
    public static final String Q_UPDATE_LOADS_INVOICE_APPROVED = "com.pls.shipment.domain.LoadEntity.Q_UPDATE_LOADS_INVOICE_APPROVED";
    public static final String Q_UPDATE_LOADS_FINALIZATION_STATUS = "com.pls.shipment.domain.LoadEntity.Q_UPDATE_LOADS_FINALIZATION_STATUS";
    public static final String Q_GET_CONSOLIDATED_INVOICES = "com.pls.shipment.domain.LoadEntity.Q_GET_CONSOLIDATED_INVOICES";
    public static final String Q_GET_TRANSACTIONAL_INVOICES = "com.pls.shipment.domain.LoadEntity.Q_GET_TRANSACTIONAL_INVOICES";
    public static final String Q_UPDATE_LOAD_WITH_VENDOR_BILL_INFO = "com.pls.shipment.domain.LoadEntity.Q_UPDATE_LOAD_WITH_VENDOR_BILL_INFO";
    public static final String Q_GET_LOAD_BY_SCAC_AND_BOL = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_BY_SCAC_AND_BOL";
    public static final String Q_GET_LOAD_BY_REF_AND_ORG_ID = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_BY_REF_AND_ORG_ID";
    public static final String Q_GET_LOAD_BY_SCAC_AND_PRO = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_BY_SCAC_AND_PRO";
    public static final String Q_GET_LOAD_BY_SCAC_AND_BOL_AND_ZIP = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_BY_SCAC_AND_BOL_AND_ZIP";
    public static final String Q_GET_LOAD_BY_SCAC_AND_BOL_AND_ADDRESSES = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_BY_SCAC_AND_BOL_AND_ADDRESSES";
    public static final String Q_GET_MATCHED_LOAD_INFO = "com.pls.shipment.domain.LoadEntity.Q_GET_MATCHED_LOAD_INFO";
    public static final String Q_TRACKING_BOARD_BOS_BY_CRITERIA = "com.pls.shipment.domain.LoadEntity.Q_TRACKING_BOARD_BOS_BY_CRITERIA";
    public static final String Q_FIND_SHIPMENT_INFO = "com.pls.shipment.domain.LoadEntity.Q_FIND_SHIPMENT_INFO";
    public static final String Q_FIND_SHIPMENT_INFO_BY_DATE_RANGE = "com.pls.shipment.domain.LoadEntity.Q_FIND_SHIPMENT_INFO_BY_DATE_RANGE";
    public static final String Q_GET_LOAD_CARRIER = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_CARRIER";
    public static final String Q_UPDATE_STATUS = "com.pls.shipment.domain.LoadEntity.Q_UPDATE_STATUS";
    public static final String Q_GET_LOAD_PRO_NUMBER = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_PRO_NUMBER";
    public static final String Q_GET_PRIMARY_LOAD_COST_DETAIL = "com.pls.shipment.domain.LoadEntity.Q_GET_PRIMARY_LOAD_COST_DETAIL";
    public static final String Q_GET_PRICE_AUDIT_SHIPMENT = "com.pls.shipment.domain.LoadEntity.Q_GET_PRICE_AUDIT_SHIPMENT";
    public static final String Q_GET_LOADS_FOR_MATCHED_VENDOR_BILL = "com.pls.shipment.domain.LoadEntity.Q_GET_LOADS_FOR_MATCHED_VENDOR_BILL";
    public static final String Q_GET_LOAD_BY_ORG_ID_AND_SHIPMENT_NO = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_BY_ORG_ID_AND_SHIPMENT_NO";
    public static final String Q_GET_LOAD_BY_REF_NO_AND_BOL = "com.pls.shipment.domain.LoadEntity.Q_GET_LOAD_BY_ORG_ID_AND_SHIPMENT_NO_AND_BOL";

    public static final String GET_LOADS_FOR_ACTIVITY_REPORT = "com.pls.shipment.domain.LoadEntity.GET_LOADS_FOR_ACTIVITY_REPORT";
    public static final String GET_LOADS_FOR_CARRIER_ACTIVITY_REPORT = "com.pls.shipment.domain.LoadEntity.GET_LOADS_FOR_CARRIER_ACTIVITY_REPORT";
    public static final String GET_PRODUCTS_FOR_ACTIVITY_REPORT = "com.pls.shipment.domain.LoadEntity.GET_PRODUCTS_FOR_ACTIVITY_REPORT";
    public static final String GET_ACCESSORIAL_FOR_ACTIVITY_REPORT = "com.pls.shipment.domain.LoadEntity.GET_ACCESSORIAL_FOR_ACTIVITY_REPORT";
    public static final String GET_REASONS_FOR_CARRIER_ACTIVITY_REPORT = "com.pls.shipment.domain.LoadEntity.GET_REASONS_FOR_CARRIER_ACTIVITY_REPORT";

    public static final String GET_LOADS_FOR_SAVINGS_REPORT = "com.pls.shipment.domain.LoadEntity.GET_LOADS_FOR_SAVINGS_REPORT";
    public static final String GET_PRODUCTS_FOR_SAVINGS_REPORT = "com.pls.shipment.domain.LoadEntity.GET_PRODUCTS_FOR_SAVINGS_REPORT";
    public static final String GET_ACCESSORIAL_FOR_SAVINGS_REPORT = "com.pls.shipment.domain.LoadEntity.GET_ACCESSORIAL_FOR_SAVINGS_REPORT";
    public static final String GET_SHIPMENT_BILL_TO = "com.pls.shipment.domain.LoadEntity.GET_SHIPMENT_BILL_TO";
    public static final String Q_ACTIVITY_BOOKED = "com.pls.shipment.domain.LoadEntity.Q_ACTIVITY_BOOKED";
    public static final String Q_ACTIVITY = "com.pls.shipment.domain.LoadEntity.Q_ACTIVITY";
    public static final String Q_GET_MATCHED_LOADS_BY_PRO_AND_ORG_ID = "com.pls.shipment.domain.LoadEntity.Q_GET_MATCHED_LOADS_BY_PRO_AND_ORG_ID";
    public static final String GET_LOAD_INVOICE_TYPE = "com.pls.shipment.domain.LoadEntity.GET_LOAD_INVOICE_TYPE";
    public static final String GET_CREATION_REPORT_DATA = "com.pls.shipment.domain.LoadEntity.GET_CREATION_REPORT_DATA";
    public static final String IS_ANY_INVOICED_LOADS = "com.pls.shipment.domain.LoadEntity.IS_ANY_INVOICED_LOADS";
    public static final String Q_TRACKING_BOARD_BOOKED_AND_PEN_PAY = "com.pls.shipment.domain.LoadEntity.Q_TRACKING_BOARD_BOOKED_AND_PEN_PAY";
    public static final String GET_PAPERWORK_EMAILS = "com.pls.shipment.domain.LoadEntity.GET_PAPERWORK_EMAILS";
    public static final String GET_ORDERS_HISTORY = "com.pls.shipment.domain.LoadEntity.GET_ORDERS_HISTORY";
    public static final String Q_FIND_HOLD_SHIPMENT = "com.pls.shipment.domain.LoadEntity.Q_FIND_HOLD_SHIPMENT";
    public static final String Q_GET_PICKUPS_AND_DELIVERIES = "com.pls.shipment.domain.LoadEntity.Q_GET_PICKUPS_AND_DELIVERIES";
    public static final String Q_GET_LOADS_PICKUPS_AND_DELIVERIES = "com.pls.shipment.domain.LoadEntity.Q_GET_LOADS_PICKUPS_AND_DELIVERIES";
    public static final String Q_UNBILLED_REPORT = "com.pls.shipment.domain.LoadEntity.Q_UNBILLED_REPORT";
    public static final String Q_UPDATE_INVOICED_LOADS = "com.pls.shipment.domain.LoadEntity.Q_UPDATE_INVOICED_LOADS";
    public static final String GET_LOAD_TRACKING = "com.pls.shipment.domain.LoadEntity.GET_LOAD_TRACKING";
    public static final String GET_LOAD_TRACKING_UPDATES = "com.pls.shipment.domain.LoadEntity.GET_LOAD_TRACKING_UPDATES";
    
    private static final String LOAD_COLUMN = "load";

    private static final long serialVersionUID = -4172687511294994215L;

    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY)
    private Set<FinancialAccountReceivablesEntity> accountReceivables;

    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY)
    @Where(clause = "status = 'A' and FAA_DETAIL_ID is null")
    private Set<LdBillingAuditReasonsEntity> billingAuditReasons;

    // Currently active cost details. Use this field if you need to work with actual cost details.
    // Should be only one active cost details, we use Set because of Hibernate restrictions.
    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Where(clause = "status = 'A'")
    private Set<LoadCostDetailsEntity> activeCostDetails;

    @Column(name = "BILL_TO", nullable = false)
    private Long billToId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO", insertable = false, updatable = false)
    private BillToEntity billTo;

    @Column(name = "BOL", insertable=false, updatable = false)
    private String BOL;
    
    @Column(name = "PRO_NUM")
    private String proNum;
    
    @Column(name = "BOL_INSTRUCTIONS")
    private String bolInstructions;

    @Column(name = "AWARD_CARRIER_ORG_ID")
    private Long carrierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AWARD_CARRIER_ORG_ID", insertable = false, updatable = false)
    private CarrierEntity carrier;

    @Column(name = "AWARDED_BY")
    private Long awardedBy;

    @Column(name = "AWARD_DATE", updatable = false)
    private Date awardDate = new Date();

    @Column(name = "GL_DATE", insertable = false, updatable = false)
    private Date glDate;

    @Column(name = "COMMODITY_CD")
    private String commodity;

    @Column(name = "CONTAINER_CD")
    private String container = "VANLTL";

    @Column(name = "ORIGINATING_SYSTEM")
    private String originatingSystem = "PLS2_LT";

    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<LoadCostDetailsEntity> costDetails;

    @Column(name = "CUSTOMS_BROKER")
    private String customsBroker;

    @Column(name = "CUSTOMS_BROKER_PHONE")
    private String customsBrokerPhone;

    /**
     * Indicates that all documents required for customer invoice have been uploaded.
     */
    @Column(name = "CUST_REQ_DOC_RECV_FLAG")
    @Type(type = "yes_no")
    private Boolean custReqDocPresent = false;

    @Column(name = "SPECIAL_MESSAGE")
    private String deliveryNotes;

    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "LOAD_ID", referencedColumnName = "LOAD_ID",
                    unique = true, nullable = true, insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "LOAD_ACTION", value = "'D'")),
            @JoinColumnOrFormula(
                    formula = @JoinFormula(referencedColumnName = "POINT_TYPE", value = "'D'")) })
    private LoadDetailsEntity destination;

    @Column(name = "FINALIZATION_STATUS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType",
            parameters = {
                    @Parameter(name = "enumClass",
                            value = "com.pls.core.domain.enums.ShipmentFinancialStatus"),
                    @Parameter(name = "identifierMethod", value = "getStatusCode"),
                    @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentFinancialStatus finalizationStatus = ShipmentFinancialStatus.NONE;

    @Column(name = "INV_APPROVED")
    @Type(type = "yes_no")
    private Boolean invoiceApproved = true;

    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY)
    @Where(clause = "status = 'A' and GL_DATE is null")
    private Set<FinancialAccessorialsEntity> financialAccessorials;

    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY)
    @Where(clause = "status = 'A'")
    @OrderBy("id")
    private Set<FinancialAccessorialsEntity> allFinancialAccessorials;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loads_sequence")
    @SequenceGenerator(name = "loads_sequence", sequenceName = "LOADS_SEQ", allocationSize = 1)
    @Column(name = "LOAD_ID")
    private Long id;

    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<LoadDetailsEntity> loadDetails = new HashSet<LoadDetailsEntity>();

    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY, orphanRemoval = true,
            cascade = CascadeType.ALL)
    private Set<LoadNotificationsEntity> loadNotifications;

    @Column(name = "LOCATION_ID", nullable = false)
    private Long locationId;

    @OneToMany
    @JoinColumns({ @JoinColumn(name = "ORG_ID", referencedColumnName = "ORG_ID"),
            @JoinColumn(name = "LOCATION_ID", referencedColumnName = "LOCATION_ID") })
    private Set<CustomerUserEntity> customerLocationUsers;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID", insertable = false, updatable = false)
    private OrganizationLocationEntity location;

    @OneToMany(mappedBy = "load", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LtlLoadAccessorialEntity> ltlAccessorials;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LOAD_ID")
    private Set<CustomsEntity> customsEntities;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LOAD_ID")
    private CustomsLoadDetailsEntity customsLoadDetails;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID", nullable = false)
    private CustomerEntity organization;

    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "LOAD_ID", referencedColumnName = "LOAD_ID",
                    unique = true, nullable = true, insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "LOAD_ACTION", value = "'P'")),
            @JoinColumnOrFormula(
                    formula = @JoinFormula(referencedColumnName = "POINT_TYPE", value = "'O'")) })
    private LoadDetailsEntity origin;

    @Column(name = "PERSON_ID")
    private Long personId;

    @Column(name = "MILEAGE")
    private Integer mileage;

    @Column(name = "PIECES")
    private Integer pieces;

    /**
     * We need to save route only if there is no other route in DB with same Origin/Destination fields.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ROUTE_ID", nullable = false)
    private RouteEntity route;

    @OneToMany(mappedBy = "load", fetch = FetchType.LAZY)
    private Set<ShipmentNoteEntity> shipmentNotes;

    @Column(name = "SOURCE_IND")
    private String sourceInd;

    @Column(name = "SPECIAL_INSTRUCTIONS")
    private String specialInstructions;

    @OneToOne(mappedBy = "load", cascade = CascadeType.ALL)
    private LoadNoteEntity specialMessage;

    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY)
    @Where(clause = "STATUS='A'")
    private Set<LoadDispatchInformationEntity> loadDispatchInformation = new HashSet<LoadDispatchInformationEntity>();

    @OneToOne(mappedBy = "load", cascade = CascadeType.ALL)
    private ShipmentRequestedByNoteEntity requestedBy;

    @Column(name = "LOAD_STATUS")
    @Type(type = "com.pls.core.domain.usertype.LoadStatusUserType")
    private ShipmentStatus status;

    /**
     * The duration of the trip in minutes.
     */
    @Column(name = "TRAVEL_TIME")
    private Long travelTime;

    @Column(name = "WEIGHT")
    private Integer weight;

    @Column(name = "WEIGHT_UOM_CODE")
    private String weightUOM = "LBS";

    @Embedded
    private LoadVendorBillEntity vendorBillDetails = new LoadVendorBillEntity();

    @Embedded
    private LoadNumbersEntity numbers = new LoadNumbersEntity();

    @Column(name = "PAY_TERMS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType",
            parameters = { @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.PaymentTerms"),
                    @Parameter(name = "identifierMethod", value = "getPaymentTermsCode"),
                    @Parameter(name = "valueOfMethod", value = "getByCode") })
    private PaymentTerms paymentTerms = PaymentTerms.PREPAID;

    @Column(name = "INBOUND_OUTBOUND_FLG")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType",
            parameters = {
                    @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentDirection"),
                    @Parameter(name = "identifierMethod", value = "getCode"),
                    @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentDirection shipmentDirection = ShipmentDirection.OUTBOUND;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FRT_BILL_PAY_TO_ID")
    private FreightBillPayToEntity freightBillPayTo;

    @Column(name = "HAZMAT_FLAG")
    @Type(type = "yes_no")
    private Boolean hazmat;

    @Column(name = "VOLUME_QUOTE_ID", nullable = true)
    private String volumeQuoteId;

    @Column(name = "SAVED_QUOTE_ID", nullable = true, updatable = false)
    private Long savedQuoteId;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SAVED_QUOTE_ID", insertable = false, updatable = false)
    private SavedQuoteEntity savedQuote;

    @OneToMany(mappedBy = LOAD_COLUMN, fetch = FetchType.LAZY)
    @Where(clause = "status <> 'I'")
    private Set<ShipmentAlertEntity> alerts;

    @OneToOne(mappedBy = "load", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LoadAdditionalInfoEntity loadAdditionalInfo;

    @OneToOne(mappedBy = "load", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LoadAdditionalFieldsEntity loadAdditionalFields;

    @OneToMany(mappedBy = LOAD_COLUMN, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LoadPricingDetailsEntity> loadPricingDetails;

    @OneToMany(mappedBy = LOAD_COLUMN, cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            orphanRemoval = true)
    private Set<PrepaidDetailEntity> prepaidDetails;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", insertable = false, updatable = false)
    private Set<LoadDocumentEntity> documents;
    
    @Column(name = "EXTERNAL_UUID")
    private String externalUuid;

    /**
     * Get active load cost details.
     * <p>
     * There may be multiple cost details, but only one record is active and contains up-to-date information.
     * </p>
     *
     * @return active cost details or <code>null</code>.
     */
    public LoadCostDetailsEntity getActiveCostDetail() {
        if (getActiveCostDetails() != null) {
            for (LoadCostDetailsEntity cost : getActiveCostDetails()) {
                if (cost.getStatus() == Status.ACTIVE) {
                    return cost;
                }
            }
        }
        if (getCostDetails() != null) {
            for (LoadCostDetailsEntity cost : getCostDetails()) {
                if (cost.getStatus() == Status.ACTIVE) {
                    return cost;
                }
            }
        }
        return null;
    }

    public Set<LoadCostDetailsEntity> getActiveCostDetails() {
        return activeCostDetails;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public String getBolInstructions() {
        return bolInstructions;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public String getCommodity() {
        return commodity;
    }

    public String getContainer() {
        return container;
    }
    
    

    public String getBOL() {
		return BOL;
	}

	public void setBOL(String bOL) {
		BOL = bOL;
	}

	
	public String getProNum() {
		return proNum;
	}

	public void setProNum(String proNum) {
		this.proNum = proNum;
	}

	/**
     * Get cost details. Although it is a Set, only one of them is active. All others are history.
     *
     * @return cost details.
     */
    public Set<LoadCostDetailsEntity> getCostDetails() {
        return costDetails;
    }

    public String getCustomsBroker() {
        return customsBroker;
    }

    public String getCustomsBrokerPhone() {
        return customsBrokerPhone;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public LoadDetailsEntity getDestination() {
        return destination;
    }

    public ShipmentFinancialStatus getFinalizationStatus() {
        return finalizationStatus;
    }

    public Set<FinancialAccessorialsEntity> getFinancialAccessorials() {
        return financialAccessorials;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Set<LoadDetailsEntity> getLoadDetails() {
        return Collections.unmodifiableSet(loadDetails);
    }

    public Set<LoadNotificationsEntity> getLoadNotifications() {
        return loadNotifications;
    }

    public OrganizationLocationEntity getLocation() {
        return location;
    }

    public Set<LtlLoadAccessorialEntity> getLtlAccessorials() {
        return ltlAccessorials;
    }

    public Set<LoadDocumentEntity> getDocuments() {
        return documents;
    }

    public String getExternalUuid() {
        return externalUuid;
    }

    public void setExternalUuid(String externalUuid) {
        this.externalUuid = externalUuid;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public CustomerEntity getOrganization() {
        return organization;
    }

    public LoadDetailsEntity getOrigin() {
        return origin;
    }

    /**
     * Get dispatcher person id. This is the last person who edit the load. But this person is not accountable
     * for the load, it is not Account Executive.
     *
     * @return dispatcher person id.
     */
    public Long getPersonId() {
        return personId;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getPieces() {
        return pieces;
    }

    public RouteEntity getRoute() {
        return route;
    }

    public Set<ShipmentNoteEntity> getShipmentNotes() {
        return shipmentNotes;
    }

    public String getSourceInd() {
        return sourceInd;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public LoadNoteEntity getSpecialMessage() {
        return specialMessage;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    /**
     * Get the duration of the trip in minutes.
     *
     * @return the duration of the trip in minutes.
     */
    public Long getTravelTime() {
        return travelTime;
    }

    public Integer getWeight() {
        return weight;
    }

    public boolean isCustReqDocPresent() {
        return custReqDocPresent != null && custReqDocPresent;
    }

    /**
     * Sets the bill to for the load.
     * 
     * @param billTo
     *            Customer bill to
     */
    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
        if (this.billTo != null) {
            this.billToId = this.billTo.getId();
        } else {
            this.billToId = null;
        }
    }

    public void setBolInstructions(String bolInstructions) {
        this.bolInstructions = bolInstructions;
    }

    /**
     * Set the carrier for the load.
     * 
     * @param carrier
     *            carrier the load is awarded to
     */
    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
        if (this.carrier != null) {
            this.carrierId = this.carrier.getId();
        } else {
            this.carrierId = null;
        }
    }

    public Long getAwardedBy() {
        return awardedBy;
    }

    public void setAwardedBy(Long awardedBy) {
        this.awardedBy = awardedBy;
    }

    public Date getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(Date awardDate) {
        this.awardDate = awardDate;
    }

    public Date getGlDate() {
        return glDate;
    }

    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    /**
     * Set container.
     *
     * @deprecated don't use this method. For LTL loads value should be 'VANLTL' which is default.
     * @param container
     *            to set
     */
    @Deprecated
    public void setContainer(String container) {
        this.container = container;
    }

    /**
     * Set cost details. See {@link LoadEntity#getCostDetails()} comment.
     *
     * @param costDetails
     *            cost details.
     */
    public void setCostDetails(Set<LoadCostDetailsEntity> costDetails) {
        this.costDetails = costDetails;
    }

    public void setCustomsBroker(String customsBroker) {
        this.customsBroker = customsBroker;
    }

    public void setCustomsBrokerPhone(String customsBrokerPhone) {
        this.customsBrokerPhone = customsBrokerPhone;
    }

    public void setCustReqDocPresent(Boolean custReqDocPresent) {
        this.custReqDocPresent = custReqDocPresent;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public void setFinalizationStatus(ShipmentFinancialStatus finalizationStatus) {
        this.finalizationStatus = finalizationStatus;
    }

    public void setFinancialAccessorials(Set<FinancialAccessorialsEntity> financialAccessorials) {
        this.financialAccessorials = financialAccessorials;
    }

    public Set<FinancialAccessorialsEntity> getAllFinancialAccessorials() {
        return allFinancialAccessorials;
    }

    public void setAllFinancialAccessorials(Set<FinancialAccessorialsEntity> allFinancialAccessorials) {
        this.allFinancialAccessorials = allFinancialAccessorials;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLoadNotifications(Set<LoadNotificationsEntity> loadNotifications) {
        this.loadNotifications = loadNotifications;
    }

    public void setLocation(OrganizationLocationEntity location) {
        this.location = location;
    }

    public void setLtlAccessorials(Set<LtlLoadAccessorialEntity> ltlAccessorials) {
        this.ltlAccessorials = ltlAccessorials;
    }

    public void setDocuments(Set<LoadDocumentEntity> documents) {
        this.documents = documents;
    }

    public void setOrganization(CustomerEntity organization) {
        this.organization = organization;
    }

    /**
     * Set dispatcher person id.
     *
     * @param personId
     *            dispatcher person id.
     */
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public void setRoute(RouteEntity route) {
        this.route = route;
    }

    public void setShipmentNotes(Set<ShipmentNoteEntity> shipmentNotes) {
        this.shipmentNotes = shipmentNotes;
    }

    public void setSourceInd(String sourceInd) {
        this.sourceInd = sourceInd;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public void setSpecialMessage(LoadNoteEntity specialMessage) {
        this.specialMessage = specialMessage;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    /**
     * Set the duration of the trip in minutes.
     *
     * @param travelTime
     *            the duration of the trip in minutes.
     */
    public void setTravelTime(Long travelTime) {
        this.travelTime = travelTime;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getWeightUOM() {
        return weightUOM;
    }

    public void setWeightUOM(String weightUOM) {
        this.weightUOM = weightUOM;
    }

    public PaymentTerms getPayTerms() {
        return paymentTerms;
    }

    public void setPayTerms(PaymentTerms paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    /**
     * Add {@link #getOrigin()} and {@link #getDestination()} {@link LoadDetailsEntity}s into
     * {@link #getLoadDetails()} list to make ability to save changes (update DB data ) in
     * {@link #getOrigin()} and {@link #getDestination()}.
     */
    @PreUpdate
    @PrePersist
    protected void initLoadDetails() {
        if (origin != null) {
            loadDetails.add(origin);
        }
        if (destination != null) {
            loadDetails.add(destination);
        }
    }

    /**
     * If all fields in Embedded entity are null, then Hibernate doesn't initialize whole entity. So we do it
     * manually.
     */
    @PostLoad
    protected void initNumbers() {
        if (this.numbers == null) {
            numbers = new LoadNumbersEntity();
        }
    }

    public void setActiveCostDetails(Set<LoadCostDetailsEntity> activeCostDetails) {
        this.activeCostDetails = activeCostDetails;
    }

    public ShipmentDirection getShipmentDirection() {
        return shipmentDirection;
    }

    public void setShipmentDirection(ShipmentDirection shipmentDirection) {
        this.shipmentDirection = shipmentDirection;
    }

    public FreightBillPayToEntity getFreightBillPayTo() {
        return freightBillPayTo;
    }

    public void setFreightBillPayTo(FreightBillPayToEntity freightBillPayTo) {
        this.freightBillPayTo = freightBillPayTo;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getInvoiceApproved() {
        return invoiceApproved != null && invoiceApproved;
    }

    public void setInvoiceApproved(Boolean invoiceApproved) {
        this.invoiceApproved = invoiceApproved;
    }

    public Set<FinancialAccountReceivablesEntity> getAccountReceivables() {
        return accountReceivables;
    }

    public void setAccountReceivables(Set<FinancialAccountReceivablesEntity> accountReceivables) {
        this.accountReceivables = accountReceivables;
    }

    public String getOriginatingSystem() {
        return originatingSystem;
    }

    public void setOriginatingSystem(String originatingSystem) {
        this.originatingSystem = originatingSystem;
    }

    public Boolean getHazmat() {
        return hazmat;
    }

    public void setHazmat(Boolean hazmat) {
        this.hazmat = hazmat;
    }

    public String getVolumeQuoteId() {
        return volumeQuoteId;
    }

    public void setVolumeQuoteId(String volumeQuoteId) {
        this.volumeQuoteId = StringUtils.isEmpty(volumeQuoteId) ? null : volumeQuoteId;
    }

    public Long getSavedQuoteId() {
        return savedQuoteId;
    }

    public void setSavedQuoteId(Long savedQuoteId) {
        this.savedQuoteId = savedQuoteId;
    }

    public Set<LdBillingAuditReasonsEntity> getBillingAuditReasons() {
        return billingAuditReasons;
    }

    public void setBillingAuditReasons(Set<LdBillingAuditReasonsEntity> billingAuditReasons) {
        this.billingAuditReasons = billingAuditReasons;
    }

    public SavedQuoteEntity getSavedQuote() {
        return savedQuote;
    }

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public Long getCarriedId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Set<CustomerUserEntity> getCustomerLocationUsers() {
        return customerLocationUsers;
    }

    public void setCustomerLocationUsers(Set<CustomerUserEntity> customerLocationUsers) {
        this.customerLocationUsers = customerLocationUsers;
    }

    /**
     * Add {@link LoadDetailsEntity} for this load. Normally LTL load should have only one
     * {@link LoadDetailsEntity} for origin (see {@link #getOrigin()} ) and only one {@link LoadDetailsEntity}
     * for destination information (see {@link #getDestination()} ). All other combination of
     * {@link LoadDetailsEntity} for LTL loads may lead unpredictable system behavior.
     *
     * @param newLoadDetails
     *            Not <code>null</code> {@link LoadDetailsEntity} for origin or destination.
     */
    public void addLoadDetails(LoadDetailsEntity newLoadDetails) {
        if (newLoadDetails != null) {
            if (isOrigin(newLoadDetails)) {
                loadDetails.remove(origin);
                origin = newLoadDetails;
            } else if (isDestination(newLoadDetails)) {
                loadDetails.remove(destination);
                destination = newLoadDetails;
            }
            loadDetails.add(newLoadDetails);
        }
    }

    private boolean isDestination(LoadDetailsEntity newLoadDetails) {
        return newLoadDetails != null && LoadAction.DELIVERY.equals(newLoadDetails.getLoadAction())
                && PointType.DESTINATION.equals(newLoadDetails.getPointType());
    }

    private boolean isOrigin(LoadDetailsEntity newLoadDetails) {
        return newLoadDetails != null && LoadAction.PICKUP.equals(newLoadDetails.getLoadAction())
                && PointType.ORIGIN.equals(newLoadDetails.getPointType());
    }

    public Set<ShipmentAlertEntity> getAlerts() {
        return alerts;
    }

    public void setAlerts(Set<ShipmentAlertEntity> alerts) {
        this.alerts = alerts;
    }

    public LoadVendorBillEntity getVendorBillDetails() {
        return vendorBillDetails;
    }

    public void setVendorBillDetails(LoadVendorBillEntity vendorBillDetails) {
        this.vendorBillDetails = vendorBillDetails;
    }

    public LoadNumbersEntity getNumbers() {
        return numbers;
    }

    public void setNumbers(LoadNumbersEntity numbers) {
        this.numbers = numbers;
    }

    public LoadAdditionalInfoEntity getLoadAdditionalInfo() {
        return loadAdditionalInfo;
    }

    public void setLoadAdditionalInfo(LoadAdditionalInfoEntity loadAdditionalInfo) {
        this.loadAdditionalInfo = loadAdditionalInfo;
    }

    public LoadAdditionalFieldsEntity getLoadAdditionalFields() {
        return loadAdditionalFields;
    }

    public void setLoadAdditionalFields(LoadAdditionalFieldsEntity loadAdditionalFields) {
        this.loadAdditionalFields = loadAdditionalFields;
    }

    public Set<LoadPricingDetailsEntity> getLoadPricingDetails() {
        return loadPricingDetails;
    }

    public void setLoadPricingDetails(Set<LoadPricingDetailsEntity> loadPricingDetails) {
        this.loadPricingDetails = loadPricingDetails;
    }

    public ShipmentRequestedByNoteEntity getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(ShipmentRequestedByNoteEntity requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Set<PrepaidDetailEntity> getPrepaidDetails() {
        return prepaidDetails;
    }

    public void setPrepaidDetails(Set<PrepaidDetailEntity> prepaidDetails) {
        this.prepaidDetails = prepaidDetails;
    }

    public Set<LoadDispatchInformationEntity> getLoadDispatchInformation() {
        return loadDispatchInformation;
    }

    public void setLoadDispatchInformation(Set<LoadDispatchInformationEntity> loadDispatchInformation) {
        this.loadDispatchInformation = loadDispatchInformation;
    }

	public Set<CustomsEntity> getCustomsEntities() {
		return customsEntities;
	}

	public void setCustomsEntities(Set<CustomsEntity> customsEntities) {
		this.customsEntities = customsEntities;
	}

	public CustomsLoadDetailsEntity getCustomsLoadDetails() {
		return customsLoadDetails;
	}

	public void setCustomsLoadDetails(CustomsLoadDetailsEntity customsLoadDetails) {
		this.customsLoadDetails = customsLoadDetails;
	}
}