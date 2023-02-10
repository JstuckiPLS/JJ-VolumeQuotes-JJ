package com.pls.core.domain.sterling;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.sterling.bo.IntegrationMessageBO;

/**
 * An immutable class that holds a String payload and another String that contains some arbitrary type identifier.
 *
 * @author Jasmin Dhamelia
 *
 */
public class SterlingIntegrationMessageBO implements Serializable {

    private static final long serialVersionUID = -2968016227867528801L;
    private final IntegrationMessageBO payload;
    private final EDIMessageType type;

    /**
     * Construct this outbound message with a payload in some type identifier name.
     *
     * @param payload
     *            Carries a payload in the form of a String. Cannot be null or empty.
     * @param type
     *            Designates some arbitrary type. Cannot be null or empty.
     */
    public SterlingIntegrationMessageBO(IntegrationMessageBO payload, EDIMessageType type) {
        super();
        this.payload = payload;
        this.type = type;
    }

    /**
     * Return payload.
     *
     * @return The payload string.
     */
    public IntegrationMessageBO getPayload() {
        return payload;
    }

    /**
     * Return type.
     *
     * @return The FinancialOutboundMessageType.
     */
    public EDIMessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("payload", getPayload()).append("type", getType());

        return builder.toString();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getPayload());
        builder.append(getType());
        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof SterlingIntegrationMessageBO) {
            if (obj == this) {
                result = true;
            } else {
                SterlingIntegrationMessageBO other = (SterlingIntegrationMessageBO) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getPayload(), other.getPayload()).append(getType(), other.getType());

                result = builder.isEquals();
            }
        }
        return result;
    }
}
