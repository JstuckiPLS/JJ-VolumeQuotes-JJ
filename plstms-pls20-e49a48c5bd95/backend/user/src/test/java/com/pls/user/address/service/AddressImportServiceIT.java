package com.pls.user.address.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.ValidationException;

/**
 * Test cases for {@link AddressImportService}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class AddressImportServiceIT extends BaseServiceITClass {
    private static final long CUSTOMER_ID = 3L;

    private static final String USER_NAME = "userName" + Math.random();

    @Autowired
    private AddressImportService sut;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    AddressService addressService;

    @Test
    public void shouldImportAddressesFromValidFile() throws ValidationException, ImportException {
        SecurityTestUtils.login(USER_NAME);

        InputStream data = loadFile("1SheetValidData.xls");

        ImportFileResults result = sut.importAddresses(CUSTOMER_ID, SecurityTestUtils.DEFAULT_PERSON_ID, data, "xls");

        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        assertNotNull(result);
        assertEquals(3, result.getSuccesRowsCount());
        assertEquals(0, result.getFaiedRowsCount());

        List<UserAddressBookEntity> list = addressService.getCustomerAddressBookForUser(CUSTOMER_ID, SecurityTestUtils.DEFAULT_PERSON_ID, false,
                AddressType.values());
        assertEquals(3, list.size());
        int defaultCount = getDefaultCount(list);
        assertEquals(1, defaultCount);
    }

    private int getDefaultCount(List<UserAddressBookEntity> list) {
        int defaultCount = 0;
        for (UserAddressBookEntity entity : list) {
            if (entity.getIsDefault()) {
                defaultCount++;
            }
        }
        return defaultCount;
    }

    @Test
    public void shouldImportAddressesFromPartialValidFile() throws ValidationException, ImportException {
        SecurityTestUtils.login(USER_NAME);

        InputStream data = loadFile("1SheetValidInvalidData.xlsx");

        ImportFileResults result = sut.importAddresses(SecurityTestUtils.DEFAULT_ORGANIZATION_ID, SecurityTestUtils.DEFAULT_PERSON_ID, data, "xlsx");

        flushAndClearSession();

        assertNotNull(result);
        assertEquals(2, result.getSuccesRowsCount());
        assertEquals(2, result.getFaiedRowsCount());
        assertTrue(result.getFailedDocumentId() > 0);
    }

    @Test
    public void shouldImportAddressesInValidDataCountryCode() throws ValidationException, ImportException {
        SecurityTestUtils.login(USER_NAME);

        InputStream data = loadFile("1SheetInValidDataCountryCode.xls");

        ImportFileResults result = sut.importAddresses(SecurityTestUtils.DEFAULT_ORGANIZATION_ID,
                SecurityTestUtils.DEFAULT_PERSON_ID, data, "xls");

        flushAndClearSession();

        assertNotNull(result);
        assertEquals(1, result.getSuccesRowsCount());
        assertEquals(1, result.getFaiedRowsCount());
        assertTrue(result.getFailedDocumentId() > 0);
    }

    @Test
    public void shouldImportAddressesInValidDataStateCode() throws ValidationException, ImportException {
        SecurityTestUtils.login(USER_NAME);

        InputStream data = loadFile("1SheetInValidDataStateCode.xls");

        ImportFileResults result = sut.importAddresses(SecurityTestUtils.DEFAULT_ORGANIZATION_ID,
                SecurityTestUtils.DEFAULT_PERSON_ID, data, "xls");

        flushAndClearSession();

        assertNotNull(result);
        assertEquals(1, result.getSuccesRowsCount());
        assertEquals(1, result.getFaiedRowsCount());
        assertTrue(result.getFailedDocumentId() > 0);
    }

    private InputStream loadFile(String string) {
        InputStream result = ClassLoader.getSystemResourceAsStream("addressImport" + File.separator + string);
        assertNotNull(result);
        /*openedFiles.add(result);*/
        return result;
    }
}
