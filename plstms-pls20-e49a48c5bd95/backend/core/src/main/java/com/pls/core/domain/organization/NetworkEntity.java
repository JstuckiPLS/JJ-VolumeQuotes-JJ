package com.pls.core.domain.organization;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.user.NetworkUserEntity;

/**
 * Base class for organizations networks.
 * 
 * @author Brichak Aleksandr
 */

@Entity
@Table(name = "NETWORKS")
public class NetworkEntity implements Identifiable<Long> {

    private static final long serialVersionUID = -1735679856409915891L;

    public static final String Q_GET_ALL_NETWORK = "com.pls.core.domain.organization.NetworkEntity.Q_GET_ALL_NETWORK";
    public static final String Q_GET_ACTIVE_NETWORKS_BY_USER = "com.pls.core.domain.organization.NetworkEntity.Q_GET_ACTIVE_NETWORKS_BY_USER";
    public static final String Q_GET_UNIT_AND_COST_CENTER_CODES = "com.pls.core.domain.organization.NetworkEntity.Q_GET_UNIT_AND_COST_CENTER_CODES";

    @Id
    @Column(name = "NETWORK_ID", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "NAME", nullable = false, insertable = false, updatable = false)
    private String name;

    @Column(name = "VISIBLE", nullable = false, insertable = false, updatable = false)
    @Type(type = "yes_no")
    private Boolean visible;

    @Column(name = "DEFAULT_AP_TERMS", insertable = false, updatable = false)
    private String defaultApTerms;

    @Column(name = "MAX_AP_DUEDAYS", insertable = false, updatable = false)
    private Integer maxApDuedays;

    @Column(name = "SHARE_CARRIER_NETWORK_ID", insertable = false, updatable = false)
    private Integer shareCarrierNetworkId;

    @Column(name = "CREDIT_LIMIT_REQUIRED")
    @Type(type = "yes_no")
    private Boolean requiredCreditLimit;

    @Column(name = "AUTO_CREDIT_HOLD", insertable = false, updatable = false)
    @Type(type = "yes_no")
    private Boolean autoCreditHold;

    /**
     * List of users that bound with network through NETWORK_USERS table.
     */
    @OneToMany(mappedBy = "network", fetch = FetchType.LAZY)
    private List<NetworkUserEntity> networkUsers;

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public List<NetworkUserEntity> getNetworkUsers() {
        return networkUsers;
    }

    public Boolean getVisible() {
        return visible;
    }

    public String getDefaultApTerms() {
        return defaultApTerms;
    }

    public Integer getMaxApDuedays() {
        return maxApDuedays;
    }

    public Integer getShareCarrierNetworkId() {
        return shareCarrierNetworkId;
    }

    public Boolean getRequiredCreditLimit() {
        return requiredCreditLimit;
    }

    public Boolean getAutoCreditHold() {
        return autoCreditHold;
    }

    public void setAutoCreditHold(Boolean autoCreditHold) {
        this.autoCreditHold = autoCreditHold;
    }

}
