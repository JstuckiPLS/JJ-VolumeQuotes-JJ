/**
 * Carrier pricing pallet page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricingPalletPageObject', [function() {
    return {
        controller : '[data-ng-controller="PalletPricingCtrl"]',
        copyFrom : '#inputCopyFrom',
        copyFromValue : 'selectedRateToCopy',
        addNewButton : 'button[data-ng-click="addNewEmptyRecord()"]',
        activeTab : "[data-ng-click='loadActiveDetails()']",
        archivedTab : "[data-ng-click='loadArchivedDetails()']",
        activeGridRows : 'div[data-ng-grid="activeGridOptions"] [ng-row]',
        inactiveGridRows : 'div[data-ng-grid="inactiveGridOptions"] [ng-row]',
        resetButton : 'button[data-ng-click="loadActiveDetails()"]',
        saveButton : 'button[data-ng-click="save()"]',
        palletTabSelector: 'a[href="#/pricing/tariffs/46/edit?pricing.pallet"]',

        getCopyFromDisplay : function () {
            return element(this.copyFrom).attr("disabled");
        },
        getActiveGridRowsCount : function() {
            return element(this.activeGridRows).count();
        },
        getInactiveGridRowsCount : function() {
            return element(this.inactiveGridRows).count();
        },
        clickAddNewButton : function() {
            element(this.addNewButton).click();
        },
        getAddNewButtonDisplay : function () {
            return element(this.addNewButton).attr("disabled");
        },
        clickActiveTab : function() {
            element(this.activeTab).click();
        },
        clickArchivedTab : function() {
            element(this.archivedTab).click();
        },
        clickResetButton : function() {
            element(this.resetButton).click();
        },
        getResetButtonDisplay : function () {
            return element(this.resetButton).attr("disabled");
        },
        clickSaveButton : function() {
            element(this.saveButton).click();
        },
        getSaveButtonDisplay : function () {
            return element(this.saveButton).attr("disabled");
        }
    };
}]);