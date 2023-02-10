package com.pls.scheduler.service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PingServer {

    /**
     * The Constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String[] ips;
    
    @Value("${mode.production:false}")
    private boolean isProd;
    
    @Value("${schedulers.ping.sterling:true}")
    private boolean isEnabled;
    
    @Value("${sterling.brokerURL:tcp://127.0.0.1}")
    private String ipAddresses;

    /**
     * Sends the ping request to the ipAddress passed, if not, it will take
     * broker url configured, and pings the server.
     *
     * @param ipAddress the ip address to ping
     * @throws UnknownHostException if the ip address is not proper host, it
     * will UnknownHostException.
     * @throws IOException IOException if any.
     * sterling.brokerURL=failover:(tcp://10.180.184.77:61616,tcp://10.180.184.76:61616)
     */
    public void sendPingRequest(String ipAddress) throws UnknownHostException, IOException {

        try {
        
            ips = ipAddresses.split("tcp://");

            for (int i = 1; i < ips.length; i++) {
                String ip = ips[i];
                ip = ip.replace(":61616", "").replace(",", "").replace(")","");

                InetAddress geek = InetAddress.getByName(ip);
                if (!geek.isReachable(5000)) {
                    LOGGER.error("IP Address " + ip + " is not reachable");
                } else {
                    if (!isProd) {
                    LOGGER.info("IP Address " + ip + " is reachable");
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.debug("Exception occured " + ex);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void sendPingRequest() throws IOException {
    	if (isEnabled) {
        	this.sendPingRequest(null);
    	}
    }
}
