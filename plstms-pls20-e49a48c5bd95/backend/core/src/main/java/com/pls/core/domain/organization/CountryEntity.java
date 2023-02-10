package com.pls.core.domain.organization;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.shared.Status;

/**
 * Country.
 *
 * @author Denis Zhupinsky
 */
@Entity
@Table(name = "COUNTRIES")
public class CountryEntity implements Identifiable<String> {
    public static final String Q_GET_ALL_BY_STATUS = "com.pls.core.domain.organization.CountryEntity.Q_GET_ALL_BY_STATUS";
    public static final String Q_GET_ID_BY_SHORT_CODE = "com.pls.core.domain.organization.CountryEntity.Q_GET_ID_BY_SHORT_CODE";
    public static final String Q_GET_SHORT_CODE_BY_ID = "com.pls.core.domain.organization.CountryEntity.Q_GET_SHORT_CODE_BY_ID";

    private static final long serialVersionUID = 3378210663619464642L;

    @Id
    @Column(name = "COUNTRY_CODE")
    private String id;

    @Column(nullable = false, updatable = false)
    private String name;

    @Column(name = "DIALING_CODE", nullable = false, updatable = false)
    private String phoneCode;

    @Column(name = "STATUS", nullable = false, updatable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "COUNTRY_CD_SHORT", updatable = false)
    private String shortCountryCode;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get country name.
     *
     * @return country name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set country name.
     *
     * @param name the country name.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getShortCountryCode() {
        return shortCountryCode;
    }

    public void setShortCountryCode(String shortCountryCode) {
        this.shortCountryCode = shortCountryCode;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(name).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CountryEntity) {
            final CountryEntity other = (CountryEntity) obj;
            return new EqualsBuilder().
                    append(name, other.name).isEquals();
        } else {
            return false;
        }
    }
}
