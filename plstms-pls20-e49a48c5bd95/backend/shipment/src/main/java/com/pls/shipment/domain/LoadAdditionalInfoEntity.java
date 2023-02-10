package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * The entity mapped to LoadAddlInfo.
 * 
 * @author Brichak Aleksandr
 * 
 */
@Entity
@Table(name = "LOAD_ADDL_INFO")
public class LoadAdditionalInfoEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    public static final String Q_GET_ADDITIONAL_INFO_BY_LOAD_ID = "com.pls.shipment.domain.LoadAdditionalInfoEntity.Q_GET_ADDITIONAL_INFO_BY_LOAD_ID";

    private static final long serialVersionUID = 8113467422073854636L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_addl_info_sequence")
    @SequenceGenerator(name = "load_addl_info_sequence", sequenceName = "load_addl_info_seq", allocationSize = 1)
    @Column(name = "LOAD_ADDL_INFO_ID")
    private Long id;

    @Column(name = "LOAD_ID", insertable = false, updatable = false)
    private Long loadId;

    @Column(name = "MARKUP")
    private Long markup;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @OneToOne
    @JoinColumn(name = "LOAD_ID", referencedColumnName = "LOAD_ID")
    private LoadEntity load;

    private final PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getMarkup() {
        return markup;
    }

    public void setMarkup(Long markup) {
        this.markup = markup;
    }

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
}
