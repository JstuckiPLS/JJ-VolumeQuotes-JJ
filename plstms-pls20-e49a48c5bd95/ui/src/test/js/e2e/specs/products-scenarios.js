/**
 * E2E scenarios for products functionality.
 * 
 * @author Denis Zhupinsky
 */
describe('Products pages scenarios. ', function() {

    function ProductEntry(description, nmfc, nmfcSubNum, commodityClass, productCode, hazmat, hazmatUnNumber, hazmatPackingGroup, hazmatClass,
            emergencyResponseCompany, emergencyResponsePhoneCountryCode, emergencyResponsePhoneAreaCode, emergencyResponsePhone,
            emergencyResponseContractNumber) {
        this.description = description;
        this.nmfc = nmfc;
        this.nmfcSubNum = nmfcSubNum;
        this.commodityClass = commodityClass;
        this.productCode = productCode;
        this.hazmat = hazmat;
        this.hazmatUnNumber = hazmatUnNumber;
        this.hazmatPackingGroup = hazmatPackingGroup;
        this.hazmatClass = hazmatClass;
        this.emergencyResponseCompany = emergencyResponseCompany;
        this.emergencyResponsePhoneCountryCode = emergencyResponsePhoneCountryCode;
        this.emergencyResponsePhoneAreaCode = emergencyResponsePhoneAreaCode;
        this.emergencyResponsePhone = emergencyResponsePhone;
        this.emergencyResponseContractNumber = emergencyResponseContractNumber;
    }

    function HazmatPhoneEntry(hazmat, emergencyResponsePhoneCountryCode, emergencyResponsePhoneAreaCode, emergencyResponsePhone) {
        this.hazmat = hazmat;
        this.emergencyResponsePhoneCountryCode = emergencyResponsePhoneCountryCode;
        this.emergencyResponsePhoneAreaCode = emergencyResponsePhoneAreaCode;
        this.emergencyResponsePhone = emergencyResponsePhone;
    }

    var productsPageObject = undefined, loginLogoutPageObject = undefined, expectedProduct = new ProductEntry('Bomb', 4645, 103, 77.5,
            '6V12588-RED-M', true, 'UN#1', '', '1', 'Google, Inc', '1', '123', '123 4567', '#3233'), hazmatPhoneEntity = new HazmatPhoneEntry(true,
            '1', '321', '7654321'), badHazmatPhoneEntity = new HazmatPhoneEntry(true, '12345', '12345', '123456789'),badCharHazmatPhoneEntity = new HazmatPhoneEntry(true, 'ghf', 'ret', 'qwertyyui');

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        productsPageObject = $injector.get('ProductsPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsCustomer);
    });

    it('should open products page', function() {
        browser().navigateTo('#/products/products-list');
        expect(browser().location().path()).toBe("/products/products-list");
    });

    it('test if dialog open and OK button validation(enabled\\disabled)', function() {
        var productDescription = expectedProduct.description + new Date().getTime();

        // Click 'Add' button -> Dialog should be opened
        expect(productsPageObject.addDialog.getDialogDisplay()).toBe("none");
        productsPageObject.listPage.clickAdd();
        expect(productsPageObject.addDialog.getDialogDisplay()).not().toBe("none");

        expect(angularElement(productsPageObject.addDialog.description, 'Product description input')).toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.description, 'Product description input')).toHaveClass('ng-invalid-required');

        expect(angularElement(productsPageObject.addDialog.commodityClass, 'Commodity class input')).toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.commodityClass, 'Commodity class input')).toHaveClass('ng-invalid-required');

        expect(angularElement(productsPageObject.addDialog.hazmatSectionDiv, 'Hazmat section div')).toHaveClass('well');

        // Start filling form -> 'OK' button should become enabled only when all required data is entered
        input(productsPageObject.addDialog.description).enter(productDescription);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        expect(angularElement(productsPageObject.addDialog.description, 'Product description input')).toHaveClass('ng-valid');
        expect(angularElement(productsPageObject.addDialog.description, 'Product description input')).toHaveClass('ng-valid-required');

        input(productsPageObject.addDialog.nmfc).enter(expectedProduct.nmfc);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(productsPageObject.addDialog.nmfcSubNum).enter(expectedProduct.nmfcSubNum);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        select(productsPageObject.addDialog.commodityClass).option(expectedProduct.commodityClass);
        // Required data is entered -> 'OK' should be enabled
        expect(productsPageObject.addDialog.getOkDisplay()).not().toBe("disabled");

        expect(angularElement(productsPageObject.addDialog.commodityClass, 'Commodity class input')).toHaveClass('ng-valid');
        expect(angularElement(productsPageObject.addDialog.commodityClass, 'Commodity class input')).toHaveClass('ng-valid-required');

        input(productsPageObject.addDialog.productCode).enter(expectedProduct.productCode);

        input(productsPageObject.addDialog.hazmat).check(expectedProduct.hazmat);
        // after checking hazmat checkbox - hazmat data need to be filled
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        expect(angularElement(productsPageObject.addDialog.hazmatUnNumber, 'Hazmat UN number input')).toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.hazmatClass, 'Hazmat class input')).toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponseCompany, 'Emergency response company input')).toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneAreaCode, 'Emergency response phone area code input')).toHaveClass(
                'ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhone, 'Emergency response phone number input'))
                .toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponseContractNumber, 'Emergency response contract number input')).toHaveClass(
                'ng-invalid');

        expect(angularElement(productsPageObject.addDialog.hazmatUnNumber, 'Hazmat UN number input')).toHaveClass('ng-invalid-required');
        expect(angularElement(productsPageObject.addDialog.hazmatClass, 'Hazmat class input')).toHaveClass('ng-invalid-required');
        expect(angularElement(productsPageObject.addDialog.emergencyResponseCompany, 'Emergency response company input')).toHaveClass(
                'ng-invalid-required');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneAreaCode, 'Emergency response phone area code input')).toHaveClass(
                'ng-invalid-required');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhone, 'Emergency response phone number input')).toHaveClass(
                'ng-invalid-required');
        expect(angularElement(productsPageObject.addDialog.emergencyResponseContractNumber, 'Emergency response contract number input')).toHaveClass(
                'ng-invalid-required');

        input(productsPageObject.addDialog.hazmatUnNumber).enter(expectedProduct.hazmatUnNumber);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(productsPageObject.addDialog.hazmatPackingGroup).enter(expectedProduct.hazmatPackingGroup);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        select(productsPageObject.addDialog.hazmatClass).option(expectedProduct.hazmatClass);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(productsPageObject.addDialog.emergencyResponseCompany).enter(expectedProduct.emergencyResponseCompany);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(productsPageObject.addDialog.emergencyResponsePhoneCountryCode).enter(expectedProduct.emergencyResponsePhoneCountryCode);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(productsPageObject.addDialog.emergencyResponsePhoneAreaCode).enter(expectedProduct.emergencyResponsePhoneAreaCode);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(productsPageObject.addDialog.emergencyResponsePhone).enter(expectedProduct.emergencyResponsePhone);
        expect(productsPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(productsPageObject.addDialog.emergencyResponseContractNumber).enter(expectedProduct.emergencyResponseContractNumber);
        // Required data is entered -> 'OK' should be enabled

        expect(productsPageObject.addDialog.getOkDisplay()).not().toBe("disabled");

        expect(angularElement(productsPageObject.addDialog.hazmatUnNumber, 'Hazmat UN number input')).toHaveClass('ng-valid');
        expect(angularElement(productsPageObject.addDialog.hazmatClass, 'Hazmat class input')).toHaveClass('ng-valid');
        expect(angularElement(productsPageObject.addDialog.hazmatUnNumber, 'Hazmat UN number input')).toHaveClass('ng-valid-required');
        expect(angularElement(productsPageObject.addDialog.hazmatClass, 'Hazmat class input')).toHaveClass('ng-valid-required');

        // Save product -> Dialog should be closed
        productsPageObject.addDialog.clickOk();
        expect(productsPageObject.addDialog.getDialogDisplay()).toBe("none");
    });

    it('should fill Details section', function() {
        loginLogoutPageObject.logout();
        loginLogoutPageObject.login(loginLogoutPageObject.plsCustomer);

        browser().navigateTo('#/products/products-list');
        // Select row in product grid -> Details section should be filled.
        expect(productsPageObject.listPage.getDetailsDescriptionText()).toBeFalsy();
        expect(productsPageObject.listPage.getDetailsCommodityClassText()).toBeFalsy();

        productsPageObject.listPage.selectLastRow();

        expect(productsPageObject.listPage.getDetailsDescriptionText()).toBeTruthy();
        expect(productsPageObject.listPage.getDetailsCommodityClassText()).toBeTruthy();
    });

    it('should test if Delete button works properly', function() {
        // Click 'Delete' btn -> confirmation dialog should be opened.
        productsPageObject.listPage.selectLastRow();
        productsPageObject.listPage.clickDelete();
        expect(productsPageObject.listPage.getDeleteDialogDisplay()).not().toBe("none");

        productsPageObject.listPage.clickDeleteOk();
        expect(productsPageObject.listPage.getDeleteDialogDisplay()).toBe("none");
    });

    it('should test if buttons Add, Edit, Delete, Import are disabled properly', function() {
        // test default 'disabled' state
        expect(productsPageObject.listPage.getAddDisplay()).not().toBe("disabled");
        expect(productsPageObject.listPage.getEditDisplay()).toBe("disabled");
        expect(productsPageObject.listPage.getDeleteDisplay()).toBe("disabled");
        expect(productsPageObject.listPage.getImportDisplay()).not().toBe("disabled");

        // test 'disabled' state after grid row selection
        productsPageObject.listPage.selectFirstRow();
        expect(productsPageObject.listPage.getAddDisplay()).not().toBe("disabled");
        expect(productsPageObject.listPage.getEditDisplay()).not().toBe("disabled");
        expect(productsPageObject.listPage.getDeleteDisplay()).not().toBe("disabled");
        expect(productsPageObject.listPage.getImportDisplay()).not().toBe("disabled");
    });

    it('test chek hazmatPhone-fields)', function() {
        // Click 'Add' button -> Dialog should be opened
        browser().navigateTo('#/products/products-list');
        expect(productsPageObject.addDialog.getDialogDisplay()).toBe("none");
        productsPageObject.listPage.clickAdd();
        expect(productsPageObject.addDialog.getDialogDisplay()).not().toBe("none");

        input(productsPageObject.addDialog.hazmat).check();
        // after checking hazmat checkbox - hazmat data need to be filled
        input(productsPageObject.addDialog.emergencyResponsePhoneCountryCode).enter('');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneCountryCode, 'Emergency response country code input')).toHaveClass(
                'ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneAreaCode, 'Emergency response phone area code input')).toHaveClass(
                'ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhone, 'Emergency response phone number input'))
                .toHaveClass('ng-invalid');

        input(productsPageObject.addDialog.emergencyResponsePhoneCountryCode).enter(hazmatPhoneEntity.emergencyResponsePhoneCountryCode);
        input(productsPageObject.addDialog.emergencyResponsePhoneAreaCode).enter(hazmatPhoneEntity.emergencyResponsePhoneAreaCode);
        input(productsPageObject.addDialog.emergencyResponsePhone).enter(hazmatPhoneEntity.emergencyResponsePhone);

        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneCountryCode)).toHaveClass('ng-valid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneAreaCode)).toHaveClass('ng-valid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhone)).toHaveClass('ng-valid');

        expect(element('#phoneCountryCodeInp').attr('maxlength')).toBe('3');
        expect(element('[data-ng-model="editProductModel.product.hazmatEmergencyPhone.areaCode"]').attr('maxlength')).toBe('3');
        expect(element('[data-ng-model="editProductModel.product.hazmatEmergencyPhone.number"]').attr('maxlength')).toBe('7');

        input(productsPageObject.addDialog.emergencyResponsePhoneCountryCode).enter(badHazmatPhoneEntity.emergencyResponsePhoneCountryCode);
        input(productsPageObject.addDialog.emergencyResponsePhoneAreaCode).enter(badHazmatPhoneEntity.emergencyResponsePhoneAreaCode);
        input(productsPageObject.addDialog.emergencyResponsePhone).enter(badHazmatPhoneEntity.emergencyResponsePhone);

        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneCountryCode)).toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneAreaCode)).toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhone)).toHaveClass('ng-invalid');

        expect(element('#phoneCountryCodeInp').attr('maxlength')).toBe('3');
        expect(element('[data-ng-model="editProductModel.product.hazmatEmergencyPhone.areaCode"]').attr('maxlength')).toBe('3');
        expect(element('[data-ng-model="editProductModel.product.hazmatEmergencyPhone.number"]').attr('maxlength')).toBe('7');

        input(productsPageObject.addDialog.emergencyResponsePhoneCountryCode).enter(badCharHazmatPhoneEntity.emergencyResponsePhoneCountryCode);
        input(productsPageObject.addDialog.emergencyResponsePhoneAreaCode).enter(badCharHazmatPhoneEntity.emergencyResponsePhoneAreaCode);
        input(productsPageObject.addDialog.emergencyResponsePhone).enter(badCharHazmatPhoneEntity.emergencyResponsePhone);

        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneCountryCode)).toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhoneAreaCode)).toHaveClass('ng-invalid');
        expect(angularElement(productsPageObject.addDialog.emergencyResponsePhone)).toHaveClass('ng-invalid');

        productsPageObject.addDialog.clickCancel();
        expect(productsPageObject.addDialog.getDialogDisplay()).toBe("none");
    });
    
    it('Should be present download template link.', function() {
      browser().navigateTo('#/products/products-list');
      expect(element(productsPageObject.listPage.templateDownloadLink).count()).toBe(1);
    });
    
    var productDescription = expectedProduct.description + new Date().getTime();
   it('Should save properly.', function() {
      loginLogoutPageObject.logout();
      loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
      browser().navigateTo('#/products/products-list');
      input(productsPageObject.selectedCustomer).enter('PLS SHIPPER');
      productsPageObject.listPage.clickAdd();
      input(productsPageObject.addDialog.description).enter(productDescription);
      select(productsPageObject.addDialog.commodityClass).option(expectedProduct.commodityClass);
      input(productsPageObject.addDialog.productCode).enter(expectedProduct.productCode);
      input(productsPageObject.addDialog.hazmat).check(expectedProduct.hazmat);
      input(productsPageObject.addDialog.hazmatUnNumber).enter(expectedProduct.hazmatUnNumber);
      input(productsPageObject.addDialog.hazmatPackingGroup).enter(expectedProduct.hazmatPackingGroup);
      select(productsPageObject.addDialog.hazmatClass).option(expectedProduct.hazmatClass);
      input(productsPageObject.addDialog.emergencyResponseCompany).enter(expectedProduct.emergencyResponseCompany);
      input(productsPageObject.addDialog.emergencyResponsePhoneCountryCode).enter(expectedProduct.emergencyResponsePhoneCountryCode);
      input(productsPageObject.addDialog.emergencyResponsePhoneAreaCode).enter(expectedProduct.emergencyResponsePhoneAreaCode);
      input(productsPageObject.addDialog.emergencyResponsePhone).enter(expectedProduct.emergencyResponsePhone);
      input(productsPageObject.addDialog.emergencyResponseContractNumber).enter(expectedProduct.emergencyResponseContractNumber);
      input(productsPageObject.addDialog.nmfc).enter(expectedProduct.nmfc);
      input(productsPageObject.addDialog.nmfcSubNum).enter(expectedProduct.nmfcSubNum);
      input(productsPageObject.addDialog.emergencyResponseInstructions).enter("instructions");
      productsPageObject.addDialog.clickOk();
      expect(productsPageObject.addDialog.getDialogDisplay()).toBe("none");
    });
    
   
    it('Should edit properly.', function() {
      setValue(productsPageObject.listPage.nameSearchField,productDescription);
      sleep(1);
      expect(element(productsPageObject.getJquerySelectorForProductDescription(0)).text()).toBe(productDescription);
      expect(element(productsPageObject.getJquerySelectorForNMFC(0)).text()).toBe(expectedProduct.nmfc+"-"+expectedProduct.nmfcSubNum);
      expect(element(productsPageObject.getJquerySelectorForClass(0)).text()).toBe("77.5");
      expect(element(productsPageObject.getJquerySelectorForProductCode(0)).text()).toBe("6V12588-RED-M");
      expect(element(productsPageObject.getHazmatCheckBoxSelectorForRow(0)).attr('class')).toEqual('fa fa-check ng-scope');
      productsPageObject.listPage.selectLastRow();
      expect(binding(productsPageObject.descriptionBinding)).toBe(productDescription);
      expect(binding(productsPageObject.nmfcBinding)).toBe(expectedProduct.nmfc+"-"+expectedProduct.nmfcSubNum);
      expect(binding(productsPageObject.commodityBinding)).toBe("77.5");
      expect(binding(productsPageObject.productCodeBinding)).toBe(expectedProduct.productCode);
      expect(element(productsPageObject.getHazmatCheckBoxSelectorForRow(0)).attr('class')).toEqual('fa fa-check ng-scope');
      expect(binding(productsPageObject.hazmatUnNumberBinding)).toBe(expectedProduct.hazmatUnNumber);
      expect(binding(productsPageObject.hazmatPackingGroupBinding)).toBe(expectedProduct.hazmatPackingGroup);
      expect(binding(productsPageObject.hazmatClassBinding)).toBe(expectedProduct.hazmatClass);
      expect(binding(productsPageObject.hazmatEmergencyBinding)).toBe(expectedProduct.emergencyResponseCompany);
      expect(binding(productsPageObject.hazmatEmergencyPhoneBinding)).toBe('+'+expectedProduct.emergencyResponsePhoneCountryCode+
          "("+expectedProduct.emergencyResponsePhoneAreaCode+")"+expectedProduct.emergencyResponsePhone);
      expect(binding(productsPageObject.hazmatEmergencyContractBinding)).toBe(expectedProduct.emergencyResponseContractNumber);
      expect(input(productsPageObject.hazmatInstructionsBinding).val()).toBe("instructions");
      productsPageObject.listPage.clickEdit();
    });
    
    it('Should delete properly.', function() {
      var newProductDescription = productDescription + "(edited)";
      input(productsPageObject.addDialog.description).enter(newProductDescription);
      select(productsPageObject.addDialog.commodityClass).option(50);
      expect(input(productsPageObject.hazmatInstructions).val()).toBe("instructions");
      productsPageObject.addDialog.clickOk();
      expect(element(productsPageObject.getJquerySelectorForProductDescription(0)).text()).toBe(newProductDescription);
      expect(element(productsPageObject.getJquerySelectorForNMFC(0)).text()).toBe(expectedProduct.nmfc+"-"+expectedProduct.nmfcSubNum);
      expect(element(productsPageObject.getJquerySelectorForClass(0)).text()).toBe("50");
      expect(element(productsPageObject.getJquerySelectorForProductCode(0)).text()).toBe(expectedProduct.productCode);
      expect(element(productsPageObject.getHazmatCheckBoxSelectorForRow(0)).attr('class')).toEqual('fa fa-check ng-scope');
      productsPageObject.listPage.selectLastRow();
      productsPageObject.listPage.clickDelete();
      expect(productsPageObject.listPage.getDeleteDialogDisplay()).not().toBe("none");
      productsPageObject.listPage.clickDeleteOk();
      expect(productsPageObject.listPage.getDeleteDialogDisplay()).toBe("none");
      sleep(1);
      expect(productsPageObject.getGridRowCount()).toBe(0);
    });
    
    it('Should appears data in grid.', function() {
      loginLogoutPageObject.logout();
      loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
      browser().navigateTo('#/products/products-list');
      setValue(productsPageObject.listPage.nameSearchField,"");
      input(productsPageObject.selectedCustomer).enter("PLS SHIPPER");
      sleep(1);
      expect(productsPageObject.getGridRowCount()).toBe(17);
    });
    
    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });

});
