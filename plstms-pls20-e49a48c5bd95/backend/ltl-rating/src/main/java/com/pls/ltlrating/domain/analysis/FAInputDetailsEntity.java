package com.pls.ltlrating.domain.analysis;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.shared.StatusYesNo;

/**
 * Data from imported file.
 *
 * @author Svetlana Kulish
 */

@Entity
@Table(name = "FA_INPUT_DETAILS")
public class FAInputDetailsEntity implements Identifiable<Long> {
    private static final long serialVersionUID = -3742993077305140035L;

    public static final String Q_GET_INPUT_DETAILS_BY_FA_ID = "com.pls.ltlrating.domain.analysis.FAInputDetailsEntity.Q_GET_INPUT_DETAILS_BY_FA_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FA_INPUT_DETAILS_SEQUENCE")
    @SequenceGenerator(name = "FA_INPUT_DETAILS_SEQUENCE", sequenceName = "FA_INPUT_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "INPUT_DETAIL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANALYSIS_ID", nullable = false)
    private FAFinancialAnalysisEntity analysis;

    @Column(name = "COMPLETED")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo completed = StatusYesNo.NO;

    @Column(name = "SEQ_NUMBER", nullable = false)
    private Long seq;

    @Column(name = "USER_SEQ_NUMBER")
    private Long userSeq;

    @Column(name = "USER_SCAC")
    private String carrierScac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID")
    private CarrierEntity carrier;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SHIPMENT_DATE")
    private Date shipmentDate;

    @Column(name = "ORIGIN_CITY")
    private String originCity;

    @Column(name = "ORIGIN_STATE")
    private String originState;

    @Column(name = "ORIGIN_ZIP", nullable = false)
    private String originZip;

    @Column(name = "ORIGIN_OVERRIDE_ZIP")
    private String originOverrideZip;

    @Column(name = "ORIGIN_COUNTRY", nullable = false)
    private String originCountry;

    @Column(name = "DEST_CITY")
    private String destCity;

    @Column(name = "DEST_STATE")
    private String destState;

    @Column(name = "DEST_ZIP", nullable = false)
    private String destZip;

    @Column(name = "DEST_OVERRIDE_ZIP")
    private String destOverrideZip;

    @Column(name = "DEST_COUNTRY", nullable = false)
    private String destCountry;

    @Column(name = "PALLET")
    private Long pallet;

    @Column(name = "PIECES")
    private Long pieces;

    @Column(name = "CALCULATE_FSC")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo calculateFSC;

    @OneToMany(mappedBy = "inputDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FAMaterialsEntity> materials;

    @OneToMany(mappedBy = "inputDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FAAccessorialsEntity> accessorials;

    @OneToMany(mappedBy = "inputDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FAOutputDetailsEntity> outputDetails;

    public Set<FAMaterialsEntity> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<FAMaterialsEntity> materials) {
        this.materials = materials;
    }

    public Set<FAAccessorialsEntity> getAccessorials() {
        return accessorials;
    }

    public void setAccessorials(Set<FAAccessorialsEntity> accessorials) {
        this.accessorials = accessorials;
    }

    public Set<FAOutputDetailsEntity> getOutputDetails() {
        return outputDetails;
    }

    public FAFinancialAnalysisEntity getAnalysis() {
        return analysis;
    }

    public void setAnalysis(FAFinancialAnalysisEntity analysis) {
        this.analysis = analysis;
    }

    public StatusYesNo getCompleted() {
        return completed;
    }

    public void setCompleted(StatusYesNo completed) {
        this.completed = completed;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public Long getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(Long userSeq) {
        this.userSeq = userSeq;
    }

    public String getCarrierScac() {
        return carrierScac;
    }

    public void setCarrierScac(String carrierScac) {
        this.carrierScac = carrierScac;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginState() {
        return originState;
    }

    public void setOriginState(String originState) {
        this.originState = originState;
    }

    public String getOriginZip() {
        return originZip;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public String getOriginOverrideZip() {
        return originOverrideZip;
    }

    public void setOriginOverrideZip(String originOverrideZip) {
        this.originOverrideZip = originOverrideZip;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public String getDestZip() {
        return destZip;
    }

    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }

    public String getDestOverrideZip() {
        return destOverrideZip;
    }

    public void setDestOverrideZip(String destOverrideZip) {
        this.destOverrideZip = destOverrideZip;
    }

    public String getDestCountry() {
        return destCountry;
    }

    public void setDestCountry(String destCountry) {
        this.destCountry = destCountry;
    }

    public Long getPallet() {
        return pallet;
    }

    public void setPallet(Long pallet) {
        this.pallet = pallet;
    }

    public Long getPieces() {
        return pieces;
    }

    public void setPieces(Long pieces) {
        this.pieces = pieces;
    }

    public StatusYesNo getCalculateFSC() {
        return calculateFSC;
    }

    public void setCalculateFSC(StatusYesNo calculateFSC) {
        this.calculateFSC = calculateFSC;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    public void setOutputDetails(Set<FAOutputDetailsEntity> outputDetails) {
        this.outputDetails = outputDetails;
    }
}
