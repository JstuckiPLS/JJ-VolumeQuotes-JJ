/**
 * End to end scenarios for pricing fuel triggers functionality.
 * 
 * @author Ashwini Neelgund
 */
describe("Pricing fuel triggers functionality", function() {

    var pricFuelTriggersPage, pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricFuelTriggersPage = $injector.get('PricFuelTriggersPageObject');
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should load the pricing's fuel trigger details", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(4);
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricFuelTriggersPage.fuelTriggerTabSelector, 'check pricing fuel triggers tab exists').count()).toBe(1);
        element(pricFuelTriggersPage.fuelTriggerTabSelector, 'go to pricing fuel triggers tab').click();
        expect(element(pricFuelTriggersPage.controller).count()).toBe(1);
        expect(pricFuelTriggersPage.getSetZipButtonDisplay()).not().toBe('disabled');
        expect(pricFuelTriggersPage.getEditZipButtonDisplay()).toBe('disabled');
        expect(pricFuelTriggersPage.getDeleteZipButtonDisplay()).toBe('disabled');
        expect(pricFuelTriggersPage.getClearButtonDisplay()).not().toBe('disabled');
        expect(pricFuelTriggersPage.getSaveAsNewButtonDisplay()).toBe('disabled');
        expect(pricFuelTriggersPage.getSaveButtonDisplay()).not().toBe('disabled');
        expect(pricFuelTriggersPage.getEditButtonDisplay()).toBe('disabled');
        expect(pricFuelTriggersPage.getExpireButtonDisplay()).toBe('disabled');
        expect(pricFuelTriggersPage.getArchiveButtonDisplay()).toBe('disabled');
        expect(pricFuelTriggersPage.getFuelTriggersRowsCount()).toBe(1);
        pricFuelTriggersPage.clickExpiredTab();
        expect(pricFuelTriggersPage.getFuelTriggersRowsCount()).toBe(0);
        pricFuelTriggersPage.clickArchivedTab();
        expect(pricFuelTriggersPage.getFuelTriggersRowsCount()).toBe(0);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});