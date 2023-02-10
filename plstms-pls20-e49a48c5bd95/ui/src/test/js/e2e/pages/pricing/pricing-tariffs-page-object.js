/**
 * Pricing tariffs page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricingTariffsPageObject', [function() {
    return {
        controller : '[data-ng-controller="ProfilesListCtrl"]',
        activeTab : "a[href='#/pricing/tariffs/active']",
        archivedTab : "a[href='#/pricing/tariffs/archived']",
        gridRows : 'div[data-ng-grid="gridOptions"] [ng-row]',
        gridFirstRow: 'div[data-ng-grid="gridOptions"] [ng-row]:first',
        gridSecondRow : 'div[data-ng-grid="gridOptions"] [ng-row]:eq(1)',
        gridThirdRow : 'div[data-ng-grid="gridOptions"] [ng-row]:eq(2)',
        gridReactivatedRow : 'div[data-ng-grid="gridOptions"] [ng-row]:eq(10)',
        gridFifthRow : 'div[data-ng-grid="gridOptions"] [ng-row]:eq(4)',
        reactivateButton : 'button[data-ng-click="updateProfileStatus()"]',
        addButton : 'button[data-ng-click="addProfile()"]',
        viewOrEditButton : 'button[data-ng-click="editProfile()"]',
        copyFromButton : 'button[data-ng-click="copyProfile()"]',
        inputFilterID : 'input[ng-model="col.searchValue"]:eq(0)',
        inputFilterType : 'input[ng-model="col.searchValue"]:eq(6)',
        getJquerySelectorForCell : function(rowNum, cellNum) {
            return '[ng-row]:eq(' + rowNum + ') > [ng-repeat]:eq(' + cellNum + ') > [ng-cell] > div > [ng-cell-text]';
        },
        getJquerySelectorForScac : function(rowNum) {
            return this.getJquerySelectorForCell(rowNum,3);
        },
        getJquerySelectorForRateName : function(rowNum) {
            return this.getJquerySelectorForCell(rowNum,1);
        },
        getCarrierTariffsRowsCount : function() {
            return element(this.gridRows).count();
        },
        clickActiveTab : function() {
            element(this.activeTab).click();
        },
        clickArchivedTab : function() {
            element(this.archivedTab).click();
        },
        selectFirstRow: function() {
            element(this.gridFirstRow).click();
        },
        selectReactivatedRow: function() {
            element(this.gridReactivatedRow).click();
        },
        selectSecondRow: function() {
            element(this.gridSecondRow).click();
        },
        selectThirdRow: function() {
            element(this.gridThirdRow).click();
        },
        selectFifthRow: function() {
            element(this.gridFifthRow).click();
        },
        clickReactivateButton : function() {
            element(this.reactivateButton).click();
        },
        getReactivateButtonDisplay : function () {
            return element(this.reactivateButton).attr("disabled");
        },
        clickAddButton : function() {
            element(this.addButton).click();
        },
        getAddButtonDisplay : function () {
            return element(this.addButton).attr("disabled");
        },
        clickViewOrEditButton : function() {
            element(this.viewOrEditButton).click();
        },
        getViewOrEditButtonDisplay : function () {
            return element(this.viewOrEditButton).attr("disabled");
        },
        clickCopyFromButton : function() {
            element(this.copyFromButton).click();
        },
        getCopyFromButtonDisplay : function () {
            return element(this.copyFromButton).attr("disabled");
        },
        setFilterType : function(value) {
            setValue(this.inputFilterType, value);
        },
        setFilterID : function(value) {
            setValue(this.inputFilterID, value);
        }
    };
}]);
