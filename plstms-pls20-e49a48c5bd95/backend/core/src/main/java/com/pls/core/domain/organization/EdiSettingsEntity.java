package com.pls.core.domain.organization;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.EdiType;
import com.pls.core.domain.enums.ShipmentStatus;

/**
 * Entity for EDI Settings.
 * 
 * @author Brichak Aleksandr
 */
@Entity
@Table(name = "EDI_SETTINGS")
public class EdiSettingsEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = 4765349755368245L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_to_sequence")
    @SequenceGenerator(name = "bill_to_sequence", sequenceName = "EDI_SETTINGS_SEQ", allocationSize = 1)
    @Column(name = "EDI_SETTINGS_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;

    @Column(name = "EDI_TYPE")
    @Type(type = "com.pls.core.domain.usertype.EnumListToStringUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.EdiType")})
    private List<EdiType> ediType;

    @Column(name = "EDI_STATUS")
    @Type(type = "com.pls.core.domain.usertype.EnumListToStringUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentStatus")})
    private List<ShipmentStatus> ediStatus;

    @Column(name = "UNIQUE_REF_BOL")
    @Type(type = "yes_no")
    private boolean uniqueRefAndBol;

    @Column(name = "IGNORE_204_UPDATES")
    @Type(type = "yes_no")
    private boolean ignore204Updates = false;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public boolean isUniqueRefAndBol() {
        return uniqueRefAndBol;
    }

    public void setIsUniqueRefAndBol(boolean isUniqueRefAndBol) {
        this.uniqueRefAndBol = isUniqueRefAndBol;
    }

    public boolean isIgnore204Updates() {
        return ignore204Updates;
    }

    public void setIgnore204Updates(boolean ignore204Updates) {
        this.ignore204Updates = ignore204Updates;
    }

    public List<EdiType> getEdiType() {
        return ediType;
    }

    public void setEdiType(List<EdiType> ediType) {
        this.ediType = ediType;
    }

    public List<ShipmentStatus> getEdiStatus() {
        return ediStatus;
    }

    public void setEdiStatus(List<ShipmentStatus> ediStatus) {
        this.ediStatus = ediStatus;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }
}
