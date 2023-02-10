/**
 * Carrier pricing terminal page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricingTerminalPageObject', [function() {
    return {
        controller : '[data-ng-controller="TerminalInfoCtrl"]',
        copyFrom : '#inputCopyFrom',
        copyFromValue : 'selectedRateToCopy',
        terminal : '#inputCompany',
        terminalValue : 'terminalInfo.terminal',
        contact : '#inputContact',
        contactValue : 'terminalInfo.contactName',
        country : '#inputCountry',
        countryValue : 'terminalInfo.address.country.id',
        address1 : '#inputAddress1',
        address1Value : 'terminalInfo.address.address1',
        address2 : '#inputAddress2',
        address2Value : 'terminalInfo.address.address2',
        zip : '#inputCityStZip',
        zipValue : 'terminalInfo.address.zip',
        phoneCountryCode : '#phoneCountryCode',
        phoneCountryCodeValue : 'terminalInfo.phone.countryCode',
        phoneAreaCode : '#phoneAreaCode',
        phoneAreaCodeValue : 'terminalInfo.phone.areaCode',
        phoneNumber : '#phone',
        phoneNumberValue : 'terminalInfo.phone.number',
        faxCountryCode : '#faxCountryCode',
        faxCountryCodeValue : 'terminalInfo.fax.countryCode',
        faxAreaCode : '#faxAreaCode',
        faxAreaCodeValue : 'terminalInfo.fax.areaCode',
        faxNumber : '#fax',
        faxNumberValue : 'terminalInfo.fax.number',
        email : '#inputEmail',
        emailValue : 'terminalInfo.email',
        transiteTime : '#inputTransit',
        transiteTimeValue : 'terminalInfo.transiteTime',
        blockDisplay : '#inputBlockDisplay',
        blockDisplayValue : 'terminalInfo.visible',
        cancelButton : 'button[data-ng-click="loadTerminalInfo()"]',
        saveButton : 'button[data-ng-click="save()"]',
        terminalTabSelector: 'a[href="#/pricing/tariffs/46/edit?pricing.terminal"]',
        rdwyTerminalTabSelector: 'a[href="#/pricing/tariffs/38/edit?pricing.terminal"]',

        getBlockDisplayDisplay : function () {
            return element(this.blockDisplay).attr("disabled");
        },
        getBlockDisplay : function() {
            return element(this.blockDisplay).val();
        },
        getCopyFromDisplay : function () {
            return element(this.copyFrom).attr("disabled");
        },
        setTerminal : function(value) {
            input(this.terminalValue).enter(value);
        },
        getTerminal : function() {
            return element(this.terminal).val();
        },
        getTerminalDisplay : function () {
            return element(this.terminal).attr("disabled");
        },
        getContact : function() {
            return element(this.contact).val();
        },
        getContactDisplay : function () {
            return element(this.contact).attr("disabled");
        },
        getCountry : function() {
            return element(this.country).val();
        },
        getCountryDisplay : function () {
            return element(this.country).attr("disabled");
        },
        getAddress1 : function() {
            return element(this.address1).val();
        },
        getAddress1Display : function () {
            return element(this.address1).attr("disabled");
        },
        getAddress2 : function() {
            return element(this.address2).val();
        },
        getAddress2Display : function () {
            return element(this.address2).attr("disabled");
        },
        getZip : function() {
            return element(this.zip).val();
        },
        getZipDisplay : function () {
            return element(this.zip).attr("disabled");
        },
        getPhCountryCode : function() {
            return element(this.phoneCountryCode).val();
        },
        getPhCountryCodeDisplay : function () {
            return element(this.phoneCountryCode).attr("disabled");
        },
        getPhAreaCode : function() {
            return element(this.phoneAreaCode).val();
        },
        getPhAreaCodeDisplay : function () {
            return element(this.phoneAreaCode).attr("disabled");
        },
        getPhNumber : function() {
            return element(this.phoneNumber).val();
        },
        getPhNumberDisplay : function () {
            return element(this.phoneNumber).attr("disabled");
        },
        getFaxCountryCode : function() {
            return element(this.faxCountryCode).val();
        },
        getFaxCountryCodeDisplay : function () {
            return element(this.faxCountryCode).attr("disabled");
        },
        getFaxAreaCode : function() {
            return element(this.faxAreaCode).val();
        },
        getFaxAreaCodeDisplay : function () {
            return element(this.faxAreaCode).attr("disabled");
        },
        getFaxNumber : function() {
            return element(this.faxNumber).val();
        },
        getFaxNumberDisplay : function () {
            return element(this.faxNumber).attr("disabled");
        },
        getEmail : function() {
            return element(this.email).val();
        },
        getEmailDisplay : function () {
            return element(this.email).attr("disabled");
        },
        getTransiteTime : function() {
            return element(this.transiteTime).val();
        },
        setTransiteTime : function(value) {
            input(this.transiteTimeValue).enter(value);
        },
        getTransiteTimeDisplay : function () {
            return element(this.transiteTime).attr("disabled");
        },
        clickCancelButton : function() {
            element(this.cancelButton).click();
        },
        getCancelButtonDisplay : function () {
            return element(this.cancelButton).attr("disabled");
        },
        clickSaveButton : function() {
            element(this.saveButton).click();
        },
        getSaveButtonDisplay : function () {
            return element(this.saveButton).attr("disabled");
        },
    };
}]);