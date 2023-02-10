package com.pls.core.service.address.fileimport;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.address.UserAddressBookEntity;

/**
 * Represents field of {@link com.pls.core.domain.address.UserAddressBookEntity}.
 * Holds excel file headers information.
 * 
 * @author Artem Arapov
 *
 */
public enum AddressFields {

    /**
     * Data for {@link com.pls.core.domain.address.UserAddressBookEntity#getAddressName()} field.
     * */
    ADDRESS_NAME("Name", true, 255),
    /**
     * Data for {@link com.pls.core.domain.address.UserAddressBookEntity#getAddressCode()} field.
     * */
    ADDRESS_CODE("Code", false, 255),
    /**
     * Data for {@link com.pls.core.domain.address.UserAddressBookEntity#getContactName()} field.
     * */
    CONTACT_NAME("Contact Name", true, 255),

    COUNTRY_CODE("Country Code", true, 3),
    ADDRESS1("Address 1", true, 200),
    ADDRESS2("Address 2", false, 200),
    CITY("City", true, 30),
    STATE_PROVINCE("St/Province", true, 6),
    ZIP_POSTAL_CODE("Zip/Postal Code", true, 10),
    /**
     * Data for {@link com.pls.core.domain.address.UserAddressBookEntity#getPhone()} field.
     * */
    PHONE("Phone", false, null),
    EXTENSION("Extension", false, null),
    /**
     * Data for {@link com.pls.core.domain.address.UserAddressBookEntity#getFax()} field.
     * */
    FAX("Fax", false, null),
    /**
     * Data for {@link com.pls.core.domain.address.UserAddressBookEntity#getEmail()} field.
     * */
    EMAIL("E-mail", false, 255),

    SHARED("Shared (yes or no)", true, 3),

    SHIPPING_HOURS_OF_OPERATION_BEGIN("Shipping hours of operation begin", false, null),

    SHIPPING_HOURS_OF_OPERATION_END("Shipping hours of operation end", false, null),

    RECIEVING_HOURS_OF_OPERATION_BEGIN("Receiving hours of operation begin", false, null),

    RECIEVING_HOURS_OF_OPERATION_END("Receiving hours of operation end", false, null),

    PICKUP_NOTES("Pickup Notes", false, UserAddressBookEntity.PICKUP_NOTES_LENGTH),

    DELIVERY_NOTES("Delivery Notes", false, UserAddressBookEntity.DELIVERY_NOTES_LENGTH),

    TYPE("Type (S, F, B)", false, 1),

    DEFAULT("Default", false, 3);

    private String header;
    private boolean required;
    private Integer maxLength;

    AddressFields(String header, boolean required, Integer maxLength) {
        this.header = header;
        this.required = required;
        this.maxLength = maxLength;
    }

    public String getHeader() {
        return this.header;
    }

    public boolean isRequired() {
        return this.required;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * Return appropriated {@link AddressFields} by header text.
     * 
     * @param header keyword which will be used to find {@link AddressFields}
     * @return instance of {@link AddressFields} if it was found, otherwise <tt>null</tt>
     * */
    public static AddressFields getAddressByHeader(String header) {
        AddressFields addressFields = null;

        String normalizedHeader = StringUtils.trimToNull(header);
        for (AddressFields item : AddressFields.values()) {
            if (StringUtils.equalsIgnoreCase(item.header, normalizedHeader)) {
                addressFields = item;
                break;
            }
        }

        return addressFields;
    }
}
