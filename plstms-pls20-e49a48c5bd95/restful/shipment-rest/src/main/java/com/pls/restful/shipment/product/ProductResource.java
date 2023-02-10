package com.pls.restful.shipment.product;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pls.core.common.MimeTypes;
import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportRecordsNumberExceededException;
import com.pls.core.service.CustomerService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.ResponseVO;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.dto.ImportResultDTO;
import com.pls.dto.ProductDTO;
import com.pls.dto.ValueLabelDTO;
import com.pls.dto.enums.CommodityClassDTO;
import com.pls.dtobuilder.CommodityClassDTOBuilder;
import com.pls.dtobuilder.product.ProductDTOBuilder;
import com.pls.dtobuilder.product.ProductDTOBuilder.DataProvider;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.service.ProductService;
import com.pls.shipment.service.dictionary.PackageTypeDictionaryService;

/**
 * Rest service providing products info.
 * 
 * @author Gleb Zgonikov
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/product")
public class ProductResource {
    private static final String IMPORT_FILE_EXTENSION_REGEXP = "^.*\\.(csv|xls|xlsx)$";
    private static final Logger LOG = LoggerFactory.getLogger(ProductResource.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PackageTypeDictionaryService packageTypeDictionaryService;

    private final ProductDTOBuilder dtoBuilder = new ProductDTOBuilder(new DataProvider() {
        @Override
        public LtlProductEntity getProductById(Long id) {
            return productService.findById(id);
        }

        @Override
        public PackageTypeEntity findPackageType(String id) {
            return packageTypeDictionaryService.getById(id);
        }
    });
    @Autowired
    private ProductService productService;
    @Autowired
    private UserPermissionsService userPermissionsService;
    @Value("Product_Import_Template.xls")
    private ClassPathResource productImportTemplateResource;

    /**
     * Archive LTL product.
     *
     * @param customerId id of customer
     * @param productId  LTL Product ID.
     * @throws ApplicationException when invalid customer ID or validation was failed.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{productId}/archive", method = RequestMethod.PUT)
    @ResponseBody
    public void archive(@PathVariable("customerId") Long customerId,
                        @PathVariable("productId") Long productId) throws ApplicationException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.DELETE_PRODUCT.name());

        LtlProductEntity product = productService.findById(productId);

        if (product != null && product.getPersonId() != null && !product.getPersonId().equals(SecurityUtils.getCurrentPersonId())) {
            throw new ApplicationException("You can't delete product created by different user");
        }

        productService.archiveProduct(SecurityUtils.getCurrentPersonId(), productId);
    }

    /**
     * Find product by id.
     *
     * @param customerId id of customer
     * @param productId  id of product
     * @return product
     * @throws ApplicationException if product not found or validation was failed.
     */
    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public ProductDTO get(@PathVariable("customerId") Long customerId,
                          @PathVariable("productId") Long productId) throws ApplicationException {
        userPermissionsService.checkOrganization(customerId);

        LtlProductEntity product = productService.findById(productId);

        if (product != null && product.getPersonId() != null && !product.getPersonId().equals(SecurityUtils.getCurrentPersonId())) {
            throw new ApplicationException("You can't see product created by different user with 'self' option");
        }

        return dtoBuilder.buildDTO(product);
    }

    /**
     * Get products by criteria. Returns all relevant products if filter is not null or empty.<br>
     * List of latest products otherwise.
     *
     * @param customerId     id of customer
     * @param filter         the search filter
     * @param commodityClass actual product class
     * @param hazmat         'hazmatOnly' filter flag
     * @return list of ValueLabelDTO for products combobox sorted and formatted according to user settings
     */
    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    @ResponseBody
    public List<ValueLabelDTO> filter(@PathVariable("customerId") Long customerId,
                                         @RequestParam(value = "search", required = false) String filter,
                                         @RequestParam(value = "commodityClass", required = false) CommodityClassDTO commodityClass,
                                         @RequestParam(value = "hazmat", required = false) Boolean hazmat) {

        CommodityClassDTOBuilder commodityDtoBuilder = new CommodityClassDTOBuilder();
        CommodityClass commodityClassEntity = commodityDtoBuilder.buildEntity(commodityClass);

        userPermissionsService.checkOrganization(customerId);

        List<LtlProductEntity> productList = productService.findRecentProducts(customerId,
                SecurityUtils.getCurrentPersonId(), filter, commodityClassEntity, BooleanUtils.isTrue(hazmat));
        ProductListPrimarySort sort = customerService.getProductListPrimarySort(customerId);

        return buildProductsByPrimarySort(sort, productList);
    }

    /**
     * Returns the text document with data about product records which was failed to import during
     * importProducts() method processing.
     *
     * @param customerId id of customer
     * @param docId      id of document. Received in importProducts() method call result.
     * @return text data file or empty response if FixNowDow with specified id isn't found.
     * @throws ApplicationException
     *             when document by given id was not found or cannot be received from file system.
     */
    @RequestMapping(value = "/fixNowDoc/{docId}", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity getFixNowDoc(@PathVariable("customerId") Long customerId,
                                               @PathVariable("docId") Long docId) throws ApplicationException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.IMPORT_PRODUCT.name());

        final LoadDocumentEntity document = productService.getImportFailedDocument(docId);

        MimeTypes fileType = ObjectUtils.defaultIfNull(document.getFileType(), MimeTypes.XLSX);
        String extension = ObjectUtils.toString(FilenameUtils.getExtension(document.getDocFileName()), fileType.name());

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        String stringDate = formatter.format(new Date());
        final String fileName = String.format("Import_Incorrect_Data_%s.%s", stringDate, extension);

        return new FileInputStreamResponseEntity(document.getStreamContent(), document.getStreamLength(), fileName);
    }

    /**
     * Get list of products for specified customer.
     *
     * @param customerId id of customer
     * @return Not <code>null</code> {@link List}.
     * @throws EntityNotFoundException if customer with specified id is not found
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ProductDTO> get(@PathVariable("customerId") Long customerId) throws EntityNotFoundException {
        userPermissionsService.checkOrganization(customerId);
        return dtoBuilder.buildList(productService.findAllProducts(customerId, SecurityUtils.getCurrentPersonId()));
    }

    /**
     * Upload text file with Products to import into current customer product list. Products import file
     * should be uploaded, validated and its items should be inserted into DB. If some product insertion fails
     * then text document with that failed items would be generated.
     * <p/>
     * Returns string instead of json object due to issue with IE7.
     *
     * @param customerId id of customer
     * @param upload     upload file data.
     * @return result of products insertion.
     * @throws ValidationException     when validation checks fail.
     * @throws EntityNotFoundException if customer with specified id not found
     * @throws JsonProcessingException if some issue occurs during marshalling object to string
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/import", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ImportResultDTO importProducts(@PathVariable("customerId") Long customerId,
                                 @RequestParam("upload") MultipartFile upload)
            throws ValidationException, EntityNotFoundException, JsonProcessingException {

        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.IMPORT_PRODUCT.name());

        ImportResultDTO resultDTO = new ImportResultDTO();

        String fileName = upload.getOriginalFilename();
        LOG.info("Loading LTL Products from '{}' file...", fileName);
        if (!fileName.toLowerCase().matches(IMPORT_FILE_EXTENSION_REGEXP)) {
            LOG.info("File '{}' has unsupported extension", fileName);
            resultDTO.setSuccess(false);

            return resultDTO;
        }

        String extension = FilenameUtils.getExtension(fileName);
        InputStream inputStream = null;
        try {
            inputStream = upload.getInputStream();
            ImportFileResults result = productService.importProductsFromFile(inputStream, extension, customerId,
                    SecurityUtils.getCurrentPersonId());

            resultDTO.setSucceedCount(result.getSuccesRowsCount());
            resultDTO.setFailedCount(result.getFaiedRowsCount());
            resultDTO.setFixNowDocId(result.getFailedDocumentId());
            resultDTO.setErrorMessageList(result.getErrorMessageList());
            resultDTO.setSuccess(true);
        } catch (ImportRecordsNumberExceededException e) {
            LOG.error("Import products from file failed", e);
            resultDTO.setSuccess(false);
            resultDTO.setErrorType(ImportResultDTO.ImportErrorType.RECORDS_NUMBER_EXCEEDED);
        } catch (ImportException e) {
            LOG.error("Import products from file failed", e);
            resultDTO.setSuccess(false);
        } catch (IOException e) {
            LOG.error("Import products from file failed", e);
            resultDTO.setSuccess(false);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return resultDTO;
    }

    /**
     * Remove FixNowDow document with specified id.
     *
     * @param customerId id of customer
     * @param docId      id of document. Received in importProducts() method call result.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/fixNowDoc/{docId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeFixNowDoc(@PathVariable("customerId") Long customerId,
                                @PathVariable("docId") Long docId) {
        try {
            userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.IMPORT_PRODUCT.name());

            productService.removeImportFailedDocument(docId);
        } catch (Exception e) {
            LOG.error("Delete binary document exception: ", e);
        }
    }

    /**
     * Save or update product.
     *
     * @param customerId id of customer
     * @param product    to save
     * @return product id
     * @throws ApplicationException    when invalid customer ID or required product was not found or when product validation checks fail.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseVO save(@PathVariable("customerId") Long customerId,
                           @RequestBody ProductDTO product) throws ApplicationException {
        LtlProductEntity entity = dtoBuilder.buildEntity(product);

        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.ADD_EDIT_PRODUCT.name());

        if (!product.isSharedProduct() && !userPermissionsService.hasCapability(Capabilities.PRODUCT_LIST_CREATE_SELF.name())) {
            throw new ApplicationException("You don't have permission to save product with 'self' option");
        }

        if (!product.isSharedProduct() && !entity.getModification().getCreatedBy().equals(SecurityUtils.getCurrentPersonId())) {
            throw new ApplicationException("You can't save product created by different user with 'self' option");
        }

        productService.saveOrUpdateProduct(entity, customerId, SecurityUtils.getCurrentPersonId(), product.isSharedProduct());

        return new ResponseVO(entity.getId());
    }

    private List<ValueLabelDTO> buildProductsByPrimarySort(ProductListPrimarySort sort,
            List<LtlProductEntity> products) {
        List<ValueLabelDTO> results = new ArrayList<ValueLabelDTO>();

        for (LtlProductEntity product : products) {
            String value = product.getId().toString();
            String unNumber = null;
            String hazmatClass = null;
            String packingGroup = null;
            if (product.getHazmatInfo() != null) {
                unNumber = product.getHazmatInfo().getUnNumber();
                hazmatClass = product.getHazmatInfo().getHazmatClass();
                packingGroup = product.getHazmatInfo().getPackingGroup();
            }
            String label = null;
            if (sort == null || sort == ProductListPrimarySort.PRODUCT_DESCRIPTION) {
                label = String.join(" ", StringUtils.defaultString(product.getDescription()),
                        StringUtils.defaultString(product.getProductCode()), StringUtils.defaultString(unNumber),
                        StringUtils.defaultString(hazmatClass), StringUtils.defaultString(packingGroup));
            } else if (sort == ProductListPrimarySort.SKU_PRODUCT_CODE) {
                label = String.join(" ", StringUtils.defaultString(product.getProductCode()),
                        StringUtils.defaultString(product.getDescription()), StringUtils.defaultString(unNumber),
                        StringUtils.defaultString(hazmatClass), StringUtils.defaultString(packingGroup));
            }
            results.add(new ValueLabelDTO(value, label));
        }
        return results;
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
    @RequestMapping(value = "/productImportTemplate", method = { RequestMethod.GET, RequestMethod.HEAD })
    // RequestMethod.HEAD is required for IE when displaying document inline
    public void downloadProductImportTemplate(@PathVariable("customerId") Long customerId, HttpServletResponse response) throws IOException {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.IMPORT_PRODUCT.name());

        response.setContentType(MimeTypes.XLS.getMimeString());
        response.setHeader("Content-Disposition", "attachment; filename=Product_Import_Template.xls");

        InputStream inputStream = productImportTemplateResource.getInputStream();
        if (inputStream != null) {
            IOUtils.copy(inputStream, response.getOutputStream());
            inputStream.close();
        }

    }

    /**
     * Checks whether the product's description and class are unique or not.
     *
     * @param customerId     customer id
     * @param productId      product id or <code>null</code> for nonexistent product
     * @param description    product description
     * @param commodityClass product commodity class
     * @param shared         flag which indicates whether the created product is shared or not
     * @return <code>true</code> if the product is unique, otherwise <code>false</code>
     */
    @RequestMapping(value = "/{productId}/isUnique", method = RequestMethod.GET)
    @ResponseBody
    public Boolean isProductUnique(@PathVariable("customerId") Long customerId, @PathVariable("productId") Long productId,
                                   @RequestParam("description") String description, @RequestParam("commodityClass") CommodityClassDTO commodityClass,
                                   @RequestParam("shared") Boolean shared) {
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.ADD_EDIT_PRODUCT.name());

        Long personId = !shared ? SecurityUtils.getCurrentPersonId() : null;
        CommodityClass commodityClassEntity = new CommodityClassDTOBuilder().buildEntity(commodityClass);
        return productService.isProductUnique(customerId, productId, personId, description, commodityClassEntity);
    }

    /**
     * Export list of products for specified customer.
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
        userPermissionsService.checkCapabilityAndOrganization(customerId, Capabilities.EXPORT_PRODUCTS.name());

        return productService.export(customerId, SecurityUtils.getCurrentPersonId());
    }
}
