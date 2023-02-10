describe('Class estimator functionality', function() {
    var loginLogoutPageObject, startQuotePageObject, plsClassEstimatorPageObject, userMgtPageObject;

    function checkAllElementsExist() {
        expect(element(plsClassEstimatorPageObject.calcTypeSelector).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.calcTypeLabel).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.qtyLabel).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.totalWeightLabel).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.qtyInput).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.totalWeightInput).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.dimensionsLabel).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.lengthInput).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.widthInput).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.heightInput).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.weightUnitSelect).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.dimensionUnitSelect).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.addButton).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.classEstimatorGrid).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.deleteRowButton).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.clearGridButton).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.closeButton).count()).toBeGreaterThan(0);
        expect(element(plsClassEstimatorPageObject.okButton).count()).toBeGreaterThan(0);
    }

    function checkAllElementsState() {
        expect(element(plsClassEstimatorPageObject.qtyInput).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.calcTypeSelector).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.totalWeightInput).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.lengthInput).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.heightInput).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.widthInput).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.weightUnitSelect).attr('disabled')).toBe('disabled');
        expect(element(plsClassEstimatorPageObject.dimensionUnitSelect).attr('disabled')).toBe('disabled');
        expect(element(plsClassEstimatorPageObject.addButton).attr('disabled')).toBe('disabled');
        expect(element(plsClassEstimatorPageObject.deleteRowButton).attr('disabled')).toBe('disabled');
        expect(element(plsClassEstimatorPageObject.clearGridButton).attr('disabled')).toBe('disabled');
        expect(element(plsClassEstimatorPageObject.okButton).attr('disabled')).toBe('disabled');
        expect(element(plsClassEstimatorPageObject.closeButton).attr('disabled')).not().toBeDefined();
    }

    function fillAllRequiredFields() {
        plsClassEstimatorPageObject.setTotalWeight(100);
        plsClassEstimatorPageObject.setLength(33);
        plsClassEstimatorPageObject.setWidth(33);
        plsClassEstimatorPageObject.setHeight(33);
    }
    beforeEach(function() {
        $injector = angular.injector(['PageObjectsModule']);
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        plsClassEstimatorPageObject = $injector.get('PlsClassEstimatorPageObject');
        rateQuotePageObject = $injector.get('RateQuotePageObject');
        userMgtPageObject = $injector.get("UserMgtPageObject");
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should open Class estimator and close it', function() {
        browser().navigateTo('#/quotes/quote');
        expect(browser().location().path()).toBe("/quotes/quote");
        rateQuotePageObject.openDensityClassEstimator();
        checkAllElementsExist();
        checkAllElementsState();
        expect(plsClassEstimatorPageObject.getSelectedCalculationType()).toBeTruthy("Scaffolding");

        expect(element(plsClassEstimatorPageObject.qtyInput).val()).toEqual('1');
        expect(element(plsClassEstimatorPageObject.totalWeightInput).val()).toEqual('');
        expect(element(plsClassEstimatorPageObject.lengthInput).val()).toEqual('');
        expect(element(plsClassEstimatorPageObject.heightInput).val()).toEqual('');
        expect(element(plsClassEstimatorPageObject.widthInput).val()).toEqual('');

        fillAllRequiredFields();
        expect(plsClassEstimatorPageObject.getGridRowCount()).toEqual(0);
        element(plsClassEstimatorPageObject.addButton).click();
        expect(plsClassEstimatorPageObject.getGridRowCount()).toBeGreaterThan(0);
        plsClassEstimatorPageObject.clearGrid();
        expect(plsClassEstimatorPageObject.getGridRowCount()).toEqual(0);

        fillAllRequiredFields();
        element(plsClassEstimatorPageObject.addButton).click();

        element(plsClassEstimatorPageObject.closeButton).click();
        expect(rateQuotePageObject.getSelectedCommodityClass()).toBe('');
    });

    it('should check Clear grid buttons work', function() {
        browser().navigateTo('#/quotes/quote');
        expect(browser().location().path()).toBe("/quotes/quote");
        rateQuotePageObject.openDensityClassEstimator();

        fillAllRequiredFields();
        expect(plsClassEstimatorPageObject.getGridRowCount()).toEqual(0);
        element(plsClassEstimatorPageObject.addButton).click();
        expect(plsClassEstimatorPageObject.getGridRowCount()).toEqual(2);
        plsClassEstimatorPageObject.clearGrid();
        expect(plsClassEstimatorPageObject.getGridRowCount()).toEqual(0);

        fillAllRequiredFields();
        expect(plsClassEstimatorPageObject.getGridRowCount()).toEqual(0);
        element(plsClassEstimatorPageObject.addButton).click();
        expect(plsClassEstimatorPageObject.getGridRowCount()).toEqual(2);
        plsClassEstimatorPageObject.selectFirstRow();
        element(plsClassEstimatorPageObject.deleteRowButton).click();
        expect(plsClassEstimatorPageObject.getGridRowCount()).toEqual(1);

        element(plsClassEstimatorPageObject.closeButton).click();
        expect(rateQuotePageObject.getSelectedCommodityClass()).toBe('');
    });

    it('should open Class estimator and calculate class', function() {
        browser().navigateTo('#/quotes/quote');
        expect(browser().location().path()).toBe("/quotes/quote");
        rateQuotePageObject.openDensityClassEstimator();

        checkAllElementsExist();
        checkAllElementsState();

        expect(plsClassEstimatorPageObject.getSelectedCalculationType()).toBeTruthy("Standard");

        fillAllRequiredFields();

        expect(element(plsClassEstimatorPageObject.addButton).attr('disabled')).not().toBeDefined();
        element(plsClassEstimatorPageObject.addButton).click();

        expect(element(plsClassEstimatorPageObject.totalWeightInput).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.lengthInput).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.widthInput).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.heightInput).attr('disabled')).not().toBeDefined();
        expect(element(plsClassEstimatorPageObject.addButton).attr('disabled')).toBe('disabled');
        expect(element(plsClassEstimatorPageObject.deleteRowButton).attr('disabled')).toBe('disabled');

        expect(plsClassEstimatorPageObject.getGridRowCount()).toBeGreaterThan(0);

        plsClassEstimatorPageObject.clickOkButton();
        expect(rateQuotePageObject.getSelectedCommodityClass()).toBeTruthy('85');
    });
});