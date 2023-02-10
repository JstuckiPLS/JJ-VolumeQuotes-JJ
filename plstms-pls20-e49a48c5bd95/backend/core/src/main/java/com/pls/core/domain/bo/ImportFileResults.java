package com.pls.core.domain.bo;

import java.util.List;

/**
 * Results of file import.
 * 
 * @author Artem Arapov
 *
 */
public class ImportFileResults {

    // FIXME typo
    private int succesRowsCount;

    // FIXME typo
    private int faiedRowsCount;

    private Long failedDocumentId;

    private List<String> errorMessageList;

    public int getSuccesRowsCount() {
        return succesRowsCount;
    }

    public void setSuccesRowsCount(int succesRowsCount) {
        this.succesRowsCount = succesRowsCount;
    }

    public int getFaiedRowsCount() {
        return faiedRowsCount;
    }

    public void setFaiedRowsCount(int faiedRowsCount) {
        this.faiedRowsCount = faiedRowsCount;
    }

    public Long getFailedDocumentId() {
        return failedDocumentId;
    }

    public void setFailedDocumentId(Long failedDocumentId) {
        this.failedDocumentId = failedDocumentId;
    }

    public List<String> getErrorMessageList() {
        return errorMessageList;
    }

    public void setErrorMessageList(List<String> errorMessageList) {
        this.errorMessageList = errorMessageList;
    }
}
