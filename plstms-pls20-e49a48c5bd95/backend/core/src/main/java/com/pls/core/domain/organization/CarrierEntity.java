package com.pls.core.domain.organization;



import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.pls.core.domain.enums.Currency;

/**
 * Carrier Entity.
 *
 * One carrier may have multiple additional info records, but for different locations.
 *
 * @author Gleb Zgonikov
 */
@Entity
@DiscriminatorValue("CARRIER")
public class CarrierEntity extends OrganizationEntity {
    private static final long serialVersionUID = -1685064001224627580L;

    public static final String Q_FIND_CARRIER_INFO_BY_NAME = "com.pls.core.domain.organization.CarrierEntity.Q_FIND_CARRIER_INFO_BY_NAME";

    public static final String Q_FIND_CARRIER_EDI_CAPABLE = "com.pls.core.domain.organization.CarrierEntity.Q_FIND_CARRIER_EDI_CAPABLE";

    public static final String Q_FIND_DEFAULT_CARRIER = "com.pls.core.domain.organization.CarrierEntity.Q_FIND_DEFAULT_CARRIER";

    public static final String Q_REJECT_EDI_FOR_CUSTOMER = "com.pls.core.domain.organization.CarrierEntity.Q_REJECT_EDI_FOR_CUSTOMER";

    public static final String Q_FIND_CARRIER_BY_SCAC = "com.pls.core.domain.organization.CarrierEntity.Q_FIND_CARRIER_BY_SCAC";
    
    public static final String Q_FIND_CARRIER_BY_SCAC_INCLUDING_ACTUAL = "com.pls.core.domain.organization.CarrierEntity.Q_FIND_CARRIER_BY_SCAC_INCLUDING_ACTUAL";
    
    public static final String Q_FIND_CARRIER_BY_SCAC_AND_CURRENCY = "com.pls.core.domain.organization.CarrierEntity.Q_FIND_CARRIER_BY_SCAC_AND_CURRENCY";

    public static final String Q_FIND_CARRIER_BY_ID = "com.pls.core.domain.organization.CarrierEntity.Q_FIND_CARRIER_BY_ID";

    @Column(name = "MC_NUM")
    private String mcNumber;

    @Column(name = "CURRENCY_CODE")
    @Enumerated(EnumType.STRING)
    private Currency currencyCode = Currency.USD;

    @OneToOne(mappedBy = "carrier", cascade = CascadeType.ALL)
    private OrgServiceEntity orgServiceEntity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "carrier", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EdiRejectedCustomerEntity> rejectedCustomers;

    @OneToOne(mappedBy = "carrier", cascade = CascadeType.ALL)
    private PaperworkEmailEntity paperworkEmail;

    public String getMcNumber() {
        return mcNumber;
    }

    public void setMcNumber(String mcNumber) {
        this.mcNumber = mcNumber;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Currency currencyCode) {
        this.currencyCode = currencyCode;
    }

    public OrgServiceEntity getOrgServiceEntity() {
        return orgServiceEntity;
    }

    public void setOrgServiceEntity(OrgServiceEntity orgServiceEntity) {
        this.orgServiceEntity = orgServiceEntity;
    }

    public Set<EdiRejectedCustomerEntity> getRejectedCustomers() {
        return rejectedCustomers;
    }

    public void setRejectedCustomers(Set<EdiRejectedCustomerEntity> rejectedCustomers) {
        this.rejectedCustomers = rejectedCustomers;
    }

    public PaperworkEmailEntity getPaperworkEmail() {
        return paperworkEmail;
    }

    public void setPaperworkEmail(PaperworkEmailEntity paperworkEmail) {
        this.paperworkEmail = paperworkEmail;
    }
}
