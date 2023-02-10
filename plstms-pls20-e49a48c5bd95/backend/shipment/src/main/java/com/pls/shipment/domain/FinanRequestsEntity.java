package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.shared.Status;

/**
 * The entity mapped to finan_requests.
 * @author Hima Bindu Challa
 *
 */
@Entity
@Table(name = "finan_requests")
public class FinanRequestsEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 5886697332227541148L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "frq_sequence")
    @SequenceGenerator(name = "frq_sequence", sequenceName = "frq_seq", allocationSize = 1)
    @Column(name = "REQUEST_ID")
    private Long id;

    @Column(name = "P_PERSON_ID")
    private Long personId;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "parameter_list")
    private String parameterList;

    @Column(name = "p_load_list")
    private String loadList;

    @Column(name = "p_fin_status")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentFinancialStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentFinancialStatus finStatus;

    @Column(name = "p_to_fin_status")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentFinancialStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentFinancialStatus toFinStatus;

    @Column(name = "p_hold_release")
    private String holdRelease = "R";

    @Column(name = "p_gl_date")
    private Date glDate;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "ORIGINATING_SYSTEM")
    private String originatingSystem = "PLS2_LT";

    @Column(name = "loads_processed")
    private Integer loadsProcessed;

    @Column(name = "load_status_changes")
    private Integer loadStatusChanges;

    @Column(name = "loads_finalized")
    private Integer loadsFinalized;

    @Column(name = "ar_finalized")
    private Integer arFinalized;

    @Column(name = "ap_finalized")
    private Integer apFinalized;

    @Column(name = "load_errors")
    private Integer loadErrors;

    @Column(name = "ab_count")
    private Integer abCount;

    @Column(name = "abh_count")
    private Integer abhCount;

    @Column(name = "abr_count")
    private Integer abrCount;

    @Column(name = "percent_complete")
    private Integer percentComplete;

    @Column(name = "total_costs_finalized")
    private BigDecimal totalCostsFinalized;

    @Column(name = "total_revenue_finalized")
    private BigDecimal totalRevenueFinalized;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getParameterList() {
        return parameterList;
    }

    public void setParameterList(String parameterList) {
        this.parameterList = parameterList;
    }

    public String getLoadList() {
        return loadList;
    }

    public void setLoadList(String loadList) {
        this.loadList = loadList;
    }

    public ShipmentFinancialStatus getFinStatus() {
        return finStatus;
    }

    public void setFinStatus(ShipmentFinancialStatus finStatus) {
        this.finStatus = finStatus;
    }

    public ShipmentFinancialStatus getToFinStatus() {
        return toFinStatus;
    }

    public void setToFinStatus(ShipmentFinancialStatus toFinStatus) {
        this.toFinStatus = toFinStatus;
    }

    public String getHoldRelease() {
        return holdRelease;
    }

    public void setHoldRelease(String holdRelease) {
        this.holdRelease = holdRelease;
    }

    public Date getGlDate() {
        return glDate;
    }

    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getOriginatingSystem() {
        return originatingSystem;
    }

    public void setOriginatingSystem(String originatingSystem) {
        this.originatingSystem = originatingSystem;
    }

    public Integer getLoadsProcessed() {
        return loadsProcessed;
    }

    public void setLoadsProcessed(Integer loadsProcessed) {
        this.loadsProcessed = loadsProcessed;
    }

    public Integer getLoadStatusChanges() {
        return loadStatusChanges;
    }

    public void setLoadStatusChanges(Integer loadStatusChanges) {
        this.loadStatusChanges = loadStatusChanges;
    }

    public Integer getLoadsFinalized() {
        return loadsFinalized;
    }

    public void setLoadsFinalized(Integer loadsFinalized) {
        this.loadsFinalized = loadsFinalized;
    }

    public Integer getArFinalized() {
        return arFinalized;
    }

    public void setArFinalized(Integer arFinalized) {
        this.arFinalized = arFinalized;
    }

    public Integer getApFinalized() {
        return apFinalized;
    }

    public void setApFinalized(Integer apFinalized) {
        this.apFinalized = apFinalized;
    }

    public Integer getLoadErrors() {
        return loadErrors;
    }

    public void setLoadErrors(Integer loadErrors) {
        this.loadErrors = loadErrors;
    }

    public Integer getAbCount() {
        return abCount;
    }

    public void setAbCount(Integer abCount) {
        this.abCount = abCount;
    }

    public Integer getAbhCount() {
        return abhCount;
    }

    public void setAbhCount(Integer abhCount) {
        this.abhCount = abhCount;
    }

    public Integer getAbrCount() {
        return abrCount;
    }

    public void setAbrCount(Integer abrCount) {
        this.abrCount = abrCount;
    }

    public Integer getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(Integer percentComplete) {
        this.percentComplete = percentComplete;
    }

    public BigDecimal getTotalCostsFinalized() {
        return totalCostsFinalized;
    }

    public void setTotalCostsFinalized(BigDecimal totalCostsFinalized) {
        this.totalCostsFinalized = totalCostsFinalized;
    }

    public BigDecimal getTotalRevenueFinalized() {
        return totalRevenueFinalized;
    }

    public void setTotalRevenueFinalized(BigDecimal totalRevenueFinalized) {
        this.totalRevenueFinalized = totalRevenueFinalized;
    }
}
