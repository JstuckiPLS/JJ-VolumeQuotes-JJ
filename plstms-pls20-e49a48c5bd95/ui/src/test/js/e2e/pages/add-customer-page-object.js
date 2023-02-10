angular.module('PageObjectsModule').factory('AddCustomersPageObject', [ function() {
    return {
        addCustomerControllerSelector : '[data-ng-controller="AddCustomerCtrl"]',
        addCustomerDetailsControllerSelector : '[data-ng-controller="AddCustomerDetailsCtrl"]',
        divAddCustomer : '#addCustomer',
        inputCustomer : '#customerName',
        inputAddress1 : "addCustomerWizard.customer.address.address1",
        inputZip : "plsZipSearch",
        inputFirstName : "addCustomerWizard.customer.contactFirstName",
        inputLastName : "addCustomerWizard.customer.contactLastName",

        inputPhoneAreaCode : 'addCustomerWizard.customer.contactPhone.areaCode',
        inputPhoneNumber : 'addCustomerWizard.customer.contactPhone.number',
        inputEmail : 'addCustomerWizard.customer.contactEmail',

        inputAccountExecutive : "addCustomerWizard.customer.accountExecutive",

        buttonCopyFromCustomerContact: "#copyFromCustomerContact",
        inputBillToName: "billTo.address.addressName",

        inputCurrencyCode: "billTo.currency",
        inputEdiNumber : "addCustomerWizard.customer.ediAccount",
        inputProcessTime: "timeDto",
        inputLocation: "addCustomerWizard.customer.location",
        inputBusinessUnit: "addCustomerWizard.customer.networkId",
        inputCompanyCode: "#companyCode :selected",
        labelCostCenter: "#costCenterCode",
        checkboxEdiInvoice: 'billTo.invoicePreferences.ediInvoice',
        checkboxGenerateConsigneeInvoice: 'addCustomerWizard.customer.generateConsigneeInvoice',
        labelGenerateConsigneeInvoice: 'label[for="generateConsigneeInvoice"]',

        buttonCancel : "#cancel",
        buttonBack : "#back",
        buttonNext : "#next",
        buttonDone : "#done",

        checkGenerateConsigneeInvoice: function() {
            input(this.checkboxGenerateConsigneeInvoice).check();
        },
        clickCancelButton: function () {
            element(this.buttonCancel).click();
        },
        clickBackButton: function () {
            element(this.buttonBack).click();
        },
        clickNextButton: function () {
            element(this.buttonNext).click();
        },
        clickDoneButton: function () {
            element(this.buttonDone).click();
        },

        getBackBtnDisplay: function () {
            return element(this.buttonBack).css("display");
        },
        setCustomerInput : function(value) {
            element(this.inputCustomer).query(function(elements, done) {
                elements.val(value);
                elements.trigger('blur');
                done();
            });
        },
        setAddress1Input : function(value) {
            input(this.inputAddress1).enter(value);
        },
        setTaxIDInput : function(value) {
            input('addCustomerWizard.customer.federalTaxId').enter(value);
        },
        setZipInput : function(value) {
            input(this.inputZip).enter(value);
        },
        setFirstNameInput : function(value) {
            input(this.inputFirstName).enter(value);
        },
        setLastNameInput : function(value) {
            input(this.inputLastName).enter(value);
        },
        setPhoneAreaCodeInput : function(value) {
            input(this.inputPhoneAreaCode).enter(value);
        },
        setPhoneNumberInput : function(value) {
            input(this.inputPhoneNumber).enter(value);
        },
        setEmailInput : function(value) {
            input(this.inputEmail).enter(value);
        },
        selectAccountExecutiveInput : function(value) {
            select(this.inputAccountExecutive).option(value);
        },
        clickCopyFromCustomerContactButton : function() {
            element(this.buttonCopyFromCustomerContact).click();
        },
        setBillToNameInput : function(value) {
            input(this.inputBillToName).enter(value);
        },
        setCurrencyCodeInput : function(value) {
            select(this.inputCurrencyCode).option(value);
        },
        setProcessTime : function(value) {
            input(this.inputProcessTime).enter(value);
        },
        setEdiNumInput : function(value) {
            input(this.inputEdiNumber).enter(value);
        },
        setLocationInput : function(value) {
            input(this.inputLocation).enter(value);
        },
        selectBusinessUnitInput : function(value) {
            select(this.inputBusinessUnit).option(value);
        },
        getCompanyCode : function() {
            return element(this.inputCompanyCode);
        },
        getCostCenter : function() {
            return element(this.labelCostCenter);
        },
        checkedEdiInvoice: function() {
            input(this.checkboxEdiInvoice).check();
        },
        gridLastRow : '[ng-row]:last',
        selectLastRow : function() { 
            element(this.gridLastRow).click();
        }
    };
} ]);