package com.pls.search.service.impl.file;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.service.file.ExcelDocumentWriter;
import com.pls.search.domain.vo.CustomerLibraryVO;


/**
 * Write results of searching customers to byte array as a excel document.
 * 
 * @author Artem Arapov
 * 
 */
public class CustomerLibraryDocumentWriter extends ExcelDocumentWriter<CustomerLibraryVO> {

    private SimpleDateFormat formatter = new SimpleDateFormat(DateUtility.POSITIONAL_DATE, Locale.getDefault());

    @Override
    public String[] getHeaders() {
        return new String[] { "Customer", "Account Exec", "Added", "Last Load" };
    }

    @Override
    public String[] getRowFromEntity(CustomerLibraryVO entity) {

        return new String[] {
                entity.getCustomerName(),
                entity.getAccountExecName(),
                formatter.format(entity.getCreatedDate()),
                formatter.format(entity.getLastLoadDate())
        };
    }

}
