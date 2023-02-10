package com.pls.shipment.service.impl.edi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.pls.core.dao.IntegrationAuditDao;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.XmlSerializationException;
import com.pls.core.service.xml.JaxbService;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;

/**
 * EDI Helper Service that checks if EDI 204 was changed and new version needs to be sent to carrier.
 *
 * @author Brichak Aleksandr
 * @author Aleksandr Leshchenko
 */
@Component
public class EDIHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(EDIHelper.class);

    private static final String UPDATE_OPERATION_TYPE = "UPDATE";

    private static final String PERSON_ID_XPATH = "//*[local-name()='PersonId']";
    private static final String OPERATION_TYPE_XPATH = "//*[local-name()='OperationType']";
    private static final String OPERATION_TYPE_TEXT_XPATH = OPERATION_TYPE_XPATH + "/text()";

    @Autowired
    private IntegrationAuditDao auditDao;

    @Autowired
    private JaxbService jaxbService;

    /**
     * The method compares the provided BO (EDI204) with the previous version of EDI204 for the same loadId.
     * 
     * @param loadBO
     *            {@link LoadMessageJaxbBO} EDI204 BO to compare.
     * @return <code>true</code> if provided EDI204 was changed or if operation type is not "Update",<br/>
     *         <code>false</code> if EDI204 was not changed,<br/>
     *         <code>null</code> if previous EDI 204 can't be found or if it's invalid or if provided XML is
     *         not an outbound EDI 204.
     */
    public Boolean isEdi204Changed(LoadMessageJaxbBO loadBO) {
        try {
            String xml = convertMessage(loadBO);
            if (EDIMessageType.EDI204_STERLING_MESSAGE_TYPE.getCode().equals(loadBO.getMessageType())) {
                return isValidAndChangedEdi204XML(xml, loadBO);
            }
        } catch (Exception e) {
            LOGGER.error("Error comparing 204 xml for load " + loadBO.getLoadId(), e);
        }
        return null;
    }

    private String convertMessage(LoadMessageJaxbBO message) throws ApplicationException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            jaxbService.write(message, outputStream);
            return outputStream.toString();
        } catch (XmlSerializationException e) {
            throw new ApplicationException(
                    String.format("Caught exception while attempting to serialize an instance of LoadMessageJaxbBO: %s", e.getMessage()), e);
        }
    }

    private Boolean isValidAndChangedEdi204XML(String newXml, IntegrationMessageBO integrationMessageBO)
            throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilder docBuilder = getDocumentBuilder();
        String operationType = getOperationTypeFromEdi204XML(newXml, docBuilder);
        if (!UPDATE_OPERATION_TYPE.equals(operationType)) {
            return true;
        }
        String oldXml = auditDao.getLastEdi204XMLByLoadIdAndScac(integrationMessageBO.getLoadId(), integrationMessageBO.getScac());
        return oldXml == null ? null : isXMLChanged(newXml, oldXml, docBuilder);
    }

    private boolean isXMLChanged(String newXml, String oldXml, DocumentBuilder docBuilder)
            throws SAXException, IOException, XPathExpressionException {
        Document doc1 = getDocumentForComparison(newXml, docBuilder);
        Document doc2 = getDocumentForComparison(oldXml, docBuilder);
        return !doc1.isEqualNode(doc2);
    }

    private Document getDocumentForComparison(String xml, DocumentBuilder docBuilder)
            throws SAXException, IOException, XPathExpressionException {
        Document document = getDocumentFromXML(xml, docBuilder);
        XPathFactory xpathFact = XPathFactory.newInstance();
        XPath xpath = xpathFact.newXPath();
        replaceDummyItem(document, xpath, OPERATION_TYPE_XPATH);
        replaceDummyItem(document, xpath, PERSON_ID_XPATH);
        return document;
    }

    private void replaceDummyItem(Document document, XPath xpath, String xpathStr) throws XPathExpressionException {
        Element evaluate = (Element) xpath.evaluate(xpathStr, document, XPathConstants.NODE);
        evaluate.setTextContent("DUMMY");
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);
        return dbf.newDocumentBuilder();
    }

    private Document getDocumentFromXML(String xml, DocumentBuilder docBuilder) throws SAXException, IOException {
        Document doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        doc.normalizeDocument();
        return doc;
    }

    private String getOperationTypeFromEdi204XML(String xml, DocumentBuilder docBuilder)
            throws XPathExpressionException, SAXException, IOException {
        Document doc = getDocumentFromXML(xml, docBuilder);
        XPathFactory xpathFact = XPathFactory.newInstance();
        XPath xpath = xpathFact.newXPath();
        return xpath.evaluate(OPERATION_TYPE_TEXT_XPATH, doc);
    }
}
