/**
 * Customer pricing details page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('CustPricDtlsPageObject', [function() {
    return {
        controller : '[data-ng-controller="CustomerPricingController"]',
        backToCustSearchBtn : 'button[data-ng-click="backToCustomerScreen()"]',
        activateCustPricing : '#inputActivatePricing',
        activateCustPricingValue : 'custPricing.orgPricing.activateCustPricing',
        minAcceptMargin : '#inputMarginTolerance',
        minAcceptMarginValue : 'custPricing.orgPricing.minAcceptMargin',
        gainshareAccount : '#inputGainshareAcct',
        gainshareAccountValue : 'custPricing.orgPricing.gainshareAccount',
        gsPlsPct : '#inputGSPlsPct',
        gsPlsPctValue : 'custPricing.orgPricing.gsPlsPct',
        gsCustPct : '#inputGSCustPct',
        gsCustPctValue : 'custPricing.orgPricing.gsCustPct',
        includeBmAcc : '#inputIncludeBMAcc',
        includeBmAccValue : 'custPricing.orgPricing.includeBenchmarkAccessorial',
        blkIndirectSrcTyp : '#blkIndirectSrcTyp',
        blkIndirectSrcTypValue : 'custPricing.orgPricing.blkIndirectSrcTyp',
        defaultMargin : '#inputDefaultMargin',
        defaultMarginValue : 'custPricing.orgPricing.defaultMargin',
        defaultMinMarginAmt : '#inputDefaultMarginMinAmt',
        defaultMinMarginAmtValue : 'custPricing.orgPricing.defaultMinMarginAmt',
        setCustMarginBtn : 'button[data-ng-click="setCustomerMargin()"]',
        activeTab : "[data-ng-click='loadBMProfilesByCriteria('ACTIVE')']",
        archivedTab : "[data-ng-click='loadBMProfilesByCriteria('INACTIVE')']",
        bmProfilesGridRows : 'div[data-ng-grid="benchmarkProfilesGrid"] [ng-row]',
        bmProfilesGridFirstRow: 'div[data-ng-grid="benchmarkProfilesGrid"] [ng-row]:first',
        addBmProfileBtn : 'button[data-ng-click="addBMProfile()"]',
        editBmProfileBtn : 'button[data-ng-click="editBMProfile()"]',
        copyBmProfileBtn : 'button[data-ng-click="copyBMProfile()"]',
        archiveBmProfileBtn : 'button[data-ng-click="updateBMProfileStatus()"]',
        custPricProfGridRows : 'div[data-ng-grid="custPricingProfilesGrid"] [ng-row]',
        custPricProfGridFirstRow: 'div[data-ng-grid="custPricingProfilesGrid"] [ng-row]:first',
        cancelBtn : 'button[data-ng-click="cancel()"]',
        saveBtn : 'button[data-ng-click="submit()"]',

        clickbackToCustSearchBtn : function() {
            element(this.backToCustSearchBtn).click();
        },
        getbackToCustSearchBtnDisplay : function () {
            return element(this.backToCustSearchBtn).attr("disabled");
        },
        setActivateCustPric : function(value) {
            input(this.activateCustPricingValue).check(value);
        },
        getActivateCustPricDisplay : function () {
            return element(this.activateCustPricing).attr("disabled");
        },
        getActivateCustPric : function() {
            return element(this.activateCustPricing).val();
        },
        setMinAcceptMargin : function(value) {
            input(this.minAcceptMarginValue).enter(value);
        },
        getMinAcceptMargin : function() {
            return element(this.minAcceptMargin).val();
        },
        getMinAcceptMarginDisplay : function () {
            return element(this.minAcceptMargin).attr("disabled");
        },
        setGainshareAccount : function(value) {
            input(this.gainshareAccountValue).check(value);
        },
        getGainshareAccountDisplay : function () {
            return element(this.gainshareAccount).attr("disabled");
        },
        getGainshareAccount : function() {
            return element(this.gainshareAccount).val();
        },
        setGsPlsPctMargin : function(value) {
            input(this.gsPlsPctValue).enter(value);
        },
        getGsPlsPctMargin : function() {
            return element(this.gsPlsPct).val();
        },
        getGsPlsPctDisplay : function () {
            return element(this.gsPlsPct).attr("disabled");
        },
        setGsCustPctMargin : function(value) {
            input(this.gsCustPctValue).enter(value);
        },
        getGsCustPctMargin : function() {
            return element(this.gsCustPct).val();
        },
        getGsCustPctDisplay : function () {
            return element(this.gsCustPct).attr("disabled");
        },
        setIncludeBmAcc : function(value) {
            input(this.includeBmAccValue).check(value);
        },
        getIncludeBmAccDisplay : function () {
            return element(this.includeBmAcc).attr("disabled");
        },
        getIncludeBmAcc : function() {
            return element(this.includeBmAcc).val();
        },
        setBlkIndirectSrcTyp : function(value) {
            input(this.blkIndirectSrcTypValue).check(value);
        },
        getBlkIndirectSrcTypDisplay : function () {
            return element(this.blkIndirectSrcTyp).attr("disabled");
        },
        getBlkIndirectSrcTyp : function() {
            return element(this.blkIndirectSrcTyp).val();
        },
        setDefaultMargin : function(value) {
            input(this.defaultMarginValue).enter(value);
        },
        getDefaultMargin : function() {
            return element(this.defaultMargin).val();
        },
        getDefaultMarginDisplay : function () {
            return element(this.defaultMargin).attr("disabled");
        },
        setDefaultMinMarginAmt : function(value) {
            input(this.defaultMinMarginAmtValue).enter(value);
        },
        getDefaultMinMarginAmt : function() {
            return element(this.defaultMinMarginAmt).val();
        },
        getDefaultMinMarginAmtDisplay : function () {
            return element(this.defaultMinMarginAmt).attr("disabled");
        },
        clickSetCustMarginBtn : function() {
            element(this.setCustMarginBtn).click();
        },
        getSetCustMarginBtnDisplay : function () {
            return element(this.setCustMarginBtn).attr("disabled");
        },
        clickActiveTab : function() {
            element(this.activeTab).click();
        },
        clickArchivedTab : function() {
            element(this.archivedTab).click();
        },
        getbmProfilesGridRowsCount : function() {
            return element(this.bmProfilesGridRows).count();
        },
        selectbmProfilesGridFirstRow: function() {
            element(this.bmProfilesGridFirstRow).click();
        },
        clickAddBmProfileBtn : function() {
            element(this.addBmProfileBtn).click();
        },
        getAddBmProfileBtnDisplay : function () {
            return element(this.addBmProfileBtn).attr("disabled");
        },
        clickEditBmProfileBtnButton : function() {
            element(this.editBmProfileBtn).click();
        },
        getEditBmProfileBtnDisplay : function () {
            return element(this.editBmProfileBtn).attr("disabled");
        },
        clickCopyBmProfileBtn : function() {
            element(this.copyBmProfileBtn).click();
        },
        getCopyBmProfileBtnDisplay : function () {
            return element(this.copyBmProfileBtn).attr("disabled");
        },
        clickArchiveBmProfileBtn : function() {
            element(this.archiveBmProfileBtn).click();
        },
        getArchiveBmProfileBtnDisplay : function () {
            return element(this.archiveBmProfileBtn).attr("disabled");
        },
        getCustPricProfRowsCount : function() {
            return element(this.custPricProfGridRows).count();
        },
        selectCustPricProfFirstRow: function() {
            element(this.custPricProfGridFirstRow).click();
        },
        getCancelBtnDisplay : function () {
            return element(this.cancelBtn).attr("disabled");
        },
        getSaveBtnDisplay : function () {
            return element(this.saveBtn).attr("disabled");
        },
        clickSaveBtn : function() {
            element(this.saveBtn).click(); 
        }
    };
}]);