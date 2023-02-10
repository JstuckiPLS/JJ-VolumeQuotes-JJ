package com.pls.core.service.fileimport.parser.core.csv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.parser.core.Page;
import com.pls.core.service.fileimport.parser.core.Record;

/**
 * {@link Page} implementation for CSV document.
 * 
 * @author Artem Arapov
 *
 */
public class CsvPage implements Page {

    /**
     * Default page name.
     */
    public static final String PAGE_NAME = "CSV Data";

    private final List<Record> data = new ArrayList<Record>();

    private final Record headerRecord;

    private final String[] headerArray;

    private List<String[]> contentArray = new ArrayList<String[]>();

    /**
     * Constructor.
     * 
     * @param content
     *            file content.
     * 
     * @throws ImportFileParseException
     *             Invalid content.
     */
    public CsvPage(List<String[]> content) throws ImportFileParseException {
        if (content == null) {
            throw new ImportFileParseException("Empty file");
        }
        if (content.isEmpty()) {
            content.add(new String[0]);
        }

        headerArray = content.get(0);
        headerRecord = new CsvRecord(headerArray, 0);

        for (int rowIndx = 1; rowIndx < content.size(); rowIndx++) {
            contentArray.add(content.get(rowIndx));
            data.add(new CsvRecord(content.get(rowIndx), rowIndx));
        }
    }

    @Override
    public Record getHeader() {
        return headerRecord;
    }

    @Override
    public String getName() {
        return PAGE_NAME;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public Iterator<Record> iterator() {
        final Iterator<Record> iterator = data.iterator();
        final Iterator<String[]> contentIterator = contentArray.iterator();

        return new Iterator<Record>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Record next() {
                contentIterator.next();
                return iterator.next();
            }

            @Override
            public void remove() {
                contentIterator.remove();
                iterator.remove();
            }
        };
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Object getSource() {
        List<String[]> result = new ArrayList<String[]>();

        result.add(headerArray);
        result.addAll(contentArray);

        return result;
    }

}
