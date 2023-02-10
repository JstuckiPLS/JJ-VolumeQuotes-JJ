/**
 * Carrier pricing freight bill pay to page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricFreightBillPayToPageObject', [function() {
    return {
        controller : '[data-ng-controller="FreightBillCtrl"]',
        copyFrom : '#inputCopyFrom',
        copyFromValue : 'selectedRateToCopy',
        company : '#inputCompany',
        companyValue : 'model.company',
        contactName : '#inputContactName',
        contactNameValue : 'model.contactName',
        country : '#inputCountry',
        countryValue : 'model.address.country.id',
        address1 : '#inputAddress1',
        address1Value : 'model.address.address1',
        address2 : '#inputAddress2',
        address2Value : 'model.address.address2',
        zip : '#inputCityStZip',
        zipValue : 'model.address.zip',
        phoneCountryCode : '#phoneCountryCode',
        phoneCountryCodeValue : 'model.phone.countryCode',
        phoneAreaCode : '#phoneAreaCode',
        phoneAreaCodeValue : 'model.phone.areaCode',
        phoneNumber : '#phone',
        phoneNumberValue : 'model.phone.number',
        faxCountryCode : '#faxCountryCode',
        faxCountryCodeValue : 'model.fax.countryCode',
        faxAreaCode : '#faxAreaCode',
        faxAreaCodeValue : 'model.fax.areaCode',
        faxNumber : '#fax',
        faxNumberValue : 'model.fax.number',
        email : '#inputEmail',
        emailValue : 'model.email',
        accountNum : '#inputAccount',
        accountNumValue : 'model.accountNum',
        cancelButton : 'button[data-ng-click="loadThirdParty()"]',
        saveButton : 'button[data-ng-click="save()"]',
        freightBillPayToTabSelector: 'a[href="#/pricing/tariffs/46/edit?pricing.freight-bill"]',

        getCopyFromDisplay : function () {
            return element(this.copyFrom).attr("disabled");
        },
        clickSaveButton : function() {
            element(this.saveButton).click();
        },
        getSaveButtonDisplay : function () {
            return element(this.saveButton).attr("disabled");
        },
        clickCancelButton : function() {
            element(this.cancelButton).click();
        },
        getCancelButtonDisplay : function () {
            return element(this.cancelButton).attr("disabled");
        },
        setCompany : function(value) {
            input(this.companyValue).enter(value);
        },
        getCompany : function() {
            return element(this.company).val();
        },
        getCompanyDisplay : function () {
            return element(this.company).attr("disabled");
        },
        getContactName : function() {
            return element(this.contactName).val();
        },
        getContactNameDisplay : function () {
            return element(this.contactName).attr("disabled");
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
        getAccNumber : function() {
            return element(this.accountNum).val();
        },
        getAccNumberDisplay : function () {
            return element(this.accountNum).attr("disabled");
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