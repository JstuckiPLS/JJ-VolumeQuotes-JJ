package com.pls.ltlrating.service.analysis.fileimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.domain.analysis.FAAccessorialsEntity;
import com.pls.ltlrating.domain.analysis.FAFinancialAnalysisEntity;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;
import com.pls.ltlrating.domain.analysis.FAMaterialsEntity;
import com.pls.ltlrating.domain.analysis.FAOutputDetailsEntity;
import com.pls.ltlrating.domain.analysis.FATariffsEntity;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Freight analysis export file builder.
 *
 * @author Svetlana Kulish
 */
public class AnalysisExcelBuilder {
    private static final String DATE_FORMAT = DateFormatConverter.convert(Locale.US, "MM/dd/yyyy");

    private XSSFWorkbook workBook;
    private CellStyle generalStyle;
    private CellStyle headerStyle;
    private CellStyle bigDecimalStyle;
    private CellStyle dateStyle;

    private int lastInputDataColumnIndex;

    private Map<String, Integer> customerNames = new HashMap<String, Integer>();

    /**
     * Constructor.
     */
    public AnalysisExcelBuilder() {
        workBook = new XSSFWorkbook();

        generalStyle = workBook.createCellStyle();
        generalStyle.setBorderBottom(BorderStyle.THIN);
        generalStyle.setBorderTop(BorderStyle.THIN);
        generalStyle.setBorderLeft(BorderStyle.THIN);
        generalStyle.setBorderRight(BorderStyle.THIN);
        generalStyle.setAlignment(HorizontalAlignment.LEFT);

        headerStyle = workBook.createCellStyle();
        headerStyle.cloneStyleFrom(generalStyle);
        Font font = workBook.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setFontHeightInPoints(new Integer(10).shortValue());
        font.setBold(true);
        headerStyle.setFont(font);

        dateStyle = workBook.createCellStyle();
        dateStyle.cloneStyleFrom(generalStyle);
        DataFormat poiFormat = workBook.createDataFormat();
        dateStyle.setDataFormat(poiFormat.getFormat(DATE_FORMAT));

        bigDecimalStyle = workBook.createCellStyle();
        bigDecimalStyle.cloneStyleFrom(generalStyle);
        bigDecimalStyle.setAlignment(HorizontalAlignment.RIGHT);
    }

    /**
     * Populate content for the result file.
     *
     * @param entity
     *            entity that was processed.
     * @param file
     *            destination file to populate results
     * @throws IOException
     *             if file is not populated
     */
    public void generateFile(FAFinancialAnalysisEntity entity, File file) throws IOException {
        List<FAInputDetailsEntity> inputDetails = new ArrayList<>(entity.getInputDetails());
        Collections.sort(inputDetails, Comparator.comparing(FAInputDetailsEntity::getSeq));

        createTempSheet(inputDetails);

        createSMC3Sheet(entity, inputDetails);
        createBenchmarkSheet(entity, inputDetails);
        createBlanketSheet(entity, inputDetails);
        createCustomersSheets(entity, inputDetails);

        workBook.removeSheetAt(0);

        FileOutputStream out = new FileOutputStream(file);
        workBook.write(out);
        out.close();

        workBook.close();
    }

    private void createTempSheet(List<FAInputDetailsEntity> inputDetails) {
        XSSFSheet sheet = workBook.createSheet("temp");
        createGeneralHeaderRow(sheet);
        for (int row = 0; row < inputDetails.size(); row++) {
            createUserInputRow(sheet, inputDetails.get(row), row + 1);
        }
    }

    private void createSMC3Sheet(FAFinancialAnalysisEntity entity, List<FAInputDetailsEntity> inputDetails) {
        List<FATariffsEntity> tariffs = entity.getTariffs().stream().filter(t -> t.getTariffType() == PricingType.SMC3)
                .sorted((t1, t2) -> new CompareToBuilder().append(t1.getTariffName(), t2.getTariffName()).toComparison())
                .collect(Collectors.toList());
        if (!tariffs.isEmpty()) {
            Sheet sheet = createSheet("SMC3");
            createHeaderForSMC3Tariffs(sheet, tariffs);
            for (int row = 0; row < inputDetails.size(); row++) {
                createResultsRowForSMC3Tariffs(sheet.getRow(row + 1), inputDetails.get(row), tariffs);
            }
        }
    }

    private void createBenchmarkSheet(FAFinancialAnalysisEntity entity, List<FAInputDetailsEntity> inputDetails) {
        List<FATariffsEntity> tariffs = entity.getTariffs().stream().filter(t -> t.getTariffType() == PricingType.BENCHMARK)
                .sorted((t1, t2) -> new CompareToBuilder().append(t1.getTariffName(), t2.getTariffName()).toComparison())
                .collect(Collectors.toList());
        if (!tariffs.isEmpty()) {
            Sheet sheet = createSheet("Benchmark");
            createHeaderForBenchmarkTariffs(sheet, tariffs);
            for (int row = 0; row < inputDetails.size(); row++) {
                createResultsRowForBenchmarkTariffs(sheet.getRow(row + 1), inputDetails.get(row), tariffs);
            }
        }
    }

    private void createBlanketSheet(FAFinancialAnalysisEntity entity, List<FAInputDetailsEntity> inputDetails) {
        List<FATariffsEntity> tariffs = entity.getTariffs().stream().filter(t -> t.getTariffType() == PricingType.BLANKET)
                .collect(Collectors.toList());
        if (!tariffs.isEmpty()) {
            Sheet sheet = createSheet("Blanket");
            int errorColumnIndex = createHeaderForCarrierTariffs(sheet, inputDetails, tariffs);
            for (int row = 0; row < inputDetails.size(); row++) {
                List<FAOutputDetailsEntity> outputDetails = inputDetails.get(row).getOutputDetails().parallelStream()
                        .filter(outputDetail -> outputDetail.getTariff() != null && outputDetail.getTariff().getTariffType() == PricingType.BLANKET)
                        .sorted((o1, o2) -> new CompareToBuilder().append(o1.getSeq(), o2.getSeq()).toComparison())
                        .collect(Collectors.toList());
                createResultsRowForCarrierTariffs(sheet.getRow(row + 1), inputDetails.get(row), outputDetails, errorColumnIndex, tariffs);
            }
        }
    }

    private void createCustomersSheets(FAFinancialAnalysisEntity entity, List<FAInputDetailsEntity> inputDetails) {
        entity.getTariffs().stream().filter(t -> t.getTariffType() != PricingType.BENCHMARK).map(t -> t.getCustomer()).filter(Objects::nonNull)
                .distinct()
                .sorted((c1, c2) -> new CompareToBuilder().append(c1.getName(), c2.getName()).append(c1.getId(), c2.getId()).toComparison())
                .forEach(customer -> {
                    String customerName = getCustomerNameForExcel(customer.getName());
                    createSheetForCustomer(entity, inputDetails, customer.getId(), customerName + " Buy", CostDetailOwner.C);
                    createSheetForCustomer(entity, inputDetails, customer.getId(), customerName + " Sell", CostDetailOwner.S);
                });
    }

    private void createSheetForCustomer(FAFinancialAnalysisEntity entity, List<FAInputDetailsEntity> inputDetails, Long customerId,
            String sheetName, CostDetailOwner owner) {
        List<FATariffsEntity> tariffs = entity.getTariffs().stream().filter(t -> ObjectUtils.equals(t.getCustomerId(), customerId))
                .collect(Collectors.toList());
        if (!tariffs.isEmpty()) {
            Sheet sheet = createSheet(sheetName);
            int errorColumnIndex = createHeaderForCarrierTariffs(sheet, inputDetails, tariffs);
            for (int row = 0; row < inputDetails.size(); row++) {
                List<FAOutputDetailsEntity> outputDetails = inputDetails.get(row).getOutputDetails().parallelStream()
                        .filter(outputDetail -> (outputDetail.getOwner() == owner || outputDetail.getOwner() == null)
                                && outputDetail.getTariff() != null && ObjectUtils.equals(outputDetail.getTariff().getCustomerId(), customerId))
                        .sorted((o1, o2) -> new CompareToBuilder().append(o1.getSeq(), o2.getSeq()).toComparison())
                        .collect(Collectors.toList());
                createResultsRowForCarrierTariffs(sheet.getRow(row + 1), inputDetails.get(row), outputDetails, errorColumnIndex, tariffs);
            }
        }
    }

    private Sheet createSheet(String sheetName) {
        Sheet cloneSheet = workBook.cloneSheet(0);
        workBook.setSheetName(workBook.getSheetIndex(cloneSheet), sheetName);
        return cloneSheet;
    }

    private void createEmptyCells(int quantity, int cellId, Row row) {
        int localCellId = cellId;
        for (int i = 0; i < quantity; i++) {
            createGeneralCell("", localCellId++, row);
        }
    }

    private void createResultsRowForSMC3Tariffs(Row row, FAInputDetailsEntity inputDetails, List<FATariffsEntity> smc3Tariffs) {
        int cellId = lastInputDataColumnIndex;
        Set<String> errors = new LinkedHashSet<String>();
        List<FATariffsEntity> nonProcessedTariffs = new ArrayList<FATariffsEntity>();
        for (FATariffsEntity tariff : smc3Tariffs) {
            Optional<FAOutputDetailsEntity> out = inputDetails.getOutputDetails().parallelStream()
                    .filter(d -> d.getTariff() != null && ObjectUtils.equals(d.getTariff().getId(), tariff.getId())).findFirst();
            if (out.isPresent() && out.get().getSubtotal() != null) {
                createNumberCell(out.get().getSubtotal(), cellId, row);
            } else {
                if (out.isPresent() && StringUtils.isNotBlank(out.get().getErrorMessage())) {
                    errors.add(getErrorMessage(out));
                } else {
                    nonProcessedTariffs.add(tariff);
                }
                createEmptyCells(1, cellId, row);
            }
            cellId++;
        }
        addErrorMessageForNonProcessedTariffs(errors, inputDetails, nonProcessedTariffs);
        createGeneralCell(errors.isEmpty() ? "" : StringUtils.join(errors, ". "), cellId, row);
    }

    private void createResultsRowForBenchmarkTariffs(Row row, FAInputDetailsEntity inputDetails, List<FATariffsEntity> tariffs) {
        int cellId = lastInputDataColumnIndex;
        Set<String> errors = new LinkedHashSet<String>();
        List<FATariffsEntity> nonProcessedTariffs = new ArrayList<FATariffsEntity>();
        for (FATariffsEntity tariff : tariffs) {
            Optional<FAOutputDetailsEntity> out = inputDetails.getOutputDetails().parallelStream()
                    .filter(d -> d.getTariff() != null && ObjectUtils.equals(d.getTariff().getId(), tariff.getId())).findFirst();
            if (out.isPresent() && out.get().getSubtotal() != null) {
                createNumberCell(out.get().getSubtotal(), cellId, row);
                createNumberCell(tariff.getPricingProfileId(), cellId + 1, row);
            } else {
                if (out.isPresent() && StringUtils.isNotBlank(out.get().getErrorMessage())) {
                    errors.add(getErrorMessage(out));
                } else {
                    nonProcessedTariffs.add(tariff);
                }
                createEmptyCells(2, cellId, row);
            }
            cellId += 2;
        }
        addErrorMessageForNonProcessedTariffs(errors, inputDetails, nonProcessedTariffs);
        createGeneralCell(errors.isEmpty() ? "" : StringUtils.join(errors, ". "), cellId, row);
    }

    private String getScac(String scac, StatusYesNo excludedFromBooking) {
        if (StatusYesNo.YES.equals(excludedFromBooking)) {
            return String.format("BM%s", scac);
        }
        return scac;
    }

    private void createResultsRowForCarrierTariffs(Row row, FAInputDetailsEntity inputDetails, List<FAOutputDetailsEntity> outputDetails,
            int errorColumnIndex, List<FATariffsEntity> tariffs) {
        int cellId = lastInputDataColumnIndex;
        Set<String> errors = new LinkedHashSet<String>();
        for (FAOutputDetailsEntity out : outputDetails) {
            if (out.getCarrier() != null && out.getSubtotal() != null) {
                createGeneralCell(getScac(out.getCarrier().getScac(), out.getTariff().getPricingProfile().getBlockedFromBooking()), cellId++, row);
                createNumberCell(out.getSubtotal(), cellId++, row);
                createNumberCell(out.getTransitDays(), cellId++, row);
                createNumberCell(out.getTariff().getPricingProfileId(), cellId++, row);
            } else {
                if (out.getCarrier() != null) {
                    createEmptyCells(4, cellId, row);
                    cellId += 4;
                }
                if (StringUtils.isNotBlank(out.getErrorMessage())) {
                    errors.add(getErrorMessage(Optional.of(out)));
                }
            }
        }
        addErrorMessageForNonProcessedTariffs(errors, inputDetails, getNonProcessedTariffs(tariffs, outputDetails));
        createGeneralCell(errors.isEmpty() ? "" : StringUtils.join(errors, ". "), errorColumnIndex, row);
    }

    private List<FATariffsEntity> getNonProcessedTariffs(List<FATariffsEntity> tariffs, List<FAOutputDetailsEntity> outputDetails) {
        return tariffs.stream().filter(t -> outputDetails.parallelStream().noneMatch(o -> t.getId().equals(o.getTariff().getId())))
                .collect(Collectors.toList());
    }

    private void addErrorMessageForNonProcessedTariffs(Set<String> errors, FAInputDetailsEntity inputDetails,
            List<FATariffsEntity> nonProcessedTariffs) {
        if (inputDetails.getCompleted() != StatusYesNo.YES) {
            errors.clear();
            errors.add("Row was not analyzed");
        } else if (!nonProcessedTariffs.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("No pricing was built for following selected tariff");
            if (nonProcessedTariffs.size() > 1) {
                errorMessage.append('s');
            }
            errorMessage.append(": ");
            errorMessage.append(nonProcessedTariffs.stream().map(FATariffsEntity::getTariffName).collect(Collectors.joining(", ")));
            errors.add(errorMessage.toString());
        }
    }

    private String getErrorMessage(Optional<FAOutputDetailsEntity> out) {
        return out.get().getErrorMessage().replaceFirst("\\.+$", ""); // remove dots in the end of the line
    }

    private void createUserInputRow(Sheet sheet, FAInputDetailsEntity inputData, int rowIndex) {
        Row row = sheet.createRow(rowIndex);
        int cellId = 0;
        createNumberCell(inputData.getUserSeq(), cellId++, row);
        createGeneralCell(inputData.getCarrierScac(), cellId++, row);
        createDateCell(inputData.getShipmentDate(), cellId++, row);
        createGeneralCell(inputData.getOriginCity(), cellId++, row);
        createGeneralCell(inputData.getOriginState(), cellId++, row);
        createGeneralCell(inputData.getOriginZip(), cellId++, row);
        createGeneralCell(inputData.getOriginOverrideZip(), cellId++, row);
        createGeneralCell(inputData.getOriginCountry(), cellId++, row);
        createGeneralCell(inputData.getDestCity(), cellId++, row);
        createGeneralCell(inputData.getDestState(), cellId++, row);
        createGeneralCell(inputData.getDestZip(), cellId++, row);
        createGeneralCell(inputData.getDestOverrideZip(), cellId++, row);
        createGeneralCell(inputData.getDestCountry(), cellId++, row);
        createNumberCell(inputData.getPallet(), cellId++, row);
        createNumberCell(inputData.getPieces(), cellId++, row);
        for (int i = 1; i <= 10; i++) {
            int materialIndex = i;
            Optional<FAMaterialsEntity> material = inputData.getMaterials().stream().filter(m -> m.getSeq() == materialIndex).findFirst();
            if (material.isPresent()) {
                createGeneralCell(material.get().getCommodityClass().getDbCode(), cellId, row);
                createNumberCell(material.get().getWeight(), cellId + 1, row);
            } else {
                createEmptyCells(2, cellId, row);
            }
            cellId += 2;
        }
        for (int i = 1; i <= 10; i++) {
            int accIndex = i - 1;
            Optional<FAAccessorialsEntity> accessorial = inputData.getAccessorials().stream().filter(a -> a.getSeq() == accIndex).findFirst();
            if (accessorial.isPresent()) {
                createGeneralCell(accessorial.get().getAccessorial(), cellId, row);
            } else {
                createEmptyCells(1, cellId, row);
            }
            cellId++;
        }
        createGeneralCell(inputData.getCalculateFSC().getCode(), cellId++, row);
    }

    private void createHeaderForSMC3Tariffs(Sheet smc3Sheet, List<FATariffsEntity> tariffs) {
        Row row = smc3Sheet.getRow(0);
        int cellId = lastInputDataColumnIndex;
        for (int i = 0; i < tariffs.size(); i++) {
            createHeaderCell(tariffs.get(i).getTariffName(), cellId, row);
            smc3Sheet.autoSizeColumn(cellId++);
        }
        createHeaderCell("Error Message", cellId, row);
        smc3Sheet.autoSizeColumn(cellId);
    }

    private void createHeaderForBenchmarkTariffs(Sheet benchmarkSheet, List<FATariffsEntity> tariffs) {
        Row row = benchmarkSheet.getRow(0);
        int cellId = lastInputDataColumnIndex;
        for (int i = 0; i < tariffs.size(); i++) {
            createHeaderCell(tariffs.get(i).getTariffName(), cellId, row);
            benchmarkSheet.autoSizeColumn(cellId++);
            createHeaderCell("Move ID", cellId, row);
            benchmarkSheet.autoSizeColumn(cellId++);
        }
        createHeaderCell("Error Message", cellId, row);
        benchmarkSheet.autoSizeColumn(cellId++);
    }

    private int createHeaderForCarrierTariffs(Sheet blanketSheet, List<FAInputDetailsEntity> inputDetails, List<FATariffsEntity> tariffs) {
        Row row = blanketSheet.getRow(0);
        int cellId = lastInputDataColumnIndex;
        Set<Long> tariffsIds = tariffs.stream().map(FATariffsEntity::getId).collect(Collectors.toSet());
        OptionalLong maxNumberOfCarriers = inputDetails.parallelStream()
                .mapToLong(inputDetail -> getOutputDetailsCountForTariffs(tariffsIds, inputDetail)).max();

        if (maxNumberOfCarriers.isPresent() && maxNumberOfCarriers.getAsLong() > 0) {
            for (int i = 1; i <= maxNumberOfCarriers.getAsLong(); i++) {
                createHeaderCell("Carrier " + i, cellId, row);
                blanketSheet.autoSizeColumn(cellId++);
                createHeaderCell("Cost " + i, cellId, row);
                blanketSheet.autoSizeColumn(cellId++);
                createHeaderCell("Transit " + i, cellId, row);
                blanketSheet.autoSizeColumn(cellId++);
                createHeaderCell("Move ID " + i, cellId, row);
                blanketSheet.autoSizeColumn(cellId++);
            }
        }
        createHeaderCell("Error Message", cellId, row); // cell ID should not be incremented here
        blanketSheet.autoSizeColumn(cellId);
        return cellId;
    }

    private long getOutputDetailsCountForTariffs(Set<Long> tariffsIds, FAInputDetailsEntity inputDetail) {
        return inputDetail.getOutputDetails().parallelStream()
                .filter(outputDetail -> isOutputDetailForOneOfSpecifiedTariffs(tariffsIds, outputDetail))
                .count();
    }

    private boolean isOutputDetailForOneOfSpecifiedTariffs(Set<Long> tariffsIds, FAOutputDetailsEntity outputDetail) {
        return outputDetail.getOwner() == CostDetailOwner.C && outputDetail.getTariff() != null
                && tariffsIds.contains(outputDetail.getTariff().getId());
    }

    private void createGeneralHeaderRow(Sheet sheet) {
        Row row = sheet.createRow(0);
        int cellId = 0;
        createHeaderCell("seq", cellId++, row);
        createHeaderCell("SCAC", cellId++, row);
        createHeaderCell("ShipDate", cellId++, row);
        createHeaderCell("Origin_City", cellId++, row);
        createHeaderCell("Origin_State", cellId++, row);
        createHeaderCell("Origin_Zip", cellId++, row);
        createHeaderCell("Origin_Override_Zip", cellId++, row);
        createHeaderCell("Origin_Country", cellId++, row);
        createHeaderCell("Dest_City", cellId++, row);
        createHeaderCell("Dest_State", cellId++, row);
        createHeaderCell("Dest_Zip", cellId++, row);
        createHeaderCell("Dest_Override_Zip", cellId++, row);
        createHeaderCell("Dest_Country", cellId++, row);
        createHeaderCell("Pallet", cellId++, row);
        createHeaderCell("Pieces", cellId++, row);
        for (int i = 1; i <= 10; i++) {
            createHeaderCell("Class" + i, cellId++, row);
            createHeaderCell("Weight" + i, cellId++, row);
        }
        for (int i = 1; i <= 10; i++) {
            createHeaderCell("Accessorial" + i, cellId++, row);
        }
        createHeaderCell("Calculate_FSC", cellId++, row);
        for (int columnPosition = 0; columnPosition < cellId; columnPosition++) {
            sheet.autoSizeColumn(columnPosition);
        }
        lastInputDataColumnIndex = cellId;
    }

    private void createGeneralCell(String value, int cellId, Row row) {
        Cell cell = row.createCell(cellId);
        if (StringUtils.isNotBlank(value)) {
            cell.setCellValue(value);
        }
        cell.setCellStyle(generalStyle);
        cell.setCellType(CellType.STRING);
    }

    private void createDateCell(Date value, int cellId, Row row) {
        Cell cell = row.createCell(cellId);
        if (value != null) {
            cell.setCellValue(value);
        }
        cell.setCellStyle(dateStyle);
    }

    private void createHeaderCell(String value, int cellId, Row row) {
        Cell cell = row.createCell(cellId);
        cell.setCellValue(value);
        cell.setCellStyle(headerStyle);
        cell.setCellType(CellType.STRING);
    }

    private void createNumberCell(Number value, int cellId, Row row) {
        Cell cell = row.createCell(cellId);
        if (value != null) {
            cell.setCellValue(value.toString());
        }
        if (value instanceof BigDecimal) {
            cell.setCellStyle(bigDecimalStyle);
        } else {
            cell.setCellStyle(generalStyle);
        }
    }

    /**
     * Returns customer name which satisfies rules from {@link WorkbookUtil#createSafeSheetName(String)} and
     * which is unique for the workbook.
     * 
     * @param customerName
     *            customer name
     * @return customer name for workbook
     */
    private String getCustomerNameForExcel(String customerName) {
        String customerNameForExcel = customerName;
        if (customerNameForExcel.length() > 26) {
            customerNameForExcel = StringUtils.left(customerNameForExcel, 12) + "..." + StringUtils.right(customerNameForExcel, 11);
        }
        customerNameForExcel = WorkbookUtil.createSafeSheetName(customerNameForExcel);
        if (customerNames.containsKey(customerNameForExcel)) {
            String mapKey = customerNameForExcel;
            if (customerNameForExcel.length() > 22) {
                customerNameForExcel = StringUtils.left(customerNameForExcel, 10) + "..." + StringUtils.right(customerNameForExcel, 9);
            }
            customerNameForExcel = StringUtils.join(customerNameForExcel, " (", customerNames.get(mapKey).toString(), ")");
            customerNames.put(mapKey, customerNames.get(mapKey) + 1);
        } else {
            customerNames.put(customerNameForExcel, 1);
        }
        return customerNameForExcel;
    }
}
