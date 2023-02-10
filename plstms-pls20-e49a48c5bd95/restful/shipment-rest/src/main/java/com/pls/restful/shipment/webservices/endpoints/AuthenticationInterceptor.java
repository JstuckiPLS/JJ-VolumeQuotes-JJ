package com.pls.restful.shipment.webservices.endpoints;

import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.interceptor.EndpointInterceptorAdapter;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.impl.security.PlsUserDetails;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.user.service.UserService;

/**
 * Interceptor to intercept the request and validate the user against the database.
 * 
 * @author Pavani Challa
 *
 */
public class AuthenticationInterceptor extends EndpointInterceptorAdapter {

    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
        SaajSoapMessage soapMessage = (SaajSoapMessage) messageContext.getRequest();

        SoapHeader soapHeader = soapMessage.getSoapHeader();
        if (soapHeader != null) {
            Iterator<SoapHeaderElement> elementIter = soapHeader.examineHeaderElements(new QName("", "Authentication"));
            if (!elementIter.hasNext()) {
                throw new UsernameNotFoundException("Invalid username or token");
            }
            while (elementIter.hasNext()) {
                String username = "";
                String token = "";
                Node authenticationNode = ((DOMSource) elementIter.next().getSource()).getNode();
                NodeList authenticationNodeChildren = authenticationNode.getChildNodes();
                for (int i = 0; i < authenticationNodeChildren.getLength(); i++) {
                    Node childNode = authenticationNodeChildren.item(i);
                    if ("Username".equals(childNode.getLocalName())) {
                        username = childNode.getTextContent();
                    }

                    if ("Token".equals(childNode.getLocalName())) {
                        token = childNode.getTextContent();
                    }
                }

                UserEntity user = userService.findByUserAndToken(username, token);
                if (user != null) {
                    PlsUserDetails authentication = new PlsUserDetails(user.getUserId(), user.getPassword(), true, new HashSet<String>(),
                            user.getId(), null, false, null);
                    SecurityUtils.setupNewAuthentication(authentication);
                    return true;
                } else {
                    throw new UsernameNotFoundException("Invalid username or token");
                }
            }
        }
        return false;
    }
}
