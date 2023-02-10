package com.pls.ltlrating.service.analysis.fileimport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.exception.fileimport.ImportFileParseException;

/**
 * Validator for Freight Analysis Report.
 * 
 * @author Dmitriy Davydenko
 *
 */
public class AnalysisReportSheetValidator {

    private static final String USA = "USA";
    private static final List<String> COUNTRY_LIST = Arrays.asList(USA, "CAN");
    private static final List<String> YES_NO = Arrays.asList("Y", "N");
    private static final Pattern WEIGHT_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");
    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern LETTERS_ONLY_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    private final List<String> errorsList;
    private CellStyle style;
    private Row row;
    private Map<AnalysisFieldsDescription, Integer> fieldDescriptorMap;
    private Sheet sheet;
    private int errorCellNumber;

    private DataFormatter formatter = new DataFormatter();

    /**
     * Constructor of Freight Analysis Report validator.
     * 
     * @param sheet - Freight Analysis sheet to validate.
     * @param style - cell style for error message cell.
     * 
     * @author Dmitriy Davydenko
     *
     */
    public AnalysisReportSheetValidator(CellStyle style, Sheet sheet) {
        this.sheet = sheet;
        fieldDescriptorMap = new HashMap<>();
        Row headerRow = this.sheet.getRow(0);
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            String headerCellName = formatter.formatCellValue(headerRow.getCell(i));
            if (StringUtils.isNotEmpty(headerCellName)) {
                AnalysisFieldsDescription descriptor = AnalysisFieldsDescription.getByHeaderText(headerCellName);
                if (descriptor != null) {
                    fieldDescriptorMap.put(descriptor, i);
                }
            }
        }
        if (headerRow.getCell(headerRow.getLastCellNum() - 1).getStringCellValue().equals("Error Description")) {
            errorCellNumber = headerRow.getLastCellNum() - 1;
        } else {
            errorCellNumber = headerRow.getLastCellNum();
        }
        this.errorsList = new ArrayList<>();
        this.style = style;
    }

    /**
     * Method for checking if there is data in correctly formatted file.
     * We assume that file has no data if first row after header has no input data.
     * 
     * @throws Runtime exception if first row after Header has no input data.
     */
    public void checkForFileEmptiness() {
        if (!isRowFilled(sheet.getRow(1))) {
            throw new IllegalStateException();
        }
    }

    /**
     * Method for validation of xls/xlsx sheet.
     * 
     * @return true - if validation errors appeared.
     *         false - is no validation errors.
     */
    public boolean validateSheet() {
        int validationErrors = 0;
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            this.row = sheet.getRow(rowNum);
            if (isRowFilled(row)) {
                try {
                    validationErrors += validateRow();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                break;
            }
        }
        return validationErrors > 0;
    }

    private int validateRow() {
        String origCountry = getFormattedStringValue(AnalysisFieldsDescription.ORIGIN_COUNTRY);
        String destCountry = getFormattedStringValue(AnalysisFieldsDescription.DEST_COUNTRY);

        validateNumericField(AnalysisFieldsDescription.SEQ);
        validateSCAC(AnalysisFieldsDescription.SCAC);
        validateDateField(AnalysisFieldsDescription.SHIPDATE);
        validateCity(AnalysisFieldsDescription.ORIGIN_CITY);
        validateState(AnalysisFieldsDescription.ORIGIN_STATE);

        if (origCountry.equalsIgnoreCase(USA)) {
            validateZip(AnalysisFieldsDescription.ORIGIN_ZIP, true);
            validateZip(AnalysisFieldsDescription.ORIGIN_OVERRIDE_ZIP, false);
        } else {
            checkIfFieldIsEmpty(AnalysisFieldsDescription.ORIGIN_ZIP);
        }

        validateCountry(AnalysisFieldsDescription.ORIGIN_COUNTRY);
        validateCity(AnalysisFieldsDescription.DEST_CITY);
        validateState(AnalysisFieldsDescription.DEST_STATE);

        if (destCountry.equalsIgnoreCase(USA)) {
            validateZip(AnalysisFieldsDescription.DEST_ZIP, true);
            validateZip(AnalysisFieldsDescription.DEST_OVERRIDE_ZIP, false);
        } else {
            checkIfFieldIsEmpty(AnalysisFieldsDescription.DEST_ZIP);
        }

        validateCountry(AnalysisFieldsDescription.DEST_COUNTRY);
        validateNumericField(AnalysisFieldsDescription.PALLET);
        validateNumericField(AnalysisFieldsDescription.PIECES);

        validateWeightAndClass();

        validateAccessorials();

        validateCalculateFSC();

        Cell errorCell = row.createCell(errorCellNumber);
        errorCell.setCellStyle(style);

        if (errorsList.size() > Collections.emptyList().size()) {
            errorCell.setCellValue(String.join(". ", errorsList));
            errorCell.setCellType(CellType.STRING);
            errorsList.clear();
            return 1;
        }
        return 0;
    }

    private void validateAccessorials() {
        for (int i = 1; i <= 10; i++) {
            AnalysisFieldsDescription accessorialDescriptor = AnalysisFieldsDescription.getByName("Accessorial" + i);
            if (fieldDescriptorMap.containsKey(accessorialDescriptor) && !isLengthValid(accessorialDescriptor)) {
                errorsList.add("Invalid Accessorial" + i + " length");
            }
        }
    }

    private boolean isRowFilled(Row row) {
        for (int i = 0; i < fieldDescriptorMap.size(); i++) {
            Cell cell = row.getCell(i);
            if (formatter.formatCellValue(cell).length() > 0) {
                return true;
            }
        }
        return false;
    }

    public int getErrorCellNumber() {
        return errorCellNumber;
    }

    /**
     * Validates header to contain all required fields.
     * 
     * @throws ImportFileParseException
     *                      - if required field is not found.
     */
    public void validateHeader() throws ImportFileParseException {
        for (AnalysisFieldsDescription descriptor : AnalysisFieldsDescription.values()) {
            if (descriptor.isRequired() && !fieldDescriptorMap.containsKey(descriptor)) {
                throw new ImportFileParseException("Column '" + descriptor.getHeader() + "' was not found.");
            }
        }
    }

    private void validateCalculateFSC() {
        if (!fieldDescriptorMap.containsKey(AnalysisFieldsDescription.CALCULATE_FSC)) {
            return;
        }
        String value = getFormattedStringValue(AnalysisFieldsDescription.CALCULATE_FSC);
        if (!YES_NO.contains(value.toUpperCase())) {
            setInvalidValueError(AnalysisFieldsDescription.CALCULATE_FSC);
        }
    }

    private void validateWeightAndClass() {
        for (int i = 1; i <= 10; i++) {
            AnalysisFieldsDescription classDescriptor = AnalysisFieldsDescription.getByName("Class" + i);
            AnalysisFieldsDescription weightDescriptor = AnalysisFieldsDescription.getByName("Weight" + i);
            boolean isClassColumnPresent = fieldDescriptorMap.containsKey(classDescriptor);
            boolean isWeightColumnPresent = fieldDescriptorMap.containsKey(weightDescriptor);
            if (!isClassColumnPresent && !isWeightColumnPresent) {
                continue;
            } else if (isClassColumnPresent && isWeightColumnPresent) {
                String classValue = getNumericValueAsString(classDescriptor);
                String weightValue = getNumericValueAsString(weightDescriptor);
                if (StringUtils.isBlank(classValue) != StringUtils.isBlank(weightValue)) {
                    setClassWeightError(weightDescriptor, classDescriptor);
                } else {
                    validateClass(classDescriptor, classValue);
                    validateWeight(weightDescriptor, weightValue);
                }
            } else {
                setClassWeightError(weightDescriptor, classDescriptor);
            }
        }
    }

    private void validateWeight(AnalysisFieldsDescription weightDesc, String weightValue) {
        if (!weightValue.isEmpty() && !isValueCorrect(weightValue, WEIGHT_PATTERN)) {
            setInvalidValueError(weightDesc);
            return;
        }
        if (weightValue.isEmpty() && weightDesc.isRequired()) {
            setEmptyCellError(weightDesc);
        }
    }

    private void validateClass(AnalysisFieldsDescription classDesc, String classValue) {
        if (classDesc.isRequired() && classValue.isEmpty()) {
            setEmptyCellError(classDesc);
            return;
        }
        if (!classValue.isEmpty()) {
            try {
                CommodityClass.convertFromDbCode(classValue);
            } catch (IllegalArgumentException e) {
                setInvalidValueError(classDesc);
            }
        }
    }

    private void validateCountry(AnalysisFieldsDescription description) {
        if (!fieldDescriptorMap.containsKey(description)) {
            return;
        }
        String value = getFormattedStringValue(description);
        if (value.length() == 0) {
            setEmptyCellError(description);
            return;
        }
        if (!COUNTRY_LIST.contains(value.toUpperCase())) {
            setInvalidValueError(description);
        }
    }

    private void validateZip(AnalysisFieldsDescription description, boolean required) {
        if (!fieldDescriptorMap.containsKey(description)) {
            return;
        }
        String value = getNumericValueAsString(description);
        if (value.isEmpty() && required) {
            setEmptyCellError(description);
            return;
        }
        if (!value.isEmpty() && (value.length() != 5 || !isValueCorrect(value, DIGITS_ONLY_PATTERN))) {
            setInvalidValueError(description);
        }
    }

    private void validateState(AnalysisFieldsDescription description) {
        if (!fieldDescriptorMap.containsKey(description)) {
            return;
        }
        String value = getFormattedStringValue(description);
        if (!value.isEmpty()) {
            if (!isLettersOnlyValue(value)) {
                setInvalidValueError(description);
                return;
            }
            if (value.length() != 2) {
                setWrongSizeError(description);
            }
        }
    }

    private void validateCity(AnalysisFieldsDescription description) {
        if (!fieldDescriptorMap.containsKey(description)) {
            return;
        }
        if (!isLengthValid(description)) {
            setTooLongFieldError(description);
        }
    }

    private void validateSCAC(AnalysisFieldsDescription description) {
        if (!fieldDescriptorMap.containsKey(description)) {
            return;
        }
        String value = getFormattedStringValue(description);
        if (!value.isEmpty() && value.length() != description.getMaxLength()) {
            setWrongSizeError(description);
        }
    }

    private void validateNumericField(AnalysisFieldsDescription description) {
        if (!fieldDescriptorMap.containsKey(description)) {
            return;
        }
        String value = getNumericValueAsString(description);
        if (!value.isEmpty() && !isValueCorrect(value, DIGITS_ONLY_PATTERN)) {
            errorsList.add(description.getHeader() + " contains letters");
        }
    }

    private void validateDateField(AnalysisFieldsDescription description) {
        if (!fieldDescriptorMap.containsKey(description)) {
            return;
        }
        try {
            Cell dateCell = this.row.getCell((int) fieldDescriptorMap.get(description));
            if (dateCell != null) {
                 dateCell.getDateCellValue();
            }
        } catch (RuntimeException ex) {
            setInvalidValueError(description);
        }
    }

    private boolean isLengthValid(AnalysisFieldsDescription description) {
        String value = getFormattedStringValue(description);
        return value.length() <= description.getMaxLength();
    }

    private void checkIfFieldIsEmpty(AnalysisFieldsDescription description) {
        if (!fieldDescriptorMap.containsKey(description)) {
            return;
        }
        String value = getFormattedStringValue(description);
        if (value.isEmpty()) {
            setEmptyCellError(description);
        }
    }

    private String getFormattedStringValue(AnalysisFieldsDescription description) {
        Cell cell = this.row.getCell((int) fieldDescriptorMap.get(description));
        return formatter.formatCellValue(cell);
    }

    private boolean isValueCorrect(String value, Pattern pattern) {
        Matcher m = pattern.matcher(value);
        return m.matches();
    }

    private boolean isLettersOnlyValue(String value) {
        Matcher m = LETTERS_ONLY_PATTERN.matcher(value);
        return m.matches();
    }

    private String getNumericValueAsString(AnalysisFieldsDescription descriptor) {
        Cell numericCell = this.row.getCell((int) fieldDescriptorMap.get(descriptor));
        if (numericCell != null) {
            numericCell.setCellType(CellType.STRING);
            return numericCell.getStringCellValue();
        }
        return "";
    }

    private void setEmptyCellError(AnalysisFieldsDescription description) {
        errorsList.add("Empty " + description.getHeader());
    }
    private void setInvalidValueError(AnalysisFieldsDescription description) {
        errorsList.add("Invalid " + description.getHeader());
    }
    private void setWrongSizeError(AnalysisFieldsDescription description) {
        errorsList.add("Wrong " + description.getHeader() + " size");
    }
    private void setTooLongFieldError(AnalysisFieldsDescription description) {
        errorsList.add(description.getHeader() + " field is too long");
    }
    private void setClassWeightError(AnalysisFieldsDescription weightDescriptor, AnalysisFieldsDescription classDescriptor) {
        errorsList.add("Product " + weightDescriptor.getHeader() + " should be specified together with " + classDescriptor.getHeader());
    }
}
