package com.pls.shipment.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.shipment.dao.ShipmentAlertDao;
import com.pls.shipment.domain.ShipmentAlertEntity;
import com.pls.shipment.domain.bo.CityStateZip;
import com.pls.shipment.domain.bo.ShipmentAlertType;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardAlertListItemBO;
import com.pls.shipment.domain.enums.ShipmentAlertsStatus;

/**
 * {@link ShipmentAlertDao} implementation.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Transactional
@Repository
public class ShipmentAlertDaoImpl extends AbstractDaoImpl<ShipmentAlertEntity, Long> implements ShipmentAlertDao {
    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentTrackingBoardAlertListItemBO> getAlertsForUser(Long userId, ShipmentAlertsStatus... statuses) {
        List<Object[]> rows = getCurrentSession().getNamedQuery(ShipmentAlertEntity.Q_FOR_USER_BY_STATUSES).setParameter("personId", userId)
                .setParameterList("loadStatuses",
                        Arrays.asList(ShipmentStatus.DELIVERED, ShipmentStatus.CANCELLED, ShipmentStatus.OPEN))
                .setParameterList("statuses", statuses).list();

        Map<Long, ShipmentTrackingBoardAlertListItemBO> alertsMap = new HashMap<Long, ShipmentTrackingBoardAlertListItemBO>();
        for (Object[] row : rows) {
            ShipmentTrackingBoardAlertListItemBO alertListItemBO;
            if (alertsMap.containsKey(row[0])) {
                alertListItemBO = alertsMap.get(row[0]);
            } else {
                alertListItemBO = new ShipmentTrackingBoardAlertListItemBO();
                alertListItemBO.setId((Long) row[0]);
                alertListItemBO.setBol((String) row[1]);
                alertListItemBO.setPro((String) row[29]);
                alertListItemBO.setCustomerName((String) row[2]);
                alertListItemBO.setCarrierName((String) row[3]);
                alertListItemBO.setPickupDate((Date) row[4]);
                alertListItemBO.setPickupWindowStart((Date) row[5]);
                alertListItemBO.setPickupWindowEnd((Date) row[6]);
                alertListItemBO.setOriginTimezone((String) row[7]);
                alertListItemBO.setShipper((String) row[10]);
                alertListItemBO.setConsignee((String) row[11]);
                alertListItemBO.setEstimatedDelivery((Date) row[12]);
                alertListItemBO.setCreatedDateTime((Date) row[13]);
                alertListItemBO.setCreatedBy((String) row[14]);

                CityStateZip origin = new CityStateZip();
                origin.setCity((String) row[15]);
                origin.setState((String) row[16]);
                origin.setZip((String) row[17]);
                CityStateZip destination = new CityStateZip();
                destination.setCity((String) row[18]);
                destination.setState((String) row[19]);
                destination.setZip((String) row[20]);
                alertListItemBO.setOriginAddress(origin);
                alertListItemBO.setDestinationAddress(destination);
                alertListItemBO.setIntegrationType((CarrierIntegrationType) row[21]);
                alertListItemBO.setIndicators((String) row[22]);
                alertListItemBO.setDispatchedDate((Date) row[23]);
                alertListItemBO.setNetwork((String) row[24]);
                alertListItemBO.setAccountExecutive(((String) row[25]));
                alertListItemBO.setPieces((Integer) row[26]);
                alertListItemBO.setWeight((Integer) row[27]);
                alertListItemBO.setShipmentStatus((ShipmentStatus) row[28]);
                alertsMap.put((Long) row[0], alertListItemBO);
            }
            alertListItemBO.appendAlertType(((ShipmentAlertType) row[8]).getType());
            alertListItemBO.setNewAlert(alertListItemBO.isNewAlert() || row[9] == ShipmentAlertsStatus.ACTIVE);
        }
        return new ArrayList<ShipmentTrackingBoardAlertListItemBO>(alertsMap.values());
    }

    @Override
    public Long getAlertsCount(Long userId, ShipmentAlertsStatus status) {
        return (Long) getCurrentSession().getNamedQuery(ShipmentAlertEntity.Q_COUNT_FOR_USER_BY_STATUSES).setParameter("personId", userId)
                .setParameterList("loadStatuses", Arrays.asList(ShipmentStatus.DELIVERED, ShipmentStatus.CANCELLED)).uniqueResult();
    }

    @Override
    public List<ShipmentAlertEntity> findAlertsByShipment(List<Long> shipmentIds) {
        if (shipmentIds.isEmpty()) {
            return Collections.emptyList();
        }
        int page = 0;
        Map<String, Object> parameters = new HashMap<String, Object>();
        List<ShipmentAlertEntity> result = new ArrayList<ShipmentAlertEntity>(shipmentIds.size());
        do {
            int toIndex = (page + 1) * 1000;
            parameters.put("shipmentIds", shipmentIds.subList(page * 1000, toIndex > shipmentIds.size() ? shipmentIds.size() : toIndex));
            result.addAll(findByNamedQuery(ShipmentAlertEntity.Q_BY_SHIPMENT_IDS, parameters));
        } while (++page * 1000 < shipmentIds.size());
        return result;
    }

    @Override
    public void acknowledgeAlerts(long shipmentId, long acknowledgedUserId) {
        getCurrentSession().createQuery("update ShipmentAlertEntity set status=:status, acknowledgedUser.id=:acknowledgedUserId where load.id=:id"
                + " and acknowledgedUser.id is null").setParameter("status", ShipmentAlertsStatus.ACKNOWLEDGED).setParameter("acknowledgedUserId",
                acknowledgedUserId).setParameter("id", shipmentId).executeUpdate();
    }

    @Override
    public List<ShipmentAlertEntity> getByStatus(ShipmentAlertsStatus... statuses) {
        if (statuses == null || statuses.length <= 0) {
            return new ArrayList<ShipmentAlertEntity>();
        }

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("statuses", statuses);
        return findByNamedQuery(ShipmentAlertEntity.Q_BY_STATUSES, parameters);
    }

    @Override
    public void updateStatus(Collection<Long> alertsIds, ShipmentAlertsStatus status) {
        if (alertsIds.isEmpty()) {
            return;
        }
        getCurrentSession().createQuery("update ShipmentAlertEntity set status=:status where id in (:ids)").setParameter("status",
                status).setParameterList("ids", alertsIds).executeUpdate();
    }

    @Override
    public void removeAlerts(Long shipmentId, ShipmentAlertType... types) {
        getCurrentSession().getNamedQuery(ShipmentAlertEntity.Q_DELETE)
                .setParameter("loadId", shipmentId)
                .setParameterList("types", types == null || types.length == 0 ? ShipmentAlertType.values() : types)
                .executeUpdate();
    }

    @Override
    public void generateNewTimeAlerts() {
        getCurrentSession().getNamedQuery(ShipmentAlertEntity.C_GENERATE_TIME_ALERTS).executeUpdate();
    }

    @Override
    public void removeOutdatedTimeAlerts() {
        getCurrentSession().getNamedQuery(ShipmentAlertEntity.D_REMOVE_TIME_ALERTS).executeUpdate();
    }

    @Override
    public ShipmentAlertEntity getShipmentAlert(Long shipmentId, ShipmentAlertType type) {
        return (ShipmentAlertEntity) getCurrentSession().getNamedQuery(ShipmentAlertEntity.Q_BY_SHIPMENT_AND_TYPE)
                .setLong("shipmentId", shipmentId)
                .setParameter("type", type)
                .setMaxResults(1)
                .uniqueResult();
    }
}
