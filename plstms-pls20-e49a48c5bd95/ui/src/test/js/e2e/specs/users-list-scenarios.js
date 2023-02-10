xdescribe('Active/Inactive Users list functionality', function() {
    var $injector, usersListPageObject, loginLogoutPageObject;


    beforeEach(function() {
        $injector = angular.injector(['PageObjectsModule']);
        usersListPageObject = $injector.get('UsersListPageObject');
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });

    it('Should login into application', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
    });

    it('should open active users page', function() {
        browser().navigateTo('#/user-mgmt/users/active');
        expect(browser().location().path()).toBe('/user-mgmt/users/active');
        expect(usersListPageObject.listPage.getGridRowCount()).toBeGreaterThan(0);
        expect(element(usersListPageObject.listPage.addButton).count()).toBe(1);
        expect(element(usersListPageObject.listPage.inactiveEditButton).count()).toBe(1);
        expect(element(usersListPageObject.listPage.activateButton).count()).toBe(0);
        expect(element(usersListPageObject.listPage.deactivateButton).count()).toBe(1);
        expect(usersListPageObject.listPage.getDeactivateButtonDisplay()).toBe('disabled');
        expect(element(usersListPageObject.listPage.resetPasswordButton).count()).toBe(1);
        expect(usersListPageObject.listPage.getResetButtonDisplay()).toBe('disabled');
    });

    it('should select user in the grid', function() {
        setValue(usersListPageObject.listPage.idSearchField, 'BOB');
        expect(usersListPageObject.listPage.getGridRowCount()).toBe(1);
        usersListPageObject.listPage.selectFirstRow();
        expect(element(usersListPageObject.listPage.editLink).count()).toBe(1);
        expect(usersListPageObject.listPage.getDeactivateButtonDisplay()).not().toBe('disabled');
        expect(usersListPageObject.listPage.getResetButtonDisplay()).not().toBe('disabled');
        expect(usersListPageObject.listPage.getUserId()).toBe('BOB');
        expect(usersListPageObject.listPage.getUserFullName()).toBe('BOB MARTIN');
    });

    it('should show reset password dialog', function() {
        expect(usersListPageObject.resetPasswordDialog.getDialogDisplay()).toBe('none');
        expect(usersListPageObject.listPage.getResetButtonDisplay()).not().toBe('disabled');
        usersListPageObject.listPage.resetUserPassword();
        expect(usersListPageObject.resetPasswordDialog.getDialogDisplay()).not().toBe('none');
        usersListPageObject.resetPasswordDialog.clickCancel();
        expect(usersListPageObject.resetPasswordDialog.getDialogDisplay()).toBe('none');
    });

    it('should show deactivate user dialog', function() {
        expect(usersListPageObject.deactivateDialog.getDialogDisplay()).toBe('none');
        expect(usersListPageObject.listPage.getDeactivateButtonDisplay()).not().toBe('disabled');
        usersListPageObject.listPage.deactivateUser();
        expect(usersListPageObject.deactivateDialog.getDialogDisplay()).not().toBe('none');
        expect(usersListPageObject.deactivateDialog.getMessage()).toContain('Are you sure you want to deactivate User BOB?');
        usersListPageObject.deactivateDialog.clickCancel();
        expect(usersListPageObject.deactivateDialog.getDialogDisplay()).toBe('none');
        expect(usersListPageObject.listPage.getGridRowCount()).toBe(1);
    });

    it('should deactivate user', function() {
        expect(usersListPageObject.deactivateDialog.getDialogDisplay()).toBe('none');
        expect(usersListPageObject.listPage.getDeactivateButtonDisplay()).not().toBe('disabled');
        usersListPageObject.listPage.deactivateUser();
        expect(usersListPageObject.deactivateDialog.getDialogDisplay()).not().toBe('none');
        expect(usersListPageObject.deactivateDialog.getMessage()).toContain('Are you sure you want to deactivate User BOB?');
        usersListPageObject.deactivateDialog.clickOk();
        expect(usersListPageObject.deactivateDialog.getDialogDisplay()).toBe('none');
        expect(usersListPageObject.listPage.getGridRowCount()).toBe(0);
    });

    it('should find inactive user', function() {
        browser().navigateTo('#/user-mgmt/users/inactive');
        expect(browser().location().path()).toBe('/user-mgmt/users/inactive');
    });

    it('should open inactive users page', function() {
        browser().navigateTo('#/user-mgmt/users/inactive');
        expect(browser().location().path()).toBe('/user-mgmt/users/inactive');
        expect(usersListPageObject.listPage.getGridRowCount()).toBeGreaterThan(0);
        expect(element(usersListPageObject.listPage.addButton).count()).toBe(0);
        expect(element(usersListPageObject.listPage.inactiveEditButton).count()).toBe(1);
        expect(element(usersListPageObject.listPage.activateButton).count()).toBe(1);
        expect(usersListPageObject.listPage.getActivateButtonDisplay()).toBe('disabled');
        expect(element(usersListPageObject.listPage.deactivateButton).count()).toBe(0);
        expect(element(usersListPageObject.listPage.resetPasswordButton).count()).toBe(0);
    });

    it('should select user in the grid', function() {
        setValue(usersListPageObject.listPage.idSearchField, 'BOB');
        expect(usersListPageObject.listPage.getGridRowCount()).toBe(1);
        usersListPageObject.listPage.selectFirstRow();
        expect(element(usersListPageObject.listPage.editLink).count()).toBe(1);
        expect(usersListPageObject.listPage.getActivateButtonDisplay()).not().toBe('disabled');
        expect(usersListPageObject.listPage.getUserId()).toBe('BOB');
        expect(usersListPageObject.listPage.getUserFullName()).toBe('BOB MARTIN');
    });

    it('should show activate user dialog', function() {
        expect(usersListPageObject.activateDialog.getDialogDisplay()).toBe('none');
        expect(usersListPageObject.listPage.getActivateButtonDisplay()).not().toBe('disabled');
        usersListPageObject.listPage.activateUser();
        expect(usersListPageObject.activateDialog.getDialogDisplay()).not().toBe('none');
        expect(usersListPageObject.activateDialog.getMessage()).toContain('Are you sure you want to activate User BOB?');
        usersListPageObject.activateDialog.clickCancel();
        expect(usersListPageObject.activateDialog.getDialogDisplay()).toBe('none');
        expect(usersListPageObject.listPage.getGridRowCount()).toBe(1);
    });

    it('should deactivate user', function() {
        expect(usersListPageObject.activateDialog.getDialogDisplay()).toBe('none');
        expect(usersListPageObject.listPage.getActivateButtonDisplay()).not().toBe('disabled');
        usersListPageObject.listPage.activateUser();
        expect(usersListPageObject.activateDialog.getDialogDisplay()).not().toBe('none');
        expect(usersListPageObject.activateDialog.getMessage()).toContain('Are you sure you want to activate User BOB?');
        usersListPageObject.activateDialog.clickOk();
        expect(usersListPageObject.activateDialog.getDialogDisplay()).toBe('none');
        expect(usersListPageObject.listPage.getGridRowCount()).toBe(0);
    });

    it('should check user was activated', function() {
        browser().navigateTo('#/user-mgmt/users/active');
        expect(browser().location().path()).toBe('/user-mgmt/users/active');
        expect(usersListPageObject.listPage.getGridRowCount()).toBeGreaterThan(0);
        setValue(usersListPageObject.listPage.idSearchField, 'BOB');
        expect(usersListPageObject.listPage.getGridRowCount()).toBe(1);
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe('/');
    });
});