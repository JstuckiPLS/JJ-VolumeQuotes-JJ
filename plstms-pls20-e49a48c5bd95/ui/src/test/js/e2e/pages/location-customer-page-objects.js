angular.module('PageObjectsModule').factory('LocationPageObject', [ function() {
    return {
        locationTabSelector: 'a[href="#/customer/1/locations"]',
        locationControllerSelector: 'div[data-ng-controller="EditCustomerLocationsCtrl"]',
        addLocationButtonSelector: 'button[data-ng-click="addLocation()"]',
        editLocationButtonSelector: 'button[data-ng-click="editLocation()"]',
        locationGridSelector: '[data-ng-grid = "locationsGrid.options"]',

        nameInputField: 'editLocationModel.location.name',
        setName: function(name) {
            input(this.nameInputField).enter(name);
        },
        inputAccountExecutive: 'editLocationModel.location.accExecPersonId',
        selectAccountExecutiveInput : function(value) {
            select(this.inputAccountExecutive).option(value);
        },
        inputStartDate: 'editLocationModel.location.accExecStartDate',
        setAccStartDate: function(value) {
            input(this.inputStartDate).enter(value);
        },
        inputEndDate: 'editLocationModel.location.accExecEndDate',
        setAccEndDate: function(value) {
            input(this.inputEndDate).enter(value);
        },
        inputBillTo: 'editLocationModel.location.billToId',
        selectBillToInput : function(value) {
            select(this.inputBillTo).option(value);
        },

        addEditControllerSelector: 'div[data-ng-controller="EditLocationCtrl"]',
        addEditFormDialogSelector: '#editLocationDialog',
        saveButtonSelector: 'button[data-ng-click="saveLocation()"]'
    };
} ]);