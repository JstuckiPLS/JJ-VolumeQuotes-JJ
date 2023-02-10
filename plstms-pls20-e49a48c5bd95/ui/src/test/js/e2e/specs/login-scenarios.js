/**
 * This scenario checks basic login\logout behaviour.
 * 
 * @author Eugene Borshch
 */
describe('Login and Logout behaviour', function () {
    var loginLogoutPageObject  = undefined;

    beforeEach(function () {
        $injector = angular.injector(['PageObjectsModule']);
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
    });

    it('should open login form with "User" and "Password" fields', function () {
        expect(element(loginLogoutPageObject.submitSelector).count()).toBe(1);
        // We have two inputs in the form (login, passwords)
        expect(element(loginLogoutPageObject.loginVisibleSelector).count()).toBe(1);
        expect(element(loginLogoutPageObject.passwordVisibleSelector).count()).toBe(1);
    });

    it('should login and logout', function () {
        loginLogoutPageObject.login(loginLogoutPageObject.plsCustomer);
         
        //Login should disappear
        expect(element(loginLogoutPageObject.loginVisibleSelector).count()).toBe(0);
        expect(element(loginLogoutPageObject.passwordVisibleSelector).count()).toBe(0);

        loginLogoutPageObject.logout();
        
        expect(element(loginLogoutPageObject.loginVisibleSelector).count()).toBe(1);
        expect(element(loginLogoutPageObject.passwordVisibleSelector).count()).toBe(1);
    });

    it('should login as PLS Customer and see corresponding menu', function () {
        var links =[ '[href="#/shipment-entry"]',
                     '[href="#/manual-bol"]', 
                     '[href="#/quotes"]',
                     '[href="#/invoices"]',
                     '[href="#/trackingBoard/alerts"]',
                     '[href="#/trackingBoard/booked"]',
                     '[href="#/trackingBoard/undelivered"]',
                     '[href="#/trackingBoard/unbilled"]',
                     '[href="#/trackingBoard/open"]',
                     '[href="#/trackingBoard/hold"]',
                     '[href="#/trackingBoard/all"]',
                     '[href="#/trackingBoard/manualBol"]',
                     '[href="#/account-history"]',
                     '[href="#/dashboard"]',
                     '[href="#/customer"]',
                     '[href="#/products"]', 
                     '[href="#/address-book"]',
                     '[href="#/user-mgmt/users"]',
                     '[href="#/user-mgmt/roles/permissions"]',
                     '[href="#/user-mgmt/roles/roles"]',
                     '[href="#/my-profile"]'];
        loginLogoutPageObject.login(loginLogoutPageObject.plsCustomer);

        _.each(links, function(link, index, list){
            var selector = loginLogoutPageObject.mainCustomerMenuSelector + link;
           expect(element(selector, selector).count()).toBe(1);
        } );
    });

    it('should login as PLS User and see corresponding menu', function () {
        var links =[ '[href="#/shipment-entry"]',
                     '[href="#/manual-bol"]',
                     '[href="#/quotes/quot"]',
                     '[href="#/quotes/saved"]',
                     '[href="#/sales-order"]',
                     '[href="#/vendorBill"]',
                     '[href="#/trackingBoard/alerts"]',
                     '[href="#/trackingBoard/booked"]',
                     '[href="#/trackingBoard/undelivered"]',
                     '[href="#/trackingBoard/unbilled"]',
                     '[href="#/trackingBoard/open"]',
                     '[href="#/trackingBoard/hold"]',
                     '[href="#/trackingBoard/all"]',
                     '[href="#/trackingBoard/manualBol"]',
                     '[href="#/financialBoard/transactional"]',
                     '[href="#/financialBoard/consolidated"]',
                     '[href="#/financialBoard/audit"]',
                     '[href="#/financialBoard/price"]',
                     '[href="#/financialBoard/errors"]',
                     '[href="#/financialBoard/history"]',
                     '[href="#/dashboard"]',
                     '[href="#/pricing/tariffs/active"]',
                     '[href="#/pricing/tariffs/analysis"]',
                     '[href="#/pricing/fuel"]',
                     '[href="#/pricing/customer"]',
                     '[href="#/pricing/scac-codes"]',
                     '[href="#/pricing/acc-types"]',
                     '[href="#/customer"]',
                     '[href="#/products"]',
                     '[href="#/address-book"]',
                     '[href="#/reports"]',
                     '[href="#/user-mgmt/users"]',
                     '[href="#/user-mgmt/roles/permissions"]',
                     '[href="#/user-mgmt/roles/roles"]',
                     '[href="#/my-profile"]',
                     '[href="#/admin"]' ];
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);

        _.each(links, function(link, index, list){
            var selector = loginLogoutPageObject.mainPlsMenuSelector + link;
            expect(element(selector, selector).count()).toBe(1);
        } );
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});