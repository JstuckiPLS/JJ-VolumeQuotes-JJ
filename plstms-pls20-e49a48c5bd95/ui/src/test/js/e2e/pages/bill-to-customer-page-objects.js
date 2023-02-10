angular.module('PageObjectsModule').factory('BillToCustomersPageObject', [ function() {
    return {
        billToTabSelector: 'a[href="#/customer/1/billTo"]',
        billToControllerSelector: 'div[data-ng-controller="EditCustomerBillToCtrl"]',
        addBillToButtonSelector : 'button[data-ng-click="addBillTo()"]',
        customersGridSelector:'[data-ng-grid = "billToGrid.options"]',

        nameInputField: 'billToModel.billTo.address.addressName',
        setName : function (name) {
            input(this.nameInputField).enter(name);
        },

        countryInputField: '[id^="countryInp"]',
        setCountry : function(country) {
            element(this.countryInputField).query(function(countryField, done) {
                countryField.val(country);
                countryField.trigger('blur');
                done();
            });
        },

        firstAddress: 'billTo.address.address1',
        setFirstAddress : function (address) {
            input(this.firstAddress).enter(address);
        },

        zip: 'plsZipSearch',
        setZip : function(zipAddress) {
            input(this.zip).enter(zipAddress);
        },

        contact: 'billTo.address.contactName',
        setContact : function (contactName) {
            input(this.contact).enter(contactName);
        },

        areaCodeInputField: 'billTo.address.phone.areaCode',
        setAreaCode : function (code) {
            input(this.areaCodeInputField).enter(code);
        },

        phoneNumberInputField : 'billTo.address.phone.number',
        setPhoneNumber : function (phone) {
            input(this.phoneNumberInputField).enter(phone);
        },

        emailInputField: 'billTo.address.email',
        setEmail : function (email) {
            input(this.emailInputField).enter(email);
        },

        addEditFormDialogSelector: '#addBillToDialog',
        saveButtonSelector: 'button[data-ng-click="saveBillTo()"]:first',

        nextButtonSelector: 'button[data-ng-click="nextStep()"]',

        editButtonSelector: 'button[data-ng-click="editBillTo()"]',

        invoicePreferencesTabSelector: 'a[data-ng-click*="invoicePreferences"]',

        reqFiledsTabSelector: 'a[data-ng-click*="reqFields"]',
        reqDocsTabSelector: 'a[data-ng-click*="reqDocs"]'
    };
} ]);