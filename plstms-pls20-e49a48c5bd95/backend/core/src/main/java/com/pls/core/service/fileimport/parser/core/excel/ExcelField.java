package com.pls.core.service.fileimport.parser.core.excel;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import com.pls.core.service.fileimport.parser.core.BaseField;

/**
 * Implementation of {@link}.
 * 
 * @author Artem Arapov
 * @author Maxim Medvedev
 *
 */
public class ExcelField extends BaseField {

    private Cell cell;

    @SuppressWarnings("deprecation")
    private static String extractStringValue(Cell cell) {
        String result = StringUtils.EMPTY;

        if (cell != null) {
            switch (cell.getCellTypeEnum()) {
            case STRING:
                result = parseStringValue(cell);
                break;
            case BOOLEAN:
                result = parseBooleanValue(cell);
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = parseDateValue(cell);
                } else {
                    result = parseNumericValue(cell);
                }
                break;
            default:
                result = StringUtils.EMPTY;
                break;
            }
        }

        return StringUtils.trimToEmpty(result);
    }

    /**
     * Constructor.
     * 
     * @param cell {@link Cell}
     * */
    public ExcelField(Cell cell) {
        super(ExcelField.extractStringValue(cell));
        this.cell = cell;
    }

    @Override
    public String getName() {
        return cell.getRow().getSheet().getSheetName() + ":"
                + CellReference.convertNumToColString(cell.getColumnIndex()) + (cell.getRowIndex() + 1);
    }

    private static String parseStringValue(Cell cell) {
        return cell.getStringCellValue();
    }

    private static String parseDateValue(Cell cell) {
        String format = cell.getCellStyle().getDataFormatString();
        if ("h:mm AM/PM".equals(format)) {
            format = "h:mm a";
        } else {
            format = DATE_FORMATS[0];
        }
        return new SimpleDateFormat(format, Locale.US).format(cell.getDateCellValue());
    }

    private static String parseNumericValue(Cell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    private static String parseBooleanValue(Cell cell) {
        return String.valueOf(cell.getBooleanCellValue());
    }
}
