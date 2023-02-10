package com.pls.extint.service.impl.helper;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.pls.core.exception.ApplicationException;
import com.pls.extint.domain.ApiMetadataEntity;
import com.pls.extint.shared.ApiRequestVO;

/**
 * Helper class for calling SOAP Web services.
 * 
 * @author Pavani Challa
 * 
 */
@Component
public class SoapServiceHelper extends WebserviceHelper {

    @Autowired
    private SoapTemplateFactory factory;

    @Override
    public String sendRequest(final ApiRequestVO requestVO) throws ApplicationException {
        try {
            return sendRequest(requestVO, getRequestData(requestVO, WebserviceHelper.HEADER_TYPE),
                    getRequestData(requestVO, WebserviceHelper.BODY_TYPE));
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage(), ex);
        }
    }

    private String sendRequest(final ApiRequestVO requestVO, final String header, final String body) throws ApplicationException {
        StringWriter response = new StringWriter();
        WebServiceTemplate template = null;

        try {
            template = factory.getTemplate(requestVO);

            StreamSource source = new StreamSource(new StringReader(body));
            StreamResult result = new StreamResult(response);
            template.sendSourceAndReceiveToResult(source, new WebServiceMessageCallback() {
                public void doWithMessage(WebServiceMessage message) throws TransformerException {
                    SoapMessage soapMessage = (SoapMessage) message;
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    if (!StringUtils.isEmpty(requestVO.getApiType().getSoapAction())) {
                        soapMessage.setSoapAction(requestVO.getApiType().getSoapAction());
                    }

                    if (!StringUtils.isEmpty(header)) {
                        SoapHeader soapHeader = soapMessage.getSoapHeader();
                        StreamSource headerSource = new StreamSource(new StringReader(header));
                        transformer.transform(headerSource, soapHeader.getResult());
                    }

                    StringWriter requestMsg = new StringWriter();
                    StreamResult dest = new StreamResult(requestMsg);
                    transformer.transform(message.getPayloadSource(), dest);

                    // Update API_LOG record with the request
                    requestVO.getRequestLog().setRequest(requestMsg.toString());
                }
            }, result);
        } catch (SoapFaultClientException ex) {
            throw new ApplicationException(ex.getFaultStringOrReason(), ex);
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage(), ex);
        } finally {
            template = null;
        }

        return response.toString();
    }

    private String getRequestData(ApiRequestVO requestVO, String category) throws Exception {
        StringBuilder builder = new StringBuilder();
        List<ApiMetadataEntity> metadata = getMetadataForCategory(requestVO.getApiType().getReqMetadata(), category);
        if (metadata != null && !metadata.isEmpty()) {
            for (ApiMetadataEntity entity : metadata) {
                parseMetadataAsXml(entity, requestVO, requestVO, builder, true);
            }
        }

        return builder.toString();
    }
}
