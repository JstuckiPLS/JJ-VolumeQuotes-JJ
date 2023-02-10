package com.pls.core.service.file;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * {@link ResponseEntity} for binary file.
 * 
 * @author Maxim Medvedev
 */
public class BinaryFileResponse extends ResponseEntity<byte[]> {
    private static HttpHeaders prepareHeaders(String fileName, int length, MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(length);
        headers.setContentType(contentType);
        headers.set("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        headers.setPragma("public");
        return headers;
    }

    /**
     * Constructor.
     * 
     * @param resultData
     *            File binary data. Should be not <code>null</code>.
     * @param fileName
     *            File name.
     */
    public BinaryFileResponse(byte[] resultData, String fileName) {
        super(resultData, BinaryFileResponse.prepareHeaders(fileName, resultData.length,
                MediaType.APPLICATION_OCTET_STREAM), HttpStatus.OK);
    }

    /**
     * Constructor.
     * 
     * @param resultData
     *            File binary data. Should be not <code>null</code>.
     * @param fileName
     *            File name.
     * @param contentType
     *            Not <code>null</code> {@link MediaType}.
     */
    public BinaryFileResponse(byte[] resultData, String fileName, MediaType contentType) {
        super(resultData, BinaryFileResponse.prepareHeaders(fileName, resultData.length, contentType),
                HttpStatus.OK);
    }
}
