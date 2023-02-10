/**
 * Carrier pricing zones page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricingZonesPageObject', [function() {
    return {
        controller : '[data-ng-controller="ZonesCtrl"]',
        copyFrom : '#inputCopyFrom',
        copyFromValue : 'selectedRateToCopy',
        zoneName : '#inputZoneName',
        zoneNameValue : 'zone.name',
        geoText : '#textareaGeography',
        geoTextValue : 'geoText',
        setZipButton : 'button[data-ng-click="setGeography()"]',
        addressGridRows : 'div[data-ng-grid="addressGrid"] [ng-row]',
        editZipButton : 'button[data-ng-click="editGeography()"]',
        deleteZipButton : 'button[data-ng-click="deleteGeography()"]',
        clearButton : 'button[data-ng-click="clear()"]',
        saveAsNewButton : 'button[data-ng-click="saveAsNew()"]',
        saveButton : 'button[data-ng-click="saveZone()"]',
        activeTab : "[data-ng-click='loadListItems('ACTIVE')']",
        archivedTab : "[data-ng-click='loadListItems('ARCHIVED')']",
        gridRows : 'div[data-ng-grid="gridItems"] [ng-row]',
        gridFirstRow: 'div[data-ng-grid="gridItems"] [ng-row]:first',
        editButton : 'button[data-ng-click="editZone()"]',
        archiveButton : 'button[data-ng-click="updateZoneStatus()"]',
        zonesTabSelector: 'a[href="#/pricing/tariffs/46/edit?pricing.zones"]',

        getCopyFromDisplay : function () {
            return element(this.copyFrom).attr("disabled");
        },
        getZonesRowsCount : function() {
            return element(this.gridRows).count();
        },
        getAddressGridRowsCount : function() {
            return element(this.addressGridRows).count();
        },
        getZoneName : function() {
            return element(this.zoneName).val();
        },
        getZoneNameDisplay : function () {
            return element(this.zoneName).attr("disabled");
        },
        getGeoText : function() {
            return element(this.geoText).val();
        },
        getGeoTextDisplay : function () {
            return element(this.geoText).attr("disabled");
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
        clickActiveTab : function() {
            element(this.activeTab).click();
        },
        clickArchivedTab : function() {
            element(this.archivedTab).click();
        },
        clickEditButton : function() {
            element(this.editButton).click();
        },
        getEditButtonDisplay : function () {
            return element(this.editButton).attr("disabled");
        },
        clickArchiveButton : function() {
            element(this.archiveButton).click();
        },
        getArchiveButtonDisplay : function () {
            return element(this.archiveButton).attr("disabled");
        }
    };
}]);