package com.pls.shipment.service.edi.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
import com.pls.shipment.dao.CarrierInvoiceDetailsDao;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.service.edi.EDIService;
import com.pls.shipment.service.impl.edi.EDIFtpClient;

/**
 * EDI service tests.
 * @author Stas Norochevskiy
 *
 */
public class EDIServiceImplITestIT extends BaseServiceITClass {

    private static final String EDI_FILE_NAME = "CTSI210_cropped.txt";
    private static final String EDI_210_FAILED_FILE_NAME = "CTSI210_cropped_1.txt";
    private static final String EDI_210_PROCESSED_FILE_NAME = "CTSI210_cropped_2.txt";
    private static final String EDI_210_ORIGIN_FILE_NAME = "CTSI210_cropped_original.txt";

    private static final int PORT = 4856;

    @Autowired
    private EDIService ediService;

    @Autowired
    private CarrierInvoiceDetailsDao carrierInvoiceDetailsDao;

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

    private String scacDir;
    private String inDir;
    private String outDir;
    private String edi210Dir;
    private String processedEdi210Dir;
    private String failedEdi210Dir;
    private String originalEdi210Dir;

    @Before
    public void before() {
        ReflectionTestUtils.setField(ftpClient, "serverAddress", "127.0.0.1");
        ReflectionTestUtils.setField(ftpClient, "port", PORT);
        ReflectionTestUtils.setField(ftpClient, "backupServerAddress", "127.0.0.1");
        ReflectionTestUtils.setField(ftpClient, "backupPort", PORT);
    }

    @Test
    public void testProcessIncomingEDIFiles() throws IOException, InterruptedException {
        getSession().createQuery("update LoadEntity set finalizationStatus = 'ABH' where id = 720").executeUpdate();

        List<CarrierInvoiceDetailsEntity> carrierInvoicesInDb;
        carrierInvoicesInDb = carrierInvoiceDetailsDao.getAll();
        int orlCarrierInvoicesNumber = carrierInvoicesInDb.size();

        FakeFtpServer fakeFtpServer = createFakeFtpServer();
        fakeFtpServer.start();

        ediService.receiveEDI();

        carrierInvoicesInDb = carrierInvoiceDetailsDao.getAll();
        int newCarrierInvoicesNumber = carrierInvoicesInDb.size();
        Assert.assertEquals(orlCarrierInvoicesNumber + 1, newCarrierInvoicesNumber);

        //validateFiles(fakeFtpServer, processedEdi210Dir, EDI_210_PROCESSED_FILE_NAME);
        //validateFiles(fakeFtpServer, originalEdi210Dir, EDI_210_ORIGIN_FILE_NAME);
        //validateFiles(fakeFtpServer, failedEdi210Dir, EDI_210_FAILED_FILE_NAME);

        fakeFtpServer.stop();

        Assert.assertEquals(1L, getSession().createQuery("select count(*) from LdBillingAuditReasonsEntity where loadId = 720").uniqueResult());
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

        scacDir = remoteDir + "/RDWY";
        inDir = scacDir + "/in";
        outDir = scacDir + "/out";
        edi210Dir = inDir + "/210";
        processedEdi210Dir = edi210Dir + "/processed";
        originalEdi210Dir = edi210Dir + "/split_originals";
        failedEdi210Dir = edi210Dir + "/failed";
        fileSystem.add(new DirectoryEntry(outDir + "/997"));
        fileSystem.add(new DirectoryEntry(processedEdi210Dir));
        fileSystem.add(new DirectoryEntry(originalEdi210Dir));
        fileSystem.add(new DirectoryEntry(failedEdi210Dir));
        fileSystem.add(new DirectoryEntry("/PLS/archive/Carriers/RDWY/out/997"));

        InputStream edi210InputStream = ClassLoader.getSystemResourceAsStream("edi" + File.separator + EDI_FILE_NAME);
        String edi210Contents = IOUtils.toString(edi210InputStream);
        fileSystem.add(new FileEntry(edi210Dir + "/" + EDI_FILE_NAME, edi210Contents));
        return fileSystem;
    }
}
