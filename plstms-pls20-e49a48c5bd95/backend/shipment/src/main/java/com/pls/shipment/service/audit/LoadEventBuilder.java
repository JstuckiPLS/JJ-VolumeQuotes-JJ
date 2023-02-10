package com.pls.shipment.service.audit;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventDataEntity;
import com.pls.shipment.domain.LoadEventDataPK;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.LoadVendorBillEntity;
import com.pls.shipment.domain.bo.AuditShipmentCostsBO;
import com.pls.shipment.domain.enums.LoadEventType;


/**
 * Builder to create {@link LoadEventEntity} for tracking load events.
 * 
 * @author Artem Arapov
 *
 */
public final class LoadEventBuilder {

    private static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy h:mm a";

    private static final String WRONG_ARGUMENT_EXCEPTION_TEMPLATE = "Wrong number of required fields for type %s. Expected: %d, but actual is %d";

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadEventBuilder.class);

    private static final String[] EMPTY_ARRAY = new String[0];

    private static Map<LoadTrackingFields, LoadTrackingFieldBuilder> buildersMap;

    static {
        buildersMap = new HashMap<LoadTrackingFields, LoadTrackingFieldBuilder>();
        buildersMap.put(LoadTrackingFields.ORGANIZATION, new LoadTrackingBuilders.LoadTrackingOrgaizationBuilder());
        buildersMap.put(LoadTrackingFields.CARRIER, new LoadTrackingBuilders.LoadTrackingCarrierBuilder());
        buildersMap.put(LoadTrackingFields.BILL_TO, new LoadTrackingBuilders.LoadTrackingBillToBuilder());
        buildersMap.put(LoadTrackingFields.STATUS, new LoadTrackingBuilders.LoadTrackingStatusBuilder());
        buildersMap.put(LoadTrackingFields.FINALIZATION_STATUS, new LoadTrackingBuilders.LoadTrackingFinalizationStatusBuilder());
        buildersMap.put(LoadTrackingFields.FRT_RECEIVE_FLAG, new LoadTrackingBuilders.LoadTrackingFrtBillRecvFlagBuilder());
        buildersMap.put(LoadTrackingFields.VENDOR_BILL_DETAILS, new LoadTrackingBuilders.LoadTrackingVendorBillDetailsBuilder());
        buildersMap.put(LoadTrackingFields.PAYMENT_TERMS, new LoadTrackingBuilders.LoadTrackingPaymentTermsBuilder());
        buildersMap.put(LoadTrackingFields.NUMBERS, new LoadTrackingBuilders.LoadTrackingNumbersBuilder());
    }

    private LoadEventBuilder() {

    }

    /**
     * Builder that produce List of {@link LoadEventEntity} depending on {@link LoadTrackingFields} which were passed as argument.
     * 
     * @param entity - Load entity.
     * @param session - Hibernate session.
     * @param field - tracking field of {@link LoadEntity} for which need to create load event.
     * @param oldValue - old value of tracking field.
     * @param newValue - new value of tracking field.
     * @return {@link LoadEventEntity} or <code>null</code> if it was provided by logic.
     */
    public static List<LoadEventEntity> buildLoadEventList(LoadEntity entity, Session session,
            LoadTrackingFields field, Object oldValue, Object newValue) {

        LoadTrackingFieldBuilder builder = buildersMap.get(field);
        if (builder != null) {
            return builder.build(entity, session, field.getDescription(), oldValue, newValue);
        }
        String[] values = ArrayUtils.toArray(field.getDescription(), ObjectUtils.toString(oldValue), ObjectUtils.toString(newValue));
        return buildListOfLoadEventEntity(buildEventEntity(entity.getId(), LoadEventType.LOADCHG, values));
    }

    /**
     * Builder that produce {@link LoadEventEntity} in depending of {@link LoadDetailsTrackingFields} what was corresponded as argument.
     * 
     * @param entity - Load detail.
     * @param field - tracking field of {@link LoadDetailsEntity} for which need to create load event.
     * @param oldValue - old value of tracking field.
     * @param newValue - new value of tracking field.
     * @return {@link LoadEventEntity}
     */
    public static Optional<LoadEventEntity> buildLoadDetailsEvent(LoadDetailsEntity entity,
            LoadDetailsTrackingFields field, Object oldValue, Object newValue) {
        Optional<LoadEventEntity> event = Optional.absent();
        switch (field) {
        case ADDRESS:
            event = Optional.of(buildAddressEvent(entity, oldValue, newValue));
            break;
        case DEPARTURE:
            event = Optional.of(buildPickupDeliveryDateEvent(entity, oldValue, newValue, true));
            break;
        case SCHEDULEDPICKUP:
            event = Optional.of(buildPickupDeliveryDateEvent(entity, oldValue, newValue, false));
            break;
        default:
            String[] values = ArrayUtils.toArray(field.getDescription(), ObjectUtils.toString(oldValue), ObjectUtils.toString(newValue));
            event = Optional.of(buildEventEntity(entity.getLoad().getId(), LoadEventType.LOADCHG, values));
            break;
        }

        return event;
    }

    /**
     * Produce {@link LoadEventEntity} for status event.
     * 
     * @param loadId - Id of Load.
     * @param fieldName - field name.
     * @param oldValue - old value of tracking field.
     * @param newValue - new value of tracking field.
     * @return {@link LoadEventEntity}
     */
    public static List<LoadEventEntity> buildStatusEvent(Long loadId, String fieldName, Object oldValue, Object newValue) {
        ShipmentStatus oldStatus = (ShipmentStatus) oldValue;
        ShipmentStatus newStatus = (ShipmentStatus) newValue;
        String[] values = ArrayUtils.toArray(fieldName, oldStatus.getDescription(), newStatus.getDescription());

        return buildListOfLoadEventEntity(buildEventEntity(loadId, LoadEventType.LOADCHG, values));
    }

    /**
     * Produce {@link LoadEventEntity} for financial status event.
     * 
     * @param loadId - Load id.
     * @param invoiceType - CBI/TRANSACTIONAL.
     * @param loadStatus - financial status of the load
     * @param code - code for the note
     * @param note - text of the note
     * @return {@link LoadEventEntity}
     */
    public static LoadEventEntity buildFinancialStatusEvent(Long loadId, InvoiceType invoiceType, ShipmentFinancialStatus loadStatus, String code,
            String note) {
        String[] values = null;
        LoadEventType type = null;
        switch (loadStatus) {
            case ACCOUNTING_BILLING:
                type = LoadEventType.LD_MV;
                values = getInvoiceString(invoiceType);
                break;
            case ACCOUNTING_BILLING_HOLD:
                if (code != null) {
                    type = LoadEventType.LD_MV_RSN;
                    values = ArrayUtils.toArray("Invoice Audit", code, note);
                } else {
                    type = LoadEventType.LD_MV;
                    values = ArrayUtils.toArray("Invoice Audit");
                }
                break;
            case PRICING_AUDIT_HOLD:
                type = LoadEventType.LD_MV_RSN;
                values = ArrayUtils.toArray("Billing Hold", code, note);
                break;
            default:
                break;
        }

        return buildEventEntity(loadId, type, values);
    }

    /**
     * Builds the invoice failed event.
     *
     * @param loadId the load id
     * @param comment the comment
     * @return the load event entity
     */
    public static LoadEventEntity buildInvoiceFailedEvent(Long loadId, String comment) {
        String safeComment = comment;
        if (comment == null) {
            safeComment = "";
        }
        return buildEventEntity(loadId, LoadEventType.LD_MV_RSN, ArrayUtils.toArray("Invoice Audit", "Invoice Failed", safeComment));
    }

    /**
     * Gets the invoice string.
     *
     * @param invoiceType the invoice type
     * @return the invoice string
     */
    public static String[] getInvoiceString(InvoiceType invoiceType) {
        switch (invoiceType) {
            case TRANSACTIONAL:
                return ArrayUtils.toArray("Transactional Invoices");
            case CBI:
                return ArrayUtils.toArray("Consolidated Invoices");
            default:
                LOGGER.warn("Unknown invoice type : " + invoiceType);
                return null;
        }
    }

    /**
     * Produce {@link LoadEventEntity} for load source.
     * 
     * @param loadId - Id of load
     * @param value - source of load
     * @return {@link LoadEventEntity}
     */
    public static LoadEventEntity buildSourceEvent(Long loadId, String value) {
        return buildEventEntity(loadId, LoadEventType.LD_SRC, ArrayUtils.toArray(value));
    }
    
    /**
     * Produce {@link LoadEventEntity} for load's original quote number.
     * 
     * @param loadId - Id of load
     * @param value - original quote number of the load
     * @return {@link LoadEventEntity}
     */
    public static LoadEventEntity buildQuoteNumberEvent(Long loadId, String value) {
        return buildEventEntity(loadId, LoadEventType.LD_QUOTENUM, ArrayUtils.toArray(value));
    }

    /**
     * Produce {@link LoadEventEntity} for ignoring the EDI update message.
     * 
     * @param loadId - id of the load
     * @return {@link LoadEventEntity}
     */
    public static LoadEventEntity buildEdiIgnoreEvent(Long loadId) {
        return buildEventEntity(loadId, LoadEventType.EDI204_IGNORE, new String[0]);
    }

    /**
     * Produce {@link LoadEventEntity} if vendor bill was attached or detached.
     * 
     * @param entity - Not <code>null</code> instance of {@link LoadEntity}
     * @return {@link LoadEventEntity}
     */
    public static Optional<LoadEventEntity> buildInitialVendorBillEvent(LoadEntity entity) {
        Optional<LoadVendorBillEntity> optVendorBill = Optional.fromNullable(entity.getVendorBillDetails());

        if (optVendorBill.isPresent()) {
            if (optVendorBill.get().getFrtBillRecvFlag()) {
                return Optional.of(buildEventEntity(entity.getId(), LoadEventType.LD_ATT, EMPTY_ARRAY));
            } else {
                return Optional.of(buildEventEntity(entity.getId(), LoadEventType.LD_DET, EMPTY_ARRAY));
            }
        }

        return Optional.absent();
    }

    /**
     * Produce {@link LoadEventEntity} with specified type.
     * 
     * @param loadId - Id of load
     * @param eventType - Event Type
     * @param values - Values which should be logged
     * @return {@link LoadEventEntity}
     */
    public static LoadEventEntity buildEventEntity(Long loadId, LoadEventType eventType, String[] values) {
        if (eventType.getRequiredFields() != values.length) {
            throw new IllegalArgumentException(String.format(WRONG_ARGUMENT_EXCEPTION_TEMPLATE, eventType.toString(),
                    eventType.getRequiredFields(), values.length));
        }

        LoadEventEntity event = buildEvent(loadId, eventType);
        List<LoadEventDataEntity> dataList = new ArrayList<LoadEventDataEntity>();
        for (int i = 0; i < values.length; i++) {
            dataList.add(buildLoadEventData(event, i, values[i]));
        }

        event.setData(dataList);

        return event;
    }

    /**
     * Builds the invoice update event.
     * 
     * @param loadId
     *            id of Load.
     * @param dto
     *            {@link AuditShipmentCostsBO}.
     * @param costDifference
     *            The difference, by which, has been updated cost.
     * @return {@link LoadEventEntity}
     */
    public static LoadEventEntity buildDisputeAndUpdateEvent(Long loadId, AuditShipmentCostsBO dto,
            BigDecimal costDifference) {
        String updateRevenue = NumberFormat.getCurrencyInstance().format(dto.getUpdateRevenueValue());
        String costDiff = NumberFormat.getCurrencyInstance().format(costDifference);
        switch (dto.getUpdateRevenue()) {
        case MARGIN_PERCENT:
            return buildEventEntity(loadId, LoadEventType.DU_MARGIN,
                    ArrayUtils.toArray(String.format("%s%%", dto.getUpdateRevenueValue()), costDiff));
        case MARGIN_VALUE:
            return buildEventEntity(loadId, LoadEventType.DU_MARGIN, ArrayUtils.toArray(updateRevenue, costDiff));
        case TOTAL_REVENUE_AMOUNT:
            return buildEventEntity(loadId, LoadEventType.DU_AMOUNT, ArrayUtils.toArray(updateRevenue, costDiff));
        case UPDATE_USING_COST_DIFF:
            return buildEventEntity(loadId, LoadEventType.DU_COST_DIFF, ArrayUtils.toArray(updateRevenue));
        case INVOICE_WITHOUT_MARKUP:
            return buildEventEntity(loadId, LoadEventType.DU_COST_DIFF, ArrayUtils.toArray(costDiff));
        default:
            return null;
        }
    }

    private static LoadEventEntity buildAddressEvent(LoadDetailsEntity entity, Object oldValue, Object newValue) {
        Long loadId = entity.getLoad().getId();
        String formattedOldValue = formatAddress((AddressEntity) oldValue);
        String formattedNewValue = formatAddress((AddressEntity) newValue);
        String[] values;

        if (entity.getLoadAction().equals(LoadAction.PICKUP)) {
            values = ArrayUtils.toArray("Origin Address", formattedOldValue, formattedNewValue);
        } else {
            values = ArrayUtils.toArray("Destination Address", formattedOldValue, formattedNewValue);
        }

        return buildEventEntity(loadId, LoadEventType.LOADCHG, values);
    }

    private static LoadEventEntity buildPickupDeliveryDateEvent(LoadDetailsEntity entity, Object oldValue, Object newValue, boolean isActual) {
        Long loadId = entity.getLoad().getId();

        String oldFormattedDate = formatDate(oldValue);
        String newFormattedDate = formatDate(newValue);
        StringBuilder dateBuilder = new StringBuilder()
                .append(isActual ? "Actual" : "Scheduled")
                .append(entity.getLoadAction().equals(LoadAction.PICKUP) ? " Pickup" : " Delivery")
                .append(" Date");

        return buildEventEntity(loadId, LoadEventType.LOADCHG, ArrayUtils.toArray(dateBuilder.toString(), oldFormattedDate, newFormattedDate));
    }

    private static String formatDate(Object oldDate) {
        return oldDate != null ? DateFormatUtils.format((Date) oldDate, DATE_FORMAT_PATTERN) : StringUtils.EMPTY;
    }

    private static String formatAddress(AddressEntity entity) {
        return StringUtils.join(new String[] {entity.getAddress1(), entity.getCity(),
                entity.getStateCode(), entity.getZip(), entity.getCountryCode()}, ",");
    }

    private static LoadEventEntity buildEvent(Long loadId, LoadEventType eventType) {
        LoadEventEntity event = new LoadEventEntity();
        event.setLoadId(loadId);
        event.setEventTypeCode(eventType.getDbCode());
        event.setFailure(false);

        return event;
    }

    private static LoadEventDataEntity buildLoadEventData(LoadEventEntity event, int ordinal, String data) {
        LoadEventDataPK dataEntityPK = new LoadEventDataPK();
        dataEntityPK.setOrdinal((byte) ordinal);
        dataEntityPK.setEvent(event);
        LoadEventDataEntity eventData = new LoadEventDataEntity();
        eventData.setEventDataPK(dataEntityPK);
        eventData.setDataType('S');
        eventData.setData(data == null ? "" : data.substring(0, Math.min(240, data.length())));// making sure it fits the column width.

        return eventData;
    }

    /**
     * Building the list of events entities.
     * @param eventEntity - {@link LoadEventEntity} which we are building the list from.
     * @return list of {@link LoadEventEntity}
     */
    static List<LoadEventEntity> buildListOfLoadEventEntity(LoadEventEntity eventEntity) {
        List<LoadEventEntity> eventList = new ArrayList<LoadEventEntity>();
        eventList.add(eventEntity);
        return eventList;
    }

}
