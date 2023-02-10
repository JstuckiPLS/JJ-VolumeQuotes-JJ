/**
 * End to end scenarios for fuel tab functionality.
 * 
 * @author Ashwini Neelgund
 */
describe('Fuel tab functionality', function() {

    var fuelTabPage, loginLogoutPageObject;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        fuelTabPage = $injector.get('FuelTabPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function() {
        //update regions
        browser().navigateTo('/restful/test/runScheduledTask?beanName=com.pls.scheduler.service.FuelRatesScheduler&methodName=receiveFuelRates');
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should load the fuel tab details', function() {
        browser().navigateTo('#/pricing/fuel');
        expect(browser().location().path()).toBe("/pricing/fuel");
        expect(element(fuelTabPage.controller).count()).toBe(1);
        expect(fuelTabPage.getFuelUpdateDisplay()).not().toBe('disabled');
        expect(fuelTabPage.getSaveButtonDisplay()).not().toBe('disabled');
        expect(fuelTabPage.getDateRangeDisplay()).not().toBe('disabled');
        expect(fuelTabPage.getStartDateDisplay()).toBe('disabled');
        expect(fuelTabPage.getEndDateDisplay()).toBe('disabled');
        fuelTabPage.clickGetFuelUpdateButton();
        expect(fuelTabPage.getFuelGridRowsCount()).toBe(11);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});