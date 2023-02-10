package com.pls.shipment.domain.edi;

import java.util.Collections;
import java.util.List;

import com.pls.shipment.domain.enums.EDITransactionSet;

/**
 * Value object which represents a result of parsing {@link EDIFile}.
 *
 * @param <T> type of entity which EDI parser returns
 * @author Mikhail Boldinov, 11/09/13
 */
public class EDIParseResult<T> {

    private EDITransactionSet transactionSet;

    private EDIFile ediFile;

    private EDIData ediData;

    private List<T> parsedEntities;

    private Status status;

    private String errorMsg;

    private String receiverId;

    /**
     * Constructor.
     *
     * @param ediFile {@link EDIFile}
     */
    public EDIParseResult(EDIFile ediFile) {
        this.ediFile = ediFile;
        this.transactionSet = ediFile.getTransactionSet();
    }

    public EDITransactionSet getTransactionSet() {
        return transactionSet;
    }

    public EDIFile getEdiFile() {
        return ediFile;
    }

    public EDIData getEdiData() {
        return ediData;
    }

    public void setEdiData(EDIData ediData) {
        this.ediData = ediData;
    }

    public List<T> getParsedEntities() {
        return parsedEntities != null ? parsedEntities : Collections.<T>emptyList();
    }

    public void setParsedEntities(List<T> parsedEntities) {
        this.parsedEntities = parsedEntities;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * EDI parse result status. Processed if EDI file processed successfully or Failed if any errors occur.
     * PARTIAL_FAIL means that file was parsed successfully but not all results were processed.
     */
    public enum Status {
        SUCCESS, FAIL, PARTIAL_FAIL
    }
}
