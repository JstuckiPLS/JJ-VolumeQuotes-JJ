angular.module('PageObjectsModule').factory('CustomerDefaultValuesPageObject', [function () {
    return {
        ediCustomsBrokerInput: 'billTo.billToDefaultValues.ediCustomsBroker',
        phoneCountryCodeInput: 'billTo.billToDefaultValues.ediCustomsBrokerPhone.countryCode',
        phoneAreaCodeInput: 'billTo.billToDefaultValues.ediCustomsBrokerPhone.areaCode',
        phoneNumberInput: 'billTo.billToDefaultValues.ediCustomsBrokerPhone.number',
        phoneExtensionInput: 'billTo.billToDefaultValues.ediCustomsBrokerPhone.extension',

        setEdiCustomsBroker: function(name) {
            input(this.ediCustomsBrokerInput).enter(name);
        },
        setEdiCountryCode: function(value) {
            input(this.phoneCountryCodeInput).enter(value);
        },
        setEdiAreaCode: function(value) {
            input(this.phoneAreaCodeInput).enter(value);
        },
        setEdiPhoneNumber: function(value) {
            input(this.phoneNumberInput).enter(value);
        },
        setEdiExtension: function(value) {
            input(this.phoneExtensionInput).enter(value);
        }
    };
}]);