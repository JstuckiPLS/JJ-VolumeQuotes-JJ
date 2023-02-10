package com.pls.search.dao.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.search.dao.CustomerLibraryDao;
import com.pls.search.domain.co.GetCustomerCO;
import com.pls.search.domain.vo.CustomerLibraryVO;

/**
 * Test cases for {@link CustomerLibraryDaoImpl}.
 * 
 * @author Artem Arapov
 * 
 */
public class CustomerLibraryDaoImplIT extends AbstractDaoTest {

    @BeforeClass
    public static void beforeClass() {
        SecurityTestUtils.login("sysadmin", "ROLE_PLS_USER");
    }

    @Autowired
    private CustomerLibraryDao sut;

    @Test
    public void testFindCustomerLibraryByCO() throws Exception {
        GetCustomerCO co = createCriteriaObject();
        List<CustomerLibraryVO> result = sut.findCustomerLibraryByCO(co);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    private GetCustomerCO createCriteriaObject() throws Exception {
        GetCustomerCO co = new GetCustomerCO();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date fromDate = dateFormat.parse("2001-01-01");
        Date toDate = dateFormat.parse("2013-01-01");
        Date lastLoadFromDate = dateFormat.parse("2001-01-01");
        Date lastLoadToDate = dateFormat.parse("2013-01-01");

        co.setFromDate(fromDate);
        co.setToDate(toDate);
        co.setFromLoadDate(lastLoadFromDate);
        co.setToLoadDate(lastLoadToDate);
        co.setStatus(Status.ACTIVE);
        co.setPersonId(1L);

        return co;
    }
}
