package com.pls.shipment.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Product adjustments info.
 * 
 * @author Dmitry Nikolaenko
 *
 */
@Entity
@Table(name = "FINAN_ADJ_ACC_DETAIL_PROD_INFO")
public class FinancialAccessorialsProductInfoEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 12561895899524391L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "faa_det_prod_info_sequence")
    @SequenceGenerator(name = "faa_det_prod_info_sequence", sequenceName = "FAA_DET_PROD_INFO_SEQ", allocationSize = 1)
    @Column(name = "FAA_DETAIL_ADDL_PROD_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAA_DETAIL_ID", nullable = false)
    private FinancialAccessorialsEntity financialAccessorials;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "LOAD_MATERIAL_ID", nullable = false)
    private LoadMaterialEntity loadMaterial;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public FinancialAccessorialsEntity getFinancialAccessorials() {
        return financialAccessorials;
    }

    public void setFinancialAccessorials(FinancialAccessorialsEntity financialAccessorials) {
        this.financialAccessorials = financialAccessorials;
    }

    public LoadMaterialEntity getLoadMaterial() {
        return loadMaterial;
    }

    public void setLoadMaterial(LoadMaterialEntity loadMaterial) {
        this.loadMaterial = loadMaterial;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
