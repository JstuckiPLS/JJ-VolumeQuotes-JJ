/**
 * Carrier pricing block carrier zip page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricBlockCarrZipPageObject', [function() {
    return {
        controller : '[data-ng-controller="BlockCarrierZipCtrl"]',
        copyFrom : '#inputCopyFrom',
        copyFromValue : 'selectedRateToCopy',
        origin : '#origin',
        originValue : 'editOrigin',
        destination : '#destination',
        destinationValue : 'editDestination',
        clearButton : 'button[data-ng-click="clear()"]',
        saveAsNewButton : 'button[data-ng-click="saveAsNew()"]',
        saveButton : 'button[data-ng-click="setZips()"]',
        activeTab : "[data-ng-click='loadGeo(true)']",
        archivedTab : "[data-ng-click='loadGeo(false)']",
        gridRows : 'div[data-ng-grid="addressGrid"] [ng-row]',
        gridFirstRow: 'div[data-ng-grid="addressGrid"] [ng-row]:first',
        editButton : 'button[data-ng-click="edit()"]',
        archiveButton : 'button[data-ng-click="changeGeoStatus()"]',
        blockCarrZipTabSelector: 'a[href="#/pricing/tariffs/46/edit?pricing.block-carrier-zip"]',

        getCopyFromDisplay : function () {
            return element(this.copyFrom).attr("disabled");
        },
        getBlockCarrZipRowsCount : function() {
            return element(this.gridRows).count();
        },
        getOrigin : function() {
            return element(this.origin).val();
        },
        getOriginDisplay : function () {
            return element(this.origin).attr("disabled");
        },
        getDestination : function() {
            return element(this.destination).val();
        },
        getDestinationDisplay : function () {
            return element(this.destination).attr("disabled");
        },
        clickActiveTab : function() {
            element(this.activeTab).click();
        },
        clickArchivedTab : function() {
            element(this.archivedTab).click();
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
        clickArchiveButton : function() {
            element(this.archiveButton).click();
        },
        getArchiveButtonDisplay : function () {
            return element(this.archiveButton).attr("disabled");
        }
    };
}]);
