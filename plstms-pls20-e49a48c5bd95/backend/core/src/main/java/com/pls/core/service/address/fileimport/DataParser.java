package com.pls.core.service.address.fileimport;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellReference;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.service.impl.security.util.SecurityUtils;

/**
 * Class to parse {@link com.pls.core.domain.address.UserAddressBookEntity} from {@link Row} data.
 *
 * @author Artem Arapov
 *
 */
public class DataParser {
    private static final int MIN_COUNTRY_CODE_LENGTH = 1;

    private static final int MAX_COUNTRY_CODE_LENGTH = 3;

    private static final int MIN_AREA_CODE_LENGTH = 1;

    private static final int MAX_AREA_CODE_LENGTH = 3;

    private static final int PHONE_NUMBER_LENGHT = 7;

    private final HeaderData headerData = new HeaderData();

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    /**
     * Get headerData value.
     *
     * @return the headerData.
     */
    public HeaderData getHeaderData() {
        return headerData;
    }

    /**
     * Parse {@link Row} data.
     * 
     * @param row
     *            Not <code>null</code> {@link Row} instance.
     * 
     * @return instance of {@link com.pls.core.domain.address.UserAddressBookEntity} if row was not emty and null in other case
     * @throws ImportFileInvalidDataException
     *             row has no mandatory data has invalid format.
     */
    public UserAddressBookEntity parseRow(Row row) throws ImportFileInvalidDataException {
        Map<AddressFields, String> values = parseValues(row);
        String rowName = prepareRowName(row);

        UserAddressBookEntity entity = null;
        if (!values.isEmpty()) {
            entity = new UserAddressBookEntity();
            entity.setAddressName(readAddressName(values, rowName));
            entity.setAddressCode(readAddressCode(values, rowName));
            entity.setContactName(readString(values, AddressFields.CONTACT_NAME, true, rowName));
            entity.setAddress(readAddressEntity(values, rowName));
            entity.setPhone(readPhoneFaxObject(values, rowName, true));
            entity.setFax(readPhoneFaxObject(values, rowName, false));
            entity.setEmail(readString(values, AddressFields.EMAIL, true, rowName));
            if (!readBoolean(values, AddressFields.SHARED, rowName)) {
                entity.setPersonId(SecurityUtils.getCurrentPersonId());
            }
            entity.setDeliveryFrom(readTimeEntity(values, AddressFields.RECIEVING_HOURS_OF_OPERATION_BEGIN, rowName));
            entity.setDeliveryTo(readTimeEntity(values, AddressFields.RECIEVING_HOURS_OF_OPERATION_END, rowName));
            entity.setPickupFrom(readTimeEntity(values, AddressFields.SHIPPING_HOURS_OF_OPERATION_BEGIN, rowName));
            entity.setPickupTo(readTimeEntity(values, AddressFields.SHIPPING_HOURS_OF_OPERATION_END, rowName));
            entity.setPickupNotes(readString(values, AddressFields.PICKUP_NOTES, false, rowName));
            entity.setDeliveryNotes(readString(values, AddressFields.DELIVERY_NOTES, false, rowName));
        }

        return entity;
    }

    @SuppressWarnings("deprecation")
    private Map<AddressFields, String> parseValues(Row row) throws ImportFileInvalidDataException {
        Map<AddressFields, String> result = new HashMap<AddressFields, String>();
        for (Entry<AddressFields, Integer> entry : headerData.entrySet()) {
            String cellValue;
            Cell cell = row.getCell(entry.getValue(), MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                switch (cell.getCellTypeEnum()) {
                case STRING:
                        cellValue = cell.getStringCellValue();
                        if (StringUtils.isNotBlank(cellValue)) {
                            result.put(entry.getKey(), cellValue.trim());
                        }
                        break;

                case NUMERIC:
                        cell.setCellType(CellType.STRING);
                        cellValue = cell.getStringCellValue();
                        if (StringUtils.isNotBlank(cellValue)) {
                            result.put(entry.getKey(), cellValue.trim());
                        }
                        break;

                    default:
                    throw new ImportFileInvalidDataException("Cell '" + new CellReference(cell).formatAsString()
                            + "' has invalid type.");
                }
            }
        }

        return result;
    }

    private String prepareRowName(Row row) {
        return row.getSheet().getSheetName() + ":" + CellReference.convertNumToColString(row.getRowNum());
    }

    /**
     * Reads Address Name.
     * @param values
     * @param rowName
     * @return Address name or null to generate it by DB layer
     * @throws ImportFileInvalidDataException
     */
    private String readAddressName(Map<AddressFields, String> values, String rowName) throws ImportFileInvalidDataException {
        return readString(values, AddressFields.ADDRESS_NAME, false, rowName);
    }

    /**
     * Reads Location Code.
     * @param values
     * @param rowName
     * @return Location code or null to generate by database
     * @throws ImportFileInvalidDataException
     */
    private String readAddressCode(Map<AddressFields, String> values, String rowName) throws ImportFileInvalidDataException {
        return readString(values, AddressFields.ADDRESS_CODE, false, rowName);
    }

    private CountryEntity readCountryEntity(Map<AddressFields, String> values, String rowName)
            throws ImportFileInvalidDataException {
        CountryEntity country = new CountryEntity();
        country.setId(readString(values, AddressFields.COUNTRY_CODE, true, rowName));

        return country;
    }

    private AddressEntity readAddressEntity(Map<AddressFields, String> values, String rowName)
            throws ImportFileInvalidDataException {
        AddressEntity address = new AddressEntity();

        address.setCountry(readCountryEntity(values, rowName));
        address.setAddress1(readString(values, AddressFields.ADDRESS1, true, rowName));
        address.setAddress2(readString(values, AddressFields.ADDRESS2, false, rowName));
        address.setCity(readString(values, AddressFields.CITY, true, rowName));
        address.setState(readStateEntity(values, rowName));
        address.setZip(readString(values, AddressFields.ZIP_POSTAL_CODE, true, rowName));

        return address;
    }

    private StateEntity readStateEntity(Map<AddressFields, String> values, String rowName)
            throws ImportFileInvalidDataException {
        StateEntity state = new StateEntity();
        StatePK id = new StatePK();
        id.setCountryCode(readString(values, AddressFields.COUNTRY_CODE, true, rowName));
        id.setStateCode(readString(values, AddressFields.STATE_PROVINCE, true, rowName));
        state.setStatePK(id);

        return state;
    }

    private PhoneEntity readPhoneFaxObject(Map<AddressFields, String> values, String rowName, boolean isPhone)
            throws ImportFileInvalidDataException {
        PhoneEntity object = new PhoneEntity();
        String number;
        if (isPhone) {
            object.setType(PhoneType.VOICE);
            number = readString(values, AddressFields.PHONE, true, rowName);
        } else {
            object.setType(PhoneType.FAX);
            number = readString(values, AddressFields.FAX, false, rowName);
        }

        if (StringUtils.isBlank(number)) {
            return null;
        }

        number = number.replaceAll("[^0-9()]", "");

        int openBraceIndex = number.indexOf('(');
        int closeBraceIndex = number.indexOf(')');
        validateBracesExisting(rowName, number, openBraceIndex, closeBraceIndex);

        String countryCodeNumber = number.substring(0, openBraceIndex);
        validateCountryCodeNumber(rowName, number, countryCodeNumber);

        String areaCodeNumber = number.substring(openBraceIndex + 1, closeBraceIndex);
        validateAreaCodeNumber(rowName, number, areaCodeNumber);

        String phoneNumber = number.substring(closeBraceIndex + 1, number.length());
        validatePhoneNumber(rowName, number, phoneNumber);

        object.setCountryCode(countryCodeNumber);
        object.setAreaCode(areaCodeNumber);
        object.setNumber(phoneNumber);

        return object;
    }

    private String readString(Map<AddressFields, String> values, AddressFields field, boolean mandatory, String rowName)
            throws ImportFileInvalidDataException {
        String result = StringUtils.trimToNull(values.get(field));
        checkLength(field, rowName, result);
        if (result == null && mandatory) {
            throw new ImportFileInvalidDataException("Value '" + field.getHeader() + "' was not found for '" + rowName
                    + "' row.");
        }
        return result;
    }

    private boolean readBoolean(Map<AddressFields, String> values, AddressFields field, String rowName)
            throws ImportFileInvalidDataException {
        String result = StringUtils.trimToNull(values.get(field));
        checkLength(field, rowName, result);
        if (result == null && field.isRequired()) {
            throw new ImportFileInvalidDataException("Value '" + field.getHeader() + "' was not found for '" + rowName
                    + "' row.");
        }
        if (result == null && !field.isRequired()) {
            return false;
        }
        if ("YES".equalsIgnoreCase(result)) {
            return true;
        } else if ("NO".equalsIgnoreCase(result)) {
            return false;
        }
        return false;
    }

    /**
     * Check length.
     *
     * @param field the field
     * @param rowName the row name
     * @param result the result
     * @throws ImportFileInvalidDataException thrown if length is incorrect
     */
    public void checkLength(AddressFields field, String rowName, String result) throws ImportFileInvalidDataException {
        if (result != null && field.getMaxLength() != null && result.length() > field.getMaxLength()) {
            throw new ImportFileInvalidDataException("Value '" + field.getHeader() + "' is too large for '" + rowName
                    + "' row.");
        }
    }

    private Time readTimeEntity(Map<AddressFields, String> values, AddressFields field, String rowName)
            throws ImportFileInvalidDataException {
        String result = StringUtils.trimToNull(values.get(field));
        checkLength(field, rowName, result);
        if (result == null && field.isRequired()) {
            throw new ImportFileInvalidDataException("Value '" + field.getHeader() + "' was not found for '" + rowName + "' row.");
        }
        if (result == null && !field.isRequired()) {
            return null;
        }
        try {
            return new Time(simpleDateFormat.parse(result).getTime());
        } catch (ParseException e) {
            throw new ImportFileInvalidDataException("Value '" + field.getHeader() + "' has invalid format for '" + rowName + "' row.");
        }
    }


    private void validatePhoneNumber(String rowName, String number, String phoneNumber)
            throws ImportFileInvalidDataException {
        if (phoneNumber.length() != PHONE_NUMBER_LENGHT) {
            throwWrongFormatException(rowName, number);
        }
    }

    private void validateAreaCodeNumber(String rowName, String number, String areaCodeNumber)
            throws ImportFileInvalidDataException {
        if (areaCodeNumber.length() < MIN_AREA_CODE_LENGTH || areaCodeNumber.length() > MAX_AREA_CODE_LENGTH) {
            throwWrongFormatException(rowName, number);
        }
    }

    private void validateCountryCodeNumber(String rowName, String number, String countryCodeNumber)
            throws ImportFileInvalidDataException {
        if (countryCodeNumber.length() < MIN_COUNTRY_CODE_LENGTH || countryCodeNumber.length() > MAX_COUNTRY_CODE_LENGTH) {
            throwWrongFormatException(rowName, number);
        }
    }

    private void validateBracesExisting(String rowName, String number, int openBraceIndex, int closeBraceIndex)
            throws ImportFileInvalidDataException {
        if (openBraceIndex < 0 && closeBraceIndex < 0) {
            throwWrongFormatException(rowName, number);
        }
    }

    private void throwWrongFormatException(String rowName, String number) throws ImportFileInvalidDataException {
        throw new ImportFileInvalidDataException("Invalid Phone format'" + number + "' was found in '" + rowName
                + "' row.");
    }
}
