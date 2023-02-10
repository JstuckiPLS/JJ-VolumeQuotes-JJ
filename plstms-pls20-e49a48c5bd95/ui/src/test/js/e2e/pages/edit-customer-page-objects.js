angular.module('PageObjectsModule').factory('EditCustomersPageObject', [ function() {
    return {
        gridSelector: '[data-ng-grid="customersGrid"]',
        rowSelector: 'div[ng-repeat="row in renderedRows"]',
        editCustomerControllerSelector:'[data-ng-controller="EditCustomerCtrl"]',
        editButtonSelector: '[data-ng-click="editCustomer()"]',
        saveChangesButtonSelector: 'button[data-ng-click="editCustomerModel.saveChanges()"]',
        gridRow: '[ng-row]',
        gridFirstRow: '[ng-row]:first',
        gridLastRow : '[ng-row]:last',
        inputSearchByCustomerSelector: 'input[data-ng-model="customerModel.filterName"]',
        searchButtonSelector: '[data-ng-click="getCustomersList()"]',
        labelBusinessUnit: '#businessUnit',
        labelCompanyCode: '#companyCode',
        labelCostCenterCode: '#costCenterCode',
        checkboxGenerateConsigneeInvoice: 'profileModel.customer.generateConsigneeInvoice',
        genereteConsigneeInvoiceCheckbox: '[data-ng-model="profileModel.customer.generateConsigneeInvoice"]',

        checkGenerateConsigneeInvoice: function() {
            input(this.checkboxGenerateConsigneeInvoice).check();
        },
        selectFirstRow: function () {
            element(this.gridFirstRow).click();
        },
        selectLastRow : function() { 
            element(this.gridLastRow).click(); 
        },
        selectRowWithText : function(text) { 
            element(this.gridRow + ":contains(\"" + text + "\")").click(); 
        },
        saveChanges: function() {
            element(this.saveChangesButtonSelector).click();
        },
        getBusinessUnit : function() {
            return element(this.labelBusinessUnit);
        },
        getCompanyCode : function() {
            return element(this.labelCompanyCode);
        },
        getCostCenter : function() {
            return element(this.labelCostCenterCode);
        }
    };
} ]);
