package com.pls.invoice.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.pls.core.exception.ApplicationException;

/**
 * SharedDriveService class provides interface for interaction with shared drive.
 * @author Sergii Belodon
 */
public interface SharedDriveService {


    /**
     * Connect and store file.
     *
     * @param destFilename the destination filename (e.g. test.txt)
     * @param pathToSourceFile the path to source file (e.g. E:\test.txt)
     * @return quantity of bytes copied
     * @throws ApplicationException Signals that exception has occurred.
     */
    long connectAndStoreFile(String destFilename, String pathToSourceFile) throws ApplicationException;

    /**
     * Connect and store file.
     *
     * @param files map with filename (e.g. test.txt) and path to source file (e.g. E:\test.txt)
     * @return Map with filename and quantity of copied bytes
     * @throws ApplicationException Signals that exception has occurred.
     */
    Map<String, Long> connectAndStoreFiles(Map<String, String> files) throws ApplicationException;

    /**
     * Connect and store file.
     *
     * @param dirPath - path to dir (e.g. E:\testFolder)
     * @return Map with filename and quantity of copied bytes
     * @throws ApplicationException Signals that exception has occurred.
     */
    Map<String, Long> connectAndStoreFilesFromDir(String dirPath) throws ApplicationException;


    /**
     * Store invoice copy.
     *
     * @param filename the filename
     * @param file the file
     * @return the long
     * @throws ApplicationException - exception
     * @throws IOException - exception
     */
    long storeInvoiceCopy(String filename, File file) throws ApplicationException, IOException;
}
