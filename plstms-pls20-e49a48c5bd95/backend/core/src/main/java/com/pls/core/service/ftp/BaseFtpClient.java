package com.pls.core.service.ftp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.pls.core.exception.FTPClientException;

/**
 * Handles ftp operation. This is a prototype scoped bean, means each service will have it's own copy of the BaseFtpClient.
 * Also proxyMode was set to TARGET_CLASS in order to have new instance of the BaseFtpClient
 * when any service calls public methods of the BaseFtpClient.
 *
 * @author Sergey Kirichenko
 * @author Denis Zhupinsky (Team International)
 */
@Service("baseFtpClient")
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BaseFtpClient {
    public static final String PATH_SEPARATOR = "/";

    public static final int DEFAULT_PORT = 21;

    protected FTPClient client = new FTPClient();

    /**
     * Returns connected FTPClient.
     *
     * @param ftpInfo - ftp connection url
     * @param port ftp port. If null, default will be used
     * @param userName - ftp user name
     * @param password - ftp user password
     * @return {@link FTPClient}
     * @throws FTPClientException if can't connect to the FTP server.
     */
    public FTPClient connect(String ftpInfo, Integer port, String userName, String password) throws FTPClientException {
        try {
            if (port != null) {
                client.connect(ftpInfo, port);
            } else {
                client.connect(ftpInfo);
            }

            client.login(userName, password);
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            return client;
        } catch (IOException e) {
            throw new FTPClientException("Unable to connect to FTP server", client.getReplyString(), e);
        }
    }

    /**
     * Close connection to the FTP server.
     *
     * @throws FTPClientException if can't close connection
     */
    public void close() throws FTPClientException {
        try {
            if (client != null && client.isConnected()) {
                client.logout();
                client.disconnect();
            }
        } catch (IOException e) {
            throw new FTPClientException("Unable to disconnect from FTP server", client.getReplyString(), e);
        }
    }

    /**
     * Upload file into the FTP server.
     *
     * @param ftpInfo - ftp connection url
     * @param port ftp port. If null, default will be used
     * @param userName - ftp user name
     * @param password - ftp user password
     * @param ftpPath - ftp working directory
     * @param fileName - file name to upload
     * @param inputStream stream to upload
     * @throws FTPClientException if there are any ftp errors.
     */
    public void uploadBinaryData(String ftpInfo, Integer port, String userName, String password, String ftpPath, String fileName,
            InputStream inputStream) throws FTPClientException {
        if (client == null) {
            throw new IllegalArgumentException("FTP client is not defined.");
        }
        if (client.isConnected()) {
            throw new IllegalStateException("FTP client is already connected.");
        }
        try {
            connect(ftpInfo, port, userName, password);
            changeWorkingDirectory(ftpPath);
            saveContent(fileName, inputStream);
        } finally {
            close();
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Upload file into the FTP server.
     *
     * @param fileName - file name to upload
     * @param data - file content as a stream
     * @throws FTPClientException if file can't be uploaded
     */
    public void saveContent(String fileName, InputStream data) throws FTPClientException {
        checkFTPClient();
        if (StringUtils.isBlank(fileName) || data == null) {
            throw new IllegalArgumentException("Content to upload into FTP server is not defined.");
        }
        try {
            client.storeFile(fileName, data);
        } catch (IOException e) {
            throw new FTPClientException("Unable to upload the file " + fileName, client.getReplyString(), e);
        }
    }

    /**
     *
     * Ping ftp server. If there is possibility to store files. Creates and deletes temporary file.
     *
     * @param serverAddress address of ftp server
     * @param userName username to connect to ftp server
     * @param password password to connect to ftp server
     * @param ftpPath path on server
     * @return <code>true</code> if server available for writing
     * @throws FTPClientException if ping fails
     */
    public boolean ping(String serverAddress, String userName, String password, String ftpPath) throws FTPClientException {
        connect(serverAddress, DEFAULT_PORT, userName, password);
        changeWorkingDirectory(ftpPath);
        /*String fileName = "pingFile" + System.nanoTime() + ".tmp"; todo check write permission. Uncomment if needed
        InputStream inputStream = null;
        try {

            inputStream = new ByteArrayInputStream("ping".getBytes());
            client.storeFile(fileName, inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        client.deleteFile(fileName);*/
        close();

        return true;
    }

    /**
     * Change working directory. If working directory doesn't exist then creates it and after changes it.
     *
     * @param ftpPath - ftp working directory
     * @throws FTPClientException if can't change working directory or create new one
     */
    protected void changeWorkingDirectory(String ftpPath) throws FTPClientException {
        checkFTPClient();
        String[] dirs = StringUtils.split(ftpPath, PATH_SEPARATOR);
        try {
            for (String dir : dirs) {
                if (!client.changeWorkingDirectory(dir)) {
                    if (!client.makeDirectory(dir)) {
                        throw new FTPClientException("Unable to create remote directory '" + dir + "'", client.getReplyString());
                    }
                    if (!client.changeWorkingDirectory(dir)) {
                        throw new FTPClientException("Unable to change into newly created remote directory '" + dir + "'", client.getReplyString());
                    }
                }
            }
        } catch (IOException e) {
            throw new FTPClientException("Unable to change working directory '" + ftpPath + "'", client.getReplyString(), e);
        }
    }

    private void checkFTPClient() {
        if (client == null) {
            throw new IllegalArgumentException("FTP client is not defined.");
        }
        if (!client.isConnected()) {
            throw new IllegalStateException("FTP client is not connected.");
        }
    }
}
