describe('New Quote No Transit Time functionality', function() {
    var $injector, loginLogoutPageObject, startQuotePageObject, selectCarrierPageObject;
    var pricingTerminalPage, pricingProfileDetailsPage, pricingTariffsPage;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        startQuotePageObject = $injector.get('RateQuotePageObject');
        selectCarrierPageObject = $injector.get('SelectCarrierPageObject');

        pricingTariffsPage = $injector.get('PricingTariffsPageObject');
        pricingProfileDetailsPage = $injector.get('PricingProfileDetailsPageObject');
        pricingTerminalPage = $injector.get('PricingTerminalPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it("should change carrier rating type and terminal for pricing profile", function() {
        browser().navigateTo('#/pricing/tariffs/active');

        pricingTariffsPage.setFilterID(38);
        sleep(3);
        pricingTariffsPage.selectFirstRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingProfileDetailsPage.profileDetailsController, 'check profile details controller exists').count()).toBe(1);
        pricingProfileDetailsPage.setCarrierRatingType('Manual');
        pricingProfileDetailsPage.clickSaveButton();
        expect(element(pricingProfileDetailsPage.rdwyPricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingProfileDetailsPage.rdwyPricingTabSelector, 'go to pricing tab').click();

        expect(element(pricingTerminalPage.rdwyTerminalTabSelector, 'check pricing terminal tab exists').count()).toBe(1);
        element(pricingTerminalPage.rdwyTerminalTabSelector, 'go to pricing terminal tab').click();
        expect(element(pricingTerminalPage.controller).count()).toBe(1);
        expect(pricingTerminalPage.getTransiteTime()).toBe('2');
        pricingTerminalPage.setTransiteTime('');
        pricingTerminalPage.clickSaveButton();
    });

    it('should get quote without transit time', function() {
        browser().navigateTo('#/quotes/quote');
        expect(browser().location().path()).toBe("/quotes/quote");

        startQuotePageObject.setOriginZip('43210');
        startQuotePageObject.setDestinationZip('44136');
        startQuotePageObject.setWeight(1000);
        startQuotePageObject.setCommodityClass(6);
        startQuotePageObject.clickGetQuote();
        sleep(3);
        expect(element(selectCarrierPageObject.selectCarrierControllerSelector, 'Div with ng-controller="SelectCarrierCtrl"').count()).toBe(1);
        expect(selectCarrierPageObject.getRoadWayCarrierName()).toEqual("ROADWAY EXPRESS");
        expect(selectCarrierPageObject.getRoadWayTransitDate()).toEqual("N/A");
        expect(selectCarrierPageObject.getRoadWayTransitTime()).toEqual("N/A");
    });

    it("should change carrier rating type and terminal back for pricing profile", function() {
        disableLocationChangeCheck();
        browser().navigateTo('#/pricing/tariffs/active');

        pricingTariffsPage.setFilterID(38);
        sleep(1);
        pricingTariffsPage.selectFirstRow();
        pricingTariffsPage.clickViewOrEditButton();
        expect(element(pricingProfileDetailsPage.profileDetailsController, 'check profile details controller exists').count()).toBe(1);
        pricingProfileDetailsPage.setCarrierRatingType('SMC3');
        pricingProfileDetailsPage.clickSaveButton();
        expect(element(pricingProfileDetailsPage.rdwyPricingTabSelector, 'check pricing tab exists').count()).toBe(1);
        element(pricingProfileDetailsPage.rdwyPricingTabSelector, 'go to pricing tab').click();

        expect(element(pricingTerminalPage.rdwyTerminalTabSelector, 'check pricing terminal tab exists').count()).toBe(1);
        element(pricingTerminalPage.rdwyTerminalTabSelector, 'go to pricing terminal tab').click();
        expect(element(pricingTerminalPage.controller).count()).toBe(1);
        expect(pricingTerminalPage.getTransiteTime()).toBe('');
        pricingTerminalPage.setTransiteTime('2');
        pricingTerminalPage.clickSaveButton();
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});