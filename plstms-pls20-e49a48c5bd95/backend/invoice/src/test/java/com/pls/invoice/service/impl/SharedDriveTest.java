package com.pls.invoice.service.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pls.core.exception.ApplicationException;
import com.pls.invoice.service.SharedDriveService;

/**
 * The Class SharedDriveTest.
 * @author Sergii Belodon
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:spring/testContext.xml" })
public class SharedDriveTest {
    private static final String TEST_FILE = "sharedDriveTestFile.txt";
    private static final String TEST_FILE2 = "sharedDriveTestFile2.txt";
    private static final String TEST_FILE3 = "sharedDriveTestFile3.txt";
    @Autowired
    private SharedDriveService sharedDriveService;

    /**
     * Connect to shared drive.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void connectToSharedDriveTest1() throws ApplicationException, IOException {
        ClassPathResource testFileResource = new ClassPathResource(TEST_FILE);
        long bytesCopied = sharedDriveService.connectAndStoreFile(TEST_FILE, testFileResource.getFile().getPath());
        assertEquals(6L, bytesCopied);
    }

    /**
     * Connect to shared drive.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void connectToSharedDriveTest2() throws ApplicationException, IOException {
        ClassPathResource testFileResource2 = new ClassPathResource(TEST_FILE2);
        ClassPathResource testFileResource3 = new ClassPathResource(TEST_FILE3);
        Map<String, String> files = new HashMap<String, String>(2);
        files.put(TEST_FILE2, testFileResource2.getFile().getPath());
        files.put(TEST_FILE3, testFileResource3.getFile().getPath());
        Map<String, Long> bytesCopied = sharedDriveService.connectAndStoreFiles(files);
        assertEquals(new Long(11), bytesCopied.get(TEST_FILE2));
        assertEquals(new Long(9), bytesCopied.get(TEST_FILE3));
    }
}
