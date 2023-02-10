package com.pls.ltlrating.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.MimeTypes;
import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.file.ExportException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.ltlrating.service.PriceImportExportService;

/**
 * Implementation of {@link PriceImportExportService}.
 *
 * @author Alex Kyrychenko
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class PriceImportExportServiceImpl implements PriceImportExportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceImportExportServiceImpl.class);

    public static final String PRICING_EXPORT_FILE = "pricing.export.file";
    public static final String PRICING_IMPORT_FILE = "pricing.import.file";
    public static final String PRICING_IMPORT_WRONG_FILE = "pricing.import.wrong.file";
    public static final String PRICING_IMPORT_WRONG_FILE_PATH = "pricing.import.wrong.file.path";
    public static final String PRICING_IMPORT_WRONG_FILE_NAME = "pricing.import.wrong.file.name";
    public static final String UUID = "UUID";
    public static final String ORIG_FILE_NAME = "ORIG_FILE_NAME";
    public static final String FILE_PREFIX = "file:";

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocFileNamesResolver docFileNamesResolver;

    @Qualifier("migrationJobLauncher")
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Qualifier("migrationTaskExecutor")
    @Autowired
    private AsyncTaskExecutor taskExecutor;

    @Autowired
    @Qualifier("exportPrices")
    private Job exportJob;

    @Autowired
    @Qualifier("importPrices")
    private Job importJob;

    @Override
    public String importPricesAsync(final InputStream inputStream, final String origFileName) throws ImportException {
        String jobUUID = java.util.UUID.randomUUID().toString();
        LoadDocumentEntity wrongImportDoc = null;
        LoadDocumentEntity tempImportDoc = null;
        try {
            tempImportDoc = getTempDocumentEntity();
            File tempImportFile = getTempFile(tempImportDoc);
            IOUtils.copyLarge(inputStream, new FileOutputStream(tempImportFile));
            wrongImportDoc = getTempDocumentEntity();
            jobLauncher.run(importJob, new JobParametersBuilder().addString(UUID, jobUUID)
                                                                 .addString(PRICING_IMPORT_FILE, FILE_PREFIX + tempImportFile.getAbsolutePath(),
                                                                            false)
                                                                 .addString(PRICING_IMPORT_WRONG_FILE,
                                                                            FILE_PREFIX + getTempFile(wrongImportDoc).getAbsolutePath(), false)
                                                                 .addString(PRICING_IMPORT_WRONG_FILE_PATH, wrongImportDoc.getDocumentPath(), false)
                                                                 .addString(PRICING_IMPORT_WRONG_FILE_NAME, wrongImportDoc.getDocFileName(), false)
                                                                 .addString(ORIG_FILE_NAME, origFileName, false)
                                                                 .toJobParameters());
        } catch (Exception e) {
            documentService.deleteTempDocument(tempImportDoc);
            documentService.deleteTempDocument(wrongImportDoc);
            throw new ImportException("Exception during start import job", e);
        }
        return jobUUID;
    }

    @Override
    public boolean isImportPricesFinished(final String jobUUID) throws ExportException {
        return isPriceJobFinished(importJob.getName(), jobUUID);
    }

    @Override
    public ImportFileResults getImportPricesResult(final String jobUUID) throws ExportException, IOException {
        if (!isImportPricesFinished(jobUUID)) {
            throw new ExportException(String.format("Price import job with UUID(%s) hasn't been finished yet.", jobUUID));
        }
        ImportFileResults results = new ImportFileResults();
        JobExecution jobExecution = getJobExecution(importJob.getName(), jobUUID);
        if (CollectionUtils.isNotEmpty(jobExecution.getStepExecutions())) {
            StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
            LOGGER.debug("Step execution: [{}]", stepExecution);
            results.setSuccesRowsCount(stepExecution.getWriteCount());
            results.setFaiedRowsCount(stepExecution.getSkipCount());
            results.setErrorMessageList(
                    jobExecution.getAllFailureExceptions().stream().map(Throwable::getMessage).collect(Collectors.toList()));
        } else {
            results.setSuccesRowsCount(-1);
            results.setErrorMessageList(
                    Collections.singletonList(String.format("Import job instance with UUID[%s] has no step executions", jobUUID)));
        }
        if (results.getFaiedRowsCount() > 0) {
            LoadDocumentEntity wrongImportDoc = new LoadDocumentEntity();
            JobParameters jobParameters = jobExecution.getJobParameters();
            wrongImportDoc.setDocumentPath(jobParameters.getString(PRICING_IMPORT_WRONG_FILE_PATH));
            wrongImportDoc.setDocFileName(jobParameters.getString(PRICING_IMPORT_WRONG_FILE_NAME));
            wrongImportDoc.setDocumentType(DocumentTypes.TEMP.getDbValue());
            wrongImportDoc.setFileType(MimeTypes.XLSX);
            documentService.savePreparedDocument(wrongImportDoc);
            results.setFailedDocumentId(wrongImportDoc.getId());
        }
        return results;
    }

    @Override
    public ImportFileResults importPrices(InputStream inputStream, String origFileName) throws ImportException, IOException {
        try {
            final String jobUUID = importPricesAsync(inputStream, origFileName);
            return taskExecutor.submit(() -> {
                while (!isImportPricesFinished(jobUUID)) {
                    Thread.sleep(100L);
                }
                return getImportPricesResult(jobUUID);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ImportException("Exception during start import job", e);
        }
    }

    @Override
    public LoadDocumentEntity getImportFailedDocument(Long id) throws EntityNotFoundException, DocumentReadException {
        return documentService.getDocumentWithStream(id);
    }

    @Override
    public void removeImportFailedDocument(Long id) throws EntityNotFoundException {
        documentService.deleteTempDocument(id);
    }

    @Override
    public String exportPrices() throws ExportException {
        LoadDocumentEntity tempDocument = null;
        try {
            LOGGER.info("Started price export job");
            String jobUUID = java.util.UUID.randomUUID().toString();
            tempDocument = getTempDocumentEntity();
            jobLauncher.run(exportJob, new JobParametersBuilder().addString(PRICING_EXPORT_FILE, getTempExportPriceFileName(tempDocument), false)
                    .addString(UUID, jobUUID).toJobParameters());
            LOGGER.info("Price export job UUID({})", jobUUID);
            return jobUUID;
        } catch (Exception e) {
            LOGGER.error("Exception on exportPrices:", e);
            documentService.deleteTempDocument(tempDocument);
            throw new ExportException("Exception on start price bulk export.", e);
        }
    }

    @Override
    public boolean isExportPricesFinished(final String jobUUID) throws ExportException {
        return isPriceJobFinished(exportJob.getName(), jobUUID);
    }

    @Override
    public FileInputStreamResponseEntity getExportPricesFile(final String jobUUID) throws ExportException {
        if (!isExportPricesFinished(jobUUID)) {
            throw new ExportException(String.format("Price export job with UUID(%s) hasn't been finished yet.",
                                                    jobUUID));
        }
        try {
            final Resource exportPricesRes = new UrlResource(getJobExecution(exportJob.getName(), jobUUID).getJobParameters().
                    getString(PRICING_EXPORT_FILE));
            return new FileInputStreamResponseEntity(exportPricesRes.getInputStream(),
                                                     exportPricesRes.contentLength(), getExportPriceFileName());
        } catch (IOException e) {
            throw new ExportException(String.format("Exception on getting export file for the job with UUID(%s)",
                                                    jobUUID), e);
        }
    }

    private boolean isPriceJobFinished(final String jobName, final String jobUUID) throws ExportException {
        validateJobUUID(jobName, jobUUID);
        JobExecution jobExecution = getJobExecution(jobName, jobUUID);
        return jobExecution != null && !jobExecution.isRunning();
    }

    private JobExecution getJobExecution(final String jobName, final String jobUUID) {
        return jobRepository.getLastJobExecution(jobName, getJobParameters(jobUUID));
    }

    private void validateJobUUID(final String jobName, final String jobUUID) throws ExportException {
        if (!jobRepository.isJobInstanceExists(jobName, getJobParameters(jobUUID))) {
            throw new ExportException(String.format("Export prices job with UUID(%s) doesn't exist", jobUUID));
        }
    }

    private String getTempExportPriceFileName(LoadDocumentEntity loadDocumentEntity) throws MalformedURLException {
        return getTempFile(loadDocumentEntity).toURI().toURL().toString();
    }

    private LoadDocumentEntity getTempDocumentEntity() throws DocumentSaveException {
        LoadDocumentEntity tempDocument = documentService.prepareTempDocument(DocumentTypes.TEMP.name(), MimeTypes.XLSX);
        documentService.savePreparedDocument(tempDocument);
        return tempDocument;
    }

    private File getTempFile(final LoadDocumentEntity loadDocumentEntity) {
        File file = new File(docFileNamesResolver.buildDirectPath(loadDocumentEntity.getDocumentPath()), loadDocumentEntity.getDocFileName());
        FileUtils.setUpOutputFile(file, false, false, true);
        return file;
    }

    private String getExportPriceFileName() {
        return String.format("prices-export-%s.xlsx",
                             DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss").withZone(ZoneId.systemDefault()).
                                     format(Instant.now()));
    }

    private JobParameters getJobParameters(final String jobUUID) {
        return new JobParametersBuilder().addString(UUID, jobUUID).toJobParameters();
    }
}
