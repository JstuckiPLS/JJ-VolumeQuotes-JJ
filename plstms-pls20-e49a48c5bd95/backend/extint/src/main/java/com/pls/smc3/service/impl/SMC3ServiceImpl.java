package com.pls.smc3.service.impl;

import com.pls.extint.shared.AvailableCarrierRequestVO;
import com.pls.extint.shared.AvailableCarrierVO;
import com.pls.extint.shared.DataModuleVO;
import com.pls.smc3.service.SMC3Service;
import com.smc.carrierconnect.webservice.enums.Method;
import com.smc.carrierconnect.webservice.objects.enums.ServiceTypes;
import com.smc.carrierconnectxl.webservice.enums.ApiVersion;
import com.smc.carrierconnectxl.webservice.pojos.CarrierService;
import com.smc.carrierconnectxl.webservice.v3.CarrierConnectXLPortTypeV3;
import com.smc.carrierconnectxl.webservice.v3.CarrierConnectXLV3;
import com.smc.carrierconnectxl.webservice.v3.CarrierServices;
import com.smc.carrierconnectxl.webservice.v3.CarrierServicesResponse;
import com.smc.carrierconnectxl.webservice.v3.carrierservices.CarrierScacService;
import com.smc.carrierconnectxl.webservice.v3.carrierservices.CarrierServicesRequest;
import com.smc.products.datamodule.ArrayOfDataModule;
import com.smc.products.datamodule.DataModule;
import com.smc.webservices.AuthenticationToken;
import com.smc.webservices.RateWareXL;
import com.smc.webservices.RateWareXLPortType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class of {@link SMC3Service}.
 * 
 * @author Pavani Challa
 * 
 */
@Service
public class SMC3ServiceImpl extends BaseSMC3ServiceImpl implements SMC3Service {

    @Override
    public List<DataModuleVO> getAvailableTariffs() throws Exception {

        RateWareXL service = new RateWareXL();
        RateWareXLPortType port = service.getRateWareXLHttpPort();

        AuthenticationToken authentication = getRatewareAuthToken();
        ArrayOfDataModule list = port.availableTariffs(authentication);

        return transformTariffResponseToDTO(list);

    }

    /**
     * Helper method to convert the web service response to DTO object.
     * 
     * @param response
     *            List of DataModule.
     * @return List of {@link DataModuleVO}.
     */
    private List<DataModuleVO> transformTariffResponseToDTO(ArrayOfDataModule response) {
        List<DataModuleVO> tariffList = new ArrayList<DataModuleVO>();

        for (DataModule dataModule : response.getDataModule()) {
            DataModuleVO module = new DataModuleVO();
            module.setDescription(dataModule.getDescription() + "_" + dataModule.getEffectiveDate());
            module.setEffectiveDate(dataModule.getEffectiveDate());
            module.setProductNumber(dataModule.getProductNumber());
            module.setRelease(dataModule.getRelease());
            module.setTariffName(dataModule.getTariffName() + "_" + dataModule.getEffectiveDate() + "_"
                    + dataModule.getProductNumber());

            tariffList.add(module);
        }

        return tariffList;
    }

    @Override
    public List<AvailableCarrierVO> getAvailableCarriers(AvailableCarrierRequestVO availableCarrierRequestVO) throws Exception {

        CarrierConnectXLV3 service = new CarrierConnectXLV3();
        CarrierConnectXLPortTypeV3 port = service.getCarrierConnectXLHttpPortV3();

        AuthenticationToken authentication = getCarrierConnectAuthToken();
        CarrierServices parameters = new CarrierServices();
        CarrierServicesRequest request = new CarrierServicesRequest();
        CarrierService carrierService = new CarrierService();
        carrierService.setSCAC(availableCarrierRequestVO.getScac());
        carrierService.setServiceMethod("LTL".equalsIgnoreCase(availableCarrierRequestVO.getServiceMethod())? Method.LTL:Method.TL);
        //TODO what will be the default service type?
        carrierService.setServiceType(ServiceTypes.STANDARD);
        request.setService(carrierService);
        parameters.setCarrierServicesRequest(request);

        CarrierServicesResponse carriers = port.carrierServices(parameters, authentication, ApiVersion.V_3_1);

        return transformCarrierResponseToDTO(carriers);
    }

    private List<AvailableCarrierVO> transformCarrierResponseToDTO(CarrierServicesResponse response) {
        List<AvailableCarrierVO> carriers = new ArrayList<AvailableCarrierVO>();
        AvailableCarrierVO carrierVO = null;
        for (CarrierScacService carrier : response.getCarrierServicesResponse().getCarrierServices()) {
            carrierVO = new AvailableCarrierVO();
            carrierVO.setName(carrier.getCarrierName());
            carrierVO.setScac(carrier.getSCAC());

            carriers.add(carrierVO);
        }

        return carriers;
    }
}
