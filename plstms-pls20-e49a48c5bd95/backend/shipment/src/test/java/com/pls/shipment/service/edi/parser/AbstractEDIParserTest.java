package com.pls.shipment.service.edi.parser;

import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.enums.EDITransactionSet;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Base class for EDI parsers tests.
 *
 * @author Mikhail Boldinov, 06/03/14
 */
public abstract class AbstractEDIParserTest {

    /**
     * Creates EDI file for test parsing.
     *
     * @param ediFileName      EDI file name
     * @param transactionSetId transaction set ID
     * @param scac             carrier SCAC
     * @return {@link EDIFile}
     * @throws IOException thrown if any I/O errors occur
     */
    protected EDIFile getEdiFile(String ediFileName, String transactionSetId, String scac) throws IOException {
        EDIFile ediFile = new EDIFile();
        ediFile.setName(ediFileName);
        ediFile.setTransactionSet(EDITransactionSet.getById(transactionSetId));
        ediFile.setCarrierScac(scac);
        ediFile.setFile(loadFile(ediFileName));
        return ediFile;
    }

    private File loadFile(String fileName) throws IOException {
        URL systemResource = getClass().getResource("/edi/" + fileName);
        if (systemResource != null) {
            String filePath = systemResource.getFile();
            Assert.assertNotNull(filePath);
            return new File(filePath);
        }
        return null;
    }

    /**
     * Deletes test EDI file.
     *
     * @param fileName file name
     * @return <code>true</code> if file has been deleted successfully, otherwise <code>false</code>
     */
    protected boolean deleteFile(String fileName) {
        File file = new File(fileName);
        return file.delete();
    }
}
