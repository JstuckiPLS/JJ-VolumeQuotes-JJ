package com.pls.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.exception.file.ExportException;
import com.pls.search.dao.CustomerLibraryDao;
import com.pls.search.domain.co.GetCustomerCO;
import com.pls.search.domain.vo.CustomerLibraryVO;
import com.pls.search.service.CustomerLibraryService;
import com.pls.search.service.impl.file.CustomerLibraryDocumentWriter;

/**
 * {@link CustomerLibraryService} implementation.
 * 
 * @author Artem Arapov
 * 
 */

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CustomerLibraryServiceImpl implements CustomerLibraryService {

    @Autowired
    private CustomerLibraryDao dao;

    private CustomerLibraryDocumentWriter documentWriter = new CustomerLibraryDocumentWriter();

    @Override
    public List<CustomerLibraryVO> getCutomersBySelectedCriteria(GetCustomerCO co) {
        return dao.findCustomerLibraryByCO(co);
    }

    @Override
    public byte[] exportCustomerList(GetCustomerCO co) throws ExportException {
        List<CustomerLibraryVO> resultList = dao.findCustomerLibraryByCO(co);
        return documentWriter.createFileBody(resultList);
    }

}
