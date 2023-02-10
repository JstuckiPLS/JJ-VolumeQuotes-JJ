package com.pls.core.service.file;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Extension of {@link ResponseEntity} that return file as input stream at response.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class FileInputStreamResponseEntity extends ResponseEntity<Resource> {
    /**
     * Constructor.
     *
     * @param inputStream input stream
     * @param contentLength content length
     * @param fileName file name
     */
    public FileInputStreamResponseEntity(InputStream inputStream, final long contentLength, String fileName) {
        super(new InputStreamResource(inputStream) {
            @Override
            public long contentLength() throws IOException {
                return contentLength;
            }
        }, prepareHeaders(fileName), HttpStatus.OK);


    }

    private static HttpHeaders prepareHeaders(String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        return httpHeaders;
    }
}
