package com.pls.smc3.service.impl;

import com.smc.webservices.AuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Helper class for SMC3 operations.
 * 
 * @author Pavani Challa
 * 
 */
@Component
public class BaseSMC3ServiceImpl {

    @Value("${smc3.rateware.license.key}")
    protected String rateWareLicenseKey;

    @Value("${smc3.rateware.username}")
    protected String rateWareUserName;

    @Value("${smc3.rateware.password}")
    protected String rateWarePassword;

    @Value("${smc3.carrierconnect.license.key}")
    protected String connectLicenseKey;

    @Value("${smc3.carrierconnect.username}")
    protected String connectUserName;

    @Value("${smc3.carrierconnect.password}")
    protected String connectPassword;

    /**
     * Util method to convert date to string.
     * 
     * @param input
     *            {@link Date}.
     * @return {@link String}.
     */
    public String convertDateToString(Date input) {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);
        format.setLenient(false);
        return format.format(input);
    }

    /**
     * Creates the authentication token for rateware services.
     * 
     * @return the authentication token
     */
    public AuthenticationToken getRatewareAuthToken() {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setLicenseKey(rateWareLicenseKey);
        authenticationToken.setUsername(rateWareUserName);
        authenticationToken.setPassword(rateWarePassword);
        return authenticationToken;
    }

    /**
     * Creates the authentication token for carrier connect services.
     * 
     * @return the authentication token
     */
    public AuthenticationToken getCarrierConnectAuthToken() {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setLicenseKey(connectLicenseKey);
        authenticationToken.setUsername(connectUserName);
        authenticationToken.setPassword(connectPassword);
        return authenticationToken;
    }

}
