package com.pls.restful.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Installs SLF4JBridgeHandler to enable jul-to-slf4j logging.
 * 
 * @author Viacheslav Krot
 * 
 */
public class JulToSlf4jInstaller implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // do nothing
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // remove existing handlers attached to j.u.l root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger(); // (since SLF4J 1.6.5)

        // add SLF4JBridgeHandler to j.u.l's root logger
        SLF4JBridgeHandler.install();
    }
}
