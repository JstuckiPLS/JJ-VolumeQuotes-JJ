/**
 * End to end scenarios for pricing fuel table functionality.
 * 
 * @author Ashwini Neelgund
 */
describe("Pricing fuel table functionality", function() {

    var pricFuelTablePage, pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricFuelTablePage = $injector.get('PricFuelTablePageObject');
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should load the pricing's fuel surcharge details", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(4);
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricFuelTablePage.fuelTableTabSelector, 'check pricing fuel table tab exists').count()).toBe(1);
        element(pricFuelTablePage.fuelTableTabSelector, 'go to pricing fuel table tab').click();
        expect(element(pricFuelTablePage.controller).count()).toBe(1);
        expect(pricFuelTablePage.getExportButtonDisplay()).not().toBe('disabled');
        expect(pricFuelTablePage.getImportButtonDisplay()).not().toBe('disabled');
        expect(pricFuelTablePage.getAddNewButtonDisplay()).not().toBe('disabled');
        expect(pricFuelTablePage.getDeleteButtonDisplay()).toBe('disabled');
        expect(pricFuelTablePage.getClearButtonDisplay()).not().toBe('disabled');
        expect(pricFuelTablePage.getSaveButtonDisplay()).not().toBe('disabled');
        expect(pricFuelTablePage.getFuelTableRowsCount()).toBe(4);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});