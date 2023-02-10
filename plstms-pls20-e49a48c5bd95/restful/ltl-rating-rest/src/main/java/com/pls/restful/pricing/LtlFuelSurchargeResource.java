package com.pls.restful.pricing;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.pls.core.domain.bo.SurchargeFileImportResult;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportRecordsNumberExceededException;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.ResponseVO;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.DotRegionFuelEntity;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.service.LtlFuelSurchargeService;

/**
 * RESRful for working Ltl Fuel Surcharge.
 * 
 * @author Stas Norochevskiy
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/ltlfuelsurcharge")
public class LtlFuelSurchargeResource {

    private static final String IMPORT_FILE_EXTENSION_REGEXP = "^.*\\.(csv|xls|xlsx)$";

    private static final Logger LOG = LoggerFactory.getLogger(LtlFuelSurchargeResource.class);

    @Autowired
    private LtlFuelSurchargeService ltlFuelSurchargeService;

    // private LtlFuelSurchargeDTOBuilder ltlFuelSurchargeDTOBuilder = new LtlFuelSurchargeDTOBuilder();

    /**
     * Performs "copy from" functionality. - inactivates active {@link LtlFuelSurchargeEntity} in
     * copyToProfileId profile - copies active {@link LtlFuelSurchargeEntity} from copyFromProfileId profile
     * to copyToProfileId
     * 
     * @param paramsMap
     *            map with copyFromProfileId and copyToProfileId
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/copyFrom", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void copyFrom(@RequestBody Map<String, Long> paramsMap) {
        Long copyFromProfileId = paramsMap.get("copyFromProfileId");
        Long copyToProfileId = paramsMap.get("copyToProfileId");
        ltlFuelSurchargeService.copyFrom(copyFromProfileId, copyToProfileId, true);
    }

    /**
     * Retrieves list of ltl fuel surcharge objects with given profileDetailId.
     * 
     * @param profileDetailId
     *            ID of profile detail object
     * @return list of ltl fuel surcharge objects
     */
    @RequestMapping(value = "/activeFuelSurchargeForProfile/{profileDetailId}", method = RequestMethod.GET)
    @ResponseBody
    public List<LtlFuelSurchargeEntity> getActiveFuelSurchargeByProfileDetailId(
            @PathVariable("profileDetailId") Long profileDetailId) {

        return ltlFuelSurchargeService.getActiveFuelSurchargeByProfileDetailId(profileDetailId);
    }

    /**
     * query LTL_FUEL_SURCHARGE table using this FUEL_CHARGE value and get the SURCHARGE like “SELECT
     * SURCHARGE FROM LTL_FUEL_SURCHARGE WHERE MIN_RATE <= <<FUEL_CHARGE>> AND MAX_RATE >= <<FUEL_CHARGE>>”.
     * 
     * @param charge
     *            taken from {@link DotRegionFuelEntity}
     * @return proper surcharge value
     */
    @RequestMapping(value = "/fuelSurchargeByFuelCharge/{charge}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO getFuelSurchargeByFuelCharge(@PathVariable("charge") BigDecimal charge) {
        return new ResponseVO(ltlFuelSurchargeService.getFuelSurchargeByFuelCharge(charge));
    }

    /**
     * Import {@link LtlFuelSurchargeEntity} from document.
     * 
     * @param fileDetail
     *            file details
     * @param profileDetailId
     *            ID of profile detail related to entities
     * 
     * @return imported entities
     * @throws ValidationException
     *             validation exception
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/import", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Map<String, Object> importLtlFuelSurchargeEntities(
            @RequestParam("upload") MultipartFile fileDetail,
            @RequestParam("profileDetailId") Long profileDetailId) throws ValidationException {

        String fileName = fileDetail.getOriginalFilename();
        LOG.info("Loading LTL Fuel SUrcharge from '{}' file...", fileName);
        if (!fileName.toLowerCase().matches(IMPORT_FILE_EXTENSION_REGEXP)) {
            LOG.info("File '{}' has unsupported extension", fileName);
            return null;
        }

        String extension = FilenameUtils.getExtension(fileName);
        List<LtlFuelSurchargeEntity> importedEntities = null;
        InputStream uploadedInputStream = null;
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            SurchargeFileImportResult surchargeFileImportResult = new SurchargeFileImportResult();
            uploadedInputStream = fileDetail.getInputStream();
            importedEntities = ltlFuelSurchargeService.importFuelSurchargeByProfileDetailIdFromFile(uploadedInputStream, extension, profileDetailId,
                    surchargeFileImportResult);
            result.put("validation", surchargeFileImportResult);

        } catch (ImportRecordsNumberExceededException e) {
            LOG.error("Import ltl fuel surcharge from file failed", e);
        } catch (ImportException e) {
            LOG.error("Import ltl fuel surcharge from file failed", e);
        } catch (IOException e) {
            LOG.error("Import ltl fuel surcharge from file failed", e);
        } finally {
            IOUtils.closeQuietly(uploadedInputStream);
        }
        result.put("result", importedEntities);
        return result;
    }

    /**
     * Saves all the given fuel surcharges for a pricing profile.
     * 
     * @param ltlFuelSurcharges
     *            fuel surcharge entities to save
     * @return persisted object
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/saveAll", method = RequestMethod.POST)
    @ResponseBody
    public List<LtlFuelSurchargeEntity> saveAllFuelSurcharges(
            @RequestBody List<LtlFuelSurchargeEntity> ltlFuelSurcharges) {
        return ltlFuelSurchargeService.saveAllFuelSurcharges(ltlFuelSurcharges);
    }

    /**
     * Inactivate entities by specified list of ids.
     * 
     * @param ids -
     *          List of ids {@link LtlFuelSurchargeEntity#getId()}.
     *          Not <code>null</code>.
     * @param profileDetailId -
     *          Profile Detail Id {@link LtlPricingProfileDetailsEntity#getId()}.
     *          Not <code>null</code>.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{profileDetailId}/inactivate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void inactivate(@RequestBody List<Long> ids, @PathVariable("profileDetailId") Long profileDetailId) {
        ltlFuelSurchargeService.updateStatus(ids, Status.INACTIVE, profileDetailId);
    }

    /**
     * Saves given fuel surcharge object.
     * 
     * @param ltlFuelSurchargeDTO
     *            object to save
     * @return persisted object
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public LtlFuelSurchargeEntity saveFuelSurcharge(@RequestBody LtlFuelSurchargeEntity ltlFuelSurchargeDTO) {
        return ltlFuelSurchargeService.saveFuelSurcharge(ltlFuelSurchargeDTO);
    }
}