package com.pls.invoice.service.impl.processing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.PhoneNumber;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.PlsCustomerTermsEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.core.service.util.SpringApplicationContext;
import com.pls.invoice.service.processing.EDIInvoiceProcessingService;
import com.pls.invoice.service.processing.InvoiceService;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;
import com.pls.shipment.domain.sterling.AccessorialJaxbBO;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.BaseRateJaxbBO;
import com.pls.shipment.domain.sterling.HazmatInfoJaxbBO;
import com.pls.shipment.domain.sterling.LoadInvoiceJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.MaterialJaxbBO;
import com.pls.shipment.domain.sterling.TransactionDateJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.domain.sterling.enums.RateType;
import com.pls.shipment.domain.sterling.enums.TransactionDateType;
import com.pls.shipment.domain.sterling.enums.YesNo;

/**
 * Implementation of {@link EDIInvoiceProcessingService}.
 * 
 * @author Jasmin Dhamelia
 */
@Service
@Transactional
public class EDIInvoiceProcessingServiceImpl implements EDIInvoiceProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EDIInvoiceProcessingServiceImpl.class);

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Override
    public void sendInvoiceViaEDI(Long invoiceId, BillToEntity billTo) throws ApplicationException {
        LOGGER.info("Sending EDI Invoices to Customer for invoice ID: {}", invoiceId);
        if (billTo.getInvoiceSettings().isEdiInvoice()) {
            List<LoadAdjustmentBO> invoices = invoiceService.getSortedInvoices(invoiceId);

            if (!invoices.isEmpty()) {
                LoadInvoiceJaxbBO loadInvoiceBO = new LoadInvoiceJaxbBO();
                List<LoadMessageJaxbBO> ltlLoadInvoices = new ArrayList<LoadMessageJaxbBO>();
                for (LoadAdjustmentBO invoice : invoices) {
                    if (invoice.getAdjustment() == null) {
                        ltlLoadInvoices.add(createInvoice(invoice.getLoad(), null));
                    } else {
                        ltlLoadInvoices.add(createInvoice(invoice.getAdjustment().getLoad(), invoice.getAdjustment()));
                    }
                }

                try {
                    loadInvoiceBO.setCustomerOrgId(ltlLoadInvoices.get(0).getCustomerOrgId());
                    loadInvoiceBO.setPersonId(extractCurrentUserId());
                    loadInvoiceBO.setLtlLoadInvoices(ltlLoadInvoices);
                    if (billTo.getInvoiceSettings().getInvoiceType() == InvoiceType.TRANSACTIONAL) {
                        loadInvoiceBO.setBol(ltlLoadInvoices.get(0).getBol());
                        loadInvoiceBO.setLoadId(ltlLoadInvoices.get(0).getLoadId());
                        loadInvoiceBO.setScac(ltlLoadInvoices.get(0).getScac());
                        loadInvoiceBO.setShipmentNo(ltlLoadInvoices.get(0).getShipmentNo());
                    }

                    SterlingIntegrationMessageBO sterlingMessage =
                            new SterlingIntegrationMessageBO(loadInvoiceBO, EDIMessageType.CUSTOMER_INVOICE_MESSAGE_TYPE);
                    sterlingMessageProducer.publishMessage(sterlingMessage);
                } catch (Exception ex) {
                    throw new InternalJmsCommunicationException("Exception occurred while publishing message to external integration message queue",
                            ex);
                }
            }
            LOGGER.info("Finished Sending EDI Invoices to Customer for invoice ID: {}", invoiceId);
        } else {
            LOGGER.info("No EDI Invoices will be sent to Customer for invoice ID: {}", invoiceId);
        }
    }

    private LoadMessageJaxbBO createInvoice(LoadEntity load, FinancialAccessorialsEntity adjustment) {
        LoadMessageJaxbBO loadMessageBO = new LoadMessageJaxbBO();
        if (adjustment == null) {
            setInvoiceWithoutAdj(loadMessageBO, load);
            setCostDetails(loadMessageBO, load.getActiveCostDetail().getCostDetailItems());
        } else {
            setInvoiceWithAdj(loadMessageBO, adjustment, load);
            setCostDetails(loadMessageBO, adjustment.getCostDetailItems());
        }
        setLoadDetails(loadMessageBO, load, adjustment);
        setMaterials(load, loadMessageBO);
        setAddresses(load, loadMessageBO);
        setTransactionDates(loadMessageBO, load.getOrigin(), load.getDestination());

        return loadMessageBO;
    }

    private void setInvoiceWithoutAdj(LoadMessageJaxbBO loadMessageBO, LoadEntity load) {
        loadMessageBO.setInvoiceDate(load.getActiveCostDetail().getGeneralLedgerDate());
        loadMessageBO.setInvoiceAmount(load.getActiveCostDetail().getTotalRevenue());
        loadMessageBO.setInvoicePayTerms((load.getBillTo().getPlsCustomerTerms() == null ? StringUtils.EMPTY : StringUtils.defaultString(load
                .getBillTo().getPlsCustomerTerms().getTermName())));
        loadMessageBO.setInvoiceNumber(load.getActiveCostDetail().getInvoiceNumber());
        loadMessageBO.setInvoiceDueDate(getDueDate(load.getActiveCostDetail().getGeneralLedgerDate(), load.getBillTo().getPlsCustomerTerms()));
    }

    private void setInvoiceWithAdj(LoadMessageJaxbBO loadMessageBO, FinancialAccessorialsEntity adjustment, LoadEntity load) {
        loadMessageBO.setInvoiceDate(adjustment.getGeneralLedgerDate());
        loadMessageBO.setInvoiceAmount(adjustment.getTotalRevenue());
        loadMessageBO.setInvoicePayTerms((load.getBillTo().getPlsCustomerTerms() == null ? StringUtils.EMPTY : StringUtils.defaultString(load
                .getBillTo().getPlsCustomerTerms().getTermName())));
        loadMessageBO.setInvoiceNumber(adjustment.getInvoiceNumber());
        loadMessageBO.setInvoiceDueDate(getDueDate(adjustment.getGeneralLedgerDate(), load.getBillTo().getPlsCustomerTerms()));
    }

    private void setLoadDetails(LoadMessageJaxbBO loadMessageBO, LoadEntity load, FinancialAccessorialsEntity adjustment) {
        loadMessageBO.setCustomerName(load.getOrganization().getName());
        loadMessageBO.setScac(load.getCarrier().getScac());
        loadMessageBO.setShipmentNo(load.getNumbers().getRefNumber());
        loadMessageBO.setProNumber(load.getNumbers().getProNumber());
        loadMessageBO.setInboundOutbound(load.getShipmentDirection().getCode());
        loadMessageBO.setLoadId(load.getId());
        loadMessageBO.setPayTerms(load.getPayTerms().getPaymentTermsCode());
        loadMessageBO.setGlNumber(load.getNumbers().getGlNumber());
        loadMessageBO.setBol(load.getNumbers().getBolNumber());
        loadMessageBO.setPoNum(load.getNumbers().getPoNumber());
        loadMessageBO.setMessageType(EDIMessageType.CUSTOMER_INVOICE_MESSAGE_TYPE.name());
        loadMessageBO.setPickupNum(load.getNumbers().getPuNumber());
        loadMessageBO.setSoNum(load.getNumbers().getSoNumber());
        loadMessageBO.setTrailerNum(load.getNumbers().getTrailerNumber());
        loadMessageBO.setTotalMiles(load.getMileage());
        if (adjustment == null) {
            loadMessageBO.setTotalCost(load.getActiveCostDetail().getTotalCost());
            loadMessageBO.setTotalRevenue(load.getActiveCostDetail().getTotalRevenue());
        } else {
            loadMessageBO.setTotalCost(adjustment.getTotalCost());
            loadMessageBO.setTotalRevenue(adjustment.getTotalRevenue());
        }
        loadMessageBO.setCustomerOrgId(load.getOrganization().getId());
        loadMessageBO.setCustomerLocName(load.getLocation().getLocationName());
        loadMessageBO.setCustomerLocationId(load.getLocation().getId());
        loadMessageBO.setCustomerBillToId(load.getBillTo().getId());
        loadMessageBO.setBillingCurrencyCode(load.getBillTo().getCurrency().name());
        loadMessageBO.setEdiAccountNum(load.getOrganization().getEdiAccount());
    }

    private void setTransactionDates(LoadMessageJaxbBO loadMessageBO, LoadDetailsEntity origin, LoadDetailsEntity destination) {
        List<TransactionDateJaxbBO> transactiondDatesList = new ArrayList<TransactionDateJaxbBO>();
        transactiondDatesList.add(getTransactionDate(TransactionDateType.ESTIMATED_PICKUP, origin.getScheduledArrival()));
        transactiondDatesList.add(getTransactionDate(TransactionDateType.CONFIRM_PICKUP, origin.getDeparture()));
        transactiondDatesList.add(getTransactionDate(TransactionDateType.ESTIMATED_DELIVERY, destination.getScheduledArrival()));
        transactiondDatesList.add(getTransactionDate(TransactionDateType.CONFIRM_DELIVERY, destination.getDeparture()));
        loadMessageBO.setTransactionDates(transactiondDatesList);
    }

    private TransactionDateJaxbBO getTransactionDate(TransactionDateType transactionDateType, Date date) {
        TransactionDateJaxbBO transactionDate = new TransactionDateJaxbBO();
        transactionDate.setTransDateType(transactionDateType);
        transactionDate.setTransDate(date);
        return transactionDate;
    }

    private void setCostDetails(LoadMessageJaxbBO loadMessageBO, Set<CostDetailItemEntity> costDetailItems) {
        for (CostDetailItemEntity costDetailItemEntity : costDetailItems) {
            if (!CostDetailOwner.B.equals(costDetailItemEntity.getOwner())) {
                if ("SRA".equals(costDetailItemEntity.getAccessorialType()) || "CRA".equals(costDetailItemEntity.getAccessorialType())) {
                    loadMessageBO.addBaseRate(setBaseRate(costDetailItemEntity));
                } else {
                    loadMessageBO.addAccessorial(setAccessorial(costDetailItemEntity));
                }
            }
        }
    }

    private AccessorialJaxbBO setAccessorial(CostDetailItemEntity costDetailItemEntity) {
        AccessorialJaxbBO accessorial = new AccessorialJaxbBO();
        if ("S".equals(costDetailItemEntity.getOwner().name())) {
            accessorial.setRateType(RateType.SHIPPER);
        } else {
            accessorial.setRateType(RateType.CARRIER);
        }
        accessorial.setCode(costDetailItemEntity.getAccessorialDictionary().getId());
        accessorial.setDescription(costDetailItemEntity.getAccessorialDictionary().getDescription());
        accessorial.setUnitCost(costDetailItemEntity.getUnitCost());
        accessorial.setUnitType(costDetailItemEntity.getUnitType());
        accessorial.setQuantity(costDetailItemEntity.getQuantity());
        accessorial.setSubTotal(costDetailItemEntity.getSubtotal());
        if (LtlAccessorialGroup.PICKUP.equals(costDetailItemEntity.getAccessorialDictionary().getAccessorialGroup())
                || LtlAccessorialGroup.DELIVERY.equals(costDetailItemEntity.getAccessorialDictionary().getAccessorialGroup())) {
            accessorial.setAutoApplied(YesNo.NO);
        } else {
            accessorial.setAutoApplied(YesNo.YES);
        }
        accessorial.setComment(costDetailItemEntity.getNote());
        return accessorial;
    }

    private BaseRateJaxbBO setBaseRate(CostDetailItemEntity costDetailItemEntity) {
        BaseRateJaxbBO baseRate = new BaseRateJaxbBO();
        if ("S".equals(costDetailItemEntity.getOwner().name())) {
            baseRate.setRateType(RateType.SHIPPER);
        } else {
            baseRate.setRateType(RateType.CARRIER);
        }
        baseRate.setUnitCost(costDetailItemEntity.getUnitCost());
        baseRate.setUnitType(costDetailItemEntity.getUnitType());
        baseRate.setQuantity(costDetailItemEntity.getQuantity());
        baseRate.setSubTotal(costDetailItemEntity.getSubtotal());
        baseRate.setComment(costDetailItemEntity.getNote());

        return baseRate;
    }

    private Date getDueDate(Date date, PlsCustomerTermsEntity plsCustomerTermsEntity) {

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(date);
        int dueDays = 0;
        PlsCustomerTermsEntity payTerms = plsCustomerTermsEntity;
        if (payTerms != null) {
            dueDays = payTerms.getDueDays();
        }
        dueDate.add(Calendar.DATE, dueDays);
        return dueDate.getTime();

    }

    private void setMaterials(LoadEntity load, LoadMessageJaxbBO loadMessageBO) {
        if (load.getOrigin().getLoadMaterials() != null) {
            Iterator<LoadMaterialEntity> loadMaterialIterator = load.getOrigin().getLoadMaterials().iterator();
            Integer totalQuantity = 0;
            BigDecimal totalWeight = BigDecimal.valueOf(0);
            Integer totalPieces = 0;
            while (loadMaterialIterator.hasNext()) {
                LoadMaterialEntity loadMaterialEntity = loadMaterialIterator.next();
                if (loadMaterialEntity.getQuantity() != null) {
                    totalQuantity += Integer.valueOf(loadMaterialEntity.getQuantity());
                }

                if (loadMaterialEntity.getWeight() != null) {
                    totalWeight = totalWeight.add(loadMaterialEntity.getWeight());
                }

                if (loadMaterialEntity.getPieces() != null) {
                    totalPieces += Integer.valueOf(loadMaterialEntity.getPieces());
                }
                loadMessageBO.addMaterial(setMaterial(loadMaterialEntity));
            }
            loadMessageBO.setTotalQuantity(totalQuantity);
            loadMessageBO.setTotalWeight(totalWeight);
            loadMessageBO.setTotalPieces(totalPieces);
        }
    }

    private MaterialJaxbBO setMaterial(LoadMaterialEntity loadMaterialEntity) {
        MaterialJaxbBO material = new MaterialJaxbBO();
        material.setWeight(loadMaterialEntity.getWeight());
        material.setCommodityClassCode(loadMaterialEntity.getCommodityClass().getDbCode());
        material.setProductCode(loadMaterialEntity.getProductCode());
        material.setProductDesc(loadMaterialEntity.getProductDescription());
        material.setLength(loadMaterialEntity.getLength());
        material.setWidth(loadMaterialEntity.getWidth());
        material.setHeight(loadMaterialEntity.getHeight());
        material.setQuantity(Integer.parseInt(loadMaterialEntity.getQuantity()));
        material.setPackagingType(loadMaterialEntity.getPackageType() != null ? loadMaterialEntity.getPackageType().getId() : "");
        material.setPackagingDesc(loadMaterialEntity.getPackageType().getDescription());
        material.setStackable(loadMaterialEntity.isStackable() ? YesNo.YES : YesNo.NO);
        material.setPieces(loadMaterialEntity.getPieces());
        material.setNmfc(loadMaterialEntity.getNmfc());
        setHazmatInfo(material, loadMaterialEntity);

        return material;
    }

    private void setHazmatInfo(MaterialJaxbBO material, LoadMaterialEntity loadMaterialEntity) {
        material.setHazmat(loadMaterialEntity.isHazmat() ? YesNo.YES : YesNo.NO);
        if (loadMaterialEntity.isHazmat()) {
            HazmatInfoJaxbBO hazmatInfo = new HazmatInfoJaxbBO();
            hazmatInfo.setUnNum(loadMaterialEntity.getUnNumber());
            hazmatInfo.setPackagingGroupNum(loadMaterialEntity.getPackingGroup());
            hazmatInfo.setEmergencyCompany(loadMaterialEntity.getEmergencyCompany());
            hazmatInfo.setEmergencyPhone(getPhoneNumber(loadMaterialEntity.getEmergencyPhone()));
            hazmatInfo.setEmergencyContractNum(loadMaterialEntity.getEmergencyContract());
            hazmatInfo.setEmergencyInstr(loadMaterialEntity.getHazmatInstruction());
            hazmatInfo.setHzClass(loadMaterialEntity.getHazmatClass());
            material.setHazmatInfo(hazmatInfo);
        }
    }

    private String getPhoneNumber(PhoneNumber phone) {
        return phone != null ? (phone.getCountryCode() + phone.getAreaCode() + phone.getNumber()) : "";
    }

    private void setAddresses(LoadEntity load, LoadMessageJaxbBO loadMessageBO) {
        loadMessageBO.addAddress(setLoadPoint(load.getOrigin(), AddressType.ORIGIN));
        loadMessageBO.addAddress(setLoadPoint(load.getDestination(), AddressType.DESTINATION));
        loadMessageBO.addAddress(setBillToAddress(load.getBillTo(), AddressType.BILL_TO));
    }

    private AddressJaxbBO setLoadPoint(LoadDetailsEntity loadDetailsEntity, AddressType addressType) {
        AddressJaxbBO address = new AddressJaxbBO();
        address.setAddressType(addressType);
        address.setName(loadDetailsEntity.getContact());
        address.setAddressCode(loadDetailsEntity.getAddressCode());
        setAddress(loadDetailsEntity.getAddress(), address);
        address.setContactName(loadDetailsEntity.getContactName());
        address.setContactPhone(loadDetailsEntity.getContactPhone());
        address.setContactFax(loadDetailsEntity.getContactFax());
        address.setContactEmail(loadDetailsEntity.getContactEmail());

        return address;
    }

    private AddressJaxbBO setBillToAddress(BillToEntity billToEntity, AddressType addressType) {
        AddressJaxbBO billToAddress = new AddressJaxbBO();
        billToAddress.setAddressType(addressType);
        billToAddress.setName(billToEntity.getName());
        setAddress(billToEntity.getBillingInvoiceNode().getAddress(), billToAddress);
        billToAddress.setContactName(billToEntity.getBillingInvoiceNode().getContactName());
        billToAddress.setContactPhone(getPhoneOrFaxNumber(billToEntity.getBillingInvoiceNode().getPhone()));
        billToAddress.setContactEmail(billToEntity.getBillingInvoiceNode().getEmail());
        billToAddress.setContactFax(getPhoneOrFaxNumber(billToEntity.getBillingInvoiceNode().getFax()));

        return billToAddress;
    }

    private String getPhoneOrFaxNumber(PhoneEntity phone) {
        return phone != null ? (phone.getCountryCode() + phone.getAreaCode() + phone.getNumber()) : "";
    }

    private void setAddress(AddressEntity entity, AddressJaxbBO address) {
        address.setAddress1(entity.getAddress1());
        address.setAddress2(entity.getAddress2());
        address.setCity(entity.getCity());
        address.setStateCode(entity.getStateCode());
        address.setPostalCode(entity.getZip());
        address.setCountryCode(entity.getCountry().getId());
    }

    private Long extractCurrentUserId() {
        Long result = SecurityUtils.getCurrentPersonId();
        return result == null ? SpringApplicationContext.getAdminUserId() : result;
    }

}
