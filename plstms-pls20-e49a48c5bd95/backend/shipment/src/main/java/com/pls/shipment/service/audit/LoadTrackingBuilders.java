package com.pls.shipment.service.audit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.LoadNumbersEntity;
import com.pls.shipment.domain.LoadVendorBillEntity;
import com.pls.shipment.domain.enums.LoadEventType;

/**
 * A class, which contains a number of load tracking builders. For tracking of different fields in {@link LoadEntity}
 * @author Dmitriy Davydenko
 *
 */
public class LoadTrackingBuilders {

    private static final String VENDOR_BILL_TEMPLATE = "Vendor Bill received. Number: %s, Date: %s";

    private static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy h:mm a";

    private static final String[] EMPTY_ARRAY = new String[0];

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadEventBuilder.class);

    private static final String EMPTY_STRING = "";

    /**
     * Builder that produce List of {@link LoadEventEntity} for {@link LoadEventBuilder} by tracking <code>organization</code> field.
     */
    public static class LoadTrackingOrgaizationBuilder implements LoadTrackingFieldBuilder {
        @Override
        public List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue) {
            String oldOrganization = ((CustomerEntity) oldValue).getName();
            Long newOrgId = ((CustomerEntity) newValue).getId();
            CustomerEntity newCustomer = (CustomerEntity) session.get(CustomerEntity.class, newOrgId);
            String[] values = ArrayUtils.toArray(fieldName, oldOrganization, newCustomer.getName());
            return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(entity.getId(), LoadEventType.LOADCHG, values));
        }
    }
    /**
     * Builder that produce List of {@link LoadEventEntity} for {@link LoadEventBuilder} by tracking <code>carrier</code> field.
     */
    public static class LoadTrackingCarrierBuilder implements LoadTrackingFieldBuilder {
        @Override
        public List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue) {
            String oldOrganization = oldValue != null ? ((OrganizationEntity) oldValue).getName() : "";
            String newOrganization = newValue != null ? ((OrganizationEntity) newValue).getName() : "";
            String[] values = ArrayUtils.toArray(fieldName, oldOrganization, newOrganization);
            return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(entity.getId(), LoadEventType.LOADCHG, values));
        }
    }
    /**
     * Builder that produce List of {@link LoadEventEntity} for {@link LoadEventBuilder} by tracking <code>billTo</code> field.
     */
    public static class LoadTrackingBillToBuilder implements LoadTrackingFieldBuilder {
        @Override
        public List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue) {
            String oldBillToName = ((BillToEntity) oldValue).getName();
            String newBillToName = ((BillToEntity) newValue).getName();
            String[] values = ArrayUtils.toArray(fieldName, oldBillToName, newBillToName);

            return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(entity.getId(), LoadEventType.LOADCHG, values));
        }
    }
    /**
     * Builder that produce List of {@link LoadEventEntity} for {@link LoadEventBuilder} by tracking <code>billTo</code> field.
     */
    public static class LoadTrackingStatusBuilder implements LoadTrackingFieldBuilder {
        @Override
        public List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue) {
            ShipmentStatus oldStatus = (ShipmentStatus) oldValue;
            ShipmentStatus newStatus = (ShipmentStatus) newValue;
            String[] values = ArrayUtils.toArray(fieldName, oldStatus.getDescription(), newStatus.getDescription());

            return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(entity.getId(), LoadEventType.LOADCHG, values));
        }
    }
    /**
     * Builder that produce List of {@link LoadEventEntity} for {@link LoadEventBuilder} by tracking <code>finalizationStatus</code> field.
     */
    public static class LoadTrackingFinalizationStatusBuilder implements LoadTrackingFieldBuilder {
        @Override
        public List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue) {
            ShipmentFinancialStatus oldStatus = (ShipmentFinancialStatus) oldValue;
            ShipmentFinancialStatus newStatus = (ShipmentFinancialStatus) newValue;

            if (newStatus != null && newStatus.equals(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE)) {
                String[] values = ArrayUtils.toArray(fieldName, oldStatus.getStatusCode(), newStatus.getStatusCode());
                return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(entity.getId(), LoadEventType.LOADCHG, values));
            }

            if (oldStatus != null && newStatus != null) {
                if (oldStatus.equals(ShipmentFinancialStatus.FINANCE_HOLD)
                        && !newStatus.equals(ShipmentFinancialStatus.FINANCE_HOLD)) {
                    return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(
                            entity.getId(), LoadEventType.FS_ALLOW, new String[0]));
                }

                if (!oldStatus.equals(ShipmentFinancialStatus.FINANCE_HOLD)
                        && newStatus.equals(ShipmentFinancialStatus.FINANCE_HOLD)) {
                    return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(
                            entity.getId(), LoadEventType.FS_HOLD, new String[0]));
                }
            }

            return null;
        }
    }
    /**
     * Builder that produce List of {@link LoadEventEntity} for {@link LoadEventBuilder} by tracking <code>frtBillRecvFlag</code> field.
     */
    public static class LoadTrackingFrtBillRecvFlagBuilder implements LoadTrackingFieldBuilder {
        @Override
        public List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue) {
            Boolean newStatus = (Boolean) newValue;

            if (newValue != null && newStatus.equals(Boolean.TRUE)) {
                String billNumber = entity.getVendorBillDetails().getFrtBillNumber();
                String billDate = DateFormatUtils.format(entity.getVendorBillDetails().getFrtBillRecvDate(), DATE_FORMAT_PATTERN);
                String value = String.format(VENDOR_BILL_TEMPLATE, billNumber, billDate);

                return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(entity.getId(),
                            LoadEventType.CARTRKNOTE, ArrayUtils.toArray(value)));
            }

            return null;
        }
    }
    /**
     * Builder that produce List of {@link LoadEventEntity} for {@link LoadEventBuilder} by tracking <code>vendorBillDetails</code> field.
     */
    public static class LoadTrackingVendorBillDetailsBuilder implements LoadTrackingFieldBuilder {
        @Override
        public List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue) {

            Optional<LoadVendorBillEntity> vendorBillEntity = Optional.fromNullable(entity.getVendorBillDetails());
            if (vendorBillEntity.isPresent()) {
                boolean wasVendorBillAttached = entity.getVendorBillDetails().getFrtBillRecvFlag();

                LoadVendorBillEntity oldVal = (LoadVendorBillEntity) oldValue;
                LoadVendorBillEntity newVal = (LoadVendorBillEntity) newValue;

                if (oldVal.getFrtBillRecvFlag() != newVal.getFrtBillRecvFlag()) {
                    if (wasVendorBillAttached) {
                        return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(entity.getId(),
                                LoadEventType.LD_ATT, EMPTY_ARRAY));
                    } else {
                        return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(entity.getId(),
                                LoadEventType.LD_DET, EMPTY_ARRAY));
                    }
                }
            }

            return null;
        }
    }
    /**
     * Builder that produce List of {@link LoadEventEntity} for {@link LoadEventBuilder} by tracking <code>paymentTerms</code> field.
     */
    public static class LoadTrackingPaymentTermsBuilder implements LoadTrackingFieldBuilder {
        @Override
        public List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue) {
            PaymentTerms oldPayTerms = (PaymentTerms) oldValue;
            PaymentTerms newPayTerms = (PaymentTerms) newValue;
            String[] values = ArrayUtils.toArray(fieldName, oldPayTerms.getDescription(), newPayTerms.getDescription());

            return LoadEventBuilder.buildListOfLoadEventEntity(LoadEventBuilder.buildEventEntity(entity.getId(), LoadEventType.LOADCHG, values));
        }
    }
    /**
     * Builder that produce List of {@link LoadEventEntity} for {@link LoadEventBuilder} by tracking <code>paymentTerms</code> field.
     */
    public static class LoadTrackingNumbersBuilder implements LoadTrackingFieldBuilder {
        @Override
        public List<LoadEventEntity> build(LoadEntity entity, Session session, String fieldName, Object oldValue, Object newValue) {
            if (entity.getNumbers() != null) {
                LoadNumbersEntity oldValueEntity = (LoadNumbersEntity) oldValue;
                LoadNumbersEntity newValueEntity = (LoadNumbersEntity) newValue;
                List<LoadEventEntity> listOfChangedValuesEvents = new ArrayList<LoadEventEntity>();
                for (Field field : LoadNumbersEntity.class.getDeclaredFields()) {
                    field.setAccessible(true);

                    String oldFieldValue = getFieldValueAsString(field, oldValueEntity);
                    String newFieldValue = getFieldValueAsString(field, newValueEntity);

                    if (field.getType().equals(String.class) && !oldFieldValue.equals(newFieldValue)) {
                        String properFieldName = getProperFieldName(field.getName());
                        if (properFieldName != null) {
                            String[] values = ArrayUtils.toArray(properFieldName, oldFieldValue, newFieldValue);
                            listOfChangedValuesEvents.add(LoadEventBuilder.buildEventEntity(entity.getId(), LoadEventType.LOADCHG, values));
                        }
                    }
                }
                return listOfChangedValuesEvents;
            }
            return null;
        }
        private static String getProperFieldName(String fieldName) {
            LoadTrackingFields field = LoadTrackingFields.getByFieldName(fieldName);
            return field == null ? null : field.getAbbreviation();
        }

        private static String getFieldValueAsString(Field field, LoadNumbersEntity valueEntity) {
            String value = EMPTY_STRING;
            try {
                if (field.get(valueEntity) != null) {
                    value = field.get(valueEntity).toString();
                }
            } catch (IllegalAccessException e) {
                LOGGER.warn("An Exception occured, trying reflectively get a value of '" + field.getName()
                            + "' field, without having an access to this field." + e);
            }
            return value;
        }
    }

}

