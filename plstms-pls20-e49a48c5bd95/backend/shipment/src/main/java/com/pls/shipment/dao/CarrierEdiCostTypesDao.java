package com.pls.shipment.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.CarrierEdiCostTypesEntity;

import java.util.Map;

/**
 * DAO for {@link CarrierEdiCostTypesEntity}.
 *
 * @author Alexander Kirichenko
 */
public interface CarrierEdiCostTypesDao extends AbstractDao<CarrierEdiCostTypesEntity, Long> {

    /**
     * Method maps carrierEdiCostType to accessorial type if any or to "MS".
     * @param carrierId - id of the carrier
     * @param carrierEdiCostType - carrier EDI cost type
     * @return accessorial reef type.
     */
    String getAccessorialRefTypeByCarrierEdiCostType(Long carrierId, String carrierEdiCostType);

    /**
     * Method return map of EDI cost type to accessorial ref type for carrier.
     * @param carrierId - id of the carrier
     * @return map of EDI cost type to accessorial ref type for carrier.
     */
    Map<String, String> getRefTypeMapForCarrier(Long carrierId);
}
