package com.pls.restful.address;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pls.core.common.MimeTypes;
import com.pls.core.domain.address.AddressNotificationsEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportRecordsNumberExceededException;
import com.pls.core.service.AddressNotificationsService;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.dto.ContactInfoSetDTO;
import com.pls.dto.FreightBillPayToDTO;
import com.pls.dto.ImportResultDTO;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.BillToDTO;
import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dtobuilder.AddressNotificationsDTOBuilder;
import com.pls.dtobuilder.ContactInfoSetDTOBuilder;
import com.pls.dtobuilder.address.AddressBookDTOBuilder;
import com.pls.dtobuilder.address.AddressBookDTOBuilder.DataProvider;
import com.pls.dtobuilder.address.AddressBookToFreightBillPayToDTOBuilder;
import com.pls.dtobuilder.address.BillToDTOBuilder;
import com.pls.dtobuilder.util.PickUpAndDeliveryDTOBuilder;
import com.pls.restful.util.ImportUtil;
import com.pls.user.address.service.AddressImportService;

/**
 * Rest service for Address Book.
 * 
 * @author Kachur Andrey
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/address")
public class AddressResource {
    private static final BillToDTOBuilder BILLTO_DTO_BUILDER = new BillToDTOBuilder();
    private static final ContactInfoSetDTOBuilder CONTACT_DTO_BUILDER = new ContactInfoSetDTOBuilder();
    private static final String IMPORT_FILE_EXTENSION_REGEXP = "^.*\\.(csv|xls|xlsx)$";
    private static final AddressBookToFreightBillPayToDTOBuilder FREIGHT_BILL_BUILDER = new AddressBookToFreightBillPayToDTOBuilder();

    private final AddressBookDTOBuilder addressBookDTOBuilder = new AddressBookDTOBuilder(new DataProvider() {
        @Override
        public UserAddressBookEntity getAddress(Long id) {
            return id == null ? null : addressService.getCustomerAddressById(id);
        }
    }, new AddressNotificationsDTOBuilder.DataProvider() {
        @Override
        public AddressNotificationsEntity getAddressNotification(Long id) {
            return addressNotificationsService.getAddressNotificationById(id);
        }
    });

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressImportService addressImportService;

    @Autowired
    private DBUtilityService dbUtilityService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private AddressNotificationsService addressNotificationsService;

    @Value("Address_Import_Template.xlsx")
    private ClassPathResource addressImportTemplateResource;

    /**
     * Delete single address item from LLT Address book by its id.
     * 
     * @param customerId
     *            id of customer
     * @param addressId
     *            address book item ID
     * @return true if address with such Id was found and successfully deleted, false otherwise.
     * @throws ApplicationException
     *             when invalid addressId or validation was failed.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{addressId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Boolean deleteAddressById(@PathVariable("customerId") Long customerId,
            @PathVariable("addressId") Long addressId) throws ApplicationException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.DELETE_ADDRESS.name());

        UserAddressBookEntity address = addressService.getCustomerAddressById(addressId);

        if (address != null && address.getPersonId() != null
                && !address.getModification().getCreatedBy().equals(SecurityUtils.getCurrentPersonId())) {
            throw new ApplicationException("You can't delete address created by different user with 'self' option");
        }
        return addressService.deleteAddressBookEntry(addressId, SecurityUtils.getCurrentPersonId());
    }

    /**
     * Find addresses by zip.
     * 
     * @param customerId
     *            id of customer
     * @param country
     *            code
     * @param zip
     *            code
     * @param city
     *            city
     * @param types
     *            type filter
     * @return list of addresses
     */
    @RequestMapping(value = "/list/by_zip", method = RequestMethod.GET)
    @ResponseBody
    public List<AddressBookEntryDTO> findAddressBookByZip(@PathVariable("customerId") Long customerId,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "zip", required = false) String zip,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "type", required = false) AddressType[] types) {
        userPermissionsService.checkOrganization(customerId);
        List<UserAddressBookEntity> addressEntityList = addressService.getCustomerAddressBookByCountryAndZip(country,
                zip, city, customerId, SecurityUtils.getCurrentPersonId(), types);
        return addressBookDTOBuilder.buildList(addressEntityList);
    }

    /**
     * Javadoc comment.
     * @param customerId customerId
     * @param query query
     * @return return
     */
    @RequestMapping(value = "/list/suggestions", method = RequestMethod.GET)
    @ResponseBody
    public List<AddressBookEntryDTO> listSuggestions(@PathVariable("customerId") Long customerId, @RequestParam("query") String query) {
        List<UserAddressBookEntity> suggestions = addressService.listSuggestions(customerId, query);
        return addressBookDTOBuilder.buildList(suggestions);
    }

    /**
     * Find contact information set.
     * 
     * @param customerId
     *            id of customer
     * @return contact information set
     */
    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    @ResponseBody
    public ContactInfoSetDTO findContactSetInfo(@PathVariable("customerId") Long customerId) {
        userPermissionsService.checkOrganization(customerId);
        //TODO: NEED TO CHECK THIS ON WHERE IT IS USED.
        return CONTACT_DTO_BUILDER.buildDTO(addressService.findContactInfoSet(SecurityUtils.getCurrentPersonId()));
    }

    /**
     * Returns one address item from Address book by its id or by address name and address code if address id is -1.
     * 
     * @param customerId
     *            id of customer
     * @param addressId
     *            address book item ID
     * @param addressName
     *            address name
     * @param addressCode
     *            address Code
     * @return address
     * @throws ApplicationException
     *             if address not found or validation was failed.
     */
    @RequestMapping(value = "/{addressId}", method = RequestMethod.GET)
    @ResponseBody
    public AddressBookEntryDTO findAddress(@PathVariable("customerId") Long customerId,
            @PathVariable("addressId") Long addressId,
                                           @RequestParam(value = "addressName", required = false) String addressName,
            @RequestParam(value = "addressCode", required = false) String addressCode) throws ApplicationException {
        userPermissionsService.checkOrganization(customerId);

        UserAddressBookEntity address = addressService.getCustomerAddressById(addressId);

        if (address != null && address.getPersonId() != null
                && !address.getPersonId().equals(SecurityUtils.getCurrentPersonId())) {
            throw new ApplicationException("You can't see address created by different user with 'self' option");
        }

        UserAddressBookEntity addressEntity;
        if (addressId == -1) {
            addressEntity = addressService.getCustomerAddressByNameAndCode(addressName, addressCode, customerId);
        } else {
            addressEntity = addressService.getCustomerAddressById(addressId);
        }

        return addressBookDTOBuilder.buildDTO(addressEntity);
    }

    /**
     * Returns list of addresses from LLT Address book.
     * 
     * @param customerId
     *            id of customer
     *
     * @return list of addresses from LLT Address book.
     */
    @RequestMapping(value = "/ltl_list", method = RequestMethod.GET)
    @ResponseBody
    public List<AddressBookEntryDTO> getAddressBookList(@PathVariable("customerId") Long customerId) {
        userPermissionsService.checkCapabilityAndOrganization(customerId,
                Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name(), Capabilities.DELETE_ADDRESS.name(),
                Capabilities.IMPORT_ADDRESS.name(), Capabilities.VIEW_ADDRESS_ONLY.name());
        List<UserAddressBookEntity> addressEntityList = addressService.getCustomerAddressBookForUser(customerId,
                SecurityUtils.getCurrentPersonId(), false, AddressType.values());

        return addressBookDTOBuilder.buildList(addressEntityList);
    }

    /**
     * Returns the text document with data about addresses records which was failed to import during
     * importAddresses() method processing.
     * 
     * @param customerId
     *            id of customer
     * @param id
     *            id of document. Received in importAddresses() method call result.
     * @return text data file or empty response if FixNowDow with specified id isn't found.
     * @throws EntityNotFoundException
     *             when document with given id not found.
     * @throws DocumentReadException if document cannot be read
     */
    @RequestMapping(value = "/import_fix_now_doc/{importFixNowDocId}", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity getImportFixNowDoc(@PathVariable("customerId") Long customerId,
            @PathVariable("importFixNowDocId") Long id) throws EntityNotFoundException, DocumentReadException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.IMPORT_ADDRESS.name());
        return ImportUtil.toFixDocumentEntity(addressImportService.getImportFailedDocument(id));
    }

    /**
     * Upload text file with Addresses to import into address book. Addresses import file should be uploaded,
     * validated and its items should be inserted to address book. If some addresses insertion fails then text
     * document with that failed items would be generated.
     * 
     * @param customerId
     *            id of customer
     * @param upload
     *            file data.
     * @return result of addresses insertion.
     * @throws JsonProcessingException
     *             if some issue occurs during marshalling object to string
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @ResponseBody
    public ImportResultDTO importAddresses(@PathVariable("customerId") Long customerId,
            @RequestParam("upload") MultipartFile upload) throws JsonProcessingException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.IMPORT_ADDRESS.name());

        Long personId = null;
        if (userPermissionsService.hasCapability(Capabilities.CAN_CREATE_ADDRESSES_WITH_SELF_OPTION.name())) {
            personId = SecurityUtils.getCurrentPersonId();
        }

        ImportResultDTO resultDTO = new ImportResultDTO();

        String fileName = upload.getOriginalFilename();
        log.info("Loading address book records from '{}' file...", fileName);
        if (!fileName.toLowerCase().matches(IMPORT_FILE_EXTENSION_REGEXP)) {
            log.info("File '{}' has unsupported extension", fileName);
            resultDTO.setSuccess(false);

            return resultDTO;
        }

        String extension = FilenameUtils.getExtension(fileName);
        InputStream inputStream = null;
        try {
            inputStream = upload.getInputStream();
            ImportFileResults result = addressImportService.importAddresses(customerId, personId, inputStream,
                    extension);

            resultDTO.setSucceedCount(result.getSuccesRowsCount());
            resultDTO.setFailedCount(result.getFaiedRowsCount());
            resultDTO.setFixNowDocId(result.getFailedDocumentId());
            resultDTO.setErrorMessageList(result.getErrorMessageList());

            resultDTO.setSuccess(true);
        } catch (ImportRecordsNumberExceededException e) {
            log.error("Import Addresses exception", e);
            resultDTO.setSuccess(false);
            resultDTO.setErrorType(ImportResultDTO.ImportErrorType.RECORDS_NUMBER_EXCEEDED);
        } catch (ImportFileInvalidDataException e) {
            log.error("Import Addresses exception", e);
            resultDTO.setSuccess(false);
            resultDTO.setErrorType(ImportResultDTO.ImportErrorType.EMPTY_FILE);
        } catch (ImportException | IOException e) {
            log.error("Import Addresses exception", e);
            resultDTO.setSuccess(false);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return resultDTO;
    }

    /**
     * Checks if address code exists in database.
     *
     * @param customerId
     *            id of customer
     * @param addressName
     *            address name
     * @param addressCode
     *            address Code
     * @return true if exists
     */
    @RequestMapping(value = "/unique", method = RequestMethod.GET)
    @ResponseBody
    public Boolean isAddressUnique(@PathVariable("customerId") Long customerId, @RequestParam("name") String addressName,
            @RequestParam("code") String addressCode) {
        userPermissionsService.checkCapabilityAndOrganization(customerId,
                Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name(), Capabilities.VIEW_ADDRESS_ONLY.name());
        return addressService.isAddressUnique(addressName, addressCode, customerId);
    }

    /**
     * Lists user BILL TO address book records by specified currency code.
     *
     * @param customerId
     *            id of customer
     * @param currency
     *            currency code (optional field, not needed for open shipments)
     * @return list of bill tos
     */
    @RequestMapping(value = "/billaddresses", method = RequestMethod.GET)
    @ResponseBody
    public List<BillToDTO> listUserBillToAddresses(@PathVariable("customerId") Long customerId,
            @RequestParam(value = "currency", required = false) Currency currency) {
        userPermissionsService.checkOrganization(customerId);
        return BILLTO_DTO_BUILDER.buildList(addressService.getOrgBillToAddresses(customerId, currency));
    }

    /**
     * Lists all user address book records.
     * 
     * @param customerId
     *            id of customer
     * @param types
     *            addresses types
     * @return list of address books
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<AddressBookEntryDTO> listUserContacts(@PathVariable("customerId") Long customerId,
            @RequestParam(value = "type", required = false) AddressType[] types) {
        userPermissionsService.checkCapabilityAndOrganization(customerId,
                Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name(), Capabilities.VIEW_ADDRESS_ONLY.name());

        List<UserAddressBookEntity> addressesList = addressService.getCustomerAddressBookForUser(customerId,
                SecurityUtils.getCurrentPersonId(), false, types);

        return addressBookDTOBuilder.buildList(addressesList);
    }

    /**
     * Remove FixNowDow document with specified id.
     * 
     * @param customerId
     *            id of customer
     * @param id
     *            id of document. Received in importAddresses() method call result.
     * @throws EntityNotFoundException
     *             if fix now document wasn't found
     */
    @RequestMapping(value = "/import_fix_now_doc/{importFixNowDocId}", method = RequestMethod.DELETE)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public void removeImportFixNowDoc(@PathVariable("customerId") Long customerId,
            @PathVariable("importFixNowDocId") Long id) throws EntityNotFoundException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.IMPORT_ADDRESS.name());

        addressImportService.removeImportFailedDocument(id);
    }

    /**
     * Saves address.
     * 
     * @param customerId
     *            id of customer
     * @param address
     *            to save
     * @return updated address
     * @throws ApplicationException
     *             when address permission checks fail
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public AddressBookEntryDTO saveAddress(@PathVariable("customerId") Long customerId,
            @RequestBody AddressBookEntryDTO address) throws ApplicationException {
        userPermissionsService.checkCapabilityAndOrganization(customerId,
                Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name());

        if ((!address.isSharedAddress() && !userPermissionsService
                .hasCapability(Capabilities.CAN_CREATE_ADDRESSES_WITH_SELF_OPTION.name()))) {
            throw new ApplicationException("PersonId " + SecurityUtils.getCurrentPersonId()
                    + " does not have privilegy to save address with 'self' option");
        }
        validatePickupAndDeliveryWindow(address.getDeliveryWindowFrom(), address.getDeliveryWindowTo(),
                address.getPickupWindowFrom(), address.getPickupWindowTo());
        dbUtilityService.startCommitMode();
        UserAddressBookEntity entity = addressBookDTOBuilder.buildEntity(address);

        if (!entity.getModification().getCreatedBy().equals(SecurityUtils.getCurrentPersonId()) && !address.isSharedAddress()) {
            throw new ApplicationException("You can't save address created by different user with 'self' option");
        }

        addressService.saveOrUpdate(entity, customerId, SecurityUtils.getCurrentPersonId(), true,
                !address.isSharedAddress());
        dbUtilityService.flushSession();
        return addressBookDTOBuilder.buildDTO(entity);
    }

    /**
     * Get Address Import Template.
     * 
     * @param customerId
     *            customer id
     * @param response
     *            servlet response
     * @throws IOException
     *             when can't write file to output stream
     */
    @RequestMapping(value = "/addressImportTemplate", method = { RequestMethod.GET, RequestMethod.HEAD })
    // RequestMethod.HEAD is required for IE when displaying document inline
    public void downloadAddressImportTemplate(@PathVariable("customerId") Long customerId, HttpServletResponse response)
            throws IOException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.IMPORT_ADDRESS.name());

        response.setContentType(MimeTypes.XLSX.getMimeString());
        response.setHeader("Content-Disposition", "attachment; filename=Address_Import_Template.xlsx");
        IOUtils.copy(addressImportTemplateResource.getInputStream(), response.getOutputStream());
    }

    /**
     * Export list of addresses for specified customer.
     *
     * @param customerId
     *            id of customer
     * @return Not <code>null</code> {@link List}.
     * @throws IOException
     *             i/o Exception
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity export(@PathVariable("customerId") Long customerId) throws IOException {
        userPermissionsService.checkOrganization(customerId);
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.EXPORT_ADDRESSES.name());

        return addressService.export(customerId, SecurityUtils.getCurrentPersonId());
    }

    /**
     * Get user address books for Freight Bill Pay To.
     * 
     * @param customerId
     *            id of customer
     * @param filter
     *            text to filter results.
     * @return list of freight bills
     */
    @RequestMapping(value = "/freightBill", method = RequestMethod.GET)
    @ResponseBody
    public List<FreightBillPayToDTO> getAddressBooksForFreightBill(@PathVariable("customerId") Long customerId,
            @RequestParam("filter") String filter) {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name(),
                Capabilities.VIEW_ADDRESS_ONLY.name());

        List<UserAddressBookEntity> addressesList = addressService.getAddressBooksForFreightBill(customerId, SecurityUtils.getCurrentPersonId(),
                filter);

        return FREIGHT_BILL_BUILDER.buildList(addressesList);
    }

    /**
     * Lists addresses.
     *
     * @param customerId
     *            id of customer
     * @return list of address books
     */
    @RequestMapping(value = "/defaultFreightBillPayTo", method = RequestMethod.GET)
    @ResponseBody
    public FreightBillPayToDTO getDefaultFreightBillPayTo(@PathVariable("customerId") Long customerId) {
        userPermissionsService.checkCapabilityAndOrganization(customerId,
                Capabilities.ADD_EDIT_ADDRESS_BOOK_PAGE.name(), Capabilities.VIEW_ADDRESS_ONLY.name());

        UserAddressBookEntity freightBillPayTo = addressService.getDefaultFreightBillPayToAddresses(customerId);
        if (freightBillPayTo != null) {
            return FREIGHT_BILL_BUILDER.buildDTO(freightBillPayTo);
        }
        return null;
    }

    private void validatePickupAndDeliveryWindow(PickupAndDeliveryWindowDTO... args) throws ApplicationException {
        PickUpAndDeliveryDTOBuilder pickUpAndDeliveryDtoBuilder = new PickUpAndDeliveryDTOBuilder();
        for (int i = 0; i < args.length; i++) {
            checkPickupAndDeliveryWindowOnMinutesAndHours(args[i]);
            checkPickupAndDeliveryWindowIfFieldsAreEmptyAndTimeIsCorrect(pickUpAndDeliveryDtoBuilder, i, args);
        }
    }

    private void checkPickupAndDeliveryWindowIfFieldsAreEmptyAndTimeIsCorrect(
            PickUpAndDeliveryDTOBuilder pickUpAndDeliveryDtoBuilder, int i, PickupAndDeliveryWindowDTO... args)
            throws ApplicationException {
        if ((i + 1) % 2 == 0) {

            if (args[i] == null ^ args[i - 1] == null) {
                throw new ApplicationException("You can't save address, you enter wrong Pickup or Delivery Window.");
            }

            Time fromTime = pickUpAndDeliveryDtoBuilder.buildEntity(args[i - 1]);
            Time toTime = pickUpAndDeliveryDtoBuilder.buildEntity(args[i]);

            if (args[i] != null && args[i - 1] != null && fromTime.after(toTime)) {
                throw new ApplicationException("You can't save address, you enter wrong Pickup or Delivery Window.");
            }
        }
    }

    private void checkPickupAndDeliveryWindowOnMinutesAndHours(PickupAndDeliveryWindowDTO args)
            throws ApplicationException {
        Integer hours;
        Integer minutes;
        if (args != null) {
            hours = args.getHours();
            minutes = args.getMinutes();
            if (hours != null && (hours < 0 || hours > 12)) {
                throw new ApplicationException("You can't save address, you enter wrong Pickup or Delivery Window.");
            }
            if ((minutes != null && !(minutes == 0 || minutes == 30))) {
                throw new ApplicationException("You can't save address, you enter wrong Pickup or Delivery Window.");
            }
        }
    }
}
