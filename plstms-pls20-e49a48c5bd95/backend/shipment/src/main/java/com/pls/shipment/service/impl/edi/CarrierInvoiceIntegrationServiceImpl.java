package com.pls.shipment.service.impl.edi;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.enums.CarrierInvoiceAddressType;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.MaterialJaxbBO;
import com.pls.shipment.domain.sterling.TransactionDateJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.service.edi.IntegrationService;
import com.pls.shipment.service.edi.match.VendorBillEdiSaver;

/**
 * This service is used for handling the Invoice which is received by carrier or sent to customer.
 * 
 * @author Yasaman Honarvar
 */
@Service("carrierInvoiceIntegrationService")
@Transactional(rollbackFor = Exception.class)
public class CarrierInvoiceIntegrationServiceImpl implements IntegrationService<LoadMessageJaxbBO> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CarrierInvoiceIntegrationServiceImpl.class);

    @Autowired
    private CarrierDao carrierDao;

    @Autowired
    private VendorBillEdiSaver vendorBillSaver;

    /**
     * This handler dispatches the CarrierInvoicedetails entity to the right DAO in order to handle invoice.
     * 
     * @param invoice
     *            the object holding the invoice details and the data we got from XML file.
     * @throws ApplicationException
     *             when an exception occurs in the sub-methods.
     */
    @Override
    public void processMessage(LoadMessageJaxbBO invoice) throws ApplicationException {
        CarrierInvoiceDetailsEntity carrierInvoiceDetails = new CarrierInvoiceDetailsEntity();
        Currency currency = invoice.getBillingCurrencyCode() != null ? Currency.valueOf(invoice.getBillingCurrencyCode()) : Currency.USD;
        CarrierEntity carrier = carrierDao.findByScacAndCurrency(invoice.getScac(), currency);
        
        if(carrier == null) {
            LOGGER.warn("Inbound invoice for non-existing carrier and currency combination: {}, currency: {}; bol: {}, pro: {}, invoice#: {}, edi account: {}", 
                    invoice.getScac(), currency, invoice.getBol(), invoice.getProNumber(), invoice.getInvoiceNumber(), invoice.getEdiAccountNum());
        }

        carrierInvoiceDetails.setCarrier(carrier);
        carrierInvoiceDetails.setStatus(Status.ACTIVE);
        carrierInvoiceDetails.setEdi(true);
        carrierInvoiceDetails.setInvoiceDate(Calendar.getInstance().getTime());
        carrierInvoiceDetails.setPaymentTerms(PaymentTerms.getByCode(invoice.getInvoicePayTerms()));
        carrierInvoiceDetails.setNetAmount(invoice.getInvoiceAmount());
        carrierInvoiceDetails.setTotalWeight(invoice.getTotalWeight());
        carrierInvoiceDetails.setTotalCharges(invoice.getTotalCost());
        carrierInvoiceDetails.setTotalQuantity(invoice.getTotalQuantity());

        setIdentificationValues(invoice, carrierInvoiceDetails);

        setDates(carrierInvoiceDetails, invoice.getTransactionDates());

        setAddresses(carrierInvoiceDetails, invoice.getAddresses());

        setLineItems(carrierInvoiceDetails, invoice.getMaterials());

        vendorBillSaver.saveEdiVendorBill(carrierInvoiceDetails, new HashMap<String, String>());
    }

    /**
     * Sets the pickup, delivery and estimated delivery if exists.
     * 
     * @param carrierInvoiceDetails
     *            the invoice detail entity to be set.
     * @param transactionDates
     *            the dates we received from the invoice.
     */
    private void setDates(CarrierInvoiceDetailsEntity carrierInvoiceDetails, List<TransactionDateJaxbBO> transactionDates) {
        if (transactionDates != null) {
            for (TransactionDateJaxbBO transactionDate : transactionDates) {
                switch (transactionDate.getTransDateType()) {
                case CONFIRM_PICKUP:
                    carrierInvoiceDetails.setActualPickupDate(transactionDate.getTransDate());
                    break;
                case CONFIRM_DELIVERY:
                    carrierInvoiceDetails.setDeliveryDate(transactionDate.getTransDate());
                    break;
                case ESTIMATED_DELIVERY:
                    carrierInvoiceDetails.setEstDeliveryDate(transactionDate.getTransDate());
                    break;
                default:
                    break;
                }
            }
        }
    }

    /**
     * Sets up the values for individual items in the Invoice.
     * 
     * @param carrierInvoiceDetails
     *            is the main invoice Entity.
     * @param list
     *            is the Item BO received to populate the item objects.
     */
    private void setLineItems(CarrierInvoiceDetailsEntity carrierInvoiceDetails, List<MaterialJaxbBO> materials) {
        Set<CarrierInvoiceLineItemEntity> carrierInvoiceLineItems = new HashSet<CarrierInvoiceLineItemEntity>();
        for (MaterialJaxbBO material : materials) {
            CarrierInvoiceLineItemEntity carrierInvoiceLineItem = new CarrierInvoiceLineItemEntity();
            carrierInvoiceLineItem.setCarrierInvoiceDetails(carrierInvoiceDetails);
            carrierInvoiceLineItem.setWeight(material.getWeight());
            carrierInvoiceLineItem.setCharge(material.getSubtotal());
            carrierInvoiceLineItem.setNmfc(material.getNmfc());
            carrierInvoiceLineItem.setDescription(material.getProductDesc());
            carrierInvoiceLineItem.setOrderNumber(material.getOrderNum());
            carrierInvoiceLineItem.setPackagingCode(material.getPackagingType());
            carrierInvoiceLineItem.setQuantity(material.getQuantity());
            carrierInvoiceLineItem.setSpecialChargeCode(material.getSpecialChargeCode());
            carrierInvoiceLineItem.setStatus(Status.ACTIVE);
            carrierInvoiceLineItem.setCommodityClass(CommodityClass.convertFromDbCodeSafe(material.getCommodityClassCode()));
            carrierInvoiceLineItems.add(carrierInvoiceLineItem);
        }
        carrierInvoiceDetails.setCarrierInvoiceLineItems(carrierInvoiceLineItems);
    }

    /**
     * goes through the addresses and finds the original and destination address that are to be set in the Entity and Sets up the address Entity with
     * the given address BO.
     * 
     * @param carrierInvoiceDetails
     *            the Entity we are setting values for.
     * @param list
     *            the list of address which can holds destinations, origins and etc.
     */
    private void setAddresses(CarrierInvoiceDetailsEntity carrierInvoiceDetails, List<AddressJaxbBO> addresses) {

        for (AddressJaxbBO add : addresses) {
            if (add.getAddressType() != AddressType.ORIGIN && add.getAddressType() != AddressType.DESTINATION) {
                continue;
            }

            CarrierInvoiceAddressDetailsEntity address = new CarrierInvoiceAddressDetailsEntity();
            address.setAddress1(add.getAddress1());
            address.setAddress2(add.getAddress2());
            address.setAddressName(add.getName());
            address.setCarrierInvoiceDetails(carrierInvoiceDetails);
            address.setCity(add.getCity());
            address.setPostalCode(add.getPostalCode());
            address.setState(add.getStateCode());
            address.setCountryCode(add.getCountryCode());

            if (add.getAddressType() == AddressType.ORIGIN) {
                address.setAddressType(CarrierInvoiceAddressType.ORIGIN);
                carrierInvoiceDetails.setOriginAddress(address);
            } else if (add.getAddressType() == AddressType.DESTINATION) {
                address.setAddressType(CarrierInvoiceAddressType.DESTINATION);
                carrierInvoiceDetails.setDestinationAddress(address);
            }
        }
    }

    /**
     * Sets up the identification values and numbers of carrierInvoice details.
     * 
     * @param invoice
     *            the Business object holding the needed values.
     * @param carrierInvoiceDetails
     *            the Entity object.
     */
    private void setIdentificationValues(LoadMessageJaxbBO invoice, CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        carrierInvoiceDetails.setBolNumber(invoice.getBol());
        carrierInvoiceDetails.setProNumber(invoice.getProNumber());
        carrierInvoiceDetails.setReferenceNumber(invoice.getPickupNum());
        carrierInvoiceDetails.setPoNumber(invoice.getPoNum());
        carrierInvoiceDetails.setShipperRefNumber(invoice.getShipmentNo());
        carrierInvoiceDetails.setInvoiceNumber(invoice.getInvoiceNumber());
        carrierInvoiceDetails.setEdiAccount(invoice.getEdiAccountNum());
    }
}
