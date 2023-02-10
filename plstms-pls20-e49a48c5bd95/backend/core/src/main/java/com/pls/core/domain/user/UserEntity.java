package com.pls.core.domain.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CustomerServiceContactInfoType;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.shared.Status;

/**
 * Application user.
 *
 * @author Denis Zhupinsky
 */
@Entity
@Table(name = "USERS")
public class UserEntity implements Identifiable<Long>, HasModificationInfo {

    public static final String Q_GET_ID_BY_LOGIN = "com.pls.core.domain.user.UserEntity.Q_GET_ID_BY_LOGIN";
    public static final String Q_GET_BY_ID = "com.pls.core.domain.user.UserEntity.Q_GET_BY_ID";
    public static final String Q_COUNT_ADMIN_USERS_BY_CUSTOMER = "com.pls.core.domain.user.UserEntity.Q_COUNT_ADMIN_USERS_BY_CUSTOMER";
    public static final String Q_PARENT = "com.pls.core.domain.user.UserEntity.Q_PARENT";
    public static final String Q_UPDATE_USER_STATUS = "com.pls.core.domain.user.UserEntity.Q_UPDATE_USER_STATUS";
    public static final String Q_FIND_ACCOUNT_EXECUTIVES_USERS_LIST = "com.pls.core.domain.user.UserEntity.Q_FIND_ACCOUNT_EXECUTIVES_USERS_LIST";
    public static final String Q_GET_USERS_NOTIFICATIONS = "com.pls.core.domain.user.UserEntity.Q_GET_USERS_NOTIFICATIONS";
    public static final String Q_SAVE_LAST_LOGIN_DATE_BY_USER_ID = "com.pls.core.domain.user.UserEntity.Q_SAVE_LAST_LOGIN_DATE_BY_USER_ID";
    public static final String Q_GET_USERS_BY_LOGIN = "com.pls.core.domain.user.UserEntity.Q_GET_USERS_BY_LOGIN";
    public static final String Q_LOAD_ORGANIZATIONS = "com.pls.core.domain.user.UserEntity.Q_LOAD_ORGANIZATIONS";
    public static final String Q_LOAD_CAPABILITIES = "com.pls.core.domain.user.UserEntity.Q_LOAD_CAPABILITIES";
    public static final String Q_USER_NETWORK_CUSTOMER = "com.pls.core.domain.user.UserEntity.Q_USER_NETWORK_CUSTOMER";
    public static final String Q_FIND_USER_INFO = "com.pls.core.domain.user.UserEntity.Q_FIND_USER_INFO";
    public static final String Q_FIND_BY_USER_ID = "com.pls.core.domain.user.UserEntity.Q_FIND_BY_USER_ID";
    public static final String Q_GET_EMAIL_BY_FILTER_VALUE = "com.pls.core.domain.user.UserEntity.Q_GET_EMAIL_BY_FILTER_VALUE";
    public static final String Q_SEARCH_USERS = "com.pls.core.domain.user.UserEntity.Q_SEARCH_USERS";
    public static final String Q_GET_USER_INFO_BY_LOGIN = "com.pls.core.domain.user.UserEntity.Q_GET_USER_INFO_BY_LOGIN";

    private static final long serialVersionUID = 2955262237818684542L;

    private static final String USER_COLUMN = "user";

    @Column(name = "EMAIL_ADDRESS", nullable = false)
    private String email;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_sequence")
    @SequenceGenerator(name = "usr_sequence", sequenceName = "USR_SEQ", allocationSize = 1)
    @Column(name = "PERSON_ID")
    private Long id;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "USERID", unique = true, nullable = false)
    private String login;

    // Master user - user with admin permissions. By default first customer user became master.
    @Column(name = "MASTER", nullable = true)
    @Type(type = "yes_no")
    private boolean masterUser;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    /**
     * Where clause is added to select only Active and Inactive Customer Users. This is done to increase the
     * performance, as for some users there is outdated information saved with other statuses.
     */
    @OneToMany(mappedBy = USER_COLUMN, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Where(clause = "status in ('A','I')")
    private final List<CustomerUserEntity> customerUsers = new ArrayList<CustomerUserEntity>();

    @OneToMany(mappedBy = USER_COLUMN, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<NetworkUserEntity> networkUsers = new ArrayList<NetworkUserEntity>();

    @Column(name = "ORG_ID", nullable = true)
    private Long parentOrgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID", insertable = false, updatable = false)
    private OrganizationEntity parentOrganization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_PERSON_ID")
    private UserEntity parentUser;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.UserStatus"),
            @Parameter(name = "identifierMethod", value = "getUserStatus"),
            @Parameter(name = "valueOfMethod", value = "getUserStatusBy") })
    private UserStatus userStatus;

    @Column(name = "LAST_LOGIN_DATE")
    private Date lastLoginDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<UserAddressEntity> addresses = new ArrayList<UserAddressEntity>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<UserPhoneEntity> phones = new ArrayList<UserPhoneEntity>();

    @Column(name = "CUST_SERV_INFO_TYPE")
    @Enumerated(EnumType.STRING)
    private CustomerServiceContactInfoType customerServiceContactInfoType;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private UserAdditionalContactInfoEntity additionalInfo;

    @Column(name = "AUTH_TOKEN", nullable = true, insertable = true, updatable = false)
    private String authToken;

    @OneToMany(mappedBy = "accountExecutive", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Where(clause = "status = 'A'")
    private Set<PromoCodeEntity> promoCodes = new HashSet<PromoCodeEntity>();

    @Override
    public boolean equals(Object obj) {

        boolean result = false;
        if (obj instanceof UserEntity) {
            if (obj == this) {
                result = true;
            } else {
                final UserEntity other = (UserEntity) obj;
                result = new EqualsBuilder().append(getFirstName(), other.getFirstName())
                        .append(getLastName(), other.getLastName()).append(getEmail(), other.getEmail())
                        .append(getLogin(), other.getLogin()).append(getPassword(), other.getPassword())
                        .append(getUserStatus(), other.getUserStatus()).isEquals();
            }
        }
        return result;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public List<CustomerUserEntity> getCustomerUsers() {
        return customerUsers;
    }

    public List<NetworkUserEntity> getNetworkUsers() {
        return networkUsers;
    }


    public Long getParentOrgId() {
        return parentOrgId;
    }

    public OrganizationEntity getParentOrganization() {
        return parentOrganization;
    }

    public UserEntity getParentUser() {
        return parentUser;
    }

    public String getPassword() {
        return password;
    }

    public Long getPersonId() {
        return id;
    }

    public String getUserId() {
        return login;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getFirstName()).append(getLastName()).append(getEmail())
                .append(getLogin()).append(getPassword()).append(getUserStatus()).toHashCode();
    }

    public boolean isMasterUser() {
        return masterUser;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setMasterUser(boolean masterUser) {
        this.masterUser = masterUser;
    }

    public void setParentOrgId(Long pParentOrgId) {
        parentOrgId = pParentOrgId;
    }

    public void setParentOrganization(OrganizationEntity parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public void setParentUser(UserEntity parentUser) {
        this.parentUser = parentUser;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }


    public Date getLastLoginDate() {
        return lastLoginDate;
    }


    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public List<UserAddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserAddressEntity> addresses) {
        this.addresses = addresses;
    }

    /**
     * Returns user's address.
     *
     * @return {@link UserAddressEntity}
     */
    public UserAddressEntity getUserAddress() {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        UserAddressEntity result = null;
        for (UserAddressEntity userAddress : addresses) {
            if (userAddress.getCustomerUserId() == null) {
                result = userAddress;
                break;
            }
        }
        return result;
    }

    public List<UserPhoneEntity> getPhones() {
        return phones;
    }

    public void setPhones(List<UserPhoneEntity> phones) {
        this.phones = phones;
    }

    /**
     * Returns active {@link UserPhoneEntity} with specified {@link UserPhoneEntity#getType()}.
     *
     * @param type
     *            Not <code>null</code> {@link com.pls.core.domain.enums.PhoneType}.
     * @return Not <code>null</code> value if required entity was found. Otherwise returns <code>null</code>.
     */
    public UserPhoneEntity getActiveUserPhoneByType(PhoneType type) {
        UserPhoneEntity result = null;
        for (UserPhoneEntity entity : getPhones()) {
            if (Status.ACTIVE.equals(entity.getStatus()) && type.equals(entity.getType()) && entity.getCustomerUser() == null) {
                result = entity;
                break;
            }
        }
        return result;
    }

    /**
     * Returns active {@link CustomerUserEntity}.
     *
     * @return list of {@link CustomerUserEntity}
     */
    public List<CustomerUserEntity> getActiveCustomerUsers() {
        List<CustomerUserEntity> activeCustomerUsers = new ArrayList<CustomerUserEntity>();
        for (CustomerUserEntity customerUser : customerUsers) {
            if (Status.ACTIVE.equals(customerUser.getStatus())) {
                activeCustomerUsers.add(customerUser);
            }
        }
        return activeCustomerUsers;
    }

    public CustomerServiceContactInfoType getCustomerServiceContactInfoType() {
        return customerServiceContactInfoType;
    }

    public void setCustomerServiceContactInfoType(CustomerServiceContactInfoType customerServiceContactInfoType) {
        this.customerServiceContactInfoType = customerServiceContactInfoType;
    }

    public UserAdditionalContactInfoEntity getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(UserAdditionalContactInfoEntity additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Set<PromoCodeEntity> getPromoCodes() {
        return promoCodes;
    }

    public void setPromoCodes(Set<PromoCodeEntity> promoCodes) {
        this.promoCodes = promoCodes;
    }
}
