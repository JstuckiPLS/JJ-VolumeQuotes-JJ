/**
 * End to end scenarios for Pricing profile details functionality.
 * 
 * @author Ashwini Neelgund
 */
describe('Pricing profile details functionality', function() {

    var pricingProfileDetailsPageObject, loginLogoutPageObject, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should save new active blanket pricing profile details', function() {
        browser().navigateTo('#/pricing/tariffs/active');
        pricingTariffsPage.clickAddButton();
        expect(element(pricingProfileDetailsPage.profileDetailsController).count()).toBe(1);
        pricingProfileDetailsPage.setRateName('ABFS Blanket 1');
        pricingProfileDetailsPage.setPricingType('Blanket');
        pricingProfileDetailsPage.setScac('ABFS:ABF Freight');
        pricingProfileDetailsPage.setCarrierRatingType('SMC3');
        pricingProfileDetailsPage.setMileageCalc('MileMaker 1');
        pricingProfileDetailsPage.setSmc3Tariff('ABF 504, US/US_20020801');
        pricingProfileDetailsPage.setActiveCheckbox('checked');
        pricingProfileDetailsPage.setEffectiveDate('11/01/2010');
        pricingProfileDetailsPage.setExpiryDate('11/01/2025');
        pricingProfileDetailsPage.setProfileNotes('This is blanket pricing profile for carrier ABFS');
        console.log(pricingProfileDetailsPage.pricingTypeValue);
        pricingProfileDetailsPage.clickSaveButton();
        pricingProfileDetailsPage.clickBackToTariffsButton();
        pricingTariffsPage.clickActiveTab();
        expect(element(pricingTariffsPage.controller).count()).toBe(1);
        expect(element(pricingTariffsPage.getJquerySelectorForScac(0)).text()).toBe('ABFS');
        expect(element(pricingTariffsPage.getJquerySelectorForRateName(0)).text()).toBe('ABFS Blanket 1');
    });

    it('should enable to edit existing pricing profile details', function() {
        pricingTariffsPage.selectFirstRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingProfileDetailsPage.profileDetailsController).count()).toBe(1);
        expect(pricingProfileDetailsPage.getScac()).toBe('ABFS:ABF Freight');
        expect(pricingProfileDetailsPage.getRateName()).toBe('ABFS Blanket 1');
        expect(pricingProfileDetailsPage.getActiveCheckbox()).toBe('on');
        pricingProfileDetailsPage.setActiveCheckbox('unchecked');
        pricingProfileDetailsPage.clickSaveButton();
        pricingProfileDetailsPage.clickBackToTariffsButton();
        pricingTariffsPage.clickArchivedTab();
        expect(element(pricingTariffsPage.controller).count()).toBe(1);
        expect(pricingTariffsPage.getCarrierTariffsRowsCount()).toBe(5);
    });

    it('should allow to add/edit applicable customers for CSP, blanket/CSP or buy/sell pricing profiles', function() {
        pricingTariffsPage.clickActiveTab();
        pricingTariffsPage.selectSecondRow();
        pricingTariffsPage.clickCopyFromButton();
        pricingProfileDetailsPage.setPricingType('Blanket/CSP');
        expect(pricingProfileDetailsPage.getApplicableCustomersCount()).toBe(0);
        pricingProfileDetailsPage.clickApplicableCustomersButton();
        expect(pricingProfileDetailsPage.getAddAllButtonDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getAddCustomersBtnDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getRemoveAllButtonDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getRemoveCustomersBtnDisplay()).toBe('disabled');
        pricingProfileDetailsPage.setCustomerFilter('PLS');
        expect(pricingProfileDetailsPage.getAssignedCustRowsCount()).toBe(1);
        expect(pricingProfileDetailsPage.getAddAllButtonDisplay()).not().toBe('disabled');
        expect(pricingProfileDetailsPage.getAddCustomersBtnDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getRemoveAllButtonDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getRemoveCustomersBtnDisplay()).toBe('disabled');
        pricingProfileDetailsPage.clickAddAllButton();
        expect(pricingProfileDetailsPage.getAssignedCustRowsCount()).toBe(0);
        expect(pricingProfileDetailsPage.getApplicableCustGridRowsCount()).toBe(1);
        expect(pricingProfileDetailsPage.getAddAllButtonDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getAddCustomersBtnDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getRemoveAllButtonDisplay()).not().toBe('disabled');
        expect(pricingProfileDetailsPage.getRemoveCustomersBtnDisplay()).toBe('disabled');
        pricingProfileDetailsPage.clickRemoveAllButton();
        expect(pricingProfileDetailsPage.getAddAllButtonDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getAddCustomersBtnDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getRemoveAllButtonDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getRemoveCustomersBtnDisplay()).toBe('disabled');
        pricingProfileDetailsPage.setCustomerFilter('PL');
        expect(pricingProfileDetailsPage.getAssignedCustRowsCount()).toBe(1);
        expect(pricingProfileDetailsPage.getAddCustomersBtnDisplay()).toBe('disabled');
        pricingProfileDetailsPage.selectAssignedCustFirstRow();
        expect(pricingProfileDetailsPage.getAddCustomersBtnDisplay()).not().toBe('disabled');
        pricingProfileDetailsPage.clickAddCustomersButton();
        expect(pricingProfileDetailsPage.getAssignedCustRowsCount()).toBe(0);
        expect(pricingProfileDetailsPage.getApplicableCustGridRowsCount()).toBe(1);
        expect(pricingProfileDetailsPage.getRemoveCustomersBtnDisplay()).toBe('disabled');
        pricingProfileDetailsPage.clickAssignedCustSaveButton();
        expect(pricingProfileDetailsPage.getApplicableCustomersCount()).toBe(1);
        pricingProfileDetailsPage.clickApplicableCustomersButton();
        pricingProfileDetailsPage.selectApplicableCustFirstRow();
        expect(pricingProfileDetailsPage.getRemoveCustomersBtnDisplay()).not().toBe('disabled');
        pricingProfileDetailsPage.clickRemoveCustomersButton();
        pricingProfileDetailsPage.clickAssignedCustSaveButton();
        expect(pricingProfileDetailsPage.getApplicableCustomersCount()).toBe(0);
    });

    it('should enable to copy from existing pricing blanket profile details to new blanket/CSP profile', function() {
        pricingProfileDetailsPage.setPricingType('Blanket/CSP');
        expect(pricingProfileDetailsPage.getScacDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getCarrierRatingTypeDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getMileageCalcDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getSmc3TariffDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getActiveCheckboxDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getEffectiveDateDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getExpiryDateDisplay()).toBe('disabled');
        expect(pricingProfileDetailsPage.getProfileNotesDisplay()).toBe('disabled');
        pricingProfileDetailsPage.setRateName('Blanket/CSP SAIA 1');
        pricingProfileDetailsPage.clickApplicableCustomersButton();
        pricingProfileDetailsPage.setCustomerFilter('PLS');
        pricingProfileDetailsPage.clickAddAllButton();
        pricingProfileDetailsPage.clickAssignedCustSaveButton();
        pricingProfileDetailsPage.clickSaveButton();
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});