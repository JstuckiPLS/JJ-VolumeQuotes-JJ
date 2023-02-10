package com.pls.shipment.service.product.impl.fileimport;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.service.fileimport.parser.core.Field;
import com.pls.core.service.fileimport.parser.core.Record;
import com.pls.core.service.fileimport.parser.core.RecordParser;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;

/**
 * Implementation of {@link RecordParser}.
 * 
 * @author Artem Arapov
 *
 */
public class ProductRecordParser extends RecordParser<LtlProductEntity, ProductFieldsDescription> {

    private static final int PHONE_NUMBER_LENGTH = 13;

    private static final String[] BOOLEAN_FLAG_FALSE_VALUES = { "no", "false" };

    private static final String[] BOOLEAN_FLAG_TRUE_VALUES = { "yes", "true" };

    private final Map<Double, CommodityClass> commodityClassValues = new HashMap<Double, CommodityClass>();

    @Override
    public LtlProductEntity parseRecord(Record record) throws ImportFileInvalidDataException {
        initCommodityClassValues();

        LtlProductEntity entity = new LtlProductEntity();
        entity.setDescription(readString(record, ProductFieldsDescription.DESCRIPTION));
        entity.setNmfcNum(readString(record, ProductFieldsDescription.NMFC_NUM));
        entity.setCommodityClass(readCommodityClass(record));
        entity.setProductCode(readString(record, ProductFieldsDescription.SKU));
        entity.setShared(readBooleanFlag(record, ProductFieldsDescription.SHARED));
        if (readBooleanFlag(record, ProductFieldsDescription.HAZMAT_FLAG)) {
            LtlProductHazmatInfo hazmatInfo = new LtlProductHazmatInfo();
            hazmatInfo.setUnNumber(readString(record, ProductFieldsDescription.HAZMAT_UN));
            hazmatInfo.setPackingGroup(readString(record, ProductFieldsDescription.HAZMAT_PACKING_CODE));
            hazmatInfo.setHazmatClass(readHazmatClass(record, ProductFieldsDescription.HAZMAT_CLASS));

            hazmatInfo.setEmergencyCompany(readString(record, ProductFieldsDescription.HAZMAT_EMERGENCY_COMPANY));
            hazmatInfo.setEmergencyPhone(readPhone(record, ProductFieldsDescription.HAZMAT_EMERGENCY_PHONE));
            if (hazmatInfo.getEmergencyPhone() != null) {
                hazmatInfo.getEmergencyPhone().setExtension(readString(record, ProductFieldsDescription.EXTENSION));
            }
            hazmatInfo.setEmergencyContract(readString(record, ProductFieldsDescription.HAZMAT_EMERGENCY_CONTRACT_NUM));
            hazmatInfo.setInstructions(readString(record, ProductFieldsDescription.HAZMAT_INSTRUCTIONS));
            entity.setHazmatInfo(hazmatInfo);
        }

        return entity;
    }

    private void initCommodityClassValues() throws ImportFileInvalidDataException {
        try {
            if (commodityClassValues.isEmpty()) {
                NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                for (CommodityClass commodityClass : CommodityClass.values()) {
                    double doubleValue = formatter.parse(commodityClass.getDbCode()).doubleValue();
                    commodityClassValues.put(doubleValue, commodityClass);
                }
            }
        } catch (ParseException e) {
            throw new ImportFileInvalidDataException("Unable to init commodity class parser", e);
        }
    }

    private String readHazmatClass(Record record, ProductFieldsDescription headerId) throws ImportFileInvalidDataException {
        String hazmatClass = readString(record, headerId).trim();
        if (!hazmatClass.matches("^[0-9][.]?[0-9]*$")) {
            throw new ImportFileInvalidDataException("Invalid hazmat class '" + hazmatClass + "' was found in '"
                    + record.getName() + "' row.");
        }
        return hazmatClass;
    }

    private String readString(Record record, ProductFieldsDescription headerId) throws ImportFileInvalidDataException {
        Field field = getColumnData(record, headerId);

        // hazmat field should be 'no' if field is empty
        if (field.isEmpty() && (headerId.equals(ProductFieldsDescription.HAZMAT_FLAG) || headerId.equals(ProductFieldsDescription.SHARED))) {
            return BOOLEAN_FLAG_FALSE_VALUES[0];
        } else if (field.isEmpty() && headerId.isRequired()) {
            throw new ImportFileInvalidDataException(prepareColumnNotFoundMessage(record, headerId));
        }

        String value = field.getString();
        if (value != null && headerId.getMaxLength() != null && headerId.getMaxLength() < value.length()) {
            throw new ImportFileInvalidDataException("Invalid size for field");
        }
        return value;
    }

    private PhoneEmbeddableObject readPhone(Record record, ProductFieldsDescription headerId) throws ImportFileInvalidDataException {
        String number = readString(record, headerId);

        if (StringUtils.isBlank(number)) {
            return null;
        }

        number = number.replaceAll("[^a-zA-Z0-9//]", "");

        //Remove extension
        if (number.indexOf('x') != -1) {
            number =  number.substring(0, number.indexOf('x'));
        }

        int phoneLength = number.length();

        if (phoneLength < 10) {
            throw new ImportFileInvalidDataException("Invalid Phone Format");
        }

        if (phoneLength > PHONE_NUMBER_LENGTH) {
            number = number.substring(0, PHONE_NUMBER_LENGTH);
            phoneLength = PHONE_NUMBER_LENGTH;
        }
        String phoneNumber = number.substring(phoneLength - 7, phoneLength);
        String areaCodeNumber = number.substring(phoneLength - 10, phoneLength - 7);
        String countryCodeNumber = "001";
        if (phoneLength > 10) {
            countryCodeNumber = number.substring(0, phoneLength - 10);
        }

        PhoneEmbeddableObject object = new PhoneEmbeddableObject();
        object.setCountryCode(countryCodeNumber);
        object.setAreaCode(areaCodeNumber);
        object.setNumber(phoneNumber);

        return object;
    }


    private String prepareColumnNotFoundMessage(Record record, ProductFieldsDescription field) {
        return "Column '" + field.getHeader() + "' was not found for '" + record.getName() + "' row.";
    }

    private CommodityClass readCommodityClass(Record record)
            throws ImportFileInvalidDataException {
        String stringValue = readString(record, ProductFieldsDescription.COMMODITY_CLASS);

        CommodityClass result = commodityClassValues.get(Double.valueOf(stringValue));
        if (result == null) {
            throw new ImportFileInvalidDataException("Invalid commodity class '" + stringValue + "' was found in '"
                    + record.getName() + "' row.");
        }
        return result;
    }

    private boolean readBooleanFlag(Record record, ProductFieldsDescription field)
            throws ImportFileInvalidDataException {
        boolean result = false;
        String stringValue = readString(record, field);

        if (hasValues(stringValue, BOOLEAN_FLAG_TRUE_VALUES)) {
            result = true;
        } else if (hasValues(stringValue, BOOLEAN_FLAG_FALSE_VALUES)) {
            result = false;
        } else {
            throw new ImportFileInvalidDataException("Invalid hazmat flag value '" + stringValue + "' was found in '"
                    + record.getName() + "' row.");
        }

        return result;
    }

    private boolean hasValues(String stringValue, String[] values) {
        boolean result = false;
        for (String value : values) {
            if (StringUtils.equalsIgnoreCase(value, stringValue)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    protected ProductFieldsDescription parseHeaderColumn(String headerString) {
        String header = headerString.replace("\n", "");
        return ProductFieldsDescription.getFromHeaderText(header);
    }
}
