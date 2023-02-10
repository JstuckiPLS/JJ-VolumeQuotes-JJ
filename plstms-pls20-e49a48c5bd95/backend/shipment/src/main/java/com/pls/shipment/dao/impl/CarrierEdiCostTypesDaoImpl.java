package com.pls.shipment.dao.impl;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.CarrierEdiCostTypesDao;
import com.pls.shipment.domain.CarrierEdiCostTypesEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation for {@link CarrierEdiCostTypesDao}.
 *
 * @author Alexander Kirichenko
 */
@Repository
@Transactional
public class CarrierEdiCostTypesDaoImpl extends AbstractDaoImpl<CarrierEdiCostTypesEntity, Long> implements CarrierEdiCostTypesDao {
    @Override
    public String getAccessorialRefTypeByCarrierEdiCostType(Long carrierId, String carrierEdiCostType) {
        String accRefType = (String) getCurrentSession().getNamedQuery(CarrierEdiCostTypesEntity.Q_ACC_TYPE_BY_CARRIER_EDI_COST_TYPE).
                setParameter("carrierId", carrierId).setParameter("carrierCostRefType", carrierEdiCostType).uniqueResult();
        return StringUtils.isNotBlank(accRefType) ? accRefType : CarrierEdiCostTypesEntity.DEFAULT_ACC_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getRefTypeMapForCarrier(Long carrierId) {
        HashMap<String, String> map = new HashMap<String, String>();
        List<Object[]> rows = getCurrentSession().getNamedQuery(CarrierEdiCostTypesEntity.Q_MAP_FOR_CARRIER).
                setParameter("carrierId", carrierId).list();
        for (Object[] row : rows) {
            map.put((String) row[0], (String) row[1]);
        }
        return map;
    }
}
