package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Load EDI data entity.
 * <p/>
 * EDI data contains generated code for GS and ST segments
 *
 * @author Nalapko Alexander
 */
@Entity
@Table(name = "EDI_LOAD_DATA")
public class LoadEdiDataEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = -3227049905824669782L;
    public static final String Q_GET_LOAD_BY_GS_SEGMENT = "com.pls.shipment.domain.edi.LoadEdiDataEntity.Q_GET_LOAD_BY_GS_SEGMENT";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID")
    private LoadEntity load;

    @Column(name = "EDI_NUM")
    private Long source;

    @Column(name = "EDI_FILE_NAME")
    private String fileName;

    @Column(name = "ISA")
    private Long isa;

    @Id
    @Column(name = "GS")
    private Long gs;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getISA() {
        return isa;
    }

    public void setISA(Long isa) {
        this.isa = isa;
    }

    public Long getGS() {
        return gs;
    }

    public void setGS(Long gs) {
        this.gs = gs;
    }

    @Override
    public Long getId() {
        return gs;
    }

    @Override
    public void setId(Long id) {
        this.gs = id;
    }
}
