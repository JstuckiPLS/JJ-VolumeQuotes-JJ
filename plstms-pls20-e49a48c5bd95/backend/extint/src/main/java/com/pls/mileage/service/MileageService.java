package com.pls.mileage.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.shared.AddressVO;
import com.pls.extint.shared.MileageCalculatorType;
import com.pls.mileage.dto.Address;
import com.pls.mileage.dto.MileageInfoRequest;
import com.pls.mileage.dto.MileageInfoResponse;
import com.pls.mileage.dto.MileageLookupServiceType;
import com.pls.mileage.dto.MileageType;

/**
 * Service to calculate mileage from Origin to Destination.
 */
@Component
public class MileageService {
    
    @Autowired
    MileageClientService mileageClientService;
    
    public int getMileage(AddressVO origAddress, AddressVO destAddress, MileageCalculatorType mileageCalcType) {
        
        MileageInfoResponse mileageInfo = mileageClientService
                .getMileageInfo(createMileageInfoRequest(origAddress, destAddress, mileageCalcType));

        if (mileageInfo != null && mileageInfo.getTotalMiles() != null) {
            return mileageInfo.getTotalMiles().intValue();
        }

        return 0;
    }
    
    private MileageInfoRequest createMileageInfoRequest(AddressVO origin, AddressVO destination,
            MileageCalculatorType calculatorType) {
        MileageInfoRequest request = new MileageInfoRequest();
        request.setOriginDestination(convertToMileageAddress(origin), convertToMileageAddress(destination));
        request.setLookupServiceType(calculatorType == MileageCalculatorType.MILE_MAKER ? MileageLookupServiceType.MILEMAKER : MileageLookupServiceType.PCMILER);
        request.setMileageType(MileageType.OPTIMIZE);
        return request;
    }

    private Address convertToMileageAddress(AddressVO address) {
        Address mAddress = new Address();
        
        // note - (SCR 6855) for now we will disregard Address 1 and 2 for mileage requests
        mAddress.setCity(address.getCity());
        mAddress.setCountryCode(address.getCountryCode());

        if (address.getCountryCode().equalsIgnoreCase("USA") && address.getPostalCode().length() > 5) {
            // strip the geographic code from the postal code if exists before sending for mileage calculation.
            mAddress.setPostalCode(address.getPostalCode().substring(0, 5));
        } else {
            mAddress.setPostalCode(address.getPostalCode());
        }
        mAddress.setStateCode(address.getStateCode());
        return mAddress;
    }
}
