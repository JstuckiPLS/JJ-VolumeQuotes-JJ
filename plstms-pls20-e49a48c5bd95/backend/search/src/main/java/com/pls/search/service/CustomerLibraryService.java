package com.pls.search.service;

import java.util.List;

import com.pls.core.exception.file.ExportException;
import com.pls.search.domain.co.GetCustomerCO;
import com.pls.search.domain.vo.CustomerLibraryVO;

/**
 * Service that handle business logic and for Customer Library.
 * 
 * @author Artem Arapov
 * 
 */
public interface CustomerLibraryService {

    /**
     * Get List of {@link CustomerLibraryVO} by specified criteria object.
     * 
     * @param co
     *            - Not <code>null</code> instance of {@link GetCustomerCO}.
     * @return {@link List} of {@link CustomerLibraryVO}.
     */
    List<CustomerLibraryVO> getCutomersBySelectedCriteria(GetCustomerCO co);

    /**
     * Export results prepared by specified criteria object.
     * 
     * @param co
     *            - Not <code>null</code> instance of {@link GetCustomerCO}.
     * @return byte[]
     * @throws ExportException
     *             exception.
     */
    byte[] exportCustomerList(GetCustomerCO co) throws ExportException;
}
