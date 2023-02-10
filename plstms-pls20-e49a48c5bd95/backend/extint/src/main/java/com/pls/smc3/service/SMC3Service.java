package com.pls.smc3.service;

import com.pls.extint.shared.AvailableCarrierRequestVO;
import com.pls.extint.shared.AvailableCarrierVO;
import com.pls.extint.shared.DataModuleVO;

import java.util.List;

/**
 * SMC3Service to get the available tariffs.
 * 
 * @author PAVANI CHALLA
 * 
 */
public interface SMC3Service {

    /**
     * Available tariffs.
     * 
     * @return List<DataModuleVO> List of Tariff object.
     * @throws Exception
     *             DataModuleException from the webservice
     */
    List<DataModuleVO> getAvailableTariffs() throws Exception;

    /**
     * Method provides list of Available carriers(name, scac).
     * 
     * @return List of Available Carriers.
     * @throws Exception
     *             Exception thrown by web service
     */
    List<AvailableCarrierVO> getAvailableCarriers(AvailableCarrierRequestVO availableCarrierRequestVO) throws Exception;

}
