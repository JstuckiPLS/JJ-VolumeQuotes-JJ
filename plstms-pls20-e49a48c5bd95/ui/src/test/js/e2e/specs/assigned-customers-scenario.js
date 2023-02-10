/**
 * This scenario checks assigned customers behaviour.
 * 
 * @author Aleksandr Brychak
 */
describe('PLS Assigned Customers', function() {
    var $injector, loginLogoutPageObject, assignedCustomersPageObject;

    beforeEach(function() {
        $injector = angular.injector(['PageObjectsModule']);
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
        assignedCustomersPageObject = $injector.get("AssignedCustomersPageObject");
    });
    
    var unassignedCustomers = 0;
    var assignedCustomers = 0;
    
    function calculateCustomerLength(){
        element(assignedCustomersPageObject.unassignedCustomersGridRows).query(function(elements, done) {
            unassignedCustomers = elements.length;
            done();
        });

        element(assignedCustomersPageObject.assignedCustomersGridRows).query(function(elements, done) {
            assignedCustomers = elements.length;
            done();
        });
    };

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('Should open edit user page', function() {
        browser().navigateTo('#/user-mgmt/users/edit/2');
        expect(browser().location().path()).toBe("/user-mgmt/users/edit/2");
        expect(input(assignedCustomersPageObject.userId).val()).toEqual('SPARTAN1');
        expect(input(assignedCustomersPageObject.firstName).val()).toEqual('RICH');
        expect(input(assignedCustomersPageObject.lastName).val()).toEqual('LITTON');
        expect(input(assignedCustomersPageObject.organization).val()).toEqual('PLS SHIPPER');
        expect(input(assignedCustomersPageObject.email).val()).toEqual('RELITTONT@test.com');
        expect(input(assignedCustomersPageObject.country).val()).toEqual('USA');
    });

    it('Should open Edit Customer List', function() {
        assignedCustomersPageObject.clickEditCustomersListButton();
        expect(assignedCustomersPageObject.getUnassignedCustomersGridRowsCount()).toBe(0);
        expect(assignedCustomersPageObject.getAssignedCustomersGridRowsCount()).toBe(1);
        expect(assignedCustomersPageObject.getAddButtonDisplay()).toBe('disabled');
        expect(assignedCustomersPageObject.getRemoveButtonDisplay()).toBe('disabled');
    });

    it('Should search unassigned customers', function() {
        expect(input(assignedCustomersPageObject.searchValue).val()).toEqual('');
        assignedCustomersPageObject.setSearchValue('%');
        expect(input(assignedCustomersPageObject.searchValue).val()).toEqual('%');
        assignedCustomersPageObject.clickSearchCustomersButton();
        expect(assignedCustomersPageObject.getUnassignedCustomersGridRowsCount()).not().toBe(0);
        expect(assignedCustomersPageObject.getAssignedCustomersGridRowsCount()).toBe(1);
        assignedCustomersPageObject.setCustomerOrAccExecutive('Account Executives');
        assignedCustomersPageObject.setSearchValue('admin sysadmin');
        assignedCustomersPageObject.clickSearchCustomersButton();
        expect(assignedCustomersPageObject.getUnassignedCustomersGridRowsCount()).not().toBe(0);
        expect(assignedCustomersPageObject.getAssignedCustomersGridRowsCount()).toBe(1);
    });

    it('Calculate customer length', function() {
        calculateCustomerLength();
    });

    it('Should move customer', function() {
        assignedCustomersPageObject.selectUnassignedCustomersFirstRow();
        expect(assignedCustomersPageObject.getAddButtonDisplay()).toBe(undefined);
        expect(assignedCustomersPageObject.getRemoveButtonDisplay()).toBe('disabled');
        assignedCustomersPageObject.clickAddButton();

        expect(assignedCustomersPageObject.getUnassignedCustomersGridRowsCount()).not().toBe(0);
        expect(assignedCustomersPageObject.getAssignedCustomersGridRowsCount()).not().toBe(0);

        assignedCustomersPageObject.selectAssignedCustomersFirstRow();
        expect(assignedCustomersPageObject.getAddButtonDisplay()).toBe('disabled');
        expect(assignedCustomersPageObject.getRemoveButtonDisplay()).toBe(undefined);

        assignedCustomersPageObject.clickRemoveButton();
        expect(assignedCustomersPageObject.getUnassignedCustomersGridRowsCount()).toBe(unassignedCustomers);
        expect(assignedCustomersPageObject.getAssignedCustomersGridRowsCount()).toBe(assignedCustomers);
    });

    it('Should open Edit Locations', function() {
        assignedCustomersPageObject.selectAssignedCustomersFirstRow();
        assignedCustomersPageObject.clickLocationDialog();
        expect(assignedCustomersPageObject.getCustomerLocationGridRowsCount()).toBe(2);
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe('disabled');
        assignedCustomersPageObject.selectCustomerLocationFirstRow();
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe('disabled');
        assignedCustomersPageObject.selectCustomerLocationLastRow();
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe(undefined);
    });

    it('Calculate customer length', function() {
        calculateCustomerLength();
    });

    it('Should keep selected Locations', function() {
        assignedCustomersPageObject.selectCustomerLocationFirstRow();
        assignedCustomersPageObject.clickLocation();

        expect(unassignedCustomers).not().toBe(0);
        expect(assignedCustomers).not().toBe(0);
        expect(assignedCustomersPageObject.getCustomerLocationGridRowsCount()).not().toBe(0);
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe(undefined);
        assignedCustomersPageObject.selectCustomerLocationLastRow();
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe(undefined);

        assignedCustomersPageObject.clickUpdateLocationButton();

        expect(assignedCustomersPageObject.getUnassignedCustomersGridRowsCount()).toBe(unassignedCustomers);
        expect(assignedCustomersPageObject.getAssignedCustomersGridRowsCount()).toBe(assignedCustomers);
        assignedCustomersPageObject.selectAssignedCustomersFirstRow();
        assignedCustomersPageObject.clickLocationDialog();
        expect(assignedCustomersPageObject.getCustomerLocationGridRowsCount()).toBe(2);
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe("disabled");
        assignedCustomersPageObject.selectCustomerLocationFirstRow();
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe(undefined);
        assignedCustomersPageObject.selectCustomerLocationLastRow();
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe(undefined);

        assignedCustomersPageObject.clickUpdateLocationButton();
        assignedCustomersPageObject.clickCancelButton();
        assignedCustomersPageObject.selectCustomersModelFirstRow();
        assignedCustomersPageObject.clickEditLocationsListButton();
        expect(assignedCustomersPageObject.getCustomerLocationGridRowsCount()).toBe(2);
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe("disabled");
        assignedCustomersPageObject.selectCustomerLocationFirstRow();
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe(undefined);
        assignedCustomersPageObject.selectCustomerLocationLastRow();
        expect(element(assignedCustomersPageObject.notifications).attr("disabled")).toBe(undefined);

    });

    it('Should logout from application', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});