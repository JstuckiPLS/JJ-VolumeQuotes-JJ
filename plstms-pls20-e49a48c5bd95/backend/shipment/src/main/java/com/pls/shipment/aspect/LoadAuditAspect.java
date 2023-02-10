package com.pls.shipment.aspect;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.service.audit.LoadEventBuilder;
import com.pls.shipment.service.audit.LoadTrackingFields;

/**
 * Aspect for tracking load events.
 * 
 * @author Artem Arapov
 *
 */
@Aspect
public class LoadAuditAspect {

    @Autowired
    private LtlShipmentDao dao;

    @Autowired
    private ShipmentEventDao eventDao;

    /**
     * Logs updating of {@link LoadEntity} status.
     * 
     * @param pjp - method execution.
     * @param shipmentId - Id of shipment.
     * @param status - Status of shipment
     * @return value that pjp is returning.
     * @throws Throwable that pjp is throwing.
     */
    @Around(value = "execution(* com.pls.shipment.dao.impl.LtlShipmentDaoImpl.updateStatus(..)) && args(shipmentId,status)",
            argNames = "shipmentId,status")
    public Object logLoadStatusEvent(ProceedingJoinPoint pjp, Long shipmentId, ShipmentStatus status) throws Throwable {
        ShipmentStatus oldStatus = dao.getShipmentStatus(shipmentId);

        Object result = pjp.proceed();

        List<LoadEventEntity> listOfevents =
                LoadEventBuilder.buildStatusEvent(shipmentId, LoadTrackingFields.STATUS.getDescription(), oldStatus, status);
        for (LoadEventEntity eventEntity : listOfevents) {
            eventDao.saveOrUpdate(eventEntity);
        }

        return result;
    }

}
