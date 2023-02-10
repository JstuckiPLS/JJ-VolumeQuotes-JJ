package com.pls.core.domain.user;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.NetworkEntity;
import com.pls.core.shared.Status;

/**
 * Network user.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "NETWORK_USERS")
public class NetworkUserEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = -5369563042788761091L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NETWORK_USER_SEQUENCE")
    @SequenceGenerator(name = "NETWORK_USER_SEQUENCE", sequenceName = "NET_USR_SEQ", allocationSize = 1)
    @Column(name = "NETWORK_USER_ID")
    private Long id;

    @JoinColumn(name = "NETWORK_ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private NetworkEntity network;

    @Column(name = "NETWORK_ID", nullable = false)
    private Long networkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID")
    protected UserEntity user;

    @Column(name = "PERSON_ID", nullable = false, insertable = false, updatable = false)
    private Long userId;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    protected Status status = Status.ACTIVE;

    @Embedded
    protected final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public NetworkEntity getNetwork() {
        return network;
    }

    public void setNetwork(NetworkEntity network) {
        this.network = network;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
