angular.module('PageObjectsModule').factory('ActiveCustomersPageObject', [ function() {
    return {
        buttonAdd : '[data-ng-click="addCustomer()"]',
        buttonEdit: '[data-ng-click="editCustomer()"]',
        buttonSearch : '[data-ng-click="getCustomersList()"]',
        customerSearchControllerSelector:'[data-ng-controller="CustomerSearchCtrl"]',
        customersGridSelector:'[data-ng-grid = "customersGrid"]',
        clickAddButton : function() {
            element(this.buttonAdd).click();
        },
        clickEditButton : function() {
            element(this.buttonEdit).click();
        },
        searchForCustomers: function(searchString) {
            input('customerModel.filterName').enter(searchString);
            element(this.buttonSearch).click();
        }
    };
} ]);