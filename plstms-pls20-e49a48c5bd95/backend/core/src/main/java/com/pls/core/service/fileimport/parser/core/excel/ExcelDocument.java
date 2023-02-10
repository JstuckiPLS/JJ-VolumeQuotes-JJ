package com.pls.core.service.fileimport.parser.core.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.exception.fileimport.InvalidFormatException;
import com.pls.core.service.fileimport.parser.core.Document;
import com.pls.core.service.fileimport.parser.core.Page;

/**
 * Implementation of {@link Document} for Excel file.
 * 
 * @author Artem Arapov
 *
 */
public class ExcelDocument implements Document {

    private List<Page> pages = new ArrayList<Page>();

    private Workbook workbook;

    /**
     * Constructor.
     * 
     * @param stream not <code>null</code> {@link InputStream}.
     * @throws ImportException exception
     */
    public ExcelDocument(InputStream stream) throws ImportException {
        if (stream == null) {
            throw new ImportException("Not specified input stream!");
        }

        try {
            workbook = WorkbookFactory.create(stream);

            for (int idx = 0; idx < workbook.getNumberOfSheets(); idx++) {
                pages.add(new ExcelPage(workbook.getSheetAt(idx)));
            }
        } catch (IOException e) {
            throw new ImportFileParseException("Unable to parse file", e);
        } catch (OpenXML4JException e) {
            throw new InvalidFormatException("Invalid file format", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidFormatException("Invalid file format", e);
        }
    }

    @Override
    public Iterator<Page> iterator() {
        return pages.iterator();
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        workbook.write(stream);
    }
}
