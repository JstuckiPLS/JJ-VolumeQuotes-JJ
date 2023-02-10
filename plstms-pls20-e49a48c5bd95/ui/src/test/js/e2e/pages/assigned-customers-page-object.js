angular.module('PageObjectsModule').factory('AssignedCustomersPageObject', [ function() {
    return {
        userId: 'user.userId',
        firstName: 'user.firstName',
        lastName: 'user.lastName',
        organization: 'user.parentOrganization',
        email: 'user.email',
        country: 'plsCountrySearch',
        saveChangesButton: 'button[data-ng-click="handleSave()"]',
        okEditCustomerButton: 'button[data-ng-click="updateCustomersList()"]',
        closeButton: 'button[data-ng-click="closeEditCustomersListDialog()()"]',
        addButton: 'button[data-ng-click="addCustomer()"]',
        updateLocationButton: 'button[data-ng-click="updateCustomersLocations()"]',
        searchCustomersButton: 'button[data-ng-click="searchCustomers()"]',
        removeButton: 'button[data-ng-click="removeCustomer()"]',
        editCustomersListButton: 'button[data-ng-click="editCustomerList()"]',
        assignedCustomersGridRows : 'div[data-ng-grid="assignedCustomersListGrid"] [ng-row]',
        unassignedCustomersGridRows : 'div[data-ng-grid="unassignedCustomersListGrid"] [ng-row]',
        customersModelGridRows : 'div[data-ng-grid="customersModel.options"] [ng-row]',

        assignedCustomersFirstRow: 'div[data-ng-grid="assignedCustomersListGrid"] [ng-row]:first',
        unassignedCustomersFirstRow: 'div[data-ng-grid="unassignedCustomersListGrid"] [ng-row]:first',
        customersModelFirstRow: 'div[data-ng-grid="customersModel.options"] [ng-row]:first',

        searchValue: 'editCustomersListModel.searchValue',
        customerOrAccExecutiveModel: 'customerOrAccExecutive.value',
        locationDialog: 'a[data-ng-click="openlocationDialog(row.entity)"]',
        customerLocationGridRows : 'div[data-ng-grid="customerLocationGrid"] [ng-row]',
        customerLocationFirstRow: 'div[data-ng-grid="customerLocationGrid"] [ng-row]:first',
        customerLocationLastRow: 'div[data-ng-grid="customerLocationGrid"] [ng-row]:last',
        notifications: 'input[data-checklist-value="notification.value"]',
        locationCheck: 'input[data-ng-model="row.entity.isChecked"] :first',
        editLocationsListButton: 'button[data-ng-click="editLocationsList(transferObject)"]',

        setUserId: function(value) {
            input(this.userId).enter(value);
        },
        setFirstName: function(value) {
            input(this.firstName).enter(value);
        },
        setLastName: function(value) {
            input(this.lastName).enter(value);
        },
        setOrganization: function(value) {
            input(this.organization).enter(value);
        },
        setEmail: function(value) {
            input(this.email).enter(value);
        },
        setCountry: function(value) {
            input(this.country).enter(value);
        },
        clickSaveChangesButton : function() {
            element(this.saveChangesButton).click();
        },
        clickEditCustomersListButton : function() {
            element(this.editCustomersListButton).click();
        },
        clickUpdateLocationButton : function() {
            element(this.updateLocationButton).click();
        },
        clickSearchCustomersButton : function() {
            element(this.searchCustomersButton).click();
        },
        clickAddButton : function() {
            element(this.addButton).click();
        },
        clickRemoveButton : function() {
            element(this.removeButton).click();
        },
        clickCancelButton : function() {
            element(this.closeButton).click();
        },
        clickEditLocationsListButton : function() {
            element(this.editLocationsListButton).click();
        },
        clickLocationDialog : function() {
            element(this.locationDialog).click();
        },
        clickLocation : function() {
            element(this.locationCheck).click();
        },
        getCustomerEmail: function() {
            return input(this.customerEmail).val();
        },
        getCustomerContactName: function() {
            return input(this.customerContactName).val();
        },
        getUnassignedCustomersGridRowsCount : function() {
            return element(this.unassignedCustomersGridRows).count();
        },
        getAssignedCustomersGridRowsCount : function() {
            return element(this.assignedCustomersGridRows).count();
        },
        getCustomerLocationGridRowsCount : function() {
            return element(this.customerLocationGridRows).count();
        },
        getAddButtonDisplay : function () {
            return element(this.addButton).attr("disabled");
        },
        getRemoveButtonDisplay : function () {
            return element(this.removeButton).attr("disabled");
        },
        setSearchValue: function(value) {
            input(this.searchValue).enter(value);
        },
        setCustomerOrAccExecutive: function(value) {
            select(this.customerOrAccExecutiveModel).option(value);
        },
        selectAssignedCustomersFirstRow: function() {
            element(this.assignedCustomersFirstRow).click(); 
         },
         selectCustomersModelFirstRow: function() {
             element(this.customersModelFirstRow).click(); 
        },
        selectUnassignedCustomersFirstRow: function() {
             element(this.unassignedCustomersFirstRow).click(); 
        },
        selectCustomerLocationFirstRow: function() {
            element(this.customerLocationFirstRow).click(); 
       },
       selectCustomerLocationLastRow: function() {
           element(this.customerLocationLastRow).click(); 
      }
    };
} ]);