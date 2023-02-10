package com.pls.ltlrating.domain;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Where;

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
import java.util.ArrayList;
import java.util.List;

/**
 * LTL Accessorials Geocode services (LTL_ACC_GEO_SERVICES) entity. This table is used to set up the origin
 * and destinations to which these LTL Accessorials rates are applicable.
 *
 * @author Hima Bindu Challa
 */
@Entity
@Table(name = "LTL_ACC_GEO_SERVICES")
public class LtlAccGeoServicesEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 4225341234383521321L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_ACC_GEO_SERVICE_SEQUENCE")
    @SequenceGenerator(name = "LTL_ACC_GEO_SERVICE_SEQUENCE", sequenceName = "LTL_ACC_GEO_SERV_SEQ", allocationSize = 1)
    @Column(name = "LTL_ACC_GEO_SERVICE_ID")
    private Long id;

    @Column(name = "LTL_ACCESSORIAL_ID", nullable = false, insertable = false, updatable = false)
    private Long ltlAccessorialId;

    @Transient
    private String origin;

    @Transient
    private String destination;

    @Column(name = "ORIGIN")
    private String originAggValue;

    @Column(name = "DESTINATION")
    private String destinationAggValue;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "geoService")
    @Where(clause = "GEO_TYPE = 1")
    private List<LtlAccGeoServiceDetailsEntity> ltlAccOriginGeoServiceDetails = new ArrayList<LtlAccGeoServiceDetailsEntity>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "geoService")
    @Where(clause = "GEO_TYPE = 2")
    private List<LtlAccGeoServiceDetailsEntity> ltlAccDestGeoServiceDetails = new ArrayList<LtlAccGeoServiceDetailsEntity>();

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    /**
     * Default constructor.
     */
    public LtlAccGeoServicesEntity() {

    }

    /**
     * Copying constructor.
     *
     * @param source
     *            - entity to be cloned.
     */
    public LtlAccGeoServicesEntity(LtlAccGeoServicesEntity source) {
        for (LtlAccGeoServiceDetailsEntity geoEntity : source.getLtlAccOriginGeoServiceDetails()) {
            this.ltlAccOriginGeoServiceDetails.add(new LtlAccGeoServiceDetailsEntity(geoEntity, this));
        }

        for (LtlAccGeoServiceDetailsEntity geoEntity : source.getLtlAccDestGeoServiceDetails()) {
            this.ltlAccDestGeoServiceDetails.add(new LtlAccGeoServiceDetailsEntity(geoEntity, this));
        }
    }

    @Override
    public Long getId() {
        return id;
    }


    @Override
    public void setId(Long pId) {
        this.id = pId;
    }

    public Long getLtlAccessorialId() {
        return ltlAccessorialId;
    }

    public void setLtlAccessorialId(Long pLtlAccessorialId) {
        this.ltlAccessorialId = pLtlAccessorialId;
    }

    /**
     * Get the origin for this geo service.
     *
     * @return the origin for the geo service
     */
    public String getOrigin() {
        if (origin == null && ltlAccOriginGeoServiceDetails != null && !ltlAccOriginGeoServiceDetails.isEmpty()) {
            origin = StringUtils.join(ltlAccOriginGeoServiceDetails, ",");
        }
        return origin;
    }

    public void setOrigin(String pOrigin) {
        this.origin = pOrigin;
    }

    public String getOriginAggValue() {
        return originAggValue;
    }

    public void setOriginAggValue(final String originAggValue) {
        this.originAggValue = StringUtils.right(originAggValue, 500);
    }

    /**
     * Get the destination for the geo service.
     *
     * @return the destination for the geo service.
     */
    public String getDestination() {
        if (destination == null && ltlAccDestGeoServiceDetails != null && !ltlAccDestGeoServiceDetails.isEmpty()) {
            destination = StringUtils.join(ltlAccDestGeoServiceDetails, ",");
        }
        return destination;
    }

    public void setDestination(String pDestination) {
        this.destination = pDestination;
    }

    public String getDestinationAggValue() {
        return destinationAggValue;
    }

    public void setDestinationAggValue(final String destinationAggValue) {
        this.destinationAggValue = StringUtils.right(destinationAggValue, 500);
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer pVersion) {
        this.version = pVersion;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject pModification) {
        this.modification = pModification;
    }

    public List<LtlAccGeoServiceDetailsEntity> getLtlAccOriginGeoServiceDetails() {
        return ltlAccOriginGeoServiceDetails;
    }

    /**
     * Set the geo details for origin.
     *
     * @param ltlAccOriginGeoServiceDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlAccOriginGeoServiceDetails(List<LtlAccGeoServiceDetailsEntity> ltlAccOriginGeoServiceDetails) {
        this.ltlAccOriginGeoServiceDetails.clear();
        this.ltlAccOriginGeoServiceDetails.addAll(ltlAccOriginGeoServiceDetails);
    }

    public List<LtlAccGeoServiceDetailsEntity> getLtlAccDestGeoServiceDetails() {
        return ltlAccDestGeoServiceDetails;
    }

    /**
     * Set the geo details for destination.
     *
     * @param ltlAccDestGeoServiceDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlAccDestGeoServiceDetails(List<LtlAccGeoServiceDetailsEntity> ltlAccDestGeoServiceDetails) {
        this.ltlAccDestGeoServiceDetails.clear();
        this.ltlAccDestGeoServiceDetails.addAll(ltlAccDestGeoServiceDetails);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();

        builder.append(getOrigin()).append(getDestination());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LtlAccGeoServicesEntity) {
            if (obj == this) {
                result = true;
            } else {
                LtlAccGeoServicesEntity other = (LtlAccGeoServicesEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getOrigin(), other.getOrigin()).append(getDestination(), other.getDestination());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("ltlAccessorialId", getLtlAccessorialId())
                .append("origin", getOrigin())
                .append("destination", getDestination())
                .append("modification", getModification());

        return builder.toString();
    }
}
