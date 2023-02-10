/**
 * Carrier pricing fuel table page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricFuelTablePageObject', [function() {
    return {
        controller : '[data-ng-controller="FuelTableCtrl"]',
        copyFrom : '#inputCopyFrom',
        copyFromValue : 'selectedRateToCopy',
        gridRows : 'div[data-ng-grid="gridOptions"] [ng-row]',
        gridFirstRow: 'div[data-ng-grid="gridOptions"] [ng-row]:first',
        exportButton : 'button[data-ng-click="onExport()"]',
        importButton : 'button[data-ng-click="onShowImportDialog()"]',
        addNewButton : 'button[data-ng-click="onNewRecord()"]',
        deleteButton : 'button[data-ng-click="openDeleteDialog()"]',
        clearButton : 'button[data-ng-click="loadFuelSurchargeList()"]',
        saveButton : 'button[data-ng-click="saveFuelSurchargeList()"]',
        fuelTableTabSelector : 'a[href="#/pricing/tariffs/46/edit?pricing.fuel-table"]',

        getCopyFromDisplay : function () {
            return element(this.copyFrom).attr("disabled");
        },
        getFuelTableRowsCount : function() {
            return element(this.gridRows).count();
        },
        clickExportButton : function() {
            element(this.exportButton).click();
        },
        getExportButtonDisplay : function () {
            return element(this.exportButton).attr("disabled");
        },
        clickImportButton : function() {
            element(this.importButton).click();
        },
        getImportButtonDisplay : function () {
            return element(this.importButton).attr("disabled");
        },
        clickAddNewButton : function() {
            element(this.addNewButton).click();
        },
        getAddNewButtonDisplay : function () {
            return element(this.addNewButton).attr("disabled");
        },
        clickDeleteButton : function() {
            element(this.deleteButton).click();
        },
        getDeleteButtonDisplay : function () {
            return element(this.deleteButton).attr("disabled");
        },
        clickClearButton : function() {
            element(this.clearButton).click();
        },
        getClearButtonDisplay : function () {
            return element(this.clearButton).attr("disabled");
        },
        clickSaveButton : function() {
            element(this.saveButton).click();
        },
        getSaveButtonDisplay : function () {
            return element(this.saveButton).attr("disabled");
        },
    };
}]);
