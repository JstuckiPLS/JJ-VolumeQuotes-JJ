describe('Edit Customer. Add bill To scenarios', function() {
    var addressName = 'ZTest Bill To' + new Date().getTime();
    var $injector, loginLogoutPageObject, editCustomersPageObject, billToCustomerPageObject, customerInvoicesPreferencesPageObject,
                    customerReqFieldsPageObject, customerInvoicesReqDocsPageObject, customerDefaultValuesPageObject;

    function checkPristineIdentifier(isPro) {
        expect(isChecked(customerReqFieldsPageObject.identifierRequired)).toBe(isPro ? true : false);
        expect(getOptionText(customerReqFieldsPageObject.identifierIO)).toEqual('Both');
        expect(getOptionValue(customerReqFieldsPageObject.identifierIO)).toEqual('B');
        expect(getValue(customerReqFieldsPageObject.identifierZip)).toEqual('');
        expect(getValue(customerReqFieldsPageObject.identifierCity)).toEqual('');
        expect(getValue(customerReqFieldsPageObject.identifierState)).toEqual('');
        expect(getValue(customerReqFieldsPageObject.identifierCountry)).toEqual('');
        expect(getOptionText(customerReqFieldsPageObject.identifierOD)).toEqual('Both');
        expect(getOptionValue(customerReqFieldsPageObject.identifierOD)).toEqual('B');
        expect(getValue(customerReqFieldsPageObject.identifierDefaultValue)).toEqual('');
        expect(getValue(customerReqFieldsPageObject.identifierStartWith)).toEqual('');
        expect(getValue(customerReqFieldsPageObject.identifierEndWith)).toEqual('');
        expect(getOptionText(customerReqFieldsPageObject.identifierAction)).toEqual('');
    }

    function startEditIdentifier() {
        expect(element(customerReqFieldsPageObject.addButton, 'check add button is not disabled').attr('disabled')).not().toBeDefined();
        expect(element(customerReqFieldsPageObject.editButton, 'check add button is not disabled').attr('disabled')).not().toBeDefined();
        expect(element(customerReqFieldsPageObject.deleteButton, 'check add button is disabled').attr('disabled')).toBe('disabled');
        expect(isElementVisible(customerReqFieldsPageObject.identifierDialog)).toBe(false);
        customerReqFieldsPageObject.editReqField();
        expect(isElementVisible(customerReqFieldsPageObject.identifierDialog)).toBe(true);
        expect(getText(customerReqFieldsPageObject.identifierDialogHeader)).toEqual('Edit Identifier');
        expect(element(customerReqFieldsPageObject.identifierName, 'check identifier select is disabled').attr('disabled')).toBe('disabled');
    }

    function setIdentifierValues(isPro) {
        if (!isPro) {
            customerReqFieldsPageObject.IdentifierRequired().click();
        }
        customerReqFieldsPageObject.selectIdentifierIO('I');
        customerReqFieldsPageObject.setIdentifierZip('01010, 43210');
        customerReqFieldsPageObject.setIdentifierCity('new york, odessa');
        customerReqFieldsPageObject.setIdentifierState('mi, il');
        customerReqFieldsPageObject.setIdentifierCountry('usa');
        customerReqFieldsPageObject.selectIdentifierOD('O');
        customerReqFieldsPageObject.setIdentifierDefaultValue('123');
        customerReqFieldsPageObject.setIdentifierStartWith('1');
        customerReqFieldsPageObject.setIdentifierEndWith('3');
        customerReqFieldsPageObject.selectIdentifierAction('A');
    }

    function checkSavedIdentifiers() {
        expect(isChecked(customerReqFieldsPageObject.identifierRequired)).toBe(true);
        expect(getOptionText(customerReqFieldsPageObject.identifierIO)).toEqual('Inbound');
        expect(getOptionValue(customerReqFieldsPageObject.identifierIO)).toEqual('I');
        expect(getValue(customerReqFieldsPageObject.identifierZip)).toEqual('01010, 43210');
        expect(getValue(customerReqFieldsPageObject.identifierCity)).toEqual('NEW YORK, ODESSA');
        expect(getValue(customerReqFieldsPageObject.identifierState)).toEqual('MI, IL');
        expect(getValue(customerReqFieldsPageObject.identifierCountry)).toEqual('USA');
        expect(getOptionText(customerReqFieldsPageObject.identifierOD)).toEqual('Origin');
        expect(getOptionValue(customerReqFieldsPageObject.identifierOD)).toEqual('O');
        expect(getValue(customerReqFieldsPageObject.identifierDefaultValue)).toEqual('123');
        expect(getValue(customerReqFieldsPageObject.identifierStartWith)).toEqual('1');
        expect(getValue(customerReqFieldsPageObject.identifierEndWith)).toEqual('3');
        expect(getOptionValue(customerReqFieldsPageObject.identifierAction)).toEqual('A');
    }

    beforeEach(function() {
        $injector = angular.injector(['PageObjectsModule']);

        editCustomersPageObject = $injector.get('EditCustomersPageObject');
        billToCustomerPageObject = $injector.get('BillToCustomersPageObject');
        customerInvoicesPreferencesPageObject = $injector.get('CustomerInvoicesPreferencesPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        customerReqFieldsPageObject = $injector.get('CustomerReqFieldsPageObject');
        customerInvoicesReqDocsPageObject = $injector.get('CustomerInvoicesReqDocsPageObject');
        customerDefaultValuesPageObject = $injector.get('CustomerDefaultValuesPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should open customer bill to screen', function() {
        browser().navigateTo('#/customer/active');

        setValue(editCustomersPageObject.inputSearchByCustomerSelector, "pls*");
        element(editCustomersPageObject.searchButtonSelector, 'click search button').click();
        sleep(1);
        expect(element(editCustomersPageObject.gridRow).count()).toBe(1);
        editCustomersPageObject.selectFirstRow();

        expect(element(editCustomersPageObject.editButtonSelector, 'check edit customer button exists').count()).toBe(1);
        element(editCustomersPageObject.editButtonSelector, 'click edit button').click();

        expect(element(editCustomersPageObject.editCustomerControllerSelector, 'div containing data-ng-controller="EditCustomerCtrl"').count()).toBe(1);
        expect(editCustomersPageObject.getBusinessUnit().text()).toEqual("LTL (LT)");
        expect(editCustomersPageObject.getCompanyCode().text()).toEqual("LTL (LT)");
        expect(editCustomersPageObject.getCostCenter().text()).toEqual("Pitt1");

        expect(element(billToCustomerPageObject.billToTabSelector, 'check BillTo tab exists').count()).toBe(1);
        element(billToCustomerPageObject.billToTabSelector, 'go to BillTo tab').click();

        expect(element(billToCustomerPageObject.billToControllerSelector, 'check div contains data-ng-controller="EditCustomerBillToCtrl"').count()).toBe(1);
    });

    it('should fill in address tab', function() {
        expect(element(billToCustomerPageObject.addBillToButtonSelector, 'check add button exists').count()).toBe(1);
        element(billToCustomerPageObject.addBillToButtonSelector, 'click button and add new address').click();

        expect(element(billToCustomerPageObject.billToControllerSelector, 'check div contains EditCustomerBillToCtrl').count()).toBe(1);

        billToCustomerPageObject.setName(addressName);
        billToCustomerPageObject.setCountry('UA');
        billToCustomerPageObject.setFirstAddress('8888 University Drive');
        billToCustomerPageObject.setZip('AARONSBURG, PA, 16820');
        billToCustomerPageObject.setContact('Madison Kirwan');
        billToCustomerPageObject.setAreaCode('158');
        billToCustomerPageObject.setPhoneNumber('7323492');
        billToCustomerPageObject.setEmail('Madison.Kirwan@yahoo.com');

        element(billToCustomerPageObject.nextButtonSelector, 'next').click();
    });

    it('should fill in invoice preferences tab', function() {
        expect(element(customerInvoicesPreferencesPageObject.editCustomerInvoicesPreferencesSelector,
            'check div contains data-pls-bill-to-invoice-preferences attribute').count()).toBeGreaterThan(0);

        expect(element(customerInvoicesPreferencesPageObject.processingTypeSelect, 'check processing type exists').count())
            .toBe(0);
        expect(element(customerInvoicesPreferencesPageObject.processingTimeTypeaheadSelector, 'check processing time typeahead exists').count())
            .toBeGreaterThan(0);
        expect(element(customerInvoicesPreferencesPageObject.processingTimeZoneSelector).count(), 'check processing time zone select exist')
            .toBeGreaterThan(0);
        expect(element(customerInvoicesPreferencesPageObject.processingDaySelector, 'check processing day of week select doesn\'t exist').count())
            .toBe(0);

        customerInvoicesPreferencesPageObject.setCurrencyCode('USD');
        customerInvoicesPreferencesPageObject.setInvoiceType('CBI');
        customerInvoicesPreferencesPageObject.setProcessingType(0);
        customerInvoicesPreferencesPageObject.setProcessingPeriod(1);
        customerInvoicesPreferencesPageObject.setDayOfWeek(0);
        customerInvoicesPreferencesPageObject.setReleaseDayOfWeek(1);

        expect(element(billToCustomerPageObject.nextButtonSelector, 'check next step button is disabled').attr('disabled')).toBe('disabled');
        customerInvoicesPreferencesPageObject.checkedEdiInvoice();

        expect(element(billToCustomerPageObject.nextButtonSelector, 'check next step button is enabled').attr('disabled')).not().toBeDefined();
        element(billToCustomerPageObject.nextButtonSelector, 'next').click();
    });

    it('should fill in EDI Settings tab', function() {
        expect(element(billToCustomerPageObject.nextButtonSelector, 'check next step button is enabled').attr('disabled')).not().toBeDefined();
        element(billToCustomerPageObject.nextButtonSelector, 'next').click();
    });

    it('should fill in BOL', function() {
        expect(element(customerReqFieldsPageObject.reqFieldsGridRow, 'check grid contains 11 rows').count()).toBe(11);
        // Check buttons state if no selected row
        expect(element(customerReqFieldsPageObject.addButton, 'check add button is not disabled').attr('disabled')).not().toBeDefined();
        expect(element(customerReqFieldsPageObject.editButton, 'check add button is disabled').attr('disabled')).toBe('disabled');
        expect(element(customerReqFieldsPageObject.deleteButton, 'check add button is disabled').attr('disabled')).toBe('disabled');
        // Check BOL Identifier
        customerReqFieldsPageObject.selectRow1();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('BOL#');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in Cargo Value', function() {
        // Check Cargo Value Identifier
        customerReqFieldsPageObject.selectRow2();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Cargo value');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in GL', function() {
        // Check GL Identifier
        customerReqFieldsPageObject.selectRow3();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('GL#');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in JOB', function() {
        // Check JOB# Identifier
        customerReqFieldsPageObject.selectRow4();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Job#');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in PO', function() {
        // Check PO Identifier
        customerReqFieldsPageObject.selectRow5();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('PO#');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in PRO', function() {
        // Check Pro# Identifier
        customerReqFieldsPageObject.selectRow6();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Pro#');
        checkPristineIdentifier(true);
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier(true);
        setIdentifierValues(true);
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in PU', function() {
        // Check PU Identifier
        customerReqFieldsPageObject.selectRow7();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('PU#');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in Requested By', function() {
        // Check Requested By Identifier
        customerReqFieldsPageObject.selectRow8();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Requested By');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in SO', function() {
        // Check SO Identifier
        customerReqFieldsPageObject.selectRow9();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('SO#');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in Shipper Ref', function() {
        // Check Shipper Ref Identifier
        customerReqFieldsPageObject.selectRow10();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Shipper Ref#');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();
    });
    it('should fill in Trailer', function() {
        // Check Trailer Identifier
        customerReqFieldsPageObject.selectRow11();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Trailer#');
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.cancelIdentifier();
        customerReqFieldsPageObject.editReqField();
        checkPristineIdentifier();
        setIdentifierValues();
        customerReqFieldsPageObject.saveIdentifier();

        expect(element(billToCustomerPageObject.nextButtonSelector, 'check next step button is enabled').attr('disabled')).not().toBeDefined();
        element(billToCustomerPageObject.nextButtonSelector, 'next').click();
    });

    it('should fill in default values tab', function() {
        customerDefaultValuesPageObject.setEdiCustomsBroker('Livingston');
        customerDefaultValuesPageObject.setEdiCountryCode('333');
        customerDefaultValuesPageObject.setEdiAreaCode('333');
        customerDefaultValuesPageObject.setEdiPhoneNumber('7777777');
        customerDefaultValuesPageObject.setEdiExtension('666666');

        expect(element(billToCustomerPageObject.nextButtonSelector, 'check next step button is enabled').attr('disabled')).not().toBeDefined();
        element(billToCustomerPageObject.nextButtonSelector, 'next').click();
    });

    it('should fill in required docs tab', function() {
        expect(element(billToCustomerPageObject.nextButtonSelector, 'check next step button is enabled').attr('disabled')).not().toBeDefined();

        expect(element(customerInvoicesReqDocsPageObject.editCustomerInvoicesReqDocsSelector,
        'check div contains data-pls-bill-to-req-docs attribute').count()).toBeGreaterThan(0);

        expect(element(billToCustomerPageObject.nextButtonSelector, 'check next step button is enabled').attr('disabled')).not().toBeDefined();
        customerInvoicesPreferencesPageObject.setPaperWorkRequirements(1);
        element(billToCustomerPageObject.nextButtonSelector, 'next').click();
    });

    it('should fill in audit preferences tab and save bill to', function() {
        expect(element(billToCustomerPageObject.saveButtonSelector, 'check done button is enabled').attr('disabled')).not().toBeDefined();
        element(billToCustomerPageObject.saveButtonSelector, 'done').click();

        expect(element(billToCustomerPageObject.billToControllerSelector, 'check div contains data-ng-controller="EditCustomerBillToCtrl"').count()).toBe(1);

        element('.ngViewport', 'Important! scroll grit to the bottom').query(function(elements, done) {
            /*
             * We need to scroll to the bottom of the page to prefetch all elements in ng-grid.
             * Otherwise elements from the bottom of the grid won't be fetched by the query bellow.
             */
            var maxBootomPosition = 5000;
            elements.scrollTop(maxBootomPosition);
            done();
        });

        editCustomersPageObject.selectLastRow();

        element(billToCustomerPageObject.customersGridSelector, 'check bill to address grid').query(function(elements, done) {
            var billToAddress = elements.find('div:contains(' + addressName + ')');
            if (billToAddress.length > 0) {
                done();
            } else {
                done('Bill to address has not been created');
            }
        });
    });

    it('should open customer bill to screen after saving', function() {
        browser().navigateTo('#/customer/active');

        setValue(editCustomersPageObject.inputSearchByCustomerSelector, "pls*");
        element(editCustomersPageObject.searchButtonSelector, 'click search button').click();
        sleep(1);
        expect(element(editCustomersPageObject.gridRow).count()).toBe(1);
        editCustomersPageObject.selectFirstRow();

        expect(element(editCustomersPageObject.editButtonSelector, 'check edit customer button exists').count()).toBe(1);
        element(editCustomersPageObject.editButtonSelector, 'edit customer').click();
        expect(element(editCustomersPageObject.editCustomerControllerSelector, 'div containing data-ng-controller="EditCustomerCtrl"').count()).toBe(1);
        expect(element(billToCustomerPageObject.billToTabSelector, 'check BillTo tab exists').count()).toBe(1);
        element(billToCustomerPageObject.billToTabSelector, 'go to BillTo tab').click();
    });

    it('should open last bill to for editing', function() {
        expect(element(billToCustomerPageObject.billToControllerSelector, 'check div contains data-ng-controller="EditCustomerBillToCtrl"').count()).toBe(1);
        element('.ngViewport', 'Important! scroll grid to the bottom').query(function(elements, done) {
            /*
             * We need to scroll to the bottom of the page to prefetch all elements in ng-grid.
             * Otherwise elements from the bottom of the grid won't be fetched by the query bellow.
             */
            var maxBootomPosition = 5000;
            elements.scrollTop(maxBootomPosition);
            done();
        });

        sleep(1);

        editCustomersPageObject.selectLastRow();

        expect(element('span:contains(' + addressName + ')', 'check that specified bill to address exists in the list').count()).toBe(1);
        element('span:contains(' + addressName + ')', 'select specified bill to address from the list').click();
        expect(element(billToCustomerPageObject.editButtonSelector, 'check edit button exists').count()).toBe(1);
        element(billToCustomerPageObject.editButtonSelector, 'edit bill to address').click();

        expect(element('#addressNameInp', 'check name input field').val()).toBe(addressName);

        billToCustomerPageObject.setFirstAddress('Edited Address');

        element(billToCustomerPageObject.addEditFormDialogSelector, 'ensure form is valid before submitting').query(function(editBillToDialog, done) {
            var pageElement = editBillToDialog.find('.ng-invalid');
            if (pageElement.length > 0) {
                done('Form validation failure');
            } else {
                done();
            }
        });
        element(billToCustomerPageObject.invoicePreferencesTabSelector, 'go to BillTo tab').click();
        expect(element(billToCustomerPageObject.saveButtonSelector, 'check save button is enabled').attr('disabled')).not().toBeDefined();
    });

    it('should verify required fields', function() {
        element(billToCustomerPageObject.reqFiledsTabSelector, 'go to Required Fields tab').click();
        expect(element(customerReqFieldsPageObject.reqFieldsGridRow, 'check grid contains 11 rows').count()).toBe(11);
        // Check buttons state if no selected row
        expect(element(customerReqFieldsPageObject.addButton, 'check add button is not disabled').attr('disabled')).not().toBeDefined();
        expect(element(customerReqFieldsPageObject.editButton, 'check add button is disabled').attr('disabled')).toBe('disabled');
        expect(element(customerReqFieldsPageObject.deleteButton, 'check add button is disabled').attr('disabled')).toBe('disabled');
        // Check BOL Identifier
        customerReqFieldsPageObject.selectRow1();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('BOL#');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check Cargo Value Identifier
        customerReqFieldsPageObject.selectRow2();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Cargo value');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check GL Identifier
        customerReqFieldsPageObject.selectRow3();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('GL#');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check JOB Identifier
        customerReqFieldsPageObject.selectRow4();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Job#');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check PO Identifier
        customerReqFieldsPageObject.selectRow5();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('PO#');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check PRO Identifier
        customerReqFieldsPageObject.selectRow6();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Pro#');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check PU Identifier
        customerReqFieldsPageObject.selectRow7();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('PU#');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check Requested By Identifier
        customerReqFieldsPageObject.selectRow8();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Requested By');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check SO Identifier
        customerReqFieldsPageObject.selectRow9();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('SO#');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check Shipper Ref Identifier
        customerReqFieldsPageObject.selectRow10();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Shipper Ref#');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
        // Check Trailer Identifier
        customerReqFieldsPageObject.selectRow11();
        startEditIdentifier();
        expect(getOptionText(customerReqFieldsPageObject.identifierName)).toEqual('Trailer#');
        checkSavedIdentifiers();
        customerReqFieldsPageObject.cancelIdentifier();
    });

    it('should save bill to after editing', function() {
        element(billToCustomerPageObject.saveButtonSelector, 'click save button').click();
        expect(element(billToCustomerPageObject.billToControllerSelector, 'div containing data-ng-controller="EditCustomerBillToCtrl"').count()).toBe(1);
        element(billToCustomerPageObject.customersGridSelector, 'check bill to address grid').query(function(elements, done) {
            var name = elements.find('div:contains(' + addressName + ')');
            var billToAddress = elements.find('div:contains("Edited Address")');
            if (billToAddress.prevObject.length > 0 && name.length > 0) {
                done();
            } else {
                done('Bill to address has not been updated');
            }
        });
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});