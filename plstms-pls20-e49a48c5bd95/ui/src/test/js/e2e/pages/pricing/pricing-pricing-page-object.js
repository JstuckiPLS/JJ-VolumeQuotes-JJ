/**
 * Carrier pricing profile's pricing page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricingPageObject', [function() {
    return {
         controller : '[data-ng-controller="PricingDetailsCtrl"]',
         copyFrom : '#inputCopyFrom',
         copyFromValue : 'selectedRateToCopy',
         costType : '#inputCostType',
         costTypeValue : 'priceDetail.costType',
         unitCost : 'input[data-ng-model="priceDetail.unitCost"]',
         unitCostValue : 'priceDetail.unitCost',
         minLbs : '#inputMinLbsValue',
         minLbsValue : 'priceDetail.costApplMinWt',
         maxLbs : '#inputMaxLbsValue',
         maxLbsValue : 'priceDetail.costApplMaxWt',
         minMiles : '#inputMinMilesValue',
         minMilesValue : 'priceDetail.costApplMinDist',
         maxMiles : '#inputMaxMilesValue',
         maxMilesValue : 'priceDetail.costApplMaxDist',
         minCost : '#inputMinCostValue',
         minCostValue : 'priceDetail.minCost',
         marginType : '#inputMarginType',
         marginTypeValue : 'priceDetail.marginType',
         unitMargin : 'input[data-ng-model="priceDetail.unitMargin"]',
         unitMarginValue : 'priceDetail.unitMargin',
         minMargin : '#inputMinMarginValue',
         minMarginValue : 'priceDetail.marginDollarAmt',
         tariff : '#inputTariff',
         tariffValue : 'priceDetail.smcTariff',
         freightClass : '#freightClass',
         freightClassValue : 'priceDetail.commodityClass',
         movType : '#inputMovType',
         movTypeValue : 'priceDetail.movementType',
         effectiveDate : '#inputEffectiveDate',
         effectiveDateValue : 'priceDetail.effDate',
         expiredDate : '#inputExpiredDate',
         expiredDateValue : 'priceDetail.expDate',
         serviceType : '#inputType',
         serviceTypeValue : 'priceDetail.serviceType',
         origin : 'textarea[data-ng-model="editOrigin"]',
         originValue : 'editOrigin',
         destination : 'textarea[data-ng-model="editDestination"]',
         destinationValue : 'editDestination',
         setZipButton : 'button[data-ng-click="setZips()"]',
         addressGridRows : 'div[data-ng-grid="addressGrid"] [ng-row]',
         editZipButton : 'button[data-ng-click="editZips()"]',
         deleteZipButton : 'button[data-ng-click="deleteZips()"]',
         fak50 : '#fak50',
         fak50Value : 'fak50',
         fak55 : '#fak55',
         fak55Value : 'fak55',
         fak60 : '#fak60',
         fak60Value : 'fak60',
         fak65 : '#fak65',
         fak65Value : 'fak65',
         fak70 : '#fak70',
         fak70Value : 'fak70',
         fak77 : '#fak77',
         fak77Value : 'fak77',
         fak85 : '#fak85',
         fak85Value : 'fak85',
         fak92 : '#fak92',
         fak92Value : 'fak92',
         fak100 : '#fak100',
         fak100Value : 'fak100',
         fak110 : '#fak110',
         fak110Value : 'fak110',
         fak125 : '#fak125',
         fak125Value : 'fak125',
         fak150 : '#fak150',
         fak150Value : 'fak150',
         fak175 : '#fak175',
         fak175Value : 'fak175',
         fak200 : '#fak200',
         fak200Value : 'fak200',
         fak250 : '#fak250',
         fak250Value : 'fak250',
         fak300 : '#fak300',
         fak300Value : 'fak300',
         fak400 : '#fak400',
         fak400Value : 'fak400',
         fak500 : '#fak500',
         fak500Value : 'fak500',
         clearButton : 'button[data-ng-click="clear()"]',
         saveAsNewButton : 'button[data-ng-click="save(true)"]',
         saveButton : 'button[data-ng-click="save()"]',
         activeTab : "[data-ng-click='loadListItems('Active')']",
         expiredTab : "[data-ng-click='loadListItems('Expired')']",
         archivedTab : "[data-ng-click='loadListItems('Archived')']",
         gridRows : 'div[data-ng-grid="gridOptions"] [ng-row]',
         gridFirstRow: 'div[data-ng-grid="gridOptions"] [ng-row]:first',
         editButton : 'button[data-ng-click="editDetails()"]',
         expireButton : 'button[data-ng-click="expireEvent()"]',
         archiveButton : 'button[data-ng-click="changeStatusEvent()"]',
         pricingTabSelector: 'a[href="#/pricing/tariffs/46/edit?pricing"]',

         getCopyFromDisplay : function () {
             return element(this.copyFrom).attr("disabled");
         },
         getPricingDataRowsCount : function() {
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
