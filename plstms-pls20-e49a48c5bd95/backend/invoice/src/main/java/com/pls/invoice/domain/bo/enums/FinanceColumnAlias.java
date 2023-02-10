package com.pls.invoice.domain.bo.enums;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Contains alias' names for finance query.
 *
 * @author Sergey Kirichenko
 */
public enum FinanceColumnAlias {
    INVOICE_NUMBER("invoiceNumber"), LOAD_ID("loadId"), ADJUSTMENT_ID("adjId"), ADJUSTMENT_TYPE("adjType"), PRICE("price"),
    UNIT_COST("unitCost"), UNIT_TYPE("unitType"), QUANTITY("quantity"), COST_ITEM_ID("itemId"),
    BILL_TO_NAME("billToName"), BILL_TO_ID("billToId"), CURRENCY("currency"), NETWORK_ID("networkId"), CUSTOMER_CODE("customerCode"),
    CUSTOMER_NUMBER("customerNumber"), CUSTOMER_ID("customerId"), PIECES("pieces"), MILEAGE("mileage"), BOL("bol"), GL_NUMBER("glNumber"),
    PRO_NUMBER("pro"), PO_NUMBER("po"), REF_NUMBER("ref"), PAYMENT_TERMS("paymentTerms"), SHIPMENT_DIRECTION("shipmentDirection"),
    COMMODITY("commodity"), TRAILER_NUMBER("trailer"), MARGIN("margin"), DEPARTURE_DATE("departure"), ARRIVAL_DATE("arrivalDate"),
    CARRIER_NAME("carrierName"), SCAC("scac"), DESTINATION_ADDRESS1("destAddress1"), DESTINATION_ADDRESS2("destAddress2"),
    DESTINATION_CITY("destCity"), DESTINATION_ZIP_CODE("destZip"), DESTINATION_STATE_CODE("destStateCode"),
    DESTINATION_COUNTRY_CODE("destCountryCode"), DESTINATION_ADDRESS_CODE("destAddressCode"), DESTINATION_CONTACT_NAME("destContact"),
    ORIGIN_ADDRESS1("origAddress1"), ORIGIN_ADDRESS2("origAddress2"), ORIGIN_CITY("origCity"), ORIGIN_ZIP_CODE("origZip"),
    ORIGIN_STATE_CODE("origStateCode"), ORIGIN_COUNTRY_CODE("origCountryCode"), ORIGIN_ADDRESS_CODE("origAddressCode"),
    ORIGIN_CONTACT_NAME("origContact"), ADJUSTMENT_DATE("adjCreatedDate"), GL_DATE("glDate"), ADJUSTMENT_ACCESSORIAL("adjustmentAccessorial"),
    REVISION("revision"), COST_ITEM_OWNER("owner"), CARRIER_CURRENCY_CODE("carrierCC"), DO_NOT_INVOICE("doNotInvoice"),
    SHORT_PAY("shortPay"), DESTINATION_AVAILABLE_DELIVERY_DATE("destAvailableDeliveryDate"),
    ORIGIN_AVAILABLE_DELIVERY_DATE("origAvailableDeliveryDate"), WEIGHT("weight"), TOTAL_COST("totalCost"),
    SEQ_IN_ROUTE("seqInRoute"), ORIG_DEPARTURE("originDeparture"),
    COST_CENTER("costCenterCode"), UNIT_CODE("unitCode"), HAZMAT("hazmat"), CT_DESCRIPTION("ctDescription"),
    BILLER_TYPE("billerType"), REQUEST_ID("requestId"), DEFAULT_AP_TERMS("defaultApTerms"), MAX_AP_DUEDAYS("maxApDuedays"),
    AP_DUEDAYS("apDueDays"), FRT_BILL_RECVD_DATE("frtBillRecvDate"), EQUIPMENT_TYPE("eqType"),
    JOB_NUMBER("jobNumbers"), JOB_PERCENTS("jobPcts"), AP_TERMS("apTerms"),
    NOTE("note"), CUSTOMER_ORG_ID("customerOrgId"), LOCATION_ID("locationId"), LOCATION_NAME("locationName"), CREATION_DATE("creationDate"),
    FREIGHT_CLASS("commdityClass"), SO_NUMBER("soNumber"), SOURCE_IND("sourceInd"), LOAD_CREATED_BY_USER_ID("loadCreatedByUserId"), LOAD_CREATED_DATE("loadCreatedDate");

    private String aliasName;

    FinanceColumnAlias(String aliasName) {
        this.aliasName = aliasName;
    }

    /**
     * Return alias index.
     *
     * @param aliases query aliases
     * @return index as an int
     */
    public int getAliasIndex(String[] aliases) {
        return ArrayUtils.indexOf(aliases, aliasName);
    }
}
