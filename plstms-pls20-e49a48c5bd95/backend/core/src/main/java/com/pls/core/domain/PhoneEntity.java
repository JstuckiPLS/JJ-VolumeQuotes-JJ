package com.pls.core.domain;

import com.pls.core.domain.enums.PhoneType;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Phones Table.
 * 
 * @author Artem Arapov
 */
@Entity
@Table(name = "PHONES")
public class PhoneEntity implements Identifiable<Long>, PhoneNumber, HasModificationInfo {

    private static final long serialVersionUID = 39977339126398197L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PHONES_SEQUENCE")
    @SequenceGenerator(name = "PHONES_SEQUENCE", sequenceName = "PHONES_SEQ", allocationSize = 1)
    @Column(name = "PHONE_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "PHONE_TYPE", nullable = false)
    private PhoneType type;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "AREA_CODE")
    private String areaCode;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String number;

    @Column(name = "EXTENSION")
    private String extension;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    @Version
    private long version = 1L;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public PhoneType getType() {
        return type;
    }

    public void setType(PhoneType phoneType) {
        this.type = phoneType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
