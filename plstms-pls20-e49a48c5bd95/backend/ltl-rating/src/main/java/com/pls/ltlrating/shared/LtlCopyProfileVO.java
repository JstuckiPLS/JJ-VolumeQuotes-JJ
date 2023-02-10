package com.pls.ltlrating.shared;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * VO to get the list of LTL Rating object to display the same in the Copy From dorpdown.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlCopyProfileVO implements Serializable {

    private static final long serialVersionUID = 2134523167851132323L;

    private Long id;
    private String rateName;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRateName() {
        return rateName;
    }
    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getId());
        builder.append(getRateName());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LtlCopyProfileVO) {
            if (obj == this) {
                result = true;
            } else {
                LtlCopyProfileVO other = (LtlCopyProfileVO) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getRateName(), other.getRateName());
                builder.append(getId(), other.getId());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", getId())
                .append("rateName", getRateName());

        return builder.toString();
    }

}
