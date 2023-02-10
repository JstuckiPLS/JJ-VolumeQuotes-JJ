package com.pls.shipment.service.impl;

import com.pls.shipment.dao.LaneDataDao;
import com.pls.shipment.domain.LaneDataEntity;
import com.pls.shipment.service.ExcelFileBuilder;
import com.pls.shipment.service.LaneDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service for Lane Data.
 * 
 * @author Viacheslav Vasianovych
 * 
 */
@Service
@Transactional
public class LaneDataServiceImpl implements LaneDataService {

    private static final String CURRENCY_FORMAT = "$#,##0.00";

    private static final String BOL_COLUMN_TITLE = "BOL";
    private static final String CARRIER_COLUMN_TITLE = "Carrier";
    private static final String INVOICE_DATE_COLUMN_TITLE = "Invoice Date";
    private static final String PICKUP_DATE_COLUMN_TITLE = "Pickup Date";
    private static final String CLASS1_COLUMN_TITLE = "Class 1";
    private static final String WEIGHT1_COLUMN_TITLE = "Weight 1";
    private static final String CLASS2_COLUMN_TITLE = "Class 2";
    private static final String WEIGHT2_COLUMN_TITLE = "Weight 2";
    private static final String ORIGIN_ZIP_COLUMN_TITLE = "Origin Zip";
    private static final String DESTINATION_ZIP_COLUMN_TITLE = "Destination Zip";
    private static final String ACCESSORIALS_COLUMN_TITLE = "Accessorials";
    private static final String COST_COLUMN_TITLE = "Cost";
    private static final String FUEL_COLUMN_TITLE = "Fuel";
    private static final String TOTAL_COLUMN_TITLE = "Total";

    private static final int COLUMNS_SKIP = 1;
    private static final int ROWS_SKIP = 1;

    @Autowired
    private LaneDataDao laneDataDao;

    private List<LaneDataEntity> getLaneDataByPeriod(Long customerId, Date startDate, Date endDate,
            Map<String, String> sortInfo) {
        return laneDataDao.getLaneDataByPeriod(customerId, startDate, endDate, sortInfo);
    }

    private void buildExcel(List<LaneDataEntity> laneDataList, OutputStream out) throws IOException {
        ExcelFileBuilder builder = new ExcelFileBuilder();
        builder.setLeftMargin(COLUMNS_SKIP);
        builder.setTopMargin(ROWS_SKIP);
        builder.addHeader(BOL_COLUMN_TITLE);
        builder.addHeader(CARRIER_COLUMN_TITLE);
        builder.addHeader(INVOICE_DATE_COLUMN_TITLE);
        builder.addHeader(PICKUP_DATE_COLUMN_TITLE);
        builder.addHeader(CLASS1_COLUMN_TITLE);
        builder.addHeader(WEIGHT1_COLUMN_TITLE);
        builder.addHeader(CLASS2_COLUMN_TITLE);
        builder.addHeader(WEIGHT2_COLUMN_TITLE);
        builder.addHeader(ORIGIN_ZIP_COLUMN_TITLE);
        builder.addHeader(DESTINATION_ZIP_COLUMN_TITLE);
        builder.addHeader(COST_COLUMN_TITLE);
        builder.addHeader(FUEL_COLUMN_TITLE);
        builder.addHeader(ACCESSORIALS_COLUMN_TITLE);
        builder.addHeader(TOTAL_COLUMN_TITLE);
        for (LaneDataEntity data : laneDataList) {
            builder.addRow().addValue(data.getBol()).addValue(data.getCarrier())
                    .addValue(data.getInvoiceDate()).addValue(data.getPickupDate())
                    .addValue(data.getClass1()).addValue(data.getWeight1()).addValue(data.getClass2())
                    .addValue(data.getWeight2()).addValue(data.getOriginZip())
                    .addValue(data.getDestinationZip())
                    .addValue(new DecimalFormat(CURRENCY_FORMAT).format(data.getCost()))
                    .addValue(new DecimalFormat(CURRENCY_FORMAT).format(data.getFuel()))
                    .addValue(new DecimalFormat(CURRENCY_FORMAT).format(data.getAccessorials()))
                    .addValue(new DecimalFormat(CURRENCY_FORMAT).format(data.getTotal()));
        }
        builder.buildExcelFile(out);
    }

    @Override
    public void generateExcelFile(Long customerId, OutputStream out, Date startDate, Date endDate,
            Map<String, String> sortInfo) throws IOException {
        List<LaneDataEntity> laneDataList = getLaneDataByPeriod(customerId, startDate, endDate, sortInfo);
        buildExcel(laneDataList, out);
    }

    @Override
    public List<LaneDataEntity> getLaneDataByPeriod(Long customerId, Date startDate, Date endDate) {
        return laneDataDao.getLaneDataByPeriod(customerId, startDate, endDate);
    }
}