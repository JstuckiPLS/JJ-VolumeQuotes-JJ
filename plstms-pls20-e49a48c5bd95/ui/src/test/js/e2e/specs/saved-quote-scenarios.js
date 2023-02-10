/**
 * This scenario checks Saved Quote functionality.
 * 
 * @author Aleksandr Leshchenko
 */
describe('Saved Quote functionality', function() {

    var rateQuotePageObject, selectCarrierPageObject, savedQuotesPageObject, loginLogoutPageObject;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        rateQuotePageObject = $injector.get('RateQuotePageObject');
        selectCarrierPageObject = $injector.get('SelectCarrierPageObject');
        savedQuotesPageObject = $injector.get('SavedQuotesPageObject');

        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsCustomer);
    });

    it('should save quote with customer', function() {
        browser().navigateTo('#/quotes/quote');
        rateQuotePageObject.setCustomer("PLS SHIPPER");
        rateQuotePageObject.setOriginZip('16821');
        rateQuotePageObject.setDestinationZip('16820');
        rateQuotePageObject.setWeight(123);
        rateQuotePageObject.setCommodityClass('85');
        rateQuotePageObject.selectProduct('Budweiser 24545214SKU');
        rateQuotePageObject.clickGetQuote();

        expect(element(selectCarrierPageObject.selectCarrierControllerSelector, 'Div with ng-controller="SelectCarrierCtrl"').count()).toBe(1);
        var carrierPropsRows = element(selectCarrierPageObject.carrierPropositionsRowSelector, 'Carrier propositions table rows');

        element(selectCarrierPageObject.carrierPropositionsThirdRowSelector).click();
        expect(element(selectCarrierPageObject.saveQuoteButtonEnabledSelector, 'Enabled Save Quote buttons').count()).toBe(1);

        selectCarrierPageObject.clickSaveQuote();
        expect(element(selectCarrierPageObject.quoteIdSelector, 'Quote ID input').prop('required')).not().toBeTruthy();
        var quoteId = 'QId' + new Date().toISOString();
        selectCarrierPageObject.setQuoteId(quoteId);
        expect(angularElement(selectCarrierPageObject.quoteIdSelector, 'Quote ID input')).toHaveClass('ng-valid');
        selectCarrierPageObject.clickOkButton();

        element(selectCarrierPageObject.carrierPropositionsThirdRowSelector).click();

        disableLocationChangeCheck();
        browser().navigateTo('#/quotes/saved');

        savedQuotesPageObject.searchByQuoteId(quoteId);
        savedQuotesPageObject.clickSearchButton();
        expect(element(savedQuotesPageObject.quoteRowSelector, 'Quote should be saved').count()).toBe(1);
        savedQuotesPageObject.clickFirstRow();
    });

    it('should save quote without customer', function() {
        browser().navigateTo('#/quotes/quote');
        rateQuotePageObject.setOriginZip('16821');
        rateQuotePageObject.setDestinationZip('16820');
        rateQuotePageObject.setWeight(123);
        rateQuotePageObject.setCommodityClass('85');
        rateQuotePageObject.selectProduct('Budweiser 24545214SKU');
        rateQuotePageObject.clickGetQuote();

        expect(element(selectCarrierPageObject.selectCarrierControllerSelector, 'Div with ng-controller="SelectCarrierCtrl"').count()).toBe(1);
        var carrierPropsRows = element(selectCarrierPageObject.carrierPropositionsRowSelector, 'Carrier propositions table rows');

        element(selectCarrierPageObject.carrierPropositionsThirdRowSelector).click();
        expect(element(selectCarrierPageObject.saveQuoteButtonEnabledSelector, 'Enabled Save Quote buttons').count()).toBe(1);

        selectCarrierPageObject.clickSaveQuote();
        expect(element(selectCarrierPageObject.quoteIdSelector, 'Quote ID input').prop('required')).not().toBeTruthy();
        var quoteId = 'QId' + new Date().toISOString();
        selectCarrierPageObject.setQuoteId(quoteId);
        expect(angularElement(selectCarrierPageObject.quoteIdSelector, 'Quote ID input')).toHaveClass('ng-valid');
        selectCarrierPageObject.clickOkButton();

        element(selectCarrierPageObject.carrierPropositionsThirdRowSelector).click();

        disableLocationChangeCheck();
        browser().navigateTo('#/quotes/saved');
        savedQuotesPageObject.searchByQuoteId(quoteId);
        savedQuotesPageObject.clickSearchButton();
        savedQuotesPageObject.clickFirstRow();
        expect(element(savedQuotesPageObject.saveQuoteModalSelector, 'div containing data-pls-modal="showSaveQuoteDialog"').count()).toBe(1);
        expect(selectCarrierPageObject.getBuildOrderButtonDisplay()).toBe('disabled');
    });

    it('user should be able to save proposal marked as "Exclude from booking" but not be able to book load with it ', function () {
        browser().navigateTo('#/quotes/quote');
        rateQuotePageObject.setOriginZip('16821');
        rateQuotePageObject.setDestinationZip('16820');
        rateQuotePageObject.setWeight(1000);
        rateQuotePageObject.setCommodityClass(6);
        rateQuotePageObject.selectProduct('Budweiser 24545214SKU');
        rateQuotePageObject.clickGetQuote();
        expect(element('[data-ng-controller="SelectCarrierCtrl"]', 'Div with ng-controller="SelectCarrierCtrl"').count()).toBe(1);
        expect(selectCarrierPageObject.getPropositionsGridRowCount()).toBeGreaterThan(9);
        selectCarrierPageObject.clickBlockedRow();
        expect(selectCarrierPageObject.getBookButtonDisplay()).toBe('disabled');
        expect(selectCarrierPageObject.getSaveQuoteButtonDisplay()).not().toBe('disabled');
        selectCarrierPageObject.clickSaveQuote();
        var quoteId = 'buildDisabled' + new Date().getTime();
        selectCarrierPageObject.setQuoteId(quoteId);
        selectCarrierPageObject.clickOkButton();
        selectCarrierPageObject.clickBack();
        rateQuotePageObject.clearAllButtonClick();
        browser().navigateTo('#/quotes/saved');
        savedQuotesPageObject.clickSearchButton();
        savedQuotesPageObject.searchByQuoteId(quoteId);
        savedQuotesPageObject.clickFirstRow();
        savedQuotesPageObject.clickViewButton();
        expect(selectCarrierPageObject.getBuildOrderButtonDisplay()).toBe('disabled');
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});