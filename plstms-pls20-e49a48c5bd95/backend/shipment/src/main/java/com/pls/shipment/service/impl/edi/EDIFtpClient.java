package com.pls.shipment.service.impl.edi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.pb.x12.Cf;
import org.pb.x12.X12;
import org.pb.x12.X12Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.FTPClientException;
import com.pls.core.service.util.OutboundEdiQueueMappingUtils;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.edi.enums.FTPActionEnum;
import com.pls.shipment.service.impl.edi.parser.enums.EDIBaseElement;
import com.pls.shipment.service.impl.edi.utils.EDIFtpMonitoringUtils;
import com.pls.shipment.service.impl.edi.utils.EDIUtils;

/**
 * FTP client for working with EDI files.
 * FTP client expects that server's folder structure is following
 * <p/>
 * <pre>
 * +--{SCAC}                //SCAC of the Carrier
 * |  +--in                 //Files sent by carrier to PLS. Incoming files
 * |  |  +--{EDI type}      //Type of file. E.g. 210, 204 etc.
 * |  |  |  +--failed       //Files that failed to process
 * |  |  |  |--processed    //Successfully processed files
 * |  +--out                //The files that should be sent to carrier from PLS PRO
 * |
 * </pre>
 *
 * @author Mikhail Boldinov, 03/09/13
 */
@Component
public class EDIFtpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EDIFtpClient.class);

    private static final String IN_FOLDER_NAME = "in";
    private static final String OUT_FOLDER_NAME = "out";
    private static final String PROCESSED_FOLDER_NAME = "processed";
    private static final String PARTIALLY_PROCESSED_FOLDER_NAME = "split_originals";
    private static final String FAILED_FOLDER_NAME = "failed";
    private static final String DOCUMENTS_FOLDER_NAME = "documents";
    private static final String[] NON_EDI_FILE_EXTENSIONS = new String[] { "doc", "docx", "xls", "xlsx", "ppt", "pptx", "fpx", "gif", "j2c",
            "j2k", "jfif", "jif", "jp2", "jpeg", "jpg", "jpx", "pcd", "pdf", "png", "tif", "tiff", "bmp" };

    @Value("${ftp.serverAddress}")
    private String serverAddress;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.userId}")
    private String userName;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.remoteDirectory}")
    private String remoteDir;

    @Value("${ftp.backup.serverAddress}")
    private String backupServerAddress;

    @Value("${ftp.backup.port}")
    private int backupPort;

    @Value("${ftp.backup.userId}")
    private String backupUserName;

    @Value("${ftp.backup.password}")
    private String backupPassword;

    @Value("${ftp.backup.remoteDirectory}")
    private String backupRemoteDir;

    @Autowired
    private OutboundEdiQueueMappingUtils outboundEdiQueueUtils;

    @Autowired
    EDIFtpMonitoringUtils ediFtpMonitoringUtils;

    /**
     * Lists all EDI files available for import.
     *
     * @return list of {@link EDIFile}
     * @throws FTPClientException
     *             thrown if FTP server is not accessible
     * @throws InterruptedException
     *             if insufficient timeout to execute this method
     */
    public List<EDIFile> listFiles() throws FTPClientException, InterruptedException {
        List<EDIFile> ediFiles = Collections.synchronizedList(new ArrayList<EDIFile>());
        FTPTask<List<EDIFile>> getEdiFilesTask = new FTPTask<List<EDIFile>>(FTPActionEnum.GET_EDI_FILES_FOR_CARRIERS, ediFiles);
        return ediFtpMonitoringUtils.executeTask(getEdiFilesTask, ediFiles);
    }

    private List<EDIFile> getEdiFilesForCarriersAsynchronously(List<EDIFile> ediFiles) throws FTPClientException {
        FTPClient client = null;
        try {
            client = createFtpConnection(serverAddress, port, userName, password);
            client.changeWorkingDirectory(remoteDir);
            for (FTPFile carrierDirectory : client.listDirectories()) {
                if (".".equals(carrierDirectory.getName()) || "..".equals(carrierDirectory.getName())) {
                    continue;
                }
                if (!outboundEdiQueueUtils.isQueueEnabled(carrierDirectory.getName())) {
                    ediFiles.addAll(getEdiFilesForCarrier(carrierDirectory.getName(), client));
                }
            }
        } catch (IOException e) {
            throw new FTPClientException("FTP input/output error", client.getReplyString(), e);
        } finally {
            closeFtpConnection(client);
        }
        return ediFiles;
    }

    /**
     * Downloads EDI file content.
     *
     * @param ediFile
     *            {@link EDIFile}
     * @return {@link EDIFile} content
     * @throws FTPClientException
     *             thrown if FTP server is not accessible
     * @throws InterruptedException
     *             if insufficient timeout to execute this method
     */
    public File downloadFile(EDIFile ediFile) throws FTPClientException, InterruptedException {
        FTPTask<File> downloadTask = new FTPTask<File>(FTPActionEnum.DOWNLOAD_FILE, ediFile);
        return ediFtpMonitoringUtils.executeTask(downloadTask, ediFile.getFile());
    };

    private File downloadFileAsynchronously(EDIFile ediFile) throws FTPClientException {
        File file = retrieveFileContent(ediFile.getFilePath());
        moveEDIFileToAppropriateCarrierFolder(ediFile, file);
        return file;
    }

    private void moveEDIFileToAppropriateCarrierFolder(EDIFile ediFile, File file) throws FTPClientException {
        InputStream fileContent = null;
        X12 x12 = null;
        String fileForDelete = ediFile.getFilePath();
        try {
            fileContent = EDIUtils.trimFile(file);
            Cf config = new Cf("");
            config.addChild(EDIBaseElement.ISA_SCAC.getSegment(), EDIBaseElement.ISA_SCAC.getSegment());
            x12 = (X12) new X12Parser(config).parse(fileContent);
            String scac = x12.findLoop(EDIBaseElement.ISA_SCAC.getSegment()).get(0).getSegment(0)
                    .getElement(EDIBaseElement.ISA_SCAC.getIndex()).trim();
            if (!StringUtils.equals(ediFile.getCarrierScac(), scac)) {
                ediFile.setFile(file);
                saveFile(ediFile, remoteDir, scac, IN_FOLDER_NAME, ediFile.getTransactionSet().getId());
                deleteFile(fileForDelete);
                throw new ApplicationException(String.format("EDI %s file '%s' is moved from folder %s to folder %s",
                        ediFile.getTransactionSet().getId(), ediFile.getName(), ediFile.getCarrierScac(), scac));
            }
            deleteFile(fileForDelete);
        } catch (ApplicationException e) {
            throw new FTPClientException("Inorrect carrier folder", e.getMessage(), e);
        } catch (Exception e) {
            /*
             Original EDI file should be deleted if some unexpected exception occurs. For Example if EDI
             file was not parsed successfully. In this case copy of EDI file will be placed in failed
             folder.
             */
            deleteFile(fileForDelete);
            LOGGER.error(String.format("Error moving EDI File '%s' to appropriate carrier folder. %s", ediFile.getName(), e.getMessage()), e);
        } finally {
            IOUtils.closeQuietly(fileContent);
        }
    }

    private void deleteFile(String fileForDelete) throws FTPClientException {
        FTPClient client = null;
        try {
            client = createFtpConnection(serverAddress, port, userName, password);
            client.deleteFile(fileForDelete);
        } catch (IOException e) {
            String message = String.format("EDI file %s can't be deleted. %s", fileForDelete, e.getMessage());
            throw new FTPClientException(message, client.getReplyString(), e);
        } finally {
            closeFtpConnection(client);
        }
    }

    /**
     * Uploads EDI file to ftp server.
     *
     * @param file {@link EDIFile}
     * @return <code>true</code> if file uploaded successfully, otherwise <code>false</code>
     * @throws FTPClientException thrown if FTP server is not accessible
     * @throws InterruptedException thrown if insufficient timeout to execute this method
     */
    public boolean uploadFile(EDIFile file) throws FTPClientException, InterruptedException {
        FTPTask<Boolean> uploadTask = new FTPTask<Boolean>(FTPActionEnum.UPLOAD_FILE, file);
        return ediFtpMonitoringUtils.executeTask(uploadTask, file.getFile());
    }

    private Boolean uploadFileAsynchronously(EDIFile file) throws FTPClientException {
        return saveFile(file, remoteDir, file.getCarrierScac(), OUT_FOLDER_NAME, file.getTransactionSet().getId());
    }

    /**
     * Puts EDI file into directory with successfully processed EDI files.
     * 
     * @param file
     *            {@link EDIFile}
     * @return true if successfully completed, false if not
     * @throws FTPClientException
     *             thrown if FTP server is not accessible
     */
    public boolean markAsProcessed(EDIFile file) throws FTPClientException {
        LOGGER.info(String.format("Marking EDI '%s' as processed", file.getName()));
        return saveFile(file, remoteDir, file.getCarrierScac(), IN_FOLDER_NAME, file.getTransactionSet().getId(),
                PROCESSED_FOLDER_NAME);
    }

    /**
     * Puts EDI file into directory with processed EDI files that contain some failed transactions.
     *
     * @param file
     *            {@link EDIFile}
     * @return true if successfully completed, false if not
     * @throws FTPClientException
     *             thrown if FTP server is not accessible
     */
    public boolean markAsPartiallyFailed(EDIFile file) throws FTPClientException {
        LOGGER.info(String.format("Marking EDI '%s' as partially failed", file.getName()));
        return saveFile(file, remoteDir, file.getCarrierScac(), IN_FOLDER_NAME, file.getTransactionSet().getId(),
                PARTIALLY_PROCESSED_FOLDER_NAME);
    }

    /**
     * Puts EDI file into directory with EDI files which import is failed.
     *
     * @param file
     *            {@link EDIFile}
     * @return true if successfully completed, false if not
     * @throws FTPClientException
     *             thrown if FTP server is not accessible
     */
    public boolean markAsFailed(EDIFile file) throws FTPClientException {
        LOGGER.info(String.format("Marking EDI '%s' as failed", file.getName()));
        return saveFile(file, remoteDir, file.getCarrierScac(), IN_FOLDER_NAME, file.getTransactionSet().getId(),
                FAILED_FOLDER_NAME);
    }

    /**
     * Uploads EDI file to backup ftp server.
     *
     * @param file
     *            file {@link EDIFile}
     * @return <code>true</code> if file uploaded successfully, otherwise <code>false</code>
     * @throws FTPClientException
     *             thrown if FTP server is not accessible
     */
    public boolean backupFile(EDIFile file) throws FTPClientException {
        FTPClient backupClient = null;
        try {
            backupClient = createFtpConnection(backupServerAddress, backupPort, backupUserName, backupPassword);
            return saveFileForExistingClient(backupClient, file, backupRemoteDir, file.getCarrierScac(),
                    OUT_FOLDER_NAME, file.getTransactionSet().getId());
        } finally {
            closeFtpConnection(backupClient);
        }
    }

    private static FTPClient createFtpConnection(String address, int port, String userName, String password)
            throws FTPClientException {
        FTPClient client = new FTPClient();
        try {
            client.connect(address, port);
            client.login(userName, password);
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            throw new FTPClientException("Unable to connect to FTP server", client.getReplyString(), e);
        }
        return client;
    }

    private void closeFtpConnection(FTPClient client) throws FTPClientException {
        if (client == null) {
            return;
        }
        try {
            client.logout();
            client.disconnect();
        } catch (IOException e) {
            throw new FTPClientException("Unable to disconnect from FTP server", client.getReplyString(), e);
        }
    }

    private List<EDIFile> getEdiFilesForCarrier(String scac, FTPClient client) throws IOException {
        List<EDIFile> ediFiles = new ArrayList<EDIFile>();
        final String currentPath = buildPath(remoteDir, scac, IN_FOLDER_NAME);
        for (FTPFile typeDirectory : client.listDirectories(currentPath)) {
            if (".".equals(typeDirectory.getName()) || "..".equals(typeDirectory.getName())) {
                continue;
            }
            EDITransactionSet transactionSet;
            try {
                transactionSet = EDITransactionSet.getById(typeDirectory.getName());
            } catch (IllegalArgumentException e) {
                continue;
            }
            ediFiles.addAll(getEDIFilesForCarrierOfSprcifiedType(buildPath(currentPath, typeDirectory.getName()), scac,
                    transactionSet, client));
        }
        return ediFiles;
    }

    private List<EDIFile> getEDIFilesForCarrierOfSprcifiedType(String path, String scac,
            EDITransactionSet transactionSet, FTPClient client) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Reading files for carrier %s", scac));
        }
        List<EDIFile> ediFiles = new ArrayList<EDIFile>();
        for (FTPFile file : client.listFiles(path)) {
            if (file.isFile()) {
                if (isNotEDIFile(file.getName())) {
                    moveToDocumentsFolder(file, path, client);
                } else {
                    ediFiles.add(createEDIFile(file.getName(), scac, transactionSet, buildPath(path, file.getName())));
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Found %d EDI file(s) for carrier %s", ediFiles.size(), scac));
        }
        return ediFiles;
    }

    private void moveToDocumentsFolder(FTPFile file, String path, FTPClient client) throws IOException {
        String originFileName = buildPath(path, file.getName());
        String destinationFileName = buildPath(path, DOCUMENTS_FOLDER_NAME, file.getName());
        createDirTree(client, buildPath(path, DOCUMENTS_FOLDER_NAME));
        if (!client.rename(originFileName, destinationFileName)) {
            LOGGER.warn(String.format("Unable to move file %s from folder %s to folder %s", file.getName(), path, DOCUMENTS_FOLDER_NAME));
        } else {
            LOGGER.info(String.format("Successfully moved file %s to %s folder", originFileName, DOCUMENTS_FOLDER_NAME));
        }
    }

    private boolean isNotEDIFile(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        extension = StringUtils.lowerCase(extension);
        return ArrayUtils.contains(NON_EDI_FILE_EXTENSIONS, extension);
    }

    private EDIFile createEDIFile(String fileName, String carrierScac, EDITransactionSet transactionSet, String filePath) {
        EDIFile ediFile = new EDIFile();
        ediFile.setName(fileName);
        ediFile.setCarrierScac(carrierScac);
        ediFile.setTransactionSet(transactionSet);
        ediFile.setFilePath(filePath);
        return ediFile;
    }

    private File retrieveFileContent(String filePath) throws FTPClientException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Reading content of EDI file '%s'", filePath));
        }
        FileOutputStream tempFileOutputStream = null;
        InputStream inputStream = null;
        File tempFile = null;
        FTPClient client = null;
        try {
            client = createFtpConnection(serverAddress, port, userName, password);
            tempFile = File.createTempFile("ediFile", "tmp");
            tempFile.deleteOnExit();
            tempFileOutputStream = new FileOutputStream(tempFile);
            inputStream = client.retrieveFileStream(filePath);
            IOUtils.copy(inputStream, tempFileOutputStream);
            client.completePendingCommand();
        } catch (IOException e) {
            throw new FTPClientException("Unable to retreive file content", client.getReplyString(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(tempFileOutputStream);
            closeFtpConnection(client);
        }

        return tempFile;
    }

    private boolean saveFile(EDIFile file, String... dirTree) throws FTPClientException {
        FTPClient client = null;
        try {
            client = createFtpConnection(serverAddress, port, userName, password);
            return saveFileForExistingClient(client, file, dirTree);
        } finally {
            closeFtpConnection(client);
        }
    }

    private boolean saveFileForExistingClient(FTPClient client, EDIFile file, String... dirTree) throws FTPClientException {
        try {
            createDirTree(client, dirTree);
            String filePath = buildPath(dirTree);
            client.changeWorkingDirectory(filePath);
            InputStream inputStream = null;
            try {
                inputStream = file.getNewFileContent();
                return client.storeFile(file.getName(), inputStream);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        } catch (IOException e) {
            throw new FTPClientException("Unable to upload the file " + file.getName(), client.getReplyString(), e);
        }
    }

    private void createDirTree(FTPClient client,  String... dirTree) throws IOException {
        for (String dir : dirTree) {
            boolean dirExists = client.changeWorkingDirectory(dir);
            if (!dirExists) {
                if (!client.makeDirectory(dir)) {
                    throw new IOException("Unable to create remote directory '" + dir + "'");
                }
                if (!client.changeWorkingDirectory(dir)) {
                    throw new IOException("Unable to change into newly created remote directory '" + dir + "'");
                }
            }
        }
    }

    private static String buildPath(String... pathNames) {
        StringBuilder path = new StringBuilder();
        boolean first = true;
        for (String pathName : pathNames) {
            if (first) {
                path.append(pathName);
                first = false;
            } else {
                path.append('/').append(pathName);
            }
        }
        return path.toString();
    }

    /**
     * Class to create asynchronous tasks and return the result. It is used to execute in parallel thread
     * operations for EDI, that run on FTP.
     * 
     * @author Brichak Aleksandr
     * 
     * @param <T>
     *            generic type of return result
     */
    public class FTPTask<T> implements Callable<T> {

        private final FTPActionEnum methodName;
        private final EDIFile ediFile;
        private final List<EDIFile> listEdiFile;

        /**
         * Constructor with necessary parameters.
         * 
         * @param methodName
         *            name of method to execute
         * @param listEdiFile
         *            ediFile for carriers
         */
        public FTPTask(FTPActionEnum methodName, List<EDIFile> listEdiFile) {
            this.methodName = methodName;
            this.listEdiFile = listEdiFile;
            this.ediFile = null;
        }

        /**
         * Constructor with necessary parameters.
         * 
         * @param methodName
         *            name of method to execute
         * @param ediFile
         *            ediFile for download or upload
         */
        public FTPTask(FTPActionEnum methodName, EDIFile ediFile) {
            this.methodName = methodName;
            this.ediFile = ediFile;
            this.listEdiFile = null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T call() throws InterruptedException, FTPClientException {
            LOGGER.info("call " + methodName);
            stopThreadIfItWasInterrupted();
            switch (methodName) {
            case DOWNLOAD_FILE:
                return (T) downloadFileAsynchronously(ediFile);
            case UPLOAD_FILE:
                return (T) uploadFileAsynchronously(ediFile);
            case GET_EDI_FILES_FOR_CARRIERS:
                return (T) getEdiFilesForCarriersAsynchronously(listEdiFile);
            default:
                throw new IllegalArgumentException("Method is not defined");
            }
        }

        private void stopThreadIfItWasInterrupted() {
            final Thread currentThread = Thread.currentThread();
            new Thread(new Runnable() {
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    while (currentThread.isAlive()) {
                        if (currentThread.isInterrupted()) {
                            LOGGER.info("Force STOP thread " + currentThread.getName());
                            currentThread.stop();
                            return;
                        }
                    }
                }
            }).start();
        }
    }

}
