package com.pls.extint.service.impl.helper;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.pls.extint.shared.ApiRequestVO;

/**
 * Factory for creating templates for calling SOAP web services.
 * 
 * @author Pavani Challa
 * 
 */
@Component
public class SoapTemplateFactory {

    /**
     * Creates Web service template for sending SOAP request. If the request has credentials provided, sets the credentials in the message sender.
     * Also, sets the url to send the SOAP request to.
     * 
     * @param requestVO
     *            VO containing the request details like the credentials and the url
     * @return the web service template through which the SOAP request is sent
     * @throws Exception
     *             if the username and password can not be set
     */
    public WebServiceTemplate getTemplate(ApiRequestVO requestVO) throws Exception {
        WebServiceTemplate template = new WebServiceTemplate();
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
        messageSender.setAuthScope(AuthScope.ANY);
        if (!StringUtils.isEmpty(requestVO.getApiType().getUsername())) {
            messageSender.setCredentials(new UsernamePasswordCredentials(requestVO.getApiType().getUsername(), requestVO.getApiType().getPassword()));
        }

        messageSender.afterPropertiesSet();
        template.setMessageSender(messageSender);
        template.setDefaultUri(requestVO.getApiType().getUrl());
        return template;
    }
}
