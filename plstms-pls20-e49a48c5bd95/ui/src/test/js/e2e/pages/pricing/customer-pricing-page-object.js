/**
 * Customer Pricing page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('CustPricingPageObject', [function() {
    return {
        controller : '[data-ng-controller="CustomersTabCtrl"]',
        name : '#inputCustName',
        nameValue : 'criteria.name',
        accountExecutive : '#inputAccExec',
        accountExecutiveValue : 'plsAccountExecutive',
        dateRange : '#inputRange',
        dateRangeValue : 'criteria.dateRange',
        fromDate : '#inputFrom',
        fromDateValue : 'criteria.fromDate',
        toDate : '#inputTo',
        toDateValue : 'criteria.toDate',
        loadDateRange : '#inputLoadRange',
        loadDateRangeValue : 'criteria.loadDateRange',
        loadFromDate : '#inputLoadFrom',
        loadFromDateValue : 'criteria.loadFromDate',
        loadToDate : '#inputLoadTo',
        loadToDateValue : 'criteria.loadToDate',
        clearButton : 'button[data-ng-click="clearSearchCriteria()"]',
        activeTab : "[data-ng-click='changeStatus('A')']",
        archivedTab : "[data-ng-click='changeStatus('I')']",
        gridRows : 'div[data-ng-grid="gridOptions"] [ng-row]',
        gridFirstRow: 'div[data-ng-grid="gridOptions"] [ng-row]:first',
        custPricingButton : 'button[data-ng-click="displayCustProf()"]',

        setName : function(value) {
            input(this.nameValue).enter(value);
        },
        getName : function() {
            return element(this.name).val();
        },
        getNameDisplay : function () {
            return element(this.name).attr("disabled");
        },
        setAccountExecutive : function(value) {
            input(this.accountExecutiveValue).enter(value);
        },
        getAccountExecutive : function() {
            return element(this.accountExecutive).val();
        },
        getAccountExecutiveDisplay : function () {
            return element(this.accountExecutive).attr("disabled");
        },
        setDateRange : function(value) {
            select(this.dateRangeValue).option(value);
        },
        getDateRange : function() {
            return element(this.dateRange).val();
        },
        getDateRangeDisplay : function () {
            return element(this.dateRange).attr("disabled");
        },
        setFromDate : function(value) {
            input(this.fromDateValue).enter(value);
        },
        getFromDate : function() {
            return element(this.fromDate).val();
        },
        getFromDateDisplay : function () {
            return element(this.fromDate).attr("disabled");
        },
        setToDate : function(value) {
            input(this.toDateValue).enter(value);
        },
        getToDate : function() {
            return element(this.toDate).val();
        },
        getToDateDisplay : function () {
            return element(this.toDate).attr("disabled");
        },
        setLoadDateRange : function(value) {
            select(this.loadDateRangeValue).option(value);
        },
        getLoadDateRange : function() {
            return element(this.loadDateRange).val();
        },
        getLoadDateRangeDisplay : function () {
            return element(this.loadDateRange).attr("disabled");
        },
        setLoadFromDate : function(value) {
            input(this.loadFromDateValue).enter(value);
        },
        getLoadFromDate : function() {
            return element(this.loadFromDate).val();
        },
        getLoadFromDateDisplay : function () {
            return element(this.loadFromDate).attr("disabled");
        },
        setLoadToDate : function(value) {
            input(this.loadToDateValue).enter(value);
        },
        getLoadToDate : function() {
            return element(this.loadToDate).val();
        },
        getLoadToDateDisplay : function () {
            return element(this.loadToDate).attr("disabled");
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
        getCustomerRowsCount : function() {
            return element(this.gridRows).count();
        },
        selectFirstRow: function() {
            element(this.gridFirstRow).click();
        },
        clickCustPricingButton : function() {
            element(this.custPricingButton).click();
        },
        getCustPricingButtonDisplay : function () {
            return element(this.custPricingButton).attr("disabled");
        },
    };
}]);