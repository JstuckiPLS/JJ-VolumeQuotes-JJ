package com.pls.core.domain.address;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.organization.CountryEntity;

/**
 * Route data. Combination of all Origin and Destination fields should be unique for ROUTES table.
 * 
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "ROUTES")
public class RouteEntity implements Identifiable<Long> {

    private static final long serialVersionUID = -6383812156488470291L;

    public static final String Q_BY_ADDRESS = "com.pls.core.domain.RouteEntity.Q_BY_ADDRESS";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROUTES_SEQ")
    @SequenceGenerator(name = "ROUTES_SEQ", sequenceName = "ROUTES_SEQ", allocationSize = 1)
    @Column(name = "ROUTE_ID")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate = new Date();

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "ORIG_ZIP")
    private String originZip;

    @Column(name = "ORIG_COUNTRY")
    private String originCountry;

    @Column(name = "ORIG_STATE")
    private String originState;

    @Column(name = "ORIG_CITY")
    private String originCity;

    @Column(name = "DEST_ZIP")
    private String destZip;

    @Column(name = "DEST_COUNTRY")
    private String destCountry;

    @Column(name = "DEST_STATE")
    private String destState;

    @Column(name = "DEST_CITY")
    private String destCity;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumns({
        @JoinColumn(name = "ORIG_ZIP", referencedColumnName = "ZIP_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "ORIG_STATE", referencedColumnName = "STATE_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "ORIG_COUNTRY", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "ORIG_CITY", referencedColumnName = "CITY", insertable = false, updatable = false)
    })
    private ZipCodeEntity originZipCode;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumns({
        @JoinColumn(name = "DEST_ZIP", referencedColumnName = "ZIP_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "DEST_STATE", referencedColumnName = "STATE_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "DEST_COUNTRY", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "DEST_CITY", referencedColumnName = "CITY", insertable = false, updatable = false)
    })
    private ZipCodeEntity destinationZipCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getOriginZip() {
        return originZip;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getOriginState() {
        return originState;
    }

    public void setOriginState(String originState) {
        this.originState = originState;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestZip() {
        return destZip;
    }

    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }

    public String getDestCountry() {
        return destCountry;
    }

    public void setDestCountry(String destCountry) {
        this.destCountry = destCountry;
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public ZipCodeEntity getOriginZipCode() {
        return originZipCode == null ? fillOriginZipCode() : originZipCode;
    }

    private ZipCodeEntity fillOriginZipCode() {
        ZipCodeEntity zipCode = new ZipCodeEntity();
        zipCode.setCity(originCity);
        zipCode.setStateCode(originState);
        zipCode.setZipCode(originZip);
        zipCode.setId(new ZipCodePK());
        zipCode.getId().setCountry(new CountryEntity());
        zipCode.getId().getCountry().setId(originCountry);
        return zipCode;
    }

    public void setOriginZipCode(ZipCodeEntity originZipCode) {
        this.originZipCode = originZipCode;
    }

    public ZipCodeEntity getDestinationZipCode() {
        return destinationZipCode == null ? fillDestinationZipCode() : destinationZipCode;
    }

    private ZipCodeEntity fillDestinationZipCode() {
        ZipCodeEntity zipCode = new ZipCodeEntity();
        zipCode.setCity(destCity);
        zipCode.setStateCode(destState);
        zipCode.setZipCode(destZip);
        zipCode.setId(new ZipCodePK());
        zipCode.getId().setCountry(new CountryEntity());
        zipCode.getId().getCountry().setId(destCountry);
        return zipCode;
    }

    public void setDestinationZipCode(ZipCodeEntity destinationZipCode) {
        this.destinationZipCode = destinationZipCode;
    }
}
