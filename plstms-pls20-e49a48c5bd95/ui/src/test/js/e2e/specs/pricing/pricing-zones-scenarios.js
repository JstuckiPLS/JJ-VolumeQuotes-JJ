/**
 * End to end scenarios for pricing zones functionality.
 * 
 * @author Ashwini Neelgund
 */
describe("Pricing zones functionality", function() {

    var pricingZonesPage, pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricingZonesPage = $injector.get('PricingZonesPageObject');
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should load the pricing's zones detail", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(4);
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricingZonesPage.zonesTabSelector, 'check pricing zones tab exists').count()).toBe(1);
        element(pricingZonesPage.zonesTabSelector, 'go to pricing zones tab').click();
        expect(element(pricingZonesPage.controller).count()).toBe(1);
        expect(pricingZonesPage.getSetZipButtonDisplay()).not().toBe('disabled');
        expect(pricingZonesPage.getEditZipButtonDisplay()).toBe('disabled');
        expect(pricingZonesPage.getDeleteZipButtonDisplay()).toBe('disabled');
        expect(pricingZonesPage.getClearButtonDisplay()).not().toBe('disabled');
        expect(pricingZonesPage.getSaveAsNewButtonDisplay()).toBe('disabled');
        expect(pricingZonesPage.getSaveButtonDisplay()).toBe('disabled');
        expect(pricingZonesPage.getEditButtonDisplay()).toBe('disabled');
        expect(pricingZonesPage.getArchiveButtonDisplay()).toBe('disabled');
        expect(pricingZonesPage.getZoneNameDisplay()).not().toBe('disabled');
        expect(angularElement(pricingZonesPage.zoneName, 'Zone Name field')).toHaveClass('ng-invalid');
        expect(angularElement(pricingZonesPage.zoneName, 'Zone Name field')).toHaveClass('ng-invalid-required');
        expect(angularElement(pricingZonesPage.zoneName, 'Zone Name field')).toHaveClass('ng-pristine');
        expect(pricingZonesPage.getAddressGridRowsCount()).toBe(0);
        expect(pricingZonesPage.getZonesRowsCount()).toBe(0);
        pricingZonesPage.clickArchivedTab();
        expect(pricingZonesPage.getZonesRowsCount()).toBe(0);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});