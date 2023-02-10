/**
 * End to end scenarios for Pricing tariffs functionality.
 * 
 * @author Ashwini Neelgund
 */
describe('Pricing tariffs functionality', function() {

    var pricingTariffsPage, loginLogoutPageObject, pricingProfileDetailsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should display active and archived carrier tariffs of all pricing types', function() {
        browser().navigateTo('#/pricing/tariffs/active');
        expect(element(pricingTariffsPage.controller).count()).toBe(1);
        expect(pricingTariffsPage.getReactivateButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getAddButtonDisplay()).not().toBe('disabled');
        expect(pricingTariffsPage.getViewOrEditButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getCopyFromButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getCarrierTariffsRowsCount()).toBeGreaterThan(14);
        pricingTariffsPage.clickArchivedTab();
        expect(pricingTariffsPage.getReactivateButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getAddButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getViewOrEditButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getCopyFromButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getCarrierTariffsRowsCount()).toBe(4);
    });

    it('should filter on active tab by Type field', function() {
        browser().navigateTo('#/pricing/tariffs/active');
        pricingTariffsPage.clickActiveTab();
        pricingTariffsPage.setFilterType('Blanket/CSP');
        sleep(1);
        expect(pricingTariffsPage.getCarrierTariffsRowsCount()).toBe(2);
        pricingTariffsPage.setFilterType('');
    });

    it('should reactivate archived carrier tariff', function() {
        pricingTariffsPage.clickArchivedTab();
        expect(pricingTariffsPage.getReactivateButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getAddButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getViewOrEditButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getCopyFromButtonDisplay()).toBe('disabled');
        pricingTariffsPage.selectFirstRow();
        expect(pricingTariffsPage.getReactivateButtonDisplay()).not().toBe('disabled');
        expect(pricingTariffsPage.getAddButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getViewOrEditButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getCopyFromButtonDisplay()).toBe('disabled');
        pricingTariffsPage.clickReactivateButton();
        expect(pricingTariffsPage.getCarrierTariffsRowsCount()).toBe(3);
        expect(pricingTariffsPage.getReactivateButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getAddButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getViewOrEditButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getCopyFromButtonDisplay()).toBe('disabled');
    });

    it('should inactivate active carrier tariff', function() {
        pricingTariffsPage.clickActiveTab();
        expect(pricingTariffsPage.getReactivateButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getAddButtonDisplay()).not().toBe('disabled');
        expect(pricingTariffsPage.getViewOrEditButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getCopyFromButtonDisplay()).toBe('disabled');
        pricingTariffsPage.selectReactivatedRow();
        expect(pricingTariffsPage.getReactivateButtonDisplay()).not().toBe('disabled');
        expect(pricingTariffsPage.getAddButtonDisplay()).not().toBe('disabled');
        expect(pricingTariffsPage.getViewOrEditButtonDisplay()).not().toBe('disabled');
        expect(pricingTariffsPage.getCopyFromButtonDisplay()).not().toBe('disabled');
        pricingTariffsPage.clickReactivateButton();
        expect(pricingTariffsPage.getReactivateButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getAddButtonDisplay()).not().toBe('disabled');
        expect(pricingTariffsPage.getViewOrEditButtonDisplay()).toBe('disabled');
        expect(pricingTariffsPage.getCopyFromButtonDisplay()).toBe('disabled');
        pricingTariffsPage.clickArchivedTab();
        expect(pricingTariffsPage.getCarrierTariffsRowsCount()).toBe(4);
    });

    it('should open new pricing profile details page', function() {
        pricingTariffsPage.clickActiveTab();
        expect(pricingTariffsPage.getAddButtonDisplay()).not().toBe('disabled');
        pricingTariffsPage.clickAddButton();
        expect(element(pricingProfileDetailsPage.profileDetailsController).count()).toBe(1);
        expect(angularElement(pricingProfileDetailsPage.rate, 'Rate field')).toHaveClass('ng-invalid');
        expect(angularElement(pricingProfileDetailsPage.rate, 'Rate field')).toHaveClass('ng-invalid-required');
        expect(angularElement(pricingProfileDetailsPage.rate, 'Rate field')).toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.pricingTypeDropdown, 'Pricing type field')).
              toHaveClass('ng-invalid');
        expect(angularElement(pricingProfileDetailsPage.pricingTypeDropdown, 'Pricing type field')).
              toHaveClass('ng-invalid-required');
        expect(angularElement(pricingProfileDetailsPage.pricingTypeDropdown, 'Pricing type field')).
              toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.scac, 'SCAC field')).toHaveClass('ng-invalid');
        expect(angularElement(pricingProfileDetailsPage.scac, 'SCAC field')).toHaveClass('ng-invalid-required');
        expect(angularElement(pricingProfileDetailsPage.scac, 'SCAC field')).toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.carrierRatingType, 'Carrier Rating Type field')).
              toHaveClass('ng-invalid');
        expect(angularElement(pricingProfileDetailsPage.carrierRatingType, 'Carrier Rating Type field')).
              toHaveClass('ng-invalid-required');
        expect(angularElement(pricingProfileDetailsPage.carrierRatingType, 'Carrier Rating Type field')).
              toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.effectiveDate, 'Effective Date field')).toHaveClass('ng-invalid');
        expect(angularElement(pricingProfileDetailsPage.effectiveDate, 'Effective Date field')).
              toHaveClass('ng-invalid-required');
        pricingProfileDetailsPage.clickCancelButton();
        expect(element(pricingTariffsPage.controller).count()).toBe(1);
    });

    it('should open view/edit pricing profile details page', function() {
        pricingTariffsPage.selectFirstRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingProfileDetailsPage.profileDetailsController).count()).toBe(1);
        expect(angularElement(pricingProfileDetailsPage.rate, 'Rate field')).toHaveClass('ng-valid');
        expect(angularElement(pricingProfileDetailsPage.rate, 'Rate field')).toHaveClass('ng-valid-required');
        expect(angularElement(pricingProfileDetailsPage.rate, 'Rate field')).toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.pricingTypeDropdown, 'Pricing type field')).
              toHaveClass('ng-valid');
        expect(angularElement(pricingProfileDetailsPage.pricingTypeDropdown, 'Pricing type field')).
              toHaveClass('ng-valid-required');
        expect(angularElement(pricingProfileDetailsPage.pricingTypeDropdown, 'Pricing type field')).
              toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.scac, 'SCAC field')).toHaveClass('ng-valid');
        expect(angularElement(pricingProfileDetailsPage.scac, 'SCAC field')).toHaveClass('ng-valid-required');
        expect(angularElement(pricingProfileDetailsPage.scac, 'SCAC field')).toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.carrierRatingType, 'Carrier Rating Type field')).
              toHaveClass('ng-valid');
        expect(angularElement(pricingProfileDetailsPage.carrierRatingType, 'Carrier Rating Type field')).
              toHaveClass('ng-valid-required');
        expect(angularElement(pricingProfileDetailsPage.carrierRatingType, 'Carrier Rating Type field')).
              toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.effectiveDate, 'Effective Date field')).toHaveClass('ng-valid');
        expect(angularElement(pricingProfileDetailsPage.effectiveDate, 'Effective Date field')).
              toHaveClass('ng-valid-required');
        expect(pricingProfileDetailsPage.getScac()).toBe('FXNL:FEDEX FREIGHT ECONOMY');
        expect(pricingProfileDetailsPage.getRateName()).toBe('FXNL PLS SHIPPER');
        pricingProfileDetailsPage.clickCancelButton();
        expect(element(pricingTariffsPage.controller).count()).toBe(1);
    });

    it('should copy from selected tariffs to new pricing profile details page', function() {
        pricingTariffsPage.selectFirstRow();
        pricingTariffsPage.clickCopyFromButton();
        expect(element(pricingProfileDetailsPage.profileDetailsController).count()).toBe(1);
        expect(angularElement(pricingProfileDetailsPage.rate, 'Rate field')).toHaveClass('ng-invalid');
        expect(angularElement(pricingProfileDetailsPage.rate, 'Rate field')).toHaveClass('ng-invalid-required');
        expect(angularElement(pricingProfileDetailsPage.rate, 'Rate field')).toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.pricingTypeDropdown, 'Pricing type field')).
              toHaveClass('ng-valid');
        expect(angularElement(pricingProfileDetailsPage.pricingTypeDropdown, 'Pricing type field')).
              toHaveClass('ng-valid-required');
        expect(angularElement(pricingProfileDetailsPage.pricingTypeDropdown, 'Pricing type field')).
              toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.scac, 'SCAC field')).toHaveClass('ng-valid');
        expect(angularElement(pricingProfileDetailsPage.scac, 'SCAC field')).toHaveClass('ng-valid-required');
        expect(angularElement(pricingProfileDetailsPage.scac, 'SCAC field')).toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.carrierRatingType, 'Carrier Rating Type field')).
              toHaveClass('ng-valid');
        expect(angularElement(pricingProfileDetailsPage.carrierRatingType, 'Carrier Rating Type field')).
              toHaveClass('ng-valid-required');
        expect(angularElement(pricingProfileDetailsPage.carrierRatingType, 'Carrier Rating Type field')).
              toHaveClass('ng-pristine');
        expect(angularElement(pricingProfileDetailsPage.effectiveDate, 'Effective Date field')).toHaveClass('ng-valid');
        expect(angularElement(pricingProfileDetailsPage.effectiveDate, 'Effective Date field')).
              toHaveClass('ng-valid-required');
        expect(pricingProfileDetailsPage.getScac()).toBe('FXNL:FEDEX FREIGHT ECONOMY');
        expect(pricingProfileDetailsPage.getRateName()).toBe('');
        pricingProfileDetailsPage.clickCancelButton();
        expect(element(pricingTariffsPage.controller).count()).toBe(1);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});