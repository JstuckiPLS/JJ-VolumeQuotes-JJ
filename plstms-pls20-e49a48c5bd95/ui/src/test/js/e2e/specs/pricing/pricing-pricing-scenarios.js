/**
 * End to end scenarios for pricing profile's pricing functionality.
 * 
 * @author Ashwini Neelgund
 */
describe("Pricing profile's pricing functionality", function() {

    var pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should load the blanket pricing type details', function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(4);
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricingPage.controller).count()).toBe(1);
        expect(pricingPage.getSetZipButtonDisplay()).toBe('disabled');
        expect(pricingPage.getEditZipButtonDisplay()).toBe('disabled');
        expect(pricingPage.getDeleteZipButtonDisplay()).toBe('disabled');
        expect(pricingPage.getClearButtonDisplay()).not().toBe('disabled');
        expect(pricingPage.getSaveAsNewButtonDisplay()).toBe('disabled');
        expect(pricingPage.getSaveButtonDisplay()).toBe('disabled');
        expect(pricingPage.getEditButtonDisplay()).toBe('disabled');
        expect(pricingPage.getExpireButtonDisplay()).toBe('disabled');
        expect(pricingPage.getArchiveButtonDisplay()).toBe('disabled');
        expect(pricingPage.getPricingDataRowsCount()).toBe(1);
        pricingPage.clickExpiredTab();
        expect(pricingPage.getPricingDataRowsCount()).toBe(0);
        pricingPage.clickArchivedTab();
        expect(pricingPage.getPricingDataRowsCount()).toBe(0);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});