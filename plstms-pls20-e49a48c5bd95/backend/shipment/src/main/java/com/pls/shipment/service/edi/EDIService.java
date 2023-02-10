package com.pls.shipment.service.edi;

import com.pls.core.exception.EdiProcessingException;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.enums.EDITransactionSet;

import java.util.List;
import java.util.Map;

/**
 * EDI Service interface.
 *
 * @author Mikhail Boldinov, 30/08/13
 */
public interface EDIService {


    /**
     * Receives and processes all incoming EDI data.
     */
    void receiveEDI();

    /**
     * Sends EDI outgoing data.
     *
     * @param carrierId      carrier id to send EDI to
     * @param entityIds      list of entities ids to send in EDI
     * @param transactionSet {@link EDITransactionSet}
     * @param params         EDI custom parameters map
     * @throws EdiProcessingException if EDI data cannot be sent
     */
    void sendEDI(Long carrierId, List<Long> entityIds, EDITransactionSet transactionSet, Map<String, Object> params) throws EdiProcessingException;

    /**
     * Method send an EDI file added to EDI ftp by carriers.
     * 
     * @param ediFile
     *            {@link EDIFile}
     * @throws EdiProcessingException
     *             if edi file cannot be sended
     */
    void sendEDIFile(EDIFile ediFile) throws EdiProcessingException;
}
