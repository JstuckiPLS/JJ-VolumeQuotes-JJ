package com.pls.shipment.domain.edi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.service.impl.edi.utils.EDIUtils;

/**
 * EDI File.
 *
 * @author Mikhail Boldinov, 03/09/13
 */
public class EDIFile implements Serializable {

    private static final long serialVersionUID = -4585195372727939872L;

    private Long isa;

    private Long gs;

    private String name;

    private String filePath;

    private File file;

    private EDITransactionSet transactionSet;

    private String carrierScac;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Get file content. Will return content of file if specified.
     *
     * @return edi file input stream
     * @throws IOException if input stream creation fails
     */
    public InputStream getNewFileContent() throws IOException {
        if (file != null) {
            return EDIUtils.trimFile(file);
        }
        return null;
    }

    public EDITransactionSet getTransactionSet() {
        return transactionSet;
    }

    public void setTransactionSet(EDITransactionSet transactionSet) {
        this.transactionSet = transactionSet;
    }

    public String getCarrierScac() {
        return carrierScac;
    }

    public void setCarrierScac(String carrierScac) {
        this.carrierScac = carrierScac;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Long getIsa() {
        return isa;
    }

    public void setIsa(Long isa) {
        this.isa = isa;
    }

    public Long getGs() {
        return gs;
    }

    public void setGs(Long gs) {
        this.gs = gs;
    }
}
