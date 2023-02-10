package com.pls.shipment.service.impl.edi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.FTPClientException;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIMessage;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.edi.EDIService;
import com.pls.shipment.service.edi.EDIWorker;
import com.pls.shipment.service.impl.edi.jms.producer.EDIJmsMessageProducer;

/**
 * {@link EDIService} implementation.
 *
 * @author Mikhail Boldinov, 28/08/13
 */
@Service
public class EDIServiceImpl implements EDIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EDIServiceImpl.class);

    @Autowired
    private EDIFtpClient ediFtpClient;

    @Autowired
    private EDIJmsMessageProducer ediJmsMessageProducer;

    @Autowired
    private EDIWorker ediWorker;

    @Override
    public void receiveEDI() {
        List<EDIFile> ediFiles = listEdiFiles();
        if (!ediFiles.isEmpty()) {
            LOGGER.info("Processing EDI data...");
            for (EDIFile ediFile : ediFiles) {
                ediWorker.readEDI(ediFile);
            }
            LOGGER.info("Processing EDI data completed");
        }
    }

    @Override
    public void sendEDI(Long carrierId, List<Long> entityIds, EDITransactionSet transactionSet, Map<String, Object> params)
            throws EdiProcessingException {
        try {
            EDIMessage message = new EDIMessage();
            message.setCarrierId(carrierId);
            message.setEntityIds(entityIds);
            message.setTransactionSet(transactionSet);
            message.setParams(params);
            ediJmsMessageProducer.sendMessage(message);
        } catch (JMSException e) {
            throw new EdiProcessingException("Cannot send EDI file: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendEDIFile(EDIFile ediFile) throws EdiProcessingException {
        try {
            ediFtpClient.uploadFile(ediFile);
            LOGGER.info(String.format("EDI '%s' has been sent to carrier '%s' successfully", ediFile.getName(),
                    ediFile.getCarrierScac()));
            boolean backup = ediFtpClient.backupFile(ediFile);
            if (!backup) {
                LOGGER.warn(String.format("Unable to backup EDIFile: '%s', Carrier: '%s'", ediFile.getName(),
                        ediFile.getCarrierScac()));
            }
        } catch (FTPClientException e) {
            throw new EdiProcessingException("Cannot send EDIFile: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new EdiProcessingException(e.getMessage(), e);
        }
    }

    private List<EDIFile> listEdiFiles() {
        List<EDIFile> ediFiles = new ArrayList<EDIFile>();
        try {
            LOGGER.info("EDI FTP client connected successfully");
            ediFiles = ediFtpClient.listFiles();
            LOGGER.info("EDI FTP client disconnected successfully");
        } catch (FTPClientException | InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
        return ediFiles;
    }
}
