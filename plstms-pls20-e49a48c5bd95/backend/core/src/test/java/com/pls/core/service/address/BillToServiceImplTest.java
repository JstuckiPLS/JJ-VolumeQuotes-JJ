package com.pls.core.service.address;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.BillToDao;
import com.pls.core.service.address.impl.BillToServiceImpl;

/**
 * Unit test class for respective service.
 * @author Dmitriy Nefedchenko
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BillToServiceImplTest {
    @InjectMocks private BillToServiceImpl testee;

    @Mock private BillToDao dao;

    @Test
    public void testGetBillTo() {
        Long id = 1L;

        testee.getBillTo(id);
        verify(dao).find(id);
    }

    @Test
    public void testValidateNameDuplication() {
        String nameToBeValidated = "name";

        testee.validateDuplicateName(nameToBeValidated, 1L);
        verify(dao).validateDuplicateName(nameToBeValidated, 1L);
    }
}
