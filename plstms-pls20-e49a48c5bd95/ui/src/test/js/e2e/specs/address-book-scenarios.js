/**
 * This scenario checks basic address-book behaviour.
 *
 * @author Eugene Borshch
 */
describe('PLS Customer AddressBook. ', function () {
    var $injector, addressBookPageObject, loginLogoutPageObject;

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

    var jimiHendrix = new AddressBookEntry('JIMI ADDRESS', 'Jimi Hendrix',
            '350 MONROE AVENUE NE GREENWOOD MEMORIAL PARK', 'RENTON, WA, 98056',
            '425', '255 1511', 'no@email.com', 'LC', "United States of America", "RENTON", "WA"),

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
            };

    beforeEach(function () {
        $injector = angular.injector(['PageObjectsModule']);
        addressBookPageObject = $injector.get('AddressBookPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function () {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsCustomer);
    });

    it('should open address book page', function () {
        browser().navigateTo('#/address-book/address-book-list');
        expect(browser().location().path()).toBe("/address-book/address-book-list");
    });

    it('test if dialog open and OK button validation(enabled/disabled)', function () {
        var addressName = jimiHendrix.name + new Date().getTime();

        //Click 'Add' button -> Dialog should be opened
        expect(addressBookPageObject.addDialog.getDialogDisplay()).toBe("none");
        addressBookPageObject.listPage.clickAdd();
        expect(addressBookPageObject.addDialog.getDialogDisplay()).not().toBe("none");

        //Start filling form -> 'OK' button should become enabled only when all required data is entered 
        input(addressBookPageObject.addDialog.addressName).enter(addressName);
        expect(addressBookPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(addressBookPageObject.addDialog.contactName).enter(jimiHendrix.contacName);
        expect(addressBookPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(addressBookPageObject.addDialog.address1).enter(jimiHendrix.address1);
        expect(addressBookPageObject.addDialog.getOkDisplay()).toBe("disabled");

        input(addressBookPageObject.addDialog.phoneArea).enter(jimiHendrix.areaCode);
        input(addressBookPageObject.addDialog.phoneNumber).enter(jimiHendrix.phoneNumber);

        input(addressBookPageObject.addDialog.cityStZip).enter(jimiHendrix.zip);
        expect(addressBookPageObject.addDialog.getOkDisplay()).not().toBe("disabled");

        //Save address -> Dialog should be closed 
        addressBookPageObject.addDialog.clickOk();
        expect(addressBookPageObject.addDialog.getDialogDisplay()).toBe("none");
    });

    it('test prohibit creation of two non-unique address names', function () {
        var jimiHedirxWithStamp = angular.copy(jimiHendrix);
        jimiHedirxWithStamp.name = jimiHedirxWithStamp.name + new Date().getTime();
        jimiHedirxWithStamp.locationCode = jimiHedirxWithStamp.name;

        //fill same address for the first time
        expect(addressBookPageObject.addDialog.getDialogDisplay()).toBe("none");
        addressBookPageObject.listPage.clickAdd();
        expect(addressBookPageObject.addDialog.getDialogDisplay()).not().toBe("none");
        fillAddress(jimiHedirxWithStamp);
        expect(addressBookPageObject.addDialog.getOkDisplay()).not().toBe("disabled");
        addressBookPageObject.addDialog.clickOk();
        expect(addressBookPageObject.addDialog.getDialogDisplay()).toBe("none");

        //fill same address for the second time
        expect(addressBookPageObject.addDialog.getDialogDisplay()).toBe("none");
        addressBookPageObject.listPage.clickAdd();
        expect(addressBookPageObject.addDialog.getDialogDisplay()).not().toBe("none");
        fillAddress(jimiHedirxWithStamp);
        expect(addressBookPageObject.addDialog.getOkDisplay()).not().toBe("disabled");
        addressBookPageObject.addDialog.clickOk();
        expect(addressBookPageObject.addDialog.getDialogDisplay()).not().toBe("none"); //dialog should remain opened.

        //Check error message
//        expect(element(addressBookPageObject.addDialog.errorNameDuplicated).html()).toContain('Address Name is duplicated');

        addressBookPageObject.addDialog.clickCancel();
        expect(addressBookPageObject.addDialog.getDialogDisplay()).toBe("none"); //dialog should be closed.
    });

    it('should test if buttons Add, Edit, Delete, Import are disabled properly', function () {
        loginLogoutPageObject.logout();
        loginLogoutPageObject.login(loginLogoutPageObject.plsCustomer);

        browser().navigateTo('#/address-book/address-book-list');
        //test default 'disabled' state
        expect(addressBookPageObject.listPage.getAddDisplay()).not().toBe("disabled");
        expect(addressBookPageObject.listPage.getEditDisplay()).toBe("disabled");
        expect(addressBookPageObject.listPage.getDeleteDisplay()).toBe("disabled");
        expect(addressBookPageObject.listPage.getImportDisplay()).not().toBe("disabled");

        //test 'disabled' state after grid row selection
        addressBookPageObject.listPage.selectFirstRow();
        expect(addressBookPageObject.listPage.getAddDisplay()).not().toBe("disabled");
        expect(addressBookPageObject.listPage.getEditDisplay()).not().toBe("disabled");
        expect(addressBookPageObject.listPage.getDeleteDisplay()).not().toBe("disabled");
        expect(addressBookPageObject.listPage.getImportDisplay()).not().toBe("disabled");
    });

    it('should test if Delete button works properly', function () {
        //Click 'Delete' btn -> confirmation dialog should be opened.
        addressBookPageObject.listPage.selectLastRow();
        addressBookPageObject.listPage.clickDelete();
        expect(addressBookPageObject.listPage.getDeleteDialogDisplay()).not().toBe("none");

        addressBookPageObject.listPage.clickDeleteOk();
        expect(addressBookPageObject.listPage.getDeleteDialogDisplay()).toBe("none");
    });

    it('Should be present download template link.', function () {
        browser().navigateTo('#/address-book/address-book-list');
        expect(element(addressBookPageObject.listPage.templateDownloadLink).count()).toBe(1);
    });

    var postfix = "(3)";
    var addressName = jimiHendrix.name + " " + new Date().getTime();

    it('Should add properly.', function () {
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
        browser().navigateTo('#/address-book/address-book-list');
        addressBookPageObject.listPage.clickAdd();
        input(addressBookPageObject.addDialog.addressName).enter(addressName);
        input(addressBookPageObject.addDialog.contactName).enter(jimiHendrix.contacName);
        input(addressBookPageObject.addDialog.address1).enter(jimiHendrix.address1);
        input(addressBookPageObject.addDialog.cityStZip).enter(jimiHendrix.zip);
        input(addressBookPageObject.addDialog.phoneArea).enter(jimiHendrix.areaCode);
        input(addressBookPageObject.addDialog.phoneNumber).enter(jimiHendrix.phoneNumber);
        input(addressBookPageObject.addDialog.email).enter(jimiHendrix.email);
        addressBookPageObject.addDialog.clickOk();
    });

    it('Should edit properly.', function () {
        setValue(addressBookPageObject.listPage.nameSearchField, addressName);
        sleep(1);
        expect(addressBookPageObject.getGridRowCount()).toBe(1);
        addressBookPageObject.listPage.selectLastRow();
        expect(binding(addressBookPageObject.addressNameBinding)).toBe(addressName);
        expect(binding(addressBookPageObject.countryNameBinding)).toBe(jimiHendrix.country);

        expect(binding(addressBookPageObject.address1Binding)).toBe(jimiHendrix.address1);
        expect(binding(addressBookPageObject.contactNameBinding)).toBe(jimiHendrix.contacName);
        expect(binding(addressBookPageObject.phoneNumberBinding)).toBe("+1(" + jimiHendrix.areaCode + ")" + jimiHendrix.phoneNumber);
        expect(binding(addressBookPageObject.faxNumberBinding)).toBe("");
        expect(element(addressBookPageObject.getLinkForEmail(jimiHendrix.email)).count()).toBe(1);
        addressBookPageObject.listPage.clickEdit();
        input(addressBookPageObject.addDialog.addressName).enter(addressName + postfix);
        input(addressBookPageObject.addDialog.contactName).enter(jimiHendrix.contacName + postfix);
        input(addressBookPageObject.addDialog.address1).enter(jimiHendrix.address1 + postfix);
        addressBookPageObject.addDialog.clickOk();
        expect(element(addressBookPageObject.getJquerySelectorForAddressName(0)).text()).toBe(addressName + postfix);
        expect(element(addressBookPageObject.getJquerySelectorForAddress1(0)).text()).toBe(jimiHendrix.address1 + postfix);
        expect(element(addressBookPageObject.getJquerySelectorForCity(0)).text()).toBe(jimiHendrix.city);
        expect(element(addressBookPageObject.getJquerySelectorForState(0)).text()).toBe(jimiHendrix.state);
        expect(element(addressBookPageObject.getJquerySelectorForContactName(0)).text()).toBe(jimiHendrix.contacName + postfix);
    });

    it('Should delete properly.', function () {
        addressBookPageObject.listPage.selectLastRow();
        addressBookPageObject.listPage.clickDelete();
        expect(addressBookPageObject.listPage.getDeleteDialogDisplay()).not().toBe("none");
        addressBookPageObject.listPage.clickDeleteOk();
        sleep(1);
        expect(addressBookPageObject.getGridRowCount()).toBe(0);
    });

    it('Should appears data in grid.', function () {
        loginLogoutPageObject.logout();
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
        browser().navigateTo('#/address-book/address-book-list');
        setValue(addressBookPageObject.listPage.nameSearchField, "");
        addressBookPageObject.setCustomer("PLS SHIPPER");
        sleep(1);
        expect(addressBookPageObject.getGridRowCount()).toBeGreaterThan(0);
    });

    it('Should logout from application.', function () {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});