package com.pls.extint.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pls.core.exception.ApplicationException;
import com.pls.extint.domain.ApiExceptionEntity;
import com.pls.extint.domain.ApiMetadataEntity;
import com.pls.extint.domain.ApiTypeEntity;
import com.pls.extint.service.ApiExceptionsService;
import com.pls.extint.service.TrackingService;
import com.pls.extint.shared.TrackingRequestVO;
import com.pls.extint.shared.TrackingResponseVO;

/**
 * Implementation class of {@link TrackingService}.
 * 
 * @author Pavani Challa
 * 
 */
@Service
@Transactional(readOnly = true)
public class TrackingServiceImpl extends ApiServiceImpl<TrackingResponseVO, TrackingRequestVO> implements TrackingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackingServiceImpl.class);

    @Autowired
    private ApiExceptionsService apiExceptionsService;

    private static final String TRACKING_CATEGORY = "TRACKING";

    private static final String BY_PRO_NUMBER = "PRO";

    private static final String BY_BOL = "BOL";

    private static Set<String> exceptionFields = new HashSet<String>();

    /**
     * Method to set the field names for which exceptions had to be checked for after initializing the bean.
     */
    @PostConstruct
    public void setExceptionFields() {
        exceptionFields.add("weight");
        exceptionFields.add("pieces");
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public TrackingResponseVO getTrackingInformation(TrackingRequestVO requestVO) {
        List<ApiTypeEntity> apiTypes = apiTypeDao.findByCategory(requestVO.getCarrierOrgId(), requestVO.getShipperOrgId(), TRACKING_CATEGORY,
                CARRIER_ORG_TYPE);

        try {
            if (apiTypes != null && !apiTypes.isEmpty()) {
                for (ApiTypeEntity apiType : apiTypes) {
                    if ((requestVO.getCarrierRefNum() != null && BY_PRO_NUMBER.equalsIgnoreCase(apiType.getApiType()))
                            || (requestVO.getCarrierRefNum() == null && BY_BOL.equalsIgnoreCase(apiType.getApiType()))) {
                        requestVO.setApiType(apiType);
                        TrackingResponseVO response = processRequest(requestVO);
                        processResponse(requestVO, response);
                        return response;
                    }
                }
            }
        } catch (ApplicationException ae) {
            // Ignore the exception and continue with the next api call.
            LOGGER.error("An error occurred while retrieving tracking information from API", ae);
        }

        return null;
    }

    /**
     * Parse the response received from the web service.
     * 
     * @param requestVO
     *            Object containing request data.
     * @param wsResponse
     *            response from the web service
     * @return TrackingResponseVO the response object created from the web service response.
     * @throws ApplicationException
     *             thrown for any errors while parsing the response.
     */
    @Override
    protected TrackingResponseVO parseWsResponse(TrackingRequestVO requestVO, String wsResponse) throws ApplicationException {
        try {
            TrackingResponseVO responseVO = new TrackingResponseVO(requestVO);
            parseXmlResponse(requestVO, responseVO, wsResponse);

            if (!StringUtils.isEmpty(responseVO.getError()) || StringUtils.isEmpty(responseVO.getNote())) {
                updateLogStatusErrored(requestVO.getRequestLog(), responseVO.getError(), wsResponse);
                throw new ApplicationException(responseVO.getError());
            }

            requestVO.getRequestLog().setTrackingNote(responseVO.getNote());
            return responseVO;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Sets the value for the property in the entity. Looks for the property with PLSFieldName in the entity passed and sets the data after formatting
     * the value as per the PLS field type and pls data format. Uses the PropertyUtils class to set the data. Checks for mismatch in data between the
     * original value and value received from API and logs an exception if there is a mismatch.
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
        try {
            if (exceptionFields.contains(metadata.getPlsFieldName())) {
                Object oldValue = PropertyUtils.getNestedProperty(entity, metadata.getPlsFieldName());
                super.setData(metadata, entity, value, orgId);
                Object newValue = PropertyUtils.getNestedProperty(entity, metadata.getPlsFieldName());

                if (newValue != null && !newValue.equals(oldValue)) {
                    createApiException((TrackingResponseVO) entity, oldValue, newValue, metadata.getPlsFieldDescription());
                }
            } else {
                super.setData(metadata, entity, value, orgId);
            }
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage(), ex.getCause());
        }
    }

    private void createApiException(TrackingResponseVO response, Object oldValue, Object newValue, String fieldName) {
        ApiExceptionEntity entity = new ApiExceptionEntity();
        entity.setApiTypeId(response.getApiTypeId());
        entity.setCarrierReferenceNumber(response.getCarrierRefNum());
        entity.setLoadId(response.getLoadId());
        entity.setFieldName(fieldName);
        entity.setOldValue((oldValue != null) ? oldValue.toString() : null);
        entity.setNewValue((newValue != null) ? newValue.toString() : null);
        apiExceptionsService.save(entity);
    }

    private void processResponse(TrackingRequestVO request, TrackingResponseVO response) {
        if (!StringUtils.isEmpty(response.getCurrentStatus())) {
            String latestStatus = apiLogService.getLatestTrackingNote(response.getLoadId(), request.getRequestLog().getId());

            if (latestStatus != null && latestStatus.equalsIgnoreCase(response.getNote())) {
                response.setNote(null);
            }
        }
    }

}
