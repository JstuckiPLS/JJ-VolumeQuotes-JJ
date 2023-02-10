/**
 * Carrier pricing guaranteed page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricGuaranteedPageObject', [function() {
    return {
        controller : '[data-ng-controller="GuaranteedCtrl"]',
        copyFrom : '#inputCopyFrom',
        copyFromValue : 'selectedRateToCopy',
        applyBeforeFuelValue : 'price.applyBeforeFuel',
        bollCarrierName : '#inputBOLCarrierName',
        bollCarrierNameValue : 'price.bollCarrierName',
        time : '#inputTime',
        timeValue : 'price.time',
        chargeRuleType : '#inputFlat',
        chargeRuleTypeValue : 'price.chargeRuleType',
        unitCost : 'input[data-ng-model="price.unitCost"]',
        unitCostValue : 'price.unitCost',
        unitMargin : '#inputMargin',
        unitMarginValue : 'price.unitMargin',
        minMargin : '#inputMinChange',
        minMarginValue : 'price.minMargin',
        minCost : '#inputMinCost',
        minCostValue : 'price.minCost',
        maxCost : '#inputMaxCost',
        maxCostValue : 'price.maxCost',
        minLbs : '#inputMinLbsValue',
        minLbsValue : 'price.costApplMinWt',
        maxLbs : '#inputMaxLbsValue',
        maxLbsValue : 'price.costApplMaxWt',
        minMiles : '#inputMinMilesValue',
        minMilesValue : 'price.costApplMinDist',
        maxMiles : '#inputMaxMilesValue',
        maxMilesValue : 'price.costApplMaxDist',
        effectiveDate : '#inputEffectiveDate',
        effectiveDateValue : 'price.effDate',
        expiredDate : '#inputExpiredDate',
        expiredDateValue : 'price.expDate',
        extNotes : '#inputExtNotes',
        extNotesValue : 'price.extNotes',
        serviceType : '#inputType',
        serviceTypeValue : 'price.serviceType',
        movType : '#inputMovType',
        movTypeValue : 'price.movementType',
        intNotes : '#inputIntNotes',
        intNotesValue : 'price.intNotes',
        origin : 'textarea[data-ng-model="originText"]',
        originValue : 'originText',
        destination : 'textarea[data-ng-model="blockedText"]',
        destinationValue : 'blockedText',
        setZipButton : 'button[data-ng-click="blockedSet()"]',
        addressGridRows : 'div[data-ng-grid="gridBlockDestinationsOptions"] [ng-row]',
        editZipButton : 'button[data-ng-click="blockedEdit()"]',
        deleteZipButton : 'button[data-ng-click="blockedDelete()"]',
        clearButton : 'button[data-ng-click="clear()"]',
        saveAsNewButton : 'button[data-ng-click="saveAsNew()"]',
        saveButton : 'button[data-ng-click="save()"]',
        activeTab : "[data-ng-click='loadGuaranteed('Active');']",
        expiredTab : "[data-ng-click='loadGuaranteed('Expired');']",
        archivedTab : "[data-ng-click='loadGuaranteed('Archived');']",
        gridRows : 'div[data-ng-grid="gridOptions"] [ng-row]',
        gridFirstRow: 'div[data-ng-grid="gridOptions"] [ng-row]:first',
        editButton : 'button[data-ng-click="edit()"]',
        expireButton : 'button[data-ng-click="expireGuaranteed()"]',
        reactivateButton : 'button[data-ng-click="archiveButtonAction()"]',
        archiveButtonFrmActive : 'button[data-ng-click="archiveButtonAction()"]',
        archiveButtonFrmExpired : 'button[data-ng-click="archiveButtonAction()"]',
        guaranteedTabSelector: 'a[href="#/pricing/tariffs/46/edit?pricing.guaranteed"]',

        getCopyFromDisplay : function () {
            return element(this.copyFrom).attr("disabled");
        },
        getGuaranteedDataRowsCount : function() {
            return element(this.gridRows).count();
        },
        getAddressGridRowsCount : function() {
            return element(this.addressGridRows).count();
        },
        clickActiveTab : function() {
            element(this.activeTab).click();
        },
        clickExpiredTab : function() {
            element(this.expiredTab).click();
        },
        clickArchivedTab : function() {
            element(this.archivedTab).click();
        },
        clickSetZipButton : function() {
            element(this.setZipButton).click();
        },
        getSetZipButtonDisplay : function () {
            return element(this.setZipButton).attr("disabled");
        },
        clickEditZipButton : function() {
            element(this.editZipButton).click();
        },
        getEditZipButtonDisplay : function () {
            return element(this.editZipButton).attr("disabled");
        },
        clickDeleteZipButton : function() {
            element(this.deleteZipButton).click();
        },
        getDeleteZipButtonDisplay : function () {
            return element(this.deleteZipButton).attr("disabled");
        },
        clickClearButton : function() {
            element(this.clearButton).click();
        },
        getClearButtonDisplay : function () {
            return element(this.clearButton).attr("disabled");
        },
        clickSaveAsNewButton : function() {
            element(this.saveAsNewButton).click();
        },
        getSaveAsNewButtonDisplay : function () {
            return element(this.saveAsNewButton).attr("disabled");
        },
        clickSaveButton : function() {
            element(this.saveButton).click();
        },
        getSaveButtonDisplay : function () {
            return element(this.saveButton).attr("disabled");
        },
        clickEditButton : function() {
            element(this.editButton).click();
        },
        getEditButtonDisplay : function () {
            return element(this.editButton).attr("disabled");
        },
        clickExpireButton : function() {
            element(this.expireButton).click();
        },
        getExpireButtonDisplay : function () {
            return element(this.expireButton).attr("disabled");
        },
        getArchiveButtonFrmActiveDisplay : function () {
            return element(this.archiveButtonFrmActive).attr("disabled");
        }
    };
}]);
