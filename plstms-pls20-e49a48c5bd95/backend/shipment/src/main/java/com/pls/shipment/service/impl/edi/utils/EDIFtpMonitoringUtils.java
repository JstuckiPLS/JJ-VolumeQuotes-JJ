package com.pls.shipment.service.impl.edi.utils;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pls.core.exception.FTPClientException;
import com.pls.shipment.domain.edi.EDIFile;

/**
 * Utility for monitoring and executing in parallel thread operations for EDI, that run on FTP.
 * 
 * @author Brichak Aleksandr
 * 
 */
@Component
public class EDIFtpMonitoringUtils {

    @Value("${ftp.uploadOrDownloadTimeout}")
    private long uploadOrDownloadTimeout;

    @Value("${ftp.getEdiFilesTimeout}")
    private long getEdiFilesTimeout;

    @Value("${ftp.numberOfRepeats}")
    private long numberOfRepeats;

    private static final Logger LOGGER = LoggerFactory.getLogger(EDIFtpMonitoringUtils.class);

    /**
     * Method for execute asynchronous task.
     * 
     * @param task
     *            the task to be executed
     * @param file
     *            ediFile for download or upload
     * @param <T>
     *            generic type of return result
     * @return result of an asynchronous task
     * @throws InterruptedException
     *             thrown if insufficient timeout to execute this method
     * @throws FTPClientException
     *             application exception
     */
    public <T> T executeTask(Callable<T> task, File file) throws InterruptedException, FTPClientException {
        long currentTimeout = calculateUploadAndDownloadTimeOut(file);
        long currentNumberOfRepeats = numberOfRepeats;
        while (currentNumberOfRepeats > 0) {
            try {
                return launchWork(task, currentTimeout);
            } catch (InterruptedException e) {
                currentNumberOfRepeats--;
                LOGGER.warn("Process interrupted timeout = " + currentTimeout + " was not enough. Left " + currentNumberOfRepeats + " attempts");
                currentTimeout = currentTimeout + (currentTimeout * 30) / 100;
            }
        }
        LOGGER.error("Process interrupted timeout = " + currentTimeout + " was not enough");
        throw new InterruptedException("Process interrupted timeout = " + currentTimeout + " was not enough");
    }

    /**
     * Method for execute asynchronous task.
     * 
     * @param task
     *            the task to be executed
     * @param listEdiFile
     *            ediFile for carrier
     * @param <T>
     *            generic type of return result
     * @return result of an asynchronous task
     * @throws InterruptedException
     *             thrown if insufficient timeout to execute this method
     * @throws FTPClientException
     *             application exception
     */
    public <T> T executeTask(Callable<T> task, List<EDIFile> listEdiFile) throws InterruptedException, FTPClientException {
        long currentTimeout = getEdiFilesTimeout;
        long currentNumberOfRepeats = numberOfRepeats;
        while (currentNumberOfRepeats > 0) {
            try {
                return launchWork(task, listEdiFile, currentTimeout);
            } catch (InterruptedException e) {
                currentNumberOfRepeats--;
                LOGGER.warn("Process interrupted timeout = " + currentTimeout + " was not enough. Left " + currentNumberOfRepeats + " attempts");
                currentTimeout = currentTimeout + (currentTimeout * 30) / 100;
            }
        }
        LOGGER.error("Process interrupted timeout = " + currentTimeout + " was not enough");
        throw new InterruptedException("Process interrupted timeout = " + currentTimeout + " was not enough");
    }

    private long calculateUploadAndDownloadTimeOut(final File file) throws FTPClientException {
        Callable<Long> calculateUploadAndDownloadTimeTask = new Callable<Long>() {
            public Long call() throws Exception {
                long size = 0;
                if (file != null) {
                    try {
                        size = file.length();
                        return (size / (1024 * 1024) * uploadOrDownloadTimeout) + uploadOrDownloadTimeout;
                    } catch (SecurityException e) {
                        LOGGER.warn("Denied read access to the  " + file.getName());
                    }
                }
                return uploadOrDownloadTimeout;
            }
        };
        try {
            return launchWork(calculateUploadAndDownloadTimeTask, uploadOrDownloadTimeout);
        } catch (InterruptedException e) {
            return uploadOrDownloadTimeout;
        }
    }

    private <T> T launchWork(Callable<T> task, long currentTimeout) throws InterruptedException, FTPClientException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> result = executor.submit(task);
        executor.shutdown();
        if (executor.awaitTermination(currentTimeout, TimeUnit.SECONDS)) {
            return getResult(result);
        } else {
            result.cancel(true);
            throw new InterruptedException();
        }
    }

    private <T> T launchWork(Callable<T> task, List<EDIFile> listEdiFile, long currentTimeout) throws InterruptedException, FTPClientException {
        long currentListEdiFileSize = -1;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> result = executor.submit(task);
        executor.shutdown();
        while (listEdiFile.size() != currentListEdiFileSize) {
            currentListEdiFileSize = listEdiFile.size();
            Thread.sleep(currentTimeout * 1000);
            if (executor.isTerminated()) {
                return getResult(result);
            }
        }
        result.cancel(true);
        throw new InterruptedException();
    }

    private <T> T getResult(Future<T> result) throws InterruptedException, FTPClientException {
        try {
            return result.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof FTPClientException) {
                throw (FTPClientException) e.getCause();
            }
            throw new FTPClientException(e.getMessage(), "", e);
        } finally {
            result.cancel(true);
        }
    }
}
