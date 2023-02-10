/**
 * This scenario update Go Ship load for invoicing.
 * 
 * @author Brichak Aleksandr
 */
describe('Create Sales Order for Invoicing functionality and process to finance',
    function() {
        var loginLogoutPageObject, editDataSalesOrderCreateObject, trackingBoardPageObject, financialBoardPageObject, transactionalPageObject, editCustomersPageObject;
        function AddressBookEntry(name, contacName, address1, zip, areaCode, phoneNumber, email, locationCode, country, city, state) {
            this.name = name;
            this.contacName = contacName;
            this.address1 = address1;
            this.zip = zip;
            this.areaCode = areaCode;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.locationCode = locationCode;
            this.country = country;
            this.city = city;
            this.state = state;
        }

        var jimi = new AddressBookEntry('JIMI ADDRESS', 'Jimi Hendrix',
                '350 MONROE AVENUE NE GREENWOOD MEMORIAL PARK', 'RENTON, WA, 98056',
                '425', '255 1511', 'no@email.com',null, "United States of America", "RENTON", "WA");

        var eddy = new AddressBookEntry('EDDY ADDRESS', 'Jimi Hendrix',
                '350 MONROE AVENUE NE GREENWOOD MEMORIAL PARK', 'RENTON, WA, 98056',
                '425', '255 1511', 'no@email.com',null,"United States of America", "RENTON", "WA");

        fillAddress = function (entry) {
            input(addressBookPageObject.addDialog.addressName).enter(entry.name);
            input(addressBookPageObject.addDialog.contactName).enter(entry.contacName);
            input(addressBookPageObject.addDialog.address1).enter(entry.address1);
            input(addressBookPageObject.addDialog.cityStZip).enter(entry.zip);
            input(addressBookPageObject.addDialog.phoneArea).enter(entry.areaCode);
            input(addressBookPageObject.addDialog.phoneNumber).enter(entry.phoneNumber);
            input(addressBookPageObject.addDialog.email).enter(entry.email);
            if (entry.locationCode) {
                input(addressBookPageObject.addDialog.locationCode).enter(entry.locationCode);
            }
            addressBookPageObject.addDialog.clickOk();
        };

        beforeEach(function() {
            $injector = angular.injector([ 'PageObjectsModule' ]);
            loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
            editDataSalesOrderCreateObject = $injector.get('EditDataSalesOrderCreateObject');
            trackingBoardPageObject = $injector.get('TrackingBoardPageObject');
            financialBoardPageObject = $injector.get('FinancialBoardPageObject');
            addressBookPageObject = $injector.get('AddressBookPageObject');
            goShipPageObject = $injector.get('GOShipPageObject');


            transactionalPageObject = financialBoardPageObject.transactional;
            consolidatedPageObject = financialBoardPageObject.consolidated;
            allPage = trackingBoardPageObject.all;
        });

        it('Should login into application', function() {
            browser().navigateTo('/my-freight/');
            loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
        });

        function addVendorBillAndOverrideDateHold() {
            trackingBoardPageObject.unbilled.clickRow();
            trackingBoardPageObject.unbilled.clickEditButton();
            editDataSalesOrderCreateObject.clickAddVendorBillButton();
            editDataSalesOrderCreateObject.clickSaveVendorBillButton();
            editDataSalesOrderCreateObject.clickSaveEditSalesOrderButton();
            trackingBoardPageObject.unbilled.clickRow();
            trackingBoardPageObject.unbilled.clickOverrideDateHoldButton();
        }

        it('Should find Go Ship Load on TrackingBoardAllShipments page', function() {
            browser().navigateTo('#/trackingBoard/all');
            expect(element(allPage.controller, 'Div with ng-controller="TrackingBoardAllShipmentsController"').count()).toBe(1);
            allPage.setBOL('%');
            allPage.setCustomer('Go Ship');
            allPage.clickButtonSearch();
            expect(allPage.getRowCount()).toBe(1);
            allPage.clickRow();
            allPage.clickView();
            expect(element('[data-ng-controller="EditSalesOrderCtrl"]', 'Div with ng-controller="EditSalesOrderCtrl"').count()).toBe(1);
        });

        it('Should check and fill out the general tab fields', function() {
            editDataSalesOrderCreateObject.clickSwitchEditModeButton();
            expect(getOptionText(goShipPageObject.inputShippingStatus)).toEqual('Dispatched');
            expect(element(allPage.inputCustomer).val()).toBe('Go Ship');
            expect(element(allPage.inputPro).val()).toBe('');

            expect(element(goShipPageObject.inputCarrier).val()).toBe('EXLA:ESTES EXPRESS LINES');
            expect(element(goShipPageObject.inputPickupDate).val()).not().toBe('');
            expect(element(goShipPageObject.inputEstimatedDeliveryDate)).not().toBe('');
            setValue(goShipPageObject.inputActualPickupDate, '09/08/2016');
            setValue(goShipPageObject.inputActualDeliveryDate,'09/08/2016');
            setValue(goShipPageObject.inputProNumber, 'pro');
            expect(getOptionText(goShipPageObject.inputShippingStatus)).toEqual('Delivered');

        });

        it('Should check and fill out the addresses tab fields', function() {
            element('[class="a_addresses"]').click();
            
            expect( element('[data-ng-model="selectedAddressName"]:first').val()).toBe('origin');

            expect( element('[data-ng-bind="address.address1"]:first').text()).toBe('225 W. DODRIDGE ST');
            expect( element('[data-ng-bind="address.zip | zip"]:first').text()).toBe('COLUMBUS, OH, 43210');
            expect( element('[data-ng-bind="address.contactName"]:first').text()).toBe('originName');
            expect( element('[data-ng-bind="address.email"]:first').text()).toBe('test@plslogistics.com');

            expect( element('[data-ng-model="selectedAddressName"]:last').val()).toBe('destination');
            expect( element('[data-ng-bind="address.address1"]:last').text()).toBe('122 HAYNES HILL RD.');
            expect( element('[data-ng-bind="address.zip | zip"]:last').text()).toBe('BRIMFIELD, MA, 01010');
            expect( element('[data-ng-bind="address.contactName"]:last').text()).toBe('destinationName');
            expect( element('[data-ng-bind="address.email"]:last').text()).toBe('test@plslogistics.com');

            expect(element('[data-ng-model="selectedLocation"]').val()).toEqual('Go Ship');
            expect( element('[data-ng-bind="shipment.billTo.address.address1"]').text()).toBe('TEST BILLING ADDRESS');
            expect( element('[data-ng-bind="shipment.billTo.address.zip | zip"]').text()).toBe('CRANBERRY TWP, PA, 16066');
            expect( element('[data-ng-bind="shipment.billTo.address.email"]').text()).not().toBe('');
            
            element('[data-ng-click="addAddressEntry(origin)"]:first').click();
            fillAddress(jimi);
            element('[data-ng-click="addAddressEntry(origin)"]:last').click();
            fillAddress(eddy);
        });
        
        it('Should check and fill out the details tab fields', function() {
            element('[class="a_details"]').click();
            expect(element(goShipPageObject.inputBOL).val()).not().toBe('');
            expect(element(goShipPageObject.inputSO).val()).toBe('');
            expect(element(goShipPageObject.inputPO).val()).toBe('');
            expect(element(goShipPageObject.inputPU).val()).toBe('');
            expect(element(goShipPageObject.inputPRO).val()).toBe('pro');
            expect(element(goShipPageObject.inputRef).val()).toBe('');
            expect(element(goShipPageObject.inputTrailer).val()).toBe('');
            expect(element(goShipPageObject.inputCargoValue).val()).toBe('');
            expect(element(goShipPageObject.inputGL).val()).toBe('');
            expect(element(goShipPageObject.inputShippinghoursOfOerationFromTime).val()).toBe('');
            expect(element(goShipPageObject.inputShippinghoursOfOerationToTime).val()).toBe('');

            setValue(goShipPageObject.inputSO, 'so');
            setValue(goShipPageObject.inputPO, 'po');
            setValue(goShipPageObject.inputPU, 'pu');
            setValue(goShipPageObject.inputRef, 'sr');
            setValue(goShipPageObject.inputTrailer, 'tr');
            setValue(goShipPageObject.inputCargoValue, '32');
            setValue(goShipPageObject.inputRequestedBy, 'rb');
            setValue(goShipPageObject.inputGL, 'gl');
            setValue(goShipPageObject.deliveryFromWindow, '11:00 AM');
            setValue(goShipPageObject.deliveryToWindow, '11:30 AM');
            editDataSalesOrderCreateObject.clickSaveEditSalesOrderButton();
            editDataSalesOrderCreateObject.clickCloseEditSalesOrderButton();
        });

        it('should add vendor bill and override date hold', function() {
            browser().navigateTo('#/trackingBoard/unbilled');
            expect(browser().location().path()).toBe("/trackingBoard/unbilled");
            trackingBoardPageObject.unbilled.searchByCustomer("Go Ship");
            expect(trackingBoardPageObject.unbilled.getRowsCount()).toBe(1);
            addVendorBillAndOverrideDateHold();
        });

        it('should open transactional invoice page and process invoices to Finance', function() {
            browser().navigateTo('#/financialBoard/transactional');

            expect(element(transactionalPageObject.controller, 'Div with ng-controller="FinancialBoardTransactionalController"').count())
                    .toBe(1);
            transactionalPageObject.searchByCustomer("Go Ship");
            transactionalPageObject.selectFirstRow();
            transactionalPageObject.uncheckProcessOnSchedule();
            expect(transactionalPageObject.getOverrideScheduledProcessButtonDisplay()).not().toBe('disabled');
            transactionalPageObject.clickOverrideScheduledProcessButton();
            expect(transactionalPageObject.getCloseProcessResultsDialogButtonDisplay()).not().toBe('disabled');
            transactionalPageObject.clickProcessInvoicesToFinanceButton();
            expect(transactionalPageObject.getProcessInvoicesToFinanceButtonDisplay()).not().toBe('disabled');
            transactionalPageObject.clickCloseProcessResultsDialogButton();
        });

        it('Should logout from application', function() {
            loginLogoutPageObject.logout();
            expect(browser().location().path()).toBe("/");
        });
    });