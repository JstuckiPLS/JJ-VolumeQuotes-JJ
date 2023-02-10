/**
 * Carrier pricing fuel triggers page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricFuelTriggersPageObject', [function() {
    return {
        controller : '[data-ng-controller="FuelAndTriggersCtrl"]',
        copyFrom : '#inputCopyFrom',
        copyFromValue : 'selectedRateToCopy',
        dotRegion : '#inputRegion',
        dotRegionValue : 'selectedFuel.dotRegion',
        effectiveDay : '#inputEffectiveDay',
        effectiveDayValue : 'selectedFuel.effectiveDay',
        upchargeFlatType : '#upchargeFlatRadio',
        upchargeTypeValue : 'selectedFuel.upchargeType',
        upchargeFlat : '#inputUpchargeFlat1',
        upchargeFlatValue : 'selectedFuel.upchargeFlat',
        upchargePercType : '#upchargePercentRadio',
        upchargePerc : '#inputUpchargeFlat',
        upchargePercValue : 'selectedFuel.upchargePercent',
        effectiveDate : '#inputEffectiveDate',
        effectiveDateValue : 'selectedFuel.effectiveDate',
        expirationDate : '#inputExpiredDate',
        expirationDateValue : 'selectedFuel.expirationDate',
        origin : '#textAreaOriginZip',
        originValue : 'zipOriginText',
        setZipButton : 'button[data-ng-click="submitGeoServiceChanges()"]',
        addressGridRows : 'div[data-ng-grid="addressGrid"] [ng-row]',
        editZipButton : 'button[data-ng-click="startEditGeoService()"]',
        deleteZipButton : 'button[data-ng-click="deleteGeoService()"]',
        clearButton : 'button[data-ng-click="clear()"]',
        saveAsNewButton : 'button[data-ng-click="save(true)"]',
        saveButton : 'button[data-ng-click="save(false)"]',
        activeTab : "[data-ng-click='loadLtlFuelAndTriggers(tabsEnum.ACTIVE)']",
        expiredTab : "[data-ng-click='loadLtlFuelAndTriggers(tabsEnum.EXPIRED)']",
        archivedTab : "[data-ng-click='loadLtlFuelAndTriggers(tabsEnum.ARCHIVED)']",
        gridRows : 'div[data-ng-grid="gridOptions"] [ng-row]',
        gridFirstRow: 'div[data-ng-grid="gridOptions"] [ng-row]:first',
        editButton : 'button[data-ng-click="startEdit()"]',
        expireButton : 'button[data-ng-click="onExpire()"]',
        archiveButton : 'button[data-ng-click="onChangeStatus()"]',
        fuelTriggerTabSelector: 'a[href="#/pricing/tariffs/46/edit?pricing.fuel-triggers"]',

        getCopyFromDisplay : function () {
            return element(this.copyFrom).attr("disabled");
        },
        getFuelTriggersRowsCount : function() {
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
        }
    };
}]);
