/**
 * Fuel tab page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('FuelTabPageObject', [function() {
    return {
        controller : '[data-ng-controller="FuelCtrl"]',
        dateRange : '#inputDateRangeType',
        dateRangeValue : 'rangeType',
        startDate : '#inputFromDate',
        startDateValue : 'startDate',
        endDate : '#inputToDate',
        endDateValue : 'endDate',
        fuelGridRows : 'div[data-ng-grid=fuelGrid] [ng-row]',
        fuelGridFirstRow: 'div[data-ng-grid="fuelGrid"] [ng-row]:first',
        getFuelUpdateButton : 'button[data-ng-click="fuelUpdate()"]',
        saveButton : 'button[data-ng-click="save()"]',

        setDateRange : function(value) {
            select(this.dateRangeValue).option(value);
        },
        getDateRangeDisplay : function () {
            return element(this.dateRange).attr("disabled");
        },
        getStartDateDisplay : function () {
            return element(this.startDate).attr("disabled");
        },
        getEndDateDisplay : function () {
            return element(this.endDate).attr("disabled");
        },
        getFuelUpdateDisplay : function () {
            return element(this.getFuelUpdateButton).attr("disabled");
        },
        getSaveButtonDisplay : function () {
            return element(this.saveButton).attr("disabled");
        },
        clickGetFuelUpdateButton : function() {
            element(this.getFuelUpdateButton).click();
        },
        clickSaveButton : function() {
            element(this.saveButton).click();
        },
        getFuelGridRowsCount : function() {
            return element(this.fuelGridRows).count();
        },
    };
}]);