package com.pls.ltlrating.domain;

import com.pls.core.domain.Identifiable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.util.Date;

/**
 * LTL Pricing Notes.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(schema = "FLATBED", name = "LTL_PRICING_NOTES")
public class LtlPricingNotesEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 4313078684295095031L;

    public static final String FIND_BY_PROFILE_ID = "com.pls.ltlrating.domain.LtlPricingNotesEntity.FIND_BY_PROFILE_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_PRICING_NOTES_SEQUENCE")
    @SequenceGenerator(name = "LTL_PRICING_NOTES_SEQUENCE", sequenceName = "LTL_PRICING_NOTES_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRICING_NOTE_ID")
    private Long id;

    @Column(name = "LTL_PRICING_PROFILE_ID")
    private Long pricingProfileId;

    @Column(name = "NOTES")
    private String notes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate = new Date();

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy;

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    /**
     * Default constructor.
     */
    public LtlPricingNotesEntity() {
    }

    /**
     * Copying constructor.
     *
     * @param source - entity to be cloned.
     */
    public LtlPricingNotesEntity(LtlPricingNotesEntity source) {
        this.notes = source.getNotes();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPricingProfileId() {
        return pricingProfileId;
    }

    public void setPricingProfileId(Long pricingProfileId) {
        this.pricingProfileId = pricingProfileId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                    .append(getNotes())
                    .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof LtlPricingNotesEntity) {
            if (obj == this) {
                result = true;
            } else {
                LtlPricingNotesEntity rhs = (LtlPricingNotesEntity) obj;

                result = new EqualsBuilder()
                            .append(getNotes(), rhs.getNotes())
                            .isEquals();
            }
        }

        return result;
    }
}
