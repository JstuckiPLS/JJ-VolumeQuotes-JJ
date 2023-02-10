package com.pls.shipment.service.impl.edi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.AbstractDao;
import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.FTPClientException;
import com.pls.shipment.dao.LoadReasonTrackingStatusDao;
import com.pls.shipment.dao.LoadTrackingStatusDao;
import com.pls.shipment.dao.edi.EDIQualifierDao;
import com.pls.shipment.dao.impl.edi.EDISequencesFetcher;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.edi.EDIParseResult;
import com.pls.shipment.domain.edi.EDIQualifierEntity;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.edi.EDIWorker;
import com.pls.shipment.service.edi.handler.EDIParseResultHandler;
import com.pls.shipment.service.edi.parser.EDIParser;
import com.pls.shipment.service.impl.edi.parser.AbstractEDIParser;
import com.pls.shipment.service.impl.edi.parser.EDIParserFactory;

/**
 * {@link EDIWorker} implementation.
 *
 * @author Mikhail Boldinov, 12/03/14
 */
@Service
@Transactional
public class EDIWorkerImpl implements EDIWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(EDIWorkerImpl.class);

    @Value("${admin.personId}")
    private Long ediUserId;

    @Autowired
    private EDIFtpClient ediFtpClient;

    @Autowired
    private EDISequencesFetcher ediSequencesFetcher;

    @Autowired
    private EDIQualifierDao ediQualifierDao;

    @Autowired
    private CarrierDao carrierDao;

    @Autowired
    private LoadTrackingStatusDao loadTrackingStatusDao;

    @Autowired
    private LoadReasonTrackingStatusDao loadReasonTrackingStatusDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void readEDI(EDIFile ediFile) {
        try {
            LOGGER.info(String.format("Downloading EDI file '%s'", ediFile.getName()));
            ediFile.setFile(ediFtpClient.downloadFile(ediFile));
            EDIParseResult.Status status = processIncomingEDIFile(ediFile);
            if (status == EDIParseResult.Status.SUCCESS) {
                ediFtpClient.markAsProcessed(ediFile);
                LOGGER.info(String.format("EDI '%s' processed successfully", ediFile.getName()));
            } else if (status == EDIParseResult.Status.FAIL) {
                ediFtpClient.markAsFailed(ediFile);
                LOGGER.warn(String.format("Processing EDI '%s' failed", ediFile.getName()));
            } else if (status == EDIParseResult.Status.PARTIAL_FAIL) {
                ediFile.setName(getFileNameWithSuffix(ediFile.getName(), "_original"));
                ediFtpClient.markAsPartiallyFailed(ediFile);
                LOGGER.warn(String.format("EDI '%s' processed with parital errors", ediFile.getName()));
            }
        } catch (FTPClientException | InterruptedException e) {
            LOGGER.error("EDI File " + ediFile.getName() + " has not been read, the reason  " + e.toString());
        }
    }

    @Override
    public void writeEDI(Long carrierId, List<Long> entityIds, EDITransactionSet transactionSet, Map<String, Object> params)
            throws EdiProcessingException {
        try {
            AbstractDao<Identifiable<Long>, Long> dao = applicationContext.getBean(transactionSet.getDaoClass());
            List<Identifiable<Long>> entities = dao.getAll(entityIds);
            CarrierEntity carrier = carrierDao.find(carrierId);
            EDIParser<Identifiable<Long>> parser = getParser(carrier, transactionSet);
            EDIFile ediFile = parser.create(entities, params);
            ediFtpClient.uploadFile(ediFile);
            LOGGER.info(String.format("EDI '%s' has been sent to carrier '%s' successfully", ediFile.getName(), ediFile.getCarrierScac()));
            boolean backup = ediFtpClient.backupFile(ediFile);
            if (!backup) {
                LOGGER.warn(String.format("Unable to backup EDIFile: '%s', Carrier: '%s'", ediFile.getName(), ediFile.getCarrierScac()));
            }

            EDIParseResult<Identifiable<Long>> parseResult = new EDIParseResult<Identifiable<Long>>(ediFile);
            parseResult.setStatus(EDIParseResult.Status.SUCCESS);
            parseResult.setParsedEntities(entities);
            EDIParseResultHandler<Identifiable<Long>> resultHandler = applicationContext.getBean(transactionSet.getHandlerClass());
            resultHandler.handle(parseResult);
        } catch (IOException e) {
            throw new EdiProcessingException("Cannot create EDIFile: " + e.getMessage(), e);
        } catch (FTPClientException e) {
            throw new EdiProcessingException("Cannot send EDIFile: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new EdiProcessingException(e.getMessage(), e);
        }
    }

    private String getFileNameWithSuffix(String name, String suffix) {
        StringBuilder newFileName = new StringBuilder(FilenameUtils.getBaseName(name)).append(suffix);
        String extension = FilenameUtils.getExtension(name);
        if (!StringUtils.isEmpty(extension)) {
            newFileName.append('.').append(extension);
        }
        return newFileName.toString();
    }

    private EDIParseResult.Status processIncomingEDIFile(EDIFile ediFile) {
        try {
            EDIParser<Identifiable<Long>> parser = getEdiParser(ediFile);
            LOGGER.info(String.format("Parse EDI '%s'", ediFile.getName()));
            EDIParseResult<Identifiable<Long>> parseResult = parser.parse(ediFile);
            LOGGER.info(String.format("EDI '%s' parsed successfully", ediFile.getName()));
            LOGGER.info(String.format("Processing parse result of EDI '%s'", parseResult.getEdiFile().getName()));
            EDIParseResultHandler<Identifiable<Long>> resultHandler = applicationContext.getBean(parseResult.getTransactionSet().getHandlerClass());
            List<Integer> failedEntities = resultHandler.handle(parseResult);
            if (!failedEntities.isEmpty()) {
                EDIFile successFile = parser.getEDIFileWithoutSelectedEntities(ediFile, failedEntities);
                successFile.setName(getFileNameWithSuffix(ediFile.getName(), "_2"));
                LOGGER.info(String.format("EDI file '%s' was created for failed transactions", successFile.getFile().getName()));
                ediFtpClient.markAsProcessed(successFile);
                List<Integer> successEntities = getSuccessTransactions(parseResult.getParsedEntities(), failedEntities);
                EDIFile failedFile = parser.getEDIFileWithoutSelectedEntities(ediFile, successEntities);
                failedFile.setName(getFileNameWithSuffix(ediFile.getName(), "_1"));
                LOGGER.info(String.format("EDI file '%s' was created for success transactions", failedFile.getFile().getName()));
                ediFtpClient.markAsFailed(failedFile);
                return EDIParseResult.Status.PARTIAL_FAIL;
            }
            return parseResult.getStatus();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return EDIParseResult.Status.FAIL;
        }
    }

    private List<Integer> getSuccessTransactions(List<Identifiable<Long>> parsedEntities, List<Integer> failedTransactions) {
        List<Integer> successTransactions = new ArrayList<Integer>();
        for (int i = 0; i < parsedEntities.size(); i++) {
            if (!failedTransactions.contains(i)) {
                successTransactions.add(i);
            }
        }
        return successTransactions;
    }

    private EDIParser<Identifiable<Long>> getEdiParser(EDIFile ediFile) throws Exception {
        CarrierEntity carrier = carrierDao.findByScac(ediFile.getCarrierScac());
        if (carrier != null) {
            return getParser(carrier, ediFile.getTransactionSet());
        } else {
            throw new EntityNotFoundException("Unable to find carrier by SCAC: " + ediFile.getCarrierScac());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Identifiable<Long>> EDIParser<T> getParser(CarrierEntity carrier, EDITransactionSet transactionSet) {
        return EDIParserFactory.create(carrier, transactionSet, new EDIParserDataProvider(carrier.getId(), transactionSet.getId()));
    }

    private final class EDIParserDataProvider implements AbstractEDIParser.DataProvider {
        private final Long carrierId;
        private final String transactionId;

        private Long gs = 0L;
        private Long isa = 0L;

        EDIParserDataProvider(Long carrierId, String transactionId) {
            this.carrierId = carrierId;
            this.transactionId = transactionId;
        }

        @Override
        public Long getNextIsaId() {
            isa = ediSequencesFetcher.getNextISA();
            return isa;
        }

        @Override
        public Long getNextGSId() {
            gs = ediSequencesFetcher.getNextGS();
            return gs;
        }

        @Override
        public Map<String, String> getQualifiers() {
            Map<String, String> qualifiers = new HashMap<String, String>();
            for (EDIQualifierEntity ediQualifier : ediQualifierDao.getQualifiersForCarrier(carrierId, transactionId)) {
                qualifiers.put(ediQualifier.getPlsReference(), ediQualifier.getQualifier());
            }
            return qualifiers;
        }

        @Override
        public Long getEdiUserId() {
            return ediUserId;
        }

        @Override
        public Map<String, String> getLoadTrackingStatusTypes() {
            return loadTrackingStatusDao.getMap(Long.valueOf(transactionId));
        }

        @Override
        public Map<String, String> getLoadReasonTrackingStatusTypes() {
            return loadReasonTrackingStatusDao.getMap(Long.valueOf(transactionId));
        }

        @Override
        public Long getGs() {
            return gs;
        }

        @Override
        public Long getIsa() {
            return isa;
        }
    }
}
