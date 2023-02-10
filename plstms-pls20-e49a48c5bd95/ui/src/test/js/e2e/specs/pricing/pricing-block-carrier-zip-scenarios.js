/**
 * End to end scenarios for pricing block carrier zip functionality.
 * 
 * @author Ashwini Neelgund
 */
describe("Pricing block carrier zip functionality", function() {

    var pricBlockCarrZipPage, pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricBlockCarrZipPage = $injector.get('PricBlockCarrZipPageObject');
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should load the pricing's block carrier zip details", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(4);
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricBlockCarrZipPage.blockCarrZipTabSelector, 'check pricing block carrier zip tab exists').count()).toBe(1);
        element(pricBlockCarrZipPage.blockCarrZipTabSelector, 'go to pricing block carrier zip tab').click();
        expect(element(pricBlockCarrZipPage.controller).count()).toBe(1);
        expect(pricBlockCarrZipPage.getClearButtonDisplay()).not().toBe('disabled');
        expect(pricBlockCarrZipPage.getSaveAsNewButtonDisplay()).toBe('disabled');
        expect(pricBlockCarrZipPage.getSaveButtonDisplay()).toBe('disabled');
        expect(pricBlockCarrZipPage.getEditButtonDisplay()).toBe('disabled');
        expect(pricBlockCarrZipPage.getArchiveButtonDisplay()).toBe('disabled');
        expect(pricBlockCarrZipPage.getCopyFromDisplay()).not().toBe('disabled');
        expect(pricBlockCarrZipPage.getOriginDisplay()).not().toBe('disabled');
        expect(pricBlockCarrZipPage.getDestinationDisplay()).not().toBe('disabled');
        expect(angularElement(pricBlockCarrZipPage.origin, 'origin field')).toHaveClass('ng-invalid');
        expect(angularElement(pricBlockCarrZipPage.origin, 'origin field')).toHaveClass('ng-invalid-required');
        expect(angularElement(pricBlockCarrZipPage.origin, 'origin field')).toHaveClass('ng-pristine');
        expect(angularElement(pricBlockCarrZipPage.destination, 'destination field')).toHaveClass('ng-invalid');
        expect(angularElement(pricBlockCarrZipPage.destination, 'destination field')).toHaveClass('ng-invalid-required');
        expect(angularElement(pricBlockCarrZipPage.destination, 'destination field')).toHaveClass('ng-pristine');
        expect(pricBlockCarrZipPage.getBlockCarrZipRowsCount()).toBe(0);
        pricBlockCarrZipPage.clickArchivedTab();
        expect(pricBlockCarrZipPage.getBlockCarrZipRowsCount()).toBe(0);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});