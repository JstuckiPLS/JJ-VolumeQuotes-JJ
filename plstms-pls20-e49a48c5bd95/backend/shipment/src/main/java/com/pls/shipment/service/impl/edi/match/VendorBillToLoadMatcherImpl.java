package com.pls.shipment.service.impl.edi.match;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.edi.match.VendorBillToLoadMatcher;

/**
 * Implementation of {@link VendorBillToLoadMatcher}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class VendorBillToLoadMatcherImpl implements VendorBillToLoadMatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(VendorBillToLoadMatcherImpl.class);

    private static final List<ShipmentStatus> STATUSES = Arrays.asList(ShipmentStatus.CANCELLED, ShipmentStatus.OPEN, ShipmentStatus.BOOKED);

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Override
    public LoadEntity findMatchingLoad(CarrierInvoiceDetailsEntity vendorBill) {
        Collection<LoadEntity> loads = findMatchingLoads(vendorBill);
        loads = filterLoadsByBolNumber(loads, vendorBill.getBolNumber());
        loads = filterLoadsByZipCodes(loads, vendorBill);
        loads = filterLoadsByStatus(loads);
        loads = filterInvoicedLoads(loads);
        if (loads.size() == 1) {
            LOGGER.info(String.format("Single match was found for vendor bill: %d", loads.iterator().next().getId()));
            return loads.iterator().next();
        } else {
            LOGGER.info(String.format("No single match was found for vendor bill. Actually there were %d loads found", loads.size()));
            return null;
        }
    }

    private Collection<LoadEntity> filterInvoicedLoads(Collection<LoadEntity> loads) {
        if (loads.size() > 1) {
            Collection<LoadEntity> filteredLoads = Collections2.filter(loads, new Predicate<LoadEntity>() {
                @Override
                public boolean apply(LoadEntity input) {
                    return input.getFinalizationStatus() != ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE;
                }
            });
            if (!filteredLoads.isEmpty() && filteredLoads.size() < loads.size()) {
                LOGGER.info("Loads for vendor bill were filtered by financial status");
                return filteredLoads;
            }
        }
        return loads;
    }

    private List<LoadEntity> findMatchingLoads(CarrierInvoiceDetailsEntity vendorBill) {
        List<LoadEntity> loads = findByProNumber(vendorBill);
        if (CollectionUtils.isEmpty(loads)) {
            loads = findByZip(vendorBill);
        }
        if (CollectionUtils.isEmpty(loads)) {
            loads = findByCityAndState(vendorBill);
        }
        return loads == null ? Collections.<LoadEntity>emptyList() : loads;
    }

    private List<LoadEntity> findByProNumber(CarrierInvoiceDetailsEntity vendorBill) {
        if (StringUtils.isNotEmpty(vendorBill.getProNumber())) {
            LOGGER.info(String.format("Searching loads for vendor bill by PRO#: '%s' and SCAC: '%s'", vendorBill.getProNumber(),
                    vendorBill.getCarrier().getScac()));
            return ltlShipmentDao.findAllShipmentsByScacAndProNumber(vendorBill.getCarrier().getScac(), vendorBill.getProNumber());
        }
        return null;
    }

    private List<LoadEntity> findByZip(CarrierInvoiceDetailsEntity vendorBill) {
        CarrierInvoiceAddressDetailsEntity origin = vendorBill.getOriginAddress();
        CarrierInvoiceAddressDetailsEntity destination = vendorBill.getDestinationAddress();
        if (StringUtils.isNotEmpty(vendorBill.getBolNumber()) && origin != null && StringUtils.isNotEmpty(origin.getPostalCode())
                && destination != null && StringUtils.isNotEmpty(destination.getPostalCode())) {
            LOGGER.info(String.format("Searching loads for vendor bill by BOL#: '%s' and SCAC: '%s' and zip codes", vendorBill.getBolNumber(),
                    vendorBill.getCarrier().getScac()));
            return ltlShipmentDao.findShipmentByScacAndBolNumberAndZip(vendorBill.getBolNumber(), vendorBill.getCarrier().getScac(),
                    origin.getPostalCode(), destination.getPostalCode());
        }
        return null;
    }

    private List<LoadEntity> findByCityAndState(CarrierInvoiceDetailsEntity vendorBill) {
        CarrierInvoiceAddressDetailsEntity origin = vendorBill.getOriginAddress();
        CarrierInvoiceAddressDetailsEntity destination = vendorBill.getDestinationAddress();
        if (StringUtils.isNotEmpty(vendorBill.getBolNumber())
                && origin != null && StringUtils.isNotEmpty(origin.getCity()) && StringUtils.isNotEmpty(origin.getState())
                && destination != null && StringUtils.isNotEmpty(destination.getCity()) && StringUtils.isNotEmpty(destination.getState())) {
            LOGGER.info(String.format("Searching loads for vendor bill by BOL#: '%s' and SCAC: '%s' and address information",
                    vendorBill.getBolNumber(), vendorBill.getCarrier().getScac()));
            return ltlShipmentDao.findShipmentByScacAndBolNumberAndCityAndState(vendorBill.getBolNumber(),
                vendorBill.getCarrier().getScac(), origin.getCity(), origin.getState(), destination.getCity(), destination.getState());
        }
        return null;
    }

    private Collection<LoadEntity> filterLoadsByBolNumber(Collection<LoadEntity> loads, final String bolNumber) {
        if (loads.size() > 1 && StringUtils.isNotEmpty(bolNumber)) {
            Collection<LoadEntity> filteredLoads = Collections2.filter(loads, new Predicate<LoadEntity>() {
                @Override
                public boolean apply(LoadEntity input) {
                    return StringUtils.equalsIgnoreCase(bolNumber, input.getNumbers().getBolNumber());
                }
            });
            if (!filteredLoads.isEmpty() && filteredLoads.size() < loads.size()) {
                LOGGER.info(String.format("Loads for vendor bill were filtered by BOL#: '%s'", bolNumber));
                return filteredLoads;
            }
        }
        return loads;
    }

    private Collection<LoadEntity> filterLoadsByZipCodes(Collection<LoadEntity> loads, CarrierInvoiceDetailsEntity vendorBill) {
        CarrierInvoiceAddressDetailsEntity origin = vendorBill.getOriginAddress();
        CarrierInvoiceAddressDetailsEntity destination = vendorBill.getDestinationAddress();
        if (loads.size() > 1 && origin != null && StringUtils.isNotEmpty(origin.getPostalCode())
                && destination != null && StringUtils.isNotEmpty(destination.getPostalCode())) {
            final String originZip = origin.getPostalCode();
            final String destinationZip = destination.getPostalCode();
            Collection<LoadEntity> filteredLoads = Collections2.filter(loads, new Predicate<LoadEntity>() {
                @Override
                public boolean apply(LoadEntity input) {
                    return StringUtils.equalsIgnoreCase(originZip, getZip(input.getOrigin()))
                            && StringUtils.equalsIgnoreCase(destinationZip, getZip(input.getDestination()));
                }
            });
            if (!filteredLoads.isEmpty() && filteredLoads.size() < loads.size()) {
                LOGGER.info(String.format("Loads for vendor bill were filtered by ZIP codes: '%s', '%s'", origin.getPostalCode(),
                        destination.getPostalCode()));
                return filteredLoads;
            }
        }
        return loads;
    }

    private Collection<LoadEntity> filterLoadsByStatus(Collection<LoadEntity> loads) {
        if (loads.size() > 1) {
            Collection<LoadEntity> filteredLoads = Collections2.filter(loads, new Predicate<LoadEntity>() {
                @Override
                public boolean apply(LoadEntity input) {
                    return !STATUSES.contains(input.getStatus());
                }
            });
            if (!filteredLoads.isEmpty() && filteredLoads.size() < loads.size()) {
                LOGGER.info("Loads for vendor bill were filtered by status");
                return filteredLoads;
            }
        }
        return loads;
    }

    private String getZip(LoadDetailsEntity details) {
        return details != null && details.getAddress() != null ? details.getAddress().getZip() : null;
    }
}
