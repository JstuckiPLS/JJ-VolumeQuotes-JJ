/**
 * End to end scenarios for Pricing freight bill pay to functionality.
 * 
 * @author Ashwini Neelgund
 */
describe('Pricing freight bill pay to functionality', function() {

    var pricFreightBillPayToPage, pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricFreightBillPayToPage = $injector.get('PricFreightBillPayToPageObject');
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should load the pricing freight bill pay to details", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(4);
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricFreightBillPayToPage.freightBillPayToTabSelector, 'check pricing freight bill pay to tab exists').count()).toBe(1);
        element(pricFreightBillPayToPage.freightBillPayToTabSelector, 'go to pricing freight bill pay to tab').click();
        expect(element(pricFreightBillPayToPage.controller).count()).toBe(1);
        expect(pricFreightBillPayToPage.getSaveButtonDisplay()).not().toBe('disabled');
        expect(pricFreightBillPayToPage.getCancelButtonDisplay()).not().toBe('disabled');
        expect(pricFreightBillPayToPage.getCopyFromDisplay()).not().toBe('disabled');
        expect(pricFreightBillPayToPage.getCompanyDisplay()).not().toBe('disabled');
        expect(pricFreightBillPayToPage.getContactNameDisplay()).not().toBe('disabled');
        expect(pricFreightBillPayToPage.getCountryDisplay()).not().toBe('disabled');
        expect(pricFreightBillPayToPage.getAddress1Display()).not().toBe('disabled');
        expect(pricFreightBillPayToPage.getCompany()).toBe('SOME COMPANY36');
        expect(pricFreightBillPayToPage.getContactName()).toBe('TEST USER');
        expect(pricFreightBillPayToPage.getCountry()).toBe('0');
        expect(pricFreightBillPayToPage.getAddress1()).toBe('3120 Unionville Rd');
        expect(pricFreightBillPayToPage.getZip()).toBe('CRANBERRY TWP, PA, 16066');
        expect(pricFreightBillPayToPage.getEmail()).toBe('a@test.com');
        expect(pricFreightBillPayToPage.getPhNumber()).toBe('5849000');
        expect(pricFreightBillPayToPage.getFaxNumber()).toBe('1234567');
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});