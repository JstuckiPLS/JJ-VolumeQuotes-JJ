package com.pls.ltlrating.batch.migration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.ltlrating.batch.migration.model.LtlPricingItem;

/**
 * Skip listener to save all invalid {@link LtlPricingItem} to specified resource using POI writer.
 *
 * @author Aleksandr Leshchenko
 */
public class LtlPricingItemSkipListener implements SkipListener<LtlPricingItem, LtlPricingItem>, InitializingBean, StepExecutionListener {
    private static final Logger LOG = LoggerFactory.getLogger(LtlPricingItemSkipListener.class);
    public static final int ROW_ACCESS_WINDOW_SIZE = 100;

    private List<String> headers;
    private Resource resource;
    private Workbook workbook;
    //Current row set to 1 due to first row[index = 0] is reserved for headers
    private int currentRow = 1;
    private int errorMsgColumnNumber;

    @Override
    public void onSkipInRead(final Throwable error) {
        LOG.info("onSkipInRead", error);
        LtlPricingItem item = new LtlPricingItem();
        item.setError(getActualError(error, "Exception during item read"));
        saveWrongItem(item);
    }

    @Override
    public void onSkipInWrite(final LtlPricingItem item, final Throwable error) {
        LOG.info("onSkipInWrite", error);
        item.setError(getActualError(error, "Exception during item write"));
        saveWrongItem(item);
    }

    @Override
    public void onSkipInProcess(final LtlPricingItem item, final Throwable error) {
        LOG.info("onSkipInProcess", error);
        if (item.isValid()) {
            item.setError(getActualError(error, "Exception during item process"));
        }
        saveWrongItem(item);
    }

    public void setHeaders(final String[] headers) {
        this.headers = Arrays.stream(headers).collect(Collectors.toList());
    }

    public void setResource(final Resource resource) {
        this.resource = resource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(resource, "Resource for bad import items should be set");
        Assert.isTrue(CollectionUtils.isNotEmpty(headers), "Headers names should be set.");
        workbook = new SXSSFWorkbook(ROW_ACCESS_WINDOW_SIZE);
        workbook.createSheet();
        ExcelUtil.addHeaders(headers, workbook);
        LOG.info("Skip listener was created");
    }

    public void setErrorMsgColumnNumber(final int errorMsgColumnNumber) {
        this.errorMsgColumnNumber = errorMsgColumnNumber;
    }

    private Exception getActualError(final Throwable error, final String errorMsg) {
        Exception actualError;
        if (error instanceof Exception) {
            actualError = (Exception) error;
        } else {
            actualError = new ImportException(errorMsg, error);
        }
        return actualError;
    }

    private void saveWrongItem(final LtlPricingItem item) {
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.createRow(currentRow++);
        int columnInd = 0;
        if (item.getOrigColumns() != null) {
            for (String columnValue : item.getOrigColumns()) {
                row.createCell(columnInd++).setCellValue(columnValue);
            }
        }
        row.createCell(Math.max(columnInd, errorMsgColumnNumber)).setCellValue(item.getError().getMessage());
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {
        LOG.debug("LtlPricingItemSkipListener.beforeStep({})", stepExecution);
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
        LOG.info("LtlPricingItemSkipListener.afterJob");
        ExcelUtil.serializeWorkbook(workbook, resource);
        return stepExecution.getExitStatus();
    }
}
