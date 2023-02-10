package com.pls.core.service.xls;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.service.file.FileInputStreamResponseEntity;

/**
 * Abstract report class.
 * 
 * @author Alexander Nalapko
 *
 */
public class AbstractReportExcelBuilder {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected static final String CURRENCY_FORMAT = "$#,##0.00";
    protected static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    protected static final String DATE_FORMAT = DateFormatConverter.convert(Locale.US, SIMPLE_DATE_FORMAT);
    protected static final String CURRENT_DATE = new SimpleDateFormat("MMM dd, yyy", Locale.US).format(new Date());
    protected static final String PERCENTAGE_FORMAT = "0.00%";

    protected final ClassPathResource revenueTemplate;
    protected final Sheet mainSheet;
    protected final Workbook workbook;

    protected final CellStyle currencyCellStyle;
    protected final CellStyle dateCellStyle;
    protected final CellStyle percCellStyle;
    protected final CellStyle style;
    protected final CellStyle rptHdngStyle;
    protected final CellStyle footerHdngStyle;
    protected final XSSFCellStyle colHdngStyle;
    protected final CellStyle summHdngStyle;
    protected final CellStyle summHdngStyleCurr;
    protected final CellStyle summHdngStylePerc;

    /**
     * Revenue report builder constructor.
     * 
     * @param revenueReportTemplate
     *            - revenueReport template
     * @throws IOException
     *             if can't generate report
     */
    protected AbstractReportExcelBuilder(ClassPathResource revenueReportTemplate) throws IOException {
        InputStream template = revenueReportTemplate.getInputStream();
        workbook = new XSSFWorkbook(template);
        mainSheet = workbook.getSheetAt(0);
        mainSheet.setDisplayGridlines(false);
        revenueTemplate = revenueReportTemplate;
        DataFormat dataFormat = workbook.createDataFormat();

        style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);

        Font rptHdngFont = workbook.createFont();
        rptHdngFont.setBold(true);
        rptHdngFont.setFontName("Calibri");

        rptHdngFont.setFontHeightInPoints(new Integer(16).shortValue());
        rptHdngStyle = workbook.createCellStyle();
        rptHdngStyle.setFont(rptHdngFont);
        rptHdngStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        rptHdngStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

        Font footerHdngFont = workbook.createFont();
        footerHdngFont.setBold(true);
        footerHdngFont.setFontName("Calibri");
        footerHdngFont.setFontHeightInPoints(new Integer(14).shortValue());
        footerHdngStyle = workbook.createCellStyle();
        footerHdngStyle.cloneStyleFrom(rptHdngStyle);
        footerHdngStyle.setFont(footerHdngFont);

        Font colHdngFont = workbook.createFont();
        colHdngFont.setFontName("Arial");
        colHdngFont.setColor(IndexedColors.WHITE.getIndex());
        colHdngFont.setBold(true);
        colHdngFont.setFontHeightInPoints(new Integer(10).shortValue());
        colHdngStyle = (XSSFCellStyle) workbook.createCellStyle();
        colHdngStyle.cloneStyleFrom(style);
        colHdngStyle.setFont(colHdngFont);
        XSSFColor blueColor = new XSSFColor(new Color(54, 96, 146));
        colHdngStyle.setFillForegroundColor(blueColor);
        colHdngStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        currencyCellStyle = workbook.createCellStyle();
        currencyCellStyle.cloneStyleFrom(style);
        currencyCellStyle.setDataFormat(dataFormat.getFormat(CURRENCY_FORMAT));

        percCellStyle = workbook.createCellStyle();
        percCellStyle.cloneStyleFrom(style);
        percCellStyle.setDataFormat(dataFormat.getFormat(PERCENTAGE_FORMAT));

        dateCellStyle = workbook.createCellStyle();
        dateCellStyle.cloneStyleFrom(style);
        dateCellStyle.setDataFormat(dataFormat.getFormat(DATE_FORMAT));

        Font summaryHdngFont = workbook.createFont();
        summaryHdngFont.setBold(true);
        summHdngStyle = workbook.createCellStyle();
        summHdngStyle.cloneStyleFrom(style);
        summHdngStyle.setFont(summaryHdngFont);

        summHdngStyleCurr = workbook.createCellStyle();
        summHdngStyleCurr.cloneStyleFrom(currencyCellStyle);
        summHdngStyleCurr.setFont(summaryHdngFont);

        summHdngStylePerc = workbook.createCellStyle();
        summHdngStylePerc.cloneStyleFrom(percCellStyle);
        summHdngStylePerc.setFont(summaryHdngFont);

    }

    /**
     * Add cell with the string value.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <String> value
     */
    protected void fillDataCell(Row currentRow, int columnIndex, String value) {
        fillDataCell(currentRow, columnIndex, value, null);
    }

    /**
     * Add cell with the string value and cell style.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <String> value
     * @param style
     *            <CellStyle> cell style
     */
    protected void fillDataCell(Row currentRow, int columnIndex, String value, CellStyle style) {
        Cell cell = currentRow.getCell(columnIndex);
        if (cell == null) {
            cell = currentRow.createCell(columnIndex);
        }
        cell.setCellValue(StringUtils.trimToEmpty(value));
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(this.style);
        }
    }

    /**
     * Add cell with the boolean value and cell style.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <String> value
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Boolean value) {
        fillDataCell(currentRow, columnIndex, value, null);
    }

    /**
     * Add cell with the boolean value and cell style.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <String> value
     * @param style
     *            <CellStyle> cell style
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Boolean value, CellStyle style) {
        Cell cell = currentRow.getCell(columnIndex);
        if (cell == null) {
            cell = currentRow.createCell(columnIndex);
        }
        if (value != null) {
            if (value) {
                cell.setCellValue("Yes");
            } else {
                cell.setCellValue("No");
            }
        }
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(this.style);
        }
    }

    /**
     * Add cell with the string value and XSSF Cell style.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <String> value
     * @param style
     *            <CellStyle> cell style
     */
    protected void fillDataCell(Row currentRow, int columnIndex, String value, XSSFCellStyle style) {
        Cell cell = currentRow.getCell(columnIndex);
        if (cell == null) {
            cell = currentRow.createCell(columnIndex);
        }
        cell.setCellValue(StringUtils.trimToEmpty(value));
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(this.style);
        }
    }

    /**
     * Add cell with the Date value.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <Date> value
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Date value) {
        fillDataCell(currentRow, columnIndex, value, null);
    }

    /**
     * Add cell with the Date value and cell style.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <Date> value
     * @param style
     *            <CellStyle> cell style
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Date value, CellStyle style) {
        Cell cell = currentRow.createCell(columnIndex);
        if (value != null) {
            cell.setCellValue(DateUtils.toCalendar(value));
        }
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(dateCellStyle);
        }
    }

    /**
     * Add cell with the BigDecimal value with style from template.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <BigDecimal> value
     */
    protected void fillDataCellAndSaveStyle(Row currentRow, int columnIndex, BigDecimal value) {
        Cell cell = currentRow.getCell(columnIndex);
        CellStyle style = cell.getCellStyle();
        fillDataCell(currentRow, columnIndex, value, style);
    }

    /**
     * Add cell with the BigDecimal value with style from template.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <String> value
     */
    protected void fillDataCellAndSaveStyle(Row currentRow, int columnIndex, String value) {
        Cell cell = currentRow.getCell(columnIndex);
        CellStyle style = cell.getCellStyle();
        fillDataCell(currentRow, columnIndex, value, style);
    }

    /**
     * Add cell with the BigDecimal value.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <BigDecimal> value
     */
    protected void fillDataCell(Row currentRow, int columnIndex, BigDecimal value) {
        fillDataCell(currentRow, columnIndex, value, null);
    }

    /**
     * Add cell with the BigDecimal value and cell style.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <BigDecimal> value
     * @param style
     *            <CellStyle> cell style
     */
    protected void fillDataCell(Row currentRow, int columnIndex, BigDecimal value, CellStyle style) {
        Cell cell = currentRow.createCell(columnIndex);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(BigDecimal.ZERO.doubleValue());
        }
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(currencyCellStyle);
        }
    }

    /**
     * Add cell with the Long value.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <Long> value
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Long value) {
        fillDataCell(currentRow, columnIndex, value, null);
    }

    /**
     * Add cell with the Long value and cell style.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <Long> value
     * @param style
     *            <CellStyle> cell style
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Long value, CellStyle style) {
        Cell cell = currentRow.createCell(columnIndex);
        if (value != null && value > 0) {
            cell.setCellValue(value.longValue());
        }
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(this.style);
        }
    }

    /**
     * Add cell with the Integer value.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <Integer> value
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Integer value) {
        fillDataCell(currentRow, columnIndex, value, null);
    }

    /**
     * Add cell with the Integer value and cell style.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <Integer> value
     * @param style
     *            <CellStyle>
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Integer value, CellStyle style) {
        Cell cell = currentRow.createCell(columnIndex);
        if (value != null) {
            cell.setCellValue(value.intValue());
        }
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(this.style);
        }
    }

    /**
     * Add cell with the double value.
     *
     * @param currentRow
     *            the current row
     * @param columnIndex
     *            the column index
     * @param value
     *            the value
     */
    protected void fillDataCell(Row currentRow, int columnIndex, double value) {
        fillDataCell(currentRow, columnIndex, value, null);
    }

    /**
     * Add cell with the double value and cell style.
     *
     * @param currentRow
     *            the current row
     * @param columnIndex
     *            the column index
     * @param value
     *            the value
     * @param style
     *            the style
     */
    protected void fillDataCell(Row currentRow, int columnIndex, double value, CellStyle style) {
        Cell cell = currentRow.createCell(columnIndex);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(this.style);
        }
    }

    /**
     * Add cell with the List of String values.
     *
     * @param currentRow
     *            the current row
     * @param columnIndex
     *            the column index
     * @param values
     *            the list of string values
     */
    protected void fillDataCell(Row currentRow, int columnIndex, List<String> values) {
        fillDataCell(currentRow, columnIndex, values, null);
    }

    /**
     * Add cell with the List of String values and cell style.
     *
     * @param currentRow
     *            the current row
     * @param columnIndex
     *            the column index
     * @param values
     *            the list of string values
     * @param style
     *            the style
     */
    protected void fillDataCell(Row currentRow, int columnIndex, List<String> values, CellStyle style) {
        Cell cell = currentRow.createCell(columnIndex);
        if (values != null) {
            int listLength = values.size() - 1;
            String accessorialList = "";
            for (String value : values) {
                if (listLength == 0) {
                    accessorialList = accessorialList.concat(value);
                } else {
                    accessorialList = accessorialList.concat(value + ", ");
                }
                listLength--;
            }
            cell.setCellValue(accessorialList);
        }
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(this.style);
        }
    }

    /**
     * Method to round a double value.
     * 
     * @param value
     *            the value
     * @param power
     *            the power
     * @return rounded double value
     */
    protected double round(double value, int power) {
        return Math.round(value * Math.pow(10, power)) / Math.pow(10, power);
    }

    /**
     * Add or replace value in cell.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param newValue
     *            <String> value
     * @param subStr
     *            <String> string for replacing
     */
    protected void fillCellValue(Row currentRow, int columnIndex, String newValue, String subStr) {
        fillCellValue(currentRow, columnIndex, newValue, subStr, null, null);
    }

    /**
     * Add or replace value in cell.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param newValue
     *            <String> value
     * @param subStr
     *            <String> string for replacing
     * @param style
     *            <CellStyle>
     * @param newValueFont
     *            Font for new text
     */
    protected void fillCellValue(Row currentRow, int columnIndex, String newValue, String subStr, CellStyle style,
            Font newValueFont) {
        Cell cell = currentRow.getCell(columnIndex);
        XSSFRichTextString richTextString = (XSSFRichTextString) cell.getRichStringCellValue();
        if (subStr != null) {
            int index = richTextString.getString().indexOf(subStr);
            Font font = null;
            try {
                font = richTextString.getFontAtIndex(index);
            } catch (Exception e) {
                log.error("Font not found");
            }
            richTextString.setString(richTextString.getString().replace(subStr, newValue));
            if (newValueFont != null) {
                richTextString.applyFont(index, index + newValue.length(), newValueFont);
            } else if (font != null) {
                richTextString.applyFont(index, index + newValue.length(), font);
            }
        }
        cell.setCellValue(richTextString);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    /**
     * Add style for cell .
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param style
     *            <CellStyle>
     */
    protected void fillEmptyCell(Row currentRow, int columnIndex, CellStyle style) {
        Cell cell = currentRow.createCell(columnIndex);
        if (style != null) {
            cell.setCellStyle(style);
        } else {
            cell.setCellStyle(this.style);
        }
    }

    /**
     * Add style for cells .
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param cellCount
     *            number of cells
     * @param style
     *            <CellStyle>
     */
    protected void fillEmptyCells(Row currentRow, int columnIndex, CellStyle style, int cellCount) {
        for (int i = 0; i < cellCount; i++) {
            Cell cell = currentRow.createCell(columnIndex + i);
            if (style != null) {
                cell.setCellStyle(style);
            } else {
                cell.setCellStyle(this.style);
            }
        }
    }

    /**
     * Add cell with the Object value.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <Object> value
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Object value) {
        fillDataCell(currentRow, columnIndex, value, null);
    }

    /**
     * Add cell with the Object value and cell style.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param value
     *            <Object> value
     * @param style
     *            <CellStyle>
     */
    protected void fillDataCell(Row currentRow, int columnIndex, Object value, CellStyle style) {
        if (value != null) {
            Class<?> valueClass = value.getClass();
            if (valueClass == String.class) {
                fillDataCell(currentRow, columnIndex, (String) value, style);
            } else if (value instanceof Date) {
                fillDataCell(currentRow, columnIndex, (Date) value, style);
            } else if (valueClass == BigDecimal.class) {
                fillDataCell(currentRow, columnIndex, (BigDecimal) value, style);
            } else if (valueClass == Integer.class) {
                fillDataCell(currentRow, columnIndex, (Integer) value, style);
            } else if (valueClass == Long.class) {
                fillDataCell(currentRow, columnIndex, (Long) value, style);
            } else if (valueClass == Double.class) {
                fillDataCell(currentRow, columnIndex, (double) value, style);
            } else if (valueClass == Boolean.class) {
                fillDataCell(currentRow, columnIndex, (Boolean) value, style);
            } else {
                fillEmptyCell(currentRow, columnIndex, style);
            }
        } else {
            fillEmptyCell(currentRow, columnIndex, style);
        }
    }

    /**
     * Generates revenue report as xlsx file.
     * 
     * @param subjectName
     *            name of customer.
     * @param startDate
     *            the start date of the report.
     * @param endDate
     *            the end date of report.
     * @return report data as input stream at response.
     * @throws IOException
     *             if can't generate report
     */
    protected FileInputStreamResponseEntity generateReport(String subjectName, Date startDate, Date endDate)
            throws IOException {
        File tempFile = File.createTempFile("reportData", "tmp");
        try {
            String fileName = getReportFileName(subjectName, startDate, endDate);
            workbook.write(new FileOutputStream(tempFile));
            return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    private String getReportFileName(String subjectName, Date startDate, Date endDate) {
        SimpleDateFormat format = new SimpleDateFormat("MMddyy", Locale.US);
        String name = StringUtils.substringBefore(revenueTemplate.getFilename(), ".");
        StringBuilder fileName = new StringBuilder(name);
        fileName.append('_');
        fileName.append(StringUtils.substring(StringUtils.remove(subjectName, " "), 0, 6));
        if (startDate != null) {
            fileName.append('_');
            fileName.append(format.format(startDate));
        }
        if (endDate != null) {
            fileName.append('-');
            fileName.append(format.format(endDate));
        }
        fileName.append(".xlsx");
        return fileName.toString();
    }

}
