package com.pls.ltlrating.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CarrierDao;
import com.pls.core.dao.CountryDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.AddressVO;
import com.pls.extint.shared.MileageCalculatorType;
import com.pls.ltlrating.dao.LtlPricingTerminalInfoDao;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;
import com.pls.ltlrating.service.TerminalService;
import com.pls.ltlrating.shared.GetTerminalsCO;
import com.pls.ltlrating.shared.TerminalContactVO;
import com.pls.ltlrating.shared.TerminalsVO;
import com.pls.mileage.service.MileageService;
import com.pls.smc3.dto.CarrierTerminalDetailDTO;
import com.pls.smc3.dto.TerminalDetailsDTO;
import com.pls.smc3.dto.TerminalResponseDetailDTO;
import com.pls.smc3.service.TerminalDetailsClient;

/**
 * Implementation of {@link TerminalService}.
 *
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional(readOnly = true)
public class TerminalServiceImpl implements TerminalService {
    private static final String LTL_TYPE = "LTL";

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalServiceImpl.class);

    @Autowired
    private TerminalDetailsClient terminalDetails;

    @Autowired
    private LtlPricingTerminalInfoDao terminalInfoDao;

    @Autowired
    private MileageService mileageService;

    @Autowired
    private CountryDao countryDao;
    
    @Autowired
    private CarrierDao carrierDao;

    @Override
    public TerminalsVO getTerminalInformation(GetTerminalsCO criteria) throws ApplicationException {
        TerminalsVO terminalsVO = getTerminalsFromCarrierConnect(criteria);

        //If terminal addresses are not returned by Carrier Connect, use the one that is set up in the Profile.
        if (terminalsVO == null && criteria.getProfileDetailId() != null) {
            terminalsVO = populateTerminalsInformationFromProfile(criteria);
        }

        if (terminalsVO == null) {
            terminalsVO = new TerminalsVO();
        }

        if (terminalsVO.getOriginTerminal() != null && terminalsVO.getDestinationTerminal() != null) {
            populateMileageBetweenTerminals(criteria, terminalsVO);
        }

        return terminalsVO;
    }

    private TerminalsVO getTerminalsFromCarrierConnect(GetTerminalsCO criteria) {
        TerminalsVO terminalsVO = null;
        List<TerminalResponseDetailDTO> terminals = terminalDetails.getTerminalDetailsByPostalCode(getTerminalsCriteria(criteria), criteria.getShipmentDate());
        if (terminals != null && !terminals.isEmpty()) {
            TerminalResponseDetailDTO terminal = terminals.get(0);
            if (terminal.getDetails() != null && !terminal.getDetails().isEmpty()) {
                terminalsVO = new TerminalsVO();
                setTerminalInfo(terminalsVO, terminal.getDetails().get(0), criteria.getOriginAddress(), true);
                if (terminals.size() > 1 && terminals.get(1).getDetails() !=null && !terminals.get(1).getDetails().isEmpty()) {
                    setTerminalInfo(terminalsVO, terminals.get(1).getDetails().get(0), criteria.getDestinationAddress(), false);
                } else {
                    terminalsVO.setDestinationTerminal(terminalsVO.getOriginTerminal());
                }
            }
        }
        return terminalsVO;
    }

    private TerminalsVO populateTerminalsInformationFromProfile(GetTerminalsCO criteria) {
        LtlPricingTerminalInfoEntity terminalEntity = terminalInfoDao.findActiveByProfileDetailId(criteria.getProfileDetailId());
        TerminalsVO terminalsVO = new TerminalsVO();
        if (terminalEntity != null && Boolean.TRUE == terminalEntity.getVisible()) {
            AddressVO addressVO = convertAddressEntityToVO(terminalEntity.getAddress());
            terminalsVO.setOriginTerminal(addressVO);
            terminalsVO.setDestinationTerminal(addressVO);
        }
        return terminalsVO;
    }

    private List<TerminalDetailsDTO> getTerminalsCriteria(GetTerminalsCO criteria) {
        List<TerminalDetailsDTO> terminalsCriteriaList = new ArrayList<TerminalDetailsDTO>();
        TerminalDetailsDTO terminalsCriteria = new TerminalDetailsDTO();
        
        CarrierEntity carrier = carrierDao.findByScacIncludingActual(criteria.getScac());
        terminalsCriteria.setScac(carrier.getActualScac());
        
        terminalsCriteria.setMethod(LTL_TYPE);
        List<AddressVO> shipmentAddresses = new ArrayList<AddressVO>();
        shipmentAddresses.add(refineAddressCriteria(criteria.getOriginAddress()));
        shipmentAddresses.add(refineAddressCriteria(criteria.getDestinationAddress()));
        terminalsCriteria.setShipmentAddresses(shipmentAddresses);
        terminalsCriteriaList.add(terminalsCriteria);
        return terminalsCriteriaList;
    }

    private void populateMileageBetweenTerminals(GetTerminalsCO criteria, TerminalsVO terminalsVO) {
        //Now lets get the Mileage between multiple points.
        //Mileage between Actual Origin and Origin terminal
        terminalsVO.setMileageToOrigTerminal(calculateMileage(criteria.getOriginAddress(), terminalsVO.getOriginTerminal()));

        //Mileage between Origin terminal and Destination terminal
        terminalsVO.setMileageBtwOrigTermDestTerm(calculateMileage(terminalsVO.getOriginTerminal(), terminalsVO.getDestinationTerminal()));

        //Mileage between Destination terminal and actual Destination
        terminalsVO.setMileageFromDestTerminal(calculateMileage(terminalsVO.getDestinationTerminal(), criteria.getDestinationAddress()));
    }

    private int calculateMileage(AddressVO origAddress, AddressVO destAddress) {
       return mileageService.getMileage(origAddress, destAddress, MileageCalculatorType.PC_MILER);
    }

    private AddressVO convertAddressEntityToVO(AddressEntity entity) {
        AddressVO address = new AddressVO();
        address.setAddress1(entity.getAddress1());
        address.setAddress2(entity.getAddress2());
        address.setCity(entity.getCity());
        address.setPostalCode(entity.getZip());
        address.setStateCode(entity.getStateCode());
        address.setCountryCode(entity.getCountry().getName());

        return address;
    }

    private AddressVO refineAddressCriteria(AddressVO address) {
        if ("CAN".equalsIgnoreCase(address.getCountryCode())) {
            address.setPostalCode(address.getPostalCode().replaceAll("\\s+", ""));
        }
        return address;
    }

    private void setTerminalInfo(TerminalsVO terminalsVO, CarrierTerminalDetailDTO terminalDetailDTO, AddressVO critAddress, boolean isOrigin) {
        TerminalContactVO terminalContact = new TerminalContactVO();
        AddressVO terminalAddress = new AddressVO();

        terminalContact.setContact(terminalDetailDTO.getContact());
        terminalContact.setContactEmail(terminalDetailDTO.getContactEmail());
        terminalContact.setName(terminalDetailDTO.getName());
        terminalContact.setPhone(addPhone(terminalDetailDTO.getPhone(), critAddress.getCountryCode()));

        terminalAddress.setAddress1(terminalDetailDTO.getAddress1());
        terminalAddress.setAddress2(terminalDetailDTO.getAddress2());
        terminalAddress.setCity(terminalDetailDTO.getCity());
        terminalAddress.setPostalCode(terminalDetailDTO.getPostalcode());
        terminalAddress.setStateCode(terminalDetailDTO.getStateProvince());
        terminalAddress.setCountryCode(critAddress.getCountryCode());

        if (isOrigin) {
            terminalsVO.setOriginTerminal(terminalAddress);
            terminalsVO.setOriginTerminalContact(terminalContact);
        } else {
            terminalsVO.setDestinationTerminal(terminalAddress);
            terminalsVO.setDestTerminalContact(terminalContact);
        }

    }

    private PhoneBO addPhone(String phoneString, String countryCode) {
        PhoneBO phoneBO = new PhoneBO();
        CountryEntity countryEntity = countryDao.find(countryCode);

        phoneBO.setCountryCode(countryEntity.getPhoneCode());

        if (StringUtils.isNotBlank(phoneString)) {
            List<String> phoneComponents = Arrays.asList(StringUtils.split(phoneString, "-"));
            int position = 0;
            for (String component : phoneComponents) {
                switch (position++) {
                case 0:
                    phoneBO.setAreaCode(component);
                    break;
                case 1:
                    phoneBO.setNumber(component);
                    break;
                case 2:
                default:
                    phoneBO.setNumber(phoneBO.getNumber() + component);
                    break;
                }
            }
        }
        return phoneBO;
    }
    
    public AddressVO buildAddressVO(AddressEntity address) {
        if (address == null) {
            return null;
        }

        AddressVO addressVO = new AddressVO();
        addressVO.setAddress1(address.getAddress1());
        addressVO.setAddress2(address.getAddress2());
        addressVO.setCity(address.getCity());
        addressVO.setStateCode(address.getStateCode());
        addressVO.setPostalCode(address.getZip());
        addressVO.setCountryCode(address.getState().getStatePK().getCountryCode());
        return addressVO;
    }
}
