package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Manual Bol Job Number Entity.
 * 
 * @author Artem Arapov
 *
 */
@Entity
@Table(name = "MANUAL_BOL_JOB_NUMBERS")
public class ManualBolJobNumberEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = 1499208708350851130L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manual_bol_job_num_sequence")
    @SequenceGenerator(name = "manual_bol_job_num_sequence", sequenceName = "MANAUL_BOL_JOB_NUMBERS_SEQ", allocationSize = 1)
    @Column(name = "MANUAL_BOL_JOB_NUMBER_ID")
    private Long id;

    @Column(name = "MANUAL_BOL_ID")
    private Long manualBolId;

    @Column(name = "JOB_NUMBER")
    private String jobNumber;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    private Integer version = 1;

    /**
     * Default constructor.
     */
    public ManualBolJobNumberEntity() {
    }

    /**
     * Constructor.
     * 
     * @param manualBolId - {@link ManualBolEntity#getId()}.
     * @param jobNumer - Job Number. Not <code>null</code>.
     */
    public ManualBolJobNumberEntity(Long manualBolId, String jobNumer) {
        this.manualBolId = manualBolId;
        this.jobNumber = jobNumer;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getManualBolId() {
        return manualBolId;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setManualBolId(Long manualBolId) {
        this.manualBolId = manualBolId;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public Integer getVersion() {
        return version;
    }
}
