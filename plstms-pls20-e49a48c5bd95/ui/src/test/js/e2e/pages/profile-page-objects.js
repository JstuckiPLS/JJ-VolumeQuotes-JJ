angular.module('PageObjectsModule').factory('ProfilePageObject', [ function() {
    return {
        profileControllerSelector: '[data-ng-controller="ProfileController"]',
        userIdSelector: '#userId',
        changePasswordButtonSelector: '[data-ng-click="changePassword()"]',
        userNameSelector: '#userName',
        userEmailSelector: '#userEmail',
        notificationSelector: 'div[data-ng-repeat="notification in notificationTypes"]',
        editButtonSelector: '[data-ng-click="editProfile()"]',
        editControllerSelector: '[data-ng-controller="EditUserProfileCtrl"]',
        userModelNotificationSelector: 'div[data-ng-repeat="notification in userModel.notificationTypes"',
        notificationDispatched: '[data-ng-class]:contains("Dispatched"), [class="checkbox ng-binding"]:contains("Dispatched") input:checkbox',
        notificationPick_up: '[data-ng-class="{muted:!isSelected}"]:contains("Picked Up"), [class="checkbox ng-binding"]:contains("Picked Up") input:checkbox',
        notificationOut_for_Delivery: '[data-ng-class="{muted:!isSelected}"]:contains("Out For Delivery"), [class="checkbox ng-binding"]:contains("Out For Delivery") input:checkbox',
        notificationDelivered:'[data-ng-class="{muted:!isSelected}"]:contains("Delivered"), [class="checkbox ng-binding"]:contains("Delivered") input:checkbox',
        notificationDetails: '[data-ng-class="{muted:!isSelected}"]:contains("Details"), [class="checkbox ng-binding"]:contains("Tracking updates") input:checkbox',
        selectDispatched: '[data-ng-class]:contains("Dispatched")',
        selectPick_up: '[data-ng-class="{muted:!isSelected}"]:contains("Picked Up")',
        selectOut_for_Delivery: '[data-ng-class="{muted:!isSelected}"]:contains("Out For Delivery")',
        selectDelivered:'[data-ng-class="{muted:!isSelected}"]:contains("Delivered")',
        selectDetails: '[data-ng-class="{muted:!isSelected}"]:contains("Tracking updates")',

        firstnameField: 'userModel.user.firstName',
        setFirstname : function(name) {
            input(this.firstnameField).enter(name);
        },

        lastnameField: 'userModel.user.lastName',
        setLastname : function(name) {
            input(this.lastnameField).enter(name);
        },

        emailField: 'userModel.user.email',
        setEmail : function(email) {
            input(this.emailField).enter(email);
        },

        saveUserSelector: 'button[data-ng-click="saveUser()"]',
        editUserDialogSelector: '#editUserDialog',
        changePasswordSelector: 'button[data-ng-click="changePassword()"]',
        editPasswordControllerSelector: '[data-ng-controller="EditUserPasswordCtrl"]',

        currentPasswordField: 'userModel.credentials.currentPassword',
        setCurrentPassword : function(pass) {
            input(this.currentPasswordField).enter(pass);
        },

        newPasswordField: 'userModel.credentials.newPassword',
        setNewPassword : function(pass) {
            input(this.newPasswordField).enter(pass);
        },

        confirmedPasswordField: 'userModel.credentials.confirmedPassword',
        setConfirmedPassword : function(pass) {
            input(this.confirmedPasswordField).enter(pass);
        },

        editUserPasswordDialogSelector: '#editUserPasswordDialog',
        editPasswordSelector: 'button[data-ng-click="savePasswordDialog()"]'
    };
} ]);
