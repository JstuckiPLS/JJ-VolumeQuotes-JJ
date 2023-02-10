package com.pls.shipment.service.edi.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.FileSystemEntry;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.shipment.service.edi.EDIService;
import com.pls.shipment.service.impl.edi.EDIFtpClient;

/**
 * EDI service tests.
 * 
 * @author Aleksandr Leshchenko
 */
public class EDIServiceImplPartialFailureIT extends BaseServiceITClass {

    private static final String EDI_214_FILE_NAME = "CTSI214_cropped.txt";
    private static final String EDI_214_FAILED_FILE_NAME = "CTSI214_cropped_1.txt";
    private static final String EDI_214_PROCESSED_FILE_NAME = "CTSI214_cropped_2.txt";
    private static final String EDI_214_ORIGIN_FILE_NAME = "CTSI214_cropped_original.txt";

    private static final int PORT = 4856;

    @Autowired
    private EDIService ediService;

    @Autowired
    private EDIFtpClient ftpClient;

    @Value("${ftp.serverAddress}")
    private String serverAddress;

    @Value("${ftp.userId}")
    private String userName;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.remoteDirectory}")
    private String remoteDir;

    private String processedEdi214Dir;
    private String failedEdi214Dir;
    private String originalEdi214Dir;

    @Before
    public void before() {
        ReflectionTestUtils.setField(ftpClient, "serverAddress", "127.0.0.1");
        ReflectionTestUtils.setField(ftpClient, "port", PORT);
        ReflectionTestUtils.setField(ftpClient, "backupServerAddress", "127.0.0.1");
        ReflectionTestUtils.setField(ftpClient, "backupPort", PORT);
    }

    @Test
    public void shouldProcessIncomingEDIFilesForMultipleSuccessAndFailedTransactions() throws IOException, InterruptedException {
        FakeFtpServer fakeFtpServer = createFakeFtpServer();
        fakeFtpServer.start();

        ediService.receiveEDI();

        //validateFiles(fakeFtpServer, processedEdi214Dir, EDI_214_PROCESSED_FILE_NAME);
       // validateFiles(fakeFtpServer, originalEdi214Dir, EDI_214_ORIGIN_FILE_NAME);
        //validateFiles(fakeFtpServer, failedEdi214Dir, EDI_214_FAILED_FILE_NAME);

        fakeFtpServer.stop();
    }

    private void validateFiles(FakeFtpServer fakeFtpServer, String ftpDir, String fileName) throws IOException {
        FileSystemEntry entry = fakeFtpServer.getFileSystem().getEntry(ftpDir + "/" + fileName);
        String processedEDIContent = IOUtils.toString(((FileEntry) entry).createInputStream());
        String expectedProcessedEDIContent = IOUtils.toString(ClassLoader.getSystemResourceAsStream("edi" + File.separator + fileName));
        Assert.assertEquals(expectedProcessedEDIContent, processedEDIContent);
    }

    private FakeFtpServer createFakeFtpServer() throws IOException {
        // Prepare fake FTP server server with EDI document
        FakeFtpServer fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(PORT);
        fakeFtpServer.addUserAccount(new UserAccount(userName, password, "/"));

        fakeFtpServer.setFileSystem(createFtpFileSystem());

        return fakeFtpServer;
    }

    private FileSystem createFtpFileSystem() throws IOException {
        FileSystem fileSystem = new UnixFakeFileSystem();

        String scacDir = remoteDir + "/RDWY";
        String inDir = scacDir + "/in";
        String outDir = scacDir + "/out/997";
        String edi214Dir = inDir + "/214";
        processedEdi214Dir = edi214Dir + "/processed";
        originalEdi214Dir = edi214Dir + "/split_originals";
        failedEdi214Dir = edi214Dir + "/failed";
        fileSystem.add(new DirectoryEntry(remoteDir));
        fileSystem.add(new DirectoryEntry(outDir));
        fileSystem.add(new DirectoryEntry(processedEdi214Dir));
        fileSystem.add(new DirectoryEntry(originalEdi214Dir));
        fileSystem.add(new DirectoryEntry(failedEdi214Dir));
        fileSystem.add(new DirectoryEntry("/PLS/archive/Carriers/RDWY/out/997"));

        InputStream edi214InputStream = ClassLoader.getSystemResourceAsStream("edi" + File.separator + EDI_214_FILE_NAME);
        String edi214Contents = IOUtils.toString(edi214InputStream);
        fileSystem.add(new FileEntry(edi214Dir + "/" + EDI_214_FILE_NAME, edi214Contents));

        return fileSystem;
    }
}
