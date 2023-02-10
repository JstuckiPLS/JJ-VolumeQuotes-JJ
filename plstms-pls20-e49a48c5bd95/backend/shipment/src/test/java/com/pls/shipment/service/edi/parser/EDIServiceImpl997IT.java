package com.pls.shipment.service.edi.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.shipment.service.edi.EDIService;
import com.pls.shipment.service.impl.edi.EDIFtpClient;

/**
 * EDI service tests.
 * 
 * @author Aleksandr Leshchenko
 */
public class EDIServiceImpl997IT extends BaseServiceITClass {

    private static final String EDI_214_FILE_NAME = "CTSI214_cropped.txt";
    private static final String EDI_210_FILE_NAME = "CTSI210_cropped.txt";
    private static final String EDI_997_214 = "EDI997_CTSI214_cropped";
    private static final String EDI_997_210 = "EDI997_CTSI210_cropped";
    private static final String LOCAL_EDI_997_214 = "EDI997_CTSI214_cropped.txt";
    private static final String LOCAL_EDI_997_210 = "EDI997_CTSI210_cropped.txt";

    private static final Pattern ISA_SEGMENT_PATTERN = Pattern.compile("ISA[*][^*]{2}[*][^*]{10}[*][^*]{2}[*][^*]{10}[*][^*]{2}[*][^*]{15}[*]"
            + "[^*]{2}[*][^*]{15}([*]\\d{6}[*]\\d{4}[*])[^*][*][^*]{5}[*]([^*]+)[*][^*]+[*][^*]+[*][\\^]");
    private static final Pattern GS_SEGMENT_PATTERN = Pattern.compile("GS[*][^*]*[*][^*]*[*][^*]*([*]\\d{8}[*]\\d{4}[*])([^*]*)[*][^*]*[*][^*]*");

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

    @Value("${admin.personId}")
    private Long adminPersonId;

    private String edi997Dir;

    @Before
    public void before() {
        ReflectionTestUtils.setField(ftpClient, "serverAddress", "127.0.0.1");
        ReflectionTestUtils.setField(ftpClient, "port", PORT);
        ReflectionTestUtils.setField(ftpClient, "backupServerAddress", "127.0.0.1");
        ReflectionTestUtils.setField(ftpClient, "backupPort", PORT);
    }

    @Test
    public void responseOn210and214() throws IOException, InterruptedException {
        SecurityTestUtils.logout();
        SecurityTestUtils.login("ediadmin", adminPersonId);
        FakeFtpServer fakeFtpServer = createFakeFtpServer();
        fakeFtpServer.start();

        ediService.receiveEDI();

        @SuppressWarnings("unchecked")
        List<FileSystemEntry> listFiles = fakeFtpServer.getFileSystem().listFiles(edi997Dir + "/");
        Assert.assertEquals(2, listFiles.size());

        verify997ResultFile(listFiles, EDI_997_210, LOCAL_EDI_997_210);
        verify997ResultFile(listFiles, EDI_997_214, LOCAL_EDI_997_214);

        fakeFtpServer.stop();
    }

    private void verify997ResultFile(List<FileSystemEntry> listFiles, String resultFileName, String localFileName) throws IOException {
        FileSystemEntry resultFile = findResultFile(resultFileName, listFiles);
        Assert.assertNotNull(resultFile);
        String actualResultFileContent = replaceDateTimeData(IOUtils.toString(((FileEntry) resultFile).createInputStream()));
        InputStream localFile = ClassLoader.getSystemResourceAsStream("edi" + File.separator + localFileName);
        String expectedResultFileContent = replaceDateTimeData(IOUtils.toString(localFile));
        Assert.assertEquals(expectedResultFileContent, actualResultFileContent);
    }

    private String replaceDateTimeData(String ediContent) {
        ediContent = replaceDateTimeAndSequencesData(ediContent, ISA_SEGMENT_PATTERN);
        ediContent = replaceDateTimeAndSequencesData(ediContent, GS_SEGMENT_PATTERN);
        return ediContent;
    }

    private static String replaceDateTimeAndSequencesData(String ediContent, Pattern pattern) {
        Matcher matcher = pattern.matcher(ediContent);
        if (matcher.find()) {
            ediContent = ediContent.replace(matcher.group(1), "*");
            if (matcher.groupCount() > 1 && !StringUtils.isEmpty(matcher.group(2))) {
                ediContent = ediContent.replace(matcher.group(2), "*");
            }
        }
        return ediContent;
    }

    private FileSystemEntry findResultFile(String resultFileName, List<FileSystemEntry> listFiles) {
        for (FileSystemEntry file : listFiles) {
            if (StringUtils.contains(file.getName(), resultFileName)) {
                return file;
            }
        }
        return null;
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
        String outDir = scacDir + "/out";
        String edi214Dir = inDir + "/214";
        String edi210Dir = inDir + "/210";
        edi997Dir = outDir + "/997";
        fileSystem.add(new DirectoryEntry(edi214Dir + "/processed"));
        fileSystem.add(new DirectoryEntry(edi214Dir + "/failed"));
        fileSystem.add(new DirectoryEntry(edi214Dir + "/split_originals"));
        fileSystem.add(new DirectoryEntry(edi210Dir + "/processed"));
        fileSystem.add(new DirectoryEntry(edi210Dir + "/failed"));
        fileSystem.add(new DirectoryEntry(edi210Dir + "/split_originals"));
        fileSystem.add(new DirectoryEntry(edi997Dir));
        fileSystem.add(new DirectoryEntry("/PLS/archive/Carriers/RDWY/out/997"));

        InputStream edi210InputStream = ClassLoader.getSystemResourceAsStream("edi" + File.separator
                + EDI_210_FILE_NAME);
        String edi210Contents = IOUtils.toString(edi210InputStream);
        fileSystem.add(new FileEntry(edi210Dir + "/" + EDI_210_FILE_NAME, edi210Contents));

        InputStream edi214InputStream = ClassLoader.getSystemResourceAsStream("edi" + File.separator
                + EDI_214_FILE_NAME);
        String edi214Contents = IOUtils.toString(edi214InputStream);
        fileSystem.add(new FileEntry(edi214Dir + "/" + EDI_214_FILE_NAME, edi214Contents));

        return fileSystem;
    }
}
