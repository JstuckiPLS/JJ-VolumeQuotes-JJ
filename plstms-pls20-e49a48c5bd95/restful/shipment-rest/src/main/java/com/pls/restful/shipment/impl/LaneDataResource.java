package com.pls.restful.shipment.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.dto.lanedata.LaneDataDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.shipment.LaneDataDTOBuilder;
import com.pls.shipment.domain.LaneDataEntity;
import com.pls.shipment.service.LaneDataService;

/**
 * REST service for Lane Data access.
 * 
 * @author Viacheslav Vasianovych
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/laneData")
public class LaneDataResource {

    private static final int COLUMN_NAME_GROUP = 2;

    private static final String EXCEL_FILE_NAME = "lane data.xlsx";

    private static final Pattern SOFT_INFO_PATTERN = Pattern.compile("(([a-zA-Z0-9]+)=(ASC|DESC))+");

    private static final int SORT_DIRECTION_GROUP = 3;

    private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    private final AbstractDTOBuilder<LaneDataEntity, LaneDataDTO> dtoBuilder = new LaneDataDTOBuilder();

    @Autowired
    private LaneDataService laneDataService;

    /**
     * Generate excel file for period.
     *
     * @param customerId id of customer
     * @param start      date
     * @param end        date
     * @param sortInfo   information about sorting
     * @return servletResponse parameter.
     * @throws IOException    is something goes wrong
     * @throws ParseException if date format is invalid
     */
    @RequestMapping(value = "/export/", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity export(
            @PathVariable("customerId") Long customerId,
            @RequestParam(value = "startDate", required = false) String start,
            @RequestParam(value = "endDate", required = false) String end,
            @RequestParam(value = "sortInfo", required = false) String sortInfo) throws IOException, ParseException {
        Date startDate = null;
        Date endDate = null;
        if (StringUtils.hasText(start)) {
            startDate = dateFormat.parse(start);
        }

        if (StringUtils.hasText(end)) {
            endDate = dateFormat.parse(end);
        }
        Map<String, String> sortInfoMap = new HashMap<String, String>();
        if (StringUtils.hasText(sortInfo)) {
            Matcher matcher = SOFT_INFO_PATTERN.matcher(sortInfo);
            while (matcher.find()) {
                String column = matcher.group(COLUMN_NAME_GROUP);
                String sortDirection = matcher.group(SORT_DIRECTION_GROUP);
                sortInfoMap.put(column, sortDirection);
            }
        }

        OutputStream resultStream = null;
        try {
            File tempFile = File.createTempFile("laneDataExport", "tmp");
            resultStream = new FileOutputStream(tempFile);
            laneDataService.generateExcelFile(customerId, resultStream, startDate, endDate, sortInfoMap);

            tempFile.deleteOnExit();
            return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), EXCEL_FILE_NAME);
        } finally {
            IOUtils.closeQuietly(resultStream);
        }
    }

    /**
     * Provides list of all lane data.
     *
     * @param customerId id of customer
     * @param start      date
     * @param end        date
     * @return List of lane data
     * @throws ParseException if date format is incorrect
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<LaneDataDTO> getLaneData(
            @PathVariable("customerId") Long customerId,
            @RequestParam(value = "startDate", required = false) String start,
            @RequestParam(value = "endDate", required = false) String end) throws ParseException {
        Date startDate = null;
        Date endDate = null;

        if (StringUtils.hasText(start)) {
            startDate = dateFormat.parse(start);
        }

        if (StringUtils.hasText(end)) {
            endDate = dateFormat.parse(end);
        }
        List<LaneDataEntity> entities = laneDataService.getLaneDataByPeriod(customerId, startDate, endDate);
        return dtoBuilder.buildList(entities);
    }
}
