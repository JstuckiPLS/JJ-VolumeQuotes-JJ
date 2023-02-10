package com.pls.smc3.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.Identifiable;

/**
 * Entity class to store SMC3 Error Codes.
 * 
 * @author PAVANI CHALLA
 * 
 */
@Entity
@Table(name = "SMC3_ERROR_CODES")
public class SMCErrorCodeEntity implements Identifiable<Long> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final int MAGIC_NUMBER_173 = 173;
    private static final int MAGIC_NUMBER_181 = 181;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SMC3_ERROR_CODES_SEQUENCE")
    @SequenceGenerator(name = "SMC3_ERROR_CODES_SEQUENCE", sequenceName = "SMC3_ERROR_CODES_SEQ", allocationSize = 1)
    @Column(name = "SMC3_ERROR_CODE_ID")
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ERROR_TYPE")
    private String errorType;

    @Column(name = "RESOLUTION")
    private String resolution;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(MAGIC_NUMBER_173, MAGIC_NUMBER_181).append(getCode()).append(getDescription())
                .append(getErrorType()).append(getResolution()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof SMCErrorCodeEntity) {
            final SMCErrorCodeEntity entity = (SMCErrorCodeEntity) obj;
            return new EqualsBuilder().appendSuper(super.equals(obj)).append(getCode(), entity.getCode())
                    .append(getDescription(), entity.getDescription()).append(getErrorType(), entity.getErrorType())
                    .append(getResolution(), entity.getResolution()).isEquals();
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
