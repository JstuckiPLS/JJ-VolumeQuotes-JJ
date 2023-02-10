package com.pls.extint.service.impl.helper;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.pls.core.exception.ApplicationException;
import com.pls.extint.dao.ApiTypeDao;
import com.pls.extint.domain.ApiMetadataEntity;
import com.pls.extint.domain.enums.DataType;
import com.pls.extint.domain.enums.PLSFieldType;
import com.pls.extint.shared.ApiRequestVO;

/**
 * Base helper class for calling the Web services.
 * 
 * @author Pavani Challa
 * 
 */
public abstract class WebserviceHelper {
    public static final String ATTRIBUTE_TYPE = "ATTRIBUTE";

    public static final String HEADER_TYPE = "HEADER";

    public static final String BODY_TYPE = "BODY";

    private static final String EMPTY_STRING = "";

    private static final String NS = "web:";

    private static final String NAMESPACE_TAG = " xmlns:web=\"";

    @Autowired
    private ApiTypeDao apiTypeDao;

    /**
     * Creates the request object and calls the web service.
     * 
     * @param requestVO
     *            VO containing data for creating the request.
     * @return response received from the web service
     * @throws ApplicationException
     *             for any errors that occur during the web service call
     */
    public abstract String sendRequest(ApiRequestVO requestVO) throws ApplicationException;

    /**
     * Parses the metadata and returns the metadata for the category requested.
     * 
     * @param metadata
     *            metadata to be parsed
     * @param category
     *            category for which metadata is requested - HEADER/BODY
     * @return the metadata for the category requested.
     */
    protected List<ApiMetadataEntity> getMetadataForCategory(List<ApiMetadataEntity> metadata, String category) {
        List<ApiMetadataEntity> list = new ArrayList<ApiMetadataEntity>();
        for (ApiMetadataEntity entity : metadata) {
            if (category.equalsIgnoreCase(entity.getFieldType())) {
                list.add(entity);
            }
        }

        return list;
    }

    /**
     * Parses the metadata and writes it as XML.
     * 
     * @param metadata
     *            metadata to be parsed
     * @param entity
     *            PLS Entity from which data has to be added to request
     * @param requestVO
     *            Actual request object
     * @param builder
     *            String builder object to which the formatted xml has to be written
     * @param addNamespace
     *            boolean flag indicating whether namespace should be added to xml element
     * @throws Exception
     *             thrown for any unhandled exceptions while parsing the metadata
     * 
     */
    @SuppressWarnings("rawtypes")
    protected void parseMetadataAsXml(ApiMetadataEntity metadata, Object entity, ApiRequestVO requestVO, StringBuilder builder, Boolean addNamespace)
            throws Exception {
        if (!ATTRIBUTE_TYPE.equalsIgnoreCase(metadata.getFieldType())) {
            // If the data type is a LITERAL (for eg, the xml definition <?xml.version="1.0">), then the default
            // value will be written as it is without the api field name
            if (DataType.LITERAL == metadata.getDataType()) {
                builder.append(metadata.getDefaultValue());
            } else {
                buildXmlElementStartTag(metadata, builder, entity, requestVO, addNamespace);

                // PLS Field Parent should be entered only for the metadata where it has to be iterated over the
                // collection fields. This will be handled when multiple flag is handled.
                builder.append(getRequestValue(metadata, entity, requestVO));

                // If the multiple flag in the metadata is set to "Y", metadata need to be iterated for all the objects in the collection.
                if ("Y".equalsIgnoreCase(metadata.getMultiple())) {
                    for (Object childEntity : (Collection) PropertyUtils.getProperty(entity, metadata.getPlsFieldName())) {
                        parseMetadataAsXml(metadata.getChildren(), childEntity, requestVO, builder, addNamespace);
                    }
                } else {
                    if (metadata.getChildren() != null && !metadata.getChildren().isEmpty()) {
                        for (ApiMetadataEntity child : metadata.getChildren()) {
                            parseMetadataAsXml(child, entity, requestVO, builder, addNamespace);
                        }
                    }
                }

                buildElementEndTag(metadata, builder, addNamespace);
            }
        }
    }

    private void parseMetadataAsXml(List<ApiMetadataEntity> metadata, Object entity, ApiRequestVO requestVO, StringBuilder builder,
            boolean addNamespace) throws Exception {
        for (ApiMetadataEntity metadataEntity : metadata) {
            if (metadataEntity.getPlsFieldParent() != null && "request".equalsIgnoreCase(metadataEntity.getPlsFieldParent())) {
                parseMetadataAsXml(metadataEntity, requestVO, requestVO, builder, addNamespace);
            } else {
                parseMetadataAsXml(metadataEntity, entity, requestVO, builder, addNamespace);
            }
        }
    }

    private void buildXmlElementStartTag(ApiMetadataEntity metadata, StringBuilder builder, Object entity, ApiRequestVO requestVO,
            boolean addNamespace) throws Exception {
        if (!StringUtils.isEmpty(metadata.getApiFieldName())) {
            builder.append('<');
            if (metadata.getNamespace() != null && !metadata.getNamespace().isEmpty() && addNamespace) {
                builder.append(StringUtils.isEmpty(metadata.getNsElement()) ? NS : (metadata.getNsElement() + ":")).append(metadata.getApiFieldName())
                        .append(NAMESPACE_TAG).append(metadata.getNamespace()).append('\"');
            } else {
                builder.append(StringUtils.isEmpty(metadata.getNsElement()) ? EMPTY_STRING : (metadata.getNsElement() + ":")).append(
                        metadata.getApiFieldName());
            }

            addAttributesForElementTags(metadata, builder, entity, requestVO);

            builder.append('>');
        }

    }

    private void addAttributesForElementTags(ApiMetadataEntity metadata, StringBuilder builder, Object entity, ApiRequestVO requestVO)
            throws Exception {
        if (metadata.getChildren() != null && !metadata.getChildren().isEmpty()) {
            for (ApiMetadataEntity child : metadata.getChildren()) {
                if (ATTRIBUTE_TYPE.equalsIgnoreCase(child.getFieldType())) {
                    builder.append(' ').append(child.getApiFieldName()).append("=\"");
                    builder.append(getRequestValue(child, entity, requestVO));
                    builder.append("\" ");
                }
            }
        }
    }

    private void buildElementEndTag(ApiMetadataEntity metadata, StringBuilder builder, boolean addNamespace) {
        if (!StringUtils.isEmpty(metadata.getApiFieldName())) {
            if ((metadata.getNamespace() != null && !metadata.getNamespace().isEmpty()) && addNamespace) {
                builder.append("</").append(StringUtils.isEmpty(metadata.getNsElement()) ? NS : (metadata.getNsElement() + ":"))
                        .append(metadata.getApiFieldName()).append('>');
            } else {
                builder.append("</").append(StringUtils.isEmpty(metadata.getNsElement()) ? EMPTY_STRING : (metadata.getNsElement() + ":"))
                        .append(metadata.getApiFieldName()).append('>');
            }
        }
    }

    /**
     * Returns the value to be added to the request based on metadata data type.
     * 
     * @param metadata
     *            metadata of the API field
     * @param entity
     *            PLS entity from which value to be extracted based on the PLS field name given in the metadata
     * @param requestVO
     *            Actual request object
     * @return the value to be added
     * @throws Exception
     *             thrown for any unhandled exceptions while parsing the metadata
     */
    protected String getRequestValue(ApiMetadataEntity metadata, Object entity, ApiRequestVO requestVO) throws Exception {
        if (!StringUtils.isEmpty(metadata.getPlsFieldName()) || !StringUtils.isEmpty(metadata.getDefaultValue())) {
            if (DataType.STATIC_VALUE == metadata.getDataType()) {
                return metadata.getDefaultValue() == null ? EMPTY_STRING : metadata.getDefaultValue();
            } else {
                Object data = getNonStaticRequestValue(metadata, entity, requestVO);

                if (DataType.LOOKUP == metadata.getDataType() && data != null) {
                    return apiTypeDao.getLookupValue(metadata.getLookup(), requestVO.getCarrierOrgId(), data.toString(), true);
                }

                return data == null ? EMPTY_STRING : formatDataToString(metadata, data);
            }
        }
        return EMPTY_STRING;
    }

    private Object getNonStaticRequestValue(ApiMetadataEntity metadata, Object entity, ApiRequestVO requestVO) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        if (isWrapperType(entity.getClass()) || String.class.equals(entity.getClass())) {
            return entity.toString();
        } else {
            try {
                return PropertyUtils.getProperty(entity, metadata.getPlsFieldName());
            } catch (NoSuchMethodException ex) {
                // if entity is not same as the request object, then check if the property exists in the
                // request object
                if (requestVO != null && !entity.equals(requestVO)) {
                    return PropertyUtils.getProperty(requestVO, metadata.getPlsFieldName());
                }
            }
        }
        return null;
    }

    /**
     * Formats the PLS field value to String based on the field type and the format.
     * 
     * @param metadata
     *            API metadata for the xml element
     * @param data
     *            data to be formatted
     * @return the formatted data
     */
    protected String formatDataToString(ApiMetadataEntity metadata, Object data) {
        String value = null;
        if (!StringUtils.isEmpty(metadata.getApiFieldFormat())) {
            // Only date fields are handled as the format the date has to be written might be different for each API.
            if (PLSFieldType.DATE == metadata.getPlsFieldType() || data instanceof Date) {
                SimpleDateFormat formatter = new SimpleDateFormat(metadata.getApiFieldFormat(), Locale.getDefault());
                value = formatter.format(data);
            } else {
                value =  data.toString();
            }
        } else {
            value =  data.toString();
        }

        int startIndex = (metadata.getStartIndex() == null || metadata.getMaxLength() <= 0) ? 0 : (metadata.getStartIndex() - 1);
        if ((metadata.getMaxLength() == null) || (metadata.getMaxLength() <= 0) || (value.length() < (startIndex + metadata.getMaxLength()))) {
            return value.substring(startIndex);
        } else {
            return value.substring(startIndex, startIndex + metadata.getMaxLength());
        }
    }

    /**
     * Determines whether the class is a Wrapper type.
     * 
     * @param clazz
     *            class to be compared
     * @return true if the class is a wrapper type
     */
    @SuppressWarnings("rawtypes")
    protected boolean isWrapperType(Class clazz) {
        return Boolean.class.equals(clazz) || Integer.class.equals(clazz) || Character.class.equals(clazz) || Byte.class.equals(clazz)
                || Short.class.equals(clazz) || Double.class.equals(clazz) || Long.class.equals(clazz) || Float.class.equals(clazz);
    }
}
