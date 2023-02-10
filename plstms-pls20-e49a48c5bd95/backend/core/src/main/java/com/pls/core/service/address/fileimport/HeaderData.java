package com.pls.core.service.address.fileimport;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.core.exception.fileimport.ImportFileInvalidDataException;

/**
 * Class to parse and validate header data.
 * 
 * @author Artem Arapov
 *
 */
public class HeaderData {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<AddressFields, Integer> headerData = new HashMap<AddressFields, Integer>();

    /**
     * Get Entry set.
     * 
     * @return header entry set
     * */
    public Set<Map.Entry<AddressFields, Integer>> entrySet() {
        return headerData.entrySet();
    }

    /**
     * Check that current sheet has at least one header.
     * 
     * @return <code>true</code> if least one data column was found. Otherwise returns false and this sheet
     *         should be skipped.
     * */
    public boolean hasData() {
        return !headerData.isEmpty();
    }

    /**
     * Read header data.
     * 
     * @param headerRow
     *            The first row that will be used to read header data for current sheet.
     * 
     * @throws ImportFileInvalidDataException
     *             Invalid header data.
     */
    public void readData(Row headerRow) throws ImportFileInvalidDataException {
        log.debug("Parsing header data");
        headerData.clear();

        if (headerRow != null) {
            readHeaderData(headerRow);
            validateHeaderData(headerRow.getSheet().getSheetName());
        }
    }

    @SuppressWarnings("deprecation")
    private void readHeaderData(Row headerRow) {
        for (Cell column : headerRow) {
            if (CellType.STRING == column.getCellTypeEnum()) {
                AddressFields field = AddressFields.getAddressByHeader(column.getStringCellValue());
                if (field != null) {
                    headerData.put(field, column.getColumnIndex());
                }
            } else {
                log.warn("Cell ''{}'' was skipped due to invalid type. Only string values are valid for header row.",
                        new CellReference(column).formatAsString());
            }
        }
    }

    private void validateHeaderData(String sheetName) throws ImportFileInvalidDataException {
        for (AddressFields field : AddressFields.values()) {
            if (field.isRequired() && !headerData.containsKey(field)) {
                throw new ImportFileInvalidDataException(String.format("Column ''%s'' was not found on ''%s'' sheet.",
                        field.getHeader(), sheetName));
            }
        }
    }
}
