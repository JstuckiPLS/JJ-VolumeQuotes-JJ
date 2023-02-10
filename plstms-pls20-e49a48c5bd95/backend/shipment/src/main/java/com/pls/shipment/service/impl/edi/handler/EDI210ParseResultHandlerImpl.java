package com.pls.shipment.service.impl.edi.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pls.core.dao.CountryDao;
import com.pls.core.exception.ApplicationException;
import com.pls.shipment.dao.CarrierEdiCostTypesDao;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.edi.EDIData;
import com.pls.shipment.domain.edi.EDIFunctionalGroup;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.domain.edi.EDITransaction;
import com.pls.shipment.service.edi.EDIService;
import com.pls.shipment.service.edi.handler.EDI210ParseResultHandler;
import com.pls.shipment.service.edi.handler.EDIParseResultHandler;
import com.pls.shipment.service.edi.match.VendorBillEdiSaver;
import com.pls.shipment.service.impl.edi.parser.EDI997Producer;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * {@link EDIParseResultHandler} implementation for EDI 210.
 *
 * @author Mikhail Boldinov, 05/03/14
 */
@Service
public class EDI210ParseResultHandlerImpl implements EDI210ParseResultHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EDI210ParseResultHandlerImpl.class);

    private static final String TIMEZONE_CODE = "ET";
    private static final String USA = "USA";
    private static final String CAN = "CAN";

    @Value("${admin.personId}")
    private Long ediUserId;

    @Autowired
    private CarrierEdiCostTypesDao costTypesDao;

    @Autowired
    private EDI997Producer<CarrierInvoiceDetailsEntity> edi997Producer;

    @Autowired
    private EDIService ediService;

    @Autowired
    private EDIEmailSender ediEmailSender;

    @Autowired
    private LoadTrackingDao loadTrackingDao;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private VendorBillEdiSaver vendorBillSaver;

    @Override
    public List<Integer> handle(EDIParseResult<CarrierInvoiceDetailsEntity> parseResult) throws Exception {
        List<Integer> unprocessedEntitiesInd = new ArrayList<Integer>();
        List<LoadEntity> matchedLoads = new ArrayList<LoadEntity>();
        List<Integer> rejectTransactionList = new ArrayList<Integer>();
        if (parseResult.getStatus() == EDIParseResult.Status.SUCCESS) {
            Map<String, String> carrierRefTypeMap = costTypesDao.getRefTypeMapForCarrier(parseResult
                    .getParsedEntities().iterator().next().getCarrier().getId());
            int transactionNum = 0;
            for (CarrierInvoiceDetailsEntity carrierInvoiceDetails : parseResult.getParsedEntities()) {
                try {
                    if (vendorBillSaver.isReject(carrierInvoiceDetails.getEdiAccount(), carrierInvoiceDetails
                            .getCarrier().getScac())) {
                        rejectTransactionList.add(transactionNum);
                        vendorBillSaver.saveArchiveEdiVendorBill(carrierInvoiceDetails);
                    } else {
                        saveVendorBill(carrierRefTypeMap, matchedLoads, carrierInvoiceDetails);
                    }
                    transactionNum++;
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    unprocessedEntitiesInd.add(parseResult.getParsedEntities().indexOf(carrierInvoiceDetails));
                }
            }
            if (!unprocessedEntitiesInd.isEmpty()) {
                ediEmailSender.vendorBillFailed(parseResult.getEdiFile().getName());
                if (unprocessedEntitiesInd.size() == parseResult.getParsedEntities().size()) {
                    throw new ApplicationException("EDI 210 parsing failed for file "
                            + parseResult.getEdiFile().getName());
                }
            }
        } else {
            ediEmailSender.vendorBillFailed(parseResult.getEdiFile().getName());
        }

        rejectTransaction(parseResult.getEdiData(), rejectTransactionList);
        try {
            ediService.sendEDIFile(edi997Producer.create(parseResult));
            sendTracking(matchedLoads);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return unprocessedEntitiesInd;
    }

    void rejectTransaction(EDIData ediData, List<Integer> transactionIds) {
        if (!transactionIds.isEmpty()) {
            for (Entry<String, EDIFunctionalGroup> entryFunctionalGroup : ediData.getElements().entrySet()) {
                EDIFunctionalGroup functionalGroup = entryFunctionalGroup.getValue();
                int transactionNum = 0;
                for (Entry<String, EDITransaction> entryTransaction : functionalGroup.getElements().entrySet()) {
                    if (transactionIds.contains(transactionNum)) {
                        EDITransaction transaction = new EDITransaction(entryTransaction.getValue().getId());
                        transaction.setRejected(true);
                        entryTransaction.setValue(transaction);
                    }
                    transactionNum++;
                }
            }
        }
    }

    private void saveVendorBill(Map<String, String> carrierRefTypeMap, List<LoadEntity> matchedLoads,
            CarrierInvoiceDetailsEntity vendorBill) throws ApplicationException {
        checkCountryCodeAndZip(vendorBill.getOriginAddress());
        checkCountryCodeAndZip(vendorBill.getDestinationAddress());
        LoadEntity load = vendorBillSaver.saveEdiVendorBill(vendorBill, carrierRefTypeMap);
        if (load != null) {
            matchedLoads.add(load);
        }
    }

    private void checkCountryCodeAndZip(CarrierInvoiceAddressDetailsEntity address) {
        String countryCode = address.getCountryCode();
        if (countryCode != null && countryCode.length() < 3) {
            countryCode = countryDao.findFullCountryCode(countryCode);
        }
        if (StringUtils.isBlank(countryCode)) {
            countryCode = address.getPostalCode() == null || Pattern.matches("\\d+", address.getPostalCode()) ? USA
                    : CAN;
        }
        address.setCountryCode(countryCode);
        fixZipCode(address);
    }

    private void fixZipCode(CarrierInvoiceAddressDetailsEntity address) {
        if (StringUtils.equals(USA, address.getCountryCode()) && address.getPostalCode() != null
                && !Pattern.matches("\\d{5}", address.getPostalCode())) {
            String zipCode = StringUtils.left(address.getPostalCode().replaceAll("[^\\d]", ""), 5);
            address.setPostalCode(zipCode);
        }
    }

    private void sendTracking(List<LoadEntity> matchedLoads) {
        for (LoadEntity load : matchedLoads) {
            LoadTrackingEntity loadTracking = new LoadTrackingEntity();
            loadTracking.setSource(997L);
            loadTracking.setLoad(load);
            loadTracking.setCreatedBy(ediUserId);
            loadTracking.setCarrier(load.getCarrier());
            loadTracking.setPro(load.getNumbers().getProNumber());
            loadTracking.setBol(load.getNumbers().getBolNumber());
            loadTracking.setScac(load.getCarrier().getScac());
            loadTracking.setTrackingDate(new Date());
            loadTracking.setTimezoneCode(TIMEZONE_CODE);
            loadTracking.setFreeMessage("997 has been sent");
            loadTrackingDao.saveOrUpdate(loadTracking);
        }
    }
}
