package com.pls.core.jaxb.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides methods to simplify working with JAXBObjects
 * 
 * @author gpoppelreiter
 *
 */
public abstract class JaxbHelper {

	/**
	 * Will convert any JAXB Object into its string xml representation
	 * 
	 * @param jaxbObject
	 * @return
	 * @throws JAXBException
	 */
	@SuppressWarnings("rawtypes")
	public static String marshalToString(Object jaxbObject) throws JAXBException {
	    //if the object is a generic form of JAXBElement, we need to get the
		//declared type and not the 'class' since the class is just JAXBElement
		Class clazz = null;
		if(jaxbObject instanceof JAXBElement) {
			clazz = ((JAXBElement)jaxbObject).getDeclaredType();
		} else {
			clazz = jaxbObject.getClass();
		}
		
		JAXBContext ctx = JAXBContext.newInstance(clazz);
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		StringWriter sw = new StringWriter();
		marshaller.marshal(jaxbObject, sw);
		return sw.toString();

	}
	

	/**
	 * returns the name of the root node of a string representation of an XML
	 * document.
	 * 
	 * @param xmlString
	 * @return
	 * @throws Exception
	 */
	public static String getRootNodeName(String xmlString) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));
		Element rootElement = document.getDocumentElement();
		return rootElement.getNodeName();
	}

	/**
	 * Converts an xml string into an Ojbect of whatever type is passed in as
	 * 'classToBeBound'
	 * 
	 * @param xmlString
	 * @param classToBeBound
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Object unmarshalJaxbObject(String xmlString, Class classToBeBound) throws Exception {
		JAXBContext context = JAXBContext.newInstance(classToBeBound);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		InputStream unmarshalStream = new ByteArrayInputStream(xmlString.getBytes());
		return unmarshaller.unmarshal(unmarshalStream);
	}
}