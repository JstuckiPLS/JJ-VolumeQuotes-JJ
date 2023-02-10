package com.pls.core.service.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.XmlSerializationException;
import com.pls.core.service.util.exception.ContextUtils;

/**
 * Service that handles processing to and from xml using JAXB.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Service
public class JaxbService {
    private static final Logger LOG = LoggerFactory.getLogger(JaxbService.class);

    private JAXBContext jaxbContext;

    @Resource
    private List<String> jaxbRelatedPackages;

    /**
     * Constructor.
     *
     * @param packages packages to scan
     *//*
    public JaxbService(List<String> packages) {
        this.packages = packages;
    }*/


    /**
     * Read xml input stream.
     *
     * @param inputStream input stream
     * @param <T> type of object
     * @return xml
     * @throws XmlSerializationException if reading fails
     */
    @SuppressWarnings("unchecked")
    public <T> T read(InputStream inputStream) throws XmlSerializationException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            String message = "Cannot read xml from stream:"  + e.getMessage();
            LOG.error(message);
            throw new XmlSerializationException(message, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Serialize object to xml stream.
     *
     * @param object object to serialize
     * @param outputStream output stream for xml
     * @throws XmlSerializationException if serialization fails
     */
    public void write(Object object, OutputStream outputStream) throws XmlSerializationException {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
            marshaller.marshal(object, new StreamResult(outputStream));
        } catch (JAXBException e) {
            String message = "Cannot write xml from object:" + e.getMessage();
            LOG.error(message);
            throw new XmlSerializationException(message, e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }


    /**
     * Post construct initialization.
     *
     * @throws ApplicationException if initialization fails
     */
    @PostConstruct
    protected void init() throws ApplicationException {
        setCorrectClassLoader();

        try {
            List<Class<?>> classes = new ArrayList<Class<?>>();
            for (String packageName : jaxbRelatedPackages) {
                classes.addAll(ContextUtils.findClasses(packageName, XmlRootElement.class));
            }

            jaxbContext = JAXBContext.newInstance(classes.toArray(new Class[classes.size()]));
        } catch (Exception e) {
            String message = "Cannot initialize JAXB context for xml serialization:" + e.getMessage();
            LOG.error(message, e);
            throw new ApplicationException(message, e);
        }
    }

    // set class load needed for JAXB
    private void setCorrectClassLoader() {
        Thread currentThread = Thread.currentThread();
        ClassLoader defaultCl = currentThread.getContextClassLoader();
        if (defaultCl == null) {
            currentThread.setContextClassLoader(ClassLoader.getSystemClassLoader());
        }
    }
}
