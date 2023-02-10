package com.pls.restful.search;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.exception.file.ExportException;
import com.pls.core.service.file.BinaryFileResponse;
import com.pls.dto.search.CustomerLibraryQueryDTO;
import com.pls.dtobuilder.CustomerLibraryQueryDTOBuilder;
import com.pls.search.domain.co.GetCustomerCO;
import com.pls.search.domain.vo.CustomerLibraryVO;
import com.pls.search.service.CustomerLibraryService;

/**
 * Search Customer Resource.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/searchCustomers")
public class SearchCustomerResource {

    private CustomerLibraryQueryDTOBuilder builder = new CustomerLibraryQueryDTOBuilder();

    @Autowired
    private CustomerLibraryService service;

    /**
     * Export results prepared by specified criteria object.
     * 
     * @param dto
     *            - Not <code>null</code> instance of {@link CustomerLibraryQueryDTO}.
     * @return {@link ResponseEntity}.
     * @throws ExportException
     *             exception.
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> exportCustomerList(@RequestParam("query") CustomerLibraryQueryDTO dto)
            throws ExportException {
        GetCustomerCO co = builder.buildEntity(dto);

        byte[] documentBytes = service.exportCustomerList(co);

        SimpleDateFormat formatter = new SimpleDateFormat(DateUtility.POSITIONAL_DATE, Locale.getDefault());
        String stringDate = formatter.format(new Date());
        String fileName = "CUSTOMERS_" + stringDate + ".xlsx";

        return new BinaryFileResponse(documentBytes, fileName);
    }

    /**
     * Get List of {@link CustomerLibraryVO} by specified criteria object.
     * 
     * @param dto
     *            - Not <code>null</code> instance of {@link CustomerLibraryQueryDTO}.
     * @return {@link List} of {@link CustomerLibraryVO}.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public List<CustomerLibraryVO> getCustomersBySelectedCriteria(@RequestBody CustomerLibraryQueryDTO dto) {
        GetCustomerCO co = builder.buildEntity(dto);

        return service.getCutomersBySelectedCriteria(co);
    }
}
