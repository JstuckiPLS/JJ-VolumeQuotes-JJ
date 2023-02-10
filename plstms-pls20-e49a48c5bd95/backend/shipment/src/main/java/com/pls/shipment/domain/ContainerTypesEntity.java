package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * The entity mapped to container_types.
 * @author Ladan Khamnian
 *
 */
@Entity
@Table(name = "container_types")
public class ContainerTypesEntity {

    @Id
    @Column(name = "CONTAINER_CD")
    private String containerCD;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "BILLING_CONTAINER_CD")
    private String billingContainerCD;

    @Column(name = "MODE_OF_TRANSPORT")
    private String modeOfTransport;

    public String getContainerCD() {
        return containerCD;
    }

    public void setContainerCD(String containerCD) {
        this.containerCD = containerCD;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBillingContainerCD() {
        return billingContainerCD;
    }

    public void setBillingContainerCD(String billingContainerCD) {
        this.billingContainerCD = billingContainerCD;
    }

    public String getModeOfTransport() {
        return modeOfTransport;
    }

    public void setModeOfTransport(String modeOfTransport) {
        this.modeOfTransport = modeOfTransport;
    }

}
