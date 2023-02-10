package com.pls.shipment.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.enums.DefaultValuesAction;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.organization.BillToRequiredFieldEntity;
import com.pls.core.domain.organization.BillToThresholdSettingsEntity;
import com.pls.core.shared.BillToRequiredField;
import com.pls.core.shared.DisputeCost;
import com.pls.core.shared.Reasons;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.BillingAuditReasonCodeDao;
import com.pls.shipment.dao.BillingAuditReasonsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LdBillingAuditReasonsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadJobNumbersEntity;
import com.pls.shipment.domain.LoadNumbersEntity;
import com.pls.shipment.service.ShipmentBillToUtils;
import com.pls.shipment.service.audit.LoadFinancialStatusTrackingService;
import com.pls.shipment.service.billing.audit.SidHarveyAuditReasonService;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;


/**
 * Implements {@link BillingAuditService}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class BillingAuditServiceImpl implements BillingAuditService {

    static final BigDecimal DEFAULT_REVENUE = new BigDecimal(1500);

    static final BigDecimal DEFAULT_MARGIN_PERCENT = new BigDecimal(70);

    static final BigDecimal DEFAULT_THRESHOLD = new BigDecimal("4.99");

    private static final List<String> AE_EMAIL_REASONS = Stream.of(Reasons.COST_DIFF, Reasons.LOW_MARGIN, Reasons.HIGH_REVENUE_REVIEW)
            .map(Reasons::getReasonCode).collect(Collectors.toList());

    @Autowired
    private BillingAuditReasonsDao billingAuditReasonsDao;

    @Autowired
    private BillingAuditReasonCodeDao billingAuditReasonCodeDao;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private OrganizationPricingDao organizationPricingDao;

    @Autowired
    private SidHarveyAuditReasonService sidHarveyAuditReasonService;

    @Autowired
    private BillingAuditReasonCodeDao reasonCodeDao;


    @Autowired
    private ShipmentEmailSender shipmentEmailSender;


    @Autowired
    private LoadFinancialStatusTrackingService loadTrackingService;
    /**
     * Update reasons for load and send email reasons if financial status's been changed.
     * 
     * @param loadId
     *            loadId which use to get loadEntity {@link LoadEntity#getId()}
     * 
     * @param status
     *            status which load had when came in response and before was likely to be modified {@link ShipmentFinancialStatus}
     * 
     */
    @Override
    public void updateBillingAuditReasonForLoad(Long loadId, ShipmentFinancialStatus status) {
        LoadEntity load = ltlShipmentDao.find(loadId);
        updateBillingAuditReasonForLoad(load, status);
    }

    /**
     * Update reasons for load and send email reasons if financial status's been changed.
     * 
     * @param load
     *            load entity {@link LoadEntity}
     * 
     * @param previousFinStatus
     *            status which load had when came in response and before was likely to be modified {@link ShipmentFinancialStatus}
     */
    @Override
    public void updateBillingAuditReasonForLoad(LoadEntity load, ShipmentFinancialStatus previousFinStatus) {
        if ((load.getFinalizationStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD
                || load.getFinalizationStatus() == ShipmentFinancialStatus.PRICING_AUDIT_HOLD)
                && isNotReadyForConsolidated(load)) {
            List<Reasons> reasons = getReasonsForLoad(load);
            boolean isAnotherReason = isAnotherReasonSpecified(load.getBillingAuditReasons());
            boolean isHold = !reasons.isEmpty() || isAnotherReason;
            if (load.getFinalizationStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD && !isHold) {
                load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING);
                ltlShipmentDao.saveOrUpdate(load);
                updateBillingAuditReasons(reasons, load.getBillingAuditReasons(), load.getId());
                loadTrackingService.logLoadFinancialStatusEvent(new AuditReasonBO(load.getId()), ShipmentFinancialStatus.ACCOUNTING_BILLING);
            } else {
                List<String> reasonCodes = updateBillingAuditReasons(getReasonsForLoad(load), load.getBillingAuditReasons(), load.getId());
                if (isSendLoadReasonEmail(load, previousFinStatus, reasonCodes)) {
                    shipmentEmailSender.sendLoadReasonsEmail(load, reasonCodeDao.getAll(reasonCodes));
                }
            }
        }
    }

    private boolean isSendLoadReasonEmail(LoadEntity load, ShipmentFinancialStatus previousFinStatus, List<String> reasonCodes) {
        return load.getFinalizationStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD
                && BooleanUtils.isTrue(load.getBillTo().getEmailAccountExecutive())
                && ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD != previousFinStatus
                && reasonCodes.stream().anyMatch(AE_EMAIL_REASONS::contains);
    }

    private List<Reasons> getReasonsForLoad(LoadEntity load) {
        List<Reasons> allReasons = new ArrayList<Reasons>(7);
        allReasons.addAll(getGenericReasons(load));
        allReasons.addAll(getReasonsForSidHarvey(load));
        return allReasons;
    }

    private List<Reasons> getGenericReasons(LoadEntity load) {
        List<Reasons> reasons = new ArrayList<Reasons>(12);
        if (isCostDiff(load)) {
            reasons.add(Reasons.COST_DIFF);
        }
        if (isLowMargin(load)) {
            reasons.add(Reasons.LOW_MARGIN);
        }
        if (!load.isCustReqDocPresent()) {
            reasons.add(Reasons.MISSING_DOCUMENT);
        }
        if (isMissingPaymentsTerm(load)) {
            reasons.add(Reasons.MISSING_PAYMENTS_TERM);
        }
        if (isRequiredInvoiceAudit(load)) {
            reasons.add(Reasons.INVOICE_AUDIT);
        }
        if (!isSidHarvey(load) && isHighRevReason(load)) {
            reasons.add(Reasons.HIGH_REVENUE_REVIEW);
        }
        addCostAuditReasons(load, reasons);
        addMissingFieldReasons(load, reasons);
        addRequiredIdentifierReasons(load, reasons);
        return reasons;
    }

    private void addRequiredIdentifierReasons(LoadEntity load, List<Reasons> reasons) {

        Map<BillToRequiredField, BillToRequiredFieldEntity> billToReqFields = ShipmentBillToUtils.getMatchedRules(load);
        LoadNumbersEntity loadNumbersEntity = load.getNumbers();

        addRequiredIdentifierReason(loadNumbersEntity.getBolNumber(), billToReqFields, BillToRequiredField.BOL, reasons,
                Reasons.INCORRECT_BOL_NUMBER);

        String cargoValue = load.getLoadAdditionalFields() != null
                && load.getLoadAdditionalFields().getCargoValue() != null
                        ? load.getLoadAdditionalFields().getCargoValue().toString() : null;

        addRequiredIdentifierReason(loadNumbersEntity.getGlNumber(), billToReqFields, BillToRequiredField.GL, reasons,
                Reasons.INCORRECT_GL_NUMBER);

        addRequiredIdentifierReason(cargoValue, billToReqFields, BillToRequiredField.CARGO, reasons,
                Reasons.INCORRECT_CARGO_VALUE);

        if (loadNumbersEntity.getJobNumbers() != null) {
            for (LoadJobNumbersEntity jobNumber : loadNumbersEntity.getJobNumbers()) {
                if (isFieldIncorrect(jobNumber.getJobNumber(), billToReqFields, BillToRequiredField.JOB)) {
                    reasons.add(Reasons.INCORRECT_JOB_NUMBER);
                    break;
                }
            }
        }

        addRequiredIdentifierReason(loadNumbersEntity.getPoNumber(), billToReqFields, BillToRequiredField.PO, reasons,
                Reasons.INCORRECT_PO_NUMBER);

        addRequiredIdentifierReason(loadNumbersEntity.getProNumber(), billToReqFields, BillToRequiredField.PRO, reasons,
                Reasons.INCORRECT_PRO_NUMBER);

        addRequiredIdentifierReason(loadNumbersEntity.getPuNumber(), billToReqFields, BillToRequiredField.PU, reasons,
                Reasons.INCORRECT_PU_NUMBER);

        String requestedBy = load.getRequestedBy() != null ? load.getRequestedBy().getNote() : "";
        addRequiredIdentifierReason(requestedBy, billToReqFields, BillToRequiredField.REQUESTED_BY, reasons,
                Reasons.INCORRECT_REQUESTED_BY);

        addRequiredIdentifierReason(loadNumbersEntity.getRefNumber(), billToReqFields, BillToRequiredField.SHIPPER_REF,
                reasons, Reasons.INCORRECT_SHIPPER_REF_NUMBER);

        addRequiredIdentifierReason(loadNumbersEntity.getSoNumber(), billToReqFields, BillToRequiredField.SO, reasons,
                Reasons.INCORRECT_SO_NUMBER);

        addRequiredIdentifierReason(loadNumbersEntity.getTrailerNumber(), billToReqFields, BillToRequiredField.TRAILER,
                reasons, Reasons.INCORRECT_TRAILER_NUMBER);
    }

    void addRequiredIdentifierReason(String fieldToValidate,
            Map<BillToRequiredField, BillToRequiredFieldEntity> requiredFields, BillToRequiredField fieldTypeToValidate,
            List<Reasons> reasons, Reasons reason) {
        if (isFieldIncorrect(fieldToValidate, requiredFields, fieldTypeToValidate)) {
            reasons.add(reason);
        }
    }

    private boolean isFieldIncorrect(String fieldToValidate, Map<BillToRequiredField, BillToRequiredFieldEntity> requiredFields,
            BillToRequiredField fieldTypeToValidate) {
        BillToRequiredFieldEntity requiredFieldEntity = requiredFields.get(fieldTypeToValidate);
        if (requiredFieldEntity == null || requiredFieldEntity.getActionForDefaultValues() != DefaultValuesAction.AUDIT) {
            return false;
        }
        if (requiredFieldEntity.getStartWith() != null || requiredFieldEntity.getEndWith() != null) {
            return fieldToValidate == null || !fieldToValidate.matches(requiredFieldEntity.getRuleExp());
        }
        if (requiredFieldEntity.getDefaultValue() != null) {
            return ObjectUtils.notEqual(fieldToValidate, requiredFieldEntity.getDefaultValue());
        }
        return false;
    }

    private void addMissingFieldReasons(LoadEntity load, List<Reasons> reasons) {
        if (isMissingBOL(load)) {
            reasons.add(Reasons.MISSING_BOL);
        }
        if (isMissingGL(load)) {
            reasons.add(Reasons.MISSING_GL);
        }
        if (isMissingPO(load)) {
            reasons.add(Reasons.MISSING_PO);
        }
        if (isMissingPU(load)) {
            reasons.add(Reasons.MISSING_PU);
        }
        if (isMissingSO(load)) {
            reasons.add(Reasons.MISSING_SO);
        }
        if (isMissingShipperRef(load)) {
            reasons.add(Reasons.MISSING_SHIPPER_REF);
        }
        if (isMissingTrailerNumber(load)) {
            reasons.add(Reasons.MISSING_TRAILER_NUMBER);
        }
    }

    private void addCostAuditReasons(LoadEntity load, List<Reasons> reasons) {
        if (isRequestPaperwork(load)) {
            reasons.add(Reasons.REQUEST_PAPERWORK);
        }
        if (isDisputeAccExecutive(load)) {
            reasons.add(Reasons.DISPUTE_ACCOUNT_EXEC);
        }
        if (isDisputeFinance(load)) {
            reasons.add(Reasons.DISPUTE_FINANCE);
        }
        if (isCostReviewComplete(load)) {
            reasons.add(Reasons.COST_REVIEW_COMPLETE);
        }
        if (isDisputeResolved(load)) {
            reasons.add(Reasons.DISPUTE_RESOLVED);
        }
    }

    private boolean isCostReviewComplete(LoadEntity load) {
        return load.getActiveCostDetail().getAuditShipmentCostDetails() != null
                && load.getActiveCostDetail().getAuditShipmentCostDetails().getUpdateRevenue() != null;
    }

    private boolean isDisputeFinance(LoadEntity load) {
        return load.getActiveCostDetail().getAuditShipmentCostDetails() != null
                && DisputeCost.FINANCE == load.getActiveCostDetail().getAuditShipmentCostDetails().getDisputeCost();
    }

    private boolean isDisputeAccExecutive(LoadEntity load) {
        return load.getActiveCostDetail().getAuditShipmentCostDetails() != null
                && DisputeCost.ACCOUNT_EXEC == load.getActiveCostDetail().getAuditShipmentCostDetails().getDisputeCost();
    }

    private boolean isDisputeResolved(LoadEntity load) {
        return load.getActiveCostDetail().getAuditShipmentCostDetails() != null
                && DisputeCost.RESOLVED_NEW_VB == load.getActiveCostDetail().getAuditShipmentCostDetails().getDisputeCost();
    }

    private boolean isRequestPaperwork(LoadEntity load) {
        return load.getActiveCostDetail().getAuditShipmentCostDetails() != null
                && BooleanUtils.isTrue(load.getActiveCostDetail().getAuditShipmentCostDetails().getRequestPaperwork());
    }

    private boolean isHighRevReason(LoadEntity load) {
        LoadCostDetailsEntity activeCostDetail = load.getActiveCostDetail();
        BillToThresholdSettingsEntity thresholdSettings = load.getBillTo().getBillToThresholdSettings();
        BigDecimal thresholdTotalRevenue = thresholdSettings != null
                ? ObjectUtils.defaultIfNull(thresholdSettings.getTotalRevenue(), DEFAULT_REVENUE) : DEFAULT_REVENUE;
        BigDecimal thresholdMargin = thresholdSettings != null
                ? ObjectUtils.defaultIfNull(thresholdSettings.getMargin(), DEFAULT_MARGIN_PERCENT) : DEFAULT_MARGIN_PERCENT;

        return (activeCostDetail.getTotalRevenue().compareTo(thresholdTotalRevenue) > 0)
                || (activeCostDetail.getMargin().compareTo(thresholdMargin) > 0);
    }

    private boolean isFieldRequired(LoadEntity load, final BillToRequiredField field) {
        return load.getBillTo().getBillToRequiredField().stream()
                .anyMatch(entity -> field.getCode().equals(entity.getFieldName()) && entity.getStatus() == Status.ACTIVE);
    }

    private boolean isMissingPaymentsTerm(LoadEntity load) {
        return load.getBillTo() == null || load.getBillTo().getPlsCustomerTerms() == null
                || load.getBillTo().getPlsCustomerTerms().getId() == null;
    }

    private boolean isRequiredInvoiceAudit(LoadEntity load) {
        return load.getBillTo() != null && BooleanUtils.isTrue(load.getBillTo().isAuditPrefReq());
    }

    @Override
    public String getBillingAuditReasonForLoad(Long loadId) {
        LoadEntity load = ltlShipmentDao.find(loadId);
        List<String> result = new ArrayList<String>(8);

        List<Reasons> reasonsForLoad = getReasonsForLoad(load);
        for (Reasons reasons : reasonsForLoad) {
            result.add(billingAuditReasonCodeDao.getReasonEntityForReasonCode(reasons.getReasonCode()).getDescription());
        }
        return StringUtils.join(result, ",");
    }

    private List<String> updateBillingAuditReasons(List<Reasons> currentReasons, Collection<LdBillingAuditReasonsEntity> auditReasons, Long loadId) {
        List<String> allReasons = auditReasons.stream().map(LdBillingAuditReasonsEntity::getReasonCd).collect(Collectors.toList());
        for (Reasons reason : Reasons.values()) {
            if (currentReasons.contains(reason)) {
                if (!isReasonExistsForLoad(auditReasons, reason.getReasonCode())) {
                    billingAuditReasonsDao.createAndSave(reason.getReasonCode(), loadId, null, null);
                    allReasons.add(reason.getReasonCode());
                }
            } else {
                deleteBillingAuditReasons(auditReasons, reason.getReasonCode());
                allReasons.remove(reason.getReasonCode());
            }
        }
        return allReasons;
    }

    private boolean isReasonExistsForLoad(Collection<LdBillingAuditReasonsEntity> auditReasons, String reasonCode) {
        for (LdBillingAuditReasonsEntity ldBillingAuditReasonsEntity : auditReasons) {
            if (reasonCode.equals(ldBillingAuditReasonsEntity.getReasonCd())) {
                return true;
            }
        }
        return false;
    }

    private void deleteBillingAuditReasons(Collection<LdBillingAuditReasonsEntity> auditReasons, String reasonCd) {
        for (LdBillingAuditReasonsEntity ldBillingAuditReasonsEntity : auditReasons) {
            if (reasonCd.equals(ldBillingAuditReasonsEntity.getReasonCd())) {
                ldBillingAuditReasonsEntity.setStatus(Status.INACTIVE);
                billingAuditReasonsDao.update(ldBillingAuditReasonsEntity);
            }
        }
    }

    private boolean isAnotherReasonSpecified(Collection<LdBillingAuditReasonsEntity> auditReasons) {
        return auditReasons.stream().anyMatch(reason -> {
            String code = reason.getBillAuditReasonCodeEntity() == null ? reason.getReasonCd() : reason.getBillAuditReasonCodeEntity().getId();
            return Reasons.getReasonByCode(code) == null;
        });
    }

    private boolean isLowMargin(LoadEntity load) {
        BigDecimal minAcceptMargin = organizationPricingDao.getMinAcceptMargin(load.getOrganization().getId());
        minAcceptMargin = minAcceptMargin != null ? minAcceptMargin : BigDecimal.valueOf(5);
        LoadCostDetailsEntity activeCostDetail = load.getActiveCostDetail();
        return activeCostDetail.getMargin().compareTo(minAcceptMargin) < 0;
    }

    private boolean isCostDiff(LoadEntity load) {
        CarrierInvoiceDetailsEntity vendorBill = (!load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty())
                ? load.getVendorBillDetails().getCarrierInvoiceDetails().iterator().next() : null;
        BigDecimal vendorBillCost;
        BigDecimal threshold = load.getBillTo() != null && load.getBillTo().getBillToThresholdSettings() != null
                ? load.getBillTo().getBillToThresholdSettings().getThreshold() : DEFAULT_THRESHOLD;
        if (vendorBill != null) {
            vendorBillCost = vendorBill.getTotalCharges() != null ? vendorBill.getTotalCharges() : BigDecimal.ZERO;
        } else {
            return false;
        }
        BigDecimal actualCost = load.getActiveCostDetail().getTotalCost();
        BigDecimal discrepancy = vendorBillCost.subtract(actualCost);
        return (discrepancy.abs().compareTo(threshold) > 0);
    }

    private boolean isMissingBOL(LoadEntity load) {
        return isFieldRequired(load, BillToRequiredField.BOL) && StringUtils.isEmpty(load.getNumbers().getBolNumber());
    }

    private boolean isMissingGL(LoadEntity load) {
        return isFieldRequired(load, BillToRequiredField.GL) && StringUtils.isEmpty(load.getNumbers().getGlNumber());
    }

    private boolean isMissingPO(LoadEntity load) {
        return isFieldRequired(load, BillToRequiredField.PO) && StringUtils.isEmpty(load.getNumbers().getPoNumber());
    }

    private boolean isMissingPU(LoadEntity load) {
        return isFieldRequired(load, BillToRequiredField.PU) && StringUtils.isEmpty(load.getNumbers().getPuNumber());
    }

    private boolean isMissingSO(LoadEntity load) {
        return isFieldRequired(load, BillToRequiredField.SO) && StringUtils.isEmpty(load.getNumbers().getSoNumber());
    }

    private boolean isMissingShipperRef(LoadEntity load) {
        return isFieldRequired(load, BillToRequiredField.SHIPPER_REF)
                && StringUtils.isEmpty(load.getNumbers().getRefNumber());
    }

    private boolean isMissingTrailerNumber(LoadEntity load) {
        return isFieldRequired(load, BillToRequiredField.TRAILER) && StringUtils.isEmpty(load.getNumbers().getTrailerNumber());
    }

    private List<Reasons> getReasonsForSidHarvey(LoadEntity load) {
        return sidHarveyAuditReasonService.getBillingAuditReasonForSidHarvey(load);
    }

    private boolean isSidHarvey(LoadEntity load) {
        return sidHarveyAuditReasonService.isSidHarvey(load.getOrganization().getId());
    }

    private boolean isNotReadyForConsolidated(LoadEntity load) {
        return load.getBillingAuditReasons().stream().map(LdBillingAuditReasonsEntity::getReasonCd)
                .noneMatch(Reasons.READY_FOR_CONSOLIDATED.getReasonCode()::equals);
    }
}
