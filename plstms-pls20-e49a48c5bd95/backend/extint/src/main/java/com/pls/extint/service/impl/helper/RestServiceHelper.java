package com.pls.extint.service.impl.helper;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pls.core.exception.ApplicationException;
import com.pls.extint.domain.ApiMetadataEntity;
import com.pls.extint.domain.enums.DataType;
import com.pls.extint.shared.ApiRequestVO;

/**
 * Helper class for calling the API provided as REST Web Service.
 * 
 * @author Pavani Challa
 * 
 */
@Component
public class RestServiceHelper extends WebserviceHelper {

    @Autowired
    private RestTemplateFactory factory;

    // This field should be used when you are downloading a document as bytes.
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String sendRequest(ApiRequestVO requestVO) throws ApplicationException {
        try {
            HttpEntity<MultiValueMap<String, String>> requestEntity = getRequestEntity(requestVO);
            URI uri = getPath(requestVO.getApiType().getUrl().trim(), getUriParams(requestVO), getQueryParams(requestVO), false);

            // Save the params to log
            requestVO.getRequestLog().setRequest(buildRequestMsgToLog(requestEntity, uri.toString()));

            // Send the request
            RestTemplate template = factory.getTemplate(uri, requestVO);
            ResponseEntity<String> response = template.exchange(uri, requestVO.getApiType().getHttpMethod(), requestEntity, String.class);

            template = null;
            return response.getBody();
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage(), ex);
        }
    }

    /**
     * Calls the Url passed as method argument and returns the document data from the url as byte array. The url must be an absolute path with all uri
     * variables set.
     * 
     * @param url
     *            url to be invoked
     * @return the data from the url
     * @throws ApplicationException
     *             thrown for any errors in invoking the url
     */
    public byte[] getDocument(String url) throws ApplicationException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            List<MediaType> mediaTypes = new ArrayList<MediaType>();
            mediaTypes.add(MediaType.ALL);
            headers.setAccept(mediaTypes);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(null, headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(getPath(url.trim(), new HashMap<String, String>(), null, true), HttpMethod.GET,
                    requestEntity, byte[].class);
            return response.getBody();
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage(), ex);
        }
    }

    private HttpEntity<MultiValueMap<String, String>> getRequestEntity(ApiRequestVO requestVO) throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<String, String>();
        // Get the form data only for POST requests.
        if (HttpMethod.POST == requestVO.getApiType().getHttpMethod()) {
            requestParams = getRequestParams(requestVO);
        }

        Map<String, String> reqHeaders = getHeaderParams(requestVO); // Get the Headers from metadata
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(requestParams, getHttpHeaders(reqHeaders));

        return entity;
    }

    private Map<String, String> getUriParams(ApiRequestVO requestVO) throws Exception {
        Map<String, String> uriParams = new HashMap<String, String>();
        for (ApiMetadataEntity metadata : getMetadataForCategory(requestVO.getApiType().getReqMetadata(), WebserviceHelper.BODY_TYPE)) {
            if (DataType.URI_PARAM == metadata.getDataType()) {
                // Current implementation doesn't work on PLS fields that need to be iterated for fetching URI Param value.
                uriParams.put(metadata.getApiFieldName(), getRequestValue(metadata, requestVO, null));
            }
        }

        return uriParams;
    }

    private String getQueryParams(ApiRequestVO requestVO) throws Exception {
        StringBuilder queryParams = new StringBuilder();
        for (ApiMetadataEntity metadata : getMetadataForCategory(requestVO.getApiType().getReqMetadata(), WebserviceHelper.BODY_TYPE)) {
            if (DataType.QUERY_PARAM == metadata.getDataType()) {
                // Current implementation doesn't work on PLS fields that need to be iterated for fetching Query Param value.
                if (!StringUtils.isEmpty(queryParams)) {
                    queryParams.append('&');
                }
                queryParams.append(metadata.getApiFieldName()).append('=').append(getRequestValue(metadata, requestVO, null).trim());
            }
        }

        return queryParams.toString();
    }

    private MultiValueMap<String, String> getRequestParams(ApiRequestVO requestVO) throws Exception {
        MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<String, String>();
        for (ApiMetadataEntity metadata : getMetadataForCategory(requestVO.getApiType().getReqMetadata(), WebserviceHelper.BODY_TYPE)) {
            // Current implementation doesn't work on PLS fields that need to be iterated for fetching Request params unless it is to be written
            // as an XML.
            if (DataType.WELL_FORMED_XML == metadata.getDataType()) {
                StringBuilder paramBuilder = new StringBuilder();
                for (ApiMetadataEntity childMetadata : metadata.getChildren()) {
                    parseMetadataAsXml(childMetadata, requestVO, requestVO, paramBuilder, false);
                }
                reqParams.add(metadata.getApiFieldName(), paramBuilder.toString());
            } else {
                reqParams.add(metadata.getApiFieldName(), getRequestValue(metadata, requestVO, null));
            }
        }

        return reqParams;
    }

    private Map<String, String> getHeaderParams(ApiRequestVO requestVO) throws Exception {
        Map<String, String> reqParams = new HashMap<String, String>();
        for (ApiMetadataEntity metadata : getMetadataForCategory(requestVO.getApiType().getReqMetadata(), WebserviceHelper.HEADER_TYPE)) {
            // Current implementation expects the header params set as the default value.
            if (StringUtils.isEmpty(metadata.getDefaultValue())) {
                reqParams.put(metadata.getApiFieldName(), metadata.getDefaultValue());
            }
        }

        return reqParams;
    }

    private HttpHeaders getHttpHeaders(Map<String, String> reqHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);

        for (String key : reqHeaders.keySet()) {
            if ("ContentType".equalsIgnoreCase(key)) {
                headers.setContentType(MediaType.parseMediaType(reqHeaders.get(key)));
            } else {
                headers.add(key, reqHeaders.get(key));
            }
        }
        return headers;
    }

    /**
     * Converts all the path replacing the path variables with uri variables passed to the method. The path passed to this method must be an absolute
     * path.
     * 
     * @param path
     *            absolute path of the uri to the rest web service
     * @param uriParams
     *            Uri params to be expanded in the url
     * @param queryParams
     *            Query params to be appended to the url
     * @param encoded
     *            flag that denotes whether the url is already encoded or needs to be encoded
     * @return the URI of the target
     */
    public URI getPath(String path, Map<String, String> uriParams, String queryParams, Boolean encoded) {
        StringBuilder uriPath = new StringBuilder(path);
        if (path.contains("?") && !StringUtils.isEmpty(queryParams)) {
            uriPath.append('&').append(queryParams);
        } else if (!StringUtils.isEmpty(queryParams)) {
            uriPath.append('?').append(queryParams);
        }

        URI uri = null;
        if (encoded) {
            uri = ServletUriComponentsBuilder.fromUriString(uriPath.toString()).build(encoded).toUri();
        } else {
            uri = ServletUriComponentsBuilder.fromUriString(uriPath.toString()).build(encoded).expand(uriParams).toUri();
        }

        if (uri.getHost() == null) {
            uriPath = new StringBuilder().append("http://").append(uriPath.toString());
            if (encoded) {
                uri = ServletUriComponentsBuilder.fromUriString(uriPath.toString()).build(encoded).toUri();
            } else {
                uri = ServletUriComponentsBuilder.fromUriString(uriPath.toString()).build(encoded).expand(uriParams).toUri();
            }
        }

        return uri;
    }

    private String buildRequestMsgToLog(HttpEntity<MultiValueMap<String, String>> requestEntity, String url) {
        StringBuilder builder = new StringBuilder(50).append("URL: ");
        builder.append(url);
        builder.append("\n Header Params: ").append(requestEntity.getHeaders().toString());
        builder.append("\n Request Params: ").append(requestEntity.getBody().toString());

        return builder.toString();
    }
}
