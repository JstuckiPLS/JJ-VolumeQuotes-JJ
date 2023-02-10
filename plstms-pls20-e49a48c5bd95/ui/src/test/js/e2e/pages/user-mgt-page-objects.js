/**
 * User management page objects.
 * 
 * @author Dmitry Nikolaenko
 */
angular.module('PageObjectsModule').factory('UserMgtPageObject', [ function() {
    return {
        gridRow: '[ng-row]',
        gridFirstRow: '[ng-row]:eq(0)',
        gridSecondRow: '[ng-row]:eq(1)',
        gridLastRow: '[ng-row]:last',
        editUserButton: 'a:contains("Edit")',
        addUserButton: 'a[href="#/user-mgmt/users/add"]',
        userId: 'user.userId',
        firstName: 'user.firstName',
        lastName: 'user.lastName',
        organization: 'user.parentOrganization',
        email: 'user.email',
        country: 'plsCountrySearch',
        address1: 'user.address1',
        address2: 'user.address2',
        city: 'plsZipSearch',
        areaCode: 'user.phone.areaCode',
        number: 'user.phone.number',
        customerContactName: 'user.additionalInfo.contactName',
        customerEmail: 'user.additionalInfo.email',
        customerAreaCode: 'user.additionalInfo.phone.areaCode',
        customerPhone: 'user.additionalInfo.phone.number',
        sameAsUserProfile: 'input[value="SAME_AS_USER_PROFILE"]',
        defaultInfo: 'input[value="DEFAULT"]',
        customerContactInfoRadioGroup: 'input[data-ng-model="user.customerServiceContactInfoType"]',
        inputSearchByUserID: 'input[ng-model="col.searchValue"]:eq(0)',
        saveChangesButton: 'button[data-ng-click="handleSave()"]',
        closeButton: 'button[data-ng-click="goHome()"]',
        searchNameField: 'searchValue',
        companyField: 'company',
        searchUsersButton: 'button[data-ng-click="searchUsers()"]',
        promoCode: 'user.promoCode',

        setPromoCode: function (value) {
            input(this.promoCode).enter(value);
        },
        getPromoCode: function() {
            return this.promoCode;
        },
        selectFirstRow: function() {
            element(this.gridFirstRow).click();
        },
        selectSecondRow: function () {
            element(this.gridSecondRow).click();
        },
        selectLastRow: function () {
            element(this.gridLastRow).click();
        },
        editUser: function() {
            element(this.editUserButton).click();
        },
        addUser: function() {
            element(this.addUserButton).click();
        },
        setUserId: function(value) {
            input(this.userId).enter(value);
        },
        setFirstName: function(value) {
            input(this.firstName).enter(value);
        },
        setLastName: function(value) {
            input(this.lastName).enter(value);
        },
        setOrganization: function(value) {
            input(this.organization).enter(value);
        },
        setEmail: function(value) {
            input(this.email).enter(value);
        },
        setCountry: function(value) {
            input(this.country).enter(value);
        },
        setAddress1: function(value) {
            input(this.address1).enter(value);
        },
        setAddress2: function(value) {
            input(this.address2).enter(value);
        },
        setCity: function(value) {
            input(this.city).enter(value);
        },
        setAreaCode: function(value) {
            input(this.areaCode).enter(value);
        },
        setNumber: function(value) {
            input(this.number).enter(value);
        },
        searchByUserID: function(value) {
            setValue(this.inputSearchByUserID, value);
        },
        checkedSameAsUserProfile: function() {
            checkedValue(this.sameAsUserProfile);
        },
        isCheckedDefault: function() {
            return isChecked(this.defaultInfo);
        },
        isCheckedSameAsUserProfile: function() {
            return isChecked(this.sameAsUserProfile);
        },
        selectCustomerContactInfo: function(index) {
            selectRadioGroupButtonByIndex(this.customerContactInfoRadioGroup, index);
        },
        getSelectedCustomerContactInfo: function() {
            return getSelectIndexRadioGroupButton(this.customerContactInfoRadioGroup);
        },
        clickSaveChangesButton : function() {
            element(this.saveChangesButton).click();
        },
        clickCancelButton : function() {
            element(this.closeButton).click();
        },
        clickBusinessUnitsButton: function() {
            element(this.businessUnitsButton).click();
        },
        getCustomerEmail: function() {
            return input(this.customerEmail).val();
        },
        getCustomerContactName: function() {
            return input(this.customerContactName).val();
        },
        setCustomerContactName: function(value) {
            input(this.customerContactName).enter(value);
        },
        setCustomerEmail: function(value) {
            input(this.customerEmail).enter(value);
        },
        setCustomerPhone: function(areaCode, phone) {
            input(this.customerAreaCode).enter(areaCode);
            input(this.customerPhone).enter(phone);
        },
        setSearchName : function(name) {
            input(this.searchNameField).enter(name);
        },
        setCompanyField : function(company) {
            input(this.companyField).enter(company);
        },
        clickSearchUsersButton : function() {
            element(this.searchUsersButton).click();
        },
    };
    
}]);