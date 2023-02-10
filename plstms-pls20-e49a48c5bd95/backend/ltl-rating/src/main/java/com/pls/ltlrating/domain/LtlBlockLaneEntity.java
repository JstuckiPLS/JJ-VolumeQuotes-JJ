package com.pls.ltlrating.domain;

import java.util.ArrayList;
import java.util.Date;
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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * Entity containing details regarding Blocking lanes for specific/all blanket carrier profiles for a
 * customer.
 *
 * @author Ashwini Neelgund
 *
 */
@Entity
@Table(schema = "FLATBED", name = "LTL_ORG_BLOCK_LANE")
public class LtlBlockLaneEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -3592062276032484692L;

    public static final String FIND_ACTIVE_AND_EFFECTIVE = "com.pls.ltlrating.domain.LtlBlockLaneEntity.FIND_ACTIVE_AND_EFFECTIVE";

    public static final String FIND_BY_STATUS_AND_PROFILE_ID = "com.pls.ltlrating.domain.LtlBlockLaneEntity.FIND_BY_STATUS_AND_PROFILE_ID";

    public static final String FIND_EXPIRED = "com.pls.ltlrating.domain.LtlBlockLaneEntity.FIND_EXPIRED";

    public static final String GET_UNBLOCKED_BLANKET_CARR_PROF_FOR_CUST
            = "com.pls.ltlrating.domain.LtlBlockLaneEntity.GET_UNBLOCKED_BLANKET_CARR_PROF_FOR_CUST";

    public static final String EXPIRE_BY_IDS = "com.pls.ltlrating.domain.LtlBlockLaneEntity.EXPIRE_BY_IDS";

    public static final String UPDATE_STATUS = "com.pls.ltlrating.domain.LtlBlockLaneEntity.UPDATE_STATUS";

    public static final String GET_BLOCK_LANE_BY_ID = "com.pls.ltlrating.domain.LtlBlockLaneEntity.GET_BLOCK_LANE_BY_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_BLOCK_LANE_SEQ")
    @SequenceGenerator(name = "LTL_BLOCK_LANE_SEQ", sequenceName = "LTL_BLOCK_LANE_SEQ", allocationSize = 1)
    @Column(name = "LTL_BLOCK_LANE_ID")
    private Long id;

    @Column(name = "CARRIER_ORG_ID")
    private Long carrierId;

    @Column(name = "SHIPPER_ORG_ID")
    private Long shipperId;

    @Transient
    private String origin;

    @Transient
    private String destination;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "EFF_DATE")
    private Date effDate;

    @Column(name = "EXP_DATE")
    private Date expDate;

    @Column(name = "NOTES")
    private String notes;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "blockLane")
    @Where(clause = "GEO_TYPE = 1")
    private List<LtlBlockLaneGeoServDetailsEntity> ltlBkLaneOriginGeoServiceDetails = new ArrayList<LtlBlockLaneGeoServDetailsEntity>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "blockLane")
    @Where(clause = "GEO_TYPE = 2")
    private List<LtlBlockLaneGeoServDetailsEntity> ltlBkLaneDestGeoServiceDetails = new ArrayList<LtlBlockLaneGeoServDetailsEntity>();

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public Long getShipperId() {
        return shipperId;
    }

    public void setShipperId(Long shipperId) {
        this.shipperId = shipperId;
    }

    /**
     * Get the origin for this geo service.
     *
     * @return the origin for the geo service
     */
    public String getOrigin() {
        if (origin == null && ltlBkLaneOriginGeoServiceDetails != null && !ltlBkLaneOriginGeoServiceDetails.isEmpty()) {
            origin = StringUtils.join(ltlBkLaneOriginGeoServiceDetails, ",");
        }
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Get the destination for the geo service.
     *
     * @return the destination for the geo service.
     */
    public String getDestination() {
        if (destination == null && ltlBkLaneDestGeoServiceDetails != null
                && !ltlBkLaneDestGeoServiceDetails.isEmpty()) {
            destination = StringUtils.join(ltlBkLaneDestGeoServiceDetails, ",");
        }
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public List<LtlBlockLaneGeoServDetailsEntity> getLtlBkLaneOriginGeoServiceDetails() {
        return ltlBkLaneOriginGeoServiceDetails;
    }

    /**
     * Set the geo details for origin.
     *
     * @param ltlBkLaneOriginGeoServiceDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlBkLaneOriginGeoServiceDetails(
            List<LtlBlockLaneGeoServDetailsEntity> ltlBkLaneOriginGeoServiceDetails) {
        this.ltlBkLaneOriginGeoServiceDetails.clear();
        this.ltlBkLaneOriginGeoServiceDetails.addAll(ltlBkLaneOriginGeoServiceDetails);
    }

    public List<LtlBlockLaneGeoServDetailsEntity> getLtlBkLaneDestGeoServiceDetails() {
        return ltlBkLaneDestGeoServiceDetails;
    }

    /**
     * Set the geo details for destination.
     *
     * @param ltlBkLaneDestGeoServiceDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlBkLaneDestGeoServiceDetails(
            List<LtlBlockLaneGeoServDetailsEntity> ltlBkLaneDestGeoServiceDetails) {
        this.ltlBkLaneDestGeoServiceDetails.clear();
        this.ltlBkLaneDestGeoServiceDetails.addAll(ltlBkLaneDestGeoServiceDetails);
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

}
