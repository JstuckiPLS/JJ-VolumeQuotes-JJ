/**
 * End to end scenarios for pricing pallet functionality.
 * 
 * @author Ashwini Neelgund
 */
describe("Pricing pallet functionality", function() {

    var pricingPalletPage, pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricingPalletPage = $injector.get('PricingPalletPageObject');
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should load the pricing's pallet detail", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricingPalletPage.palletTabSelector, 'check pricing pallet tab exists').count()).toBe(1);
        element(pricingPalletPage.palletTabSelector, 'go to pricing pallet tab').click();
        expect(element(pricingPalletPage.controller).count()).toBe(1);
        expect(pricingPalletPage.getAddNewButtonDisplay()).not().toBe('disabled');
        expect(pricingPalletPage.getResetButtonDisplay()).not().toBe('disabled');
        expect(pricingPalletPage.getSaveButtonDisplay()).not().toBe('disabled');
        expect(pricingPalletPage.getCopyFromDisplay()).not().toBe('disabled');
        expect(pricingPalletPage.getActiveGridRowsCount()).toBe(0);
        pricingPalletPage.clickArchivedTab();
        expect(pricingPalletPage.getInactiveGridRowsCount()).toBe(0);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});