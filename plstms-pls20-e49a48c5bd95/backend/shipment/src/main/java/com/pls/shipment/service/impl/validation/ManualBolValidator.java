package com.pls.shipment.service.impl.validation;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.validation.JobNumberValidator;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.shipment.domain.ManualBolAddressEntity;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.ManualBolJobNumberEntity;
import com.pls.shipment.domain.ManualBolMaterialEntity;

/**
 * Manual Bol Validator.
 * 
 * @author Artem Arapov
 *
 */
@Component
public class ManualBolValidator extends AbstractValidator<ManualBolEntity> {

    private static final String MATERIAL_STR = "materials.";
    private static final String PO_CONSTRAINT = "po";
    private static final int PO_MAX_LENGTH = 50;
    private static final Long SAFWAY_ORG_ID = 206962L;
    private static final int JOB_NUMBER_MAX_LENGTH = 30;

    @Autowired
    JobNumberValidator jobValidator;

    @Autowired
    private UserPermissionsService permissionsService;

    @Override
    protected void validateImpl(ManualBolEntity entity) {
        validateManualBolEntity(entity);
        validateAddresses(entity.getAddresses());
    }

    private void validateManualBolEntity(ManualBolEntity entity) {
        asserts.notNull(entity.getBillTo(), "billTo");
        asserts.notNull(entity.getStatus(), "status");
        asserts.notNull(entity.getOrganization(), "organization");
        asserts.notNull(entity.getOrganization().getId(), "organization");
        asserts.notNull(entity.getLocation(), "location");
        if (entity.getLocation() != null) {
            asserts.notNull(entity.getLocation().getId(), "location");
        }
        validatePONumberLength(entity.getNumbers().getPoNumber());
        validateLoadNumbers(entity);
        validateJobNumbers(entity.getNumbers().getJobNumbers(), entity.getOrganization().getId());
        validateMaterials(entity.getMaterials());
    }

    private void validateAddresses(Set<ManualBolAddressEntity> addresses) {
        asserts.notNull(addresses, "addresses");
        if (addresses == null) {
            return;
        }

        for (ManualBolAddressEntity detail : addresses) {
            asserts.notNull(detail.getAddress(), "address");
        }
    }

    private void validateMaterials(Set<ManualBolMaterialEntity> materials) {
        if (materials == null) {
            return;
        }

        for (ManualBolMaterialEntity material : materials) {
            asserts.notNull(material.getCommodityClass(), MATERIAL_STR + "commodityClass");
            asserts.notNull(material.getWeight(), MATERIAL_STR + "weight");

            if (material.isHazmat()) {
                asserts.notEmpty(material.getUnNumber(), "unNumber");
                asserts.notEmpty(material.getHazmatClass(), "hazmatClass");
            }
        }
    }

    private void validatePONumberLength(String poNumber) {
        if (poNumber != null && poNumber.length() > PO_MAX_LENGTH) {
            asserts.fail(PO_CONSTRAINT, ValidationError.GREATER_THAN);
        }
    }

    private void validateLoadNumbers(ManualBolEntity entity) {
        if (isFieldNotRequiredOrPresent(entity.getNumbers().getBolNumber(), Capabilities.REQUIRE_SHIPMENT_BOL)) {
            asserts.fail("bol", ValidationError.IS_EMPTY);
        }

        if (isFieldNotRequiredOrPresent(entity.getNumbers().getSoNumber(), Capabilities.REQUIRE_SHIPMENT_SO)) {
            asserts.fail("soNumber", ValidationError.IS_EMPTY);
        }

        if (isFieldNotRequiredOrPresent(entity.getNumbers().getTrailerNumber(), Capabilities.REQUIRE_SHIPMENT_TRAILER)) {
            asserts.fail("trailer", ValidationError.IS_EMPTY);
        }

        if (isFieldNotRequiredOrPresent(entity.getNumbers().getGlNumber(), Capabilities.REQUIRE_SHIPMENT_GL)) {
            asserts.fail("glNumber", ValidationError.IS_EMPTY);
        }

        if (isFieldNotRequiredOrPresent(entity.getNumbers().getPoNumber(), Capabilities.REQUIRE_SHIPMENT_PO)) {
            asserts.fail("po", ValidationError.IS_EMPTY);
        }
        if (isFieldNotRequiredOrPresent(entity.getNumbers().getPuNumber(), Capabilities.REQUIRE_SHIPMENT_PU)) {
            asserts.fail("pu", ValidationError.IS_EMPTY);
        }
        if (isFieldNotRequiredOrPresent(entity.getNumbers().getRefNumber(), Capabilities.REQUIRE_SHIPMENT_REF)) {
            asserts.fail("ref", ValidationError.IS_EMPTY);
        }
    }

    private boolean isFieldNotRequiredOrPresent(String fieldValue, Capabilities capability) {
        return StringUtils.isBlank(fieldValue) && permissionsService.hasCapability(capability.name());
    }

    private void validateJobNumbers(Set<ManualBolJobNumberEntity> jobs, Long orgId) {
        if (SAFWAY_ORG_ID.equals(orgId)) {
            for (ManualBolJobNumberEntity job : jobs) {
                validateComponent(jobValidator, job.getJobNumber(), "jobNumber");
            }
        } else {
            for (ManualBolJobNumberEntity job : jobs) {
                if (job.getJobNumber().length() > JOB_NUMBER_MAX_LENGTH) {
                    asserts.fail("jobNumber", ValidationError.FORMAT);
                }
            }
        }
    }
}
