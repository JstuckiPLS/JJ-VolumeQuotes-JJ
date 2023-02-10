package com.pls.invoice.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.util.SpringApplicationContext;
import com.pls.goship.pls30.dao.UserPLS30CustomerReferenceEntityDao;
import com.pls.goship.pls30.domain.UserPLS30CustomerReferenceEntity;
import com.pls.invoice.dao.InvoiceToAXDao;
import com.pls.invoice.domain.FinancialInvoiceHistoryEntity;
import com.pls.invoice.domain.bo.enums.FinanceColumnAlias;
import com.pls.invoice.domain.xml.finance.AddlOrderInfo;
import com.pls.invoice.domain.xml.finance.FinanceInfoLine;
import com.pls.invoice.domain.xml.finance.FinanceInfoTable;
import com.pls.invoice.domain.xml.finance.salesorder.SalesLine;
import com.pls.invoice.domain.xml.finance.salesorder.SalesOrder;
import com.pls.invoice.domain.xml.finance.vendinvoice.VendInvoiceInfoLine;
import com.pls.invoice.domain.xml.finance.vendinvoice.VendInvoiceInfoTable;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * {@link InvoiceToAXDao} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class InvoiceToAXDaoImpl extends AbstractDaoImpl<LoadEntity, Long> implements InvoiceToAXDao {
    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final int PURCH_NAME_VALUE_LENGTH = 60;
    private static final int DEFAULT_AP_MAX_DUE_DAYS = 35;
    private static final String DEFAULT_AP_TERMS = "NET35";
    
    @Autowired
    UserPLS30CustomerReferenceEntityDao pls30dao;
    
    @Override
    public Collection<FinanceInfoTable> getDataForFinanceSystemByInvoiceID(Long invoiceId) {
        Map<String, FinanceInfoTable> parts = new HashMap<String, FinanceInfoTable>();
        getDataForFinanceSystemByQuery(invoiceId, parts, FinancialInvoiceHistoryEntity.Q_GET_AR_BY_INVOICE_NUMBERS);
        getDataForFinanceSystemByQuery(invoiceId, parts, FinancialInvoiceHistoryEntity.Q_GET_AR_BY_INVOICE_NUMBERS_ADJ);
        handleAddlLoadInfo(invoiceId, parts, CostDetailItemEntity.Q_GET_LOAD_JOB_NUMS_AND_FRT_CLASS);
        handleAddlLoadInfo(invoiceId, parts, CostDetailItemEntity.Q_GET_LOAD_JOB_NUMS_AND_FRT_CLASS_ADJ);
        return (Collection<FinanceInfoTable>) parts.values();
    }

    @SuppressWarnings("unchecked")
    private void getDataForFinanceSystemByQuery(Long invoiceId, Map<String, FinanceInfoTable> parts, String queryName) {
        Map<String, AddlOrderInfo> addlOrderInfo = new HashMap<String, AddlOrderInfo>();
        Query query = getCurrentSession().getNamedQuery(queryName);
        List<Object[]> rows = (List<Object[]>) query.setLong("invoiceId", invoiceId).list();
        String[] aliases = query.getReturnAliases();

        for (Object[] row : rows) {
            StringBuilder addlInfoKey = new StringBuilder("");

            if (getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID) != null) {
                addlInfoKey.append("ADJ_").append(getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID));
            } else {
                addlInfoKey.append("LOAD_").append(getRowValue(row, aliases, FinanceColumnAlias.LOAD_ID));
            }
            if (addlOrderInfo.get(addlInfoKey.toString()) == null) {
                addlOrderInfo.put(addlInfoKey.toString(), new AddlOrderInfo());
            }

            CostDetailOwner owner = (CostDetailOwner) getRowValue(row, aliases, FinanceColumnAlias.COST_ITEM_OWNER);
            switch (owner) {
            case S:
                handleFinanceInfoData(parts, aliases, row, true, owner);
                break;
            case C:
                handleFinanceInfoData(parts, aliases, row, false, owner);
                handleAddlOrderInfo(aliases, row, owner, addlOrderInfo.get(addlInfoKey.toString()));
                break;
            case B:
                handleAddlOrderInfo(aliases, row, owner, addlOrderInfo.get(addlInfoKey.toString()));
                break;
            default:
                break;
            }
        }
        addAdditionalInfo(parts, addlOrderInfo);
    }

    private void addAdditionalInfo(Map<String, FinanceInfoTable> parts, Map<String, AddlOrderInfo> addlOrderInfo) {
        for (FinanceInfoTable financeInfo : parts.values()) {
            if (financeInfo.isArType()) {
                SalesOrder so = (SalesOrder) financeInfo;
                StringBuilder addlInfoKey = new StringBuilder();
                if (so.getFaaDetailId() != null) {
                    addlInfoKey.append("ADJ_").append(so.getFaaDetailId());
                } else {
                    addlInfoKey.append("LOAD_").append(so.getLoadId());
                }

                AddlOrderInfo addlInfo = addlOrderInfo.get(addlInfoKey.toString());
                if (addlInfo != null) {
                    so.setCarrierPremium(addlInfo.getCarrierLinehaul());
                    so.setBenchmarkRate(addlInfo.getBenchmarkRate());
                    so.setFuelBenchmark(addlInfo.getFuelBenchmark());
                    if (addlInfo.getBenchmarkRate() != null || addlInfo.getFuelBenchmark() != null) {
                        so.setTotalBenchmark((addlInfo.getBenchmarkRate() != null ? addlInfo.getBenchmarkRate() : 0.0d)
                                + (addlInfo.getFuelBenchmark() != null ? addlInfo.getFuelBenchmark() : 0.0d));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleAddlLoadInfo(Long invoiceId, Map<String, FinanceInfoTable> parts, String queryName) {
        Query query = getCurrentSession().getNamedQuery(queryName);
        List<Object[]> rows = (List<Object[]>) query.setLong("invoiceId", invoiceId).list();
        for (Object[] row : rows) {
            setLoadAddlInfo(parts, row);
        }
    }

    private String getPartKey(String[] aliases, Object[] row, boolean isAr) {
        StringBuilder partKey = new StringBuilder(isAr ? "AR_" : "AP_");

        if (getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID) != null) {
            partKey.append("ADJ_").append(getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID));
        } else {
            partKey.append("LOAD_").append(getRowValue(row, aliases, FinanceColumnAlias.LOAD_ID));
        }

        return partKey.toString();
    }

    private void setLoadAddlInfo(Map<String, FinanceInfoTable> parts, Object[] row) {
        StringBuilder partKey = new StringBuilder("AR_");

        if (row.length == 5 && row[4] != null) {
            partKey.append("ADJ_").append(row[4]);
        } else {
            partKey.append("LOAD_").append(row[0]);
        }
        SalesOrder part = (SalesOrder) parts.get(partKey.toString());
        if (part != null) {
            part.setJobNumbers((String) row[1]);
            part.setJobPercents((String) row[2]);
            part.setFreightClass((String) row[3]);
        }
    }

    private void handleFinanceInfoData(Map<String, FinanceInfoTable> parts, String[] aliases, Object[] row, boolean isAr, CostDetailOwner owner) {
        String partKey = getPartKey(aliases, row, isAr);
        FinanceInfoTable infoTable;
        if (parts.containsKey(partKey)) {
            infoTable = parts.get(partKey);
        } else {
            infoTable = createFinanceInfoTable(row, aliases, isAr);
            parts.put(partKey, infoTable);
        }

        if (owner != CostDetailOwner.B) {
            fillCostDetails(row, aliases, infoTable);
        }
    }

    private void handleAddlOrderInfo(String[] aliases, Object[] row, CostDetailOwner owner, AddlOrderInfo addlOrderInfo) {
        Object price = getRowValue(row, aliases, FinanceColumnAlias.PRICE);
        if (price != null) {
            switch (owner) {
            case C:
                if ("LH".equals((String) getRowValue(row, aliases, FinanceColumnAlias.COST_ITEM_ID))) {
                    addlOrderInfo.setCarrierLinehaul(((BigDecimal) price).doubleValue());
                }
                break;
            case B:
                if ("SBR".equals((String) getRowValue(row, aliases, FinanceColumnAlias.COST_ITEM_ID))) {
                    addlOrderInfo.setBenchmarkRate(((BigDecimal) price).doubleValue());
                } else if ("FS".equals((String) getRowValue(row, aliases, FinanceColumnAlias.COST_ITEM_ID))) {
                    addlOrderInfo.setFuelBenchmark(((BigDecimal) price).doubleValue());
                }
                break;
            default:
                break;
            }
        }
    }

    private Object getRowValue(Object[] row, String[] aliases, FinanceColumnAlias alias) {
        int index = alias.getAliasIndex(aliases);
        return index >= 0 ? row[index] : null;
    }

    private FinanceInfoTable createFinanceInfoTable(Object[] row, String[] aliases, boolean isAr) {
        FinanceInfoTable result;
        if (isAr) {
            result = createSalesTablePart(row, aliases);
        } else {
            result = createVendorTablePart(row, aliases);
        }
        return result;
    }

    private SalesOrder createSalesTablePart(Object[] row, String[] aliases) {
        SalesOrder part = new SalesOrder();
        List<SalesLine> salesLines = new ArrayList<SalesLine>();
        part.setSalesLines(salesLines);
        fillFinanceInfoTableAR(row, aliases, part);

        part.setCurrency(((Currency) getRowValue(row, aliases, FinanceColumnAlias.CURRENCY)).name());
        
        String accountNumber = null;

        Date creationDate = (Date) getRowValue(row, aliases, FinanceColumnAlias.LOAD_CREATED_DATE);
        String sourceInd = (String) getRowValue(row, aliases, FinanceColumnAlias.SOURCE_IND);
        Calendar c = Calendar.getInstance();
        c.set(2019, 8, 1, 0, 0, 0);
        // loads after 2019 sept 1., created via goship, will have AX number from pls3.0
        if ("GS".equals(sourceInd) && creationDate.after(c.getTime())) {
            Long personId = (Long) getRowValue(row, aliases, FinanceColumnAlias.LOAD_CREATED_BY_USER_ID);
            UserPLS30CustomerReferenceEntity pls3Ref = pls30dao.find(personId);

            if (pls3Ref != null && pls3Ref.getAxAccountNumber() != null) {
                accountNumber = pls3Ref.getAxAccountNumber();
            }
        }

        if (accountNumber == null) {
            accountNumber = new StringBuilder().append(getRowValue(row, aliases, FinanceColumnAlias.CUSTOMER_CODE)).append(getRowValue(row, aliases, FinanceColumnAlias.CUSTOMER_NUMBER)).append('-')
                    .append(getRowValue(row, aliases, FinanceColumnAlias.CUSTOMER_ID)).toString();
        }
        
        part.setCustAccount(accountNumber);

        part.setBol((String) getRowValue(row, aliases, FinanceColumnAlias.BOL));
        part.setShipmentNo((String) getRowValue(row, aliases, FinanceColumnAlias.REF_NUMBER));
        part.setDeliveryDate((Date) getRowValue(row, aliases, FinanceColumnAlias.ARRIVAL_DATE));
        part.setDeliveryTime((Date) getRowValue(row, aliases, FinanceColumnAlias.ARRIVAL_DATE));
        part.setPickupDate((Date) getRowValue(row, aliases, FinanceColumnAlias.DEPARTURE_DATE));
        part.setPickupTime((Date) getRowValue(row, aliases, FinanceColumnAlias.DEPARTURE_DATE));
        part.setBillToLoc((String) getRowValue(row, aliases, FinanceColumnAlias.CUSTOMER_ID));
        part.setBillToName((String) getRowValue(row, aliases, FinanceColumnAlias.BILL_TO_NAME));
        part.setBillToId(String.valueOf(getRowValue(row, aliases, FinanceColumnAlias.BILL_TO_ID)));
        part.setCarrierName((String) getRowValue(row, aliases, FinanceColumnAlias.CARRIER_NAME));
        part.setCommodity((String) getRowValue(row, aliases, FinanceColumnAlias.COMMODITY));
        part.setDestAddr1((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_ADDRESS1));
        part.setDestAddr2((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_ADDRESS2));
        part.setDestLoc((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_ADDRESS_CODE));
        part.setDestName((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_CONTACT_NAME));
        part.setDestNode((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_ADDRESS_CODE));
        part.setDestZip((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_ZIP_CODE));
        part.setDestCity((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_CITY));
        part.setDestState((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_STATE_CODE));
        part.setDestCountry((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_COUNTRY_CODE));
        part.setEquipmentType((String) getRowValue(row, aliases, FinanceColumnAlias.EQUIPMENT_TYPE));
        part.setGlNumber((String) getRowValue(row, aliases, FinanceColumnAlias.GL_NUMBER));
        part.setInbOtb(((ShipmentDirection) getRowValue(row, aliases, FinanceColumnAlias.SHIPMENT_DIRECTION)).getCode());
        part.setInvoiceNo((String) getRowValue(row, aliases, FinanceColumnAlias.INVOICE_NUMBER));
        part.setMiles((Integer) getRowValue(row, aliases, FinanceColumnAlias.MILEAGE));
        part.setCustomerOrgId((Long) getRowValue(row, aliases, FinanceColumnAlias.CUSTOMER_ORG_ID));
        part.setOrigAddr1((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_ADDRESS1));
        part.setOrigAddr2((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_ADDRESS2));
        part.setOrigLoc((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_ADDRESS_CODE));
        part.setOrigName((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_CONTACT_NAME));
        part.setOrigNode((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_ADDRESS_CODE));
        part.setOrigZip((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_ZIP_CODE));
        part.setOrigCity((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_CITY));
        part.setOrigState((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_STATE_CODE));
        part.setOrigCountry((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_COUNTRY_CODE));
        part.setLocationId((Long) getRowValue(row, aliases, FinanceColumnAlias.LOCATION_ID));
        part.setLocationName((String) getRowValue(row, aliases, FinanceColumnAlias.LOCATION_NAME));
        part.setPieces((Integer) getRowValue(row, aliases, FinanceColumnAlias.PIECES));
        part.setProse1((String) getRowValue(row, aliases, FinanceColumnAlias.GL_NUMBER));
        part.setProse2((String) getRowValue(row, aliases, FinanceColumnAlias.PO_NUMBER));
        part.setPartNum((String) getRowValue(row, aliases, FinanceColumnAlias.SO_NUMBER));
        part.setTrailerNo((String) getRowValue(row, aliases, FinanceColumnAlias.TRAILER_NUMBER));
        part.setWeight((Integer) getRowValue(row, aliases, FinanceColumnAlias.WEIGHT));
        part.setTotalStops((Integer) getRowValue(row, aliases, FinanceColumnAlias.SEQ_IN_ROUTE));
        part.setShipWith(0);
        part.setHazmat((String) getRowValue(row, aliases, FinanceColumnAlias.HAZMAT));

        part.setEquipmentDesc((String) getRowValue(row, aliases, FinanceColumnAlias.CT_DESCRIPTION));
        part.setCarrierCost(((BigDecimal) getRowValue(row, aliases, FinanceColumnAlias.TOTAL_COST)).doubleValue());
        part.setLoadNumber(evaluateSalesId(row, aliases));
        // Required for reporting - START
        part.setBillerType((String) getRowValue(row, aliases, FinanceColumnAlias.BILLER_TYPE));
        part.setLoadId((Long) getRowValue(row, aliases, FinanceColumnAlias.LOAD_ID));
        part.setRequestId(String.valueOf(getRowValue(row, aliases, FinanceColumnAlias.REQUEST_ID)));
        // END

        if (getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID) != null) {
            part.setAddend("1");
            part.setSalesOrderType("1");
            part.setFaaDetailId(String.valueOf(getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID)));
        } else {
            part.setAddend("0");
            part.setSalesOrderType("0");
        }
        fillConditionedFieldsForSalesTable(row, aliases, part);

        return part;
    }

    private String evaluateSalesId(Object[] row, String[] aliases) {
        StringBuilder salesId = new StringBuilder().append(getRowValue(row, aliases, FinanceColumnAlias.LOAD_ID));
        Object adjustmentAccessorial = getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ACCESSORIAL);
        Object revision = getRowValue(row, aliases, FinanceColumnAlias.REVISION);
        if (getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID) != null && adjustmentAccessorial != null && revision != null) {
            if ("ADJ".equals(adjustmentAccessorial)) {
                salesId.append("-AD");
            } else {
                salesId.append("-AC");
            }
            salesId.append(revision);
        }
        return salesId.toString();
    }

    private VendInvoiceInfoTable createVendorTablePart(Object[] row, String[] aliases) {
        VendInvoiceInfoTable part = new VendInvoiceInfoTable();
        part.setVendInvoiceInfoLines(new ArrayList<VendInvoiceInfoLine>());

        fillFinanceInfoTable(row, aliases, part);

        part.setCurrency(((Currency) getRowValue(row, aliases, FinanceColumnAlias.CARRIER_CURRENCY_CODE)).name());
        part.setShipmentNo((String) getRowValue(row, aliases, FinanceColumnAlias.BOL));
        part.setScac((String) getRowValue(row, aliases, FinanceColumnAlias.SCAC));
        part.setLoadNumber(evaluateSalesId(row, aliases));

        if (getRowValue(row, aliases, FinanceColumnAlias.FRT_BILL_RECVD_DATE) != null) {
            part.setFrtBillRecvDate((Date) getRowValue(row, aliases, FinanceColumnAlias.FRT_BILL_RECVD_DATE));
        } else {
            part.setApproved(NO);
        }

        String apTerms = getApTerms(row, aliases);
        if (apTerms != null && !"".equals(apTerms.trim())) {
            part.setApTerms(apTerms);
        }

        part.setCarrierName(StringUtils.substring((String) getRowValue(row, aliases, FinanceColumnAlias.CARRIER_NAME), 0, PURCH_NAME_VALUE_LENGTH));
        part.setGlDate((Date) getRowValue(row, aliases, FinanceColumnAlias.GL_DATE));
        part.setInvoiceNum((String) getRowValue(row, aliases, FinanceColumnAlias.INVOICE_NUMBER));
        part.setShipDate((Date) getRowValue(row, aliases, FinanceColumnAlias.ORIG_DEPARTURE));
        part.setPoNumber((String) getRowValue(row, aliases, FinanceColumnAlias.PO_NUMBER));

        return part;
    }

    private String getApTerms(Object[] row, String[] aliases) {
        Object apDueDays = getRowValue(row, aliases, FinanceColumnAlias.AP_DUEDAYS);
        Object maxApDueDays = getRowValue(row, aliases, FinanceColumnAlias.MAX_AP_DUEDAYS);
        Object defaultApTerms = getRowValue(row, aliases, FinanceColumnAlias.DEFAULT_AP_TERMS);

        String apTerms = "";
        if (apDueDays != null) {
            if (defaultApTerms != null && apDueDays != null && maxApDueDays != null && (int) apDueDays > (int) maxApDueDays) {
                apTerms = (String) defaultApTerms;
            } else if (apDueDays != null && (int) apDueDays > DEFAULT_AP_MAX_DUE_DAYS) {
                apTerms = DEFAULT_AP_TERMS;
            } else {
                apTerms = (String) getRowValue(row, aliases, FinanceColumnAlias.AP_TERMS);
            }
        } else {
            apTerms = (String) defaultApTerms;
        }
        return apTerms;
    }

    private void fillFinanceInfoTableAR(Object[] row, String[] aliases, SalesOrder part) {
        part.setScac((String) getRowValue(row, aliases, FinanceColumnAlias.SCAC));
        part.setGlDate((Date) getRowValue(row, aliases, FinanceColumnAlias.GL_DATE));

        Date glDate = (Date) getRowValue(row, aliases, FinanceColumnAlias.GL_DATE);
        Date releaseDate = (Date) getRowValue(row, aliases, FinanceColumnAlias.CREATION_DATE);
        int differenceInDays = getDifferenceInDays(releaseDate, glDate);
        if (differenceInDays > 14) {
            part.setOutPeriod(YES);
        } else {
            part.setOutPeriod(NO);
        }

        part.setNetworkId(String.valueOf(getRowValue(row, aliases, FinanceColumnAlias.NETWORK_ID)));
        part.setProNumber((String) getRowValue(row, aliases, FinanceColumnAlias.PRO_NUMBER));

        // Required for reporting - START
        part.setBillerType((String) getRowValue(row, aliases, FinanceColumnAlias.BILLER_TYPE));
        part.setLoadId((Long) (getRowValue(row, aliases, FinanceColumnAlias.LOAD_ID)));
        part.setRequestId(String.valueOf(getRowValue(row, aliases, FinanceColumnAlias.REQUEST_ID)));
        part.setPersonId(extractCurrentUserId());
        // END

        if (getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID) != null) {
            part.setAdjDate((Date) getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_DATE));
            part.setFaaDetailId(String.valueOf(getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID)));
            Boolean shortPay = (Boolean) getRowValue(row, aliases, FinanceColumnAlias.SHORT_PAY);
            part.setShortPay(BooleanUtils.isTrue(shortPay) ? YES : NO);
            Boolean doNotInvoice = (Boolean) getRowValue(row, aliases, FinanceColumnAlias.DO_NOT_INVOICE);
            part.setDoNotInvoice(BooleanUtils.isTrue(doNotInvoice) ? YES : NO);
        }

        part.setCostCenter((String) getRowValue(row, aliases, FinanceColumnAlias.COST_CENTER));
        part.setBusinessUnit((String) getRowValue(row, aliases, FinanceColumnAlias.UNIT_CODE));
    }

    private void fillFinanceInfoTable(Object[] row, String[] aliases, FinanceInfoTable part) {
        part.setDestCity((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_CITY));
        part.setDestState((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_STATE_CODE));
        part.setDestCountry((String) getRowValue(row, aliases, FinanceColumnAlias.DESTINATION_COUNTRY_CODE));
        part.setNetworkId(String.valueOf(getRowValue(row, aliases, FinanceColumnAlias.NETWORK_ID)));
        part.setOriginCity((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_CITY));
        part.setOriginState((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_STATE_CODE));
        part.setOriginCountry((String) getRowValue(row, aliases, FinanceColumnAlias.ORIGIN_COUNTRY_CODE));
        part.setProNumber((String) getRowValue(row, aliases, FinanceColumnAlias.PRO_NUMBER));

        // Required for reporting - START
        part.setBillerType((String) getRowValue(row, aliases, FinanceColumnAlias.BILLER_TYPE));
        part.setLoadId((Long) getRowValue(row, aliases, FinanceColumnAlias.LOAD_ID));
        part.setRequestId(String.valueOf(getRowValue(row, aliases, FinanceColumnAlias.REQUEST_ID)));
        part.setPersonId(extractCurrentUserId());
        // END

        if (getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID) != null) {
            part.setAdjDate((Date) getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_DATE));
            part.setFaaDetailId(String.valueOf(getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID)));
        }

        part.setBusinessUnit((String) getRowValue(row, aliases, FinanceColumnAlias.UNIT_CODE));
        part.setCostCenter((String) getRowValue(row, aliases, FinanceColumnAlias.COST_CENTER));
    }

    private void fillConditionedFieldsForSalesTable(Object[] row, String[] aliases, SalesOrder part) {
        if (getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_ID) != null) {
            part.setAdjType((String) getRowValue(row, aliases, FinanceColumnAlias.ADJUSTMENT_TYPE));
        }
        Object margin = getRowValue(row, aliases, FinanceColumnAlias.MARGIN);
        if (margin != null) {
            part.setMargin(((BigDecimal) margin).doubleValue());
        }
        Object paymentTerms = getRowValue(row, aliases, FinanceColumnAlias.PAYMENT_TERMS);
        if (paymentTerms != null) {
            String paymentTermsCode = ((PaymentTerms) paymentTerms).getPaymentTermsCode();
            part.setPayTerms(paymentTermsCode);
        }
    }

    private void fillCostDetails(Object[] row, String[] aliases, FinanceInfoTable part) {
        if (part instanceof SalesOrder) {
            SalesLine salesLine = new SalesLine();
            fillCostLine(salesLine, row, aliases);
            ((SalesOrder) part).getSalesLines().add(salesLine);
            if ("DS".equals(salesLine.getItemType())) {
                ((SalesOrder) part).setDiscount(salesLine.getTotal());
            }
        } else if (part instanceof VendInvoiceInfoTable) {
            VendInvoiceInfoLine infoLine = new VendInvoiceInfoLine();
            fillCostLine(infoLine, row, aliases);
            ((VendInvoiceInfoTable) part).getVendInvoiceInfoLines().add(infoLine);
        }
    }

    private void fillCostLine(FinanceInfoLine financeInfoLine, Object[] row, String[] aliases) {
        financeInfoLine.setItemType((String) getRowValue(row, aliases, FinanceColumnAlias.COST_ITEM_ID));
        financeInfoLine.setUnitType((String) getRowValue(row, aliases, FinanceColumnAlias.UNIT_TYPE));
        financeInfoLine.setUnitCost((BigDecimal) getRowValue(row, aliases, FinanceColumnAlias.UNIT_COST));
        financeInfoLine.setQuantity((Long) getRowValue(row, aliases, FinanceColumnAlias.QUANTITY));
        if (financeInfoLine.getUnitCost() != null && financeInfoLine.getUnitCost().signum() < 0
                && financeInfoLine.getQuantity() != null && financeInfoLine.getQuantity() > 0) {
            financeInfoLine.setQuantity(-financeInfoLine.getQuantity());
        }
        financeInfoLine.setComments((String) getRowValue(row, aliases, FinanceColumnAlias.NOTE));
        Object price = getRowValue(row, aliases, FinanceColumnAlias.PRICE);
        if (price != null) {
            financeInfoLine.setTotal(((BigDecimal) price).abs());
        }
    }

    private int getDifferenceInDays(Date date1, Date date2) {
        Long diff = getCalendar(date1) - getCalendar(date2);
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    private Long getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    private Long extractCurrentUserId() {
        Long result = SecurityUtils.getCurrentPersonId();
        return result == null ? SpringApplicationContext.getAdminUserId() : result;
    }
}
