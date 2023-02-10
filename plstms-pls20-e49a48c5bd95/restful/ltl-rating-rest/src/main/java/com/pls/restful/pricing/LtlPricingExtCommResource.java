package com.pls.restful.pricing;


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.address.ZipService;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.AddressVO;
import com.pls.ltlrating.service.LtlPricingEngineWSService;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingWSResult;
import com.pls.user.service.UserService;

/**
 * Resource for accessing pricing information from an external application (will be exposed as webservice call).
 * 
 * @author Pavani Challa
 */
@Controller
@RequestMapping("/ltlpricing")
@Transactional(readOnly = true)
public class LtlPricingExtCommResource {

    @Autowired
    private LtlPricingEngineWSService ratingWSService;

    @Autowired
    private UserService userService;

    @Autowired
    private ZipService zipService;

    /**
     * Calculates the rates based on the criteria object and returns the customer rates.
     * 
     * @param criteria
     *            Criteria object based on which rates will be returned
     * @return the rates for the customer
     * @throws Exception - application exception
     */
    @RequestMapping(value = "/customer/getRates", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public LtlPricingWSResult getLTLPricing(@RequestBody GetOrderRatesCO criteria) throws Exception {

       UserEntity userEntity = userService.findByUserId(criteria.getUserId(), criteria.getPassword());

        if (userEntity == null || !UserStatus.ACTIVE.equals(userEntity.getUserStatus())) {
            return getAuthErrorResult();
        }
        if (criteria.getShipperOrgId() == null) {
            criteria.setShipperOrgId(userEntity.getParentOrgId());
        }

        prepareAddress(criteria.getOriginAddress());
        prepareAddress(criteria.getDestinationAddress());

        return ratingWSService.getRatesForCustomer(criteria);
    }

    private void prepareAddress(AddressVO address) throws ValidationException {
        // If the Country code in address is missing, set "USA" as country code.
        if (address != null) {
            if (address.getCountryCode() == null || "".equals(address.getCountryCode().trim())) {
                address.setCountryCode("USA");
            }
            if (StringUtils.isBlank(address.getStateCode()) && StringUtils.isNotBlank(address.getPostalCode())) {
                List<ZipCodeEntity> destZipDetails = zipService.findZips(address.getCountryCode(), address.getPostalCode(), 1);

                if (destZipDetails != null && !destZipDetails.isEmpty()) {
                    ZipCodeEntity destZipDetail = destZipDetails.get(0);
                    address.setCity(destZipDetail.getCity());
                    address.setStateCode(destZipDetail.getStateCode());
                } else {
                    address.setStateCode("");
                }
            }
        }
    }

    private LtlPricingWSResult getAuthErrorResult() {
        LtlPricingWSResult wsResult = new LtlPricingWSResult();
        wsResult.setCode(1);
        wsResult.setError("AUTHENTICATION_ERROR");
        wsResult.setProfileCount(0);
        return wsResult;
    }

}
