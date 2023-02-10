package com.pls.ltlrating.shared;

import com.pls.core.shared.AddressVO;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * The criteria object to get terminal information for the selected carrier.
 * @author Hima Bindu Challa
 *
 */
public class GetTerminalsCO implements Serializable {

    private static final long serialVersionUID = -3423424242346384L;

    private String scac;
    private AddressVO originAddress;
    private AddressVO destinationAddress;
    private Long profileDetailId;
    private Date shipmentDate;

    public String getScac() {
        return scac;
    }
    public void setScac(String scac) {
        this.scac = scac;
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

    public Long getProfileDetailId() {
        return profileDetailId;
    }
    public void setProfileDetailId(Long profileDetailId) {
        this.profileDetailId = profileDetailId;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("scac", scac)
                .append("originAddress Postal Code", originAddress.getPostalCode())
                .append("originAddress City", originAddress.getCity())
                .append("originAddress State", originAddress.getStateCode())
                .append("originAddress Country", originAddress.getCountryCode())
                .append("destinationAddress Postal Code", destinationAddress.getPostalCode())
                .append("destinationAddress City", destinationAddress.getCity())
                .append("destinationAddress State", destinationAddress.getStateCode())
                .append("destinationAddress Country", destinationAddress.getCountryCode());

        return builder.toString();
    }
}
