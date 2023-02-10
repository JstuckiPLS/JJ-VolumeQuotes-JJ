package com.pls.shipment.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.enums.AddressPriority;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.RequiredFieldPointType;
import com.pls.core.domain.enums.RequiredFieldShipmentDirection;
import com.pls.core.domain.organization.BillToRequiredFieldEntity;
import com.pls.core.shared.BillToRequiredField;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Utility for processing BillTo data.
 * 
 * @author Brichak Aleksandr
 *
 */
public final class ShipmentBillToUtils {

    private ShipmentBillToUtils() {
    }

    /**
     * Get suitable required rules of BillTo for Load.
     * 
     * @param load
     *            {@link LoadEntity}
     * @return suitable required rules of BillTo.
     */
    public static Map<BillToRequiredField, BillToRequiredFieldEntity> getMatchedRules(LoadEntity load) {
        Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules = new HashMap<BillToRequiredField, BillToRequiredFieldEntity>();
        for (BillToRequiredFieldEntity rule : load.getBillTo().getBillToRequiredField()) {
            if (ObjectUtils.defaultIfNull(rule.getShipmentDirection(), RequiredFieldShipmentDirection.BOTH)
                    .isShipmentDirectionEquals(load.getShipmentDirection())) {
                switch (ObjectUtils.defaultIfNull(rule.getAddressDirection(), RequiredFieldPointType.BOTH)) {
                case BOTH:
                    addRulesIfNeeded(load.getDestination(), filteredRules, rule);
                    addRulesIfNeeded(load.getOrigin(), filteredRules, rule);
                    break;
                case DESTINATION:
                    addRulesIfNeeded(load.getDestination(), filteredRules, rule);
                    break;
                case ORIGIN:
                    addRulesIfNeeded(load.getOrigin(), filteredRules, rule);
                    break;
                default:
                    break;
                }
            }
        }
        return filteredRules;
    }

    private static void addRulesIfNeeded(LoadDetailsEntity address,
            Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules, BillToRequiredFieldEntity rule) {
        if (isContainAddres(address, rule)) {
            putRules(filteredRules, rule.getFieldName(), rule);
        }
    }

    private static void putRules(Map<BillToRequiredField, BillToRequiredFieldEntity> filteredRules,
            BillToRequiredField key, BillToRequiredFieldEntity value) {
        if (filteredRules.containsKey(key)) {
            if (morePriority(filteredRules.get(key), value)) {
                filteredRules.put(key, value);
            }
        } else {
            filteredRules.put(key, value);
        }
    }

    private static boolean morePriority(BillToRequiredFieldEntity oldEntity, BillToRequiredFieldEntity newEntity) {
        return newEntity.getMatchedBy().getPriority() < oldEntity.getMatchedBy().getPriority();
    }

    private static boolean isContainAddres(LoadDetailsEntity loadDetails,
            BillToRequiredFieldEntity billToReqFieldEntity) {
            return isReqFieldsAddressEmpty(billToReqFieldEntity)
                    || isContainReqFieldsZip(loadDetails, billToReqFieldEntity)
                    || isContainReqFieldsCity(loadDetails, billToReqFieldEntity)
                    || isContainReqFieldsState(loadDetails, billToReqFieldEntity)
                    || isContainReqFieldsCountry(loadDetails, billToReqFieldEntity);
    }

    private static boolean isContainReqFieldsCountry(LoadDetailsEntity loadDetails,
            BillToRequiredFieldEntity billToReqFieldEntity) {
        if (billToReqFieldEntity.getCountry() != null
                && billToReqFieldEntity.getCountry().indexOf(loadDetails.getAddress().getCountryCode()) > -1) {
            billToReqFieldEntity.setAddressPriority(PointType.ORIGIN.equals(loadDetails.getPointType())
                    ? AddressPriority.ORIGIN_COUNTRY : AddressPriority.DESTINATION_COUNTRY);
            return true;
        }
        return false;
    }

    private static boolean isContainReqFieldsState(LoadDetailsEntity loadDetails,
            BillToRequiredFieldEntity billToReqFieldEntity) {
        if (billToReqFieldEntity.getState() != null
                && billToReqFieldEntity.getState().indexOf(loadDetails.getAddress().getStateCode()) > -1) {
            billToReqFieldEntity.setAddressPriority(PointType.ORIGIN.equals(loadDetails.getPointType())
                    ? AddressPriority.ORIGIN_STATE : AddressPriority.DESTINATION_STATE);
            return true;
        }
        return false;
    }

    private static boolean isContainReqFieldsCity(LoadDetailsEntity loadDetails,
            BillToRequiredFieldEntity billToReqFieldEntity) {
        if (billToReqFieldEntity.getCity() != null
                && billToReqFieldEntity.getCity().indexOf(loadDetails.getAddress().getCity()) > -1) {
            billToReqFieldEntity.setAddressPriority(PointType.ORIGIN.equals(loadDetails.getPointType())
                    ? AddressPriority.ORIGIN_CITY : AddressPriority.DESTINATION_CITY);
            return true;
        }
        return false;
    }

    private static boolean isContainReqFieldsZip(LoadDetailsEntity loadDetails,
            BillToRequiredFieldEntity billToReqFieldEntity) {
        if (billToReqFieldEntity.getZip() != null
                && billToReqFieldEntity.getZip().indexOf(loadDetails.getAddress().getZip()) > -1) {
            billToReqFieldEntity.setAddressPriority(PointType.ORIGIN.equals(loadDetails.getPointType())
                    ? AddressPriority.ORIGIN_ZIP : AddressPriority.DESTINATION_ZIP);
            return true;
        }
        return false;
    }

    private static boolean isReqFieldsAddressEmpty(BillToRequiredFieldEntity billToReqFieldEntity) {
        if (StringUtils.isEmpty(billToReqFieldEntity.getZip())
                && StringUtils.isEmpty(billToReqFieldEntity.getCity())
                && StringUtils.isEmpty(billToReqFieldEntity.getState())
                && StringUtils.isEmpty(billToReqFieldEntity.getCountry())) {
            billToReqFieldEntity.setAddressPriority(AddressPriority.EMPTY_ADDRESS);
            return true;
        }
        return false;
    }
}
