/**
 * End to end scenarios for Customer pricing functionality.
 * 
 * @author Ashwini Neelgund
 */
describe('Customer pricing functionality', function() {

    var custPricingPage, loginLogoutPageObject;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        custPricingPage = $injector.get('CustPricingPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should display active and archived customer pricing based on the serach criteria', function() {
        browser().navigateTo('#/pricing/customer');
        expect(element(custPricingPage.controller).count()).toBe(1);
        expect(custPricingPage.getNameDisplay()).not().toBe('disabled');
        expect(custPricingPage.getAccountExecutiveDisplay()).not().toBe('disabled');
        expect(custPricingPage.getDateRangeDisplay()).not().toBe('disabled');
        expect(custPricingPage.getFromDateDisplay()).toBe('disabled');
        expect(custPricingPage.getToDateDisplay()).toBe('disabled');
        expect(custPricingPage.getLoadDateRangeDisplay()).not().toBe('disabled');
        expect(custPricingPage.getLoadFromDateDisplay()).toBe('disabled');
        expect(custPricingPage.getLoadToDateDisplay()).toBe('disabled');
        expect(custPricingPage.getCustPricingButtonDisplay()).toBe('disabled');
        expect(custPricingPage.getCustomerRowsCount()).toBe(0);
        custPricingPage.clickArchivedTab();
        expect(custPricingPage.getCustomerRowsCount()).toBe(0);
        custPricingPage.clickActiveTab();
        custPricingPage.setName("PLS");
        expect(custPricingPage.getCustomerRowsCount()).toBe(3);
        custPricingPage.selectFirstRow();
        expect(custPricingPage.getCustPricingButtonDisplay()).not().toBe('disabled');
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});