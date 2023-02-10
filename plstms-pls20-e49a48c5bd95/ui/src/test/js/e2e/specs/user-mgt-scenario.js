/**
 * This scenario checks basic user management behaviour.
 * 
 * @author Dmitry Nikolaenko
 */
describe('User Management functionality', function() {
    var $injector, loginLogoutPageObject, userMgtPageObject;

    beforeEach(function() {
        $injector = angular.injector(['PageObjectsModule']);
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        userMgtPageObject = $injector.get("UserMgtPageObject");
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('Should check initial state of fields', function() {
        browser().navigateTo('#/user-mgmt/users/edit/2');
        expect(browser().location().path()).toBe("/user-mgmt/users/edit/2");
        expect(browser().location().path()).toBe("/user-mgmt/users/edit/2");
        expect(input(userMgtPageObject.userId).val()).toEqual('SPARTAN1');
        expect(input(userMgtPageObject.firstName).val()).toEqual('RICH');
        expect(input(userMgtPageObject.lastName).val()).toEqual('LITTON');
        expect(input(userMgtPageObject.organization).val()).toEqual('PLS SHIPPER');
        expect(input(userMgtPageObject.email).val()).toEqual('RELITTONT@test.com');
        expect(input(userMgtPageObject.country).val()).toEqual('USA');
    });

    it('Should add new active user', function() {
        disableLocationChangeCheck();
        browser().navigateTo('#/user-mgmt/users/active');
        userMgtPageObject.addUser();
        expect(browser().location().path()).toBe("/user-mgmt/users/add");
        var userId = 'MAXPAYNE' + _.random(10, 1000);
        userMgtPageObject.setUserId(userId);
        userMgtPageObject.setFirstName('MAX');
        userMgtPageObject.setLastName('PAYNE');
        userMgtPageObject.setOrganization('PLS PRO');
        expect(userMgtPageObject.getSelectedCustomerContactInfo()).toBe(1);
        userMgtPageObject.setEmail('maxpayne@plslogistics.com');
        userMgtPageObject.setCountry('USA');
        userMgtPageObject.setAddress1('911');
        userMgtPageObject.setAddress2('911');
        userMgtPageObject.setCity('1ST CHICAGO NAT PROC CORP, NC, 28290');
        userMgtPageObject.setAreaCode('800');
        userMgtPageObject.setNumber('3355772');
        userMgtPageObject.selectCustomerContactInfo(1);
        userMgtPageObject.setPromoCode('111');
        expect(angularElement(userMgtPageObject.getPromoCode(), 'Promo code Field')).toHaveClass('ng-invalid');
        userMgtPageObject.setPromoCode('1111');
        expect(angularElement(userMgtPageObject.getPromoCode(), 'Promo code field')).not().toHaveClass('ng-invalid');
        expect(userMgtPageObject.isCheckedSameAsUserProfile()).toBe(true);
        userMgtPageObject.clickSaveChangesButton();
        expect(browser().location().path()).toBe("/user-mgmt/users/active");

        userMgtPageObject.setSearchName(userId);
        userMgtPageObject.clickSearchUsersButton();
        userMgtPageObject.searchByUserID(userId);
        element('[data-ng-grid = "usersList.options"]', 'check Grid').query(function(elements, done) {
            var el = elements.find('div:contains('+userId+')');
            if (el.length > 0) {
                done();
            } else {
                done('User is not added');
            }
        });
    });

    it('Should edit active user', function() {
        browser().navigateTo('#/user-mgmt/users/active');
        userMgtPageObject.setCompanyField('%');
        userMgtPageObject.clickSearchUsersButton();
        userMgtPageObject.selectLastRow();
        userMgtPageObject.editUser();
        userMgtPageObject.setAddress1('test address1');
        userMgtPageObject.setAddress2('test address2');
        userMgtPageObject.setCity('AARON, GA, 30450');
        userMgtPageObject.setAreaCode('234');
        userMgtPageObject.setNumber('2345678');
        expect(userMgtPageObject.getSelectedCustomerContactInfo()).toBe(1);
        userMgtPageObject.clickSaveChangesButton();
        expect(browser().location().path()).toBe("/user-mgmt/users/active");
    });

    it('Should changed customer service contact information to default', function() {
        browser().navigateTo('#/user-mgmt/users/active');
        expect(browser().location().path()).toBe("/user-mgmt/users/active");
        var customerContactName = 'EDI master';
        var customerEmail = 'edimaster@plslogistics.com';
        var customerAreaCode = '555';
        var customerPhone = '1234567';

        userMgtPageObject.setCompanyField('%');
        userMgtPageObject.clickSearchUsersButton();
        userMgtPageObject.searchByUserID('EDIADMIN');
        userMgtPageObject.selectFirstRow();
        userMgtPageObject.editUser();
        userMgtPageObject.selectCustomerContactInfo(2);
        userMgtPageObject.setCountry('USA');
        userMgtPageObject.setAddress1('911');
        userMgtPageObject.setAddress2('911');
        userMgtPageObject.setCity('1ST CHICAGO NAT PROC CORP, NC, 28290');
        userMgtPageObject.setAreaCode('234');
        userMgtPageObject.setNumber('5577333');
        userMgtPageObject.setCustomerContactName(customerContactName);
        userMgtPageObject.setCustomerEmail(customerEmail);
        userMgtPageObject.setCustomerPhone(customerAreaCode,customerPhone);
        userMgtPageObject.clickSaveChangesButton();
        expect(browser().location().path()).toBe("/user-mgmt/users/active");
        userMgtPageObject.setCompanyField('%');
        userMgtPageObject.clickSearchUsersButton();
        userMgtPageObject.searchByUserID('EDIADMIN');
        userMgtPageObject.selectFirstRow();
        userMgtPageObject.editUser();
        expect(userMgtPageObject.getSelectedCustomerContactInfo()).toBe(2);
        expect(userMgtPageObject.getCustomerEmail()).toBe(customerEmail);
        expect(userMgtPageObject.getCustomerContactName()).toBe(customerContactName);
        userMgtPageObject.clickSaveChangesButton();
        expect(browser().location().path()).toBe("/user-mgmt/users/active");
    });

    it('Should logout from application', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});