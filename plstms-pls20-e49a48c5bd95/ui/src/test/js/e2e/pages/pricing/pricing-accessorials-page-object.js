/**
 * Carrier pricing accessorials page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricingAccPageObject', [function() {
    return {
         controller : '[data-ng-controller="AccessorialsCtrl"]',
         copyFrom : '#inputCopyFrom',
         copyFromValue : 'selectedRateToCopy',
         accessorialType : '#inputAccessorialType',
         accessorialTypeValue : 'accessorial.accessorialType',
         costType : '#inputCostType',
         costTypeValue : 'accessorial.costType',
         unitCost : 'input[data-ng-model="accessorial.unitCost"]',
         unitCostValue : 'accessorial.unitCost',
         minLbs : '#inputMinLbsValue',
         minLbsValue : 'accessorial.costApplMinWt',
         maxLbs : '#inputMaxLbsValue',
         maxLbsValue : 'accessorial.costApplMaxWt',
         minLen : '#inputMinLenValue',
         minLenValue : 'accessorial.costApplMinLength',
         maxLen : '#inputMaxLenValue',
         maxLenValue : 'accessorial.costApplMaxLength',
         minMiles : '#inputMinMilesValue',
         minMilesValue : 'accessorial.costApplMinDist',
         maxMiles : '#inputMaxMilesValue',
         maxMilesValue : 'accessorial.costApplMaxDist',
         minCost : '#inputMinCostValue',
         minCostValue : 'accessorial.minCost',
         maxCost : '#inputMaxCostValue',
         maxCostValue : 'accessorial.maxCost',
         serviceType : '#inputType',
         serviceTypeValue : 'accessorial.serviceType',
         movType : '#inputMovType',
         movTypeValue : 'accessorial.movementType',
         marginType : '#inputMarginType',
         marginTypeValue : 'accessorial.marginType',
         unitMargin : 'input[data-ng-model="accessorial.unitMargin"]',
         unitMarginValue : 'accessorial.unitMargin',
         minMargin : 'input[data-ng-model="accessorial.marginDollarAmt"]',
         minMarginValue : 'accessorial.marginDollarAmt',
         effectiveDate : '#inputEffectiveDate',
         effectiveDateValue : 'accessorial.effDate',
         expiredDate : '#inputExpiredDate',
         expiredDateValue : 'accessorial.expDate',
         extNotes : '#inputExtNotes',
         extNotesValue : 'accessorial.extNotes',
         intNotes : '#inputIntNotes',
         intNotesValue : 'accessorial.intNotes',
         origin : 'textarea[data-ng-model="editOrigin"]',
         originValue : 'editOrigin',
         destination : 'textarea[data-ng-model="editDestination"]',
         destinationValue : 'editDestination',
         setZipButton : 'button[data-ng-click="setZips()"]',
         addressGridRows : 'div[data-ng-grid="addressGrid"] [ng-row]',
         editZipButton : 'button[data-ng-click="editZips()"]',
         deleteZipButton : 'button[data-ng-click="deleteZips()"]',
         clearButton : 'button[data-ng-click="resetAccessorial()"]',
         saveAsNewButton : 'button[data-ng-click="saveAccessorial(true)"]',
         saveButton : 'button[data-ng-click="saveAccessorial(false)"]',
         activeTab : "[data-ng-click='loadListItems('ACTIVE')']",
         expiredTab : "[data-ng-click='loadListItems('EXPIRED')']",
         archivedTab : "[data-ng-click='loadListItems('ARCHIVED')']",
         gridRows : 'div[data-ng-grid="gridOptions"] [ng-row]',
         gridFirstRow: 'div[data-ng-grid="gridOptions"] [ng-row]:first',
         editButton : 'button[data-ng-click="editAccessorial()"]',
         expireButton : 'button[data-ng-click="expireAccessorials()"]',
         archiveButton : 'button[data-ng-click="updateAccessorialStatus()"]',
         accTabSelector: 'a[href="#/pricing/tariffs/46/edit?pricing.accessorials"]',

         getAccessorialTypeDisplay : function () {
             return element(this.accessorialType).attr("disabled");
         },
         getCopyFromDisplay : function () {
             return element(this.copyFrom).attr("disabled");
         },
         getAccDataRowsCount : function() {
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
         clickArchiveButton : function() {
             element(this.archiveButton).click();
         },
         getArchiveButtonDisplay : function () {
             return element(this.archiveButton).attr("disabled");
         },
    };
}]);