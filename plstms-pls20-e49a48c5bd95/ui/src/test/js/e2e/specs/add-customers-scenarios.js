describe('Add Customer functionality', function() {

    var $injector, activeCustomersPageObject, addCustomersPageObject, loginLogoutPageObject;

    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        activeCustomersPageObject = $injector.get('ActiveCustomersPageObject');
        addCustomersPageObject = $injector.get('AddCustomersPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('Should navigate to Customer page', function() {
        browser().navigateTo('#/customer/active');
        expect(browser().location().path()).toBe("/customer/active");
        expect(element(activeCustomersPageObject.customerSearchControllerSelector, 'Div with ng-controller="CustomerSearchCtrl"').count()).toBe(1);
    });

    it('Should open to addCustomer', function() {
        expect(element(addCustomersPageObject.addCustomerControllerSelector, 'Div with ng-controller="AddCustomerCtrl"').count()).toBe(1);
        expect(element(addCustomersPageObject.addCustomerDetailsControllerSelector, 'Div with ng-controller="AddCustomerDetailsCtrl"').count()).toBe(0);

        activeCustomersPageObject.clickAddButton();

        expect(element(addCustomersPageObject.addCustomerDetailsControllerSelector, 'Div with ng-controller="AddCustomerDetailsCtrl"').count()).toBe(1);
    });

    it('should add new Customer', function() {
        var name = 'Test Customer' + new Date().getTime();
        //random taxid in format ##-#######
        var taxId = _.random(10, 99) + '-' + _.random(1000000, 9999999);
        var ediNum = _.random(1,999999);
        addCustomersPageObject.setTaxIDInput("20-9548043");
        addCustomersPageObject.checkGenerateConsigneeInvoice();
        expect(getText(addCustomersPageObject.labelGenerateConsigneeInvoice)).toBe('Generate Consignee Invoice');
        addCustomersPageObject.setCustomerInput("PLS SHIPPER");
        addCustomersPageObject.setAddress1Input('test Adress');
        addCustomersPageObject.setZipInput('CONCRETE, WA, 98237');
        addCustomersPageObject.setFirstNameInput('FirstName');
        addCustomersPageObject.setLastNameInput('LastName');
        addCustomersPageObject.setPhoneAreaCodeInput('333');
        addCustomersPageObject.setPhoneNumberInput('3334355');
        addCustomersPageObject.setEmailInput('testmail@pls.com');
        addCustomersPageObject.selectBusinessUnitInput("LTL (LT)");
        expect(addCustomersPageObject.getCompanyCode().text()).toEqual("LTL (LT)");
        expect(addCustomersPageObject.getCostCenter().text()).toEqual("Pitt1");
        addCustomersPageObject.selectAccountExecutiveInput('admin sysadmin');
        addCustomersPageObject.setEdiNumInput("8388820158");
        addCustomersPageObject.setLocationInput("location1");
        expect(addCustomersPageObject.getBackBtnDisplay()).toBe("none");
        expect(element(addCustomersPageObject.buttonNext, 'check next button is enabled').attr('disabled')).not().toBeDefined();

        addCustomersPageObject.clickNextButton();
        expect(element(addCustomersPageObject.buttonCopyFromCustomerContact, 'customer name already exist')).not().toBeDefined();
        addCustomersPageObject.setCustomerInput(name);
        addCustomersPageObject.clickNextButton();
        expect(element(addCustomersPageObject.buttonCopyFromCustomerContact, 'tax id already exis')).not().toBeDefined();
        addCustomersPageObject.setTaxIDInput(taxId);
        addCustomersPageObject.clickNextButton();
        expect(element(addCustomersPageObject.buttonCopyFromCustomerContact, 'edi# already exist')).not().toBeDefined();
        addCustomersPageObject.setEdiNumInput(ediNum);
        addCustomersPageObject.clickNextButton();
        
        expect(addCustomersPageObject.getBackBtnDisplay()).toBe("inline-block");
        addCustomersPageObject.clickCopyFromCustomerContactButton();
        addCustomersPageObject.setBillToNameInput("Bill To");

        expect(element(addCustomersPageObject.buttonNext, 'check next button is enabled').attr('disabled')).not().toBeDefined();
        addCustomersPageObject.clickNextButton();

        addCustomersPageObject.setCurrencyCodeInput("USD");
        addCustomersPageObject.setProcessTime("04:00 AM");
        addCustomersPageObject.checkedEdiInvoice();

        expect(element(addCustomersPageObject.buttonNext, 'check next button is enabled').attr('disabled')).not().toBeDefined();
        addCustomersPageObject.clickNextButton();

        expect(element(addCustomersPageObject.buttonNext, 'check next button is enabled').attr('disabled')).not().toBeDefined();
        addCustomersPageObject.clickNextButton();

        expect(element(addCustomersPageObject.buttonNext, 'check next button is enabled').attr('disabled')).not().toBeDefined();
        addCustomersPageObject.clickNextButton();

        expect(element(addCustomersPageObject.buttonNext, 'check next button is enabled').attr('disabled')).not().toBeDefined();
        addCustomersPageObject.clickNextButton();

        expect(element(addCustomersPageObject.buttonNext, 'check next button is enabled').attr('disabled')).not().toBeDefined();
        addCustomersPageObject.clickNextButton();

        expect(element(addCustomersPageObject.buttonDone, 'check done button is enabled').attr('disabled')).not().toBeDefined();
        addCustomersPageObject.clickDoneButton();

        expect(element(activeCustomersPageObject.customerSearchControllerSelector, 'Div with ng-controller="CustomerCtrl"').count()).toBe(1);
        activeCustomersPageObject.searchForCustomers('Test*');

        element('[data-ng-grid = "customersGrid"]', 'check Grid').query(function(elements, done) {
            var el = elements.find('div:contains('+name+')');
            if (el.length > 0) {
                done();
            } else {
                done('Client is not added');
            }
        });
    });

    it('Should Business unit present.', function() {
        addCustomersPageObject.selectLastRow();
        activeCustomersPageObject.clickEditButton();
        var textPromise = element('#businessUnit').query(function(nameElement, done) {
            var text = nameElement.text();
            done(null, text);
        });
        expect(textPromise).toBe("LTL (LT)");
        expect(isChecked('#generateConsigneeInvoice')).toBe(true);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});