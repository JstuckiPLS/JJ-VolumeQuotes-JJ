xdescribe('Profile tab test', function() {
    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        profilePageObject = $injector.get('ProfilePageObject');
    });
    
    

    it('checks profile page elements', function() {
        loginLogoutPageObject.login(loginLogoutPageObject.plsCustomer);
        browser().navigateTo('#/my-profile');

        expect(element(profilePageObject.profileControllerSelector, 'div containing data-ng-controller="ProfileController"').count()).toBe(1);
        element(profilePageObject.userIdSelector, 'check user login value').query(function(foundElements, done) {
            var userLoginLabel = foundElements[0];
            if (!userLoginLabel.textContent) {
                done('Current user id should be defined');
            } else {
                done();
            }
        });
        expect(element(profilePageObject.changePasswordButtonSelector, 'button to change current password').count()).toBe(1);
        element(profilePageObject.userNameSelector, 'check user name value').query(function(foundElements, done) {
            var userNameLabel = foundElements[0];
            if (!userNameLabel.textContent) {
                done('Current user name should be defined');
            } else {
                done();
            }
        });
        element(profilePageObject.userEmailSelector, 'check user email value').query(function(foundElements, done) {
            var userEmailLabel = foundElements[0];
            if (!userEmailLabel.textContent) {
                done('Current user email should be defined');
            } else {
                done();
            }
        });

        element(profilePageObject.notificationSelector, 'ensure notification list exists as well').query(function(foundElements, done) {
            if (foundElements.length < 0) {
                done('Notifications should be present');
            } else {
                done();
            }
        });
        expect(element(profilePageObject.editButtonSelector, 'ensure edit button extists').count()).toBe(1);
    });

    it('should edit user profile', function() {
        
        browser().navigateTo('#/my-profile');

        element(profilePageObject.editButtonSelector, 'edit profile').click();
        expect(element(profilePageObject.editControllerSelector, 'div containing data-ng-controller="EditUserProfileCtrl"').count()).toBe(1);

        /* Fill in form fields */
        profilePageObject.setFirstname('RICH(edited)');
        profilePageObject.setLastname('LITTON(edited)');
        profilePageObject.setEmail('mail@mail.com');

        element(profilePageObject.saveUserSelector, 'Save form').click();

        expect(element(profilePageObject.editControllerSelector, 'div containing data-ng-controller="EditUserProfileCtrl"').count()).toBe(1);

        /* Rollback edited changes */
        profilePageObject.setFirstname('RICH');
        profilePageObject.setLastname('LITTON');
        profilePageObject.setEmail('RELITTONT@test.com');
        element(profilePageObject.saveUserSelector, 'Save form').click();
    });

    it('should edit user password',
            function() {
                browser().navigateTo("#/my-profile");

                element(profilePageObject.changePasswordSelector, 'edit password').click();
                expect(element(profilePageObject.editPasswordControllerSelector, 'div containing data-ng-controller="EditUserPasswordCtrl"').count())
                        .toBe(1);

                /* Fill in form fields */
                profilePageObject.setCurrentPassword('Qwerty123');
                profilePageObject.setNewPassword('Qwerty123');
                profilePageObject.setConfirmedPassword('Qwerty123');

                element(profilePageObject.editUserPasswordDialogSelector, 'ensure form is valid before submitting').query(
                        function(editUserPasswordDialog, done) {
                            var invalidFormFields = editUserPasswordDialog.find('.ng-invalid');
                            if (invalidFormFields.length > 0) {
                                done('Form validation is failed');
                            } else {
                                done();
                            }
                        });

            });

    it('should check checkbox notifications pressing button Edit', function() {
        loginLogoutPageObject.logout();
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
        browser().navigateTo('#/my-profile');
        
        element(profilePageObject.editButtonSelector, 'edit profile').click();
        expect(element(profilePageObject.editControllerSelector, 'div containing data-ng-controller="EditUserProfileCtrl"').count()).toBe(1);

        element(profilePageObject.notificationDispatched).query(function(element, done) {
           if (element.attr('class').indexOf('muted')>=0) {
                element.click();
            };
            done();
        });

        element(profilePageObject.notificationPick_up).query(function(element, done) {
            if(element.attr('class').indexOf('muted')>=0){
                element.click();
            };
            done();
        });        
        element(profilePageObject.notificationOut_for_Delivery).query(function(element, done) {
            if(element.attr('class').indexOf('muted')>=0){
                element.click();
            };
            done();
        });        
        element(profilePageObject.notificationDelivered).query(function(element, done) {
            if(element.attr('class').indexOf('muted')>=0){
                element.click();
            };
            done();
        });        element(profilePageObject.notificationDetails).query(function(element, done) {
            if(element.attr('class').indexOf('muted')>=0){
                element.click();
            };
            done();
        });

        element(profilePageObject.saveUserSelector, 'Save form').click();
    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });
});
