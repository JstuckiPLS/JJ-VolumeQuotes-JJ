package com.pls.restful.pricing;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
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

import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.file.ExportException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportRecordsNumberExceededException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.validation.ValidationException;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.dto.ImportResultDTO;
import com.pls.dto.P44ConfigDTO;
import com.pls.dtobuilder.pricing.P44ConfigDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingBlockedCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.LtlPricingTypesEntity;
import com.pls.ltlrating.integration.ltllifecycle.dto.p44.P44AccountGroupDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.p44.P44AccountGroupMappingDTO;
import com.pls.ltlrating.integration.ltllifecycle.service.LTLLifecycleRestClient;
import com.pls.ltlrating.service.LtlProfileDetailsService;
import com.pls.ltlrating.service.PriceImportExportService;
import com.pls.ltlrating.shared.GetRatesCO;
import com.pls.ltlrating.shared.LtlCopyProfileVO;
import com.pls.ltlrating.shared.LtlCustomerPricingVO;
import com.pls.ltlrating.shared.LtlPricingProfileLookupValuesVO;
import com.pls.ltlrating.shared.LtlPricingProfileVO;
import com.pls.restful.util.ImportUtil;

/**
 * Resource for Profile Details.
 * 
 * @author Aleksandr Leshchenko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/profile")
public class LtlProfileDetailsResource {
    private static final Logger LOG = LoggerFactory.getLogger(LtlProfileDetailsResource.class);
    public static final String IMPORT_PRICES_EXCEPTION_MSG = "Import Prices exception";

    @Autowired
    private LtlProfileDetailsService profileDetailsService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private PriceImportExportService priceImportExportService;
    
    @Autowired
    private LTLLifecycleRestClient ltlLcClient;
    
    @Autowired
    private P44ConfigDTOBuilder p44ConfigBuilder;

    @Qualifier("migrationTaskExecutor")
    @Autowired
    private AsyncTaskExecutor taskExecutor;

    /**
     * Returns list of affected customers for the selected pricing profile.
     * 
     * @param id
     *            - actual profile id
     * @return list of blocked customers
     */
    @RequestMapping(value = "/{id}/affectedCustomers", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlPricingBlockedCustomersEntity> getAffectedCustomersByProfileId(
            @PathVariable("id") Long id) {
        return profileDetailsService.getExplicitlyBlockedCustomersByProfileId(id);
    }

    /**
     * Returns list of applicable customers for the selected pricing profile.
     * 
     * @param id
     *            - actual profile id
     * @return list of applicable customers
     */
    @RequestMapping(value = "/{id}/applicableCustomers", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlPricingApplicableCustomersEntity> getApplicableCustomersByProfileId(
            @PathVariable("id") Long id) {
        return profileDetailsService.getApplicableCustomersByProfileId(id);
    }

    /**
     * Returns Customer names by SMC3 tariff name.
     * 
     * @param tariffName - SMC3 tariff name.
     * @return list of Customer names.
     */
    @RequestMapping(value = "/applicableCustomersForSMC3", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getApplicableCustomersBySMC3TariffName(@RequestParam("tariffName") String tariffName) {
        return profileDetailsService.getApplicableCustomersBySMC3TariffName(tariffName);
    }

    /**
     * Returns carrier pricing types.
     * 
     * @return List of LtlPricingTypesEntities
     */
    @RequestMapping(value = "/carrierPricingTypes", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlPricingTypesEntity> getCarrierPricingTypes() {
        return profileDetailsService.getPricingTypesByGroup("CARRIER");
    }

    /**
     * Returns list of carrier pricing profiles for the given customer.
     * 
     * @param id
     *            - Org Id of the customer
     * @return customerPricing - CustomerPricing VO that contains information about org changes for pricing
     *         and List of customer profiles that need to be updated
     */
    @RequestMapping(value = "/{id}/custPricProfiles", method = RequestMethod.GET)
    @ResponseBody
    public LtlCustomerPricingVO getCustomerPricingProfiles(@PathVariable("id") Long id) {
        return profileDetailsService.getCustomerPricingProfiles(id);
    }

    /**
     * Returns default lookup values for Pricing Profile screen.
     * 
     * @return list of {@link LtlPricingProfileVO} items
     * @throws Exception
     *             - exception thrown when reading the XML text.
     */
    @RequestMapping(value = "/defaultDictionary", method = RequestMethod.GET)
    @ResponseBody
    public LtlPricingProfileLookupValuesVO getDefaultLookupValues() throws Exception {
        return profileDetailsService.getDefaultLookupValues();
    }

    /**
     * Returns list of pricing profiles for the given criteria.
     * 
     * @param criteria
     *            - GetRatesCO -> pricing profile filter
     * @return list of {@link LtlPricingProfileVO} items
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public List<LtlPricingProfileVO> getPricingProfileBySelectedCriteria(@RequestBody GetRatesCO criteria) {
        return profileDetailsService.getTariffsBySelectedCriteria(criteria);
    }

    /**
     * Returns profile by customer id - org Id.
     * 
     * @param orgId
     *            - customer Org Id
     * @return instance of {@link LtlPricingProfileEntity} item
     */
    @RequestMapping(value = "/{orgId}/getCustomerMarginProfile", method = RequestMethod.GET)
    @ResponseBody
    public LtlPricingProfileEntity getCustomerMarginProfile(@PathVariable("orgId") Long orgId) {
        return profileDetailsService.getCustomerMarginProfileByOrgId(orgId);
    }

    /**
     * Returns profile by id.
     * 
     * @param id
     *            - actual profile id
     * @return instance of {@link LtlPricingProfileEntity} item
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public LtlPricingProfileEntity getProfileById(@PathVariable("id") Long id) {
        return profileDetailsService.getProfileById(id);
    }

    /**
     * Returns default lookup values for Pricing Profile screen.
     * 
     * @param id
     *            - profile id
     * @return list of {@link LtlPricingProfileVO} items
     */
    @RequestMapping(value = "/{id}/profileDictionaryInfo", method = RequestMethod.GET)
    @ResponseBody
    public LtlPricingProfileLookupValuesVO getProfileLookupValues(@PathVariable("id") Long id) {
        return profileDetailsService.getLookupValues(id);
    }

    /**
     * Returns list of pricing profiles for the given criteria.
     * 
     * @param criteria
     *            - GetRatesCO -> pricing profile copy from filter
     * @return list of {@link LtlCopyProfileVO} items
     * @throws EntityNotFoundException
     *             when entity is not found.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copylist", method = RequestMethod.POST)
    @ResponseBody
    public Set<LtlCopyProfileVO> getProfilesToCopy(@RequestBody GetRatesCO criteria) throws EntityNotFoundException {
        return profileDetailsService.getProfilesToCopy(criteria);
    }

    /**
     * Get a copy of the profile to display on the screen so that user can make changes and save the same.
     * 
     * @param copyFromProfileId
     *            - The profile id from which the profile should be copied
     * @return the copied Profile
     */
    @RequestMapping(value = "/{copyFromProfileId}/getUnsavedProfileCopy", method = RequestMethod.GET)
    @ResponseBody
    public LtlPricingProfileEntity getUnsavedProfileCopy(
            @PathVariable("copyFromProfileId") Long copyFromProfileId) {
        return profileDetailsService.getUnsavedProfileCopy(copyFromProfileId);
    }

    /**
     * To archive multiple pricing profiles. Return list of active or expired pricing details based on the
     * boolean flag "isActiveList". If flag is yes, the pricing profiles are picked from "Active" grid and so
     * need to return updated "Active" list else return updated "Expired" list method. In UI, "Edit",
     * "Copy From" and "Archive" fields should be disabled when user selects "Archived" tab.
     * 
     * @param criteria
     *            Criteria object that was used to search the Profiles.
     * @param ids
     *            List of profileIds that needs to be inactivated.
     * @param isActiveList
     *            Whether the list was picked from active list or inactive list or expired list.
     * @return a list of LtlPricingProfileVOs
     * @throws EntityNotFoundException
     *             when entity is not found.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/inactivate", method = RequestMethod.POST)
    @ResponseBody
    public List<LtlPricingProfileVO> inactivatePricingDetails(@RequestBody GetRatesCO criteria,
            @RequestParam("ids") List<Long> ids, @RequestParam("isActiveList") Boolean isActiveList)
            throws EntityNotFoundException {
        return profileDetailsService.inactivateProfiles(criteria, ids, isActiveList);
    }

    /**
     * To reactivate multiple pricing profiles. Return list of inactive pricing details. In UI, "Edit",
     * "Copy From" and "Archive" fields should be disabled when user selects "Archived" tab.
     * 
     * @param criteria
     *            Criteria object that was used to search the Profiles.
     * @param ids
     *            List of profileIds that needs to be reactivated.
     * @return a list of LtlPricingProfileVOs
     * @throws EntityNotFoundException
     *             when entity is not found.
     * @throws ValidationException
     *             - throw validation exception if any validation fails
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/reactivate", method = RequestMethod.POST)
    @ResponseBody
    public List<LtlPricingProfileVO> reactivatePricingDetails(@RequestBody GetRatesCO criteria,
            @RequestParam("ids") List<Long> ids) throws EntityNotFoundException, ValidationException {
        return profileDetailsService.reactivateProfiles(criteria, ids);
    }

    /**
     * This method is for saving the copied profile.
     * 
     * @param profile
     *            - the profile that was copied and needs to be saved
     * @return copied profile
     * @throws ValidationException
     *             - validates data and throws exception if not meeting the required validations.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/saveCopiedProfile", method = RequestMethod.POST)
    @ResponseBody
    public LtlPricingProfileEntity saveCopiedProfile(@RequestBody LtlPricingProfileEntity profile)
            throws ValidationException {
        return profileDetailsService.saveCopiedProfile(profile);
    }

    /**
     * This method is for updating the carrier profiles to override certain properties in profile.
     * 
     * @param customerPricing
     *            - CustomerPricing VO that contains information about org changes for pricing and List of
     *            customer profiles that need to be updated
     * @return customerPricing - CustomerPricing VO that contains information about org changes for pricing
     *         and List of customer profiles that need to be updated
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/saveCustPricProfiles", method = RequestMethod.POST)
    @ResponseBody
    public LtlCustomerPricingVO saveCustomerPricingProfile(@RequestBody LtlCustomerPricingVO customerPricing) {
        return profileDetailsService.saveProfilesForCustomer(customerPricing);
    }

    /**
     * This method is for both create and update operations. The Save operation returns the updated data
     * (success or roll back) along with other field values - primary key, date created, created by, date
     * modified, modified by, version and will use the same to populate the screen. This is required
     * especially for pessimistic locking.
     * 
     * @param profile
     *            - The LtlPricingProfileEntity that need to be saved
     * @return LtlPricingProfileEntity - Updated LtlPricingProfileEntity (With date created, created by and
     *         version values)
     * @throws ValidationException
     *             - validates data and throws exception if not meeting the required validations.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public LtlPricingProfileEntity saveProfile(@RequestBody LtlPricingProfileEntity profile) throws ValidationException {
        return profileDetailsService.saveProfile(profile);
    }

    /**
     * Import prices in async way. Returns job UUID.
     * @param upload file to import prices.
     * @return job UUID.
     * @throws IOException if some IO operations fail
     * @throws ImportException if validation or other error appear.
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @ResponseBody
    public String importPrices(@RequestParam("upload") final MultipartFile upload) throws IOException, ImportException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                                               Capabilities.CAN_IMPORT_PRICES.name());
        return priceImportExportService.importPricesAsync(upload.getInputStream(), upload.getOriginalFilename());
    }

    /**
     * Method that allows to check whether import prices job with specified UUID has been finished or not.
     * @param jobUUID - UUID of the import prices job
     *
     * @return UUID of the export prices job.
     * @throws ExportException if export prices job with specified UUID doesn't exist.
     */
    @RequestMapping(value = "/import/{jobUUID}/finished", method = RequestMethod.GET)
    @ResponseBody
    public boolean isImportPricesDone(@PathVariable("jobUUID") final String jobUUID) throws ExportException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                                               Capabilities.CAN_IMPORT_PRICES.name());
        return priceImportExportService.isImportPricesFinished(jobUUID);
    }

    /**
     * Method that returns export prices excel file for export prices job with specified UUID.
     * @param jobUUID - UUID of the xeport prices job
     *
     * @return UUID of the export prices job.
     * @throws ExportException if export prices job with specified UUID doesn't exist or hasn't been finished yet.
     */
    @RequestMapping(value = "/import/{jobUUID}/result", method = RequestMethod.GET)
    @ResponseBody
    public ImportResultDTO getImportResult(@PathVariable("jobUUID") final String jobUUID)
            throws ExportException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                                               Capabilities.CAN_IMPORT_PRICES.name());
        ImportResultDTO result = new ImportResultDTO();
        try {
            result = toImportResultDTO(priceImportExportService.getImportPricesResult(jobUUID));
        } catch (IOException e) {
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * Upload text file with prices to import. Prices import file should be uploaded,
     * validated and its items should be inserted/updated to the system. If some price insertion fails then text
     * document with that failed items would be generated.
     *
     * @param upload
     *            file data.
     * @return result of prices insertion.
     */
    @RequestMapping(value = "/import/sync", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @ResponseBody
    public ImportResultDTO importPricesSync(@RequestParam("upload") final MultipartFile upload) {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                Capabilities.CAN_IMPORT_PRICES.name());
        ImportResultDTO resultDTO = new ImportResultDTO();
        InputStream inputStream = null;
        try {
            inputStream = upload.getInputStream();
            resultDTO = toImportResultDTO(priceImportExportService.importPrices(inputStream, upload.getOriginalFilename()));
            resultDTO.setSuccess(true);
        } catch (ImportRecordsNumberExceededException e) {
            LOG.error(IMPORT_PRICES_EXCEPTION_MSG, e);
            resultDTO.setSuccess(false);
            resultDTO.setErrorType(ImportResultDTO.ImportErrorType.RECORDS_NUMBER_EXCEEDED);
        } catch (ImportFileInvalidDataException e) {
            LOG.error(IMPORT_PRICES_EXCEPTION_MSG, e);
            resultDTO.setSuccess(false);
            resultDTO.setErrorType(ImportResultDTO.ImportErrorType.EMPTY_FILE);
        } catch (ImportException | IOException e) {
            LOG.error(IMPORT_PRICES_EXCEPTION_MSG, e);
            resultDTO.setSuccess(false);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return resultDTO;
    }

    private ImportResultDTO toImportResultDTO(final ImportFileResults results) {
        ImportResultDTO resultDTO = new ImportResultDTO();
        resultDTO.setSucceedCount(results.getSuccesRowsCount());
        resultDTO.setFailedCount(results.getFaiedRowsCount());
        resultDTO.setFixNowDocId(results.getFailedDocumentId());
        resultDTO.setErrorMessageList(results.getErrorMessageList());
        resultDTO.setSuccess(true);
        return resultDTO;
    }

    /**
     * Returns the text document with data about price records which was failed to import during
     * importPrices() method processing.
     *
     * @param id
     *            id of document. Received in importPrices() method call result.
     * @return text data file or empty response if FixNowDow with specified id isn't found.
     * @throws EntityNotFoundException
     *             when document with given id not found.
     * @throws DocumentReadException if document cannot be read
     */
    @RequestMapping(value = "/import_fix_now_doc/{importFixNowDocId}", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity getImportFixNowDoc(@PathVariable("importFixNowDocId") final Long id)
            throws DocumentReadException, EntityNotFoundException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                Capabilities.CAN_IMPORT_PRICES.name());
        return ImportUtil.toFixDocumentEntity(priceImportExportService.getImportFailedDocument(id));
    }

    /**
     * Remove FixNowDow document with specified id.
     *
     * @param id
     *            id of document. Received in importPrices() method call result.
     * @throws EntityNotFoundException if fix now document wasn't found
     */
    @RequestMapping(value = "/import_fix_now_doc/{importFixNowDocId}", method = RequestMethod.DELETE)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public void removeImportFixNowDoc(@PathVariable("importFixNowDocId") Long id) throws EntityNotFoundException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                Capabilities.CAN_IMPORT_PRICES.name());
        priceImportExportService.removeImportFailedDocument(id);
    }

    /**
     * Method that starts export prices to excel file async job.
     *
     * @return UUID of the export prices job.
     * @throws ExportException if export prices job wasn't be able to start.
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String exportPrices() throws ExportException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                Capabilities.CAN_EXPORT_PRICES.name());
        return priceImportExportService.exportPrices();
    }

    /**
     * Method that allows to check whether export prices job with specified UUID has been finished or not.
     * @param jobUUID - UUID of the export prices job
     *
     * @return UUID of the export prices job.
     * @throws ExportException if export prices job with specified UUID doesn't exist.
     */
    @RequestMapping(value = "/export/{jobUUID}/finished", method = RequestMethod.GET)
    @ResponseBody
    public boolean isExportPricesDone(@PathVariable("jobUUID") final String jobUUID) throws ExportException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                Capabilities.CAN_EXPORT_PRICES.name());
        return priceImportExportService.isExportPricesFinished(jobUUID);
    }

    /**
     * Method that returns export prices excel file for export prices job with specified UUID.
     * @param jobUUID - UUID of the export prices job
     *
     * @return UUID of the export prices job.
     * @throws ExportException if export prices job with specified UUID doesn't exist or hasn't been finished yet.
     */
    @RequestMapping(value = "/export/{jobUUID}/result", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity getExportPricesFile(@PathVariable("jobUUID") final String jobUUID)
            throws ExportException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                Capabilities.CAN_EXPORT_PRICES.name());
        return priceImportExportService.getExportPricesFile(jobUUID);
    }

    /**
     * Method that perform export prices excel in sync way.
     *
     * @return {@link FileInputStreamResponseEntity} with the export prices.
     * @throws ExportException if export prices wasn't successfull.
     * @throws ExecutionException - if something goes wrong during task execution.
     * @throws InterruptedException - if something goes wrong during task execution.
     */
    @RequestMapping(value = "/export/sync", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity exportPricesFileSync()
            throws ExportException, ExecutionException, InterruptedException {
        userPermissionsService.checkCapability(Capabilities.PRICING_PAGE_VIEW.name(),
                                               Capabilities.CAN_EXPORT_PRICES.name());
        return taskExecutor.submit(() -> {
            String jobUUID = priceImportExportService.exportPrices();
            while (!priceImportExportService.isExportPricesFinished(jobUUID)) {
                Thread.sleep(100L);
            }
            return priceImportExportService.getExportPricesFile(jobUUID);
        }).get();
    }
    
    @RequestMapping(value = "/{id}/custP44Config", method = RequestMethod.GET)
    @ResponseBody
    public P44ConfigDTO getCustomerP44Config(@PathVariable("id") Long customerId) {
        P44AccountGroupMappingDTO accountGroupMapping = ltlLcClient.getAccountGroupMapping(customerId);
        return p44ConfigBuilder.buildP44ConfigDTO(customerId, accountGroupMapping);
    }
    
    @RequestMapping(value = "/{id}/custP44Config", method = RequestMethod.POST)
    @ResponseBody
    public void saveCustomerP44Config(@PathVariable("id") Long customerId, @RequestBody P44ConfigDTO p44Config) {
        P44AccountGroupMappingDTO accountGroupMapping = p44ConfigBuilder.buildP44AccountGroupMappingDTO(p44Config);
        ltlLcClient.saveAccountGroupMapping(customerId, accountGroupMapping);
    }
    
    @RequestMapping(value = "/P44AccountGroups", method = RequestMethod.GET)
    @ResponseBody
    public List<P44AccountGroupDTO> getP44AccountGroups() {
        return ltlLcClient.getAccountGroups();
    }
}
