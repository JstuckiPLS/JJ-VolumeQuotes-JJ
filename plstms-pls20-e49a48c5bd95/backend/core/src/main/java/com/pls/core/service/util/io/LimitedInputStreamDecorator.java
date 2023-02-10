package com.pls.core.service.util.io;

import org.apache.commons.fileupload.util.LimitedInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Decorator class that will throw an exception if someone tries to read more than specified size.
 *
 * @author Aleksandr Leshchenko
 * @author Denis Zhupinsky (Team International)
 */
public class LimitedInputStreamDecorator extends LimitedInputStream {
    /**
     * Constructor.
     *
     * @param is {@link java.io.InputStream}
     * @param pSizeMax The limit; no more than this number of bytes
     *   shall be returned by the source stream.
     */
    public LimitedInputStreamDecorator(InputStream is, long pSizeMax) {
        super(is, pSizeMax);
    }

    @Override
    protected void raiseError(long pSizeMax, long pCount) throws IOException {
        throw new LimitExceededException();
    }

    /**
     * Do nothing because otherwise it will continue reading bytes from the stream.<br\>
     *
     * @see org.apache.commons.fileupload.MultipartStream.ItemInputStream#close() for more information.
     */
    @Override
    public void close() {
        // do nothing
    }

    /**
     * Runtime exception to be thrown if size of document exceeds the allowed maximum.
     *
     * @author Denis Zhupinsky
     */
    public static class LimitExceededException extends RuntimeException {
        private static final long serialVersionUID = -6638773831161432272L;
    }
}



