package com.pls.core.domain.organization;

import java.util.Comparator;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.user.CustomerUserEntity;
import com.pls.core.shared.Status;

/**
 * Organization location entity class.
 * 
 * @author Andrey Kachur
 */
@Entity
@Table(name = "ORGANIZATION_LOCATIONS")
public class OrganizationLocationEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = 7607916162352254805L;

    public static final String Q_GET_FOR_SHIPMENT = "com.pls.core.domain.organization.OrganizationLocationEntity.Q_GET_FOR_SHIPMENT";

    public static final String Q_GET_FOR_CUSTOMERS = "com.pls.core.domain.organization.OrganizationLocationEntity.Q_GET_FOR_CUSTOMERS";

    public static final String Q_GET_LOCATIONS_FOR_AE = "com.pls.core.domain.organization.OrganizationLocationEntity.Q_GET_LOCATIONS_FOR_AE";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_loc_sequence")
    @SequenceGenerator(name = "org_loc_sequence", sequenceName = "LOC_SEQ", allocationSize = 1)
    @Column(name = "LOCATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORG_ID", nullable = false, updatable = false)
    private OrganizationEntity organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO")
    private BillToEntity billTo;

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<AccountExecutiveEntity> accountExecutives = new HashSet<AccountExecutiveEntity>();

    @Column(name = "LOCATION_NAME", nullable = false)
    private String locationName;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "IS_DEFAULT")
    @Type(type = "yes_no")
    private Boolean defaultNode = Boolean.FALSE;

    @OneToMany
    @JoinColumns({ @JoinColumn(name = "LOCATION_ID", referencedColumnName = "LOCATION_ID"),
            @JoinColumn(name = "ORG_ID", referencedColumnName = "ORG_ID") })
    private Set<CustomerUserEntity> customerLocationUsers;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    public Set<AccountExecutiveEntity> getAccountExecutives() {
        return accountExecutives;
    }

    public void setAccountExecutives(Set<AccountExecutiveEntity> accountExecutives) {
        this.accountExecutives = accountExecutives;
    }

    /**
     * Get Account Executive which is active for current organization location.
     * 
     * @return Account Executive which is active for current organization location.
     */
    public AccountExecutiveEntity getActiveAccountExecutive() {
        return accountExecutives.stream().filter(accountExecutive -> accountExecutive.getStatus() == Status.ACTIVE)
                .max(Comparator.comparingLong(AccountExecutiveEntity::getId))
                .orElse(null);
    }

    /**
     * Get Account Executive which was last active for current organization location.
     * 
     * @return Account Executive which was last active for current organization location.
     */
    public AccountExecutiveEntity getLastActiveAccountExecutive() {
        return accountExecutives.stream()
                .max(Comparator.comparingLong(AccountExecutiveEntity::getId))
                .orElse(null);
    }



    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getDefaultNode() {
        return defaultNode != null ? defaultNode : Boolean.FALSE;
    }

    public void setDefaultNode(Boolean defaultNode) {
        this.defaultNode = defaultNode;
    }

    public Set<CustomerUserEntity> getCustomerLocationUsers() {
        return customerLocationUsers;
    }

    public void setCustomerLocationUsers(Set<CustomerUserEntity> customerLocationUsers) {
        this.customerLocationUsers = customerLocationUsers;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }
}
