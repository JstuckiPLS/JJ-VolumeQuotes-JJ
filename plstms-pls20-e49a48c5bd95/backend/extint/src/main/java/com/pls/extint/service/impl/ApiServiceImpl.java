package com.pls.extint.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.pls.core.exception.ApplicationException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.extint.dao.ApiTypeDao;
import com.pls.extint.domain.ApiLogEntity;
import com.pls.extint.domain.ApiMetadataEntity;
import com.pls.extint.domain.enums.ApiRequestStatus;
import com.pls.extint.domain.enums.DataType;
import com.pls.extint.domain.enums.PLSFieldType;
import com.pls.extint.domain.enums.WebServiceType;
import com.pls.extint.service.ApiLogService;
import com.pls.extint.service.impl.helper.RestServiceHelper;
import com.pls.extint.service.impl.helper.SoapServiceHelper;
import com.pls.extint.service.impl.helper.WebserviceHelper;
import com.pls.extint.shared.ApiRequestVO;
import com.pls.extint.shared.ApiResponseVO;

/**
 * Base class for the services calling the API.
 * 
 * @param <T>
 *            type of response object returned.
 * @param <K>
 *            type of the request object
 * 
 * @author Pavani Challa
 * 
 */
public abstract class ApiServiceImpl<T extends ApiResponseVO, K extends ApiRequestVO> {

    @Autowired
    protected RestServiceHelper restHelper;

    @Autowired
    private SoapServiceHelper soapHelper;

    @Autowired
    protected ApiTypeDao apiTypeDao;

    @Autowired
    protected ApiLogService apiLogService;

    public static final String EMPTY_STRING = "";

    public static final String CARRIER_ORG_TYPE = "CARRIER";

    public static final String SHIPPER_ORG_TYPE = "SHIPPER";

    private static final Long DEFAULT_USER_ID = -1L;

    /**
     * Sends the request to the API and parses the response. Updates the Api log with appropriate statuses as required.
     * 
     * @param requestVO
     *            Object containing request data
     * @return the corresponding response object created from the web service response.
     * @throws ApplicationException
     *             for any errors while sending the request and parsing response from API.
     */
    protected T processRequest(K requestVO) throws ApplicationException {
        // Start logging the API request
        createApiRequestLog(requestVO.getRequestLog());

        try {
            String wsResponse = null;
            // Call the API
            if (WebServiceType.SOAP == requestVO.getApiType().getWsType()) {
                wsResponse = soapHelper.sendRequest(requestVO);
            } else {
                wsResponse = restHelper.sendRequest(requestVO);
            }

            // If the web service response is empty, mark the request as "Errored" and do not proceed.
            if (StringUtils.isEmpty(wsResponse)) {
                throw new ApplicationException("No response received from the API.");
            }

            // Update the request status to Processing. It means response is received and parsing the response is about to start.
            updateLogStatusProcessing(requestVO.getRequestLog(), wsResponse);

            // Parse the response.
            T response = parseWsResponse(requestVO, wsResponse);

            // Response is successfully parsed. Update the request status to completed.
            updateLogStatusCompleted(requestVO.getRequestLog());

            return response;
        } catch (ApplicationException ae) {
            // Get the stack trace from the exception to store as response.
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ae.printStackTrace(printWriter);
            printWriter.flush();

            updateLogStatusErrored(requestVO.getRequestLog(), ae.getMessage(), writer.toString());

            // Throw the exception back to the caller.
            throw ae;
        }
    }

    /**
     * Parse the response received from the web service.
     * 
     * @param requestVO
     *            Object containing request data.
     * @param wsResponse
     *            response from the web service
     * @return the corresponding response object created from the web service response.
     * @throws ApplicationException
     *             thrown for any errors while parsing the response.
     */
    protected abstract T parseWsResponse(K requestVO, String wsResponse) throws ApplicationException;

    /**
     * Parses the String using DOM Parser and returns the Document object from which the actual response will be extracted.
     * 
     * @param response
     *            response string to be parsed
     * @return the Document object
     * @throws ParserConfigurationException
     *             thrown for any errors while parsing
     * @throws IOException
     *             thrown for any errors while parsing
     * @throws SAXException
     *             thrown for any errors while parsing
     */
    protected Document getDOMDocument(String response) throws ParserConfigurationException, IOException, SAXException {
        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(response));
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputSource);
        doc.getDocumentElement().normalize();

        return doc;
    }

    /**
     * Response received from web service in xml format. Parse the nodes in xml and get the required data from the response.
     * 
     * @param requestVO
     *            request data sent to the API. It contains the response metadata needed to parse the response.
     * @param responseVO
     *            response object into which the values from API response has to be set.
     * @param wsResponse
     *            response received from the API
     * @throws Exception
     *             thrown when any exception occurs while parsing.
     */
    protected void parseXmlResponse(K requestVO, T responseVO, String wsResponse) throws Exception {
        // Convert the String response to an XML document
        Document doc = getDOMDocument(replaceBOMCharacters(wsResponse));

        if (requestVO.getApiType().getRespMetadata() != null) {
            for (ApiMetadataEntity metadata : requestVO.getApiType().getRespMetadata()) {

                // If the api field name is null in the metadata, then don't parse the XML document for this pls field.
                if (StringUtils.isEmpty(metadata.getApiFieldName())) {
                    parseResponseMetadata(metadata, responseVO, null);
                } else {
                    // If the multiple flag in metadata is not set, then get the first element in the node list and extract the value from it.
                    if ("Y".equalsIgnoreCase(metadata.getMultiple())) {
                        NodeList nList = doc.getElementsByTagName(metadata.getApiFieldName());
                        if (nList != null && nList.getLength() > 0) {
                            for (int index = 0; index < nList.getLength(); index++) {
                                Element element = (Element) nList.item(index);
                                parseResponseMetadata(metadata, responseVO, element);
                            }
                        }
                    } else {
                        Element element = (Element) doc.getElementsByTagName(metadata.getApiFieldName()).item(0);
                        parseResponseMetadata(metadata, responseVO, element);
                    }
                }
            }
        }
    }

    private void parseResponseMetadata(ApiMetadataEntity metadata, T responseVO, Element element) throws ApplicationException {
        // Ignore the metadata when the PLS field name is empty. This is response and data is not needed if PLS Field Name is empty.
        if (!StringUtils.isEmpty(metadata.getPlsFieldName()) && element != null) {
            setData(metadata, responseVO, element.getTextContent(), responseVO.getOrgId());
        } else if (!StringUtils.isEmpty(metadata.getPlsFieldName())) {
            setData(metadata, responseVO, null, responseVO.getOrgId());
        }

        // Parse the children even if PLS Field name is empty.
        if (metadata.getChildren() != null) {
            for (ApiMetadataEntity child : metadata.getChildren()) {
                if (StringUtils.isEmpty(child.getApiFieldName())) {
                    parseResponseMetadata(child, responseVO, null);
                } else if (element != null) {
                    parseChildElement(child, responseVO, element);
                }
            }
        }
    }

    private void parseChildElement(ApiMetadataEntity child, T responseVO, Element element) throws ApplicationException {
        // If the multiple flag in metadata is not set, then get the first element in the node list and extract the value from it.
        if ("Y".equalsIgnoreCase(child.getMultiple())) {
            NodeList nList = element.getElementsByTagName(child.getApiFieldName());
            if (nList != null && nList.getLength() > 0) {
                for (int index = 0; index < nList.getLength(); index++) {
                    Element childElement = (Element) nList.item(index);
                    parseResponseMetadata(child, responseVO, childElement);
                }
            }
        } else {
            // Parse the attributes for the element tag. Api field name and pls field name are mandatory for attribute metadata. Hence we
            // are parsing them them only when they are not null.
            if (WebserviceHelper.ATTRIBUTE_TYPE.equalsIgnoreCase(child.getFieldType()) && !StringUtils.isEmpty(child.getApiFieldName())
                    && !StringUtils.isEmpty(child.getPlsFieldName())) {
                setData(child, responseVO, element.getAttribute(child.getApiFieldName()), responseVO.getOrgId());
            } else {
                Element childElement = (Element) element.getElementsByTagName(child.getApiFieldName()).item(0);
                parseResponseMetadata(child, responseVO, childElement);
            }
        }
    }

    /**
     * Parses the value to the PLS field type from the format passed to the method.
     * 
     * @param value
     *            value to be parsed
     * @param fieldType
     *            PLS Field type
     * @param format
     *            format in which value is in
     * @return the parsed data
     * @throws ParseException
     *             thrown when value cannot be parsed to PLS Field type
     */
    protected Object parseValue(String value, PLSFieldType fieldType, String format) throws ParseException {
        String data = StringUtils.trim(value);

        if (!StringUtils.isEmpty(data) && fieldType != null) {
            switch (fieldType) {
            case DATE:
                return new SimpleDateFormat(format, Locale.getDefault()).parse(data);
            case LONG:
                return new Double(data).longValue();
            case INTEGER:
                return new Double(data).intValue();
            case DOUBLE:
                return Double.valueOf(data);
            default:
                // Do nothing
                break;
            }
        }

        // Default data type is String. If the format is null, we should be looking for first matching pattern in the value.
        if (!StringUtils.isEmpty(format) && !StringUtils.isEmpty(data)) {
            return findMatch(data, format);
        }
        return data;
    }

    private String findMatch(String value, String expression) {
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    /**
     * Sets the value for the property in the entity. Looks for the property with PLSFieldName in the entity passed and sets the data after formatting
     * the value as per the PLS field type and pls data format. Uses the PropertyUtils class to set the data.
     * 
     * @param metadata
     *            Api Metadata containing the details of the pls property to which the value has to be set.
     * @param entity
     *            Entity containing the property to which data has to be set
     * @param value
     *            value to be set. The value will be processed based on the data type of the metadata.
     * @param orgId
     *            Organization for which the API call is made
     * 
     * @throws ApplicationException
     *             thrown if the value cannot be parsed to the PLS field type or the PLS property cannot be accessed.
     */
    protected void setData(ApiMetadataEntity metadata, Object entity, String value, Long orgId) throws ApplicationException {
        Object data = value;

        try {
            if (metadata.getDataType() == DataType.STATIC_VALUE) {
                PropertyUtils.setProperty(entity, metadata.getPlsFieldName(), metadata.getDefaultValue().trim());
            } else if (metadata.getDataType() == DataType.URL && !StringUtils.isEmpty(value)) {
                byte[] bytes = restHelper.getDocument(value);
                PropertyUtils.setProperty(entity, metadata.getPlsFieldName(), (bytes == null ? EMPTY_STRING : bytes));
            } else {
                data = parseValue(value, metadata.getPlsFieldType(), metadata.getApiFieldFormat());

                if (data != null && !"".equals(data)) {
                    if (metadata.getDataType() == DataType.LOOKUP) {
                        data = apiTypeDao.getLookupValue(metadata.getLookup(), orgId, data.toString().trim(), false);
                    }
                    PropertyUtils.setProperty(entity, metadata.getPlsFieldName(), data);
                }
            }
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Creates an API_LOG record for the request. This will be initially created with the status IN_PROGRESS.
     * 
     * @param log
     *            log to be saved.
     */
    protected void createApiRequestLog(ApiLogEntity log) {
        log.setStatus(ApiRequestStatus.IN_PROGRESS);
        apiLogService.save(log);
    }

    /**
     * Updates the status of the API_LOG record to Open (it means that response is received from the API call and parsing of web service response is
     * about to start). This method saves the web service response from API call to the database.
     * 
     * @param log
     *            log to be saved.
     * @param wsResponse
     *            Response from web service to be updated in the log.
     */
    protected void updateLogStatusProcessing(ApiLogEntity log, String wsResponse) {
        log.setResponse(wsResponse);
        log.setStatus(ApiRequestStatus.OPEN);
        apiLogService.save(log);
    }

    /**
     * Updates the status of the API_LOG record to Completed. This means that the web service response is parsed as per the requirements and either it
     * is saved or will be returned to the caller.
     * 
     * @param log
     *            log to be saved.
     */
    protected void updateLogStatusCompleted(ApiLogEntity log) {
        log.setStatus(ApiRequestStatus.COMPLETED);
        apiLogService.save(log);
    }

    /**
     * Updates the status of the API_LOG record to Errored. The status is set to "Errored" if any error occurred during the entire process of creating
     * request object, calling the API and parsing the response from API. This method saves the brief description of the exception and the entire
     * stack trace to the database.
     * 
     * @param log
     *            log to be saved.
     * @param errorMsg
     *            brief description of the error
     * @param cause
     *            Root cause of the error.
     */
    protected void updateLogStatusErrored(ApiLogEntity log, String errorMsg, String cause) {
        log.setError(cause);
        if (!StringUtils.isEmpty(errorMsg)) {
            log.setErrorMsg(errorMsg.substring(0, errorMsg.length() > 1000 ? 1000 : errorMsg.length()));
        } else {
            log.setErrorMsg("An error occurred while calling API.");
        }
        log.setStatus(ApiRequestStatus.ERRORED);
        apiLogService.save(log);
    }

    private String replaceBOMCharacters(String wsResponse) {
        return wsResponse.replaceFirst("^([\\W]+)<", "<");
    }

    /**
     * Returns the current user.
     * 
     * @return the current user.
     */
    protected Long extractCurrentUserId() {
        Long result = SecurityUtils.getCurrentPersonId();
        return result == null ? DEFAULT_USER_ID : result;
    }
}