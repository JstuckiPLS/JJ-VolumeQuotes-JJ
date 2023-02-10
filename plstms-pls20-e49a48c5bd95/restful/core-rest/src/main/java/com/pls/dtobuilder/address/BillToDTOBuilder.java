package com.pls.dtobuilder.address;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.organization.BillToDefaultValuesEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillToRequiredFieldEntity;
import com.pls.core.domain.organization.BillToThresholdSettingsEntity;
import com.pls.core.domain.organization.BillingInvoiceNodeEntity;
import com.pls.core.domain.organization.CreditLimitEntity;
import com.pls.core.domain.organization.EdiSettingsEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.domain.organization.OpenBalanceEntity;
import com.pls.core.domain.organization.PlsCustomerTermsEntity;
import com.pls.core.domain.organization.UnbilledRevenueEntity;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.BillToDTO;
import com.pls.dto.address.InvoicePreferencesDTO;
import com.pls.dto.address.RequiredDocumentDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.EdiSettingsDTOBuilder;
import com.pls.dtobuilder.PhoneDTOBuilder;

/**
 * DTO builder provides functionality to map entities on DTO objects and vice-versa.
 * 
 * @author Andrey Cachur
 * 
 */
public class BillToDTOBuilder extends AbstractDTOBuilder<BillToEntity, BillToDTO> {

    private PhoneDTOBuilder phoneDTOBuilder = new PhoneDTOBuilder();
    private EdiSettingsDTOBuilder ediSettingsDTOBuilder = new EdiSettingsDTOBuilder();
    private BillToThresholdSettingsDTOBuilder billToThresholdSettingsDTOBuilder = new BillToThresholdSettingsDTOBuilder(
            new BillToThresholdSettingsDTOBuilder.DataProvider() {
                @Override
                public BillToThresholdSettingsEntity getThresholdSettings(Long id) {
                    if (dataProvider != null) {
                        return dataProvider.getThresholdSettings(id);
                    }
                    return null;
                }
            });

    private BillToDefaultValuesDTOBuilder billToDefaultValuesDTOBuilder = new BillToDefaultValuesDTOBuilder(
            new BillToDefaultValuesDTOBuilder.DataProvider() {
                @Override
                public BillToDefaultValuesEntity getDefaultValues(Long id) {
                    if (dataProvider != null) {
                        return dataProvider.getDefaultValues(id);
                    }
                    return null;
                }
            });

    private InvoicePreferencesDTOBuilder invoicePreferencesDTOBuilder = new InvoicePreferencesDTOBuilder(
            new InvoicePreferencesDTOBuilder.DataProvider() {
        @Override
        public InvoiceSettingsEntity getInvoiceSettings(Long id) {
            if (dataProvider != null) {
                return dataProvider.getInvoicePreferences(id);
            }
            return null;
        }
    });

    BillToRequiredFieldDTOBuilder billToRequiredFieldDTOBuilder = new BillToRequiredFieldDTOBuilder(new BillToRequiredFieldDTOBuilder.DataProvider() {
        @Override
        public BillToRequiredFieldEntity getRequiredFieldById(Long id) {
            if (dataProvider != null) {
                return dataProvider.getRequiredFieldById(id);
            }
            return null;
        }
    });
    private final RequiredDocumentDTOBuilder requiredDocumentDTOBuilder = new RequiredDocumentDTOBuilder();

    private DataProvider dataProvider;

    /**
     * Default constructor.
     */
    public BillToDTOBuilder() {
    }

    /**
     * Constructor.
     *
     * @param dataProvider {@link DataProvider}
     */
    public BillToDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public BillToDTO buildDTO(BillToEntity bo) {
        if (bo == null) {
            throw new IllegalArgumentException("Can't build dto from null!");
        }
        BillToDTO dto = new BillToDTO();
        dto.setId(bo.getId());
        dto.setAddress(getAddressDTO(bo));

        dto.setDefaultNode(bo.isDefaultNode());
        dto.setInvoicePreferences(getInvoicePreferenceDTO(bo));
        dto.setCurrency(bo.getCurrency());
        dto.setAuditPrefReq(bo.isAuditPrefReq() != null && bo.isAuditPrefReq());
        dto.setEmailAccountExecutive(bo.getEmailAccountExecutive());
        dto.setAuditInstructions(bo.getAuditInstructions());
        if (bo.getPlsCustomerTerms() != null) {
            dto.setPayTermsId(bo.getPlsCustomerTerms().getId());
        }
        dto.setEdiSettings(ediSettingsDTOBuilder.buildDTO(bo.getEdiSettings()));
        dto.setCreditLimit(getCreditLimit(bo.getCreditLimit()));
        dto.setUnpaidAmount(getUnpaidAmount(bo.getUnbilledRevenue()).add(getOpenBalance(bo.getOpenBalance())));
        dto.setAvailableAmount(bo.getAvailableCreditAmount());
        setAutoCreditHold(bo, dto);
        dto.setCreditHold(bo.getCreditHold());
        dto.setOverrideCreditHold(bo.getOverrideCreditHold());

        dto.setBillToRequiredFields(billToRequiredFieldDTOBuilder.buildList(bo.getBillToRequiredField()));

        if (bo.getOrganization() != null) {
            if (bo.getOrganization().getNetwork() != null) {
                dto.setNetworkAutoCreditHold(bo.getOrganization().getNetwork().getAutoCreditHold());
            }
            dto.setCustomerOverrideCreditHold(bo.getOrganization().getOverrideCreditHold());
            dto.setCustomerAutoCreditHold(bo.getOrganization().getAutoCreditHold());
        }
        dto.setBillToThresholdSettings(billToThresholdSettingsDTOBuilder.buildDTO(bo.getBillToThresholdSettings()));
        dto.setBillToDefaultValues(billToDefaultValuesDTOBuilder.buildDTO(bo.getBillToDefaultValues()));
        dto.setCreditCardEmail(bo.getCreditCardEmail());
        dto.setPaymentMethod(bo.getPaymentMethod());
        dto.setSendInvoicesReports(bo.isSendInvoicesReports());
        return dto;
    }

    private void setAutoCreditHold(BillToEntity bo, BillToDTO dto) {
        Boolean autoCreditHold = bo.getAutoCreditHold();
        if (bo.getOrganization() != null && !BooleanUtils.isTrue(bo.getOverrideCreditHold())) {
            if (BooleanUtils.isTrue(bo.getOrganization().getOverrideCreditHold())) {
                autoCreditHold = bo.getOrganization().getAutoCreditHold();
            } else if (bo.getOrganization().getNetwork() != null) {
                autoCreditHold = bo.getOrganization().getNetwork().getAutoCreditHold();
            }
        }
        dto.setAutoCreditHold(autoCreditHold);
    }

    @Override
    public BillToEntity buildEntity(BillToDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Can't build entity from null!");
        }

        BillToEntity billToEntity = getNewOrExistingBillTo(dto);
        if (dto.getAddress() != null) {
            billToEntity.setName(dto.getAddress().getAddressName());
        }
        billToEntity.setDefaultNode(dto.isDefaultNode());
        setBillingInvoiceNodeEntity(dto, billToEntity);
        setInvoiceSettingsEntity(dto, billToEntity);
        setEdiSettingsEntity(dto, billToEntity);
        billToEntity.setCurrency(dto.getCurrency());
        billToEntity.setAuditPrefReq(dto.isAuditPrefReq());
        billToEntity.setEmailAccountExecutive(dto.isEmailAccountExecutive());
        billToEntity.setAuditInstructions(dto.getAuditInstructions());
        billToEntity.setPlsCustomerTerms(getPayTermsEntity(dto.getPayTermsId()));
        if (Boolean.TRUE.equals(dto.getOverrideCreditHold())) {
            billToEntity.setAutoCreditHold(dto.getAutoCreditHold());
        }
        billToEntity.setCreditHold(dto.getCreditHold());
        billToEntity.setOverrideCreditHold(dto.getOverrideCreditHold());
        billToEntity.setAutoCreditHold(dto.getAutoCreditHold());
        setRequiredFieldsEntity(dto, billToEntity);

        if (dto.getBillToThresholdSettings() != null) {
            setBillToThresholdSettingsEntity(dto, billToEntity);
        }
        billToEntity.setBillToDefaultValues(billToDefaultValuesDTOBuilder.buildEntity(dto.getBillToDefaultValues()));
        billToEntity.getBillToDefaultValues().setBillTo(billToEntity);
        billToEntity.setCreditCardEmail(dto.getCreditCardEmail());
        billToEntity.setPaymentMethod(dto.getPaymentMethod());
        billToEntity.setSendInvoicesReports(dto.isSendInvoicesReports());
        return billToEntity;
    }

    private void setBillToThresholdSettingsEntity(BillToDTO dto, BillToEntity billToEntity) {
        BillToThresholdSettingsEntity billToThresholdSettingsEntity = null;
        if (billToEntity.getBillToThresholdSettings() != null && dto.getBillToThresholdSettings().getId() == null) {
            dto.getBillToThresholdSettings().setId(billToEntity.getBillToThresholdSettings().getId());
        }
        billToThresholdSettingsEntity = billToThresholdSettingsDTOBuilder.buildEntity(dto.getBillToThresholdSettings());
        billToThresholdSettingsEntity.setBillTo(billToEntity);

        billToEntity.setBillToThresholdSettings(billToThresholdSettingsEntity);
    }

    private void setEdiSettingsEntity(BillToDTO dto, BillToEntity billToEntity) {
        EdiSettingsEntity ediSettingsEntity = billToEntity.getEdiSettings();
        if (ediSettingsEntity != null && ediSettingsEntity.getId() != null) {
            ediSettingsEntity.setEdiStatus(dto.getEdiSettings().getEdiStatus());
            ediSettingsEntity.setEdiType(dto.getEdiSettings().getEdiType());
            ediSettingsEntity.setIsUniqueRefAndBol(dto.getEdiSettings().isBolUnique());
        } else {
            ediSettingsEntity = ediSettingsDTOBuilder.buildEntity(dto.getEdiSettings());
            ediSettingsEntity.setBillTo(billToEntity);
            billToEntity.setEdiSettings(ediSettingsEntity);
        }
    }

    private void setRequiredFieldsEntity(BillToDTO dto, BillToEntity billToEntity) {
        billToEntity.getBillToRequiredField().clear();
        List<BillToRequiredFieldEntity> entities = billToRequiredFieldDTOBuilder.buildEntityList(dto.getBillToRequiredFields());
        entities.forEach((item) -> item.setBillTo(billToEntity));
        billToEntity.getBillToRequiredField().addAll(entities);
    }

    private PlsCustomerTermsEntity getPayTermsEntity(Long payTermsId) {
        if (payTermsId != null) {
            PlsCustomerTermsEntity payTerms = new PlsCustomerTermsEntity();
            payTerms.setId(payTermsId);
            return payTerms;
        }
        return null;
    }

    private BillToEntity getNewOrExistingBillTo(BillToDTO dto) {
        BillToEntity billToEntity = null;
        if (dto.getId() != null && dataProvider != null) {
            billToEntity = dataProvider.getEntity(dto.getId());
        }
        if (billToEntity == null) {
            billToEntity = new BillToEntity();
            billToEntity.setId(dto.getId());
        }
        return billToEntity;
    }

    private InvoicePreferencesDTO getInvoicePreferenceDTO(BillToEntity bo) {
        InvoicePreferencesDTO invoicePreferences;
        if (bo.getInvoiceSettings() != null) {
            invoicePreferences = invoicePreferencesDTOBuilder.buildDTO(bo.getInvoiceSettings());
        } else {
            invoicePreferences = new InvoicePreferencesDTO();
        }

        if (dataProvider != null) {
            List<RequiredDocumentEntity> requiredDocumentsOfShipmentTypes = dataProvider.getRequiredDocuments(bo.getId());
            List<RequiredDocumentDTO> requiredDocumentsDTOs = requiredDocumentDTOBuilder.buildList(requiredDocumentsOfShipmentTypes);
            invoicePreferences.setRequiredDocuments(requiredDocumentsDTOs);
        }
        return invoicePreferences;
    }

    private AddressBookEntryDTO getAddressDTO(BillToEntity bo) {
        BillingInvoiceNodeEntity billingInvoiceNode = bo.getBillingInvoiceNode();
        AddressEntity addr = billingInvoiceNode.getAddress();
        if (addr != null) {
            final AddressBookEntryDTO addressDTO = new AddressBookEntryDTO();
            addressDTO.setAddressName(bo.getName());
            new AddressBookEntryDTOBuilder(new AddressBookEntryDTOBuilder.DataProvider() {
                @Override
                public AddressEntity getAddress() {
                    return null;
                }

                @Override
                public AddressBookEntryDTO getDto() {
                    return addressDTO;
                }
            }).buildDTO(addr);
            addressDTO.setId(addr.getId());
            addressDTO.setEmail(billingInvoiceNode.getEmail());
            addressDTO.setPhone(phoneDTOBuilder.buildDTO(billingInvoiceNode.getPhone()));
            if (billingInvoiceNode.getFax() != null
                    && StringUtils.isNotBlank(billingInvoiceNode.getFax().getExtension())) {
                billingInvoiceNode.getFax().setExtension(null);
            }
            addressDTO.setFax(phoneDTOBuilder.buildDTO(billingInvoiceNode.getFax()));
            addressDTO.setContactName(billingInvoiceNode.getContactName());
            return addressDTO;
        }
        return null;
    }

    private void setInvoiceSettingsEntity(BillToDTO dto, BillToEntity billToEntity) {
        InvoiceSettingsEntity invoiceSettings = null;
        if (dto.getInvoicePreferences() != null) {
            invoiceSettings = invoicePreferencesDTOBuilder.buildEntity(dto.getInvoicePreferences());
            invoiceSettings.setBillTo(billToEntity);
        }
        billToEntity.setInvoiceSettings(invoiceSettings);
    }

    private void setBillingInvoiceNodeEntity(BillToDTO dto, BillToEntity billTo) {
        BillingInvoiceNodeEntity billingNode = billTo.getBillingInvoiceNode();
        if (billingNode == null) {
            billingNode = new BillingInvoiceNodeEntity();
            billingNode.setBillTo(billTo);
            billTo.setBillingInvoiceNode(billingNode);
        }

        AddressBookEntryDTO addressBookDTO = dto.getAddress();
        if (addressBookDTO != null) {
            billingNode.setAddress(getAddressEntity(billingNode, addressBookDTO));
            billingNode.setEmail(addressBookDTO.getEmail());
            billingNode.setContactName(addressBookDTO.getContactName());
            billingNode.setPhone(phoneDTOBuilder.buildEntity(addressBookDTO.getPhone()));
            billingNode.setFax(getFaxEntity(addressBookDTO.getFax()));
        }
    }

    private PhoneEntity getFaxEntity(PhoneBO faxBO) {
        PhoneEntity fax = phoneDTOBuilder.buildEntity(faxBO);
        if (fax != null) {
            fax.setType(PhoneType.FAX);
        }
        return fax;
    }

    private AddressEntity getNewOrExistingAddressEntity(BillingInvoiceNodeEntity billingNode, AddressBookEntryDTO addressBookDTO) {
        AddressEntity addressEntity = billingNode.getAddress();
        if (addressEntity == null) {
            addressEntity = new AddressEntity();
            addressEntity.setId(addressBookDTO.getId());
        }
        return addressEntity;
    }

    private AddressEntity getAddressEntity(BillingInvoiceNodeEntity billingNode, AddressBookEntryDTO addressBookDTO) {
        final AddressEntity address = getNewOrExistingAddressEntity(billingNode, addressBookDTO);
        return new AddressBookEntryDTOBuilder(new AddressBookEntryDTOBuilder.DataProvider() {
            @Override
            public AddressEntity getAddress() {
                return address;
            }

            @Override
            public AddressBookEntryDTO getDto() {
                return null;
            }
        }).buildEntity(addressBookDTO);
    }

    private BigDecimal getCreditLimit(CreditLimitEntity entity) {
        BigDecimal limit = BigDecimal.ZERO;
        if (entity != null) {
            limit = ObjectUtils.defaultIfNull(entity.getCreditLimit(), BigDecimal.ZERO);
        }

        return limit;
    }

    private BigDecimal getUnpaidAmount(UnbilledRevenueEntity entity) {
        BigDecimal amount = BigDecimal.ZERO;
        if (entity != null) {
            amount = ObjectUtils.defaultIfNull(entity.getUnbilledRevenue(), BigDecimal.ZERO);
        }

        return amount;
    }

    private BigDecimal getOpenBalance(OpenBalanceEntity entity) {
        BigDecimal balance = BigDecimal.ZERO;
        if (entity != null) {
            balance = ObjectUtils.defaultIfNull(entity.getBalance(), BigDecimal.ZERO);
        }

        return balance;
    }

    /**
     * Provider of existing Bill To entity.
     */
    public interface DataProvider {
        /**
         * Get bill to entity by id.
         *
         * @param id id of entity
         * @return found bill to entity
         */
        BillToEntity getEntity(Long id);

        /**
         * Get invoice preferences by id.
         *
         * @param id id of entity
         * @return found Invoice preferences entity
         */
        InvoiceSettingsEntity getInvoicePreferences(Long id);

        /**
         * Get required documents list by bill to id.
         *
         * @param billToId bill to id
         * @return list of {@link RequiredDocumentEntity}
         */
        List<RequiredDocumentEntity> getRequiredDocuments(Long billToId);

        /**
         * Get billTo threshold settings by id.
         *
         * @param id
         *            id of BillToThresholdSettingsEntity
         * @return list of {@link BillToThresholdSettingsEntity}
         */
        BillToThresholdSettingsEntity getThresholdSettings(Long id);

        /**
         * Get billTo default values by id.
         *
         * @param id
         *            id of BillToDefaultValuesEntity
         * @return list of {@link BillToDefaultValuesEntity}
         */
        BillToDefaultValuesEntity getDefaultValues(Long id);

        /**
         * Gets the required field by id.
         *
         * @param id the id
         * @return the required field by id
         */
        BillToRequiredFieldEntity getRequiredFieldById(Long id);
    }
}
