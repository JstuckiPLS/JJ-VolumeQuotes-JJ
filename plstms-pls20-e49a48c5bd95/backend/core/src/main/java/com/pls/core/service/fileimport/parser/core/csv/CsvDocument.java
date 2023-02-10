package com.pls.core.service.fileimport.parser.core.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.parser.core.Document;
import com.pls.core.service.fileimport.parser.core.Page;

/**
 * {@link Document} implementation for CSV file.
 * 
 * @author Artem Arapov
 *
 */
public class CsvDocument implements Document {

    private final Page page;

    /**
     * Constructor.
     * 
     * @param data
     *            Not <code>null</code> {@link InputStream}.
     * 
     * @exception ImportException
     *                Invalid file format.
     */
    public CsvDocument(InputStream data) throws ImportException {
        if (data == null) {
            throw new ImportException("Input data should be specified");
        }

        CSVReader reader = null;
        try {
            reader = new CSVReader(new InputStreamReader(data));
            page = new CsvPage(reader.readAll());
        } catch (IOException exc) {
            throw new ImportFileParseException("Unable to parse file", exc);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Override
    public Iterator<Page> iterator() {
        return Collections.singletonList(page).iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(OutputStream stream) throws IOException {
        OutputStreamWriter streamWriter = new OutputStreamWriter(stream);

        CSVWriter writer = null;
        try {
            writer = new CSVWriter(streamWriter);

            List<String[]> records = new ArrayList<String[]>();
            if (iterator().hasNext()) {
                Page page = iterator().next();
                records.addAll((List<String[]>) page.getSource());
            }

            writer.writeAll(records);
        } finally {
            IOUtils.closeQuietly(streamWriter);
            IOUtils.closeQuietly(writer);
        }
    }
}
