package com.pls.shipment.dao.impl;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.bo.LoadAuditBO;
import com.pls.shipment.domain.bo.LoadTrackingBO;

/**
 * Tests for {@link com.pls.shipment.dao.impl.LoadTrackingDaoImpl}.
 *
 * @author Mikhail Boldinov, 26/05/14
 */
public class LoadTrackingDaoImplIT extends AbstractDaoTest {

    private static final Long SOURCE = 214L;
    private static final Long LOAD_ID = 21L;
    private static final Date TRACKING_DATE = new Date();
    private static final String TIMEZONE = "EET";
    private static final String STATUS_CODE = "CD";
    private static final String STATUS_REASON = "NS";
    private static final Long CREATED_BY = 1L;
    private static final String POSTAL_CODE = "11111";

    @Autowired
    private LoadTrackingDao sut;

    @Autowired
    private LtlShipmentDao shipmentDao;

    @Test
    public void shouldFindShipmentTracking() {
        List<LoadTrackingBO> loadTrackingList = sut.findShipmentTracking(LOAD_ID);
        Assert.assertNotNull(loadTrackingList);
        Assert.assertFalse(loadTrackingList.isEmpty());
        for (LoadTrackingBO loadTrackingEntity : loadTrackingList) {
            Assert.assertNotNull(loadTrackingEntity);
            Assert.assertEquals(loadTrackingEntity.getShipmentId(), LOAD_ID);
            Assert.assertNotNull(loadTrackingEntity.getStatusDescription());
        }
    }

    @Test
    public void shouldSaveShipmentTracking() {
        LoadTrackingEntity loadTrackingEntity = getLoadTracking();
        LoadTrackingEntity savedEntity = sut.saveOrUpdate(loadTrackingEntity);
        flushAndClearSession();

        Assert.assertNotNull(savedEntity);
        Assert.assertEquals(TRACKING_DATE, savedEntity.getTrackingDate());
        Assert.assertEquals(TIMEZONE, savedEntity.getTimezoneCode());
        Assert.assertEquals(STATUS_CODE, savedEntity.getStatusCode());
        Assert.assertNotNull(savedEntity.getStatus());
        Assert.assertEquals(STATUS_REASON, savedEntity.getStatusReasonCode());
        Assert.assertNotNull(savedEntity.getLoad());
        Assert.assertEquals(LOAD_ID, savedEntity.getLoad().getId());
        Assert.assertEquals(CREATED_BY, savedEntity.getCreatedBy());
        Assert.assertEquals(POSTAL_CODE, savedEntity.getPostalCode());
    }

    @Test
    public void shouldGetShipmentAudit() {
        List<LoadAuditBO> result = sut.findShipmentAudit(603L);
        Assert.assertNotNull(result);
    }

    private LoadTrackingEntity getLoadTracking() {
        LoadTrackingEntity loadTracking = new LoadTrackingEntity();
        loadTracking.setSource(SOURCE);
        loadTracking.setTrackingDate(TRACKING_DATE);
        loadTracking.setTimezoneCode(TIMEZONE);
        loadTracking.setStatusCode(STATUS_CODE);
        loadTracking.setStatusReasonCode(STATUS_REASON);
        loadTracking.setLoad(shipmentDao.find(LOAD_ID));
        loadTracking.setCreatedBy(CREATED_BY);
        loadTracking.setPostalCode(POSTAL_CODE);
        return loadTracking;
    }
}
