/**
 * End to end scenarios for pricing accessorials functionality.
 * 
 * @author Ashwini Neelgund
 */
describe("Pricing accessorials functionality", function() {

    var pricingAccPage, pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricingAccPage = $injector.get('PricingAccPageObject');
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    xit("should load the pricing's accessorial data", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(4);
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricingAccPage.accTabSelector, 'check pricing accessorial tab exists').count()).toBe(1);
        element(pricingAccPage.accTabSelector, 'go to pricing accessorial tab').click();
        expect(element(pricingAccPage.controller).count()).toBe(1);
        expect(pricingAccPage.getAccessorialTypeDisplay()).not().toBe('disabled');
        expect(angularElement(pricingAccPage.accessorialType, 'Accessorial field')).toHaveClass('ng-invalid');
        expect(angularElement(pricingAccPage.accessorialType, 'Accessorial field')).toHaveClass('ng-invalid-required');
        expect(angularElement(pricingAccPage.accessorialType, 'Accessorial field')).toHaveClass('ng-pristine');
        expect(pricingAccPage.getSetZipButtonDisplay()).not().toBe('disabled');
        expect(pricingAccPage.getEditZipButtonDisplay()).toBe('disabled');
        expect(pricingAccPage.getDeleteZipButtonDisplay()).toBe('disabled');
        expect(pricingAccPage.getClearButtonDisplay()).not().toBe('disabled');
        expect(pricingAccPage.getSaveAsNewButtonDisplay()).toBe('disabled');
        expect(pricingAccPage.getSaveButtonDisplay()).toBe('disabled');
        expect(pricingAccPage.getEditButtonDisplay()).toBe('disabled');
        expect(pricingAccPage.getExpireButtonDisplay()).toBe('disabled');
        expect(pricingAccPage.getArchiveButtonDisplay()).toBe('disabled');
        expect(pricingAccPage.getAccDataRowsCount()).toBe(5);
        pricingAccPage.clickExpiredTab();
        expect(pricingAccPage.getAccDataRowsCount()).toBe(0);
        pricingAccPage.clickArchivedTab();
        expect(pricingAccPage.getAccDataRowsCount()).toBe(0);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});