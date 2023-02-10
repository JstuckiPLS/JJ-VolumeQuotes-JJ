/**
 * End to end scenarios for Pricing terminal functionality.
 * 
 * @author Ashwini Neelgund
 */
describe('Pricing terminal functionality', function() {

    var pricingTerminalPage, pricingPage, pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricingTerminalPage = $injector.get('PricingTerminalPageObject');
        pricingPage = $injector.get('PricingPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should load the pricing terminal details", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        pricingTariffsPage.selectFifthRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingPage.pricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingPage.pricingTabSelector, 'go to pricing tab').click();
        expect(element(pricingTerminalPage.terminalTabSelector, 'check pricing terminal tab exists').count()).toBe(1);
        element(pricingTerminalPage.terminalTabSelector, 'go to pricing terminal tab').click();
        expect(element(pricingTerminalPage.controller).count()).toBe(1);
        expect(pricingTerminalPage.getSaveButtonDisplay()).not().toBe('disabled');
        expect(pricingTerminalPage.getCancelButtonDisplay()).not().toBe('disabled');
        expect(pricingTerminalPage.getCopyFromDisplay()).not().toBe('disabled');
        expect(pricingTerminalPage.getTerminalDisplay()).not().toBe('disabled');
        expect(pricingTerminalPage.getContactDisplay()).not().toBe('disabled');
        expect(pricingTerminalPage.getCountryDisplay()).not().toBe('disabled');
        expect(pricingTerminalPage.getAddress1Display()).not().toBe('disabled');
        expect(pricingTerminalPage.getTerminal()).toBe('SOME TERMINAL35');
        expect(pricingTerminalPage.getContact()).toBe('SOME NAME35');
        expect(pricingTerminalPage.getCountry()).toBe('0');
        expect(pricingTerminalPage.getAddress1()).toBe('818 LOWELL ST');
        expect(pricingTerminalPage.getZip()).toBe('ELYRIA, OH, 44035');
        expect(pricingTerminalPage.getTransiteTime()).toBe('2');
        expect(pricingTerminalPage.getPhNumber()).toBe('1377065');
        expect(pricingTerminalPage.getFaxNumber()).toBe('9361244');
        expect(pricingTerminalPage.getBlockDisplay()).toBe('on');
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});