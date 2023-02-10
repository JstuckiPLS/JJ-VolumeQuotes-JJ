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
 * The entity mapped to Load Job Numbers.
 * 
 * @author Jasmin Dhamelia
 *
 */

@Entity
@Table(name = "LOAD_JOB_NUMBERS")
public class LoadJobNumbersEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = -7138312749556280206L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_job_num_sequence")
    @SequenceGenerator(name = "load_job_num_sequence", sequenceName = "load_job_numbers_seq", allocationSize = 1)
    @Column(name = "LOAD_JOB_NUMBER_ID")
    private Long id;

    @Column(name = "LOAD_ID")
    private Long loadId;

    @Column(name = "JOB_NUMBER")
    private String jobNumber;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    private Integer version = 1;

    /**
     * Default constructor.
     */
    public LoadJobNumbersEntity() {
    }

    /**
     * Constructor.
     * 
     * @param loadId - Load Id. Not <code>null</code>
     * @param jobNumber - Job number. Not <code>null</code>
     */
    public LoadJobNumbersEntity(Long loadId, String jobNumber) {
        this.loadId = loadId;
        this.jobNumber = jobNumber;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
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

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }
}
