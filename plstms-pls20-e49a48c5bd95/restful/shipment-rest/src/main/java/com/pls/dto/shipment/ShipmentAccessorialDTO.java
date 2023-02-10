package com.pls.dto.shipment;

import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.shared.Status;

/**
 * DTO for shipment accessorial.
 *
 * @author Mikhail Boldinov, 14/05/13
 */
public class ShipmentAccessorialDTO {

    private String id;

    private String description;

    private String applicableTo;

    private Status status;

    private LtlAccessorialGroup accessorialGroup;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LtlAccessorialGroup getAccessorialGroup() {
        return accessorialGroup;
    }

    public void setAccessorialGroup(LtlAccessorialGroup group) {
        this.accessorialGroup = group;
    }

    public String getApplicableTo() {
        return applicableTo;
    }

    public void setApplicableTo(String applicableTo) {
        this.applicableTo = applicableTo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
