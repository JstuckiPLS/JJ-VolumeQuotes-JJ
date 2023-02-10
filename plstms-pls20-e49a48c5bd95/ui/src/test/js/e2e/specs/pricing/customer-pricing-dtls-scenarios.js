/**
 * End to end scenarios for customer pricing detail functionality.
 * 
 * @author Ashwini Neelgund
 */
describe("Customer pricing detail functionality", function() {

    var custPricDtlsPage, custPricingPage, loginLogoutPageObject;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        custPricDtlsPage = $injector.get('CustPricDtlsPageObject');
        custPricingPage = $injector.get('CustPricingPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should load the selected customer pricing details', function() {
        browser().navigateTo('#/pricing/customer');
        expect(element(custPricingPage.controller).count()).toBe(1);
        custPricingPage.setName("PLS");
        expect(custPricingPage.getCustomerRowsCount()).toBe(3);
        custPricingPage.selectFirstRow();
        custPricingPage.clickCustPricingButton();
        expect(element(custPricDtlsPage.controller).count()).toBe(1);
        expect(custPricDtlsPage.getActivateCustPric()).toBe('on');
        expect(custPricDtlsPage.getMinAcceptMargin()).toBe('5');
        expect(custPricDtlsPage.getDefaultMargin()).toBe('10');
        expect(custPricDtlsPage.getDefaultMinMarginAmt()).toBe('0');
        expect(custPricDtlsPage.getbmProfilesGridRowsCount()).toBe(1);
        custPricDtlsPage.clickArchivedTab();
        expect(custPricDtlsPage.getbmProfilesGridRowsCount()).toBe(0);
        custPricDtlsPage.clickActiveTab();
        expect(custPricDtlsPage.getAddBmProfileBtnDisplay()).not().toBe('disabled');
        expect(custPricDtlsPage.getEditBmProfileBtnDisplay()).toBe('disabled');
        expect(custPricDtlsPage.getCopyBmProfileBtnDisplay()).toBe('disabled');
        expect(custPricDtlsPage.getArchiveBmProfileBtnDisplay()).toBe('disabled');
        expect(custPricDtlsPage.getSetCustMarginBtnDisplay()).not().toBe('disabled');
        expect(custPricDtlsPage.getCustPricProfRowsCount()).toBeGreaterThan(35);
    });

    it('should save selected customer pricing details', function() {
        expect(custPricDtlsPage.getActivateCustPric()).toBe('on');
        custPricDtlsPage.setActivateCustPric(false);
        custPricDtlsPage.setMinAcceptMargin(6);
        custPricDtlsPage.setDefaultMargin(11);
        custPricDtlsPage.setDefaultMinMarginAmt(1);
        custPricDtlsPage.clickSaveBtn();
        expect(custPricDtlsPage.getMinAcceptMargin()).toBe('6');
        expect(custPricDtlsPage.getDefaultMargin()).toBe('11');
        expect(custPricDtlsPage.getDefaultMinMarginAmt()).toBe('1');

        custPricDtlsPage.setActivateCustPric(true);
        custPricDtlsPage.setMinAcceptMargin(5);
        custPricDtlsPage.setDefaultMargin(10);
        custPricDtlsPage.setDefaultMinMarginAmt(0);
        custPricDtlsPage.clickSaveBtn();
        expect(custPricDtlsPage.getMinAcceptMargin()).toBe('5');
        expect(custPricDtlsPage.getDefaultMargin()).toBe('10');
        expect(custPricDtlsPage.getDefaultMinMarginAmt()).toBe('0');
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});