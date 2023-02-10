package com.pls.core.domain.address;

import com.pls.core.domain.organization.CountryEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Composite primary key for ZipCode.
 *
 * @author Sergey Kirichenko
 */
@Embeddable
public class ZipCodePK implements Serializable {

    private static final long serialVersionUID = 1240767024734906742L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COUNTRY_CODE", nullable = false)
    private CountryEntity country;

    @Column(name = "DETAIL_ID", nullable = false)
    private Long detailId;

    /**
     * Getter for the field country.
     *
     * @return the value for the field country.
     */
    public CountryEntity getCountry() {
        return country;
    }

    /**
     * Setter for the field country.
     *
     * @param country the value to set for the field.
     */
    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    /**
     * Getter for the field detailId.
     *
     * @return the value for the field detailId.
     */
    public Long getDetailId() {
        return detailId;
    }

    /**
     * Setter for the field detailId.
     *
     * @param detailId the value to set for the field.
     */
    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(country).
                append(detailId).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ZipCodePK) {
            final ZipCodePK other = (ZipCodePK) obj;
            return new EqualsBuilder().
                    append(country, other.country).
                    append(detailId, other.detailId).isEquals();
        } else {
            return false;
        }
    }
}
