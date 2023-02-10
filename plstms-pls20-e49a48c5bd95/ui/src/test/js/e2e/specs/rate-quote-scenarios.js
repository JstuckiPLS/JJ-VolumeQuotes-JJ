/**
 * E2E scenarios for Rate Quote page.
 *
 * @author Denis Zhupinsky
 */
describe('Rate quote page scenarios. ', function () {

    function MaterialEntry(weight, qty, commodityClass, packaginGroup, stackable) {
        this.weight = weight;
        this.qty = qty;
        this.commodityClass = commodityClass;
        this.packaginGroup = packaginGroup;
        this.stackable = stackable;
    }

    function addMaterial() {
        rateQuotePageObject.setWeight(material.weight);
        rateQuotePageObject.setCommodityClass(material.commodityClass);
        rateQuotePageObject.setQuantity(material.qty);
        rateQuotePageObject.setPackageType(material.packaginGroup);
        rateQuotePageObject.setStackable(material.stackable);
        rateQuotePageObject.selectProduct('Budweiser 24545214SKU');

        rateQuotePageObject.addButtonClick();
    }

    var rateQuotePageObject = undefined,
            loginLogoutPageObject = undefined,
            selectCarrierPageObject = undefined,
            material = new MaterialEntry('10', '4645', '85', 'Boxes', "checked");

    beforeEach(function () {
        $injector = angular.injector(['PageObjectsModule']);
        rateQuotePageObject = $injector.get('RateQuotePageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        selectCarrierPageObject = $injector.get('SelectCarrierPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsCustomer);
    });

    it('should open start quote page', function() {
        browser().navigateTo('#/quotes/quote');
        expect(browser().location().path()).toBe("/quotes/quote");
    });

    it('should show required fields', function () {
        expect(angularElement(rateQuotePageObject.originZipSelector, 'Origin Zip field')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.originZipSelector, 'Origin Zip field')).toHaveClass('ng-invalid-required');

        expect(angularElement(rateQuotePageObject.destinationZipSelector, 'Destination Zip field')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.destinationZipSelector, 'Destination Zip field')).toHaveClass('ng-invalid-required');

        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-invalid-required');

        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-invalid-required');

        rateQuotePageObject.setOriginZip('65790');

        expect(angularElement(rateQuotePageObject.originZipSelector, 'Origin Zip field')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.originZipSelector, 'Origin Zip field')).toHaveClass('ng-valid-required');

        rateQuotePageObject.setDestinationZip('45320');
        expect(angularElement(rateQuotePageObject.destinationZipSelector, 'Destination Zip field')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.destinationZipSelector, 'Destination Zip field')).toHaveClass('ng-valid-required');

        rateQuotePageObject.setWeight(1);
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-valid-required');

        rateQuotePageObject.setCommodityClass(50);
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-valid-required');
    });

    it('should validate add materials button (enabled\disabled) and required fields', function () {
        rateQuotePageObject.setWeight('');
        rateQuotePageObject.setCommodityClass('');
        expect(rateQuotePageObject.getAddButtonDisabled()).toBe("disabled");

        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-invalid-required');

        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-invalid-required');

        rateQuotePageObject.setWeight('Wrong value');
        expect(rateQuotePageObject.getAddButtonDisabled()).toBe("disabled");

        rateQuotePageObject.setWeight(1);
        rateQuotePageObject.setCommodityClass('85');
        rateQuotePageObject.selectProduct('Budweiser 24545214SKU');

        expect(rateQuotePageObject.getAddButtonDisabled()).not().toBe("disabled");

        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-valid-required');

        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-valid-required');

        rateQuotePageObject.setQuantity('');
        expect(angularElement(rateQuotePageObject.quantityModel, 'Material quantity input')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.quantityModel, 'Material quantity input')).toHaveClass('ng-valid-required');
      
        rateQuotePageObject.setQuantity("Wrong value");
        expect(rateQuotePageObject.getAddButtonDisabled()).toBe("disabled");
        expect(angularElement(rateQuotePageObject.quantityModel, 'Material quantity input')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.quantityModel, 'Material quantity input')).toHaveClass('ng-invalid-format');

        rateQuotePageObject.setQuantity(1);
        expect(angularElement(rateQuotePageObject.quantityModel, 'Material quantity input')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.quantityModel, 'Material quantity input')).toHaveClass('ng-valid-required');
    });

    it('should add material to material grid', function () {
        expect(rateQuotePageObject.getMaterialsRowsCount()).toBe(0);
        addMaterial();

        expect(rateQuotePageObject.getMaterialsRowsCount()).toBe(1);
    });

    it('should remove material from material grid and check disabled status for remove button', function () {
        expect(rateQuotePageObject.getMaterialsRowsCount()).toBe(1);

        expect(rateQuotePageObject.getRemoveButtonDisabled()).toBe("disabled");

        rateQuotePageObject.selectFirstRow();
        expect(rateQuotePageObject.getRemoveButtonDisabled()).not().toBe("disabled");

        rateQuotePageObject.removeButtonClick();

        expect(rateQuotePageObject.getMaterialsRowsCount()).toBe(0);
        expect(rateQuotePageObject.getRemoveButtonDisabled()).toBe("disabled");
    });

    it('should edit material at material grid  and check disabled status for edit button', function () {
        expect(input(rateQuotePageObject.weightModel).val()).toBeFalsy();
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-invalid-required');

        expect(rateQuotePageObject.getSelectedCommodityClass()).toBeFalsy();
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-invalid-required');

        expect(rateQuotePageObject.getSelectedPackagingType()).toBeFalsy();
        expect(input(rateQuotePageObject.stackableModel).check()).toBeFalsy();

        addMaterial();

        expect(rateQuotePageObject.getMaterialsRowsCount()).toBe(1);
        rateQuotePageObject.selectFirstRow();

        expect(rateQuotePageObject.getEditButtonDisabled()).not().toBe("disabled");

        rateQuotePageObject.editButtonClick();

        expect(rateQuotePageObject.getMaterialsRowsCount()).toBe(0);
        expect(rateQuotePageObject.getEditButtonDisabled()).toBe("disabled");

        expect(input(rateQuotePageObject.weightModel).val()).toEqual(material.weight);
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-valid-required');

        expect(rateQuotePageObject.getSelectedCommodityClass()).toBeTruthy(material.commodityClass);
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-valid');
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-valid-required');

        expect(input(rateQuotePageObject.quantityModel).val()).toEqual(material.qty);
        expect(rateQuotePageObject.getSelectedPackagingType()).toBeTruthy(material.packaginGroup);
        expect(element(rateQuotePageObject.stackableCheckbox).attr("checked")).toBe(material.stackable);
    });

    it('should clear all data after Clear All button click', function () {
        rateQuotePageObject.setOriginZip('16821');
        rateQuotePageObject.setDestinationZip('16820');

        addMaterial();

        rateQuotePageObject.setWeight(material.weight);
        rateQuotePageObject.setCommodityClass(material.commodityClass);
        rateQuotePageObject.setQuantity(material.qty);
        rateQuotePageObject.setPackageType(material.packaginGroup);
        rateQuotePageObject.setStackable(material.stackable);
        rateQuotePageObject.clearAllButtonClick();

        expect(input(rateQuotePageObject.originZipModel)).toBeFalsy();
        expect(input(rateQuotePageObject.destinationZipModel)).toBeFalsy();

        expect(angularElement(rateQuotePageObject.originZipSelector, 'Origin Zip field')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.originZipSelector, 'Origin Zip field')).toHaveClass('ng-invalid-required');

        expect(angularElement(rateQuotePageObject.destinationZipSelector, 'Destination Zip field')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.destinationZipSelector, 'Destination Zip field')).toHaveClass('ng-invalid-required');

        expect(input(rateQuotePageObject.weightModel).val()).toBeFalsy();
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.weightModel, 'Material weight input')).toHaveClass('ng-invalid-required');

        expect(rateQuotePageObject.getSelectedCommodityClass()).toBeFalsy();
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-invalid');
        expect(angularElement(rateQuotePageObject.commodityClassModel, 'Material commodity class select')).toHaveClass('ng-invalid-required');

        expect(rateQuotePageObject.getSelectedPackagingType()).toBeFalsy();
        expect(input(rateQuotePageObject.stackableModel).check()).toBeFalsy();
    });

    it('should copy shipment', function () {
        expect(input(rateQuotePageObject.originZipModel)).toBeFalsy();
        expect(input(rateQuotePageObject.destinationZipModel)).toBeFalsy();
        expect(rateQuotePageObject.getCopyFromDialogDisplay()).toBe("none");
        rateQuotePageObject.copyFromButtonClick();
        expect(rateQuotePageObject.getCopyFromDialogDisplay()).not().toBe("none");
        expect(rateQuotePageObject.getCopyLastButton().attr('disabled')).toBe('disabled');
        element('[data-ng-grid="copyFromGridOptions"]', 'find last shipment\' table').query(function(customersGrid, done) {
            var rows = customersGrid.find('div[ng-repeat="row in renderedRows"]');
            if (rows.length > 0) {
                rows[0].click();
                done();
            } else {
                done('Failed to edit bill to address. There has been no customers created so far.');
            }
        });

        expect(rateQuotePageObject.getCopyLastButton().attr('disabled')).not().toBe('disabled');
        rateQuotePageObject.getCopyLastButton().click();
        sleep(1);
        expect(rateQuotePageObject.getCopyFromDialogDisplay()).toBe("none");
        expect(rateQuotePageObject.getMaterialsRowsCount()).toBeGreaterThan(0);
        rateQuotePageObject.clickGetQuote();

        expect(element('[data-ng-controller="SelectCarrierCtrl"]', 'Div with ng-controller="CarrierCtrl"').count()).toBe(1);
        selectCarrierPageObject.clickBack();
    });

    it('should open "Add Product" dialog', function () {
        expect(rateQuotePageObject.getAddProductDialogDisplay()).toBe("none");
        rateQuotePageObject.addProductButtonClick();
        expect(rateQuotePageObject.getAddProductDialogDisplay()).not().toBe("none");
        rateQuotePageObject.clickAddProdCancelButton();
    });

    it('should add hazmat product', function () {
        rateQuotePageObject.clearAllButtonClick();
        rateQuotePageObject.setWeight('42');
        rateQuotePageObject.setCommodityClass('85');
        rateQuotePageObject.setHazmat('checked');
        rateQuotePageObject.selectProduct('Budweiser 24545214SKU');
        rateQuotePageObject.addButtonClick();

        sleep(1);
        expect(rateQuotePageObject.getMaterialsRowsCount()).toBe(1);
        expect(rateQuotePageObject.getHazmatIconsCount()).toBe(1);
        expect(rateQuotePageObject.getHazmatIconsDisplay()).not().toBe('none');

        browserTrigger(rateQuotePageObject.getMaterialsRows(), 'mouseover');
    });

    it('should open select carrier page after successful Get Quote click', function () {
    	rateQuotePageObject.clearAllButtonClick();
        rateQuotePageObject.setOriginZip('16821');
        rateQuotePageObject.setDestinationZip('16820');
        rateQuotePageObject.setWeight(1000);
        rateQuotePageObject.setCommodityClass(6);
        rateQuotePageObject.selectProduct('Budweiser 24545214SKU');
        rateQuotePageObject.clickGetQuote();

        expect(element('[data-ng-controller="SelectCarrierCtrl"]', 'Div with ng-controller="SelectCarrierCtrl"').count()).toBe(1);
    });

    it('user should not be able to book load with proposal marked as "Exclude from booking"', function () {
        expect(element('[data-ng-controller="SelectCarrierCtrl"]', 'Div with ng-controller="SelectCarrierCtrl"').count()).toBe(1);
        expect(selectCarrierPageObject.getPropositionsGridRowCount()).toBeGreaterThan(9);
        selectCarrierPageObject.clickBlockedRow();
        expect(selectCarrierPageObject.getBookButtonDisplay()).toBe('disabled');
        expect(selectCarrierPageObject.getSaveQuoteButtonDisplay()).not().toBe('disabled');
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});
