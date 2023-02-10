angular.module('PageObjectsModule').factory('UsersListPageObject', [ function() {
    return {
        listPage: {
            addButton: 'a[href="#/user-mgmt/users/add"]',
            inactiveEditButton: 'button:contains("Edit")',
            editLink: 'a[href^="#/user-mgmt/users/edit/"]',
            activateButton: 'button[data-ng-click="showActivateUserDialog=\'true\'"]:visible',
            deactivateButton: 'button[data-ng-click="showDeactivateUserDialog=\'true\'"]:visible',
            resetPasswordButton: 'button[data-ng-click="showResetPasswordUserDialog=true"]:visible',
            userIdLabel: '.a_userId',
            userFullNameLabel: '.a_fullName',
            idSearchField: 'input[ng-model="col.searchValue"]:eq(0)',
            gridFirstRow: '[ng-row]:first',
            selectFirstRow: function() { element(this.gridFirstRow).click(); },
            getGridRowCount: function(){ return element('[ng-row]').count();},
            addUser: function() { element((this.addButton)).click(); },
            editUser: function() { element((this.editButton)).click(); },
            getActivateButtonDisplay: function() { return element(this.activateButton).attr("disabled"); },
            activateUser: function() { element((this.activateButton)).click(); },
            getDeactivateButtonDisplay: function() { return element(this.deactivateButton).attr("disabled"); },
            deactivateUser: function() { element((this.deactivateButton)).click(); },
            getResetButtonDisplay: function() { return element(this.resetPasswordButton).attr("disabled"); },
            resetUserPassword: function() { element((this.resetPasswordButton)).click(); },
            getUserId: function() { return element(this.userIdLabel).text(); },
            getUserFullName: function() { return element(this.userFullNameLabel).text(); }
        },
        activateDialog: {
            activateDialog: '[data-pls-modal="showActivateUserDialog"]',
            activateMessageDiv: '[data-pls-modal="showActivateUserDialog"] div.modal-body div.span12',
            activateOkButton: '[data-pls-modal="showActivateUserDialog"] button[data-ng-click="activateSelectedUser(true)"]',
            activateCancelButton: '[data-pls-modal="showActivateUserDialog"] button[data-ng-click="showActivateUserDialog=false"]',
            getDialogDisplay: function (){ return element(this.activateDialog).css("display"); },
            getMessage: function() { return element(this.activateMessageDiv).text(); },
            clickOk: function() { element(this.activateOkButton).click(); },
            clickCancel: function() { element(this.activateCancelButton).click(); }
        },
        deactivateDialog: {
            deactivateDialog: '[data-pls-modal="showDeactivateUserDialog"]',
            deactivateMessageDiv: '[data-pls-modal="showDeactivateUserDialog"] div.modal-body div.span12',
            deactivateOkButton: '[data-pls-modal="showDeactivateUserDialog"] button[data-ng-click="activateSelectedUser(false)"]',
            deactivateCancelButton: '[data-pls-modal="showDeactivateUserDialog"] button[data-ng-click="showDeactivateUserDialog=false"]',
            getDialogDisplay: function (){ return element(this.deactivateDialog).css("display"); },
            getMessage: function() { return element(this.deactivateMessageDiv).text(); },
            clickOk: function() { element(this.deactivateOkButton).click(); },
            clickCancel: function() { element(this.deactivateCancelButton).click(); }
        },
        resetPasswordDialog: {
            resetDialog: '[data-pls-modal="showResetPasswordUserDialog"]',
            resetCancelButton: '[data-pls-modal="showResetPasswordUserDialog"] button[data-ng-click="showResetPasswordUserDialog=false"]',
            getDialogDisplay: function (){ return element(this.resetDialog).css("display"); },
            clickCancel: function() { element(this.resetCancelButton).click(); }
        }
    };
}]);
