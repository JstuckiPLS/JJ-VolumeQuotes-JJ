package com.pls.shipment.domain;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.shipment.domain.bo.ShipmentAlertType;
import com.pls.shipment.domain.enums.ShipmentAlertsStatus;

/**
 * Shipment alerts entity.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Entity
@Table(name = "SHIPMENT_ALERTS")
public class ShipmentAlertEntity implements Identifiable<Long>, HasModificationInfo {
    public static final String Q_BY_SHIPMENT_IDS = "com.pls.shipment.domain.ShipmentAlertEntity.Q_BY_SHIPMENT_IDS";
    public static final String Q_BY_STATUSES = "com.pls.shipment.domain.ShipmentAlertEntity.Q_BY_STATUSES";
    public static final String Q_FOR_USER_BY_STATUSES = "com.pls.shipment.domain.ShipmentAlertEntity.Q_FOR_USER_BY_STATUSES";
    public static final String Q_COUNT_FOR_USER_BY_STATUSES = "com.pls.shipment.domain.ShipmentAlertEntity.Q_COUNT_FOR_USER_BY_STATUSES";
    public static final String C_GENERATE_TIME_ALERTS = "com.pls.shipment.domain.ShipmentAlertEntity.C_GENERATE_TIME_ALERTS";
    public static final String D_REMOVE_TIME_ALERTS = "com.pls.shipment.domain.ShipmentAlertEntity.D_REMOVE_TIME_ALERTS";
    public static final String Q_BY_SHIPMENT_AND_TYPE = "com.pls.shipment.domain.ShipmentAlertEntity.Q_BY_SHIPMENT_AND_TYPE";
    public static final String Q_DELETE = "com.pls.shipment.domain.ShipmentAlertEntity.Q_DELETE";

    private static final long serialVersionUID = -278953903037683669L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipment_alerts_sequence")
    @SequenceGenerator(name = "shipment_alerts_sequence", sequenceName = "SHIP_ALERTS_SEQ", allocationSize = 1)
    @Column(name = "ALERT_ID")
    private Long id;

    @Column(name = "LOAD_ID", nullable = false)
    private Long loadId;

    //use load id instead
    @Immutable
    @ManyToOne
    @JoinColumn(name = "LOAD_ID", nullable = false, insertable = false, updatable = false)
    private LoadEntity load;

    @Immutable
    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "LOAD_ID", referencedColumnName = "LOAD_ID", unique = true, nullable = true,
                    insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "LOAD_ACTION", value = "'P'")),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "POINT_TYPE", value = "'O'")) })
    private LoadDetailsEntity origin;

    @Immutable
    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "LOAD_ID", referencedColumnName = "LOAD_ID", unique = true, nullable = true,
                    insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "LOAD_ACTION", value = "'D'")),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "POINT_TYPE", value = "'D'")) })
    private LoadDetailsEntity destination;

    @Column(name = "ORG_ID", nullable = false)
    private Long customerId;

    @Immutable
    @ManyToOne
    @JoinColumn(name = "ORG_ID", nullable = false, insertable = false, updatable = false)
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "ACKNOW_USER_ID")
    private UserEntity acknowledgedUser;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.shipment.domain.enums.ShipmentAlertsStatus"),
            @Parameter(name = "identifierMethod", value = "getStatus"), @Parameter(name = "valueOfMethod", value = "getShipmentAlertsStatus")})
    private ShipmentAlertsStatus status = ShipmentAlertsStatus.ACTIVE;

    @Column(name = "TYPE", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.shipment.domain.bo.ShipmentAlertType"),
            @Parameter(name = "identifierMethod", value = "getType"), @Parameter(name = "valueOfMethod", value = "getShipmentAlertType")})
    private ShipmentAlertType type;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "createdBy", column = @Column(name = "CREATED_BY", updatable = false)),
            @AttributeOverride(name = "modifiedBy", column = @Column(name = "MODIFIED_BY"))})
    @AssociationOverrides({
            @AssociationOverride(name = "createdUser", joinColumns = @JoinColumn(name = "CREATED_BY", insertable = false, updatable = false)),
            @AssociationOverride(name = "modifiedUser", joinColumns = @JoinColumn(name = "MODIFIED_BY", insertable = false, updatable = false))})
    private PlainModificationObject modification = new PlainModificationObject();

    @Version
    private int version = 1;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public LoadDetailsEntity getOrigin() {
        return origin;
    }

    public void setOrigin(LoadDetailsEntity origin) {
        this.origin = origin;
    }

    public LoadDetailsEntity getDestination() {
        return destination;
    }

    public void setDestination(LoadDetailsEntity destination) {
        this.destination = destination;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public ShipmentAlertsStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentAlertsStatus status) {
        this.status = status;
    }

    public ShipmentAlertType getType() {
        return type;
    }

    public void setType(ShipmentAlertType type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public UserEntity getAcknowledgedUser() {
        return acknowledgedUser;
    }

    public void setAcknowledgedUser(UserEntity acknowledgedUser) {
        this.acknowledgedUser = acknowledgedUser;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }
}
