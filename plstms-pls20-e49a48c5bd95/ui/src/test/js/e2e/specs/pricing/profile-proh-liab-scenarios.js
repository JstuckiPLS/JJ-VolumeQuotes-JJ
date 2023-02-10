/**
 * End to end scenarios for Pricing profile prohibited and liability functionality.
 * 
 * @author Ashwini Neelgund
 */
describe('Pricing profile prohibited and liability functionality', function() {

    var loginLogoutPageObject, pricingProfileDetailsPageObject, pricingTariffsPage, pricProfProhLiabPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricProfProhLiabPage = $injector.get('PricProfProhLiabPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should load prohibited commodities and Liabilities', function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(4);
        pricingTariffsPage.clickActiveTab();
        pricingTariffsPage.selectThirdRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingProfileDetailsPage.prohLiabTabSelector, 
               'check prohibited and liability tab exists').count()).toBe(1);
        element(pricingProfileDetailsPage.prohLiabTabSelector, 'go to prohibited and liability tab').click();
        expect(element(pricProfProhLiabPage.controller).count()).toBe(1);
        expect(pricProfProhLiabPage.getProhibCommdtyDisplay()).not().toBe('disabled');
        expect(pricProfProhLiabPage.getInputProhibCopyFromDisplay()).not().toBe('disabled');
        expect(pricProfProhLiabPage.getInputLiabCopyFromDisplay()).not().toBe('disabled');
        expect(pricProfProhLiabPage.getExternalNotesDisplay()).not().toBe('disabled');
        expect(pricProfProhLiabPage.getInternalNotesDisplay()).not().toBe('disabled');
        expect(pricProfProhLiabPage.getResetButtonDisplay()).not().toBe('disabled');
        expect(pricProfProhLiabPage.getSaveButtonDisplay()).not().toBe('disabled');
        expect(pricProfProhLiabPage.getCarrLiabRowsCount()).toBe(18);
        expect(pricProfProhLiabPage.getProhibitedCommodities()).toBe('Food items, Glass');
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});