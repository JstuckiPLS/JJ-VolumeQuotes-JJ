package com.pls.shipment.service.product.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;

/**
 * Class to parse {@link LtlProductEntity} from {@link Row} data.
 * 
 * @author Maxim Medvedev
 */
public class DataParser {

    private static final String[] HAZMAT_FLAG_FALSE_VALUES = { "no", "false" };

    private static final String[] HAZMAT_FLAG_TRUE_VALUES = { "yes", "true" };
    private final Map<Double, CommodityClass> commodityClassValues = new HashMap<Double, CommodityClass>();

    private final HeaderData headerData = new HeaderData();

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Get headerData value.
     * 
     * @return the headerData.
     */
    public HeaderData getHeaderData() {
        return headerData;
    }

    /**
     * Parse {@link Row} data.
     * 
     * @param result
     *            Not <code>null</code> {@link List} that will be used as result storage.
     * @param row
     *            Not <code>null</code> {@link Row} instance.
     * 
     * @throws com.pls.core.exception.fileimport.ImportFileParseException
     *             when some unexpected exception during parsing happened.
     * @throws com.pls.core.exception.fileimport.ImportFileInvalidDataException
     *             when imported file contains some data on invalid format or required data is absent.
     */
    public void parseRow(List<LtlProductEntity> result, Row row) throws ImportFileParseException, ImportFileInvalidDataException {
        initCommodityClassValues();

        Map<ProductExcelFields, String> values = parseValues(row);
        String rowName = prepareRowName(row);
        if (values.isEmpty()) {
            log.debug("Empty row  '{}' was skipped", rowName);
        } else {
            LtlProductEntity entity = new LtlProductEntity();
            entity.setDescription(readString(values, ProductExcelFields.DESCRIPTION, true, rowName));

            entity.setNmfcNum(readString(values, ProductExcelFields.NMFC_NUM, false, rowName));
            entity.setNmfcSubNum(readString(values, ProductExcelFields.NMFC_SUB_NUM, false, rowName));
            entity.setCommodityClass(readCommodityClass(values, rowName));
            entity.setProductCode(readString(values, ProductExcelFields.SKU, false, rowName));

            entity.setHazmatInfo(null);
            if (readHazmatFlag(values, rowName)) {
                LtlProductHazmatInfo hazmatInfo = new LtlProductHazmatInfo();

                hazmatInfo.setUnNumber(readString(values, ProductExcelFields.HAZMAT_UN, true, rowName));
                hazmatInfo.setPackingGroup(readString(values, ProductExcelFields.HAZMAT_PACKING_CODE, true, rowName));
                hazmatInfo.setEmergencyCompany(readString(values, ProductExcelFields.HAZMAT_COMPANY, true, rowName));
                hazmatInfo.setEmergencyContract(readString(values, ProductExcelFields.HAZMAT_CONTRACT, true, rowName));
                hazmatInfo.setEmergencyPhone(parsePhone(readString(values, ProductExcelFields.HAZMAT_PHONE, true, rowName)));

                // TODO HazmatClass

                entity.setHazmatInfo(hazmatInfo);
            }

            result.add(entity);
        }
    }
    /**
     * Transforms a String formatted phone number into a PhoneEmbeddableObject.
     * @param phoneString the phone number to be converted.
     * @return PhoneEmbaddableObject the phone object that has been created.
     */
    private PhoneEmbeddableObject parsePhone(String phoneString) {
        PhoneEmbeddableObject phoneEntity = new PhoneEmbeddableObject();
        if (phoneString != null && !phoneString.isEmpty()) {
            String phone = phoneString.replaceAll("[ +-]", "");
            if (phone.matches("^\\d+\\(\\d+\\).*$")) {
                phoneEntity.setCountryCode(phone.substring(0, phone.indexOf('(')));
                phone = phone.substring(phone.indexOf('('));
            }
            if (phone.matches("^\\(\\d+\\).*$")) {
                phoneEntity.setAreaCode(phone.substring(1, phone.indexOf(')')));
                phone = phone.substring(phone.indexOf(')') + 1);
            }
            phoneEntity.setNumber(phone);
        }
        return phoneEntity;
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

    private void initCommodityClassValues() throws ImportFileParseException {
        try {
            if (commodityClassValues.isEmpty()) {
                NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                for (CommodityClass commodityClass : CommodityClass.values()) {
                    double doubleValue = formatter.parse(commodityClass.getDbCode()).doubleValue();
                    commodityClassValues.put(doubleValue, commodityClass);
                }
            }
        } catch (ParseException e) {
            throw new ImportFileParseException("Unable to init commodity class parser", e);
        }
    }

    @SuppressWarnings("deprecation")
    private Map<ProductExcelFields, String> parseValues(Row row) throws ImportFileInvalidDataException {
        Map<ProductExcelFields, String> result = new HashMap<ProductExcelFields, String>();
        for (Entry<ProductExcelFields, Integer> entry : headerData.entrySet()) {
            Cell cell = row.getCell(entry.getValue(), MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                switch (cell.getCellTypeEnum()) {
                case STRING:
                    String value = cell.getStringCellValue();
                    if (StringUtils.isNotBlank(value)) {
                        result.put(entry.getKey(), value.trim());
                    }
                    break;

                case BOOLEAN:
                    result.put(entry.getKey(), String.valueOf(cell.getBooleanCellValue()));
                    break;

                case NUMERIC:
                    result.put(entry.getKey(), String.valueOf(cell.getNumericCellValue()));
                    break;

                default:
                    throw new ImportFileInvalidDataException("Cell '" + new CellReference(cell).formatAsString()
                            + "' has invalid type.");
                }
            }
        }

        return result;
    }

    private String prepareRowName(Row row) {
        return row.getSheet().getSheetName() + ":" + CellReference.convertNumToColString(row.getRowNum());
    }

    private CommodityClass readCommodityClass(Map<ProductExcelFields, String> values, String rowName)
            throws ImportFileInvalidDataException {
        String stringValue = readString(values, ProductExcelFields.COMMODITY_CLASS, true, rowName);

        CommodityClass result = commodityClassValues.get(Double.valueOf(stringValue));
        if (result == null) {
            throw new ImportFileInvalidDataException("Invalid commodity class '" + stringValue + "' was found in '"
                    + rowName + "' row.");
        }
        return result;
    }

    private boolean readHazmatFlag(Map<ProductExcelFields, String> values, String rowName)
            throws ImportFileInvalidDataException {
        boolean result = false;
        String stringValue = readString(values, ProductExcelFields.HAZMAT_FLAG, false, rowName);

        if (hasValues(stringValue, HAZMAT_FLAG_TRUE_VALUES)) {
            result = true;
        } else if (hasValues(stringValue, HAZMAT_FLAG_FALSE_VALUES)) {
            result = false;
        } else {
            throw new ImportFileInvalidDataException("Invalid hazmat flag value '" + stringValue + "' was found in '"
                    + rowName + "' row.");
        }

        return result;
    }

    private String readString(Map<ProductExcelFields, String> values, ProductExcelFields field, boolean mandatory, String rowName)
            throws ImportFileInvalidDataException {
        String result = StringUtils.trimToNull(values.get(field));

        if (result == null && mandatory) {
            throw new ImportFileInvalidDataException("Value '" + field.getHeader() + "' was not found for '" + rowName
                    + "' row.");
        }
        return result;
    }
}