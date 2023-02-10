package com.pls.shipment.service.product.impl.fileimport;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.pls.core.domain.enums.CommodityClass;
import org.apache.commons.lang3.StringUtils;

import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.parser.core.BaseDocumentParser;
import com.pls.core.service.fileimport.parser.core.Field;

/**
 * Implementation of {@link BaseDocumentParser} for parsing products.
 * 
 * @author Artem Arapov
 *
 */
public class ProductsDocumentParser extends BaseDocumentParser<LtlProductEntity, ProductFieldsDescription> {

    private static final String[] HAZMAT_FLAG_FALSE_VALUES = { "no", "false" };
    private static final String[] HAZMAT_FLAG_TRUE_VALUES = { "yes", "true" };
    private final Map<Double, CommodityClass> commodityClassValues = new HashMap<Double, CommodityClass>();

    @Override
    protected ProductFieldsDescription parseHeaderColumn(String headerString) {
        return ProductFieldsDescription.getFromHeaderText(headerString);
    }

    @Override
    protected LtlProductEntity parseRecord() throws ImportFileInvalidDataException {
        initCommodityClassValues();

        LtlProductEntity entity = new LtlProductEntity();
        entity.setDescription(readString(ProductFieldsDescription.DESCRIPTION));
        entity.setNmfcNum(readString(ProductFieldsDescription.NMFC_NUM));
        entity.setCommodityClass(readCommodityClass());
        entity.setProductCode(readString(ProductFieldsDescription.SKU));
        if (readHazmatFlag()) {
            LtlProductHazmatInfo hazmatInfo = new LtlProductHazmatInfo();
            hazmatInfo.setUnNumber(readString(ProductFieldsDescription.HAZMAT_UN));
            hazmatInfo.setPackingGroup(readString(ProductFieldsDescription.HAZMAT_PACKING_CODE));
            hazmatInfo.setHazmatClass(readString(ProductFieldsDescription.HAZMAT_CLASS));

            entity.setHazmatInfo(hazmatInfo);
        }

        return entity;
    }

    @Override
    protected void validateHeader(Collection<ProductFieldsDescription> headerData) throws ImportFileParseException {
        for (ProductFieldsDescription descriptor : headerData) {
            if (descriptor.isRequired() && !headerData.contains(descriptor)) {
                throw new ImportFileParseException("Column '" + descriptor.getHeader() + "' was not found on '"
                        + getPageName() + "' sheet.");
            }
        }
    }

    private String readString(ProductFieldsDescription headerId) throws ImportFileInvalidDataException {
        Field field = getColumnData(headerId);

        if (field.isEmpty() && headerId.isRequired()) {
            throw new ImportFileInvalidDataException(prepareColumnNotFoundMessage(headerId));
        }

        return field.getString();
    }

    private String prepareColumnNotFoundMessage(ProductFieldsDescription field) {
        return "Column '" + field.getHeader() + "' was not found for '" + getRecordName() + "' row.";
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

    private CommodityClass readCommodityClass()
            throws ImportFileInvalidDataException {
        String stringValue = readString(ProductFieldsDescription.COMMODITY_CLASS);

        CommodityClass result = commodityClassValues.get(Double.valueOf(stringValue));
        if (result == null) {
            throw new ImportFileInvalidDataException("Invalid commodity class '" + stringValue + "' was found in '"
                    + getRecordName() + "' row.");
        }
        return result;
    }

    private boolean readHazmatFlag()
            throws ImportFileInvalidDataException {
        boolean result = false;
        String stringValue = readString(ProductFieldsDescription.HAZMAT_FLAG);

        if (hasValues(stringValue, HAZMAT_FLAG_TRUE_VALUES)) {
            result = true;
        } else if (hasValues(stringValue, HAZMAT_FLAG_FALSE_VALUES)) {
            result = false;
        } else {
            throw new ImportFileInvalidDataException("Invalid hazmat flag value '" + stringValue + "' was found in '"
                    + getRecordName() + "' row.");
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
}
