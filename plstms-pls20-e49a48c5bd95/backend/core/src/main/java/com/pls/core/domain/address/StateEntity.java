package com.pls.core.domain.address;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Country state.
 *
 * @author Denis Zhupinsky
 */
@Entity
@Table(name = "STATES")
public class StateEntity implements Serializable {
    private static final long serialVersionUID = 5790894296654373147L;

    @EmbeddedId
    private StatePK statePK;

    @Column(name = "STATE_NAME", nullable = false, updatable = false)
    private String stateName;

    /**
     * Get state primary key.
     *
     * @return the primary key.
     */
    public StatePK getStatePK() {
        return statePK;
    }

    /**
     * Set primary key.
     *
     * @param statePK the primary key.
     */
    public void setStatePK(StatePK statePK) {
        this.statePK = statePK;
    }

    /**
     * Get state name.
     *
     * @return the state name.
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * Set state name.
     *
     * @param stateName the state name.
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getStatePK()).append(stateName).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StateEntity) {
            final StateEntity other = (StateEntity) obj;
            return new EqualsBuilder().append(getStatePK(), other.getStatePK()).
                    append(stateName, other.stateName).isEquals();
        } else {
            return false;
        }
    }
}
