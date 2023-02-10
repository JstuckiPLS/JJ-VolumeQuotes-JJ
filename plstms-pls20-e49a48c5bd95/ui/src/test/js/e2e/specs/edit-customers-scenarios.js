describe('Edit Customer.', function() {
    var locationName = 'ZTest Bill To' + new Date().getTime();
    var $injector, loginLogoutPageObject, editCustomersPageObject, billToCustomerPageObject, customerInvoicesPreferencesPageObject,
                    locationPageObject;
    var transactionalBillTo = 'Altria Group, Inc.';

    beforeEach(function() {
        $injector = angular.injector(['PageObjectsModule']);

        editCustomersPageObject = $injector.get('EditCustomersPageObject');
        billToCustomerPageObject = $injector.get('BillToCustomersPageObject');
        customerInvoicesPreferencesPageObject = $injector.get('CustomerInvoicesPreferencesPageObject');
        locationPageObject = $injector.get('LocationPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should edit bill to invoice preferences', function() {
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

        expect(element(billToCustomerPageObject.billToControllerSelector, 'check div contains data-ng-controller="EditCustomerBillToCtrl"').count()).toBe(1);
        element('.ngViewport', 'Important! scroll grit to the bottom').query(function(elements, done) {
            var maxBootomPosition = 5000;
            elements.scrollTop(maxBootomPosition);
            done();
        });
        sleep(1);
        editCustomersPageObject.selectLastRow();
        element(billToCustomerPageObject.editButtonSelector, 'edit bill to address').click();
        element(billToCustomerPageObject.invoicePreferencesTabSelector, 'go to Invoice Pref tab').click();
        customerInvoicesPreferencesPageObject.setExcelOption(1);
        customerInvoicesPreferencesPageObject.setPdfOptions(1);
        customerInvoicesPreferencesPageObject.checkedEdiInvoice();

        expect(element(billToCustomerPageObject.saveButtonSelector, 'check save button is enabled').attr('disabled')).not().toBeDefined();
        element(billToCustomerPageObject.saveButtonSelector, 'click save button').click();
        sleep(1);
    });

    it('should be able to set Prepaid Only payment method to Invoice Preferences in Bill To', function() {
        editCustomersPageObject.selectRowWithText(transactionalBillTo);
        element(billToCustomerPageObject.editButtonSelector, 'edit bill to address').click();
        element(billToCustomerPageObject.invoicePreferencesTabSelector, 'go to Invoice Pref tab').click();
        customerInvoicesPreferencesPageObject.setPaymentMethod('PREPAID ONLY');
        customerInvoicesPreferencesPageObject.setEmailForCreditCard('test@plslogistics.com');

        expect(element(billToCustomerPageObject.saveButtonSelector, 'check save button is enabled').attr('disabled')).not().toBeDefined();
        element(billToCustomerPageObject.saveButtonSelector, 'click save button').click();

        sleep(1);

        element('.ngViewport', 'Important! scroll grit to the bottom').query(function(elements, done) {
            var maxBootomPosition = 5000;
            elements.scrollTop(maxBootomPosition);
            done();
        });

        editCustomersPageObject.selectRowWithText(transactionalBillTo);
        element(billToCustomerPageObject.editButtonSelector, 'edit bill to address').click();
        element(billToCustomerPageObject.invoicePreferencesTabSelector, 'go to Invoice Pref tab').click();
        expect(getOptionText(customerInvoicesPreferencesPageObject.paymentMethodSelector)).toEqual('PREPAID ONLY');
        expect(element(billToCustomerPageObject.saveButtonSelector, 'check save button is enabled').attr('disabled')).not().toBeDefined();
        element(billToCustomerPageObject.saveButtonSelector, 'click save button').click();
        sleep(1);
    });

    it('should edit bill to invoice preferences (CBI Invoice Type)', function() {
        editCustomersPageObject.selectLastRow();
        element(billToCustomerPageObject.editButtonSelector, 'edit bill to address').click();
        element(billToCustomerPageObject.invoicePreferencesTabSelector, 'go to Invoice Pref tab').click();
        sleep(1);

        expect(getOptionText(customerInvoicesPreferencesPageObject.currencyCode)).toEqual('USD');
        //expect(getOptionText(customerInvoicesPreferencesPageObject.invoiceType)).toEqual('CBI');
        //expect(getOptionText(customerInvoicesPreferencesPageObject.cbiInvoiceType)).toEqual('Invoice in PLS 2.0');
        expect(getOptionText(customerInvoicesPreferencesPageObject.xlsDocument)).toEqual('Standard Excel');
        expect(getOptionText(customerInvoicesPreferencesPageObject.pdfDocument)).toEqual('Standard Multi Transactional');
        expect(getOptionText(customerInvoicesPreferencesPageObject.sortType)).toEqual('Load ID');

        expect(isElementPresent(customerInvoicesPreferencesPageObject.xlsDocument)).toBe(true);
        customerInvoicesPreferencesPageObject.setCbiInvoiceType('FIN');
        expect(isElementPresent(customerInvoicesPreferencesPageObject.xlsDocument)).toBe(false);
        element(billToCustomerPageObject.saveButtonSelector, 'click save button').click();
        sleep(1);

        editCustomersPageObject.selectLastRow();
        element(billToCustomerPageObject.invoicePreferencesTabSelector, 'go to BillTo tab').click();
        sleep(1);

        expect(getText(customerInvoicesPreferencesPageObject.invoiceType)).toEqual('CBI');
        expect(getText(customerInvoicesPreferencesPageObject.cbiInvoiceType)).toEqual('Invoice in Financials');
    });

    it('should define invoice preferences', function() {
        browser().navigateTo('#/customer/active');

        setValue(editCustomersPageObject.inputSearchByCustomerSelector, "pls*");
        element(editCustomersPageObject.searchButtonSelector, 'click search button').click();
        sleep(1);

        element(editCustomersPageObject.gridSelector, 'find customers\' table').query(function(customersGrid, done) {
            var rows = customersGrid.find('div[ng-repeat="row in renderedRows"]');
            if (rows.length > 0) {
                rows[0].click();
                done();
            } else {
                done('Failed to edit bill to address. There has been no customers created so far.');
            }
        });

        expect(element(editCustomersPageObject.editButtonSelector, 'check edit customer button exists').count()).toBe(1);
        element(editCustomersPageObject.editButtonSelector, 'click edit button').click();

        expect(element(editCustomersPageObject.editCustomerControllerSelector, 'div containing data-ng-controller="EditCustomerCtrl"').count())
            .toBe(1);

        editCustomersPageObject.saveChanges();
    });

    it('should add new location', function() {
        browser().navigateTo('#/customer/active');

        setValue(editCustomersPageObject.inputSearchByCustomerSelector, "pls*");
        element(editCustomersPageObject.searchButtonSelector, 'click search button').click();
        sleep(1);
        expect(element(editCustomersPageObject.gridRow).count()).toBe(1);
        editCustomersPageObject.selectFirstRow();

        expect(element(editCustomersPageObject.editButtonSelector, 'check edit customer button exists').count()).toBe(1);
        element(editCustomersPageObject.editButtonSelector, 'edit customer').click();
        expect(element(editCustomersPageObject.editCustomerControllerSelector, 'div containing data-ng-controller="EditCustomerCtrl"').count()).toBe(1);

        expect(element(locationPageObject.locationTabSelector, 'check Locations tab exists').count()).toBe(1);
        element(locationPageObject.locationTabSelector, 'go to Locations tab').click();

        expect(element(locationPageObject.locationControllerSelector, 'check div contains data-ng-controller="EditCustomerLocationsCtrl"').count()).toBe(1);

        expect(element(locationPageObject.addLocationButtonSelector, 'check add button exists').count()).toBe(1);
        element(locationPageObject.addLocationButtonSelector, 'click button and add new location').click();
        expect(element(locationPageObject.locationControllerSelector, 'check div contains data-ng-controller="EditLocationCtrl"').count()).toBe(1);

        locationPageObject.setName(locationName);
        element(locationPageObject.saveButtonSelector, 'save location').click();

        expect(element(locationPageObject.locationControllerSelector, 'check div contains data-ng-controller="EditCustomerLocationsCtrl"').count()).toBe(1);

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

        element(locationPageObject.locationGridSelector, 'check location grid').query(function(elements, done) {
            var location = elements.find('div:contains(' + locationName + ')');
            if (location.length > 0) {
                done();
            } else {
                done('Location has not been created');
            }
        });
    });

    it('should edit location', function() {
        browser().navigateTo('#/customer/active');

        setValue(editCustomersPageObject.inputSearchByCustomerSelector, "pls*");
        element(editCustomersPageObject.searchButtonSelector, 'click search button').click();
        sleep(1);
        expect(element(editCustomersPageObject.gridRow).count()).toBe(1);
        editCustomersPageObject.selectFirstRow();

        expect(element(editCustomersPageObject.editButtonSelector, 'check edit customer button exists').count()).toBe(1);
        element(editCustomersPageObject.editButtonSelector, 'edit customer').click();
        expect(element(editCustomersPageObject.editCustomerControllerSelector, 'div containing data-ng-controller="EditCustomerCtrl"').count()).toBe(1);

        expect(element(locationPageObject.locationTabSelector, 'check Locations tab exists').count()).toBe(1);
        element(locationPageObject.locationTabSelector, 'go to Locations tab').click();

        expect(element(locationPageObject.locationControllerSelector, 'check div contains data-ng-controller="EditCustomerLocationsCtrl"').count()).toBe(1);

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

        expect(element('span:contains(' + locationName + ')', 'check that expected location exists in the grid').count()).toBe(1);
        element('span:contains(' + locationName + ')', 'select specified location in the grid').click();
        expect(element(locationPageObject.editLocationButtonSelector, 'check edit button exists').count()).toBe(1);
        element(locationPageObject.editLocationButtonSelector, 'edit location').click();

        expect(element('#locationNameInp', 'check location input field').val()).toBe(locationName);
        locationPageObject.selectAccountExecutiveInput('SYSTEM ADMINISTRATOR');
        locationPageObject.setAccStartDate('10/10/2014');
        locationPageObject.setAccEndDate('12/12/3000');
        locationPageObject.selectBillToInput('Belrun');

        element(locationPageObject.saveButtonSelector, 'save location').click();

        expect(element(locationPageObject.locationControllerSelector, 'check div contains data-ng-controller="EditCustomerLocationsCtrl"').count()).toBe(1);

        element(locationPageObject.locationGridSelector, 'check location grid').query(function(elements, done) {
            var location = elements.find('div:contains(' + locationName + ')');
            var accountExecutive = elements.find('div:contains("SYSTEM ADMINISTRATOR")');
            var startDate = elements.find('div:contains("Fri 10/10/14")');
            var endDate = elements.find('div:contains("Fri 12/12/00")');
            var billTo = elements.find('div:contains("Belrun")');
            if (location.length > 0 && accountExecutive.length > 0 && startDate.length > 0 && endDate.length > 0 && billTo.length > 0) {
                done();
            } else {
                done('Location has not been updated');
            }
        });
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});