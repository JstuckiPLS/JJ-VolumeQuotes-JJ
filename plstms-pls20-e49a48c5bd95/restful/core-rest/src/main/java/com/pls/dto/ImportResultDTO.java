package com.pls.dto;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Response from server side to AddressesImport file upload.
 * AddressesImport file should be uploaded, validated and its items should be inserted to address book.
 * If some addresses insertion fails then text document with that failed items would be generated
 *
 * @author Alexey Tarasyuk
 */
@XmlRootElement
public class ImportResultDTO {
    private boolean success;

    private int succeedCount;

    private int failedCount;

    private Long fixNowDocId;

    private ImportErrorType errorType;

    private List<String> errorMessageList;

    /**
     * Contains list of possible error types raised during import processing.
     */
    public enum ImportErrorType {
        RECORDS_NUMBER_EXCEEDED,
        PARSER_FAILED,
        EMPTY_FILE
    }

    /**
     * Returns result of AddressesImport file validation.
     *
     * @return true if input file was parsed successfully or false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets result of AddressesImport file validation.
     *
     * @param success - set true if input file was parsed successfully or false otherwise.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Number of addresses which was imported successfully.
     *
     * @return number of addresses which was imported successfully.
     */
    public int getSucceedCount() {
        return succeedCount;
    }

    /**
     * Number of addresses which was imported successfully.
     *
     * @param succeedCount - number of addresses which was imported successfully.
     */
    public void setSucceedCount(int succeedCount) {
        this.succeedCount = succeedCount;
    }

    /**
     * Returns number of addresses which was parsed successfully but was failed with import because of inconsistent data
     * or because of same address already exist in address book.
     *
     * @return number of addresses which was failed to import.
     */
    public int getFailedCount() {
        return failedCount;
    }

    /**
     * Sets number of addresses which was parsed successfully but was failed with import because of inconsistent data
     * or because of same address already exist in address book.
     *
     * @param failedCount - number of addresses which was failed to import.
     */
    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    /**
     * If some addresses was failed to import then the text document with that addresses would be generated.
     *
     * @return id of the document with the failed addresses data.
     */
    public Long getFixNowDocId() {
        return fixNowDocId;
    }

    /**
     * Sets id of the text document where data of addresses which was failed to import were placed.
     *
     * @param fixNowDocId - id of the document with the failed addresses data.
     */
    public void setFixNowDocId(Long fixNowDocId) {
        this.fixNowDocId = fixNowDocId;
    }

    public List<String> getErrorMessageList() {
        return errorMessageList;
    }

    public void setErrorMessageList(List<String> errorMessageList) {
        this.errorMessageList = errorMessageList;
    }

    public ImportErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ImportErrorType errorType) {
        this.errorType = errorType;
    }
}
