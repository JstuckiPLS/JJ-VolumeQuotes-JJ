package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.shared.AddressVO;

/**
 * Criteria object to get rates for a quote or shipment.
 *
 * @author Hima Bindu Challa
 *
 */
public class RateQuoteCO implements Serializable {

    private static final long serialVersionUID = -83512573927629323L;

    private Long shipperOrgId;
    private Long carrierOrgId;
    private AddressVO originAddress;
    private AddressVO destinationAddress;

    private List<String> accessorialCodes;

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public Long getCarrierOrgId() {
        return carrierOrgId;
    }

    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }

    public AddressVO getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(AddressVO originAddress) {
        this.originAddress = originAddress;
    }

    public AddressVO getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(AddressVO destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public List<String> getAccessorialCodes() {
        return accessorialCodes;
    }

    public void setAccessorialCodes(List<String> accessorialCodes) {
        this.accessorialCodes = accessorialCodes;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("shipperOrgId", shipperOrgId)
                .append("carrierOrgId", carrierOrgId)
                .append("originAddress", originAddress.toString())
                .append("destinationAddress", destinationAddress.toString());

        return builder.toString();
    }
}
