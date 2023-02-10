package com.pls.shipment.service.edi;

import com.pls.core.exception.EdiProcessingException;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.enums.EDITransactionSet;

import java.util.List;
import java.util.Map;

/**
 * Interface to provide contract for interacting with EDI files.
 * <p/>
 * Do not call methods of this service directly. Use {@link com.pls.shipment.service.edi.EDIService} instead to have asynchronous EDI processing.
 *
 * @author Mikhail Boldinov, 12/03/14
 */
public interface EDIWorker {

    /**
     * Method processes an EDI file added to EDI ftp by carriers.
     * <p/>
     * Namely:
     * <ul>
     * <li>downloads all incoming EDI files from EDI ftp server</li>
     * <li>parses downloaded files</li>
     * <li>moves all successfully parsed files to 'processed' directory</li>
     * <li>moves all unparsed files to 'failed' directory</li>
     * <li>saves entities created during files parsing into DB</li>
     * <li>performs all specific EDI format operations</li>
     * </ul>
     *
     * @param ediFile {@link EDIFile} to read
     */
    void readEDI(EDIFile ediFile);

    /**
     * Creates {@link EDIFile} from provided entities using appropriate {@link com.pls.shipment.service.edi.parser.EDIParser}
     * and uploads created EDI file to FTP server.
     *
     * @param carrierId      id of carrier which will receive the created EDI file
     * @param entityIds      ids of entities to export into EDI file
     * @param transactionSet {@link EDITransactionSet} of created file
     * @param params         EDI custom parameters map
     * @throws EdiProcessingException if edi file cannot be created
     */
    void writeEDI(Long carrierId, List<Long> entityIds, EDITransactionSet transactionSet, Map<String, Object> params)
            throws EdiProcessingException;
}
