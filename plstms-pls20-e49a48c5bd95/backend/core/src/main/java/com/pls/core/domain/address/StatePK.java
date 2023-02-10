package com.pls.core.domain.address;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Composite primary key for country state.
 *
 * @author Denis Zhupinsky
 */
@Embeddable
public class StatePK implements Serializable {
    private static final long serialVersionUID = 2693092889501029490L;

    @Column(name = "STATE_CODE", nullable = false, updatable = false)
    private String stateCode;

    @Column(name = "COUNTRY_CODE", nullable = false, updatable = false)
    private String countryCode;

    /**
     * Get state code.
     *
     * @return the state code.
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * Set state code.
     *
     * @param stateCode the state code.
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * Get country code.
     *
     * @return the country code.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Set country code.
     *
     * @param countryCode the country code.
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(stateCode).
                append(countryCode).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StatePK) {
            final StatePK other = (StatePK) obj;
            return new EqualsBuilder().
                    append(stateCode, other.stateCode).
                    append(countryCode, other.countryCode).isEquals();
        } else {
            return false;
        }
    }
}
