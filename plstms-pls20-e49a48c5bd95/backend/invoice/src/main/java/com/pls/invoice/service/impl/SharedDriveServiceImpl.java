package com.pls.invoice.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.context.support.StandardServletEnvironment;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.exception.ApplicationException;
import com.pls.invoice.service.SharedDriveService;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import jcifs.util.LogStream;

/**
 * Implementation of iteraction with shared drive.
 * @author Sergii Belodon
 */

public class SharedDriveServiceImpl implements SharedDriveService, ResourceLoaderAware {

    private String[] locations;

    private String pathToFolder;

    private String pathToInvoiceCopiesFolder;

    private Resource propertiesFile;

    private ResourceLoader resourceLoader;

    @Autowired
    private StandardServletEnvironment environment;

    public void setLocations(String[] locations) {
        this.locations = Arrays.copyOf(locations, locations.length);
    }

    public void setPropertiesFile(Resource propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setEnvironment(StandardServletEnvironment environment) {
        this.environment = environment;
    }

     /**
     * Initialize shared drive properties.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @PostConstruct
    public void init()  throws IOException {
        for (String location : locations) {
            Resource resource = resourceLoader.getResource(this.environment.resolveRequiredPlaceholders(location));
            if (resource.exists()) {
                environment.getPropertySources().addFirst(new ResourcePropertySource(resource));
            }
        }
        Properties sharedDriveProperties = new Properties();
        sharedDriveProperties.load(propertiesFile.getInputStream());
        for (Object key:sharedDriveProperties.keySet()) {
            jcifs.Config.setProperty((String) key, environment.getProperty((String) key));
        }
        LogStream.setLevel(Integer.parseInt(environment.getProperty("jcifs.util.loglevel")));
        pathToFolder = environment.getProperty("sharedDrive.pathToFolder");
        pathToInvoiceCopiesFolder = environment.getProperty("sharedDrive.pathToInvoiceCopiesFolder");
    }

    @Override
    public long connectAndStoreFile(String destFilename, String pathToSourceFile) throws ApplicationException {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null);
        return copyFileToSharedDrive(destFilename, pathToSourceFile, auth);
    }

    @Override
    public Map<String, Long> connectAndStoreFiles(Map<String, String> files) throws ApplicationException {
        Map<String, Long> bytesCopiedInfo = new HashMap<String, Long>(files.size());
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null);
        for (Map.Entry<String, String> file : files.entrySet()) {
            bytesCopiedInfo.put(file.getKey(), copyFileToSharedDrive(file.getKey(), file.getValue(), auth));
        }
        return bytesCopiedInfo;
    }

    @Override
    public Map<String, Long> connectAndStoreFilesFromDir(String dirPath) throws ApplicationException {
        File dir = new File(dirPath);
        Map<String, String> files = new HashMap<String, String>();
        File[] listFiles = dir.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                files.put(file.getName(), file.getPath());
            }
            return connectAndStoreFiles(files);
        }
        return new HashMap<String, Long>();
    }

    private long copyFileToSharedDrive(String destFilename, String pathToSourceFile, NtlmPasswordAuthentication auth) throws ApplicationException {
        long bytesCopied = -1;
        SmbFileOutputStream out = null;
        FileInputStream input = null;
        try {
            String folderName = DateUtility.dateToString(Calendar.getInstance().getTime(), "MMddyyyy");
            SmbFile folder = new SmbFile(pathToFolder + folderName, auth);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            SmbFile file = new SmbFile(pathToFolder + folderName + System.getProperty("file.separator") + destFilename, auth);
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new SmbFileOutputStream(file);
            File srcFile = new File(pathToSourceFile);
            input = new FileInputStream(srcFile);
            bytesCopied = IOUtils.copyLarge(input, out);
            out.flush();
            if (srcFile.length() != bytesCopied) {
                throw new ApplicationException("Exception: Error while copying file.");
            }
        } catch (IOException exception) {
            throw new ApplicationException("Exception: " + exception.getMessage());
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(input);
        }
        return bytesCopied;
    }

    @Override
    public long storeInvoiceCopy(String filename, File srcFile) throws ApplicationException, IOException {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null);
        SmbFileOutputStream out = null;
        FileInputStream input = null;
        long bytesCopied = -1;
        try {
            SmbFile folder = new SmbFile(pathToInvoiceCopiesFolder, auth);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            SmbFile file = new SmbFile(pathToInvoiceCopiesFolder + System.getProperty("file.separator") + filename, auth);
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new SmbFileOutputStream(file);
            input = new FileInputStream(srcFile);
            bytesCopied = IOUtils.copyLarge(input, out);
            out.flush();
            if (srcFile.length() != bytesCopied) {
                throw new ApplicationException("Exception: Error while copying file.");
            }
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(input);
        }
        return bytesCopied;
    }
}
