package com.pls.smc3.validation;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.pls.smc3.dto.LTLRateShipmentDTO;

/**
 * Class validates whether required fields for the request are not null.
 * 
 * @author PAVANI CHALLA
 * 
 */
@Component
public class LtlSMC3Validator {

    public static final String USA = "USA";
    public static final String MEX = "MEX";
    public static final String CAN = "CAN";
    private static final Integer LENGTH_3 = 3;
    private static final Integer LENGTH_5 = 5;
    private static final Integer LENGTH_6 = 6;
    private static final Integer LENGTH_8 = 8;

    private static List<Integer> sizes = Arrays.asList(LENGTH_3, LENGTH_5, LENGTH_6);
    private static List<String> supportedCountries = Arrays.asList(USA, MEX, CAN);

    /**
     * Validates whether the request has the required fields.
     * 
     * @param shipmentDTO
     *            {@link LTLRateShipmentDTO}.
     * @return boolean
     * @throws WebServiceValidationException
     *             validation exception
     */
    public boolean validateShipmentRequest(LTLRateShipmentDTO shipmentDTO) throws WebServiceValidationException {

        validateTariffName(shipmentDTO);
        validateRequiredFields(shipmentDTO);

        if (!validateCountry(shipmentDTO.getOriginCountry())) {
            throw new WebServiceValidationException("Invalid Origin Country. Supported countries are USA, MEX and CAN.");
        }
        if (!validateCountry(shipmentDTO.getDestinationCountry())) {

            throw new WebServiceValidationException(
                    "Invalid Destination Country. Supported countries are USA, MEX and CAN.");
        }
        if (!validateZipCode(shipmentDTO.getOriginPostalCode(), shipmentDTO.getOriginCountry())) {
            throw new WebServiceValidationException("Invalid Origin Country Postal Code.");
        }

        if (!validateZipCode(shipmentDTO.getDestinationPostalCode(), shipmentDTO.getDestinationCountry())) {
            throw new WebServiceValidationException("Invalid Destination Country Postal Code.");
        }
        return true;
    }

    /**
     * 
     * Validates if request has all the required fields or not.
     * 
     * @param shipmentDTO
     *            {@link LTLRateShipmentDTO}.
     * @return boolean
     * 
     * @throws WebServiceValidationException
     *             validation exception.
     */
    public boolean validateRequiredFields(LTLRateShipmentDTO shipmentDTO) throws WebServiceValidationException {

        if (shipmentDTO.getShipmentDate() == null) {
            throw new WebServiceValidationException("Shipment Date is required.");
        }
        if (StringUtils.isBlank(shipmentDTO.getOriginPostalCode())) {
            throw new WebServiceValidationException("Origin Postal Code is required.");
        }
        if (StringUtils.isBlank(shipmentDTO.getDestinationPostalCode())) {
            throw new WebServiceValidationException("Destination Postal Code is required.");
        }
        if (StringUtils.isBlank(shipmentDTO.getOriginCountry())) {
            throw new WebServiceValidationException("Origin Country is required.");
        }
        if (StringUtils.isBlank(shipmentDTO.getDestinationCountry())) {
            throw new WebServiceValidationException("Destination Country is required.");
        }
        if (null == shipmentDTO.getDetails() || shipmentDTO.getDetails().isEmpty()) {
            throw new WebServiceValidationException("LTL Request Detail is required.");
        }
        return true;
    }

    /**
     * Validates Tariff Name.
     * 
     * @param shipmentDTO
     *            {@link LTLRateShipmentDTO}.
     * @return boolean.
     * @throws WebServiceValidationException
     *             validation exception.
     */
    public boolean validateTariffName(LTLRateShipmentDTO shipmentDTO) throws WebServiceValidationException {

        if (StringUtils.isBlank(shipmentDTO.getTariffName())) {
            throw new WebServiceValidationException("Tariff Name is required.");
        }
        if (StringUtils.length(shipmentDTO.getTariffName()) < LENGTH_8) {
            throw new WebServiceValidationException("Minimum 8 characters required for tariff Name.");
        }
        return true;
    }

    /**
     * Validates the allowed country.
     * 
     * @param countryCode
     *            {@link String}.
     * @return boolean.
     */
    public boolean validateCountry(String countryCode) {

        return supportedCountries.contains(countryCode);
    }

    /**
     * Validates postal Codes for CAN, MEX & USA.
     * 
     * @param input
     *            {@link String}.
     * @param country
     *            {@link String}.
     * @return boolean
     */
    public boolean validateZipCode(String input, String country) {

        if (USA.equals(country) && ((input.length() == LENGTH_3) || (input.length() == LENGTH_5))) {

            return matchPattern("\\d+", input);
        }

        if (MEX.equals(country)) {

            return matchPattern("\\d{5}", input);
        }

        if (CAN.equals(country) && sizes.contains(input.length())) {

            return matchPattern("[\\p{Alnum}]*", input);
        }

        return false;
    }

    /**
     * Pattern Matcher method.
     * 
     * @param patternType
     *            {@link String}.
     * @param input
     *            {@link String}.
     * @return boolean.
     */
    public boolean matchPattern(String patternType, String input) {
        Pattern pattern = Pattern.compile(patternType);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

}
