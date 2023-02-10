package com.pls.core.service.address.fileimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;

/**
 * Test cases for {@link com.pls.core.service.address.fileimport.DataParser}.
 *
 * @author Artem Arapov
 * */
public class DataParserTest {

    @Test
    public void testRowDataWithOptionalFields() throws Exception {
        UserAddressBookEntity expectedEntity = createValidEntityWithOptionalFields();

        Row header = createHeaderRow();
        Row row = createRowByValueOf(expectedEntity);

        DataParser parser = new DataParser();
        parser.getHeaderData().readData(header);
        UserAddressBookEntity actualEntity = parser.parseRow(row);

        checkAddressEntity(expectedEntity, actualEntity);
    }

    @Test
    public void testRowDataWithoutOptionalFields() throws Exception {
        UserAddressBookEntity expectedEntity = createValidEntityWithoutOptionalFields();

        Row header = createHeaderRow();
        Row row = createRowByValueOf(expectedEntity);

        DataParser parser = new DataParser();
        parser.getHeaderData().readData(header);
        UserAddressBookEntity actualEntity = parser.parseRow(row);

        checkAddressEntity(expectedEntity, actualEntity);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testRowDataWithoutMandatoryFields() throws Exception {
        UserAddressBookEntity expectedEntity = createEntityWithoutMandatoryFields();

        Row header = createHeaderRow();
        Row row = createRowByValueOf(expectedEntity);

        DataParser parser = new DataParser();
        parser.getHeaderData().readData(header);
        parser.parseRow(row);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testRowDataWithIncorrectPhoneCodes() throws Exception {
        UserAddressBookEntity expectedEntity = createEntityIncorrectPhoneCodes();

        Row header = createHeaderRow();
        Row row = createRowByValueOf(expectedEntity);

        DataParser parser = new DataParser();
        parser.getHeaderData().readData(header);
        parser.parseRow(row);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testRowDataWithIncorrectFaxCodes() throws Exception {
        UserAddressBookEntity expectedEntity = createEntityIncorrectFaxCodes();

        Row header = createHeaderRow();
        Row row = createRowByValueOf(expectedEntity);

        DataParser parser = new DataParser();
        parser.getHeaderData().readData(header);
        parser.parseRow(row);
    }

    private Row createRow() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Test");
        Row headerRow = sheet.createRow(0);
        return headerRow;
    }

    private Row createHeaderRow() {
        Row row = createRow();
        AddressFields[] fields = AddressFields.values();
        for (int indx = 0; indx < fields.length; indx++) {
            row.createCell(indx).setCellValue(fields[indx].getHeader());
        }

        return row;
    }

    private Row createRowByValueOf(UserAddressBookEntity entity) {
        Row row = createRow();

        int rowNumber = 0;
        row.createCell(rowNumber++, CellType.STRING).setCellValue(entity.getAddressName());
        row.createCell(rowNumber++).setCellValue(entity.getAddressCode());
        row.createCell(rowNumber++).setCellValue(entity.getContactName());

        AddressEntity address = entity.getAddress();

        if (address != null) {
            row.createCell(rowNumber++, CellType.NUMERIC).setCellValue(address.getCountry().getId());
            row.createCell(rowNumber++, CellType.STRING).setCellValue(address.getAddress1());
            row.createCell(rowNumber++, CellType.STRING).setCellValue(address.getAddress2());
            row.createCell(rowNumber++, CellType.STRING).setCellValue(address.getCity());
            row.createCell(rowNumber++, CellType.NUMERIC).setCellValue(address.getState().getStatePK().getStateCode());
            row.createCell(rowNumber++, CellType.NUMERIC).setCellValue(address.getZip());
        }

        PhoneEntity phone = entity.getPhone();

        if (phone != null) {
            row.createCell(rowNumber++, CellType.NUMERIC).setCellValue(preparePhone(phone));
        }
        row.createCell(rowNumber++, CellType.NUMERIC)
                .setCellValue(phone.getExtension() != null ? phone.getExtension() : "");

        PhoneEntity fax = entity.getFax();

        if (fax != null) {
            row.createCell(rowNumber++, CellType.NUMERIC).setCellValue(preparePhone(fax));
        }

        row.createCell(rowNumber++, CellType.STRING).setCellValue(entity.getEmail());

        if (entity.getPersonId() != null) {
            row.createCell(rowNumber, CellType.STRING).setCellValue("no");
        } else {
            row.createCell(rowNumber, CellType.STRING).setCellValue("yes");
        }

        return row;
    }

    private String preparePhone(PhoneEntity phone) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(phone.getCountryCode());
        stringBuilder.append("(");
        stringBuilder.append(phone.getAreaCode());
        stringBuilder.append(")");
        stringBuilder.append(phone.getNumber());
        return  stringBuilder.toString();
    }

    private UserAddressBookEntity createValidEntityWithOptionalFields() {
        UserAddressBookEntity entity = new UserAddressBookEntity();
        entity.setAddressName("Test Address");
        entity.setAddressCode("123");
        entity.setContactName("Contact name");

        AddressEntity address = new AddressEntity();
        address.setAddress1("Address1");
        address.setAddress2("Address2");

        CountryEntity country = new CountryEntity();
        country.setId("USA");
        address.setCountry(country);
        address.setCity("City");

        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setStateCode("222");
        state.setStatePK(statePK);
        address.setState(state);

        address.setZip("123456789");

        entity.setAddress(address);

        PhoneEntity phone = new PhoneEntity();
        phone.setCountryCode("1");
        phone.setAreaCode("222");
        phone.setNumber("1234567");
        entity.setPhone(phone);

        PhoneEntity fax = new PhoneEntity();
        fax.setCountryCode("001");
        fax.setAreaCode("222");
        fax.setNumber("7654321");
        entity.setFax(fax);

        entity.setEmail("a@a.com");

        return entity;
    }

    private UserAddressBookEntity createValidEntityWithoutOptionalFields() {
        UserAddressBookEntity entity = new UserAddressBookEntity();

        entity.setAddressName("Imported_Address_000001");
        entity.setAddressCode("LC_000001");
        entity.setContactName("Contact name");

        AddressEntity address = new AddressEntity();
        address.setAddress1("Address1");

        CountryEntity country = new CountryEntity();
        country.setId("111");
        address.setCountry(country);
        address.setCity("City");

        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setStateCode("222");
        state.setStatePK(statePK);
        address.setState(state);

        address.setZip("123456789");

        entity.setAddress(address);

        PhoneEntity phone = new PhoneEntity();
        phone.setCountryCode("001");
        phone.setAreaCode("222");
        phone.setNumber("1234567");
        entity.setPhone(phone);

        PhoneEntity fax = new PhoneEntity();
        fax.setCountryCode("001");
        fax.setAreaCode("222");
        fax.setNumber("7654321");
        entity.setFax(fax);

        entity.setEmail("a@a.com");

        return entity;
    }

    private UserAddressBookEntity createEntityWithoutMandatoryFields() {
        UserAddressBookEntity entity = new UserAddressBookEntity();

        entity.setContactName("");

        AddressEntity address = new AddressEntity();
        address.setAddress1("Address1");

        CountryEntity country = new CountryEntity();
        country.setId("111");
        address.setCountry(country);
        address.setCity("City");

        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setStateCode("222");
        state.setStatePK(statePK);
        address.setState(state);

        address.setZip("");

        entity.setAddress(address);

        PhoneEntity phone = new PhoneEntity();
        phone.setCountryCode("001");
        phone.setAreaCode("222");
        phone.setNumber("1234567");
        entity.setPhone(phone);

        PhoneEntity fax = new PhoneEntity();
        fax.setCountryCode("001");
        fax.setAreaCode("222");
        fax.setNumber("7654321");
        entity.setFax(fax);

        entity.setEmail("");

        return entity;
    }

    private UserAddressBookEntity createEntityIncorrectPhoneCodes() {
        UserAddressBookEntity entity = new UserAddressBookEntity();

        entity.setContactName("Contact name");

        AddressEntity address = new AddressEntity();
        address.setAddress1("Address1");

        CountryEntity country = new CountryEntity();
        country.setId("111");
        address.setCountry(country);
        address.setCity("City");

        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setStateCode("222");
        state.setStatePK(statePK);
        address.setState(state);

        address.setZip("123456789");

        entity.setAddress(address);

        PhoneEntity phone = new PhoneEntity();
        phone.setCountryCode("555");
        phone.setAreaCode("789");
        phone.setNumber("654125");
        entity.setPhone(phone);

        PhoneEntity fax = new PhoneEntity();
        fax.setCountryCode("111");
        fax.setAreaCode("222");
        fax.setNumber("7654321");
        entity.setFax(fax);

        entity.setEmail("a@a.com");

        return entity;
    }

    private UserAddressBookEntity createEntityIncorrectFaxCodes() {
        UserAddressBookEntity entity = new UserAddressBookEntity();

        entity.setContactName("Contact name");

        AddressEntity address = new AddressEntity();
        address.setAddress1("Address1");

        CountryEntity country = new CountryEntity();
        country.setId("111");
        address.setCountry(country);
        address.setCity("City");

        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setStateCode("222");
        state.setStatePK(statePK);
        address.setState(state);

        address.setZip("123456789");

        entity.setAddress(address);

        PhoneEntity phone = new PhoneEntity();
        phone.setCountryCode("001");
        phone.setAreaCode("222");
        phone.setNumber("1234567");
        entity.setPhone(phone);

        PhoneEntity fax = new PhoneEntity();
        fax.setCountryCode("545");
        fax.setAreaCode("222");
        fax.setNumber("765432154654");
        entity.setFax(fax);

        entity.setEmail("a@a.com");

        return entity;
    }

    private void checkAddressEntity(UserAddressBookEntity expectedEntity, UserAddressBookEntity actualEntity) {
        assertNotNull(actualEntity);
        assertEquals(expectedEntity.getAddressName(), actualEntity.getAddressName());
        assertEquals(expectedEntity.getAddressCode(), actualEntity.getAddressCode());
        assertEquals(expectedEntity.getContactName(), actualEntity.getContactName());
        assertEquals(expectedEntity.getAddress().getCountry().getId(), actualEntity.getAddress().getCountry().getId());
        assertEquals(expectedEntity.getAddress().getAddress1(), actualEntity.getAddress().getAddress1());
        assertEquals(expectedEntity.getAddress().getAddress2(), actualEntity.getAddress().getAddress2());
        assertEquals(expectedEntity.getAddress().getCity(), actualEntity.getAddress().getCity());
        assertEquals(expectedEntity.getAddress().getState().getStatePK().getStateCode(), actualEntity.getAddress().getState().getStatePK()
                .getStateCode());
        assertEquals(expectedEntity.getAddress().getZip(), actualEntity.getAddress().getZip());
        assertEquals(expectedEntity.getPhone().getAreaCode(), actualEntity.getPhone().getAreaCode());
        assertEquals(expectedEntity.getPhone().getCountryCode(), actualEntity.getPhone().getCountryCode());
        assertEquals(expectedEntity.getPhone().getNumber(), actualEntity.getPhone().getNumber());
        assertEquals(expectedEntity.getFax().getAreaCode(), actualEntity.getFax().getAreaCode());
        assertEquals(expectedEntity.getFax().getCountryCode(), actualEntity.getFax().getCountryCode());
        assertEquals(expectedEntity.getFax().getNumber(), actualEntity.getFax().getNumber());
        assertEquals(expectedEntity.getEmail(), actualEntity.getEmail());
    }
}
