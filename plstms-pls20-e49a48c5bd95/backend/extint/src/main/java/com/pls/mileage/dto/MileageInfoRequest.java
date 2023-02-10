package com.pls.mileage.dto;

import java.util.ArrayList;
import java.util.List;

public class MileageInfoRequest {

    private List<Address> addresses;

    private MileageType mileageType = MileageType.STANDARD;

    private MileageLookupServiceType lookupServiceType = MileageLookupServiceType.PCMILER;

    private HazmatType hazmatType = HazmatType.NONE;

    private boolean returnDrivingDirections = false;

    public void setOriginDestination(Address origin, Address destination) {
        addresses = new ArrayList<>();
        origin.setSequenceInRoute(0);
        destination.setSequenceInRoute(1);
        addresses.add(origin);
        addresses.add(destination);
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public MileageType getMileageType() {
        return mileageType;
    }

    public void setMileageType(MileageType mileageType) {
        this.mileageType = mileageType;
    }

    public MileageLookupServiceType getLookupServiceType() {
        return lookupServiceType;
    }

    public void setLookupServiceType(MileageLookupServiceType lookupServiceType) {
        this.lookupServiceType = lookupServiceType;
    }

    public HazmatType getHazmatType() {
        return hazmatType;
    }

    public void setHazmatType(HazmatType hazmatType) {
        this.hazmatType = hazmatType;
    }

    public boolean isReturnDrivingDirections() {
        return returnDrivingDirections;
    }

    public void setReturnDrivingDirections(boolean returnDrivingDirections) {
        this.returnDrivingDirections = returnDrivingDirections;
    }

}
