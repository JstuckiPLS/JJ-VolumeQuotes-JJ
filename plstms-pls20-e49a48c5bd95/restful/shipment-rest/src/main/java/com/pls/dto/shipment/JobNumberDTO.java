package com.pls.dto.shipment;

/**
 * Job Number DTO.
 * 
 * @author Artem Arapov
 *
 */
public class JobNumberDTO {

    private Long id;

    private String jobNumber;

    /**
     * Default constructor.
     */
    public JobNumberDTO() {
    }

    /**
     * Constructor.
     * 
     * @param id - id of job number.
     * @param jobNumber - Job Number. Not <code>null</code>.
     */
    public JobNumberDTO(Long id, String jobNumber) {
        this.id = id;
        this.jobNumber = jobNumber;
    }

    public Long getId() {
        return id;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }
}
