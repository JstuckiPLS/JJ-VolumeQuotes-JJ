package com.pls.core.service.fileimport.parser.core;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represent data list to parse.
 * 
 * @author Artem Arapov
 *
 */
public interface Document extends Iterable<Page> {

    /**
     * Write current document into {@link OutputStream}.
     * 
     * @param stream {@link OutputStream} to write.
     * @throws IOException if anything can't be written.
     */
    void write(OutputStream stream) throws IOException;
}
