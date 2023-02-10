package com.pls.ltlrating.batch.migration;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.HasLabel;

/**
 * Custom spring batch item writer to serialize items to Excel files.
 *
 * @author Alex Krychenko.
 */
@Component
public class LtlPricingItemExportWriter extends AbstractItemStreamItemWriter<LtlPricingItem>
        implements ResourceAwareItemWriterItemStream<LtlPricingItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LtlPricingItemExportWriter.class);

    private List<String> headers;
    private List<String> names;
    private Resource resource;
    private Workbook workbook;
    private int currentRow;
    private BeanWrapper beanWrapper = new BeanWrapperImpl(LtlPricingItem.class);
    private CellStyle numberCellStyle;
    private CellStyle doubleCellStyle;
    private CellStyle dateCellStyle;

    @Override
    public void setResource(final Resource resource) {
        this.resource = resource;
    }

    public void setHeaders(final String[] headers) {
        this.headers = Arrays.stream(headers).collect(Collectors.toList());
    }

    public void setNames(final String[] names) {
        this.names = Arrays.stream(names).collect(Collectors.toList());
    }

    @Override
    public void open(ExecutionContext executionContext) {
        LOGGER.debug("Open writer fro pricing export");
        super.open(executionContext);
        Assert.notNull(resource, "The resource must be set");
        if (workbook == null) {
            workbook = new SXSSFWorkbook(100);
            workbook.createSheet();
            createNumbersCellStyle();
            addHeaders();
        }
    }

    @Override
    public void close() {
        super.close();
        Assert.notNull(resource, "The resource must be set");
        ExcelUtil.serializeWorkbook(workbook, resource);
    }

    @Override
    public void write(final List<? extends LtlPricingItem> items) throws Exception {
        Sheet sheet = workbook.getSheetAt(0);
        for (LtlPricingItem item : items) {
            if (item != null) {
                Row row = sheet.createRow(currentRow++);
                int col = 0;
                for (String name : names) {
                    Cell cell = row.createCell(col++);
                    PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor(name);
                    setCellValue(item, cell, propertyDescriptor);
                }
            }
        }
    }

    private void setCellValue(final LtlPricingItem item, final Cell cell, final PropertyDescriptor propertyDescriptor)
    throws IllegalAccessException, InvocationTargetException {
        Class<?> propertyType = propertyDescriptor.getPropertyType();
        Object propertyValue = propertyDescriptor.getReadMethod().invoke(item);
        if (propertyValue == null) {
            cell.setCellValue("");
            return;
        }
        if (Long.class.isAssignableFrom(propertyType)) {
            cell.setCellStyle(numberCellStyle);
            cell.setCellValue(this.<Number>cast(propertyValue).doubleValue());
        } else if (BigDecimal.class.isAssignableFrom(propertyType)) {
            cell.setCellStyle(doubleCellStyle);
            cell.setCellValue(this.<Number>cast(propertyValue).doubleValue());
        } else if (Date.class.isAssignableFrom(propertyType)) {
            cell.setCellStyle(dateCellStyle);
            cell.setCellValue(this.<Date>cast(propertyValue));
        } else if (HasLabel.class.isAssignableFrom(propertyType)) {
            cell.setCellStyle(dateCellStyle);
            cell.setCellValue(this.<HasLabel>cast(propertyValue).getLabel());
        } else {
            cell.setCellValue(String.valueOf(propertyValue));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(final Object value) {
        return (T) value;
    }

    private void createNumbersCellStyle() {
        numberCellStyle = workbook.createCellStyle();
        numberCellStyle.setDataFormat(workbook.createDataFormat().getFormat("#0"));
        numberCellStyle.setAlignment(HorizontalAlignment.RIGHT);

        doubleCellStyle = workbook.createCellStyle();
        doubleCellStyle.setDataFormat(workbook.createDataFormat().getFormat("#0.00"));
        doubleCellStyle.setAlignment(HorizontalAlignment.RIGHT);

        dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("mm/dd/yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.RIGHT);
    }

    private void addHeaders() {
        ExcelUtil.addHeaders(headers, workbook);
        currentRow++;
    }
}
