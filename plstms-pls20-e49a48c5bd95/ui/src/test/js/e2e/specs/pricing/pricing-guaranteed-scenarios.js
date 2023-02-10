/**
 * End to end scenarios for pricing guaranteed functionality.
 * 
 * @author Ashwini Neelgund
 */
describe("Pricing guaranteed functionality", function() {

    var pricGuaranteedPage, pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricGuaranteedPage = $injector.get('PricGuaranteedPageObject');
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should load the pricing's guaranteed data", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(3);
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricGuaranteedPage.guaranteedTabSelector, 'check pricing guaranteed tab exists').count()).toBe(1);
        element(pricGuaranteedPage.guaranteedTabSelector, 'go to pricing guaranteed tab').click();
        expect(element(pricGuaranteedPage.controller).count()).toBe(1);
        expect(pricGuaranteedPage.getSetZipButtonDisplay()).toBe('disabled');
        expect(pricGuaranteedPage.getEditZipButtonDisplay()).toBe('disabled');
        expect(pricGuaranteedPage.getDeleteZipButtonDisplay()).toBe('disabled');
        expect(pricGuaranteedPage.getClearButtonDisplay()).not().toBe('disabled');
        expect(pricGuaranteedPage.getSaveAsNewButtonDisplay()).toBe('disabled');
        expect(pricGuaranteedPage.getSaveButtonDisplay()).not().toBe('disabled');
        expect(pricGuaranteedPage.getEditButtonDisplay()).toBe('disabled');
        expect(pricGuaranteedPage.getExpireButtonDisplay()).toBe('disabled');
        expect(pricGuaranteedPage.getArchiveButtonFrmActiveDisplay()).toBe('disabled');
        expect(pricGuaranteedPage.getGuaranteedDataRowsCount()).toBe(1);
        pricGuaranteedPage.clickExpiredTab();
        expect(pricGuaranteedPage.getGuaranteedDataRowsCount()).toBe(0);
        pricGuaranteedPage.clickArchivedTab();
        expect(pricGuaranteedPage.getGuaranteedDataRowsCount()).toBe(0);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});