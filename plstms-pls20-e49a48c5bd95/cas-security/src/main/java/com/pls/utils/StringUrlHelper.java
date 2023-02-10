package com.pls.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * Helper that gives possibility to change url's on web flow side.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class StringUrlHelper implements Serializable {
    private static final String PROPERTIES_FILE = "pls.properties";

    private String appWrongCredentialsUrlSuffix;

    private static final long serialVersionUID = 6278821144145338520L;

    /**
     * Constructor.
     */
    public StringUrlHelper() {
        appWrongCredentialsUrlSuffix = getProperties().getProperty("app.wrongCredentialsUrlSuffix", "");
    }

    /**
     * Construct full url from string url part.
     *
     * @param url part of url
     * @return full url
     */
    public String prepareLoginUrl(String url) {
        return url.substring(0, url.lastIndexOf('/')) + appWrongCredentialsUrlSuffix;
    }

    private Properties getProperties() {
        try {
            Properties properties = new Properties();

            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
            properties.load(resourceAsStream);

            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Application can not load settings", e);
        }
    }
}
