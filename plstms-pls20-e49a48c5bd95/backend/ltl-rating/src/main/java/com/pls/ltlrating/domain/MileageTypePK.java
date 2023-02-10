package com.pls.ltlrating.domain;

import com.pls.ltlrating.domain.enums.MileageType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * Composite primary key for mileage type.
 *
 * @author Gleb Zgonikov
 */
@Embeddable
public class MileageTypePK implements Serializable {

    private static final long serialVersionUID = -339780809738840922L;

    @Enumerated(EnumType.STRING)
    @Column(name = "MILEAGE_TYPE", nullable = false)
    private MileageType mileageType;

    @Column(name = "MILEAGE_VERSION", nullable = false)
    private String mileageVersion;

    public MileageType getMileageType() {
        return mileageType;
    }

    public void setMileageType(MileageType mileageType) {
        this.mileageType = mileageType;
    }

    public String getMileageVersion() {
        return mileageVersion;
    }

    public void setMileageVersion(String mileageVersion) {
        this.mileageVersion = mileageVersion;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(mileageType).
                append(mileageVersion).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MileageTypePK) {
            final MileageTypePK other = (MileageTypePK) obj;
            return new EqualsBuilder().
                    append(mileageType, other.mileageType).
                    append(mileageVersion, other.mileageVersion).isEquals();
        } else {
            return false;
        }
    }
}

