package com.pls.user.address.service.impl;

import java.sql.Time;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.service.address.fileimport.AddressFields;
import com.pls.core.service.fileimport.parser.core.Field;
import com.pls.core.service.fileimport.parser.core.Record;
import com.pls.core.service.fileimport.parser.core.RecordParser;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.util.PhoneUtils;

/**
 * Implementation of {@link RecordParser}.
 * 
 * @author Brichak Aleksandr
 * 
 */

public class AddressRecordParser extends RecordParser<UserAddressBookEntity, AddressFields> {

    private static final int MIN_COUNTRY_CODE_LENGTH = 1;

    private static final int MAX_COUNTRY_CODE_LENGTH = 3;

    private static final int MIN_AREA_CODE_LENGTH = 1;

    private static final int MAX_AREA_CODE_LENGTH = 3;

    private static final int PHONE_NUMBER_LENGHT = 7;

    private static final int ZIP_POSTAL_CODE_LENGHT = 10;

    private static final String ADDRESS_NAME_PREFIX = "Imported_Address_00000";

    private Integer countAddress = 0;

    private final Map<Double, CommodityClass> commodityClassValues = new HashMap<Double, CommodityClass>();

    @Override
    public UserAddressBookEntity parseRecord(Record record) throws ImportFileInvalidDataException {
        initCommodityClassValues();

        UserAddressBookEntity entity = new UserAddressBookEntity();
        entity.setAddressName(readAddressName(record));
        entity.setAddressCode(readAddressCode(record));
        entity.setContactName(readString(record, AddressFields.CONTACT_NAME));
        entity.setAddress(readAddressEntity(record));
        entity.setPhone(readPhoneFaxObject(record, true));
        if (entity.getPhone() != null) {
            entity.getPhone().setExtension(readString(record, AddressFields.EXTENSION));
        }
        entity.setFax(readPhoneFaxObject(record, false));
        entity.setEmail(readString(record, AddressFields.EMAIL));
        if (!readBoolean(record, AddressFields.SHARED)) {
            entity.setPersonId(SecurityUtils.getCurrentPersonId());
        }
        entity.setDeliveryFrom(readTimeEntity(record, AddressFields.RECIEVING_HOURS_OF_OPERATION_BEGIN));
        entity.setDeliveryTo(readTimeEntity(record, AddressFields.RECIEVING_HOURS_OF_OPERATION_END));
        entity.setPickupFrom(readTimeEntity(record, AddressFields.SHIPPING_HOURS_OF_OPERATION_BEGIN));
        entity.setPickupTo(readTimeEntity(record, AddressFields.SHIPPING_HOURS_OF_OPERATION_END));
        entity.setPickupNotes(readString(record, AddressFields.PICKUP_NOTES));
        entity.setDeliveryNotes(readString(record, AddressFields.DELIVERY_NOTES));
        String type = StringUtils.trimToNull(readString(record, AddressFields.TYPE));
        AddressType addressType = AddressType.getDefault();
        if (type != null) {
            addressType = AddressType.getByCode(type);
        }
        entity.setType(addressType);
        entity.setIsDefault(readBoolean(record, AddressFields.DEFAULT));

        return entity;
    }

    private boolean readBoolean(Record record, AddressFields headerId)
            throws ImportFileInvalidDataException {
        Field field = getColumnData(record, headerId);
        checkLength(headerId, field);
        if (field.isEmpty() && headerId.isRequired()) {
            throw new ImportFileInvalidDataException(prepareColumnNotFoundMessage(record, headerId));
        }
        if (field.isEmpty() && !headerId.isRequired()) {
            return false;
        }
        return "YES".equalsIgnoreCase(field.getString());
    }

    private Time readTimeEntity(Record record, AddressFields headerId)
            throws ImportFileInvalidDataException {
        Field field = getColumnData(record, headerId);
        checkLength(headerId, field);
        if (field.isEmpty() && headerId.isRequired()) {
            throw new ImportFileInvalidDataException(prepareColumnNotFoundMessage(record, headerId));
        }
        if (field.isEmpty() && !headerId.isRequired()) {
            return null;
        }
            return new Time(field.getDate().getTime());
    }


    /**
     * Reads Address Name.
     * 
     * @param values
     * @param rowName
     * @return Address name
     * @throws ImportFileInvalidDataException
     */
    private String readAddressName(Record record) throws ImportFileInvalidDataException {
        String addressName = readString(record, AddressFields.ADDRESS_NAME);
        if (addressName.isEmpty()) {
            countAddress++;
            addressName = buildAddressName();
        }
        return addressName;
    }

    /**
     * Reads Location Code.
     * 
     * @param values
     * @param rowName
     * @return Location code or null to generate by database
     * @throws ImportFileInvalidDataException
     */
    private String readAddressCode(Record record) throws ImportFileInvalidDataException {
        return readString(record, AddressFields.ADDRESS_CODE);
    }

    private CountryEntity readCountryEntity(Record record) throws ImportFileInvalidDataException {
        CountryEntity country = new CountryEntity();
        country.setId(readString(record, AddressFields.COUNTRY_CODE));

        return country;
    }

    private AddressEntity readAddressEntity(Record record) throws ImportFileInvalidDataException {
        AddressEntity address = new AddressEntity();

        address.setCountry(readCountryEntity(record));
        address.setAddress1(readString(record, AddressFields.ADDRESS1));
        address.setAddress2(readString(record, AddressFields.ADDRESS2));
        address.setCity(readString(record, AddressFields.CITY));
        address.setState(readStateEntity(record));
        address.setZip(validateZipPostalCode(record));

        return address;
    }

    private StateEntity readStateEntity(Record record) throws ImportFileInvalidDataException {
        StateEntity state = new StateEntity();
        StatePK id = new StatePK();
        id.setCountryCode(readString(record, AddressFields.COUNTRY_CODE));
        id.setStateCode(readString(record, AddressFields.STATE_PROVINCE));
        state.setStatePK(id);

        return state;
    }

    private PhoneEntity readPhoneFaxObject(Record record, boolean isPhone) throws ImportFileInvalidDataException {
        PhoneEntity object = new PhoneEntity();
        String number;
        if (isPhone) {
            object.setType(PhoneType.VOICE);
            number = readString(record, AddressFields.PHONE);
        } else {
            object.setType(PhoneType.FAX);
            number = readString(record, AddressFields.FAX);
        }

        if (StringUtils.isBlank(number)) {
            return null;
        }

        PhoneBO phoneBO = PhoneUtils.parse(number);

        String phoneNumber = phoneBO.getNumber();
        String areaCodeNumber = phoneBO.getAreaCode();
        String countryCodeNumber = phoneBO.getCountryCode();

        validatePhoneNumber(record, phoneNumber);
        validateAreaCodeNumber(record, areaCodeNumber);
        validateCountryCodeNumber(record, countryCodeNumber);

        object.setNumber(phoneNumber);
        object.setAreaCode(areaCodeNumber);
        object.setCountryCode(countryCodeNumber);

        return object;
    }

    private void validatePhoneNumber(Record record, String phoneNumber) throws ImportFileInvalidDataException {
        if (phoneNumber == null || phoneNumber.length() != PHONE_NUMBER_LENGHT) {
            throw new ImportFileInvalidDataException(prepareColumnNotFoundMessage(record, AddressFields.PHONE));
        }
    }

    private void validateAreaCodeNumber(Record record, String areaCodeNumber) throws ImportFileInvalidDataException {
        if (areaCodeNumber == null || areaCodeNumber.length() < MIN_AREA_CODE_LENGTH || areaCodeNumber.length() > MAX_AREA_CODE_LENGTH) {
            throw new ImportFileInvalidDataException(prepareColumnNotFoundMessage(record, AddressFields.ADDRESS_CODE));
        }
    }

    private void validateCountryCodeNumber(Record record, String countryCodeNumber)
            throws ImportFileInvalidDataException {
        if (countryCodeNumber == null || countryCodeNumber.length() < MIN_COUNTRY_CODE_LENGTH
                || countryCodeNumber.length() > MAX_COUNTRY_CODE_LENGTH) {
            throw new ImportFileInvalidDataException(prepareColumnNotFoundMessage(record, AddressFields.COUNTRY_CODE));
        }
    }

    private void initCommodityClassValues() throws ImportFileInvalidDataException {
        try {
            if (commodityClassValues.isEmpty()) {
                NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                for (CommodityClass commodityClass : CommodityClass.values()) {
                    double doubleValue = formatter.parse(commodityClass.getDbCode()).doubleValue();
                    commodityClassValues.put(doubleValue, commodityClass);
                }
            }
        } catch (ParseException e) {
            throw new ImportFileInvalidDataException("Unable to init commodity class parser", e);
        }
    }

    private String readString(Record record, AddressFields headerId) throws ImportFileInvalidDataException {
        Field field = getColumnData(record, headerId);
        checkLength(headerId, field);
        if (field.isEmpty() && headerId.isRequired()) {
            throw new ImportFileInvalidDataException(prepareColumnNotFoundMessage(record, headerId));
        }

        return field.getString();
    }

    /**
     * Check length.
     *
     * @param headerId the header id
     * @param field the field
     * @throws ImportFileInvalidDataException thrown if length is invalid
     */
    public void checkLength(AddressFields headerId, Field field) throws ImportFileInvalidDataException {
        if (field.getString() != null && headerId.getMaxLength() != null && field.getString().length() > headerId.getMaxLength()) {
            throw new ImportFileInvalidDataException("Value '" + headerId.getHeader() + "' is too large'");
        }
    }

    private String prepareColumnNotFoundMessage(Record record, AddressFields field) {
        return "Column '" + field.getHeader() + "' was not found for '" + record.getName() + "' row.";
    }

    @Override
    protected AddressFields parseHeaderColumn(String headerString) {
        String header = headerString.replace("\n", "");
        countAddress = 0;
        return AddressFields.getAddressByHeader(header);
    }

    private String buildAddressName() {
        int numberOfCharsToAppend = ADDRESS_NAME_PREFIX.length() - countAddress.toString().length();

        StringBuilder addressName = new StringBuilder();
        addressName.append(ADDRESS_NAME_PREFIX.toCharArray(), 0, numberOfCharsToAppend);
        addressName.append(countAddress);

        return addressName.toString();
    }

    private String validateZipPostalCode(Record record) throws ImportFileInvalidDataException {
        String zipPostalCode = readString(record, AddressFields.ZIP_POSTAL_CODE);
        if (zipPostalCode.length() > ZIP_POSTAL_CODE_LENGHT) {
            throw new ImportFileInvalidDataException(
                    prepareColumnNotFoundMessage(record, AddressFields.ZIP_POSTAL_CODE));
        }
        return zipPostalCode;
    }

}
