package com.pls.shipment.service.impl;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.exception.ApplicationException;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.sterling.LoadUpdateJaxbBO;
import com.pls.shipment.service.ShipmentSavingService;
import com.pls.shipment.service.edi.IntegrationService;

@Service("loadUpdateIntegrationService")
@Transactional(readOnly = true)
public class LoadUpdateIntegrationServiceImpl implements IntegrationService<LoadUpdateJaxbBO> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadUpdateIntegrationServiceImpl.class);

    @Autowired
    private ShipmentSavingService shipmentSavingService;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void processMessage(LoadUpdateJaxbBO loadUpdateBO) throws ApplicationException {

        try {
        	if (loadUpdateBO.getShipmentNo() == null || loadUpdateBO.getShipmentNo().isEmpty() || loadUpdateBO.getPoNumber() == null) {
          	  LOGGER.warn(
                      "Shipment update failed. - Invalid Id/PO Value Combination: Shipment ref [{}], PO Number [{}]",
                      loadUpdateBO.getShipmentNo(), loadUpdateBO.getPoNumber());
        		return;
        	}
	
        	LoadEntity load = null;
        	
        	if (NumberUtils.isNumber(loadUpdateBO.getShipmentNo())) {
        	  load = ltlShipmentDao.find(Long.parseLong(loadUpdateBO.getShipmentNo()));
        	}
        	
        	if (load != null) {
              load.getNumbers().setPoNumber(loadUpdateBO.getPoNumber());
              shipmentSavingService.save(load);
            } else {
            	if (loadUpdateBO.getCustomerOrgId() == null) {
            		LOGGER.warn(
                            "Shipment update failed. Unable to find unique Shipment by provided identifiers. Customer Org Id [{}], Shipment ref [{}]",
                            loadUpdateBO.getCustomerOrgId(), loadUpdateBO.getShipmentNo());
            		return;
            	}
            	List<LoadEntity> loads = ltlShipmentDao.findShipmentsByShipmentNumber(loadUpdateBO.getCustomerOrgId(), loadUpdateBO.getShipmentNo());
            	if (loads != null && loads.size() == 1) {
            		LoadEntity oneLoad = loads.get(0);
            		oneLoad.getNumbers().setPoNumber(loadUpdateBO.getPoNumber());
            		shipmentSavingService.save(oneLoad);
            		return;
            	} else {
            	  LOGGER.warn(
                        "Shipment update failed. Unable to find unique Shipment by provided identifiers. Customer Org Id [{}], Shipment ref [{}]",
                        loadUpdateBO.getCustomerOrgId(), loadUpdateBO.getShipmentNo());
                  // we will log and ignore these updates
            	}
            }
        } catch (Exception e) {
            LOGGER.warn("Shipment update failed with exception. Customer Org Id [{}], Shiment ref [{}]. [{}]",
                    loadUpdateBO.getCustomerOrgId(), loadUpdateBO.getShipmentNo(), e.getMessage());
            throw e;
        }
    }

}
