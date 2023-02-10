package com.pls.dto.organization;

import com.pls.dto.FreightBillPayToDTO;

/**
 * Organization Freight Bill Pay To DTO.
 * 
 * @author Artem Arapov
 *
 */
public class OrganizationFreightBillPayToDTO {

    private Long id;

    private Long orgId;

    private FreightBillPayToDTO freightBillPayTo;

    public Long getId() {
        return id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public FreightBillPayToDTO getFreightBillPayTo() {
        return freightBillPayTo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setFreightBillPayTo(FreightBillPayToDTO freightBillPayTo) {
        this.freightBillPayTo = freightBillPayTo;
    }
}
