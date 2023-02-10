package com.pls.ltlrating.soap.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pls.core.common.MimeTypes;
import com.pls.core.domain.enums.EmailType;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.email.dto.EmailAttachmentDTO;
import com.pls.email.producer.EmailInfo;
import com.pls.email.producer.EmailMessageProducer;
import com.pls.ltlrating.soap.proxy.Division;
import com.pls.ltlrating.soap.proxy.GetRate2;
import com.pls.ltlrating.soap.proxy.GetRate2Response;
import com.pls.ltlrating.soap.proxy.Shipment;

/**
 * SOAP client for DayAndRoss.
 * 
 * @author Brichak Aleksandr
 *
 */
@Service
public class DayAndRossSOAPClientImpl implements DayAndRossSOAPClient {

    private static final String PATH_TO_PROXY_CLASSES = "com.pls.ltlrating.soap.proxy";
    private static final String SOAP_PREFIX = "soap";
    private static final String ENV_PREFIX = "env";
    private static final String XMLNS_ENV_PREFIX = "xmlns:env";

    private static final String NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
    private static final String QUALIFIED_NAME_XSI = "xmlns:xsi";
    private static final String QUALIFIED_NAME_XSD = "xmlns:xsd";
    private static final String QUALIFIED_NAME_SOAP = "xmlns:soap";
    private static final String NAMESPACE_VALUE_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String NAMESPACE_VALUE_XSD = "http://www.w3.org/2001/XMLSchema";
    private static final String NAMESPACE_VALUE_SOAP = "http://schemas.xmlsoap.org/soap/envelope/";

    private static final String CONNECTION_URL = "https://dayross.dayrossgroup.com/public/ShipmentServices.asmx";

    private static final String REQUEST_METHOD = "POST";
    private static final String REQUEST_PROPERTY_SOAP_ACTION = "SOAPAction";
    private static final String REQUEST_PROPERTY_CONTENT_TYPE = "Content-Type";
    private static final String REQUESTPROPERTY_SOAP_ACTION_VAL = "http://dayrossgroup.com/web/public/webservices/shipmentServices/GetRate2";
    private static final String REQUEST_PROPERTY_CONTENT_TYPE_VAL = "text/xml; charset=utf-8";

    @Value("${dayAndRoss.email}")
    private String email;

    @Value("${dayAndRoss.password}")
    private String password;

    @Value("${dayAndRoss.division}")
    private String division;

    private static final Logger LOG = LoggerFactory.getLogger(DayAndRossSOAPClient.class);

    @Autowired
    private EmailMessageProducer emailMessageProducer;

    @Autowired
    private DocumentService documentService;


    @Override
    public GetRate2Response getDayAndRossRate(Shipment shipment) {

        GetRate2 request = new GetRate2();
        request.setDivision(Division.fromValue(division));
        request.setEmailAddress(email);
        request.setPassword(password);
        request.setShipment(shipment);
        return unmarshalSoapMsg(sendSoapRequest(createSOAPMessage(request)));

    }

    private GetRate2Response unmarshalSoapMsg(String receivedSoapMsg) {
        ByteArrayInputStream byteArrayInputStream = null;
        GetRate2Response response = null;
        if (receivedSoapMsg != null) {
            try {
                byteArrayInputStream = new ByteArrayInputStream(receivedSoapMsg.getBytes());
                SOAPMessage message = MessageFactory.newInstance().createMessage(null, byteArrayInputStream);
                Unmarshaller unmarshaller = JAXBContext
                        .newInstance(PATH_TO_PROXY_CLASSES, GetRate2Response.class.getClassLoader())
                        .createUnmarshaller();
                response = (GetRate2Response) unmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());
            } catch (Exception e) {
                LOG.error("Can't unmarshal soap message " + receivedSoapMsg, e);
            } finally {
                IOUtils.closeQuietly(byteArrayInputStream);
            }
        }
        return response;
    }

    private String sendSoapRequest(SOAPMessage soapMessage) {
        String result = null;
        OutputStream out = null;
        ByteArrayOutputStream byteArrayOut = null;
        InputStream is = null;
        HttpURLConnection urlconnection = null;
        try {
            byteArrayOut = new ByteArrayOutputStream();
            soapMessage.writeTo(byteArrayOut);
            LOG.info("Start sending soap messages " + new String(byteArrayOut.toByteArray()));
            Long requestFileId = createTempFileFromString(new String(byteArrayOut.toByteArray()));

            URL uri = new URL(CONNECTION_URL);
            urlconnection = (HttpURLConnection) uri.openConnection();
            urlconnection.setDoOutput(true);
            urlconnection.setDoInput(true);
            urlconnection.setRequestMethod(REQUEST_METHOD);
            urlconnection.setRequestProperty(REQUEST_PROPERTY_SOAP_ACTION, REQUESTPROPERTY_SOAP_ACTION_VAL);
            urlconnection.setRequestProperty(REQUEST_PROPERTY_CONTENT_TYPE, REQUEST_PROPERTY_CONTENT_TYPE_VAL);

            out = urlconnection.getOutputStream();
            soapMessage.writeTo(out);
            out.flush();
            is = urlconnection.getInputStream();
            result = new BufferedReader(new InputStreamReader(is)).readLine();
            LOG.info("The result is " + result);
            Long resultFileId = createTempFileFromString(result);
            sendEmail(requestFileId, resultFileId);

        } catch (Exception e) {
            LOG.error("Can't send soap message ", e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(byteArrayOut);
            if (urlconnection != null) {
                urlconnection.disconnect();
            }
        }
        return result;
    }

    private void sendEmail(Long requestFileId, Long resultFileId) {
        if (resultFileId == null || resultFileId == null) {
            LOG.info("No Files for Carrier API call");
            return;
        }
        List<EmailAttachmentDTO> attachments = new ArrayList<>();
        attachments.add(new EmailAttachmentDTO(requestFileId, "request.xml"));
        attachments.add(new EmailAttachmentDTO(resultFileId, "response.xml"));
        EmailInfo emailInfo = new EmailInfo(null, Collections.singletonList("nchastain@plslogistics.com"), "Carrier API call",
                "Request and response are attached.", attachments, EmailType.NOT_AUDITABLE, null, SecurityUtils.getCurrentPersonId(), null);
        emailMessageProducer.sendEmail(emailInfo);
    }

    private Long createTempFileFromString(String data) {
        try {
            return documentService.saveTempDocument(format(data).getBytes(), DocumentTypes.TEMP.getDbValue(), MimeTypes.TXT).getId();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private String format(String input) throws Exception {
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(xmlInput, new StreamResult(stringWriter));

        String pretty = stringWriter.toString();
        pretty = pretty.replace("\r\n", "\n");
        return pretty;
    }

    private SOAPMessage createSOAPMessage(GetRate2 object) {
        SOAPMessage message = null;
        try {
            message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
            JAXBContext jaxb = JAXBContext.newInstance(PATH_TO_PROXY_CLASSES, GetRate2.class.getClassLoader());

            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.marshal(object, message.getSOAPBody());

            message.getSOAPPart().getEnvelope().setPrefix(SOAP_PREFIX);
            message.getSOAPPart().getEnvelope().removeNamespaceDeclaration(ENV_PREFIX);
            message.getSOAPPart().getEnvelope().removeAttribute(XMLNS_ENV_PREFIX);
            message.getSOAPBody().setPrefix(SOAP_PREFIX);
            message.getSOAPHeader().detachNode();
            message.getSOAPPart().getEnvelope().setAttributeNS(NAMESPACE_URI, QUALIFIED_NAME_XSI, NAMESPACE_VALUE_XSI);
            message.getSOAPPart().getEnvelope().setAttributeNS(NAMESPACE_URI, QUALIFIED_NAME_XSD, NAMESPACE_VALUE_XSD);
            message.getSOAPPart().getEnvelope().setAttributeNS(NAMESPACE_URI, QUALIFIED_NAME_SOAP,
                    NAMESPACE_VALUE_SOAP);
            message.saveChanges();
        } catch (Exception e) {
            LOG.error("Cannot write xml from object", e.getMessage());
        }
        return message;
    }

}
