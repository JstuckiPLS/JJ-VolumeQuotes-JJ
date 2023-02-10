/**
 * Pricing profile details page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricingProfileDetailsPageObject', [function() {
    return {
         profileDetailsController : '[data-ng-controller="ProfileDetailsCtrl"]',
         pricingTab : '#pricingTab',
         profileTab : "#profileTab",
         rate : '#inputRateName',
         rateName : 'profileDetails.rateName',
         pricingTypeDropdown : "#inputPricingType",
         pricingTypeValue : 'profileDetails.ltlPricingType',
         scac : "#inputOrganization",
         scacValue : 'plsScacSearch',
         carrierRatingType : "#inputCarrierType",
         carrierRatingTypeValue : 'profileDetails.profileDetails[0].carrierType',
         mileageCalc : "#inputMileageCalc",
         mileageCalcValue : 'profileDetails.profileDetails[0].mileageCalc',
         smc3Tariff : "#inputRatingModule",
         smc3TariffValue : 'profileDetails.profileDetails[0].smc3Tariff',
         sellCarrierRatingType : "#inputCarrierType_1",
         sellCarrierRatingTypeValue : 'profileDetails.profileDetails[1].carrierType',
         sellMileageCalc : "#inputMileageCalc_1",
         sellMileageCalcValue : 'profileDetails.profileDetails[1].mileageCalc',
         sellSmc3Tariff : "#inputRatingModule_1",
         sellSmc3TariffValue : 'profileDetails.profileDetails[1].smc3Tariff',
         activeCheckbox : "#inputActive",
         activeCheckboxValue : 'profileDetails.isActive',
         effectiveDate : "#inputEffectiveDate1",
         effectiveDateValue : 'profileDetails.effDate',
         expiryDate : "#inputExpiresDate1",
         expiryDateValue : 'profileDetails.expDate',
         profileNotes : '#inputComments',
         profileNotesValue : 'profileDetails.note',
         applicableCustomers : 'option[data-ng-repeat="applCust in profileDetails.applicableCustomers"]',
         excludeFrmBkngCheckbox : "#inputBlkFrmBkngLoad",
         excludeFrmBkngCheckboxValue : 'profileDetails.isBlckdFrmBkng',
         originalScac : "#inputActOrganization",
         originalScacValue : 'plsScacSearch',

         blockedCustomersButton : 'button[data-ng-click="openAffectedCustomersDialog()"]',
         applicableCustomersButton : 'button[data-ng-click="openAssignedCustomersDialog()"]',
         saveButton : 'button[data-ng-click="submit()"]',
         cancelButton : 'button[data-ng-click="cancel()"]',
         backToTariffsButton : 'a[href="#/pricing/tariffs/active"]',

         prohLiabTabSelector: 'a[href="#/pricing/tariffs/48/edit?profile.prohibited-liability"]',
         internalNotesTabSelector: 'a[href="#/pricing/tariffs/48/edit?profile.notes"]',
         pricingTabSelector: 'a[href="#/pricing/tariffs/48/edit?pricing"]',
         rdwyPricingTabSelector: 'a[href="#/pricing/tariffs/38/edit?pricing"]',

         setRateName : function(value) {
             input(this.rateName).enter(value);
         },
         getRateName : function() {
             return element(this.rate).val();
         },
         setPricingType : function(value) {
             select(this.pricingTypeValue).option(value);
         },
         setScac : function(value) {
             input(this.scacValue).enter(value);
         },
         getScac : function() {
             return element(this.scac).val();
         },
         getScacDisplay : function () {
             return element(this.scac).attr("disabled");
         },
         setOriginalScac : function(value) {
             input(this.originalScacValue).enter(value);
         },
         getOriginalScac : function() {
             return element(this.originalScac).val();
         },
         getOriginalScacDisplay : function () {
             return element(this.originalScac).attr("disabled");
         },
         setCarrierRatingType : function(value) {
             select(this.carrierRatingTypeValue).option(value);
         },
         getCarrierRatingTypeDisplay : function () {
             return element(this.carrierRatingType).attr("disabled");
         },
         setMileageCalc : function(value) {
             select(this.mileageCalcValue).option(value);
         },
         getMileageCalcDisplay : function () {
             return element(this.mileageCalc).attr("disabled");
         },
         setSmc3Tariff : function(value) {
             select(this.smc3TariffValue).option(value);
         },
         getSmc3TariffDisplay : function () {
             return element(this.smc3Tariff).attr("disabled");
         },
         setSellCarrierRatingType : function(value) {
             select(this.sellCarrierRatingTypeValue).option(value);
         },
         setSellMileageCalc : function(value) {
             select(this.sellMileageCalcValue).option(value);
         },
         setSellSmc3Tariff : function(value) {
             select(this.sellSmc3TariffValue).option(value);
         },
         setActiveCheckbox : function(value) {
             input(this.activeCheckboxValue).check(value);
         },
         getActiveCheckboxDisplay : function () {
             return element(this.activeCheckbox).attr("disabled");
         },
         getActiveCheckbox : function() {
             return element(this.activeCheckbox).val();
         },
         setExcludeFrmBkngCheckbox : function(value) {
             input(this.excludeFrmBkngCheckboxValue).check(value);
         },
         getExcludeFrmBkngCheckboxDisplay : function () {
             return element(this.excludeFrmBkngCheckbox).attr("disabled");
         },
         getExcludeFrmBkngCheckbox : function() {
             return element(this.excludeFrmBkngCheckbox).val();
         },
         setEffectiveDate : function(value) {
             input(this.effectiveDateValue).enter(value);
         },
         getEffectiveDateDisplay : function () {
             return element(this.effectiveDate).attr("disabled");
         },
         setExpiryDate : function(value) {
             input(this.expiryDateValue).enter(value);
         },
         getExpiryDateDisplay : function () {
             return element(this.expiryDate).attr("disabled");
         },
         setProfileNotes : function(value) {
             input(this.profileNotesValue).enter(value);
         },
         getProfileNotesDisplay : function () {
             return element(this.profileNotes).attr("disabled");
         },
         clickCancelButton : function() {
             element(this.cancelButton).click();
         },
         clickSaveButton : function() {
             element(this.saveButton).click();
         },
         clickBackToTariffsButton : function() {
             element(this.backToTariffsButton).click();
         },
         clickBlockedCustomersButton : function() {
             element(this.blockedCustomersButton).click();
         },
         clickApplicableCustomersButton : function() {
             element(this.applicableCustomersButton).click();
         },
         getApplicableCustomersCount : function() {
             return element(this.applicableCustomers).count();
         },

         editAssignedCustController : '[data-ng-controller="EditAssignedCustomersCtrl"]',
         customerFilterValue : 'customerFilter',
         assignedCustGridRows : 'div[data-ng-grid="customerListGrid"] [ng-row]',
         applicableCustGridRows : 'div[data-ng-grid="applicableCustomerListGrid"] [ng-row]',
         assignedCustFirstGridRow : 'div[data-ng-grid="customerListGrid"] [ng-row]:first',
         applicableCustFirstGridRow : 'div[data-ng-grid="applicableCustomerListGrid"] [ng-row]:first',
         addAllButton: 'button[data-ng-click="addAllCustomers()"]',
         addButton: 'button[data-ng-click="addCustomer()"]',
         removeAllButton: 'button[data-ng-click="removeAllCustomers()"]',
         removeButton: 'button[data-ng-click="removeCustomer()"]',
         assignedCustSaveButton : 'button[data-ng-click="save()"]',
         assignedCustCancelButton : 'div[data-ng-controller="EditAssignedCustomersCtrl"] button[data-ng-click="cancel()"]',

         setCustomerFilter : function(value) {
             input(this.customerFilterValue).enter(value);
         },
         getApplicableCustGridRowsCount : function() {
             return element(this.applicableCustGridRows).count();
         },
         getAssignedCustRowsCount : function() {
             return element(this.assignedCustGridRows).count();
         },
         selectAssignedCustFirstRow: function() {
             element(this.assignedCustFirstGridRow).click();
         },
         selectApplicableCustFirstRow: function() {
             element(this.applicableCustFirstGridRow).click();
         },
         clickAssignedCustCancelButton : function() {
             element(this.assignedCustCancelButton).click();
         },
         getAssignedCustCancelBtnDisplay : function () {
             return element(this.assignedCustCancelButton).attr("disabled");
         },
         clickAssignedCustSaveButton : function() {
             element(this.assignedCustSaveButton).click();
         },
         getAssignedCustSaveBtnDisplay : function () {
             return element(this.assignedCustSaveButton).attr("disabled");
         },
         clickAddAllButton : function() {
             element(this.addAllButton).click();
         },
         getAddAllButtonDisplay : function () {
             return element(this.addAllButton).attr("disabled");
         },
         clickAddCustomersButton : function() {
             element(this.addButton).click();
         },
         getAddCustomersBtnDisplay : function () {
             return element(this.addButton).attr("disabled");
         },
         clickRemoveAllButton : function() {
             element(this.removeAllButton).click();
         },
         getRemoveAllButtonDisplay : function () {
             return element(this.removeAllButton).attr("disabled");
         },
         clickRemoveCustomersButton : function() {
             element(this.removeButton).click();
         },
         getRemoveCustomersBtnDisplay : function () {
             return element(this.removeButton).attr("disabled");
         }
    };
}]);
