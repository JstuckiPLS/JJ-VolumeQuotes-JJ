package com.pls.ltlrating.batch.migration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.pls.ltlrating.batch.migration.model.LtlPricingItem;

/**
 * Custom spring batch item reader that collects data from Excel file and then transform it to the {@link LtlPricingItem} for import purpose.
 *
 * @author Alex Krychenko.
 */
public class LtlPricingItemImportReader extends AbstractPaginatedDataItemReader<LtlPricingItem>
        implements ResourceAwareItemReaderItemStream<LtlPricingItem> {
    private static final Logger LOG = LoggerFactory.getLogger(LtlPricingItemImportReader.class);

    private Resource resource;
    private RowMapper<LtlPricingItem> rowMapper;
    private OPCPackage container;
    private ReadOnlySharedStringsTable stringsTable;
    private XSSFReader xssfReader;
    private StylesTable stylesTable;
    private boolean hasMoreData = true;


    @Override
    protected Iterator<LtlPricingItem> doPageRead() {
        if (hasMoreData) {
            return readPage();
        }
        return null;
    }

    private Iterator<LtlPricingItem> readPage() throws IllegalStateException {
        InputStream sheetStream;
        Collection<LtlPricingItem> ltlPricingItems;
        LOG.info("PRICE IMPORT: Read pricing items from resource[{}], page: [{}], page size: [{}] ", resource.getFilename(), page, pageSize);
        try {
            sheetStream = xssfReader.getSheetsData().next();
            ltlPricingItems = processSheet(sheetStream, pageSize * page + 1, (page + 1) * pageSize + 1);
            sheetStream.close();
        } catch (Exception e) {
            hasMoreData = false;
            throw new IllegalStateException(
                    String.format("PRICE IMPORT: Error while reading pricing items from resource[%s], page: [%d], page size: [%d]",
                                  resource.getFilename(), page, pageSize), e);
        }
        if (CollectionUtils.isNotEmpty(ltlPricingItems)) {
            if (ltlPricingItems.size() < pageSize) {
                hasMoreData = false;
            }
            return ltlPricingItems.iterator();
        }
        hasMoreData = false;
        return null;
    }

    private Collection<LtlPricingItem> processSheet(final InputStream stream, final int startInd, final int endInd)
            throws ParserConfigurationException, SAXException, IOException {
        final InputSource source = new InputSource(stream);
        final List<LtlPricingItem> pricingItems = new ArrayList<>(pageSize);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        SAXParser parser = saxParserFactory.newSAXParser();
        XSSFSheetXMLHandler xmlHandler =
                new XSSFSheetXMLHandler(stylesTable, stringsTable, new ExcelSheetContentHandler(pricingItems, rowMapper, startInd, endInd), false);
        parser.parse(source, xmlHandler);
        return pricingItems;
    }

    @Override
    public void setResource(final Resource resource) {
        this.resource = resource;
    }

    @Override
    protected void doClose() throws Exception {
        container.close();
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.notNull(this.resource, "Input resource must be set");
        if (!this.resource.exists()) {
            throw new IllegalStateException("Input resource must exist: " + this.resource);
        }

        if (!this.resource.isReadable()) {
            throw new IllegalStateException("Input resource must be readable: " + this.resource);
        }

        container = OPCPackage.open(this.resource.getFile(), PackageAccess.READ);
        stringsTable = new ReadOnlySharedStringsTable(container);
        xssfReader = new XSSFReader(container);
        stylesTable = xssfReader.getStylesTable();
    }

    public void setRowMapper(final RowMapper<LtlPricingItem> rowMapper) {
        this.rowMapper = rowMapper;
    }
}
