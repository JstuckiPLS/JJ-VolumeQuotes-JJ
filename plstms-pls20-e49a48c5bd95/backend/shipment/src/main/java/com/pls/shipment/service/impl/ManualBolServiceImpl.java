/**
 * 
 */
package com.pls.shipment.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.FreightBillPayToDao;
import com.pls.core.dao.impl.FreightBillPayToDaoImpl;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.service.validation.support.Validator;
import com.pls.shipment.dao.ManualBolDao;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.bo.ManualBolListItemBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.service.ManualBolService;
import com.pls.shipment.service.impl.validation.ManualBolValidator;

/**
 * Implementation of {@link ManualBolService}.
 * 
 * @author Alexander Nalapko
 *
 */
@Service
@Transactional
public class ManualBolServiceImpl implements ManualBolService {

    @Autowired
    private ManualBolDao dao;

    @Autowired
    private FreightBillPayToDao freightBillPayToDao;

    @Resource(type = ManualBolValidator.class)
    private Validator<ManualBolEntity> manualBolValidator;

    @Override
    public ManualBolEntity saveOrUpdate(ManualBolEntity entity) throws ValidationException {
        updateFreightBillPayTo(entity);

        manualBolValidator.validate(entity);

        return updateLoadWithBolNumber(entity);
    }

    @Override
    public ManualBolEntity find(Long id) {
        return dao.find(id);
    }

    @Override
    public List<ManualBolListItemBO> findAll(RegularSearchQueryBO search, Long userId) {
        return dao.findAll(search, userId);
    }

    @Override
    public List<ShipmentListItemBO> findAllShipment(RegularSearchQueryBO search, Long userId) {
        return ManualBolToShipment.toShipmetList(dao.findAll(search, userId));
    }

    @Override
    public boolean cancel(Long id) throws EntityNotFoundException {
        return dao.cancel(id);
    }

    private void updateFreightBillPayTo(ManualBolEntity manualBol) {
        if (manualBol.getFreightBillPayTo() != null
                && !FreightBillPayToDaoImpl.DEFAULT_FREIGHT_BILL_PAY_TO_ID.equals(manualBol.getFreightBillPayTo().getId())) {
            freightBillPayToDao.saveOrUpdate(manualBol.getFreightBillPayTo());
        }
    }

    private ManualBolEntity updateLoadWithBolNumber(ManualBolEntity manualBol) {
        ManualBolEntity savedEntity = dao.saveOrUpdate(manualBol);
        if (StringUtils.isBlank(savedEntity.getNumbers().getBolNumber())) {
            savedEntity.getNumbers().setBolNumber(String.valueOf(savedEntity.getId()));
            dao.saveOrUpdate(savedEntity);
            return savedEntity;
        }

        return savedEntity;
    }

    private static class ManualBolToShipment {
        public static List<ShipmentListItemBO> toShipmetList(List<ManualBolListItemBO> manualBolList) {
            return manualBolList.stream().map((s) -> toShipmet(s)).collect(Collectors.toList());
        }

        private static ShipmentListItemBO toShipmet(ManualBolListItemBO bol) {
            ShipmentListItemBO shipment = new ShipmentListItemBO();
            shipment.setShipmentId(bol.getId());
            shipment.setBolNumber(bol.getBolNumber());
            shipment.setSoNumber(bol.getSoNumber());
            shipment.setGlNumber(bol.getGlNumber());
            shipment.setProNumber(bol.getProNumber());
            shipment.setRefNumber(bol.getRefNumber());
            shipment.setPoNumber(bol.getPoNumber());
            shipment.setPuNumber(bol.getPuNumber());
            shipment.setShipper(bol.getShipper());
            shipment.setConsignee(bol.getConsignee());
            shipment.setCustomerId(bol.getCustomerId());
            shipment.setCarrier(bol.getCarrier());
            shipment.setStatus(bol.getStatus());
            shipment.setPickupDate(bol.getPickupDate());
            shipment.setDestination(bol.getDestination());
            shipment.setOrigin(bol.getOrigin());
            shipment.setCarrierId(bol.getCarrierId());
            shipment.setJobNumber(bol.getJobNumber());
            shipment.setScac(bol.getScac());
            shipment.setBillingStatus("NONE");
            shipment.setManualBol(true);
            return shipment;
        }
    }
}
