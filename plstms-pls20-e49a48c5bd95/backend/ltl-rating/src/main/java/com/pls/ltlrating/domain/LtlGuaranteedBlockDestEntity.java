package com.pls.ltlrating.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Where;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Ltl Guaranteed Block Destination.
 *
 * @author Artem Arapov
 *
 */

@Entity
@Table(schema = "FLATBED", name = "LTL_GUARAN_BLOCK_DEST")
public class LtlGuaranteedBlockDestEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -6447749372335974460L;

    @Id
    @Column(name = "LTL_GUARAN_BLOCK_DEST_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_GUARAN_BLOCK_DEST_SEQUENCE")
    @SequenceGenerator(name = "LTL_GUARAN_BLOCK_DEST_SEQUENCE", sequenceName = "LTL_GUARAN_BLOCK_DEST_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "LTL_GUARANTEED_PRICE_ID", nullable = false, insertable = false, updatable = false)
    private Long guaranteedPriceId;

    @Transient
    private String destination;

    @Transient
    private String origin;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    @Version
    private Long version = 1L;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "geoService")
    @Where(clause = "GEO_TYPE = 1")
    private List<LtlGuaranBlockDestDetailsEntity> ltlGuaranOriginDetails = new ArrayList<LtlGuaranBlockDestDetailsEntity>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "geoService")
    @Where(clause = "GEO_TYPE = 2")
    private List<LtlGuaranBlockDestDetailsEntity> ltlGuaranDestinationDetails = new ArrayList<LtlGuaranBlockDestDetailsEntity>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuaranteedPriceId() {
        return guaranteedPriceId;
    }

    public void setGuaranteedPriceId(Long guaranteedPriceId) {
        this.guaranteedPriceId = guaranteedPriceId;
    }

    /**
     * Get the destination for the geo service.
     *
     * @return the destination for the geo service.
     */
    public String getDestination() {
        if (destination == null && ltlGuaranDestinationDetails != null && !ltlGuaranDestinationDetails.isEmpty()) {
            destination = StringUtils.join(ltlGuaranDestinationDetails, ",");
        }
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Get the origin for this geo service.
     *
     * @return the origin for the geo service
     */
    public String getOrigin() {
        if (origin == null && ltlGuaranOriginDetails != null && !ltlGuaranOriginDetails.isEmpty()) {
            origin = StringUtils.join(ltlGuaranOriginDetails, ",");
        }
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public List<LtlGuaranBlockDestDetailsEntity> getLtlGuaranOriginDetails() {
        return ltlGuaranOriginDetails;
    }

    /**
     * Set the geo details for origin.
     *
     * @param ltlGuaranOriginDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlGuaranOriginDetails(List<LtlGuaranBlockDestDetailsEntity> ltlGuaranOriginDetails) {
        this.ltlGuaranOriginDetails.clear();
        this.ltlGuaranOriginDetails.addAll(ltlGuaranOriginDetails);
    }

    public List<LtlGuaranBlockDestDetailsEntity> getLtlGuaranDestinationDetails() {
        return ltlGuaranDestinationDetails;
    }

    /**
     * Set the geo details for destination.
     *
     * @param ltlGuaranDestinationDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlGuaranDestinationDetails(List<LtlGuaranBlockDestDetailsEntity> ltlGuaranDestinationDetails) {
        this.ltlGuaranDestinationDetails.clear();
        this.ltlGuaranDestinationDetails.addAll(ltlGuaranDestinationDetails);
    }

    /**
     * Default constructor.
     */
    public LtlGuaranteedBlockDestEntity() {
    }

    /**
     * Copy constructor that returns a copy of given clone.
     *
     * @param clone
     *            entity to copy.
     */
    public LtlGuaranteedBlockDestEntity(LtlGuaranteedBlockDestEntity clone) {
        checkNotNull(clone, "LtlGuaranteedBlockDestEntity object is required");

        for (LtlGuaranBlockDestDetailsEntity geoEntity : clone.getLtlGuaranOriginDetails()) {
            this.ltlGuaranOriginDetails.add(new LtlGuaranBlockDestDetailsEntity(geoEntity, this));
        }

        for (LtlGuaranBlockDestDetailsEntity geoEntity : clone.getLtlGuaranDestinationDetails()) {
            this.ltlGuaranDestinationDetails.add(new LtlGuaranBlockDestDetailsEntity(geoEntity, this));
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof LtlGuaranteedBlockDestEntity) {
            if (obj == this) {
                result = true;
            } else {
                LtlGuaranteedBlockDestEntity rhs = (LtlGuaranteedBlockDestEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(this.getOrigin(), rhs.getOrigin())
                        .append(this.getDestination(), rhs.getDestination());
                result = builder.isEquals();
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.getOrigin()).append(this.getDestination());

        return builder.hashCode();
    }
}
