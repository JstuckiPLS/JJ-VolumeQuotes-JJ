package com.pls.dtobuilder.invoice;

import java.util.List;

import com.pls.core.domain.bo.CustomerCreditInfoBO;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.dto.invoice.CustomerCreditBillingDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.address.BillToDTOBuilder;

/**
 * Builder to convert {@link CustomerCreditInfoBO} into {@link CustomerCreditBillingDTO}.
 *
 * @author Mikhail Boldinov, 24/01/14
 */
public class CustomerCreditBillingDTOBuilder extends AbstractDTOBuilder<CustomerCreditInfoBO, CustomerCreditBillingDTO> {

    private final BillToDTOBuilder billToDTOBuilder = new BillToDTOBuilder();
    private final OrganizationPhoneDTOBuilder organizationPhoneDTOBuilder = new OrganizationPhoneDTOBuilder();

    private DataProvider dataProvider;

    /**
     * Constructor.
     *
     * @param dataProvider {@link DataProvider}
     */
    public CustomerCreditBillingDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public CustomerCreditBillingDTO buildDTO(CustomerCreditInfoBO bo) {
        CustomerCreditBillingDTO dto = new CustomerCreditBillingDTO();
        dto.setTaxId(bo.getTaxId());
        dto.setUnpaid(bo.getUnpaid());
        dto.setCreditLimit(bo.getCreditLimit());
        dto.setAvailable(bo.getAvailable());
        dto.setAccExecName(bo.getAccExecName());
        dto.setAccExecPhone(organizationPhoneDTOBuilder.buildDTO(bo.getAccExecPhone()));
        dto.setAccExecFax(organizationPhoneDTOBuilder.buildDTO(bo.getAccExecFax()));
        dto.setAccExecEmail(bo.getAccExecEmail());
        dto.setBillToList(billToDTOBuilder.buildList(dataProvider.getBillToList()));
        return dto;
    }

    @Override
    public CustomerCreditInfoBO buildEntity(CustomerCreditBillingDTO customerCreditBillingDTO) {
        throw new UnsupportedOperationException("Unsupported method");
    }

    /**
     * Credit and Billing data provider.
     */
    public interface DataProvider {

        /**
         * Gets customers BillTo list.
         *
         * @return list of {@link BillToEntity}
         */
        List<BillToEntity> getBillToList();
    }
}
