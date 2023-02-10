/**
 * 
 */
package com.pls.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.bo.CarrierInfoBO;
import com.pls.core.domain.bo.SimpleCarrierInfoBO;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.service.CarrierService;

/**
 * Service for Carrier.
 * 
 * @author Alexander Nalapko
 */
@Service
@Transactional
public class CarrierServiceImpl implements CarrierService {

    @Autowired
    private CarrierDao dao;

    @Override
    public CarrierEntity findCarrierById(Long id) {
        return dao.find(id);
    }

    @Override
    public List<CarrierInfoBO> findCarrierByName(String name, int limit, int offset) {
        return  dao.findCarrierByName(name, limit, offset);
    }

    @Override
    public CarrierEntity findByScac(String scac) {
        return dao.findByScac(scac);
    }

    @Override
    public CarrierEntity findByMcNumber(String mcNumber) {
        return dao.findByMcNumber(mcNumber);
    }

    @Override
    public CarrierEntity findByScacAndMC(String scac, String mcNumber) {
        return dao.findByScacAndMC(scac, mcNumber);
    }

    @Override
    public List<SimpleCarrierInfoBO> getCarrierInfos(String carrier, String scacCode, String status) {
        return dao.getCarrierInfos(carrier, scacCode, status);
    }

    @Override
    public CarrierInfoBO getDefaultCarrier() {
        return dao.getDefaultCarrier();
    }

    @Override
    public void save(CarrierEntity entity) {
        dao.saveOrUpdate(entity);
    }
}
