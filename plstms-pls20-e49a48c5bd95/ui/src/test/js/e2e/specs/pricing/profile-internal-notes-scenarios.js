/**
 * End to end scenarios for Pricing profile internal notes functionality.
 * 
 * @author Ashwini Neelgund
 */
describe('Pricing profile internal notes functionality', function() {

    var loginLogoutPageObject, pricingProfileDetailsPageObject, pricingTariffsPage, pricProfProhLiabPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        profInternalNotesPage = $injector.get('ProfInternalNotesPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should load pricing profile's internal notes", function() {
        browser().navigateTo('#/pricing/tariffs/active');
        sleep(4);
        pricingTariffsPage.selectThirdRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingProfileDetailsPage.internalNotesTabSelector, 
               'check internal notes tab exists').count()).toBe(1);
        element(pricingProfileDetailsPage.internalNotesTabSelector, 'go to internal notes tab').click();
        expect(element(profInternalNotesPage.controller).count()).toBe(1);
        expect(profInternalNotesPage.getInputElementDisplay()).not().toBe('disabled');
        expect(profInternalNotesPage.getSaveButtonDisplay()).toBe('disabled');
        expect(angularElement(profInternalNotesPage.inputElement, 'Note field')).toHaveClass('ng-invalid');
        expect(angularElement(profInternalNotesPage.inputElement, 'Note field')).toHaveClass('ng-invalid-required');
        expect(angularElement(profInternalNotesPage.inputElement, 'Note field')).toHaveClass('ng-pristine');
        profInternalNotesPage.setInputElement("pricing internal notes for carrier SAIA");
        expect(angularElement(profInternalNotesPage.inputElement, 'Note field')).toHaveClass('ng-valid');
        expect(angularElement(profInternalNotesPage.inputElement, 'Note field')).toHaveClass('ng-valid-required');
        expect(angularElement(profInternalNotesPage.inputElement, 'Note field')).toHaveClass('ng-dirty');
        expect(profInternalNotesPage.getSaveButtonDisplay()).not().toBe('disabled');
        expect(profInternalNotesPage.getInternalNotesRowsCount()).toBe(0);
        profInternalNotesPage.clickSaveButton();
        expect(angularElement(profInternalNotesPage.inputElement, 'Note field')).toHaveClass('ng-invalid');
        expect(angularElement(profInternalNotesPage.inputElement, 'Note field')).toHaveClass('ng-invalid-required');
        expect(angularElement(profInternalNotesPage.inputElement, 'Note field')).toHaveClass('ng-dirty');
        expect(profInternalNotesPage.getSaveButtonDisplay()).toBe('disabled');
        expect(profInternalNotesPage.getInternalNotesRowsCount()).toBe(1);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});