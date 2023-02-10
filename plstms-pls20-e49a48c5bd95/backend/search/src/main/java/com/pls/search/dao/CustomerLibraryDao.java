package com.pls.search.dao;

import java.util.List;

import com.pls.search.domain.co.GetCustomerCO;
import com.pls.search.domain.vo.CustomerLibraryVO;

/**
 * Data Access Object for Customer Library.
 * 
 * @author Artem Arapov
 * 
 */
public interface CustomerLibraryDao {

    /**
     * Find list of {@link CustomerLibraryVO} by specified criteria object.
     * 
     * @param co
     *            Not <code>null</code> instance of {@link GetCustomerCO}.
     * @return {@link List} of {@link CustomerLibraryVO}.
     */
    List<CustomerLibraryVO> findCustomerLibraryByCO(GetCustomerCO co);
}
