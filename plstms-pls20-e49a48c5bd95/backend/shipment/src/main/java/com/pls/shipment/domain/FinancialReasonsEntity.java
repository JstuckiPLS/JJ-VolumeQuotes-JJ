package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.pls.core.domain.Identifiable;

/**
 * Entity for financial adjustment and accessorial reasons.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "FINAN_ADJ_ACC_REASONS")
@Immutable
public class FinancialReasonsEntity implements Identifiable<Long> {
    public static final String Q_LOAD_FOR_ADJUSTMENTS = "com.pls.shipment.domain.FinancialReasonsEntity.Q_LOAD_FOR_ADJUSTMENTS";

    private static final long serialVersionUID = 8565772076743912980L;

    @Id
    @Column(name = "ADJ_ACC_TYPE_ID")
    private Long id;

    @Column(name = "ADJ_ACC")
    private String adjustmentAccessorial;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ADJ_ACC_TYPE_CODE")
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdjustmentAccessorial() {
        return adjustmentAccessorial;
    }

    public void setAdjustmentAccessorial(String adjustmentAccessorial) {
        this.adjustmentAccessorial = adjustmentAccessorial;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
