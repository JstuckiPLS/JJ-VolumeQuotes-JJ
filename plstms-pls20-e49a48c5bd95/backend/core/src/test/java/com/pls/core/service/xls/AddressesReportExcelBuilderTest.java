/**
 * 
 */
package com.pls.core.service.xls;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.service.file.FileInputStreamResponseEntity;

/**
 * Test cases for {@link AddressesReportExcelBuilder} class.
 * 
 * @author Alexander Nalapko
 *
 */
public class AddressesReportExcelBuilderTest {

    private static final int LINE_COUNT = 10;

    private ClassPathResource savingsReportResource = new ClassPathResource("/templates/Addresses_Export_Template.xlsx");

    AddressesReportExcelBuilder builder = null;

    @Test
    public void exportSavingsReport() throws ParseException, IOException {

        InputStream templateEmpty = savingsReportResource.getInputStream();
        Workbook workbookEmpty = new XSSFWorkbook(templateEmpty);
        Sheet mainSheetEmpty = workbookEmpty.getSheetAt(0);
        mainSheetEmpty.setDisplayGridlines(false);

        AddressesReportExcelBuilder builder = new AddressesReportExcelBuilder(savingsReportResource);
        List<UserAddressBookEntity> reports = new ArrayList<UserAddressBookEntity>();

        for (int i = 0; i < LINE_COUNT; i++) {
            reports.add(getUserAddressBookEntity());
        }

        FileInputStreamResponseEntity entity = builder.generateReport(reports);
        System.out.println(entity);

        InputStream template = entity.getBody().getInputStream();
        Workbook workbook = new XSSFWorkbook(template);
        Sheet mainSheet = workbook.getSheetAt(0);
        mainSheet.setDisplayGridlines(false);

        Assert.assertEquals(mainSheetEmpty.getLastRowNum() + LINE_COUNT, mainSheet.getLastRowNum());

        int addressNameColumnSize = mainSheet.getColumnWidth(0);
        int pickupNotesColumnSize = mainSheet.getColumnWidth(18);
        int deliveryNotesColumnSize = mainSheet.getColumnWidth(19);

        int addressNameValueLength = mainSheet.getRow(1).getCell(0).getStringCellValue().length();
        int pickupNotesValueLength = mainSheet.getRow(1).getCell(18).getStringCellValue().length();
        int deliveryNotesValueLength = mainSheet.getRow(1).getCell(19).getStringCellValue().length();

        Assert.assertTrue(addressNameColumnSize < pickupNotesColumnSize && addressNameColumnSize < deliveryNotesColumnSize);
        Assert.assertTrue(addressNameValueLength > pickupNotesValueLength && addressNameValueLength > deliveryNotesValueLength);

        workbookEmpty.close();
        workbook.close();
    }

    private UserAddressBookEntity getUserAddressBookEntity() {
        UserAddressBookEntity bo = new UserAddressBookEntity();
        bo.setAddressName("AddressName AddressName AddressName");
        bo.setAddressCode("Code");
        bo.setContactName("ContactName");

        AddressEntity address = new AddressEntity();
        address.setCountry(new CountryEntity());

        address.setAddress1("Address1");
        address.setAddress2("Address2");
        address.setCity("City");
        address.setStateCode("Code");
        address.setZip("Zip");
        bo.setAddress(address);

        bo.setPhone(new PhoneEntity());
        bo.setFax(new PhoneEntity());
        bo.setEmail("email");
        bo.setPersonId(1L);

        bo.setPickupFrom(new Time(0));
        bo.setPickupTo(new Time(0));
        bo.setDeliveryFrom(new Time(0));
        bo.setDeliveryTo(new Time(0));
        bo.setPickupNotes("PickupNotes DeliveryNotes");
        bo.setDeliveryNotes("DeliveryNotes DeliveryNotes");
        return bo;
    }
}
